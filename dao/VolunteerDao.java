package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Volunteer;
import util.DuplicateVolunteerException;

public class VolunteerDao {
    private final Connection conn;

    public VolunteerDao(Connection conn) {
        this.conn = conn;
    }

    public void add(Volunteer volunteer) throws SQLException, DuplicateVolunteerException {
        String sql = "INSERT INTO volunteers (name, phone, aadhar, notes) VALUES (?, ?, ?, ?)";
        
        try {
            // Check for duplicate aadhar
            if (getByAadhar(volunteer.getAadhar()) != null) {
                throw new DuplicateVolunteerException("Volunteer with Aadhar " + volunteer.getAadhar() + " already exists");
            }
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, volunteer.getName());
                pstmt.setString(2, volunteer.getPhone());
                pstmt.setString(3, volunteer.getAadhar());
                pstmt.setString(4, volunteer.getNotes());
                pstmt.executeUpdate();

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        volunteer.setId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE")) {
                throw new DuplicateVolunteerException("Volunteer with Aadhar " + volunteer.getAadhar() + " already exists");
            }
            throw e;
        }
    }

    public Volunteer getByAadhar(String aadhar) throws SQLException {
        String sql = "SELECT * FROM volunteers WHERE aadhar = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, aadhar);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractVolunteer(rs);
                }
            }
        }
        return null;
    }

    public List<Volunteer> getAll() throws SQLException {
        List<Volunteer> volunteers = new ArrayList<>();
        String sql = "SELECT * FROM volunteers ORDER BY name";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                volunteers.add(extractVolunteer(rs));
            }
        }
        
        return volunteers;
    }

    private Volunteer extractVolunteer(ResultSet rs) throws SQLException {
        return new Volunteer(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("phone"),
            rs.getString("aadhar"),
            rs.getString("notes")
        );
    }
}