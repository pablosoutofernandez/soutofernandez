module com.clase {
    requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    requires jasperreports;
    requires com.google.gson;
    requires java.desktop;

    opens img;
    opens informes;
    opens com.clase to javafx.fxml;
    exports com.clase;
    exports com.clase.modelo;
    opens com.clase.modelo to com.google.gson;
}
