package com.example.subastas_javafx;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Clase de Acceso a Datos (DAO) encargada de gestionar las operaciones de persistencia
 * relacionadas con las ofertas en la base de datos SQL Server.
 */
public class OfertaDAO {

    // ==========================================
    // GUARDAR OFERTA NUEVA EN LA BASE DE DATOS
    // ==========================================
    /**
     * Almacena una nueva oferta realizada por un coleccionista en una subasta específica dentro de la base de datos.
     *
     * @param oferta El objeto Oferta que contiene los datos del postor y el precio ofertado.
     * @param subasta La subasta a la que se vincula esta puja.
     * @return true si la inserción en SQL Server fue exitosa, false si ocurrió un error de conexión o sintaxis.
     */
    public boolean guardarOfertaBD(Oferta oferta, Subasta subasta) {
        // Necesitamos guardar: Quién ofrece, cuánto ofrece y EN QUÉ subasta
        String sql = "INSERT INTO Oferta (idUsuario, precio, idSubasta) " +
                "VALUES ((SELECT idUsuario FROM Usuario WHERE identificacion = ?), ?, " +
                "(SELECT idSubasta FROM Subasta WHERE fechaVencimiento = ? AND precioMinimo = ?))";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, oferta.getOferente().getIdentificacion());
            ps.setDouble(2, oferta.getPrecioOfertado());

            // Usamos datos únicos de la subasta para encontrar su ID numérico
            ps.setTimestamp(3, Timestamp.valueOf(subasta.getFechaVencimiento()));
            ps.setDouble(4, subasta.getPrecioMinimoAceptacion());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error guardando oferta en BD: " + e.getMessage());
            return false;
        }
    }

    // ==========================================
    // BUSCAR OFERTAS DE UNA SUBASTA ESPECÍFICA
    // ==========================================

    /**
     * Recupera de la base de datos todas las ofertas asociadas a una subasta en particular,
     * cruzando información con la tabla de Usuarios para obtener los datos del postor.
     *
     * @param subasta La subasta de la cual se desean consultar las ofertas.
     * @return Un ArrayList con los objetos Oferta recuperados, o una lista vacía si no hay registros.
     */
    public ArrayList<Oferta> obtenerOfertasDeSubastaBD(Subasta subasta) {
        ArrayList<Oferta> ofertasDeLaSubasta = new ArrayList<>();

        String sql = "SELECT o.precio, u.identificacion, u.nombreCompleto " +
                "FROM Oferta o " +
                "INNER JOIN Usuario u ON o.idUsuario = u.idUsuario " +
                "INNER JOIN Subasta s ON o.idSubasta = s.idSubasta " +
                "WHERE s.fechaVencimiento = ? AND s.precioMinimo = ?";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(subasta.getFechaVencimiento()));
            ps.setDouble(2, subasta.getPrecioMinimoAceptacion());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    Coleccionista postor = new Coleccionista(
                            rs.getString("nombreCompleto"),
                            rs.getString("identificacion"),
                            null, "", "", 0, "" // Rellenamos lo que no ocupamos con null/vacío
                    );

                    Oferta of = new Oferta(postor, rs.getDouble("precio"), subasta);
                    ofertasDeLaSubasta.add(of);
                }
            }
        } catch (Exception e) {
            System.out.println("Error obteniendo las ofertas para el ganador: " + e.getMessage());
        }

        return ofertasDeLaSubasta;
    }
}