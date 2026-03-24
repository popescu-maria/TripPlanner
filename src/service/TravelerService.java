package service;

import models.Traveler;
import java.util.ArrayList;
import java.util.List;

public class TravelerService {
    private List<Traveler> travelers = new ArrayList<>();

    public Traveler registerTraveler(Traveler traveler) {
        travelers.add(traveler);
        return traveler;
    }

    public Traveler findById(int id) {
        for (Traveler t : travelers) {
            if (t.getId() == id) return t;
        }
        return null;
    }

    public Traveler findByEmail(String email) {
        for (Traveler t : travelers) {
            if (t.getEmail().equalsIgnoreCase(email)) return t;
        }
        return null;
    }

    public List<Traveler> getAllTravelers() {
        return travelers;
    }

    public void deleteTraveler(int id) {
        Traveler traveler = findById(id);
        if (traveler != null) travelers.remove(traveler);
    }
}