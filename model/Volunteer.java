package model;

public class Volunteer {
    private int id;
    private String name;
    private String phone;
    private String aadhar;
    private String notes;

    public Volunteer() {}

    public Volunteer(String name, String phone, String aadhar, String notes) {
        this.name = name;
        this.phone = phone;
        this.aadhar = aadhar;
        this.notes = notes;
    }

    public Volunteer(int id, String name, String phone, String aadhar, String notes) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.aadhar = aadhar;
        this.notes = notes;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAadhar() { return aadhar; }
    public void setAadhar(String aadhar) { this.aadhar = aadhar; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return name + " (" + phone + ")";
    }
}