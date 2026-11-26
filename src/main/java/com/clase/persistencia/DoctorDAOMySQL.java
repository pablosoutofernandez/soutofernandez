package com.clase.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.clase.modelo.Doctor;


public class DoctorDAOMySQL implements DoctorDAO {

    @Override
    public boolean guardarDoctor(Doctor doctor) {

        // iddoc es AUTO_INCREMENT, no se inserta
        String sql = "INSERT INTO doctores "
                    + "(nomdoc, apeldoc, coledoc, movildoc, emaildoc, espedoc) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionMySQL.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, doctor.getNombre());
            ps.setString(2, doctor.getApellidos());
            ps.setBoolean(3, doctor.getColegiado());
            ps.setString(4, doctor.getMovil());
            ps.setString(5, doctor.getEmail());
            ps.setString(6, doctor.getEspecialidad());

            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al guardar el doctor: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Doctor> cargarDoctores() {

        List<Doctor> doctores = new ArrayList<>();

        String sql = "SELECT iddoc, nomdoc, apeldoc, coledoc, movildoc, emaildoc, espedoc "
                + "FROM doctores "
                + "ORDER BY apeldoc, nomdoc";

        try (Connection conexion = ConexionMySQL.getConexion();
                PreparedStatement ps = conexion.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                doctores.add(leerDoctor(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al cargar los doctores: " + e.getMessage());
        }

        return doctores;
    }

    @Override
    public Doctor buscaDocporId(Integer id) {

        String sql = "SELECT iddoc, nomdoc, apeldoc, coledoc, movildoc, emaildoc, espedoc "
                + "FROM doctores "
                + "WHERE iddoc = ?";

        try (Connection conexion = ConexionMySQL.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return leerDoctor(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar el doctor: " + e.getMessage());
        }

        return null;
    }

    @Override
    public boolean modificarDoctor(Integer id, Doctor doctor) {

        String sql = "UPDATE doctores SET "
                + "nomdoc = ?, apeldoc = ?, coledoc = ?, "
                + "movildoc = ?, emaildoc = ?, espedoc = ? "
                + "WHERE iddoc = ?";

        try (Connection conexion = ConexionMySQL.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, doctor.getNombre());
            ps.setString(2, doctor.getApellidos());
            ps.setBoolean(3, doctor.getColegiado());
            ps.setString(4, doctor.getMovil());
            ps.setString(5, doctor.getEmail());
            ps.setString(6, doctor.getEspecialidad());
            ps.setInt(7, id);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al modificar el doctor: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminarDoctor(Integer id) {

        String sql = "DELETE FROM doctores WHERE iddoc = ?";

        try (Connection conexion = ConexionMySQL.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar el doctor: " + e.getMessage());
            return false;
        }
    }

    // Solo los doctores de una especialidad (botón lupa de la pestaña Doctores)
    @Override
    public List<Doctor> cargardoctoresEspec(String espedoc) {

        List<Doctor> doctores = new ArrayList<>();

        String sql = "SELECT iddoc, nomdoc, apeldoc, coledoc, movildoc, emaildoc, espedoc "
                + "FROM doctores "
                + "WHERE espedoc = ? "
                + "ORDER BY apeldoc, nomdoc";

        try (Connection conexion = ConexionMySQL.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, espedoc);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    doctores.add(leerDoctor(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al cargar los doctores por especialidad: " + e.getMessage());
        }

        return doctores;
    }

    private Doctor leerDoctor(ResultSet rs) throws SQLException {
        return new Doctor(
                rs.getInt("iddoc"),
                rs.getString("apeldoc"),
                rs.getString("nomdoc"),
                rs.getString("movildoc"),
                rs.getString("emaildoc"),
                rs.getBoolean("coledoc"),
                rs.getString("espedoc"));
    }
}
