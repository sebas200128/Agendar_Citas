
import controlador.ControladorLogin;
import modelo.UsuarioDAO;
import vista.frmLogin;

public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        frmLogin vistaL = new frmLogin();
        UsuarioDAO modeloL = new UsuarioDAO();
        ControladorLogin controlL = new ControladorLogin(vistaL, modeloL);

        controlL.iniciar();
    }

}
