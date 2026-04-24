package com.example.subastas_javafx;

import java.util.ArrayList;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.LocalDate;

public class AppSubastasGUI extends Application {

    private Stage ventanaPrincipal;
    private Scene escenaLogin, escenaRegistro;

    private Controlador admin;
    private Usuario usuarioLogueadoActual; // Variable para recordar quién inició sesión

    @Override
    public void start(Stage stage) {
        this.ventanaPrincipal = stage;
        this.admin = new Controlador();

        ventanaPrincipal.setTitle("Plataforma de Subastas CENFOTEC");

        crearEscenaLogin();
        crearEscenaRegistro();

        // LÓGICA DE ARRANQUE:
        if (!admin.existeModerador()) {
            ventanaPrincipal.setScene(crearEscenaRegistroModerador());
        } else {
            ventanaPrincipal.setScene(escenaLogin);
        }

        ventanaPrincipal.show();
    }

    // ==========================================
    // 1. PANTALLA DE LOGIN
    // ==========================================
    private void crearEscenaLogin() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));

        Label lblTitulo = new Label("Inicio de Sesión");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10); grid.setVgap(15);

        TextField txtId = new TextField();
        PasswordField txtPass = new PasswordField();

        grid.add(new Label("Identificación:"), 0, 0);
        grid.add(txtId, 1, 0);
        grid.add(new Label("Contraseña:"), 0, 1);
        grid.add(txtPass, 1, 1);

        Button btnEntrar = new Button("Ingresar al Sistema");
        Button btnIrRegistro = new Button("¿No tienes cuenta? Regístrate aquí");

        btnIrRegistro.setOnAction(e -> ventanaPrincipal.setScene(escenaRegistro));

        btnEntrar.setOnAction(e -> {
            String id = txtId.getText();
            String pass = txtPass.getText();

            Usuario encontrado = admin.buscarUsuarioPorId(id);

            if (encontrado != null && encontrado.getContrasena().equals(pass)) {

                txtId.clear();
                txtPass.clear();
                usuarioLogueadoActual = encontrado;

                crearEscenaMenuPrincipal(encontrado);
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Acceso", "Credenciales incorrectas o usuario no existe.");
            }
        });

        layout.getChildren().addAll(lblTitulo, grid, btnEntrar, btnIrRegistro);
        escenaLogin = new Scene(layout, 400, 350);
    }

    // ==========================================
    // 2. PANTALLA DE REGISTRO
    // ==========================================
    private void crearEscenaRegistro() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));

        Label lblTitulo = new Label("Registro de Nuevo Usuario");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10); grid.setVgap(15);

        TextField txtNombre = new TextField();
        TextField txtId = new TextField();
        DatePicker dpFecha = new DatePicker();
        TextField txtCorreo = new TextField();
        PasswordField txtPass = new PasswordField();
        TextField txtPuntos = new TextField();
        TextField txtDireccion = new TextField();
        ComboBox<String> cbTipo = new ComboBox<>();
        cbTipo.getItems().addAll("Vendedor", "Coleccionista");
        cbTipo.setValue("Vendedor");

        grid.add(new Label("Nombre Completo:"), 0, 0); grid.add(txtNombre, 1, 0);
        grid.add(new Label("Identificación:"), 0, 1); grid.add(txtId, 1, 1);
        grid.add(new Label("Fecha Nacimiento:"), 0, 2); grid.add(dpFecha, 1, 2);
        grid.add(new Label("Correo Electrónico:"), 0, 3); grid.add(txtCorreo, 1, 3);
        grid.add(new Label("Contraseña:"), 0, 4); grid.add(txtPass, 1, 4);
        grid.add(new Label("Puntuación Inicial:"), 0, 5); grid.add(txtPuntos, 1, 5);
        grid.add(new Label("Dirección Física:"), 0, 6); grid.add(txtDireccion, 1, 6);
        grid.add(new Label("Tipo de Usuario:"), 0, 7); grid.add(cbTipo, 1, 7);

        Button btnRegistrar = new Button("Crear Cuenta");
        Button btnVolverLogin = new Button("Volver al Inicio de Sesión");

        btnVolverLogin.setOnAction(e -> ventanaPrincipal.setScene(escenaLogin));

        btnRegistrar.setOnAction(e -> {
            try {
                String nombre = txtNombre.getText();
                String id = txtId.getText();
                String correo = txtCorreo.getText();
                String pass = txtPass.getText();
                String direccion = txtDireccion.getText();
                String tipo = cbTipo.getValue();

                if (nombre.isEmpty() || id.isEmpty() || pass.isEmpty()) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Campos Incompletos", "Por favor, llene los campos principales.");
                    return;
                }

                LocalDate fechaNac = dpFecha.getValue();
                if (fechaNac == null) throw new IllegalArgumentException("Debe seleccionar una fecha de nacimiento.");

                int puntos = Integer.parseInt(txtPuntos.getText());

                boolean exito = false;
                if (tipo.equals("Vendedor")) {
                    exito = admin.registrarVendedor(nombre, id, fechaNac, pass, correo, puntos, direccion);
                } else if (tipo.equals("Coleccionista")) {
                    exito = admin.registrarColeccionista(nombre, id, fechaNac, pass, correo, puntos, direccion);
                }

                if (exito) {
                    mostrarAlerta(Alert.AlertType.INFORMATION, "¡Registro Exitoso!", "El usuario fue creado correctamente.");
                    ventanaPrincipal.setScene(escenaLogin);

                    txtNombre.clear(); txtId.clear(); txtCorreo.clear();
                    txtPass.clear(); txtPuntos.clear(); txtDireccion.clear(); dpFecha.setValue(null);
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error de Registro", "El usuario debe ser mayor de 18 años o hubo un error en BD.");
                }

            } catch (NumberFormatException ex) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Formato", "La puntuación debe ser un número entero válido.");
            } catch (IllegalArgumentException ex) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error en Fecha", ex.getMessage());
            } catch (Exception ex) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error Inesperado", "Ocurrió un error al procesar el registro.");
            }
        });

        layout.getChildren().addAll(lblTitulo, grid, btnRegistrar, btnVolverLogin);
        escenaRegistro = new Scene(layout, 450, 600);
    }

    // ====================
    // 3. MENÚ PRINCIPAL
    // ====================
    private void crearEscenaMenuPrincipal(Usuario usuarioLogueado) {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));

        Label lblBienvenida = new Label("Bienvenido(a), " + usuarioLogueado.getNombreCompleto());
        lblBienvenida.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        String rol = usuarioLogueado.getClass().getSimpleName();
        Label lblRol = new Label("Perfil: " + rol);
        lblRol.setStyle("-fx-text-fill: gray; -fx-font-size: 14px;");

        Button btnListarUsuarios = new Button("👥 Directorio de Usuarios");
        Button btnSubastas = new Button("🔨 Gestión de Subastas");
        Button btnOfertas = new Button("💰 Panel de Ofertas");
        Button btnCerrarSesion = new Button("🚪 Cerrar Sesión");

        btnListarUsuarios.setPrefWidth(200);
        btnSubastas.setPrefWidth(200);
        btnOfertas.setPrefWidth(200);
        btnCerrarSesion.setPrefWidth(200);
        btnCerrarSesion.setStyle("-fx-base: #ff6666;");

        btnListarUsuarios.setOnAction(e -> mostrarDirectorioUsuarios());

        Button btnMisSubastas = new Button("📂 Mis Subastas");
        btnMisSubastas.setStyle("-fx-base: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
        btnMisSubastas.setMinWidth(200);
        btnMisSubastas.setOnAction(e -> mostrarMisSubastas());

        btnSubastas.setOnAction(e -> mostrarGestionSubastas());

        btnOfertas.setOnAction(e -> mostrarPanelOfertas());

        btnCerrarSesion.setOnAction(e -> {
            usuarioLogueadoActual = null;
            ventanaPrincipal.setScene(escenaLogin);
            ventanaPrincipal.setTitle("Plataforma de Subastas CENFOTEC");
        });

        Button btnCategorias = new Button("🏷️ Nueva Categoría");
        btnCategorias.setMinWidth(200);
        btnCategorias.setOnAction(e -> mostrarDialogoCategoria());

        Button btnAscenderMod = new Button("⭐ Ascender Moderador");
        btnAscenderMod.setMinWidth(200);
        btnAscenderMod.setOnAction(e -> mostrarDialogoAscenderModerador());

        layout.getChildren().addAll(
                lblBienvenida,
                lblRol,
                new Separator(),
                btnListarUsuarios,
                btnSubastas,
                btnOfertas,
                btnCategorias,
                btnAscenderMod,
                btnMisSubastas,
                new Separator(),
                btnCerrarSesion
        );
        Scene escenaMenuPrincipal = new Scene(layout, 450, 450);
        ventanaPrincipal.setScene(escenaMenuPrincipal);
        ventanaPrincipal.setTitle("Menú Principal - " + rol);
    }

    // ==========================================
    // 4. DIRECTORIO DE USUARIOS (NUEVO)
    // ==========================================
    private void mostrarDirectorioUsuarios() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        Label lblTitulo = new Label("Directorio General de Usuarios");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        TableView<Usuario> tabla = new TableView<>();

        TableColumn<Usuario, String> colNombre = new TableColumn<>("Nombre Completo");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));

        TableColumn<Usuario, String> colId = new TableColumn<>("Identificación");
        colId.setCellValueFactory(new PropertyValueFactory<>("identificacion"));

        TableColumn<Usuario, String> colCorreo = new TableColumn<>("Correo Electrónico");
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correoElectronico"));

        TableColumn<Usuario, Boolean> colEstado = new TableColumn<>("¿Activo?");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estadoActivo"));

        tabla.getColumns().addAll(colNombre, colId, colCorreo, colEstado);

        // Cargamos la lista fresca desde el Controlador
        tabla.getItems().addAll(admin.listarUsuarios());

        HBox botones = new HBox(15);
        botones.setAlignment(Pos.CENTER);

        Button btnInactivar = new Button("Cambiar Estado (Activar/Inactivar)");
        Button btnVolver = new Button("Volver al Menú");

        btnInactivar.setOnAction(e -> {
            Usuario seleccionado = tabla.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                boolean nuevoEstado = !seleccionado.isEstadoActivo();
                if (admin.cambiarEstadoUsuario(seleccionado.getIdentificacion(), nuevoEstado)) {
                    // Refrescamos la tabla para que muestre el cambio
                    tabla.getItems().clear();
                    tabla.getItems().addAll(admin.listarUsuarios());

                    mostrarAlerta(Alert.AlertType.INFORMATION, "Estado Actualizado",
                            "El usuario " + seleccionado.getNombreCompleto() + " ahora está " + (nuevoEstado ? "Activo" : "Inactivo"));
                }
            } else {
                mostrarAlerta(Alert.AlertType.WARNING, "Selección Requerida", "Por favor, seleccione un usuario de la tabla.");
            }
        });

        // Usamos la variable guardada para regresar sin perder sesión
        btnVolver.setOnAction(e -> crearEscenaMenuPrincipal(usuarioLogueadoActual));

        botones.getChildren().addAll(btnInactivar, btnVolver);
        layout.getChildren().addAll(lblTitulo, tabla, botones);

        Scene escenaDirectorio = new Scene(layout, 600, 500);
        ventanaPrincipal.setScene(escenaDirectorio);
        ventanaPrincipal.setTitle("Directorio de Usuarios");
    }

    // ==========================================
    // 5. GESTIÓN DE SUBASTAS (NUEVO)
    // ==========================================
    private void mostrarGestionSubastas() {
        VBox layoutPrincipal = new VBox(20);
        layoutPrincipal.setPadding(new Insets(20));
        layoutPrincipal.setAlignment(Pos.TOP_CENTER);

        Label lblTitulo = new Label("Configuración de Nueva Subasta");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        // Esta lista guardará los objetos TEMPORALMENTE antes de enviarlos a la base de datos
        ArrayList<ObjetoOfrecido> objetosTemporales = new ArrayList<>();

        // --- SECCIÓN 1: REGISTRO DE OBJETOS ---
        HBox seccionContenido = new HBox(30);
        seccionContenido.setAlignment(Pos.CENTER);

        VBox formObjeto = new VBox(10);
        formObjeto.setMinWidth(250);
        formObjeto.getChildren().add(new Label("Detalles del Objeto:"));

        TextField txtNomObj = new TextField(); txtNomObj.setPromptText("Nombre del objeto");
        TextField txtDescObj = new TextField(); txtDescObj.setPromptText("Descripción");
        ComboBox<String> cbEstadoObj = new ComboBox<>();
        cbEstadoObj.getItems().addAll("Excelente", "Bueno", "Regular", "Desgastado");
        cbEstadoObj.setValue("Excelente");
        DatePicker dpCompraObj = new DatePicker();
        dpCompraObj.setPromptText("Fecha de compra");

        Button btnAgregarObjeto = new Button("➕ Agregar Objeto a la Subasta");

        formObjeto.getChildren().addAll(txtNomObj, txtDescObj, cbEstadoObj, dpCompraObj, btnAgregarObjeto);

        // --- SECCIÓN 2: VISTA PREVIA (TABLA) ---
        VBox vistaPrevia = new VBox(10);
        TableView<ObjetoOfrecido> tablaPrevia = new TableView<>();
        TableColumn<ObjetoOfrecido, String> colN = new TableColumn<>("Objeto");
        colN.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        tablaPrevia.getColumns().add(colN);
        tablaPrevia.setPrefHeight(200);

        vistaPrevia.getChildren().addAll(new Label("Objetos en esta subasta:"), tablaPrevia);
        seccionContenido.getChildren().addAll(formObjeto, vistaPrevia);

        // Lógica para ir llenando la "caja" de objetos
        btnAgregarObjeto.setOnAction(e -> {
            if (!txtNomObj.getText().isEmpty() && dpCompraObj.getValue() != null) {
                ObjetoOfrecido nuevo = new ObjetoOfrecido(txtNomObj.getText(), txtDescObj.getText(),
                        cbEstadoObj.getValue(), dpCompraObj.getValue());

                // Primero lo registramos en la BD para que tenga ID (Regla de integridad)
                if (admin.registrarObjeto(nuevo.getNombre(), nuevo.getDescripcion(),
                        nuevo.getEstado(), nuevo.getFechaCompra(),
                        usuarioLogueadoActual.getIdentificacion())) {

                    objetosTemporales.add(nuevo);
                    tablaPrevia.getItems().add(nuevo);

                    // Limpiamos campos para el siguiente objeto
                    txtNomObj.clear(); txtDescObj.clear(); dpCompraObj.setValue(null);
                }
            } else {
                mostrarAlerta(Alert.AlertType.WARNING, "Datos Faltantes", "El objeto necesita al menos nombre y fecha.");
            }
        });

        // --- SECCIÓN 3: DETALLES FINALES DE LA SUBASTA ---
        Separator sep = new Separator();
        VBox formSubasta = new VBox(15);
        formSubasta.setAlignment(Pos.CENTER);
        formSubasta.setMaxWidth(400);

        TextField txtPrecioMin = new TextField(); txtPrecioMin.setPromptText("Precio mínimo de aceptación ($)");
        DatePicker dpVence = new DatePicker(); dpVence.setPromptText("Fecha de finalización");

        Button btnFinalizarSubasta = new Button("🚀 LANZAR SUBASTA OFICIALMENTE");
        btnFinalizarSubasta.setStyle("-fx-base: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");

        btnFinalizarSubasta.setOnAction(e -> {
            try {
                double precio = Double.parseDouble(txtPrecioMin.getText());
                java.time.LocalDateTime fechaVenc = dpVence.getValue().atTime(23, 59);

                // Intentamos el registro maestro (El "Combo")
                boolean exito = admin.registrarSubasta(fechaVenc, usuarioLogueadoActual,
                        0, precio, "Activa", objetosTemporales);

                if (exito) {
                    mostrarAlerta(Alert.AlertType.INFORMATION, "¡Éxito!", "La subasta ha sido publicada con sus objetos.");
                    crearEscenaMenuPrincipal(usuarioLogueadoActual);
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", "Recuerda que la subasta debe tener objetos asociados.");
                }
            } catch (Exception ex) {
                mostrarAlerta(Alert.AlertType.ERROR, "Datos Inválidos", "Revisa el precio y la fecha.");
            }
        });

        Button btnCancelar = new Button("Cancelar y Volver");
        btnCancelar.setOnAction(e -> crearEscenaMenuPrincipal(usuarioLogueadoActual));

        formSubasta.getChildren().addAll(new Label("Configuración Final:"), txtPrecioMin, dpVence, btnFinalizarSubasta, btnCancelar);

        layoutPrincipal.getChildren().addAll(lblTitulo, seccionContenido, sep, formSubasta);

        Scene escenaSubasta = new Scene(layoutPrincipal, 700, 650);
        ventanaPrincipal.setScene(escenaSubasta);
        ventanaPrincipal.setTitle("Nueva Subasta - " + usuarioLogueadoActual.getNombreCompleto());
    }

    // ==========================================
    // METODO AUXILIAR PARA ALERTAS
    // ==========================================
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    // ==========================================
    // 6. PANEL DE OFERTAS (NUEVO)
    // ==========================================
    private void mostrarPanelOfertas() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        Label lblTitulo = new Label("Panel de Ofertas Activas");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 20));


        TableView<Subasta> tablaSubastas = new TableView<>();


        TableColumn<Subasta, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        TableColumn<Subasta, Double> colPrecioMin = new TableColumn<>("Precio Base ($)");
        colPrecioMin.setCellValueFactory(new PropertyValueFactory<>("precioMinimoAceptacion"));

        tablaSubastas.getColumns().addAll(colEstado, colPrecioMin);


        tablaSubastas.getItems().addAll(admin.listarSubastas());


        HBox areaOferta = new HBox(15);
        areaOferta.setAlignment(Pos.CENTER);

        TextField txtMonto = new TextField();
        txtMonto.setPromptText("Monto a ofertar ($)");

        Button btnOfertar = new Button("💰 Realizar Oferta");
        btnOfertar.setStyle("-fx-base: #FFC107; -fx-font-weight: bold;"); // Color dorado
        Button btnVolver = new Button("Volver al Menú");

        btnOfertar.setOnAction(e -> {
            Subasta seleccionada = tablaSubastas.getSelectionModel().getSelectedItem();

            if (seleccionada == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Atención", "Primero debes seleccionar una subasta de la tabla.");
                return;
            }

            try {
                double montoOfertado = Double.parseDouble(txtMonto.getText());


                if (montoOfertado < seleccionada.getPrecioMinimoAceptacion()) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Oferta Muy Baja",
                            "La puja debe ser igual o mayor al precio base de $" + seleccionada.getPrecioMinimoAceptacion());
                    return;
                }


                if (usuarioLogueadoActual instanceof Coleccionista) {

                    admin.registrarOferta((Coleccionista) usuarioLogueadoActual, montoOfertado, seleccionada);
                    mostrarAlerta(Alert.AlertType.INFORMATION, "¡Oferta Aceptada!", "Tu puja por $" + montoOfertado + " ha sido registrada en el sistema.");
                    txtMonto.clear();
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Acceso Denegado", "Solo los usuarios tipo Coleccionista pueden realizar ofertas.");
                }

            } catch (NumberFormatException ex) {
                mostrarAlerta(Alert.AlertType.ERROR, "Formato Incorrecto", "Por favor, ingresa solo números en la casilla de monto.");
            }
        });

        btnVolver.setOnAction(e -> crearEscenaMenuPrincipal(usuarioLogueadoActual));

        areaOferta.getChildren().addAll(txtMonto, btnOfertar, btnVolver);
        layout.getChildren().addAll(lblTitulo, tablaSubastas, areaOferta);

        Scene escenaOfertas = new Scene(layout, 600, 500);
        ventanaPrincipal.setScene(escenaOfertas);
        ventanaPrincipal.setTitle("Ofertas - " + usuarioLogueadoActual.getNombreCompleto());
    }

    private Scene crearEscenaRegistroModerador() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));

        Label lblTitulo = new Label("Configuración Inicial del Sistema");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        Label lblAviso = new Label("Se requiere registrar al Moderador único para operar.");
        lblAviso.setStyle("-fx-text-fill: red;");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10); grid.setVgap(15);

        TextField txtNombre = new TextField();
        TextField txtId = new TextField();
        DatePicker dpFecha = new DatePicker();
        TextField txtCorreo = new TextField();
        PasswordField txtPass = new PasswordField();

        grid.add(new Label("Nombre Completo:"), 0, 0); grid.add(txtNombre, 1, 0);
        grid.add(new Label("Identificación:"), 0, 1); grid.add(txtId, 1, 1);
        grid.add(new Label("Fecha Nacimiento:"), 0, 2); grid.add(dpFecha, 1, 2);
        grid.add(new Label("Correo Electrónico:"), 0, 3); grid.add(txtCorreo, 1, 3);
        grid.add(new Label("Contraseña:"), 0, 4); grid.add(txtPass, 1, 4);

        Button btnRegistrar = new Button("Crear Cuenta de Moderador");
        btnRegistrar.setStyle("-fx-base: #f44336; -fx-text-fill: white;");

        btnRegistrar.setOnAction(e -> {
            try {
                // Regla 8: El moderador debe ser mayor de edad
                int edad = java.time.Period.between(dpFecha.getValue(), java.time.LocalDate.now()).getYears();
                if (edad < 18) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", "El moderador debe ser mayor de edad.");
                    return;
                }

                if (admin.registrarModerador(txtNombre.getText(), txtId.getText(), dpFecha.getValue(), txtPass.getText(), txtCorreo.getText())) {
                    mostrarAlerta(Alert.AlertType.INFORMATION, "¡Éxito!", "Moderador creado. Inicie sesión.");
                    ventanaPrincipal.setScene(escenaLogin);
                }
            } catch (Exception ex) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Revise los campos.");
            }
        });

        layout.getChildren().addAll(lblTitulo, lblAviso, grid, btnRegistrar);
        return new Scene(layout, 450, 500);
    }

    // ==========================================
    // 8. PANEL DE MIS SUBASTAS (CORREGIDO)
    // ==========================================
    private void mostrarMisSubastas() {
        System.out.println("¡El botón funciona! Buscando subastas para la cédula: " + usuarioLogueadoActual.getIdentificacion());
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        Label lblTitulo = new Label("Mis Subastas Activas");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        TableView<Subasta> tablaMisSubastas = new TableView<>();

        // Columna 1: Estado
        TableColumn<Subasta, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Columna 2: Precio Mínimo
        TableColumn<Subasta, Double> colPrecioMin = new TableColumn<>("Precio Base ($)");
        colPrecioMin.setCellValueFactory(new PropertyValueFactory<>("precioMinimoAceptacion"));

        // Columna 3: Tiempo Restante
        TableColumn<Subasta, String> colTiempo = new TableColumn<>("Tiempo Restante");
        colTiempo.setCellValueFactory(new PropertyValueFactory<>("tiempoRestante"));
        colTiempo.setPrefWidth(250);

        tablaMisSubastas.getColumns().addAll(colEstado, colPrecioMin, colTiempo);

        // Usamos el filtro mágico del Controlador
        tablaMisSubastas.getItems().addAll(admin.obtenerMisSubastas(usuarioLogueadoActual.getIdentificacion()));

        // ==========================================
        // BOTÓN PARA CERRAR SUBASTA Y VER GANADOR
        // ==========================================
        Button btnCerrarSubasta = new Button("🏆 Cerrar Subasta y Ver Ganador");
        btnCerrarSubasta.setStyle("-fx-base: #FF9800; -fx-text-fill: white; -fx-font-weight: bold;");

        btnCerrarSubasta.setOnAction(e -> {
            Subasta subastaSeleccionada = tablaMisSubastas.getSelectionModel().getSelectedItem();

            if (subastaSeleccionada != null) {
                ArrayList<Oferta> ofertasDeLaSubasta = admin.obtenerOfertasPorSubasta(subastaSeleccionada);
                System.out.println("=== DIAGNÓSTICO DE SUBASTA ===");
                System.out.println("Subasta seleccionada: Precio " + subastaSeleccionada.getPrecioMinimoAceptacion());
                System.out.println("Cantidad de ofertas encontradas en BD: " + ofertasDeLaSubasta.size());
                OrdenAdjudicacion orden = admin.cerrarSubastaYGenerarOrden(subastaSeleccionada, ofertasDeLaSubasta);

                if (orden != null) {
                    mostrarAlerta(Alert.AlertType.INFORMATION, "¡Subasta Adjudicada!",
                            "Ganador: " + orden.getNombreGanador() + "\n" +
                                    "Total a pagar: $" + orden.getPrecioTotal());

                    tablaMisSubastas.refresh();
                } else {
                    mostrarAlerta(Alert.AlertType.WARNING, "Subasta Desierta", "Nadie hizo ofertas para esta subasta o ya estaba cerrada.");
                }
            } else {
                mostrarAlerta(Alert.AlertType.WARNING, "Selección requerida", "Por favor, selecciona una subasta de la tabla primero.");
            }
        });

        Button btnVolver = new Button("Volver al Menú");
        btnVolver.setOnAction(e -> crearEscenaMenuPrincipal(usuarioLogueadoActual));

        layout.getChildren().addAll(lblTitulo, tablaMisSubastas, btnCerrarSubasta, btnVolver);

        Scene escenaMisSubastas = new Scene(layout, 650, 500);
        ventanaPrincipal.setScene(escenaMisSubastas);
        ventanaPrincipal.setTitle("Mis Subastas - " + usuarioLogueadoActual.getNombreCompleto());
    }

    // ==========================================
    // 9. GESTIÓN RÁPIDA (CATEGORÍAS Y MODERADORES)
    // ==========================================
    private void mostrarDialogoCategoria() {
        TextInputDialog dialogo = new TextInputDialog();
        dialogo.setTitle("Nueva Categoría");
        dialogo.setHeaderText("Registro de Categorías de Objetos");
        dialogo.setContentText("Nombre de la categoría:");

        dialogo.showAndWait().ifPresent(nombre -> {
            if (admin.registrarCategoria(nombre)) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Categoría '" + nombre + "' registrada.");
            }
        });
    }

    private void mostrarDialogoAscenderModerador() {
        TextInputDialog dialogo = new TextInputDialog();
        dialogo.setTitle("Ascender a Moderador");
        dialogo.setHeaderText("Convertir Coleccionista en Moderador");
        dialogo.setContentText("Ingrese la Identificación (Cédula):");

        dialogo.showAndWait().ifPresent(id -> {
            if (admin.ascenderColeccionistaAModerador(id)) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "El usuario ahora es Moderador.");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo ascender al usuario. Verifique el ID.");
            }
        });
    }
}