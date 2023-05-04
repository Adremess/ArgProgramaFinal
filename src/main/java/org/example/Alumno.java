package org.example;


import com.google.gson.Gson;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Alumno {
    String nombre;
    int legajo;
    List<String> materiasAprobadas;
    private Scanner sc = new Scanner(System.in);
    private String table_name = "alumno";
    private Gson gson = new Gson();

    public Alumno(int legajo) throws SQLException {
        this.nombre = String.valueOf(existeAlumno(legajo).get("nombre"));
        this. legajo = legajo;
        this.materiasAprobadas =  getMateriasAprobadas(legajo);
    }

    public Alumno() throws SQLException {
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setLegajo(int legajo) {
        this.legajo = legajo;
    }

    public List getAprobadas() { return this.materiasAprobadas; }

    public void addAlumno() throws SQLException {
        String cargaMaterias = "";
        int count = 0;
        System.out.println("Nombre: ");
        setNombre(sc.nextLine());
        System.out.println("Legajo: ");
        setLegajo(sc.nextInt());
        while (String.valueOf(this.legajo).length() != 6) {
            System.out.println("El numero de legajo debe contener 6 digitos, vuelva a ingresar legajo:");
            setLegajo(sc.nextInt());
        }
        while (!existeAlumno(this.legajo).isEmpty()) {
            System.out.println("El numero de legajo ya esta asignado a un alumno existente, ingrese otro legajo:");
            setLegajo(sc.nextInt());
        }
        System.out.println("Cantidad de materias aprobadas: ");
        count = sc.nextInt();
        System.out.println("Materias aprobadas: ");
        sc.nextLine();
        for (int i = 0; i < count; i++) {
            cargaMaterias = sc.nextLine();
            materiasAprobadas.add(cargaMaterias);
        }
        try {
            ConexionDB conn = new ConexionDB();
            Statement stmt = conn.connect().createStatement();
            int rowsCount = stmt.executeUpdate("INSERT INTO `" + table_name + "`" +
                    "(nombre, legajo, materiasAprobadas) VALUES(" +
                    "\"" + this.nombre + "\"," +
                    this.legajo + "," +
                    "'" + gson.toJson(materiasAprobadas) + "');");
            if (rowsCount > 0) {
                System.out.println("Alumno agregado.");
            } else {
                System.out.println("Error ingresando al alumno.");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public List<String> getMateriasAprobadas(int legajo) throws SQLException {
        List<String> materias = new ArrayList<>();
        try {
            ConexionDB conn = new ConexionDB();
            Statement stmt = conn.connect().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT materiasAprobadas FROM alumno WHERE legajo = " + legajo + ";");
            rs.next();
            materias = gson.fromJson(rs.getString("materiasAprobadas"), ArrayList.class);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return materias;
    }

    public HashMap existeAlumno(int legajo) throws SQLException {
        HashMap<String, String> alumno = null;
        try {
            ConexionDB conn = new ConexionDB();
            Statement stmt = conn.connect().createStatement();
            alumno = new HashMap<>();
            ResultSet rs = stmt.executeQuery("SELECT * FROM alumno WHERE legajo = " + legajo + ";");
            while (rs.next()) {
                alumno.put("Nombre", rs.getString("nombre"));
                alumno.put("Legajo", String.valueOf(rs.getInt("legajo")));
                alumno.put("Aprobadas", rs.getString("materiasAprobadas"));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return alumno;
    }
}
