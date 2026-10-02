package com.clase;
import javafx.scene.control.TextField;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;
import javafx.fxml.Initializable;
import javafx.beans.property.SimpleStringProperty;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.ResourceBundle;

import com.clase.modelo.Paciente;
import com.clase.persistencia.ConexionMYSQL;
import com.clase.persistencia.PacienteDAOMySQL;

import java.time.LocalDate;

public class PacienteController implements Initializable {
    // Los nombres tienen que coincidir EXACTAMENTE con los fx:id de pacientes.fxml
    @FXML
    private TextField dnipac, nompac, ape1pac, dirpac, telpac, emailpac;
    @FXML
    private DatePicker nacpac;
    @FXML
    private ComboBox<String> cmbpac, locpac;
    @FXML
    private Button btnguardarpac, btndelpac;
    @FXML 
    private TableView<Paciente> tablaPacientes;
    @FXML 
    private TableColumn<Paciente, String> coldnipac, colnompac, colapelpac, colmovilpac, colpropac, colmunpac;

    // true cuando el formulario tiene cargado un paciente que ya está en la base de datos
    private boolean pacienteexiste = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dnipac.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // Cuando el TextField pierde el foco
                comprobarDni();
            }
        });
        cmbpac.getItems().addAll("A Coruña", "Lugo", "Ourense", "Pontevedra");
        locpac.getItems().addAll("Madrid", "Barcelona", "Valencia", "Sevilla", "Zaragoza");

        coldnipac.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDni()));
        colapelpac.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getApellidos()));
        colnompac.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNombre()));
        colmovilpac.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getMovil()));
        colpropac.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getProvincia()));
        colmunpac.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getMunicipio()));

        cargarPacientes();
    }

    @FXML
    private boolean comprobarDni(){
        String dni = dnipac.getText().trim().toUpperCase();
        dnipac.setText(dni);
        if (dni.length() != 9) {
            System.out.println("DNI inválido: debe tener 9 caracteres.");
            return false;
        }
        char letra = dni.charAt(8);
        String numeros = dni.substring(0, 8);
        if (!numeros.matches("\\d{8}")) {
            System.out.println("DNI inválido: los primeros 8 caracteres deben ser números.");
            return false;
        }
        int numero = Integer.parseInt(numeros);
        String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
        char letraCorrecta = letras.charAt(numero % 23);
        if (letra != letraCorrecta) {
            System.out.println("DNI inválido: la letra no coincide con el número.");
            return false;
        }
        System.out.println("DNI válido.");
        return true;
    }

    //que la primera letra de cada palabra del nombre y apellidos esté en mayúscula y el resto en minúscula. Por ejemplo, si el usuario introduce "juan perez", se debería guardar como "Juan Perez".
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
    private void comprobarMail() {
        String email = emailpac.getText().trim();
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            System.out.println("Email inválido.");
        } else {
            System.out.println("Email válido.");
        }
    }

    @FXML
    private void guardarPaciente() {
        Paciente paciente = leerFormulario();
        if (paciente == null) {
            return;
        }

        PacienteDAOMySQL dao = new PacienteDAOMySQL();
        if (pacienteexiste) {
            // El DNI original es el del paciente seleccionado en la tabla (por si se ha cambiado en el formulario)
            String dniOriginal = tablaPacientes.getSelectionModel().getSelectedItem().getDni();
            if (dao.modificarPaciente(dniOriginal, paciente)) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Paciente modificado correctamente.");
                cargarPacientes();
                limpiarFormulario();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "No se pudo modificar el paciente. Mira la consola para ver el error.");
            }
        } else {
            if (dao.guardarPaciente(paciente)) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Paciente guardado correctamente.");
                cargarPacientes();
                limpiarFormulario();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "No se pudo guardar el paciente. Mira la consola para ver el error.");
            }
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
        eliminarPaciente(dni);
        cargarPacientes();
        limpiarFormulario();
    }

    // Devuelve el paciente con los datos del formulario, o null si el DNI no es válido
    private Paciente leerFormulario() {
        if (!comprobarDni()) {
            mostrarAlerta(Alert.AlertType.WARNING, "El DNI no es válido.");
            return null;
        }
        if (!emailpac.getText().trim().isEmpty()) {
            comprobarMail();
        }
        String dni = dnipac.getText();
        String nombre = letrasCapitales(nompac.getText());
        String apellidos = letrasCapitales(ape1pac.getText());

        LocalDate fechaNacimiento = nacpac.getValue();

        String movil = telpac.getText();
        String email = emailpac.getText();
        String direccion = dirpac.getText();

        String localidad = locpac.getValue();
        String provincia = cmbpac.getValue();

        return new Paciente(
            dni,
            apellidos,
            nombre,
            movil,
            email,
            fechaNacimiento,
            direccion,
            provincia,
            localidad
        );
    }

    // Rellena el formulario con los datos del paciente seleccionado en la tabla
    @FXML
    private void cargarPaciente() {
        Paciente seleccionado = tablaPacientes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            return;
        }

        PacienteDAOMySQL dao = new PacienteDAOMySQL();
        Paciente paciente = dao.cargarPaciente(seleccionado.getDni());
        if (paciente == null) {
            return;
        }

        dnipac.setText(paciente.getDni());
        nompac.setText(paciente.getNombre());
        ape1pac.setText(paciente.getApellidos());
        nacpac.setValue(paciente.getNacimiento());
        telpac.setText(paciente.getMovil());
        emailpac.setText(paciente.getEmail());
        dirpac.setText(paciente.getDireccion());
        cmbpac.setValue(paciente.getProvincia());
        locpac.setValue(paciente.getMunicipio());

        pacienteexiste = true;
    }

    @FXML
    private void buscarPaciente() {
        if (!comprobarDni()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Introduce un DNI válido para buscar.");
            return;
        }
        String dni = dnipac.getText();

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

    @FXML
    private void limpiarFormulario() {
        tablaPacientes.getSelectionModel().clearSelection();
        dnipac.clear();
        nompac.clear();
        ape1pac.clear();
        nacpac.setValue(null);
        telpac.clear();
        emailpac.clear();
        dirpac.clear();
        cmbpac.setValue(null);
        locpac.setValue(null);

        pacienteexiste = false;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Xestión Pacientes");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML 
     private void cargarPacientes(){
        PacienteDAOMySQL dao = new PacienteDAOMySQL();
        List<Paciente> pacientes = dao.cargarPacientes();
        tablaPacientes.getItems().setAll(pacientes);
     }

     public void eliminarPaciente(String dni) {
        String sql = "DELETE FROM pacientes WHERE dnipac = ?";
        try (Connection conexion = ConexionMYSQL.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, dni);
            ps.executeUpdate();

        } catch (Exception e) {
            System.err.println("Error al eliminar el paciente: " + e.getMessage());
        }
     }
}
