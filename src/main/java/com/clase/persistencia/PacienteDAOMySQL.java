package com.clase.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

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
     @Override
    public List<Paciente> cargarPacientes() {

        List<Paciente> pacientes = new ArrayList<>();

        // Solo obtenemos los campos que necesitamos para la tabla
        String sql = "SELECT dnipac, apelpac, nompac, movilpac, "
                + "propac, munipac "
                + "FROM pacientes "
                + "ORDER BY apelpac, nompac";

        try (Connection conexion = ConexionMYSQL.getConexion();
                PreparedStatement ps = conexion.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            // Recorremos las filas obtenidas
            while (rs.next()) {

                Paciente paciente = new Paciente(
                        rs.getString("dnipac"),
                        rs.getString("apelpac"),
                        rs.getString("nompac"),
                        rs.getString("movilpac"),
                        rs.getString("propac"),
                        rs.getString("munipac"));

                pacientes.add(paciente);
            }

        } catch (SQLException e) {
            System.out.println("Error al cargar los pacientes: " + e.getMessage());
        }

        return pacientes;
    }
    public boolean modificarPaciente(String dniOriginal, Paciente paciente) {
        String sql = "UPDATE pacientes SET "
                + "dnipac = ?, "
                + "apelpac = ?, "
                + "nompac = ?, "
                + "movilpac = ?, "
                + "emailpac = ?, "
                + "nacpac = ?, "
                + "dirpac = ?, "
                + "propac = ?, "
                + "munipac = ? "
                + "WHERE dnipac = ?";

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
            ps.setString(10, dniOriginal);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al modificar el paciente: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Paciente cargarPaciente(String dni) {

        // Obtenemos todos los campos de un paciente para rellenar el formulario
        String sql = "SELECT dnipac, apelpac, nompac, movilpac, emailpac, "
                + "nacpac, dirpac, propac, munipac "
                + "FROM pacientes "
                + "WHERE dnipac = ?";

        try (Connection conexion = ConexionMYSQL.getConexion();
                PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, dni);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    java.sql.Date nacimiento = rs.getDate("nacpac");

                    return new Paciente(
                            rs.getString("dnipac"),
                            rs.getString("apelpac"),
                            rs.getString("nompac"),
                            rs.getString("movilpac"),
                            rs.getString("emailpac"),
                            nacimiento != null ? nacimiento.toLocalDate() : null,
                            rs.getString("dirpac"),
                            rs.getString("propac"),
                            rs.getString("munipac"));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al cargar el paciente: " + e.getMessage());
        }

        return null;
    }
}