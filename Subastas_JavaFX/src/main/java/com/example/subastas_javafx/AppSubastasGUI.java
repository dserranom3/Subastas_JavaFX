package com.example.subastas_javafx; // Cambia esto si tu paquete se llama diferente

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class AppSubastasGUI extends Application {

    private Stage ventanaPrincipal;
    private Scene escenaLogin, escenaRegistro;

    @Override
    public void start(Stage stage) {
        this.ventanaPrincipal = stage;
        ventanaPrincipal.setTitle("Plataforma de Subastas CENFOTEC");

        // 1. Construimos las dos pantallas
        crearEscenaLogin();
        crearEscenaRegistro();

        // 2. Arrancamos mostrando el Login
        ventanaPrincipal.setScene(escenaLogin);
        ventanaPrincipal.show();
    }

    private void crearEscenaLogin() {
        // Contenedor principal
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));

        // Título
        Label lblTitulo = new Label("Inicio de Sesión");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // Cuadrícula para los campos de texto
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(15);

        grid.add(new Label("Identificación:"), 0, 0);
        grid.add(new TextField(), 1, 0);

        grid.add(new Label("Contraseña:"), 0, 1);
        grid.add(new PasswordField(), 1, 1);

        // Botones
        Button btnEntrar = new Button("Ingresar al Sistema");
        Button btnIrRegistro = new Button("¿No tienes cuenta? Regístrate aquí");

        // El botón de registro solo cambia la escena (no hay lógica por detrás)
        btnIrRegistro.setOnAction(e -> ventanaPrincipal.setScene(escenaRegistro));

        layout.getChildren().addAll(lblTitulo, grid, btnEntrar, btnIrRegistro);
        escenaLogin = new Scene(layout, 400, 350);
    }

    private void crearEscenaRegistro() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));

        Label lblTitulo = new Label("Registro de Nuevo Usuario");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(15);

        grid.add(new Label("Nombre Completo:"), 0, 0);
        grid.add(new TextField(), 1, 0);

        grid.add(new Label("Identificación:"), 0, 1);
        grid.add(new TextField(), 1, 1);

        grid.add(new Label("Fecha de Nacimiento:"), 0, 2);
        grid.add(new DatePicker(), 1, 2);

        grid.add(new Label("Correo Electrónico:"), 0, 3);
        grid.add(new TextField(), 1, 3);

        grid.add(new Label("Contraseña:"), 0, 4);
        grid.add(new PasswordField(), 1, 4);

        grid.add(new Label("Tipo de Usuario:"), 0, 5);
        ComboBox<String> cbTipo = new ComboBox<>();
        cbTipo.getItems().addAll("Vendedor", "Coleccionista");
        grid.add(cbTipo, 1, 5);

        Button btnRegistrar = new Button("Crear Cuenta");
        Button btnVolverLogin = new Button("Volver al Inicio de Sesión");

        // El botón de volver solo cambia la escena
        btnVolverLogin.setOnAction(e -> ventanaPrincipal.setScene(escenaLogin));

        layout.getChildren().addAll(lblTitulo, grid, btnRegistrar, btnVolverLogin);
        escenaRegistro = new Scene(layout, 450, 500);
    }

    public static void main(String[] args) {
        launch(args);
    }
}