package com.example.subastas_javafx;

import java.time.LocalDate;

public class Vendedor extends Usuario {

    // Atributos EXCLUSIVOS del Vendedor
    private int puntuacion;
    private String direccion;

    // Constructor por defecto
    public Vendedor() {
        super();
    }

    // Constructor lleno
    // Fíjate que pedimos TODOS los datos, los del padre y los del hijo
    public Vendedor(String nombreCompleto, String identificacion, LocalDate fechaNacimiento,
                    String contrasena, String correoElectronico, int puntuacion, String direccion) {

        // 1. Primero, mandamos los datos básicos al padre [cite: 242]
        super(nombreCompleto, identificacion, fechaNacimiento, contrasena, correoElectronico);

        // 2. Luego, guardamos los datos específicos de esta clase hija [cite: 242]
        this.puntuacion = puntuacion;
        this.direccion = direccion;
    }

    // --- Getters y Setters específicos del Vendedor ---
    public int getPuntuacion() { return puntuacion; }
    public void setPuntuacion(int puntuacion) { this.puntuacion = puntuacion; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    @Override
    public String toString() {
        return "[VENDEDOR] " + super.toString() + " | Puntos: " + puntuacion + " | Dir: " + direccion;
    }
}