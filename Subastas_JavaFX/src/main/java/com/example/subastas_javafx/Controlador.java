package com.example.subastas_javafx;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Clase principal que actúa como intermediario entre la interfaz gráfica (Vista) y el acceso a datos (DAO).
 * Gestiona la lógica de negocio central del sistema de subastas, controlando el flujo de información.
 */

public class Controlador {

    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private ObjetoDAO objetoDAO = new ObjetoDAO();
    private SubastaDAO subastaDAO = new SubastaDAO();

    private OfertaDAO ofertaDAO;

    private ArrayList<Usuario> listaUsuarios;
    private ArrayList<Subasta> listaSubastas;
    private ArrayList<ObjetoOfrecido> listaObjetos;
    private ArrayList<Oferta> listaOfertas;

    public Controlador() {

        this.ofertaDAO = new OfertaDAO();

        this.listaUsuarios = usuarioDAO.cargarUsuariosDesdeBD();

        this.listaSubastas = new ArrayList<>();
        this.listaObjetos = new ArrayList<>();
        this.listaOfertas = new ArrayList<>();
    }

    public boolean existeModerador() {
        for (Usuario u : listaUsuarios) {
            // "instanceof" nos permite preguntar si un objeto pertenece a una clase específica
            if (u instanceof Moderador) {
                return true;
            }
        }
        return false;
    }

    // ==========================================
    // REGISTRO DE USUARIOS (HERENCIA Y BASE DE DATOS)
    // ==========================================

    public boolean registrarModerador(String nombre, String id, LocalDate fechaNac, String pass, String correo) {
        Moderador nuevoMod = new Moderador(nombre, id, fechaNac, pass, correo);


        if (nuevoMod.getEdad() < 18) {
            return false;
        }

        if (existeModerador()) {
            return false;
        }


        if (usuarioDAO.registrarUsuario(nuevoMod, 1, 0, "")) {
            listaUsuarios.add(nuevoMod);
            return true;
        }

        return false;
    }

    public boolean registrarVendedor(String nombre, String id, LocalDate fechaNac, String pass, String correo, int puntos, String direccion) {
        Vendedor nuevoVend = new Vendedor(nombre, id, fechaNac, pass, correo, puntos, direccion);


        if (nuevoVend.getEdad() < 18) {
            return false;
        }


        if (usuarioDAO.registrarUsuario(nuevoVend, 2, puntos, direccion)) {
            listaUsuarios.add(nuevoVend);
            return true;
        }

        return false;
    }

    public boolean registrarColeccionista(String nombre, String id, LocalDate fechaNac, String pass, String correo, int puntos, String direccion) {
        Coleccionista nuevoCol = new Coleccionista(nombre, id, fechaNac, pass, correo, puntos, direccion);


        if (nuevoCol.getEdad() < 18) {
            return false;
        }


        if (usuarioDAO.registrarUsuario(nuevoCol, 3, puntos, direccion)) {
            listaUsuarios.add(nuevoCol);
            return true;
        }

        return false;
    }

    // ==========================================
    // MÉTODOS DE BÚSQUEDA Y LISTADO
    // ==========================================

    public ArrayList<Usuario> listarUsuarios() {
        return listaUsuarios;
    }

    public Usuario buscarUsuarioPorId(String identificacion) {
        for (Usuario u : listaUsuarios) {
            if (u.getIdentificacion().equals(identificacion)) {
                return u;
            }
        }
        return null;
    }

    public boolean actualizarDatosUsuario(String identificacion, String nuevoCorreo, String nuevaContrasena) {
        Usuario u = buscarUsuarioPorId(identificacion);
        if (u != null) {

            u.setCorreoElectronico(nuevoCorreo);
            u.setContrasena(nuevaContrasena);

            return usuarioDAO.actualizarUsuario(u);
        }
        return false;
    }

    public boolean cambiarEstadoUsuario(String identificacion, boolean nuevoEstado) {

        if (usuarioDAO.cambiarEstadoUsuario(identificacion, nuevoEstado)) {

            Usuario u = buscarUsuarioPorId(identificacion);
            if (u != null) {
                u.setEstadoActivo(nuevoEstado);
            }
            return true;
        }
        return false;
    }

