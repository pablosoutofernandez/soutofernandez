package com.clase.persistencia;
import java.util.List;

import com.clase.modelo.Cita;

public interface CitaDAO {
    boolean guardarCita(Cita cita);
    List<Cita> cargarCitasPaciente(String dni);
    boolean modificarCita(Cita cita);
    boolean eliminarCita(int idcita);
}
