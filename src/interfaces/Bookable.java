package interfaces;

import models.Traveler;

public interface Bookable {
    void book() throws Exception;
    void cancel() throws Exception;
    boolean isAvailable();
    double getPrice();
}
