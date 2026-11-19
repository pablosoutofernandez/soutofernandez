package com.clase;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class VentanaController {

    // Controladores de las pestañas incluidas con <fx:include fx:id="...">.
    // El nombre es el fx:id + "Controller"
    @FXML
    private PacienteController pacientesController;
    @FXML
    private DoctorController doctoresController;
    @FXML
    private CitasController citasController;

    @FXML
    private void initialize() {
        // Cuando cambia el paciente seleccionado, la pestaña Citas muestra sus citas
        pacientesController.pacienteSeleccionadoProperty().addListener(
                (obs, anterior, nuevo) -> citasController.cargarDatosPaciente(nuevo));
        // Cuando cambia el doctor seleccionado, se pone su id en la pestaña Citas
        doctoresController.doctorSeleccionadoProperty().addListener(
                (obs, anterior, nuevo) -> citasController.cargarDatosDoctor(nuevo));
    }

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
