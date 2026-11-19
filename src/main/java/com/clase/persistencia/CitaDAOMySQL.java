package com.clase.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.clase.modelo.Cita;

public class CitaDAOMySQL implements CitaDAO {

    @Override
    public boolean guardarCita(Cita cita) {

        // idcita es AUTO_INCREMENT, no se inserta
        String sql = "INSERT INTO citas "
                    + "(citadnipac, citaiddoc, fechacita, horacita, motivo, diagnostico, tratamiento, estadocita) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionMYSQL.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, cita.getCitadnipac());
            ps.setInt(2, cita.getCitaiddoc());
            ps.setDate(3, java.sql.Date.valueOf(cita.getFechacita()));
            ps.setString(4, cita.getHoracita().toString());
            ps.setString(5, cita.getMotivo());
            ps.setString(6, cita.getDiagnostico());
            ps.setString(7, cita.getTratamiento());
            ps.setString(8, cita.getEstadocita());

            ps.executeUpdate();

            System.out.println("Cita guardada correctamente");
            return true;
        } catch (SQLException e) {
            System.err.println("Error al guardar la cita: " + e.getMessage());
            return false;
        }
    }

    // Solo las citas del paciente indicado
    @Override
    public List<Cita> cargarCitasPaciente(String dni) {

        List<Cita> citas = new ArrayList<>();

        String sql = "SELECT idcita, citadnipac, citaiddoc, fechacita, horacita, "
                + "motivo, diagnostico, tratamiento, estadocita "
                + "FROM citas "
                + "WHERE citadnipac = ? "
                + "ORDER BY fechacita DESC, horacita DESC";

        try (Connection conexion = ConexionMYSQL.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, dni);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    citas.add(new Cita(
                            rs.getInt("idcita"),
                            rs.getString("citadnipac"),
                            rs.getInt("citaiddoc"),
                            rs.getDate("fechacita").toLocalDate(),
                            // horacita puede ser TIME o VARCHAR(8): se lee como texto ("10:30" o "10:30:00")
                            LocalTime.parse(rs.getString("horacita")),
                            rs.getString("motivo"),
                            rs.getString("diagnostico"),
                            rs.getString("tratamiento"),
                            rs.getString("estadocita")));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al cargar las citas: " + e.getMessage());
        }

        return citas;
    }

    @Override
    public boolean modificarCita(Cita cita) {

        String sql = "UPDATE citas SET "
                + "citadnipac = ?, citaiddoc = ?, fechacita = ?, horacita = ?, "
                + "motivo = ?, diagnostico = ?, tratamiento = ?, estadocita = ? "
                + "WHERE idcita = ?";

        try (Connection conexion = ConexionMYSQL.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, cita.getCitadnipac());
            ps.setInt(2, cita.getCitaiddoc());
            ps.setDate(3, java.sql.Date.valueOf(cita.getFechacita()));
            ps.setString(4, cita.getHoracita().toString());
            ps.setString(5, cita.getMotivo());
            ps.setString(6, cita.getDiagnostico());
            ps.setString(7, cita.getTratamiento());
            ps.setString(8, cita.getEstadocita());
            ps.setInt(9, cita.getIdcita());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al modificar la cita: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminarCita(int idcita) {

        String sql = "DELETE FROM citas WHERE idcita = ?";

        try (Connection conexion = ConexionMYSQL.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idcita);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar la cita: " + e.getMessage());
            return false;
        }
    }
}
