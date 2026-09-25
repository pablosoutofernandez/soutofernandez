package com.clase.persistencia;
import com.clase.modelo.Paciente;

public interface PacienteDAO {
    boolean guardarPaciente(Paciente paciente);
}
