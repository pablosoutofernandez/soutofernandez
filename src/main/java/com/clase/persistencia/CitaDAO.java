package com.clase.persistencia;
import java.util.List;

import com.clase.modelo.Cita;

public interface CitaDAO {
    boolean guardarCita(Cita cita);
    boolean modificarCita(Integer id, Cita cita);
    List<Cita> cargarCitaspaciente(String dnipac);
    Cita buscaCitaporId (Integer id);
    boolean eliminaCita(Integer id);
}
