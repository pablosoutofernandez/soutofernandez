package com.clase;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import com.clase.modelo.Doctor;
import com.clase.persistencia.DoctorDAOMySQL;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public class DoctoresController implements Initializable {
    // Los nombres tienen que coincidir EXACTAMENTE con los fx:id de doctores.fxml
    @FXML
    private TextField iddoc, apeldoc, nomdoc, movildoc, emaildoc;
    @FXML
    private ComboBox<String> espedoc;
    @FXML
    private Button btnguardardoc, btndeldoc;
    @FXML
    private RadioButton coledocno, coledocsi;
    @FXML
    private ToggleGroup grupocoledoc;
    @FXML
    private TableView<Doctor> tablaDoctores;
    @FXML
    private TableColumn<Doctor, String> coliddoc, colapeldoc, colnomdoc, colmovildoc, colespedoc;

    // para pasar el doctor seleccionado al panel de citas
    private CitasController citasController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        emaildoc.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                validarEmail();
            }
        });
        movildoc.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                comprobarMovil();
            }
        });
        // al salir del campo se ponen las mayúsculas
        nomdoc.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                nomdoc.setText(letrasCapitales(nomdoc.getText()));
            }
        });
        apeldoc.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                apeldoc.setText(letrasCapitales(apeldoc.getText()));
            }
        });

        cargarEspecialidades();

        coliddoc.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getID())));
        colapeldoc.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getApellidos()));
        colnomdoc.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNombre()));
        colmovildoc.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getMovil()));
        colespedoc.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEspecialidad()));

        cargarDoctors();
    }

    public void setCitasController(CitasController citasController) {
        this.citasController = citasController;
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

    private boolean validarEmail() {
        String email = emaildoc.getText().trim();
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            mostrarAlerta(Alert.AlertType.WARNING, "El email no es válido.");
            return false;
        }
        return true;
    }

    private void comprobarMovil() {
        String movil = movildoc.getText().trim();
        if (!movil.isEmpty() && !validarMovil(movil)) {
            mostrarAlerta(Alert.AlertType.WARNING, "El móvil debe tener 9 números y empezar por 6 o 7.");
        }
    }

    private boolean validarMovil(String telefono) {
        return telefono.matches("[67]\\d{8}");
    }

    // Lee data/especialidades.json: { "especialidades": [ { "id": 1, "nombre": "..." }, ... ] }
    private void cargarEspecialidades() {
        JsonObject json = JsonParser.parseReader(new InputStreamReader(
                getClass().getResourceAsStream("/com/clase/data/especialidades.json"))).getAsJsonObject();
        for (JsonElement especialidad : json.getAsJsonArray("especialidades")) {
            espedoc.getItems().add(especialidad.getAsJsonObject().get("nombre").getAsString());
        }
    }

    @FXML
    private void cleanFormdoc() {
        tablaDoctores.getSelectionModel().clearSelection();
        iddoc.clear();
        nomdoc.clear();
        apeldoc.clear();
        grupocoledoc.selectToggle(null);
        movildoc.clear();
        emaildoc.clear();
        espedoc.setValue(null);
        // vuelve a mostrar todos los doctores (por si estaba filtrada por especialidad)
        cargarDoctors();
    }

    @FXML
    private void guardarDoctor() {
        if (nomdoc.getText().trim().isEmpty()
                || apeldoc.getText().trim().isEmpty()
                || movildoc.getText().trim().isEmpty()
                || emaildoc.getText().trim().isEmpty()
                || espedoc.getValue() == null
                || grupocoledoc.getSelectedToggle() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Rellena todos los campos.");
            return;
        }
        if (!validarEmail()) {
            return;
        }

        Doctor doctor = new Doctor(
            letrasCapitales(apeldoc.getText()),
            letrasCapitales(nomdoc.getText()),
            movildoc.getText().trim(),
            emaildoc.getText().trim(),
            coledocsi.isSelected(),
            espedoc.getValue()
        );

        if (!confirmar(iddoc.getText().isEmpty() ? "¿Desea grabar el doctor?" : "¿Desea modificar los datos del doctor?")) {
            return;
        }

        DoctorDAOMySQL dao = new DoctorDAOMySQL();
        if (!iddoc.getText().isEmpty()) {
            // El doctor ya existe: se modifica
            if (dao.modificarDoctor(Integer.parseInt(iddoc.getText()), doctor)) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Doctor modificado correctamente.");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "No se pudo modificar el doctor. Mira la consola para ver el error.");
                return;
            }
        } else {
            if (dao.guardarDoctor(doctor)) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Doctor guardado correctamente.");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "No se pudo guardar el doctor. Mira la consola para ver el error.");
                return;
            }
        }
        cleanFormdoc();
    }

    // Especialidad elegida en el combo (la usa el informe de doctores; null = todas)
    public String getEspecialidadSeleccionada() {
        return espedoc.getValue();
    }

    private void cargarDoctors() {
        DoctorDAOMySQL dao = new DoctorDAOMySQL();
        tablaDoctores.getItems().setAll(dao.cargarDoctores());
    }

    // Botón lupa: la tabla muestra solo los doctores de la especialidad elegida en el combo
    @FXML
    private void buscadocsporEspecialidad() {
        if (espedoc.getValue() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Elige una especialidad para buscar.");
            return;
        }
        DoctorDAOMySQL dao = new DoctorDAOMySQL();
        tablaDoctores.getItems().setAll(dao.cargardoctoresEspec(espedoc.getValue()));
    }

    // Rellena el formulario con el doctor seleccionado en la tabla
    @FXML
    private void cargarDoctor() {
        Doctor doctorselect = tablaDoctores.getSelectionModel().getSelectedItem();
        if (doctorselect == null) {
            return;
        }

        DoctorDAOMySQL dao = new DoctorDAOMySQL();
        Doctor doctor = dao.buscaDocporId(doctorselect.getID());
        if (doctor == null) {
            return;
        }

        iddoc.setText(String.valueOf(doctor.getID()));
        nomdoc.setText(doctor.getNombre());
        apeldoc.setText(doctor.getApellidos());
        if (doctor.getColegiado()) {
            coledocsi.setSelected(true);
        } else {
            coledocno.setSelected(true);
        }
        movildoc.setText(doctor.getMovil());
        emaildoc.setText(doctor.getEmail());
        espedoc.setValue(doctor.getEspecialidad());

        // el panel de citas recibe el id del doctor
        if (citasController != null) {
            citasController.cargarDatosDoctor(doctor);
        }
    }

    @FXML
    private void deleteDoctor() {
        Doctor doctorselect = tablaDoctores.getSelectionModel().getSelectedItem();
        if (doctorselect == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecciona un doctor de la tabla para eliminarlo.");
            return;
        }
        if (!confirmar("¿Desea eliminar el doctor " + doctorselect.getNombre() + " " + doctorselect.getApellidos() + "?")) {
            return;
        }

        DoctorDAOMySQL dao = new DoctorDAOMySQL();
        if (dao.eliminarDoctor(doctorselect.getID())) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Doctor eliminado correctamente.");
            cleanFormdoc();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo eliminar el doctor (puede que tenga citas). Mira la consola.");
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Xestión Doctores");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    // Pregunta antes de guardar/borrar. Devuelve true si se pulsa Aceptar
    private boolean confirmar(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Xestión Doctores");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        Optional<ButtonType> respuesta = alerta.showAndWait();
        return respuesta.isPresent() && respuesta.get() == ButtonType.OK;
    }
}
