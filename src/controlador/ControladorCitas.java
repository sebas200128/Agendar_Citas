
package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.Cita;
import modelo.CitaDAO;
import modelo.Paciente;
import modelo.PacienteDAO;
import vista.frmCitas;

public class ControladorCitas implements ActionListener {

    private frmCitas vistaCita;
    private PacienteDAO dao = new PacienteDAO();
    private CitaDAO daoCita = new CitaDAO();

    public ControladorCitas(frmCitas vista) {
        this.vistaCita = vista;
        this.vistaCita.btnMostar.addActionListener(this);
        this.vistaCita.btnGuardarCita.addActionListener(this);
        this.vistaCita.btnMostrarCitas.addActionListener(this);

        cargarPacientes();
    }

    public void iniciarCita() {
        vistaCita.setTitle("Registrar Cita");
        vistaCita.setVisible(true);
        cargarPacientes();
    }

    private void listarTabla() {
        List<Paciente> lista = dao.listar();

        //1. Definimos los nombres de las columnas
        String[] titulos = {"ID", "Nombre", "DNI", "Genero", "Nacimiento"};

        // 2. creamos el modelo con los titulos
        DefaultTableModel model = new DefaultTableModel(null, titulos);

        //3. Llenamos las listas
        for (Paciente pa : lista) {
            Object[] fila = new Object[5];
            fila[0] = pa.getId();
            fila[1] = pa.getNombre();
            fila[2] = pa.getDni();
            fila[3] = pa.getGenero();
            fila[4] = pa.getFechaNac();

            model.addRow(fila);
        }

        //4. Asignamos el modelo de la tabla visual
        vistaCita.tblDatos.setModel(model);
    }

    // ESTE MÉTODO ACTUALIZA EL COMBOBOX
    private void cargarPacientes() {
        vistaCita.cbxPaciente.removeAllItems(); // 1. Limpia la lista vieja

        List<Paciente> lista = dao.listar();    // 2. Trae la lista nueva de la BD

        for (Paciente p : lista) {
            vistaCita.cbxPaciente.addItem(p);   // 3. Llena con los datos frescos
        }
    }

    private Date convertirFecha(String texto) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false); // ❌ fechas inválidas
            java.util.Date fecha = sdf.parse(texto);
            return new Date(fecha.getTime());
        } catch (ParseException e) {
            return null;
        }
    }

    private void guardarCita() {
        // 1. Obtener datos
        Paciente paciente = (Paciente) vistaCita.cbxPaciente.getSelectedItem();
        String fechaTexto = vistaCita.txtFechaCita.getText();
        String horaTexto = vistaCita.txtHora.getText().trim(); // LEEMOS LA HORA

        //VALIDACION DE CAMPOS FECHA Y HORA
        if (paciente == null || fechaTexto.isEmpty() || horaTexto.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Complete fecha y hora");
            return;
        }

        //VALIDAR FORMATO FECHA
        Date fecha = convertirFecha(fechaTexto);
        if (fecha == null) {
            JOptionPane.showMessageDialog(null, "Fecha inválida (dd/MM/yyyy)");
            return;
        }

        //Validar formato Hora
        if (!validarFormatoHora(horaTexto)) {
            JOptionPane.showMessageDialog(null, "Hora inválida.\nUse formato 24h: HH:mm (ej: 09:00, 14:30)");
            return;
        }

        // --- VALIDACIÓN DE CONFLICTO (FECHA + HORA) ---
        if (daoCita.existeCitaFechaHora(fecha, horaTexto)) {
            int respuesta = JOptionPane.showConfirmDialog(null,
                    "¡ALERTA! Ya existe una cita el " + fechaTexto + " a las " + horaTexto + ".\n"
                    + "¿Desea sobreescribir/agregar cita en este horario?",
                    "Horario Ocupado",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (respuesta != JOptionPane.YES_OPTION) {
                return;
            }
        }

        // --- VALIDACIÓN 2: ¿EL PACIENTE YA TIENE CITAS? ---
        if (daoCita.tieneCitaElPaciente(paciente.getId())) {
            int respuesta = JOptionPane.showConfirmDialog(null,
                    "El paciente " + paciente.getNombre() + " ya tiene citas registradas previamente.\n"
                    + "¿Desea agendarle una NUEVA cita?",
                    "Paciente con Citas",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            // Si el usuario dice NO, cancelamos.
            if (respuesta != JOptionPane.YES_OPTION) {
                return;
            }
        }

        // --- SI PASA LAS VALIDACIONES, GUARDAMOS ---
        Cita c = new Cita();
        c.setIdPaciente(paciente.getId());
        c.setFechaCita(fecha);
        c.setHora(horaTexto);

        if (daoCita.guardar(c)) {
            JOptionPane.showMessageDialog(null, "Cita registrada con éxito");
            vistaCita.txtFechaCita.setText("");
            vistaCita.txtHora.setText(""); // Limpiamos hora
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (vistaCita != null) {

            // 1. BOTÓN MOSTRAR TABLA Y ACTUALIZAR COMBO
            if (e.getSource() == vistaCita.btnMostar) {
                listarTabla();
                cargarPacientes();
            } // 2. BOTÓN GUARDAR CITA (Este SÍ valida datos)
            else if (e.getSource() == vistaCita.btnGuardarCita) {
                guardarCita(); // <--- Aquí adentro está el mensaje "Complete los datos"
            } // 3. BOTÓN MOSTRAR LISTA DE CITAS (Este NO valida nada)
            else if (e.getSource() == vistaCita.btnMostrarCitas) {

                // Simplemente pedimos la lista a la BD
                String listado = daoCita.listarCitasTexto();

                // Creamos el área de texto con scroll
                javax.swing.JTextArea textArea = new javax.swing.JTextArea(listado);
                textArea.setEditable(false);
                javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(textArea);
                scrollPane.setPreferredSize(new java.awt.Dimension(400, 300));

                // Mostramos el mensaje directo, sin preguntar nada antes
                JOptionPane.showMessageDialog(null, scrollPane, "Listado de Citas", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    // Función auxiliar para validar formato HH:mm
    private boolean validarFormatoHora(String hora) {
        // Expresión regular: 00 a 23 : 00 a 59
        return hora.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$");
    }

}
