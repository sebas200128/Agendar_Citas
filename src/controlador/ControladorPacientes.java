
package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter; // 👈 Importante para el click en la tabla
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.Paciente;
import modelo.PacienteDAO;
import vista.frmRegistrar;
import vista.frmEditarRegistro;

public class ControladorPacientes implements ActionListener {

    private PacienteDAO dao = new PacienteDAO();
    private Paciente p = new Paciente();

    private frmRegistrar vistaReg;
    private frmEditarRegistro vistaEdit;

    // Variable auxiliar para guardar el ID del paciente seleccionado en la tabla
    private int idSeleccionado = -1;

    //Constructor para Registrar
    public ControladorPacientes(frmRegistrar vistaReg) {
        this.vistaReg = vistaReg;
        this.vistaReg.btnRegistrar.addActionListener(this);
    }

    // CONSTRUCTOR 2: Para la ventana de Editar
    public ControladorPacientes(frmEditarRegistro vistaEdit) {
        this.vistaEdit = vistaEdit;

        // Escuchamos los botones
        this.vistaEdit.btnMostrarRegP.addActionListener(this);
        this.vistaEdit.btnEditarReg.addActionListener(this);
        this.vistaEdit.btnEliminarReg.addActionListener(this); // Opcional

        // Agregamos el evento del CLICK a la tabla
        this.vistaEdit.tblDatosPaciente.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarFila();
            }
        });
    }

    // METODO INICIAR VENTANA REGISTRAR
    public void iniciarRegistrar() {
        vistaReg.setTitle("Registrar");
        vistaReg.setResizable(false);
        vistaReg.setVisible(true);
    }

    // Método para iniciar ventana Editar
    public void iniciarEditar() {
        vistaEdit.setTitle("Editar Pacientes");
        vistaEdit.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // Lógica EXCLUSIVA para la ventana REGISTRAR
        if (vistaReg != null) {
            if (e.getSource() == vistaReg.btnRegistrar) {
                registrar();
            }
        }

        // --- LÓGICA DE EDICIÓN ---
        if (vistaEdit != null) {

            // 1. BOTÓN MOSTRAR: Carga tabla y llena el ComboBox
            if (e.getSource() == vistaEdit.btnMostrarRegP) {
                listarTablaEditar();
                llenarComboCampos();
            } // 2. BOTÓN EDITAR: Realiza la actualización
            else if (e.getSource() == vistaEdit.btnEditarReg) {
                editarDatos();
            } else if (e.getSource() == vistaEdit.btnEliminarReg) {
                eliminarPaciente();
            }
        }
    }

    private void listarTablaEditar() {
        List<Paciente> lista = dao.listar();
        String[] titulos = {"ID", "Nombre", "DNI", "Genero", "Nacimiento"};
        DefaultTableModel model = new DefaultTableModel(null, titulos);

        for (Paciente pa : lista) {
            Object[] fila = {pa.getId(), pa.getNombre(), pa.getDni(), pa.getGenero(), pa.getFechaNac()};
            model.addRow(fila);
        }
        vistaEdit.tblDatosPaciente.setModel(model);
    }

    private void llenarComboCampos() {
        vistaEdit.cbxDatoEditar.removeAllItems();
        // Agregamos EXACTAMENTE los nombres que usaremos en el DAO
        vistaEdit.cbxDatoEditar.addItem("Nombre");
        vistaEdit.cbxDatoEditar.addItem("DNI");
        vistaEdit.cbxDatoEditar.addItem("Genero");
        vistaEdit.cbxDatoEditar.addItem("Fecha Nacimiento");
    }

    // Este método se ejecuta al hacer click en la tabla
    private void seleccionarFila() {
        int fila = vistaEdit.tblDatosPaciente.getSelectedRow();
        if (fila >= 0) {
            // Obtenemos el ID de la columna 0 y lo guardamos en la variable global
            idSeleccionado = Integer.parseInt(vistaEdit.tblDatosPaciente.getValueAt(fila, 0).toString());

            // Opcional: Mostrar el nombre del seleccionado en algún lado para feedback visual
            String nombre = vistaEdit.tblDatosPaciente.getValueAt(fila, 1).toString();
            System.out.println("Seleccionado ID: " + idSeleccionado + " - " + nombre);
        }
    }

    private void editarDatos() {
        // 1. Validaciones
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(null, "Primero haga CLICK en un paciente de la tabla");
            return;
        }

        if (vistaEdit.txtDatoEditar.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Escriba el nuevo valor");
            return;
        }

        // 2. Obtener datos
        String campo = vistaEdit.cbxDatoEditar.getSelectedItem().toString();
        String valor = vistaEdit.txtDatoEditar.getText();

        // 3. Llamar al DAO
        if (dao.actualizarDato(idSeleccionado, campo, valor)) {
            JOptionPane.showMessageDialog(null, "Dato actualizado correctamente");

            // 4. Refrescar tabla y limpiar
            listarTablaEditar();
            vistaEdit.txtDatoEditar.setText("");
            idSeleccionado = -1; // Reseteamos la selección para obligar a elegir de nuevo
        }
    }

    private void registrar() {
        if (vistaReg.txtNombre.getText().isEmpty() || vistaReg.txtDni.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "LLene todos los campos");
            return;
        }

        p.setNombre(vistaReg.txtNombre.getText());
        p.setDni(vistaReg.txtDni.getText());
        p.setGenero(vistaReg.cbxGenero.getSelectedItem().toString());

        //Convertir fecha
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            java.util.Date parsed = format.parse(vistaReg.txtFechaNacimiento.getText());
            p.setFechaNac(new Date(parsed.getTime()));
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(null, "Formato de fecha incorrecto (dd/MM/yyyy)");
            return;
        }
        if (dao.registrar(p)) {
            JOptionPane.showMessageDialog(null, "Registrado exitosamente");
            limpiarRegistrar();
        } else {
            JOptionPane.showMessageDialog(null, "Error al registrar");
        }
    }

    private void limpiarRegistrar() {
        // Limpiamos los campos de texto
        vistaReg.txtNombre.setText("");
        vistaReg.txtDni.setText("");
        vistaReg.txtFechaNacimiento.setText("");

        // Reiniciamos el Combo Box al primer ítem (Índice 0)
        if (vistaReg.cbxGenero.getItemCount() > 0) {
            vistaReg.cbxGenero.setSelectedIndex(0);
        }

        // Ponemos el cursor en el primer campo para escribir rápido de nuevo
        vistaReg.txtNombre.requestFocus();
    }

    private void eliminarPaciente() {
        // 1. Verificar si hay alguien seleccionado
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(null, "Primero haga CLICK en un paciente de la tabla para eliminarlo.");
            return;
        }

        // 2. Preguntar ¿Estás seguro?
        int confirmacion = JOptionPane.showConfirmDialog(null,
                "¿Está seguro de eliminar al paciente seleccionado?\nEsta acción no se puede deshacer.",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            // 3. Llamar al DAO
            if (dao.eliminar(idSeleccionado)) {
                JOptionPane.showMessageDialog(null, "Paciente eliminado correctamente.");

                // 4. Actualizar tabla y resetear variable
                listarTablaEditar();
                vistaEdit.txtDatoEditar.setText("");
                idSeleccionado = -1;
            }
        }
    }
}
