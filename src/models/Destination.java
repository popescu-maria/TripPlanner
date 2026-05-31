package models;

public class Destination {
    private int id;
    private String city;
    private String country;

    public Destination(String city, String country) {
        this.city = city;
        this.country = country;
    }

    public String getCity() { return this.city; }
    public String getCountry() { return this.country; }
    public int getId() { return this.id; }

    public void setId(int id) { this.id = id; }

    @Override
    public String toString() {
        return country + ", " + city;
    }
}