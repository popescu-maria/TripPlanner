package models;

import interfaces.Bookable;

public class Activity implements Bookable {
    private static int counter = 0;
    private int id;
    private String name;
    private Destination destination;
    private double price;
    private int maxParticipants;
    private int currentParticipants;

    private Activity(Builder builder) {
        this.id = ++counter;
        this.name = builder.name;
        this.destination = builder.destination;
        this.price = builder.price;
        this.maxParticipants = builder.maxParticipants;
        this.currentParticipants = 0;
    }

    public static class Builder {
        private String name;
        private Destination destination;
        private double price;
        private int maxParticipants = 10;

        public Builder(String name) {
            this.name = name;
        }

        public Builder destination(Destination destination) { this.destination = destination; return this; }
        public Builder price(double price) { this.price = price; return this; }
        public Builder maxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; return this; }

        public Activity build() { return new Activity(this); }
    }

    public int getId() { return this.id; }

    @Override
    public void book(Traveler traveler) throws Exception {
        if (!isAvailable()) throw new Exception("Activity is fully booked.");
        currentParticipants++;
    }

    @Override
    public void cancel(Traveler traveler) throws Exception {
        if (currentParticipants > 0) currentParticipants--;
    }

    @Override
    public boolean isAvailable() { return currentParticipants < maxParticipants; }

    @Override
    public double getPrice() { return price; }

    @Override
    public String toString() {
        return name + " | " + destination + " | $" + price + " | " + currentParticipants + "/" + maxParticipants + " participants";
    }
}