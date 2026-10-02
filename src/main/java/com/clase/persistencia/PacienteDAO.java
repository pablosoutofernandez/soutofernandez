package com.clase.persistencia;
import java.util.List;

import com.clase.modelo.Paciente;

public interface PacienteDAO {
    boolean guardarPaciente(Paciente paciente);
    List<Paciente> cargarPacientes();
    Paciente cargarPaciente(String dni);
}
