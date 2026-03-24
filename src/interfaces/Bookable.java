package interfaces;

import models.Traveler;

public interface Bookable {
    void book(Traveler traveler) throws Exception;
    void cancel(Traveler traveler) throws Exception;
    boolean isAvailable();
    double getPrice();
}
