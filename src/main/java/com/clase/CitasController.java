package com.clase;

import java.net.URL;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class CitasController implements Initializable {
    // Los nombres tienen que coincidir EXACTAMENTE con los fx:id de citas.fxml

    // idcita, citadnipac y citaiddoc no son editables
    @FXML
    private TextField citadnipac, citaiddoc, idcita, motivo;
    @FXML
    private CheckBox chkatendida, chkpendiente, chkanulada;
    @FXML
    private Label lblCita;
    @FXML
    private TableView<Cita> tablaCitas;
    @FXML
    private TableColumn<Cita, String> idcitacol, apeldoccol, espedoccol, fechacitacol, horacitacol, estadocitacol;
    @FXML
    private ComboBox<String> horacita;
    @FXML
    private DatePicker fechacita;
    @FXML
    private TextArea diagnostico, tratamiento;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        idcitacol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getIdcita())));
        apeldoccol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getApeldoc()));
        espedoccol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEspedoc()));
        fechacitacol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFechacita().toString()));
        horacitacol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getHoracita()));
        estadocitacol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstadocita()));

        cargarHorario();
        chkpendiente.setSelected(true);
    }

    // Recibe el paciente seleccionado en el panel de pacientes
    public void cargarDatosPaciente(Paciente paciente) {
        citadnipac.setText(paciente.getDni());
        lblCita.setText("Citas de " + paciente.getNombre() + " " + paciente.getApellidos());
        limpiaForm();
        cargarCitas();
    }

    // Recibe el doctor seleccionado en el panel de doctores
    public void cargarDatosDoctor(Doctor doctor) {
        citaiddoc.setText(String.valueOf(doctor.getID()));
    }

    // Horas de consulta cada 30 minutos, de 09:00 a 14:00 y de 16:00 a 19:30
    private void cargarHorario() {
        horacita.getItems().clear();
        for (int h = 9; h < 20; h++) {
            if (h >= 14 && h < 16) {
                continue;
            }
            horacita.getItems().add(String.format("%02d:00", h));
            horacita.getItems().add(String.format("%02d:30", h));
        }
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

    // Limpia los datos de la cita (el paciente y el doctor se mantienen)
    @FXML
    private void limpiaForm() {
        tablaCitas.getSelectionModel().clearSelection();
        idcita.clear();
        fechacita.setValue(null);
        horacita.setValue(null);
        motivo.clear();
        marcarEstado("Pendiente");
        diagnostico.clear();
        tratamiento.clear();
    }

    @FXML
    private void guardarCita() {
        if (citadnipac.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecciona un paciente en la pestaña Pacientes.");
            return;
        }
        if (citaiddoc.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecciona un doctor en la pestaña Doctores.");
            return;
        }
        if (fechacita.getValue() == null || horacita.getValue() == null || leerEstado() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Rellena la fecha, la hora y el estado de la cita.");
            return;
        }

        Cita cita = new Cita(
            citadnipac.getText(),
            Integer.parseInt(citaiddoc.getText()),
            fechacita.getValue(),
            horacita.getValue(),
            motivo.getText().trim(),
            diagnostico.getText(),
            tratamiento.getText(),
            leerEstado()
        );

        CitaDAOMySQL dao = new CitaDAOMySQL();
        if (!idcita.getText().isEmpty()) {
            // La cita ya existe: se modifica
            if (dao.modificarCita(Integer.parseInt(idcita.getText()), cita)) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Cita modificada correctamente.");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "No se pudo modificar la cita. Mira la consola para ver el error.");
                return;
            }
        } else {
            if (dao.guardarCita(cita)) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Cita guardada correctamente.");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "No se pudo guardar la cita. Mira la consola para ver el error.");
                return;
            }
        }
        cargarCitas();
        limpiaForm();
    }

    // Solo las citas del paciente seleccionado
    private void cargarCitas() {
        if (citadnipac.getText().isEmpty()) {
            tablaCitas.getItems().clear();
            return;
        }
        CitaDAOMySQL dao = new CitaDAOMySQL();
        tablaCitas.getItems().setAll(dao.cargarCitaspaciente(citadnipac.getText()));
    }

    // Rellena el formulario con la cita seleccionada en la tabla
    @FXML
    private void cargarCita() {
        Cita citaSeleccionada = tablaCitas.getSelectionModel().getSelectedItem();
        if (citaSeleccionada == null) {
            return;
        }

        CitaDAOMySQL dao = new CitaDAOMySQL();
        Cita cita = dao.buscaCitaporId(citaSeleccionada.getIdcita());
        if (cita == null) {
            return;
        }

        idcita.setText(String.valueOf(cita.getIdcita()));
        citaiddoc.setText(String.valueOf(cita.getCitaiddoc()));
        fechacita.setValue(cita.getFechacita());
        horacita.setValue(cita.getHoracita());
        motivo.setText(cita.getMotivo());
        marcarEstado(cita.getEstadocita());
        diagnostico.setText(cita.getDiagnostico());
        tratamiento.setText(cita.getTratamiento());
    }

    @FXML
    public void eliminaCita() {
        Cita citaSeleccionada = tablaCitas.getSelectionModel().getSelectedItem();
        if (citaSeleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecciona una cita de la tabla para eliminarla.");
            return;
        }

        CitaDAOMySQL dao = new CitaDAOMySQL();
        if (dao.eliminaCita(citaSeleccionada.getIdcita())) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Cita eliminada correctamente.");
            cargarCitas();
            limpiaForm();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo eliminar la cita. Mira la consola para ver el error.");
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Xestión Citas");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
