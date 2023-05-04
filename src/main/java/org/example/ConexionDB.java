package org.example;

import javax.swing.*;
import java.sql.*;

public class ConexionDB {
    private String user = "root";
    private String password = "root";
    private String db = "argprograma";
    private String ip = "localhost";
    private String port = "3306";

    Connection conexion;

    private String PathDB = "jdbc:mysql://" + ip + ":" + port + "/" + db;

    public Connection connect() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection(PathDB, user, password);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "Error connecting to db.");
        }
        return conexion;
    }

    public void buildTables() throws SQLException {
        try {
            Statement stmt = connect().createStatement();
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS `Alumno` (" +
                    "`id` INT AUTO_INCREMENT PRIMARY KEY," +
                    "`nombre` VARCHAR(50)," +
                    "`legajo` INT," +
                    "`materiasAprobadas` JSON)");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS `Materia` (" +
                    "`id` INT AUTO_INCREMENT PRIMARY KEY," +
                    "`nombre` VARCHAR(50)," +
                    "`correlativas` JSON)");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS `Inscripcion` (" +
                    "`id` INT AUTO_INCREMENT PRIMARY KEY," +
                    "`materia` JSON," +
                    "`alumno` JSON," +
                    "`fecha` DATE," +
                    "`aprobada` BOOLEAN)");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
}
