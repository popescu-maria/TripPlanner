package models;

public class Traveler {
    private static int counter = 0;
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    public Traveler(String firstName, String lastName, String email, String phoneNumber) {
        this.id = ++counter;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public String getFullName() { return firstName + " " + lastName; }
    public int getId() { return this.id; }
    public String getEmail() { return this.email; }

    @Override
    public String toString() {
        return id + " | " + getFullName() + " | " + email;
    }

}
