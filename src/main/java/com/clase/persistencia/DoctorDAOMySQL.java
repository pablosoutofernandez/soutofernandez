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
                    + "(nomedoc, apeldoc, coledoc, movildoc, maildoc, espedoc) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionMYSQL.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, doctor.getNombre());
            ps.setString(2, doctor.getApellidos());
            ps.setBoolean(3, doctor.isColegiado());
            ps.setString(4, doctor.getMovil());
            ps.setString(5, doctor.getEmail());
            ps.setString(6, doctor.getEspecialidad());

            ps.executeUpdate();

            System.out.println("Doctor guardado correctamente");
            return true;
        } catch (SQLException e) {
            System.err.println("Error al guardar el doctor: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Doctor> cargarDoctores() {

        List<Doctor> doctores = new ArrayList<>();

        String sql = "SELECT iddoc, nomedoc, apeldoc, coledoc, movildoc, maildoc, espedoc "
                + "FROM doctores "
                + "ORDER BY apeldoc, nomedoc";

        try (Connection conexion = ConexionMYSQL.getConexion();
                PreparedStatement ps = conexion.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                doctores.add(new Doctor(
                        rs.getInt("iddoc"),
                        rs.getString("nomedoc"),
                        rs.getString("apeldoc"),
                        rs.getBoolean("coledoc"),
                        rs.getString("movildoc"),
                        rs.getString("maildoc"),
                        rs.getString("espedoc")));
            }

        } catch (SQLException e) {
            System.out.println("Error al cargar los doctores: " + e.getMessage());
        }

        return doctores;
    }

    @Override
    public boolean modificarDoctor(Doctor doctor) {

        String sql = "UPDATE doctores SET "
                + "nomedoc = ?, apeldoc = ?, coledoc = ?, "
                + "movildoc = ?, maildoc = ?, espedoc = ? "
                + "WHERE iddoc = ?";

        try (Connection conexion = ConexionMYSQL.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, doctor.getNombre());
            ps.setString(2, doctor.getApellidos());
            ps.setBoolean(3, doctor.isColegiado());
            ps.setString(4, doctor.getMovil());
            ps.setString(5, doctor.getEmail());
            ps.setString(6, doctor.getEspecialidad());
            ps.setInt(7, doctor.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al modificar el doctor: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminarDoctor(int id) {

        String sql = "DELETE FROM doctores WHERE iddoc = ?";

        try (Connection conexion = ConexionMYSQL.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar el doctor: " + e.getMessage());
            return false;
        }
    }
}
