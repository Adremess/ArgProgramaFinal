package org.example;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Materia {
    String nombre;
    List<String> correlativas;
    private Scanner sc = new Scanner(System.in);
    private String table_name = "materia";
    private Gson gson = new Gson();

    public Materia(String nombre) throws SQLException {
        this.nombre = nombre;
        this.correlativas = getCorrelativas(nombre);
    }

    public Materia() throws SQLException {
    }

    public void add() throws SQLException {
        String materia;
        char corr;
        int corrCount;
        String next;
        System.out.println("Materia a ingresar: ");
        materia = sc.nextLine();
        System.out.println("Posee correlativas? S/N");
        corr = sc.next().charAt(0);
        if (Character.toUpperCase(corr) == 'S') {
            System.out.println("Cantidad de correlativas: ");
            corrCount = sc.nextInt();
            sc.nextLine();
            System.out.println("Ingrese las correlativas separandolas con un ENTER: ");
            for (int i = 0; i < corrCount; i++) {
                next = sc.nextLine();
                this.correlativas.add(next);
            }
            addMateria(materia);
            addCorrelativas(this.correlativas, materia);
        } else {
            addMateria(materia);
        }
    }

    public void addMateria(String materia) throws SQLException {
        try {
            ConexionDB conn = new ConexionDB();
            Statement stmt = conn.connect().createStatement();
            stmt.executeUpdate("INSERT INTO `" + table_name + "` (nombre) " +
                    "VALUES(\"" + materia + "\")");
            System.out.println("Materia agregada.");
        } catch (Exception e) {
            System.out.println("Error al agregar la materia. " + e.getMessage());
        }
    }

    public void addCorrelativas(List<String> correlativas, String materia) throws SQLException {
        String query;
        int rowsCount;
        try {
            ConexionDB conn = new ConexionDB();
            Statement stmt = conn.connect().createStatement();
            query = "UPDATE `" + table_name + "` SET correlativas = '";
            query += gson.toJson(correlativas);
            query += "' WHERE nombre = '" + materia + "'";
            rowsCount = stmt.executeUpdate(query);

            if (rowsCount > 0) {
                System.out.println("Correlativas agregadas.");
            } else {
                System.out.println("Error ingresando las correlativas.");
            }
        } catch (Exception e) {
            System.out.println("Error al agregar las materias correlativas. " + e.getMessage());
        }
    }

    public List<String> getCorrelativas(String materia) throws SQLException {
        List<String> listaCorrelativas = null;
        try {
            ConexionDB conn = new ConexionDB();
            Statement stmt = conn.connect().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + table_name + " WHERE nombre = '" + materia + "'");
            rs.next();
            ObjectMapper mapper = new ObjectMapper();
            if (rs.getObject("correlativas") == null) {
                return listaCorrelativas;
            } else {
                listaCorrelativas = mapper.readValue(rs.getString("correlativas"), List.class);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return listaCorrelativas;
    }

    public HashMap materiaHM(String materia) throws SQLException {
        HashMap<String, String> materiaHM = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            ConexionDB conn = new ConexionDB();
            Statement stmt = conn.connect().createStatement();
            materiaHM = new HashMap<>();
            ResultSet rs = stmt.executeQuery("SELECT * FROM materia WHERE nombre = \"" + materia + "\";");
            while (rs.next()) {
                materiaHM.put("Nombre", rs.getString("nombre"));
                materiaHM.put("Correlativas", String.valueOf(mapper.readValue(rs.getString("correlativas"), List.class)));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return materiaHM;
    }
}
