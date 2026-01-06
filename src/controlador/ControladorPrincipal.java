package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import vista.frmPrincipal;
import vista.frmRegistrar;
import vista.frmCitas;
import vista.frmEditarRegistro;

/**
 *
 * @author Sebastian
 */
public class ControladorPrincipal implements ActionListener {

    private frmPrincipal vista;

    public ControladorPrincipal(frmPrincipal vista) {
        this.vista = vista;
        // demas botones para acceder a los formularios
        this.vista.btnFrmRegistrar.addActionListener(this);
        this.vista.btnFrmCitas.addActionListener(this);
        this.vista.btnFrmEditarRegistro.addActionListener(this);

    }

    public void iniciar() {
        vista.setTitle("Menu Principal");
        vista.setLocationRelativeTo(null);
        vista.setResizable(false);
        vista.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.btnFrmRegistrar) {

            // 🚫 Si ya está abierto, no lo vuelvas a crear
            if (internalAbierto(frmRegistrar.class)) {
                return;
            }
            frmRegistrar reg = new frmRegistrar(); // 1. Crear Vista
            ControladorPacientes ctrl = new ControladorPacientes(reg); // 2. Crear Controlador y pasarle la vista
            ctrl.iniciarRegistrar(); // 3. Iniciar

            // 👇 AGREGAR AL JDesktopPane
            vista.getEscritorio().add(reg);
            reg.setVisible(true);
            centrarInternal(reg);

        } else if (e.getSource() == vista.btnFrmCitas) {

            // 🚫 Si ya está abierto, no lo vuelvas a crear
            if (internalAbierto(frmCitas.class)) {
                return;
            }

            frmCitas cit = new frmCitas();
            ControladorCitas ctrl = new ControladorCitas(cit);
            ctrl.iniciarCita();

            vista.getEscritorio().add(cit);
            cit.setVisible(true);
            centrarInternal(cit);

        } else if (e.getSource() == vista.btnFrmEditarRegistro) {
            if (internalAbierto(frmEditarRegistro.class)) {
                return;
            }
            frmEditarRegistro editR = new frmEditarRegistro();
            ControladorPacientes ctrl = new ControladorPacientes(editR);
            ctrl.iniciarEditar();

            vista.getEscritorio().add(editR);
            editR.setVisible(true);
            centrarInternal(editR);
        }

    }

    private void centrarInternal(javax.swing.JInternalFrame frame) {
        int x = (vista.getEscritorio().getWidth() - frame.getWidth()) / 2;
        int y = (vista.getEscritorio().getHeight() - frame.getHeight()) / 2;
        frame.setLocation(x, y);
        frame.toFront();
    }

    private boolean internalAbierto(Class<?> clase) {
        for (var f : vista.getEscritorio().getAllFrames()) {
            if (clase.isInstance(f)) {
                f.toFront();
                return true;
            }
        }
        return false;
    }
}
