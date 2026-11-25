package com.clase;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AcercaDeController {

    @FXML
    private Button btncerrar;

    @FXML
    private void cerrar() {
        ((Stage) btncerrar.getScene().getWindow()).close();
    }
}
