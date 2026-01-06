
package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import modelo.UsuarioDAO;
import vista.frmLogin;
import vista.frmPrincipal;

public class ControladorLogin implements ActionListener {
    
    private frmLogin vista;
    private UsuarioDAO modelo;
    private int intentos = 0;
    
    public ControladorLogin(frmLogin vista, UsuarioDAO modelo) {
        this.vista = vista;
        this.modelo = modelo;
        this.vista.btnAcceder.addActionListener(this);
    }
    
    public void iniciar() {
        vista.setTitle("Login");
        vista.setSize(320, 350);
        vista.setLocationRelativeTo(null);
        vista.setResizable(false); 
        vista.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.btnAcceder) {
            String user = vista.txtUsuario.getText();
            String pass = new String(vista.txtContraseña.getPassword());
            
            if (modelo.validarUsuario(user, pass)) {
                JOptionPane.showMessageDialog(null, "Inicio exitoso");
                frmPrincipal principal = new frmPrincipal();
                // Aqui llamamos al controlador principal pero para simplificar:
                new ControladorPrincipal(principal).iniciar();
                vista.dispose();
            } else {
                intentos++;
                JOptionPane.showMessageDialog(null, "Datos incorrectos. Intento " + intentos + "/3");
                if (intentos >= 3) {
                    JOptionPane.showMessageDialog(null, "Has excedido los intentos");
                    System.exit(0);
                }
            }
        }
    }
}
