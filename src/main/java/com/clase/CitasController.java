package com.clase;

import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

import com.clase.modelo.Cita;
import com.clase.modelo.Doctor;
import com.clase.modelo.Paciente;
import com.clase.persistencia.CitaDAOMySQL;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class CitasController implements Initializable {
    // Los nombres tienen que coincidir EXACTAMENTE con los fx:id de citas.fxml

    // Campos id: no editables
    @FXML
    private TextField idcita, citadnipac, citaiddoc;
    @FXML
    private TextField motivo, horacita;
    @FXML
    private DatePicker fechacita;
    @FXML
    private CheckBox chkatendida, chkpendiente, chkanulada;
    @FXML
    private TextArea diagnostico, tratamiento;
    @FXML
    private TableView<Cita> tablaCitas;
    @FXML
    private TableColumn<Cita, String> colidcita, colcitaiddoc, colfechacita, colhoracita, colmotivo, colestadocita;

    // Recibe el paciente seleccionado en la pestaña Pacientes (null si no hay ninguno)
    public void cargarDatosPaciente(Paciente paciente) {
        limpiarFormulario();
        if (paciente == null) {
            citadnipac.clear();
        } else {
            citadnipac.setText(paciente.getDni());
        }
        cargarCitas();
    }

    // Recibe el doctor seleccionado en la pestaña Doctores (null si no hay ninguno)
    public void cargarDatosDoctor(Doctor doctor) {
        if (doctor == null) {
            citaiddoc.clear();
        } else {
            citaiddoc.setText(String.valueOf(doctor.getId()));
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colidcita.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getIdcita())));
        colcitaiddoc.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getCitaiddoc())));
        colfechacita.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFechacita().toString()));
        colhoracita.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getHoracita().toString()));
        colmotivo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMotivo()));
        colestadocita.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstadocita()));

        chkpendiente.setSelected(true);
    }

    @FXML
    private void gestionarExclusion(ActionEvent event) {
        CheckBox seleccionado = (CheckBox) event.getSource();

        if (seleccionado == chkpendiente && chkpendiente.isSelected()) {
            chkatendida.setSelected(false);
            chkanulada.setSelected(false);
        } else if (seleccionado == chkatendida && chkatendida.isSelected()) {
            chkpendiente.setSelected(false);
            chkanulada.setSelected(false);
        } else if (seleccionado == chkanulada && chkanulada.isSelected()) {
            chkpendiente.setSelected(false);
            chkatendida.setSelected(false);
        }
    }

    private String leerEstado() {
        if (chkpendiente.isSelected()) {
            return "Pendiente";
        } else if (chkatendida.isSelected()) {
            return "Atendida";
        } else if (chkanulada.isSelected()) {
            return "Anulada";
        }
        return null;
    }

    private void marcarEstado(String estado) {
        chkpendiente.setSelected("Pendiente".equals(estado));
        chkatendida.setSelected("Atendida".equals(estado));
        chkanulada.setSelected("Anulada".equals(estado));
    }

    @FXML
    private void guardarCita() {
        Cita cita = leerFormulario();
        if (cita == null) {
            return;
        }

        CitaDAOMySQL dao = new CitaDAOMySQL();
        if (!idcita.getText().isEmpty()) {
            // La cita ya existe: se modifica
            cita.setIdcita(Integer.parseInt(idcita.getText()));
            if (dao.modificarCita(cita)) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Cita modificada correctamente.");
                cargarCitas();
                limpiarFormulario();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "No se pudo modificar la cita. Mira la consola para ver el error.");
            }
        } else {
            if (dao.guardarCita(cita)) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Cita guardada correctamente.");
                cargarCitas();
                limpiarFormulario();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "No se pudo guardar la cita. Mira la consola para ver el error.");
            }
        }
    }

    @FXML
    private void eliminarCita() {
        Cita seleccionada = tablaCitas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecciona una cita de la tabla para eliminarla.");
            return;
        }

        CitaDAOMySQL dao = new CitaDAOMySQL();
        if (dao.eliminarCita(seleccionada.getIdcita())) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Cita eliminada correctamente.");
            cargarCitas();
            limpiarFormulario();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo eliminar la cita. Mira la consola para ver el error.");
        }
    }

    // Devuelve la cita con los datos del formulario, o null si falta algo
    private Cita leerFormulario() {
        if (citadnipac.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecciona un paciente en la pestaña Pacientes.");
            return null;
        }
        if (citaiddoc.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecciona un doctor en la pestaña Doctores.");
            return null;
        }
        if (fechacita.getValue() == null || horacita.getText().trim().isEmpty() || leerEstado() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Rellena la fecha, la hora y el estado de la cita.");
            return null;
        }

        LocalTime hora;
        try {
            hora = LocalTime.parse(horacita.getText().trim());
        } catch (DateTimeParseException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "La hora debe tener el formato HH:mm (por ejemplo 09:30).");
            return null;
        }

        return new Cita(
            0,
            citadnipac.getText(),
            Integer.parseInt(citaiddoc.getText()),
            fechacita.getValue(),
            hora,
            motivo.getText().trim(),
            diagnostico.getText(),
            tratamiento.getText(),
            leerEstado()
        );
    }

    // Rellena el formulario con la cita seleccionada en la tabla
    @FXML
    private void cargarCita() {
        Cita cita = tablaCitas.getSelectionModel().getSelectedItem();
        if (cita == null) {
            return;
        }

        idcita.setText(String.valueOf(cita.getIdcita()));
        citaiddoc.setText(String.valueOf(cita.getCitaiddoc()));
        fechacita.setValue(cita.getFechacita());
        horacita.setText(cita.getHoracita().toString());
        motivo.setText(cita.getMotivo());
        marcarEstado(cita.getEstadocita());
        diagnostico.setText(cita.getDiagnostico());
        tratamiento.setText(cita.getTratamiento());
    }

    // Limpia los datos de la cita (el paciente y el doctor se mantienen)
    @FXML
    private void limpiarFormulario() {
        tablaCitas.getSelectionModel().clearSelection();
        idcita.clear();
        fechacita.setValue(null);
        horacita.clear();
        motivo.clear();
        marcarEstado("Pendiente");
        diagnostico.clear();
        tratamiento.clear();
    }

    private void cargarCitas() {
        if (citadnipac.getText().isEmpty()) {
            tablaCitas.getItems().clear();
            return;
        }
        CitaDAOMySQL dao = new CitaDAOMySQL();
        tablaCitas.getItems().setAll(dao.cargarCitasPaciente(citadnipac.getText()));
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Xestión Citas");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
