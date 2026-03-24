package models.transportation;

import interfaces.Bookable;
import models.Traveler;

public abstract class Transportation implements Bookable {
    private static int counter = 0;
    protected int id;
    protected String provider;
    protected String origin;
    protected String destination;
    protected String departureTime;
    protected String arrivalTime;
    protected double price;
    protected boolean available;

    protected Transportation(Builder<?> builder) {
        this.id = ++counter;
        this.provider = builder.provider;
        this.origin = builder.origin;
        this.destination = builder.destination;
        this.departureTime = builder.departureTime;
        this.arrivalTime = builder.arrivalTime;
        this.price = builder.price;
        this.available = true;
    }

    public abstract static class Builder<T extends Builder<T>> {
        private String provider;
        private String origin;
        private String destination;
        private String departureTime;
        private String arrivalTime;
        private double price;

        public Builder(String provider) {
            this.provider = provider;
        }

        @SuppressWarnings("unchecked")
        public T origin(String origin) { this.origin = origin; return (T) this; }

        @SuppressWarnings("unchecked")
        public T destination(String destination) { this.destination = destination; return (T) this; }

        @SuppressWarnings("unchecked")
        public T departureTime(String departureTime) { this.departureTime = departureTime; return (T) this; }

        @SuppressWarnings("unchecked")
        public T arrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; return (T) this; }

        @SuppressWarnings("unchecked")
        public T price(double price) { this.price = price; return (T) this; }

        public abstract Transportation build();
    }

    public abstract String getTransportationType();

    public int getId() { return this.id; }

    @Override
    public void book(Traveler traveler) throws Exception {
        if (!available) throw new Exception("Transportation not available.");
        available = false;
    }

    @Override
    public void cancel(Traveler traveler) throws Exception {
        available = true;
    }

    @Override
    public boolean isAvailable() { return available; }

    @Override
    public double getPrice() { return price; }

    @Override
    public String toString() {
        return getTransportationType() + " | " + provider + " | " + origin + " → " + destination + " | " + price + " lei";
    }
}
