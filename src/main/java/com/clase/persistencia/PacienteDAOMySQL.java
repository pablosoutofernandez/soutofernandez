package com.clase.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import com.clase.modelo.Paciente;

public class PacienteDAOMySQL implements PacienteDAO {
    @Override
    public boolean guardarPaciente(Paciente paciente) {

        String sql = "INSERT INTO pacientes "
                    + "(dnipac, apelpac, nompac, movilpac, emailpac, nacpac, dirpac, propac, munipac) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionMYSQL.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, paciente.getDni());
            ps.setString(2, paciente.getApellidos());
            ps.setString(3, paciente.getNombre());
            ps.setString(4, paciente.getMovil());
            ps.setString(5, paciente.getEmail());
            if (paciente.getNacimiento() != null) {
                ps.setDate(6, java.sql.Date.valueOf(paciente.getNacimiento()));
            } else {
                ps.setNull(6, Types.DATE);
            }
            ps.setString(7, paciente.getDireccion());
            ps.setString(8, paciente.getProvincia());
            ps.setString(9, paciente.getMunicipio());

            ps.executeUpdate();

            System.out.println("Paciente guardado correctamente");
            return true;
        } catch (SQLException e) {
            System.err.println("Error al guardar el paciente: " + e.getMessage());
            return false;
        }
    }
}
