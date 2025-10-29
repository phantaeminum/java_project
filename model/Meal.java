package model;

public class Meal {
    private int id;
    private String name;
    private String description;
    private int servingSize;

    public Meal() {}

    public Meal(String name, String description, int servingSize) {
        this.name = name;
        this.description = description;
        this.servingSize = servingSize;
    }

    public Meal(int id, String name, String description, int servingSize) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.servingSize = servingSize;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getServingSize() { return servingSize; }
    public void setServingSize(int servingSize) { this.servingSize = servingSize; }

    @Override
    public String toString() {
        return name;
    }
}