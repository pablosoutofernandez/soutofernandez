package com.clase;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class VentanaController {

    @FXML
    private void salir() {
        Platform.exit();
    }

    @FXML
    private void abrirAcercaDe() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/clase/acercade.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Acerca de");
        stage.setScene(new Scene(loader.load()));
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
}
