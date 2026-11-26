package com.clase;

import java.io.IOException;

import com.clase.informes.imprimirPacientes;

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
    private PacientesController panelPacientesController;
    @FXML
    private DoctoresController panelDoctoresController;
    @FXML
    private CitasController panelCitasController;

    @FXML
    public void initialize() {
        // para acceder desde pacientes y doctores al panel de citas
        panelPacientesController.setCitasController(panelCitasController);
        panelDoctoresController.setCitasController(panelCitasController);
    }

    @FXML
    private void salirApp() {
        Platform.exit();
    }

    @FXML
    private void mostrarAcercade() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/clase/acercade.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Acerca de");
        stage.setScene(new Scene(loader.load()));
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    // Abre el explorador de archivos de Windows
    @FXML
    private void abrirDir() {
        try {
            new ProcessBuilder("explorer.exe").start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Listado de pacientes (informes/imprimirPacientes.java + pacientes.jrxml)
    @FXML
    private void informePacientes() {
        imprimirPacientes.mostrar();
    }
}
