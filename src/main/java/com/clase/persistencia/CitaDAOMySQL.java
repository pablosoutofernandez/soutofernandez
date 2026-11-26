package com.clase.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.clase.modelo.Cita;

public class CitaDAOMySQL implements CitaDAO {

    @Override
    public boolean guardarCita(Cita cita) {

        // idcita es AUTO_INCREMENT, no se inserta
        String sql = "INSERT INTO citas "
                    + "(citadnipac, citaiddoc, fechacita, horacita, motivo, diagnostico, tratamiento, estadocita, tarifa) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionMySQL.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, cita.getCitadnipac());
            ps.setInt(2, cita.getCitaiddoc());
            ps.setDate(3, java.sql.Date.valueOf(cita.getFechacita()));
            ps.setString(4, cita.getHoracita());
            ps.setString(5, cita.getMotivo());
            ps.setString(6, cita.getDiagnostico());
            ps.setString(7, cita.getTratamiento());
            ps.setString(8, cita.getEstadocita());
            ps.setDouble(9, cita.getTarifa());

            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al guardar la cita: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean modificarCita(Integer id, Cita cita) {

        String sql = "UPDATE citas SET "
                + "citadnipac = ?, citaiddoc = ?, fechacita = ?, horacita = ?, "
                + "motivo = ?, diagnostico = ?, tratamiento = ?, estadocita = ?, tarifa = ? "
                + "WHERE idcita = ?";

        try (Connection conexion = ConexionMySQL.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, cita.getCitadnipac());
            ps.setInt(2, cita.getCitaiddoc());
            ps.setDate(3, java.sql.Date.valueOf(cita.getFechacita()));
            ps.setString(4, cita.getHoracita());
            ps.setString(5, cita.getMotivo());
            ps.setString(6, cita.getDiagnostico());
            ps.setString(7, cita.getTratamiento());
            ps.setString(8, cita.getEstadocita());
            ps.setDouble(9, cita.getTarifa());
            ps.setInt(10, id);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al modificar la cita: " + e.getMessage());
            return false;
        }
    }

    // Solo las citas del paciente. JOIN con doctores para mostrar apellidos y especialidad
    @Override
    public List<Cita> cargarCitaspaciente(String dnipac) {

        List<Cita> citas = new ArrayList<>();

        String sql = "SELECT c.idcita, c.fechacita, c.horacita, c.estadocita, c.tarifa "
                + "d.apeldoc, d.espedoc "
                + "FROM citas c "
                + "INNER JOIN doctores d ON c.citaiddoc = d.iddoc "
                + "WHERE c.citadnipac = ? "
                + "ORDER BY c.fechacita";

        try (Connection conexion = ConexionMySQL.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, dnipac);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    citas.add(new Cita(
                            rs.getInt("idcita"),
                            rs.getString("apeldoc"),
                            rs.getString("espedoc"),
                            rs.getDate("fechacita").toLocalDate(),
                            rs.getString("horacita"),
                            rs.getString("estadocita"),
                            rs.getDouble("tarifa")));

                }
            }

        } catch (SQLException e) {
            System.out.println("Error al cargar citas paciente: " + e.getMessage());
        }

        return citas;
    }

    @Override
    public Cita buscaCitaporId(Integer id) {

        String sql = "SELECT idcita, citadnipac, citaiddoc, fechacita, horacita, "
                + "motivo, diagnostico, tratamiento, estadocita "
                + "FROM citas WHERE idcita = ?";

        try (Connection conexion = ConexionMySQL.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Cita(
                            rs.getInt("idcita"),
                            rs.getString("citadnipac"),
                            rs.getInt("citaiddoc"),
                            rs.getDate("fechacita").toLocalDate(),
                            rs.getString("horacita"),
                            rs.getString("motivo"),
                            rs.getString("diagnostico"),
                            rs.getString("tratamiento"),
                            rs.getString("estadocita"),
                            rs.getDouble("tarifa"));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar la cita: " + e.getMessage());
        }

        return null;
    }

    @Override
    public boolean eliminaCita(Integer id) {

        String sql = "DELETE FROM citas WHERE idcita = ?";

        try (Connection conexion = ConexionMySQL.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar la cita: " + e.getMessage());
            return false;
        }
    }
}
