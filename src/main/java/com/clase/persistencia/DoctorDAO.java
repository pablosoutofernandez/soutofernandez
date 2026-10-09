package com.clase.persistencia;
import java.util.List;

import com.clase.modelo.Doctor;

public interface DoctorDAO {
    boolean guardarDoctor(Doctor doctor);
    List<Doctor> cargarDoctores();
    boolean modificarDoctor(Doctor doctor);
    boolean eliminarDoctor(int id);
}
