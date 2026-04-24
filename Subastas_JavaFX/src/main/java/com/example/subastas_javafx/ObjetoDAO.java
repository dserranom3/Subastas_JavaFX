package com.example.subastas_javafx;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

public class ObjetoDAO {

    // ==========================================
    // 1. REGISTRAR OBJETO (CREATE)
    // ==========================================
    public boolean registrarObjeto(ObjetoOfrecido obj, String identificacionDueno) {

        String sql = "INSERT INTO Objeto (nombre, descripcion, estado, fechaCompra, idUsuario) " +
                "VALUES (?, ?, ?, ?, (SELECT idUsuario FROM Usuario WHERE identificacion = ?))";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, obj.getNombre());
            ps.setString(2, obj.getDescripcion());
            ps.setString(3, obj.getEstado());


            if (obj.getFechaCompra() != null) {
                ps.setDate(4, Date.valueOf(obj.getFechaCompra()));
            } else {
                ps.setNull(4, java.sql.Types.DATE);
            }


            ps.setString(5, identificacionDueno);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (Exception e) {
            System.out.println("Error al registrar objeto en BD: " + e.getMessage());
            return false;
        }
    }
    // ==========================================
    // CREACIÓN DE CATEGORÍAS
    // ==========================================
    public boolean registrarCategoria(String nombreCategoria) {

        String sql = "INSERT INTO Categoria (nombre) VALUES (?)";

        try (java.sql.Connection conn = ConexionBD.conectar();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombreCategoria);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error registrando categoría en BD (Verifica si tienes la tabla Categoria creada): " + e.getMessage());
            return false;
        }
    }
    public boolean guardarOfertaBD(Oferta oferta, Subasta subasta) {

        String sql = "INSERT INTO Oferta (idUsuario, monto, idSubasta) " +
                "VALUES ((SELECT idUsuario FROM Usuario WHERE identificacion = ?), ?, " +
                "(SELECT idSubasta FROM Subasta WHERE fechaVencimiento = ? AND precioMinimo = ?))";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, oferta.getOferente().getIdentificacion());
            ps.setDouble(2, oferta.getPrecioOfertado());


            ps.setTimestamp(3, Timestamp.valueOf(subasta.getFechaVencimiento()));
            ps.setDouble(4, subasta.getPrecioMinimoAceptacion());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error guardando oferta: " + e.getMessage());
            return false;
        }
    }
}