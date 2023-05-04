package org.ArgProg;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class Inscripcion {
    Materia materia;
    Alumno alumno;
    boolean aprobada;
    private Scanner sc = new Scanner(System.in);
    private Gson gson = new Gson();

    public Inscripcion() throws SQLException {
    }

    public void setAprobada (boolean aprob) {
        this.aprobada = aprob;
    }

    public void setMateria (String materia) throws SQLException {
        this.materia = new Materia(materia);
    }

    public void setAlumno (int legajo) throws SQLException {
        this.alumno = new Alumno(legajo);
    }

    public boolean formAlumno() throws SQLException {
        String materia;
        int legajo;
        boolean validarInscripcion;
        List<String> materiasAprobadas;
        System.out.println("Materia a inscribir: ");
        materia = sc.nextLine();
        setMateria(materia);
        System.out.println("Legajo del alumno: ");
        legajo = sc.nextInt();
        setAlumno(legajo);
        sc.nextLine();
        try {
            ConexionDB db = new ConexionDB();
            materiasAprobadas = alumno.getMateriasAprobadas(legajo);
            validarInscripcion = validarMaterias(materiasAprobadas, materia);
            if (validarInscripcion) {
                setAprobada(true);
            } else {
                setAprobada(false);
            }
            System.out.println("Alumno anotado, consulte la lista para ver el estado de su inscripcion.");
            inscribirAlumno();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return this.aprobada;
    }

    public boolean validarMaterias(List<String> materiasAprobadas, String materia) throws SQLException {
        boolean valido = true;
        try {
            List<String> correlativas;
            correlativas = this.materia.getCorrelativas(materia);
            if (correlativas.size() > 0) {
                for (int i = 0; i < correlativas.size(); i++) {
                    if (!materiasAprobadas.contains(correlativas.get(i))) {
                        valido = false;
                    }
                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return valido;
    }

    public void inscribirAlumno() throws SQLException {
        try {
            HashMap<String, HashMap> obj = new HashMap<>();
            obj.put("materia", this.materia.materiaHM(this.materia.nombre));
            obj.put("alumno", this.alumno.existeAlumno(this.alumno.legajo));
            Connection conn = new ConexionDB().connect();
            Statement stmt = conn.createStatement();

            String materiaJson = gson.toJson(obj.get("materia")).replaceAll("\\\\", "\\\\\\\\");
            String alumnoJson = gson.toJson(obj.get("alumno")).replaceAll("\\\\", "\\\\\\\\");

            stmt.executeUpdate("INSERT INTO `inscripcion`(materia, alumno, fecha, aprobada) VALUES(" +
                    "'" + materiaJson + "'," +
                    "'" + alumnoJson + "'," +
                    " NOW()," +
                    this.aprobada + ");");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void consultarInscripcion() throws SQLException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ArrayList<HashMap> materia = new ArrayList<>();
            ArrayList<HashMap> alumno = new ArrayList<>();
            ArrayList<Boolean> aprobado = new ArrayList<>();
            Connection conn = new ConexionDB().connect();
            Statement stmt = conn.createStatement();
            String respuesta;

            ResultSet rs = stmt.executeQuery("SELECT * FROM inscripcion");
            while (rs.next()) {
                if (rs.getObject("materia") != null) {
                    materia.add(mapper.readValue(rs.getString("materia"), HashMap.class));
                    alumno.add(mapper.readValue(rs.getString("alumno"), HashMap.class));
                    aprobado.add(rs.getBoolean("aprobada"));
                } else {
                    System.out.println("Aun no hay inscripciones cargadas.");
                }
            }

            if (materia.size() > 0) {
                for (int i = 0; i < materia.size(); i++) {
                    if (aprobado.get(i) == true) {
                        respuesta = "cumple";
                    } else {
                        respuesta = "no cumple";
                    }
                    System.out.println("El estudiante " + alumno.get(i).get("Nombre") + " de legajo " + alumno.get(i).get("Legajo") + " " + respuesta + " con los requisitos para cursar " + materia.get(i).get("Nombre") + ".");
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
