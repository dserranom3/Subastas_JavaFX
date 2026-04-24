package com.example.subastas_javafx;

import java.time.LocalDate;
import java.util.ArrayList;

public class OrdenAdjudicacion {
    private String nombreGanador;
    private LocalDate fechaOrden;
    private ArrayList<ObjetoOfrecido> objetosAdjudicados;
    private double precioTotal;

    public OrdenAdjudicacion(String nombreGanador, ArrayList<ObjetoOfrecido> objetosAdjudicados, double precioTotal) {
        this.nombreGanador = nombreGanador;
        this.fechaOrden = LocalDate.now();
        this.objetosAdjudicados = objetosAdjudicados;
        this.precioTotal = precioTotal;
    }

    public String getNombreGanador() {
        return nombreGanador;
    }

    public LocalDate getFechaOrden() {
        return fechaOrden;
    }

    public ArrayList<ObjetoOfrecido> getObjetosAdjudicados() {
        return objetosAdjudicados;
    }

    public double getPrecioTotal() {
        return precioTotal;
    }

    @Override
    public String toString() {
        return "Orden de Compra - Ganador: " + nombreGanador + " | Fecha: " + fechaOrden + " | Total: $" + precioTotal;
    }
}