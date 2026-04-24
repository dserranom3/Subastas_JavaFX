package com.example.subastas_javafx;

import java.time.LocalDate;
import java.util.ArrayList;


public class Coleccionista extends Usuario {

    private int puntuacion;
    private String direccion;
    private ArrayList<String> intereses;

    public Coleccionista() {
        super();
        this.intereses = new ArrayList<>();
    }

    public Coleccionista(String nombre, String id, LocalDate fechaNac, String pass, String correo, int puntuacion, String direccion) {

        super(nombre, id, fechaNac, pass, correo);
        this.puntuacion = puntuacion;
        this.direccion = direccion;
        this.intereses = new ArrayList<>();
    }


    public int getPuntuacion() { return puntuacion; }
    public void setPuntuacion(int puntuacion) { this.puntuacion = puntuacion; }
}