package com.clase;
import javafx.scene.control.TextField;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Button;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;

import com.clase.modelo.Paciente;
import com.clase.persistencia.PacienteDAOMySQL;

import java.time.LocalDate;

public class PacienteController implements Initializable {
    // Los nombres tienen que coincidir EXACTAMENTE con los fx:id de pacientes.fxml
    @FXML
    private TextField dnipac, nompac, ape1pac, ape2pac, dirpac, telpac, emailpac;
    @FXML
    private DatePicker nacpac;
    @FXML
    private ComboBox<String> cmbpac, locpac;
    @FXML
    private Button btnguardarpac, btnmodpac, btndelpac;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dnipac.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // Cuando el TextField pierde el foco
                comprobarDni();
            }
        });
        cmbpac.getItems().addAll("A Coruña", "Lugo", "Ourense", "Pontevedra");
        locpac.getItems().addAll("Madrid", "Barcelona", "Valencia", "Sevilla", "Zaragoza");
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
        if (!comprobarDni()) {
            mostrarAlerta(Alert.AlertType.WARNING, "El DNI no es válido.");
            return;
        }
        if (!emailpac.getText().trim().isEmpty()) {
            comprobarMail();
        }
        String dni = dnipac.getText();
        String nombre = letrasCapitales(nompac.getText());
        String apellidos = letrasCapitales(ape1pac.getText() + " " + ape2pac.getText());

        LocalDate fechaNacimiento = nacpac.getValue();

        String movil = telpac.getText();
        String email = emailpac.getText();
        String direccion = dirpac.getText();

        String localidad = locpac.getValue();
        String provincia = cmbpac.getValue();

        Paciente paciente = new Paciente(
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

        PacienteDAOMySQL dao = new PacienteDAOMySQL();
        if (dao.guardarPaciente(paciente)) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Paciente guardado correctamente.");
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo guardar el paciente. Mira la consola para ver el error.");
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Xestión Pacientes");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
