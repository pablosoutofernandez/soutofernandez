package com.clase;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import com.clase.modelo.Paciente;
import com.clase.persistencia.PacienteDAOMySQL;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class PacientesController implements Initializable {
    // Los nombres tienen que coincidir EXACTAMENTE con los fx:id de pacientes.fxml
    @FXML
    private TextField dnipac, apelpac, nompac, movilpac, emailpac, dirpac;
    @FXML
    private DatePicker nacpac;
    @FXML
    private ComboBox<String> propac, munipac;
    @FXML
    private Button btnguardarpac, btndelpac;
    @FXML
    private TableView<Paciente> tablaPacientes;
    @FXML
    private TableColumn<Paciente, String> coldnipac, colapelpac, colnompac, colmovilpac, colpropac, colmunipac;

    private boolean emailValido = true;

    // true cuando el formulario tiene cargado un paciente que ya está en la base de datos
    private boolean pacienteexiste = false;

    // para pasar el paciente seleccionado al panel de citas
    private CitasController citasController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dnipac.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // Cuando el TextField pierde el foco
                comprobarDni();
            }
        });
        emailpac.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                emailValido = validarEmail();
            }
        });
        movilpac.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                comprobarMovil();
            }
        });

        cargarProvincias();
        cargarMunicipios();

        coldnipac.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDni()));
        colapelpac.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getApellidos()));
        colnompac.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNombre()));
        colmovilpac.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getMovil()));
        colpropac.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getProvincia()));
        colmunipac.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getMunicipio()));

        cargarPacientes();
    }

    public void setCitasController(CitasController controller) {
        this.citasController = controller;
    }

    private boolean validarEmail() {
        String email = emailpac.getText().trim();
        if (email.isEmpty()) {
            return true; // el email no es obligatorio
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            mostrarAlerta(Alert.AlertType.WARNING, "El email no es válido.");
            return false;
        }
        return true;
    }

    private void comprobarDni() {
        String dni = dnipac.getText().trim().toUpperCase();
        dnipac.setText(dni);
        if (!dni.isEmpty() && !validarDni(dni)) {
            mostrarAlerta(Alert.AlertType.WARNING, "El DNI no es válido.");
        }
    }

    private boolean validarDni(String documento) {
        if (!documento.matches("\\d{8}[A-Z]")) {
            return false;
        }
        String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
        int numero = Integer.parseInt(documento.substring(0, 8));
        return documento.charAt(8) == letras.charAt(numero % 23);
    }

    // que la primera letra de cada palabra esté en mayúscula y el resto en minúscula ("juan perez" -> "Juan Perez")
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

    private void comprobarMovil() {
        String movil = movilpac.getText().trim();
        if (!movil.isEmpty() && !validarMovil(movil)) {
            mostrarAlerta(Alert.AlertType.WARNING, "El móvil debe tener 9 números y empezar por 6 o 7.");
        }
    }

    private boolean validarMovil(String telefono) {
        return telefono.matches("[67]\\d{8}");
    }

    private void cargarProvincias() {
        propac.getItems().setAll("A Coruña", "Lugo", "Ourense", "Pontevedra");
    }

    private void cargarMunicipios() {
        munipac.getItems().setAll("Madrid", "Barcelona", "Valencia", "Sevilla", "Zaragoza");
    }

    @FXML
    private void cleanFormpac() {
        tablaPacientes.getSelectionModel().clearSelection();
        dnipac.clear();
        nompac.clear();
        apelpac.clear();
        nacpac.setValue(null);
        movilpac.clear();
        emailpac.clear();
        dirpac.clear();
        propac.setValue(null);
        munipac.setValue(null);

        pacienteexiste = false;
    }

    @FXML
    private void guardarPaciente() {
        String dni = dnipac.getText().trim().toUpperCase();
        if (!validarDni(dni)) {
            mostrarAlerta(Alert.AlertType.WARNING, "El DNI no es válido.");
            return;
        }
        emailValido = validarEmail();
        if (!emailValido) {
            return;
        }

        LocalDate fechaNacimiento = nacpac.getValue();
        Paciente paciente = new Paciente(
            dni,
            letrasCapitales(apelpac.getText()),
            letrasCapitales(nompac.getText()),
            movilpac.getText().trim(),
            emailpac.getText().trim(),
            fechaNacimiento,
            dirpac.getText(),
            propac.getValue(),
            munipac.getValue()
        );

        PacienteDAOMySQL dao = new PacienteDAOMySQL();
        if (pacienteexiste) {
            // El DNI original es el del paciente seleccionado en la tabla (por si se ha cambiado en el formulario)
            String dniOriginal = tablaPacientes.getSelectionModel().getSelectedItem().getDni();
            if (dao.modificarPaciente(dniOriginal, paciente)) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Paciente modificado correctamente.");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "No se pudo modificar el paciente. Mira la consola para ver el error.");
                return;
            }
        } else {
            if (dao.guardarPaciente(paciente)) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Paciente guardado correctamente.");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "No se pudo guardar el paciente. Mira la consola para ver el error.");
                return;
            }
        }
        cargarPacientes();
        cleanFormpac();
    }

    @FXML
    private void cargarPacientes() {
        PacienteDAOMySQL dao = new PacienteDAOMySQL();
        tablaPacientes.getItems().setAll(dao.cargarPacientes());
    }

    // Rellena el formulario con los datos del paciente seleccionado en la tabla
    @FXML
    private void cargarPaciente() {
        Paciente pacienteselect = tablaPacientes.getSelectionModel().getSelectedItem();
        if (pacienteselect == null) {
            return;
        }

        PacienteDAOMySQL dao = new PacienteDAOMySQL();
        Paciente paciente = dao.buscarPaciente(pacienteselect.getDni());
        if (paciente == null) {
            return;
        }

        dnipac.setText(paciente.getDni());
        nompac.setText(paciente.getNombre());
        apelpac.setText(paciente.getApellidos());
        nacpac.setValue(paciente.getNacimiento());
        movilpac.setText(paciente.getMovil());
        emailpac.setText(paciente.getEmail());
        dirpac.setText(paciente.getDireccion());
        propac.setValue(paciente.getProvincia());
        munipac.setValue(paciente.getMunicipio());

        pacienteexiste = true;

        // el panel de citas muestra las citas de este paciente
        if (citasController != null) {
            citasController.cargarDatosPaciente(paciente);
        }
    }

    // Lo llama el botón Eliminar: borra el paciente cuyo DNI está en el formulario
    @FXML
    private void eliminarPaciente() {
        String dni = dnipac.getText().trim();
        if (dni.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Introduce el DNI del paciente para eliminarlo.");
            return;
        }
        PacienteDAOMySQL dao = new PacienteDAOMySQL();
        if (dao.eliminarPaciente(dni)) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Paciente eliminado correctamente.");
            cargarPacientes();
            cleanFormpac();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo eliminar el paciente (puede que tenga citas). Mira la consola.");
        }
    }

    @FXML
    private void buscaPacdni() {
        String dni = dnipac.getText().trim().toUpperCase();
        if (!validarDni(dni)) {
            mostrarAlerta(Alert.AlertType.WARNING, "Introduce un DNI válido para buscar.");
            return;
        }

        cargarPacientes();
        for (Paciente paciente : tablaPacientes.getItems()) {
            if (dni.equals(paciente.getDni())) {
                tablaPacientes.getSelectionModel().select(paciente);
                cargarPaciente();
                return;
            }
        }
        mostrarAlerta(Alert.AlertType.INFORMATION, "No existe ningún paciente con el DNI " + dni + ".");
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Xestión Pacientes");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
