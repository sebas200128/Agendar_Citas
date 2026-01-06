/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Sebastian
 */
public class Conexion {

    Connection con;

    public Connection getConnection() {
        try {
            String myBD = "jdbc:mysql://localhost:3306/gestion_citas?serverTimezone=UTC";
            con = DriverManager.getConnection(myBD, "root", "");
        } catch (SQLException e) {
            System.err.println("Error conexion: " + e.toString());
        }
        return con;
    }
}
