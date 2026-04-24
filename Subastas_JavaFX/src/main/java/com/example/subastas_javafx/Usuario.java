package com.example.subastas_javafx;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

public class Usuario {

    private String nombreCompleto;
    private String identificacion;
    private LocalDate fechaNacimiento;
    private String contrasena;
    private String correoElectronico;
    private boolean estadoActivo = true;

    public Usuario() {
    }

    public Usuario(String nombreCompleto, String identificacion, LocalDate fechaNacimiento,
                   String contrasena, String correoElectronico) {
        this.nombreCompleto = nombreCompleto;
        this.identificacion = identificacion;
        this.fechaNacimiento = fechaNacimiento;
        this.contrasena = contrasena;
        this.correoElectronico = correoElectronico;
    }

    public int getEdad() {
        if (this.fechaNacimiento != null) {
            return Period.between(this.fechaNacimiento, LocalDate.now()).getYears();
        }
        return 0;
    }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getIdentificacion() { return identificacion; }
    public void setIdentificacion(String identificacion) { this.identificacion = identificacion; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getCorreoElectronico() { return correoElectronico; }
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico = correoElectronico; }

    public boolean isEstadoActivo() {
        return estadoActivo;
    }

    public void setEstadoActivo(boolean estadoActivo) {
        this.estadoActivo = estadoActivo;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(identificacion, usuario.identificacion);
    }

    @Override
    public String toString() {
        return "[" + identificacion + "] " + nombreCompleto + " | Edad: " + getEdad() + " | Correo: " + correoElectronico;
    }
}