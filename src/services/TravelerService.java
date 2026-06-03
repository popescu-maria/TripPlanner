package services;

import audit.AuditService;
import dao.TravelerDAO;
import models.Traveler;

import java.util.List;

public class TravelerService {
    private final TravelerDAO travelerDAO = new TravelerDAO();
    private final AuditService audit = AuditService.getInstance();

    public Traveler registerTraveler(Traveler traveler) {
        Traveler saved = travelerDAO.save(traveler);
        audit.log("REGISTER_TRAVELER");
        return saved;
    }

    public Traveler findById(int id) {
        audit.log("FIND_TRAVELER_BY_ID");
        return travelerDAO.findById(id);
    }

    public Traveler findByEmail(String email) {
        audit.log("FIND_TRAVELER_BY_EMAIL");
        return travelerDAO.findByEmail(email);
    }

    public List<Traveler> getAllTravelers() {
        audit.log("LIST_TRAVELERS");
        return travelerDAO.findAll();
    }

    public void deleteTraveler(int id, int currentTravelerId) {
        if (id == currentTravelerId) {
            throw new IllegalStateException("You cannot delete the traveler you are logged in as.");
        }
        travelerDAO.delete(id);
        audit.log("DELETE_TRAVELER");
    }
}