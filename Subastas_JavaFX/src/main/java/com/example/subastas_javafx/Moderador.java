package com.example.subastas_javafx;

import java.time.LocalDate;

// La palabra "extends" hace la magia de la herencia
public class Moderador extends Usuario {

    // 1. CONSTRUCTOR POR DEFECTO
    public Moderador() {
        super(); // "super()" llama al constructor vacío de la clase padre (Usuario)
    }

    // 2. CONSTRUCTOR CON PARÁMETROS
    public Moderador(String nombreCompleto, String identificacion, LocalDate fechaNacimiento,
                     String contrasena, String correoElectronico) {
        // Le pasamos estos datos directamente a la clase padre para que ella los guarde
        super(nombreCompleto, identificacion, fechaNacimiento, contrasena, correoElectronico);
    }

    // 3. SOBRESCRIBIR toString
    @Override
    public String toString() {
        // Aprovechamos el toString que ya hicimos en Usuario y le agregamos una etiqueta
        return "[MODERADOR] " + super.toString();
    }
}