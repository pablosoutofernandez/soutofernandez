package com.clase;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.clase.informes.Informes;
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

    // Abre el explorador de archivos de Windows (como en el proyecto del profesor)
    @FXML
    private void abrirDir() {
        try {
            new ProcessBuilder("explorer.exe").start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ===================== INFORMES =====================

    // Listado de pacientes (clase del profesor: informes/imprimirPacientes.java + pacientes.jrxml)
    @FXML
    private void informePacientes() {
        imprimirPacientes.mostrar();
    }

    // Listado de doctores agrupado por especialidad.
    // Si en la pestaña Doctores hay una especialidad elegida en el combo, solo salen esos.
    @FXML
    private void informeDoctores() {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("ESPECIALIDAD", panelDoctoresController.getEspecialidadSeleccionada());
        Informes.mostrar("doctores.jrxml", parametros);
    }

    // Informe con SUBINFORME: cada paciente con sus citas debajo
    @FXML
    private void informePacientesCitas() {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("SUBINFORME", Informes.compilar("citas_sub.jrxml"));
            Informes.mostrar("pacientes_citas.jrxml", parametros);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Informe hecho con los doctores que hay en la tabla (lista de objetos, sin SQL)
    @FXML
    private void informeTablaDoctores() {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("TITULO", "Doctores en pantalla");
        Informes.mostrarLista("tabladoctores.jrxml", parametros, panelDoctoresController.getDoctoresTabla());
    }

    // Justificante de la cita seleccionada en la pestaña Citas
    @FXML
    private void imprimirCita() {
        panelCitasController.imprimirCita();
    }

    // Historial de citas del paciente seleccionado
    @FXML
    private void informeCitasPaciente() {
        panelCitasController.informeCitasPaciente();
    }
}
