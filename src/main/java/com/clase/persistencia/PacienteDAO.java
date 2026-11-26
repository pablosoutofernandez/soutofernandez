package com.clase.persistencia;
import java.util.List;

import com.clase.modelo.Paciente;

public interface PacienteDAO {
    boolean guardarPaciente(Paciente paciente);
    List<Paciente> cargarPacientes();
    Paciente buscarPaciente(String dni);
    boolean modificarPaciente(String dni, Paciente paciente);
    boolean eliminarPaciente(String dni);
}
