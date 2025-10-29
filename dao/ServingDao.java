package dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.Serving;
import util.InvalidServingException;

public class ServingDao {
    private final Connection conn;

    public ServingDao(Connection conn) {
        this.conn = conn;
    }

    public void add(Serving serving) throws SQLException, InvalidServingException {
        if (serving.getCount() <= 0) {
            throw new InvalidServingException("Serving count must be positive");
        }

        String sql = "INSERT INTO servings (volunteer_id, meal_id, date, count) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, serving.getVolunteerId());
            pstmt.setInt(2, serving.getMealId());
            pstmt.setString(3, serving.getDate().toString());
            pstmt.setInt(4, serving.getCount());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    serving.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public List<Serving> getRecentServings(int limit) throws SQLException {
        List<Serving> servings = new ArrayList<>();
        String sql = """
            SELECT s.*, v.name as volunteer_name, m.name as meal_name 
            FROM servings s
            JOIN volunteers v ON s.volunteer_id = v.id
            JOIN meals m ON s.meal_id = m.id
            ORDER BY s.date DESC, s.id DESC
            LIMIT ?
        """;
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    servings.add(extractServingWithNames(rs));
                }
            }
        }
        
        return servings;
    }

    public List<Serving> getMonthlyReport(int year, int month) throws SQLException {
        List<Serving> servings = new ArrayList<>();
        String sql = """
            SELECT s.*, v.name as volunteer_name, m.name as meal_name 
            FROM servings s
            JOIN volunteers v ON s.volunteer_id = v.id
            JOIN meals m ON s.meal_id = m.id
            WHERE strftime('%Y', date) = ? AND strftime('%m', date) = ?
            ORDER BY s.date DESC, s.id DESC
        """;
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, String.format("%04d", year));
            pstmt.setString(2, String.format("%02d", month));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    servings.add(extractServingWithNames(rs));
                }
            }
        }
        
        return servings;
    }

    private Serving extractServingWithNames(ResultSet rs) throws SQLException {
        Serving serving = new Serving(
            rs.getInt("id"),
            rs.getInt("volunteer_id"),
            rs.getInt("meal_id"),
            LocalDate.parse(rs.getString("date")),
            rs.getInt("count")
        );
        serving.setVolunteerName(rs.getString("volunteer_name"));
        serving.setMealName(rs.getString("meal_name"));
        return serving;
    }
}