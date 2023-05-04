package org.example;

import com.mysql.cj.MysqlConnection;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException, IOException {
        int opt = 1;
        ConexionDB conn = new ConexionDB();

        Materia materia = new Materia();
        Alumno alumno = new Alumno();
        Inscripcion inscripcion = new Inscripcion();
        conn.buildTables();
        Scanner sc = new Scanner(System.in);

        do {
            System.out.println("Seleccione una opcion:\n1. Agregar materia\n2. Agregar alumno\n3. Inscribir alumno\n4. Consultar estado suscripciones a materias\n5. Salir");
            opt = sc.nextInt();
            switch (opt) {
                case 1:
                    materia.add();
                    System.in.read();
                    sc.nextLine();
                    break;
                case 2:
                    alumno.addAlumno();
                    System.in.read();
                    break;
                case 3:
                    inscripcion.formAlumno();
                    System.in.read();
                    break;
                case 4:
                    inscripcion.consultarInscripcion();
                    System.in.read();
                    break;
                case 5:
                    opt = 0;
                    break;
                default:
                    System.out.println("Ingrese una opcion valida.");
                    System.in.read();
                    break;
            }
        } while (opt != 0);

        conn.connect().close();
    }
}