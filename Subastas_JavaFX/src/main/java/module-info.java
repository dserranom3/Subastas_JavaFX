module com.example.subastas_javafx {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.subastas_javafx to javafx.fxml;
    exports com.example.subastas_javafx;
}