    // ==========================================
    // MÉTODOS PARA SUBASTAS Y OFERTAS
    // ==========================================

    /**
     * Registra una nueva subasta en el sistema y en la base de datos SQL Server.
     *
     * @param fechaVencimiento La fecha y hora en que la subasta se cerrará.
     * @param creador El usuario que crea la subasta.
     * @param puntuacionCreador La reputación actual del usuario creador.
     * @param precioMinimo El valor base mínimo para empezar a aceptar ofertas.
     * @param estado El estado inicial de la subasta (ej. "Activa").
     * @param objetos Lista de objetos que componen el lote de la subasta.
     * @return true si la subasta se registró exitosamente, false en caso de error o datos inválidos.
     */

    public boolean registrarSubasta(LocalDateTime fechaVencimiento, Usuario creador,
                                    int puntuacionCreador, double precioMinimo, String estado,
                                    ArrayList<ObjetoOfrecido> objetos) {

        if (objetos == null || objetos.isEmpty()) {
            return false;
        }

        Subasta nuevaSubasta = new Subasta(fechaVencimiento, creador, puntuacionCreador, precioMinimo, estado);
        nuevaSubasta.setObjetosSubastados(objetos);

        if (subastaDAO.registrarSubastaConObjetos(nuevaSubasta, creador.getIdentificacion())) {
            listaSubastas.add(nuevaSubasta);
            return true;
        }

        return false;
    }
    /**
     * Finaliza una subasta activa, evalúa las ofertas recibidas y determina la puja ganadora
     * para generar la orden de adjudicación final.
     *
     * @param subastaCerrada La subasta que ha llegado a su fecha de vencimiento.
     * @param ofertasDeEstaSubasta La lista de todas las pujas realizadas para esta subasta.
     * @return Un objeto OrdenAdjudicacion con los datos del ganador, o null si no hubo ofertas válidas.
     */

    public OrdenAdjudicacion cerrarSubastaYGenerarOrden(Subasta subastaCerrada, ArrayList<Oferta> ofertasDeEstaSubasta) {

        subastaCerrada.setEstado("Cerrada");

        Oferta ganadora = subastaCerrada.obtenerOfertaGanadora(ofertasDeEstaSubasta);

        if (ganadora != null) {


            OrdenAdjudicacion nuevaOrden = new OrdenAdjudicacion(
                    ganadora.getOferente().getNombreCompleto(),
                    subastaCerrada.getObjetosSubastados(),
                    ganadora.getPrecioOfertado()
            );
            return nuevaOrden;
        }

        return null;
    }

    public boolean ascenderColeccionistaAModerador(String identificacion) {
        if (usuarioDAO.ascenderAModerador(identificacion)) {

            return true;
        }
        return false;
    }

    public boolean registrarCategoria(String nombreCategoria) {

        return objetoDAO.registrarCategoria(nombreCategoria);
    }

    public ArrayList<Subasta> listarSubastas() {
        return subastaDAO.listarSubastasActivasBD();
    }

    public boolean registrarObjeto(String nombre, String descripcion, String estado, LocalDate fechaCompra, String identificacionDueno) {
        ObjetoOfrecido nuevoObjeto = new ObjetoOfrecido(nombre, descripcion, estado, fechaCompra);

        if (objetoDAO.registrarObjeto(nuevoObjeto, identificacionDueno)) {

            listaObjetos.add(nuevoObjeto);
            return true;
        }
        return false;
    }

    public boolean registrarOferta(Coleccionista col, double monto, Subasta sub) {

        Oferta nuevaOferta = new Oferta(col, monto, sub);


        if (ofertaDAO.guardarOfertaBD(nuevaOferta, sub)) {

            this.listaOfertas.add(nuevaOferta);
            return true;
        }
        return false;
    }

    public ArrayList<Oferta> listarOfertas() {
        return listaOfertas;
    }

    // ==========================================
    // LISTADO DE "MIS SUBASTAS"
    // ==========================================
    public ArrayList<Subasta> obtenerMisSubastas(String identificacionUsuario) {

        return subastaDAO.listarMisSubastasBD(identificacionUsuario);
    }

    public ArrayList<Oferta> obtenerOfertasPorSubasta(Subasta subasta) {

        return ofertaDAO.obtenerOfertasDeSubastaBD(subasta);
    }
}