package com.clase;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AcercadeController {

    @FXML
    private Button btnCerrarAcercade;

    @FXML
    private void cerrarAcercade() {
        ((Stage) btnCerrarAcercade.getScene().getWindow()).close();
    }
}
