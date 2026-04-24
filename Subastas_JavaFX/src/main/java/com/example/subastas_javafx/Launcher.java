package com.example.subastas_javafx;

import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) {
        // Esto arranca tu ventana sin que Java tire el error de componentes
        Application.launch(AppSubastasGUI.class, args);
    }
}