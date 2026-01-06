package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

    // Variable para guardar el ID de la cita que estamos editando
    private int idCitaSeleccionada = -1;
    // Variable para recordar el orden actual (por defecto Por Fechas)
    private String ordenActual = "Por Fechas";

    public ControladorCitas(frmCitas vista) {
        this.vistaCita = vista;
        this.vistaCita.btnMostar.addActionListener(this);
        this.vistaCita.btnGuardarCita.addActionListener(this);
        this.vistaCita.btnEditarCita.addActionListener(this);
        this.vistaCita.btnOrdenar.addActionListener(this);

        cargarPacientes();
        listarCitasEnTabla(ordenActual);

        // EVENTO CLICK EN LA TABLA DE CITAS
        this.vistaCita.tblCitas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarCitaDeTabla();
            }
        });

    }

    // --- MÉTODOS DE LOGICA ---
    // 1. Listar las citas en la JTable (tblCitas)
    // Ahora recibe el criterio de ordenamiento
    private void listarCitasEnTabla(String criterio) {
        String[] titulos = {"ID Cita", "Paciente", "Fecha", "Hora", "ID Paciente"};
        DefaultTableModel model = new DefaultTableModel(null, titulos) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Llamamos al DAO pasando el criterio
        List<Object[]> lista = daoCita.listarCitasParaTabla(criterio);

        for (Object[] fila : lista) {
            model.addRow(fila);
        }
        vistaCita.tblCitas.setModel(model);

        // Ocultar columna ID Paciente
        vistaCita.tblCitas.getColumnModel().getColumn(4).setMinWidth(0);
        vistaCita.tblCitas.getColumnModel().getColumn(4).setMaxWidth(0);
        vistaCita.tblCitas.getColumnModel().getColumn(4).setWidth(0);
    }

    // 2. Al hacer clic en la tabla, pasar datos al formulario
    private void seleccionarCitaDeTabla() {
        int fila = vistaCita.tblCitas.getSelectedRow();
        if (fila >= 0) {
            // Guardamos el ID para usarlo al editar
            idCitaSeleccionada = Integer.parseInt(vistaCita.tblCitas.getValueAt(fila, 0).toString());

            // Pasamos Fecha y Hora a los txt
            vistaCita.txtFechaCita.setText(vistaCita.tblCitas.getValueAt(fila, 2).toString()); // Fecha se guarda como YYYY-MM-DD en tabla, java lo maneja

            // Ojo: Si la fecha sale en formato raro (yyyy-mm-dd), formateamos:
            try {
                String fechaTabla = vistaCita.tblCitas.getValueAt(fila, 2).toString();
                SimpleDateFormat sdfSql = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat sdfVista = new SimpleDateFormat("dd/MM/yyyy");
                Date f = sdfSql.parse(fechaTabla);
                vistaCita.txtFechaCita.setText(sdfVista.format(f));
            } catch (Exception ex) {
                // Si falla, intentamos poner el texto directo
                vistaCita.txtFechaCita.setText(vistaCita.tblCitas.getValueAt(fila, 2).toString());
            }

            vistaCita.txtHora.setText(vistaCita.tblCitas.getValueAt(fila, 3).toString());

            // SELECCIONAR EL PACIENTE EN EL COMBOBOX
            int idPac = Integer.parseInt(vistaCita.tblCitas.getValueAt(fila, 4).toString());
            seleccionarPacienteEnCombo(idPac);
        }
    }

    // Método auxiliar para buscar al paciente en el combo por su ID
    private void seleccionarPacienteEnCombo(int idPacienteBuscado) {
        for (int i = 0; i < vistaCita.cbxPaciente.getItemCount(); i++) {
            Paciente p = (Paciente) vistaCita.cbxPaciente.getItemAt(i);
            if (p.getId() == idPacienteBuscado) {
                vistaCita.cbxPaciente.setSelectedIndex(i);
                break;
            }
        }
    }

    // 3. LOGICA DEL BOTÓN EDITAR CITA
    private void editarCita() {
        if (idCitaSeleccionada == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione una cita de la tabla primero");
            return;
        }

        // Reutilizamos validaciones
        Paciente paciente = (Paciente) vistaCita.cbxPaciente.getSelectedItem();
        String fechaTexto = vistaCita.txtFechaCita.getText();
        String horaTexto = vistaCita.txtHora.getText();

        if (paciente == null || fechaTexto.isEmpty() || horaTexto.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Complete los datos");
            return;
        }

        // Convertir Fecha
        Date fecha = convertirFecha(fechaTexto);
        if (fecha == null) {
            JOptionPane.showMessageDialog(null, "Fecha inválida");
            return;
        }

        // Crear objeto y setear datos nuevos
        Cita c = new Cita();
        c.setId(idCitaSeleccionada); // IMPORTANTE: El ID original
        c.setIdPaciente(paciente.getId()); // Puede ser un paciente nuevo
        c.setFechaCita(fecha);
        c.setHora(horaTexto);

        if (daoCita.editar(c)) {
            JOptionPane.showMessageDialog(null, "Cita editada correctamente");
            listarCitasEnTabla(ordenActual); // Refrescamos la tabla visual
            limpiarCampos();
            idCitaSeleccionada = -1; // Reseteamos selección
        }
    }

    private void limpiarCampos() {
        vistaCita.txtFechaCita.setText("");
        vistaCita.txtHora.setText("");
        if (vistaCita.cbxPaciente.getItemCount() > 0) {
            vistaCita.cbxPaciente.setSelectedIndex(0);
        }
    }

    // --- NUEVO MÉTODO PARA EL BOTÓN ORDENAR ---
    private void mostrarOpcionesOrden() {
        // Opciones para la lista desplegable
        String[] opciones = {"Por Fechas", "Por Nombres", "Por Meses"};

        // JOptionPane con ComboBox (retorna un Object, hay que convertir a String)
        String seleccion = (String) JOptionPane.showInputDialog(
                null,
                "Seleccione el criterio de ordenamiento:",
                "Ordenar Tabla de Citas",
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                ordenActual // Opción seleccionada por defecto
        );

        // Si el usuario no presiona Cancelar (seleccion no es null)
        if (seleccion != null) {
            ordenActual = seleccion; // Guardamos la preferencia
            listarCitasEnTabla(ordenActual); // Refrescamos la tabla
            JOptionPane.showMessageDialog(null, "Tabla ordenada: " + ordenActual);
        }
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

            // 2. ACTUALIZAMOS LA TABLA VISUAL INMEDIATAMENTE
            listarCitasEnTabla(ordenActual);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (vistaCita != null) {

            // 1. BOTÓN MOSTRAR TABLA Y ACTUALIZAR COMBO
            if (e.getSource() == vistaCita.btnMostar) {
                listarTabla();
                cargarPacientes();
                listarCitasEnTabla(ordenActual);
            } // 2. BOTÓN GUARDAR CITA (Este SÍ valida datos)
            else if (e.getSource() == vistaCita.btnGuardarCita) {
                guardarCita(); // <--- Aquí adentro está el mensaje "Complete los datos"
            }// 3. EL NUEVO BOTÓN
            else if (e.getSource() == vistaCita.btnEditarCita) {
                editarCita();
                listarCitasEnTabla(ordenActual);
            } // BOTÓN ORDENAR
            else if (e.getSource() == vistaCita.btnOrdenar) {
                mostrarOpcionesOrden();
            }
        }
    }

    // Función auxiliar para validar formato HH:mm
    private boolean validarFormatoHora(String hora) {
        // Expresión regular: 00 a 23 : 00 a 59
        return hora.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$");
    }

}
