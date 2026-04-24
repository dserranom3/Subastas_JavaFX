package com.example.subastas_javafx;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;

public class UsuarioDAO {

    // ==========================================
    // 1. REGISTRAR USUARIO (CREATE)
    // ==========================================
    public boolean registrarUsuario(Usuario u, int idRol, int puntuacion, String direccion) {
        String sql = "INSERT INTO Usuario (nombreCompleto, identificacion, fechaNacimiento, correo, password, idRol, puntuacion, direccion) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, u.getNombreCompleto());
            ps.setString(2, u.getIdentificacion());

            if (u.getFechaNacimiento() != null) {
                ps.setDate(3, Date.valueOf(u.getFechaNacimiento()));
            } else {
                ps.setNull(3, java.sql.Types.DATE);
            }

            ps.setString(4, u.getCorreoElectronico());
            ps.setString(5, u.getContrasena());
            ps.setInt(6, idRol);
            ps.setInt(7, puntuacion);
            ps.setString(8, direccion);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (Exception e) {
            System.out.println("Error al registrar usuario en BD: " + e.getMessage());
            return false;
        }
    }

    // ==========================================
    // 2. CARGAR TODOS LOS USUARIOS (READ)
    // ==========================================
    public ArrayList<Usuario> cargarUsuariosDesdeBD() {
        ArrayList<Usuario> usuariosRecuperados = new ArrayList<>();
        String sql = "SELECT * FROM Usuario";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String nombre = rs.getString("nombreCompleto");
                String id = rs.getString("identificacion");
                LocalDate fechaNac = rs.getDate("fechaNacimiento") != null ? rs.getDate("fechaNacimiento").toLocalDate() : null;
                String correo = rs.getString("correo");
                String pass = rs.getString("password");
                int idRol = rs.getInt("idRol");
                int puntuacion = rs.getInt("puntuacion");
                String direccion = rs.getString("direccion");

                // Leemos el estado de activación que recién inyectamos
                boolean estadoActivo = rs.getBoolean("estadoActivo");

                Usuario usuarioRevivido = null;

                if (idRol == 1) {
                    usuarioRevivido = new Moderador(nombre, id, fechaNac, pass, correo);
                } else if (idRol == 2) {
                    usuarioRevivido = new Vendedor(nombre, id, fechaNac, pass, correo, puntuacion, direccion);
                } else if (idRol == 3) {
                    usuarioRevivido = new Coleccionista(nombre, id, fechaNac, pass, correo, puntuacion, direccion);
                }

                if (usuarioRevivido != null) {
                    // Restauramos el estado activo/inactivo antes de agregarlo a la lista
                    usuarioRevivido.setEstadoActivo(estadoActivo);
                    usuariosRecuperados.add(usuarioRevivido);
                }
            }
        } catch (Exception e) {
            System.out.println("Error al cargar usuarios de BD: " + e.getMessage());
        }

        return usuariosRecuperados;
    }

    // ==========================================
    // 3. ACTUALIZAR USUARIO (UPDATE)
    // ==========================================
    public boolean actualizarUsuario(Usuario u) {
        String sql = "UPDATE Usuario SET correo = ?, password = ? WHERE identificacion = ?";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, u.getCorreoElectronico());
            ps.setString(2, u.getContrasena());
            ps.setString(3, u.getIdentificacion());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    // ==========================================
    // 4. CAMBIAR ESTADO ACTIVO/INACTIVO (UPDATE)
    // ==========================================
    public boolean cambiarEstadoUsuario(String identificacion, boolean nuevoEstado) {
        String sql = "UPDATE Usuario SET estadoActivo = ? WHERE identificacion = ?";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, nuevoEstado);
            ps.setString(2, identificacion);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al cambiar estado del usuario: " + e.getMessage());
            return false;
        }
    }
    public boolean existeModerador() {

        String sql = "SELECT COUNT(*) FROM Usuario WHERE idRol = 1";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            System.out.println("Error verificando moderador: " + e.getMessage());
        }
        return false;
    }
    // ==========================================
    // ASCENDER COLECCIONISTA A MODERADOR
    // ==========================================
    public boolean ascenderAModerador(String identificacion) {

        String sql = "UPDATE Usuario SET idRol = 1 WHERE identificacion = ?";

        try (java.sql.Connection conn = ConexionBD.conectar();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, identificacion);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error ascendiendo a moderador: " + e.getMessage());
            return false;
        }
    }
}