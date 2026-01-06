/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import config.Conexion;
import config.SentenciasSQL;
import java.sql.*;
import javax.swing.JOptionPane;

/**
 *
 * @author Sebastian
 */
public class CitaDAO {

    Conexion cn = new Conexion();
    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    public boolean guardar(Cita c) {
        String sql = SentenciasSQL.REGISTRAR_CITA;
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);

            ps.setInt(1, c.getIdPaciente());
            // Convertimos java.util.Date a java.sql.Date
            ps.setDate(2, new java.sql.Date(c.getFechaCita().getTime()));

            ps.setString(3, c.getHora()); // Guardamos la hora

            ps.execute();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar cita: " + e.toString());
            return false;
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex) {
            }
        }
    }

    // 1. MÉTODO PARA VERIFICAR SI EL PACIENTE YA TIENE CITA
    public boolean tieneCitaElPaciente(int idPaciente) {
        String sql = SentenciasSQL.VERIFICAR_CITA_PACIENTE;
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idPaciente);
            rs = ps.executeQuery();
            return rs.next(); // Retorna true si encuentra al menos una cita
        } catch (SQLException e) {
            System.err.println("Error verificando paciente: " + e);
            return false;
        }
    }

    // 2. MÉTODO PARA VERIFICAR SI LA FECHA YA ESTÁ OCUPADA
    public boolean existeCitaEnFecha(java.util.Date fecha) {
        String sql = SentenciasSQL.VERIFICAR_FECHA_OCUPADA;
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            // Convertimos la fecha de Util a SQL para comparar
            ps.setDate(1, new java.sql.Date(fecha.getTime()));
            rs = ps.executeQuery();
            return rs.next(); // Retorna true si ya hay una cita ese día
        } catch (SQLException e) {
            System.err.println("Error verificando fecha: " + e);
            return false;
        }
    }

    // NUEVO MÉTODO: Verifica Fecha Y Hora exacta
    public boolean existeCitaFechaHora(java.util.Date fecha, String hora) {
        String sql = SentenciasSQL.VERIFICAR_HORARIO_OCUPADO;
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setDate(1, new java.sql.Date(fecha.getTime()));
            ps.setString(2, hora); // Buscamos por hora también
            rs = ps.executeQuery();
            return rs.next(); // True si ya existe cita en ese día a esa hora
        } catch (SQLException e) {
            System.err.println("Error verificando horario: " + e);
            return false;
        }
    }

    public String listarCitasTexto() {
        String sql = SentenciasSQL.LISTAR_CITAS_DETALLE;
        StringBuilder texto = new StringBuilder(); // Usamos StringBuilder para unir texto eficientemente

        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            // Formato para que la fecha se vea bonita (dd/MM/yyyy)
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");

            while (rs.next()) {
                int idCita = rs.getInt("id");
                String nombre = rs.getString("nombre");
                Date fecha = rs.getDate("fecha");
                String hora = rs.getString("hora"); //obtenemos la hora

                // Construimos la línea: "ID: 1 | Paciente: Juan | Fecha: 20/10/2023"
                texto.append("ID: ").append(idCita)
                        .append("  |  Paciente: ").append(nombre)
                        .append("  |  Fecha: ").append(sdf.format(fecha))
                        .append("  |  Hora: ").append(hora) //La mostramos
                        .append("\n-------------------------------------------------------------------------------------------\n");
            }

        } catch (SQLException e) {
            System.err.println("Error listando citas: " + e);
            return "Error al obtener datos.";
        } finally {
            // No cerramos conexión aquí si planeas seguir usándola, 
            // pero idealmente deberías cerrar rs y ps.
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception ex) {
            }
        }

        // Si no hubo datos, el texto estará vacío
        if (texto.length() == 0) {
            return "No hay citas registradas.";
        }

        return texto.toString();
    }

}
