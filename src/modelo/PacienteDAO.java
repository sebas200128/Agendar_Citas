package modelo;

import config.Conexion;
import config.SentenciasSQL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Sebastian
 */
public class PacienteDAO {

    //Instancia de la clase conexion
    Conexion cn = new Conexion();
    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    // METODO REGISTRAR
    public boolean registrar(Paciente p) {
        String sql = SentenciasSQL.REGISTRAR;

        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);

            // ASIGNAMOS LOS VALORES A LOS ? DEL SQL
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getDni());
            ps.setString(3, p.getGenero());
            ps.setDate(4, (Date) p.getFechaNac());

            ps.execute();
            return true;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al registrar: " + e.toString());
            return false;
        } finally {
            cerrarConexiones();
        }
    }

    // METODO LISTAR(PARA TABLA)
    public List<Paciente> listar() {
        List<Paciente> lista = new ArrayList<>();
        String sql = SentenciasSQL.LISTAR;

        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Paciente p = new Paciente();
                p.setId(rs.getInt("id"));
                p.setNombre(rs.getString("nombre"));
                p.setDni(rs.getString("dni"));
                p.setGenero(rs.getString("genero"));
                p.setFechaNac(rs.getDate("fecha_nacimiento"));

                lista.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error al Listar: " + e.toString());
        } finally {
            cerrarConexiones();
        }
        return lista;
    }

    private void cerrarConexiones() {
        try {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            System.err.println("Error cerrando conexiones: " + e.toString());
        }
    }

    public List<Integer> listarIds() {
        List<Integer> lista = new ArrayList<>();
        String sql = SentenciasSQL.LISTAR;

        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(rs.getInt("id"));
            }
        } catch (SQLException e) {
            System.err.println("Error listar IDs: " + e);
        } finally {
            cerrarConexiones();
        }
        return lista;
    }

    public boolean actualizarDato(int id, String campoSeleccionado, String nuevoValor) {
        String sql = "";

        // Decidimos qué SQL usar según lo que el usuario escogió en el ComboBox
        switch (campoSeleccionado) {
            case "Nombre":
                sql = SentenciasSQL.ACTUALIZAR_NOMBRE;
                break;
            case "DNI":
                sql = SentenciasSQL.ACTUALIZAR_DNI;
                break;
            case "Genero":
                sql = SentenciasSQL.ACTUALIZAR_GENERO;
                break;
            case "Fecha Nacimiento":
                sql = SentenciasSQL.ACTUALIZAR_FECHA;
                break;
            default:
                return false;
        }

        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);

            // CASO ESPECIAL: FECHA (Hay que convertir el texto a Date)
            if (campoSeleccionado.equals("Fecha Nacimiento")) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                sdf.setLenient(false);
                java.util.Date fechaJava = sdf.parse(nuevoValor);
                ps.setDate(1, new java.sql.Date(fechaJava.getTime()));
            } // CASO NORMAL: TEXTO
            else {
                ps.setString(1, nuevoValor);
            }

            ps.setInt(2, id); // El ID siempre es el segundo parámetro
            ps.execute();
            return true;

        } catch (SQLException | java.text.ParseException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar: " + e.getMessage());
            return false;
        } finally {
            cerrarConexiones();
        }
    }

    public boolean eliminar(int id) {
        String sql = SentenciasSQL.ELIMINAR_PACIENTE; // "DELETE FROM paciente WHERE id = ?"
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
            return true;
        } catch (SQLException e) {
            // IMPORTANTE: Si el paciente tiene citas, esto dará error por llave foránea
            JOptionPane.showMessageDialog(null, "Error al eliminar: " + e.toString());
            return false;
        } finally {
            cerrarConexiones();
        }
    }
}
