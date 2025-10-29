package model;

import java.time.LocalDate;

public class Serving {
    private int id;
    private int volunteerId;
    private int mealId;
    private LocalDate date;
    private int count;
    
    // For joining with other tables
    private String volunteerName;
    private String mealName;

    public Serving() {}

    public Serving(int volunteerId, int mealId, LocalDate date, int count) {
        this.volunteerId = volunteerId;
        this.mealId = mealId;
        this.date = date;
        this.count = count;
    }

    public Serving(int id, int volunteerId, int mealId, LocalDate date, int count) {
        this.id = id;
        this.volunteerId = volunteerId;
        this.mealId = mealId;
        this.date = date;
        this.count = count;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getVolunteerId() { return volunteerId; }
    public void setVolunteerId(int volunteerId) { this.volunteerId = volunteerId; }
    public int getMealId() { return mealId; }
    public void setMealId(int mealId) { this.mealId = mealId; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
    
    public String getVolunteerName() { return volunteerName; }
    public void setVolunteerName(String volunteerName) { this.volunteerName = volunteerName; }
    public String getMealName() { return mealName; }
    public void setMealName(String mealName) { this.mealName = mealName; }
}