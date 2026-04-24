package com.example.subastas_javafx;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;



public class SubastaDAO {

    public boolean registrarSubastaConObjetos(Subasta subasta, String identificacionCreador) {

        String sqlSubasta = "INSERT INTO Subasta (fechaVencimiento, precioMinimo, estado, idUsuario) " +
                "VALUES (?, ?, ?, (SELECT idUsuario FROM Usuario WHERE identificacion = ?))";


        String sqlBuscarObjeto = "SELECT idObjeto FROM Objeto WHERE nombre = ? AND idUsuario = (SELECT idUsuario FROM Usuario WHERE identificacion = ?)";


        String sqlPuente = "INSERT INTO SubastaObjeto (idSubasta, idObjeto) VALUES (?, ?)";

        try (Connection conn = ConexionBD.conectar()) {

            conn.setAutoCommit(false);

            try (PreparedStatement psSubasta = conn.prepareStatement(sqlSubasta, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement psBuscarObj = conn.prepareStatement(sqlBuscarObjeto);
                 PreparedStatement psPuente = conn.prepareStatement(sqlPuente)) {


                if (subasta.getFechaVencimiento() != null) {
                    psSubasta.setTimestamp(1, Timestamp.valueOf(subasta.getFechaVencimiento()));
                } else {
                    psSubasta.setNull(1, java.sql.Types.TIMESTAMP);
                }
                psSubasta.setDouble(2, subasta.getPrecioMinimoAceptacion());
                psSubasta.setString(3, subasta.getEstado());
                psSubasta.setString(4, identificacionCreador);

                psSubasta.executeUpdate();


                ResultSet rs = psSubasta.getGeneratedKeys();
                int idSubastaGenerado = 0;
                if (rs.next()) {
                    idSubastaGenerado = rs.getInt(1); // Atrapamos el ID nuevo
                } else {
                    throw new Exception("No se pudo obtener el ID de la subasta.");
                }

                for (ObjetoOfrecido obj : subasta.getObjetosSubastados()) {

                    psBuscarObj.setString(1, obj.getNombre());
                    psBuscarObj.setString(2, identificacionCreador);
                    ResultSet rsObj = psBuscarObj.executeQuery();

                    int idObjetoEncontrado = 0;
                    if (rsObj.next()) {
                        idObjetoEncontrado = rsObj.getInt("idObjeto");
                    } else {
                        throw new Exception("No se encontró el objeto: " + obj.getNombre() + " en la base de datos.");
                    }

                    psPuente.setInt(1, idSubastaGenerado);
                    psPuente.setInt(2, idObjetoEncontrado);
                    psPuente.executeUpdate();
                }


                conn.commit();
                return true;

            } catch (Exception e) {

                conn.rollback();
                System.out.println("Transacción cancelada por error: " + e.getMessage());
                return false;
            }

        } catch (Exception e) {
            System.out.println("Error de conexión en SubastaDAO: " + e.getMessage());
            return false;
        }
    }
    // ==========================================
    // LISTAR MIS SUBASTAS DESDE LA BASE DE DATOS
    // ==========================================
    public ArrayList<Subasta> listarMisSubastasBD(String identificacionCreador) {
        ArrayList<Subasta> misSubastas = new ArrayList<>();


        String sql = "SELECT * FROM Subasta WHERE idUsuario = (SELECT idUsuario FROM Usuario WHERE identificacion = ?)";

        try (java.sql.Connection conn = ConexionBD.conectar();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {


            ps.setString(1, identificacionCreador);

            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Subasta sub = new Subasta(
                            rs.getTimestamp("fechaVencimiento").toLocalDateTime(),
                            null, // Para la tabla visual no necesitamos el objeto Usuario completo
                            0,
                            rs.getDouble("precioMinimo"),
                            rs.getString("estado")
                    );
                    misSubastas.add(sub);
                }
            }
        } catch (Exception e) {
            System.out.println("Error obteniendo mis subastas desde BD: " + e.getMessage());
        }
        System.out.println("Cédula buscada: " + identificacionCreador);
        System.out.println("Subastas encontradas en BD: " + misSubastas.size());
        return misSubastas;
    }
    // ==========================================
    // CARGAR TODAS LAS SUBASTAS (PARA OFERTAR)
    // ==========================================
    public ArrayList<Subasta> listarSubastasActivasBD() {
        ArrayList<Subasta> subastasActivas = new ArrayList<>();

        String sql = "SELECT * FROM Subasta";

        try (java.sql.Connection conn = ConexionBD.conectar();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Subasta sub = new Subasta(
                        rs.getTimestamp("fechaVencimiento").toLocalDateTime(),
                        null,
                        0,
                        rs.getDouble("precioMinimo"),
                        rs.getString("estado")
                );
                subastasActivas.add(sub);
            }
        } catch (Exception e) {
            System.out.println("Error obteniendo subastas activas: " + e.getMessage());
        }
        return subastasActivas;
    }
}