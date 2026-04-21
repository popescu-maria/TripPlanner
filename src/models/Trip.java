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
    private Traveler traveler;
    private List<Destination> destinations;
    private List<Transportation> transportations;
    private Accommodation accommodation;
    private List<Activity> activities;
    private Budget budget;

    private Trip(Builder builder) {
        this.id = ++counter;
        this.name = builder.name;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.traveler = builder.traveler;
        this.destinations = builder.destinations;
        this.transportations = builder.transportations;
        this.accommodation = builder.accommodation;
        this.activities = new ArrayList<>();
        this.budget = builder.budget;
    }

    public static class Builder {
        private String name;
        private LocalDate startDate;
        private LocalDate endDate;
        private Traveler traveler;
        private List<Destination> destinations = new ArrayList<>();
        private List<Transportation> transportations = new ArrayList<>();
        private Accommodation accommodation;
        private Budget budget;

        public Builder(String name) { this.name = name; }
        public Builder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public Builder endDate(LocalDate endDate) { this.endDate = endDate; return this; }
        public Builder traveler(Traveler traveler) { this.traveler = traveler; return this; }
        public Builder destination(Destination destination) { this.destinations.add(destination); return this; }
        public Builder transportation(Transportation transportation) { this.transportations.add(transportation); return this; }
        public Builder accommodation(Accommodation accommodation) { this.accommodation = accommodation; return this; }
        public Builder budget(Budget budget) { this.budget = budget; return this; }
        public Trip build() { return new Trip(this); }
    }

    public void addActivity(Activity activity) { activities.add(activity); }
    public void removeActivity(Activity activity) { activities.remove(activity); }
    public void addTransportation(Transportation transportation) { transportations.add(transportation); }
    public void removeTransportation(Transportation transportation) { transportations.remove(transportation); }

    public double calculateTotalCost() {
        double total = 0;
        for (Transportation t : transportations) total += t.getPrice();
        if (accommodation != null) total += accommodation.getPrice();
        for (Activity a : activities) total += a.getPrice();
        return total;
    }

    public int getId() { return this.id; }
    public String getName() { return this.name; }
    public LocalDate getStartDate() { return this.startDate; }
    public LocalDate getEndDate() { return this.endDate; }
    public Traveler getTraveler() { return this.traveler; }
    public List<Destination> getDestinations() { return this.destinations; }
    public List<Transportation> getTransportations() { return this.transportations; }
    public Accommodation getAccommodation() { return this.accommodation; }
    public List<Activity> getActivities() { return this.activities; }
    public Budget getBudget() { return this.budget; }
    public void setName(String name) { this.name = name; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public void setAccommodation(Accommodation accommodation) { this.accommodation = accommodation; }
    public void setBudget(Budget budget) { this.budget = budget; }

    @Override
    public int compareTo(Trip other) {
        return this.startDate.compareTo(other.startDate);
    }

    @Override
    public String toString() {
        return id + " | " + name + " | " + startDate + " → " + endDate + " | Cost: " + calculateTotalCost() + " lei";
    }
}