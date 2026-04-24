package com.example.subastas_javafx;

import java.util.Objects;
/**
 * Entidad que representa una propuesta económica (puja) realizada por un Coleccionista
 * para intentar adquirir el lote de una Subasta en curso.
 */
public class Oferta {

    private Coleccionista oferente;
    private double precioOfertado;
    private Subasta subasta; // <-- Nueva pieza para vincular la oferta

    public Oferta() {
    }

    /**
     * Constructor principal para crear una nueva oferta vinculada a una subasta.
     *
     * @param oferente El objeto Coleccionista que realiza la puja.
     * @param precioOfertado El monto de dinero que el coleccionista está dispuesto a pagar.
     * @param subasta La subasta sobre la cual recae esta oferta.
     */
    public Oferta(Coleccionista oferente, double precioOfertado, Subasta subasta) {
        this.oferente = oferente;
        this.precioOfertado = precioOfertado;
        this.subasta = subasta;
    }

    public Coleccionista getOferente() { return oferente; }
    public void setOferente(Coleccionista oferente) { this.oferente = oferente; }

    public double getPrecioOfertado() { return precioOfertado; }
    public void setPrecioOfertado(double precioOfertado) { this.precioOfertado = precioOfertado; }

    public Subasta getSubasta() { return subasta; }
    public void setSubasta(Subasta subasta) { this.subasta = subasta; }

    @Override
    public String toString() {
        return "Oferta de: " + oferente.getNombreCompleto() +
                " | Puntuación del oferente: " + oferente.getPuntuacion() +
                " | Monto: $" + precioOfertado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Oferta oferta = (Oferta) o;
        return Double.compare(oferta.precioOfertado, precioOfertado) == 0 &&
                Objects.equals(oferente, oferta.oferente) &&
                Objects.equals(subasta, oferta.subasta);
    }
}