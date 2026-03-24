package models;

import models.transportation.Transportation;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Trip implements Comparable<Trip> {
    private static int counter = 0;
    private int id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Destination> destinations;
    private List<Traveler> travelers;
    private List<Transportation> transportations;
    private Accommodation accommodation;
    private List<Activity> activities;
    private Budget budget;

    private Trip(Builder builder) {
        this.id = ++counter;
        this.name = builder.name;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.destinations = builder.destinations;
        this.travelers = new ArrayList<>();
        this.transportations = builder.transportations;
        this.accommodation = builder.accommodation;
        this.activities = new ArrayList<>();
        this.budget = builder.budget;
    }

    public static class Builder {
        private String name;
        private LocalDate startDate;
        private LocalDate endDate;
        private List<Destination> destinations = new ArrayList<>();
        private List<Transportation> transportations = new ArrayList<>();
        private Accommodation accommodation;
        private Budget budget;

        public Builder(String name) {
            this.name = name;
        }

        public Builder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public Builder endDate(LocalDate endDate) { this.endDate = endDate; return this; }
        public Builder destination(Destination destination) { this.destinations.add(destination); return this; }
        public Builder transportation(Transportation transportation) { this.transportations.add(transportation); return this; }
        public Builder accommodation(Accommodation accommodation) { this.accommodation = accommodation; return this; }
        public Builder budget(Budget budget) { this.budget = budget; return this; }

        public Trip build() { return new Trip(this); }
    }

    public void addTraveler(Traveler traveler) { travelers.add(traveler); }
    public void removeTraveler(Traveler traveler) { travelers.remove(traveler); }
    public void addActivity(Activity activity) { activities.add(activity); }
    public void addTransportation(Transportation transportation) { transportations.add(transportation); }
    public void removeTransportation(Transportation transportation) { transportations.remove(transportation); }

    public double calculateTotalCost() {
        double total = 0;
        for (Transportation t : transportations) total += t.getPrice();
        if (accommodation != null) total += accommodation.getPrice();
        for (Activity a : activities) total += a.getPrice();
        return total;
    }

    public void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public int getId() { return this.id; }
    public List<Destination> getDestinations() { return this.destinations; }
    public LocalDate getStartDate() { return this.startDate; }
    public LocalDate getEndDate() { return this.endDate; }
    public String getName() { return this.name; }
    public List<Transportation> getTransportations() { return transportations; }
    public Accommodation getAccommodation() { return accommodation; }
    public List<Activity> getActivities() { return activities; }
    public List<Traveler> getTravelers() { return travelers; }
    public Budget getBudget() { return budget; }

    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public void setName(String name) { this.name = name; }
    public void setAccommodation(Accommodation accommodation) { this.accommodation = accommodation; }

    @Override
    public int compareTo(Trip other) {
        return this.startDate.compareTo(other.startDate);
    }

    @Override
    public String toString() {
        return id + " | " + name + " | " + startDate + " → " + endDate + " | Cost: " + calculateTotalCost() + " lei";
    }
}