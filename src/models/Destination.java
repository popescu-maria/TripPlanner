package models;

public class Destination {
    private String city;
    private String country;

    public Destination(String city, String country) {
        this.city = city;
        this.country = country;
    }

    public String getCity() { return this.city; }

    @Override
    public String toString() {
        return country + ", " + city;
    }
}
