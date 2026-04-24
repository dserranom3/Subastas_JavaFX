package com.example.subastas_javafx;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;

public class ConexionBD {

    public static Connection conectar() {
        try {

            BufferedReader lector = new BufferedReader(new FileReader("db_config.txt"));
            String url = lector.readLine();
            String usuario = lector.readLine();
            String contrasena = lector.readLine();
            lector.close();

            return DriverManager.getConnection(url, usuario, contrasena);

        } catch (Exception e) {
            System.out.println("Error de conexión a la BD: " + e.getMessage());
            return null;
        }
    }


    public static void main(String[] args) {
        Connection prueba = ConexionBD.conectar();
        if (prueba != null) {
            System.out.println("¡ÉXITO! ");
        } else {
            System.out.println("Falló la conexión. ");
        }
    }
}