package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Meal;

public class MealDao {
    private final Connection conn;

    public MealDao(Connection conn) {
        this.conn = conn;
    }

    public void add(Meal meal) throws SQLException {
        String sql = "INSERT INTO meals (name, description, serving_size) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, meal.getName());
            pstmt.setString(2, meal.getDescription());
            pstmt.setInt(3, meal.getServingSize());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    meal.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public List<Meal> getAll() throws SQLException {
        List<Meal> meals = new ArrayList<>();
        String sql = "SELECT * FROM meals ORDER BY name";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                meals.add(extractMeal(rs));
            }
        }
        
        return meals;
    }

    private Meal extractMeal(ResultSet rs) throws SQLException {
        return new Meal(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getInt("serving_size")
        );
    }
}