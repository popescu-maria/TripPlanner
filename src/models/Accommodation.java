package models;

import interfaces.Bookable;

public class Accommodation implements Bookable {
    private static int counter = 0;
    private int id;
    private String name;
    private String type;
    private Destination destination;
    private double pricePerNight;
    private int numberOfNights;
    private boolean available;

    private Accommodation(Builder builder) {
        this.id = ++counter;
        this.name = builder.name;
        this.type = builder.type;
        this.destination = builder.destination;
        this.pricePerNight = builder.pricePerNight;
        this.numberOfNights = builder.numberOfNights;
        this.available = true;
    }

    public static class Builder {
        private String name;
        private String type = "hotel";
        private Destination destination;
        private double pricePerNight;
        private int numberOfNights = 1;

        public Builder(String name) {
            this.name = name;
        }

        public Builder type(String type) { this.type = type; return this; }
        public Builder destination(Destination destination) { this.destination = destination; return this; }
        public Builder pricePerNight(double pricePerNight) { this.pricePerNight = pricePerNight; return this; }
        public Builder numberOfNights(int numberOfNights) { this.numberOfNights = numberOfNights; return this; }

        public Accommodation build() { return new Accommodation(this); }
    }

    public int getId() { return this.id; }

    @Override
    public void book() throws Exception {
        if (!available) throw new Exception("Accommodation not available.");
        available = false;
    }

    @Override
    public void cancel() throws Exception { available = true; }

    @Override
    public boolean isAvailable() { return available; }

    @Override
    public double getPrice() { return pricePerNight * numberOfNights; }

    @Override
    public String toString() {
        return name + " (" + type + ") | " + destination + " | " + pricePerNight + " lei" + "/night x " + numberOfNights + " nights";
    }
}