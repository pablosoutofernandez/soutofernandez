package com.clase;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.clase.modelo.Doctor;
import com.clase.persistencia.DoctorDAOMySQL;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public class DoctorController implements Initializable {
    // Los nombres tienen que coincidir EXACTAMENTE con los fx:id de doctores.fxml
    @FXML
    private TextField nomedoc, apeldoc, movildoc, maildoc;
    @FXML
    private RadioButton rbcolsi, rbcolno;
    @FXML
    private ToggleGroup grupocoledoc;
    @FXML
    private ComboBox<String> cmbespedoc;
    @FXML
    private TableView<Doctor> tablaDoctores;
    @FXML
    private TableColumn<Doctor, String> colnomedoc, colapeldoc, colcoledoc, colmovildoc, colmaildoc, colespedoc;

    // true cuando el formulario tiene cargado un doctor que ya está en la base de datos
    private boolean doctorexiste = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cmbespedoc.getItems().addAll("Medicina General", "Pediatría", "Cardiología", "Dermatología",
                "Traumatología", "Ginecología", "Neurología", "Oftalmología");

        colnomedoc.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNombre()));
        colapeldoc.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getApellidos()));
        colcoledoc.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().isColegiado() ? "Sí" : "No"));
        colmovildoc.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getMovil()));
        colmaildoc.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmail()));
        colespedoc.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEspecialidad()));

        cargarDoctores();
    }

    // que la primera letra de cada palabra esté en mayúscula y el resto en minúscula
    private String letrasCapitales(String texto) {
        String[] palabras = texto.trim().split("\\s+");
        StringBuilder resultado = new StringBuilder();
        for (String palabra : palabras) {
            if (palabra.length() > 0) {
                resultado.append(Character.toUpperCase(palabra.charAt(0)));
                resultado.append(palabra.substring(1).toLowerCase());
                resultado.append(" ");
            }
        }
        return resultado.toString().trim();
    }

    @FXML
    private void guardarDoctor() {
        Doctor doctor = leerFormulario();
        if (doctor == null) {
            return;
        }

        DoctorDAOMySQL dao = new DoctorDAOMySQL();
        if (doctorexiste) {
            // El id es el del doctor seleccionado en la tabla
            doctor.setId(tablaDoctores.getSelectionModel().getSelectedItem().getId());
            if (dao.modificarDoctor(doctor)) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Doctor modificado correctamente.");
                cargarDoctores();
                limpiarFormulario();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "No se pudo modificar el doctor. Mira la consola para ver el error.");
            }
        } else {
            if (dao.guardarDoctor(doctor)) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Doctor guardado correctamente.");
                cargarDoctores();
                limpiarFormulario();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "No se pudo guardar el doctor. Mira la consola para ver el error.");
            }
        }
    }

    @FXML
    private void eliminarDoctor() {
        Doctor seleccionado = tablaDoctores.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecciona un doctor de la tabla para eliminarlo.");
            return;
        }

        DoctorDAOMySQL dao = new DoctorDAOMySQL();
        if (dao.eliminarDoctor(seleccionado.getId())) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Doctor eliminado correctamente.");
            cargarDoctores();
            limpiarFormulario();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo eliminar el doctor. Mira la consola para ver el error.");
        }
    }

    // Devuelve el doctor con los datos del formulario, o null si falta algún campo
    private Doctor leerFormulario() {
        if (nomedoc.getText().trim().isEmpty()
                || apeldoc.getText().trim().isEmpty()
                || movildoc.getText().trim().isEmpty()
                || maildoc.getText().trim().isEmpty()
                || cmbespedoc.getValue() == null
                || grupocoledoc.getSelectedToggle() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Rellena todos los campos.");
            return null;
        }

        return new Doctor(
            letrasCapitales(nomedoc.getText()),
            letrasCapitales(apeldoc.getText()),
            rbcolsi.isSelected(),
            movildoc.getText().trim(),
            maildoc.getText().trim(),
            cmbespedoc.getValue()
        );
    }

    // Rellena el formulario con el doctor seleccionado en la tabla
    @FXML
    private void cargarDoctor() {
        Doctor doctor = tablaDoctores.getSelectionModel().getSelectedItem();
        if (doctor == null) {
            return;
        }

        nomedoc.setText(doctor.getNombre());
        apeldoc.setText(doctor.getApellidos());
        if (doctor.isColegiado()) {
            rbcolsi.setSelected(true);
        } else {
            rbcolno.setSelected(true);
        }
        movildoc.setText(doctor.getMovil());
        maildoc.setText(doctor.getEmail());
        cmbespedoc.setValue(doctor.getEspecialidad());

        doctorexiste = true;
    }

    @FXML
    private void limpiarFormulario() {
        tablaDoctores.getSelectionModel().clearSelection();
        nomedoc.clear();
        apeldoc.clear();
        grupocoledoc.selectToggle(null);
        movildoc.clear();
        maildoc.clear();
        cmbespedoc.setValue(null);

        doctorexiste = false;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Xestión Doctores");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void cargarDoctores() {
        DoctorDAOMySQL dao = new DoctorDAOMySQL();
        List<Doctor> doctores = dao.cargarDoctores();
        tablaDoctores.getItems().setAll(doctores);
    }
}
