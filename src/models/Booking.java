package models;

import interfaces.Bookable;
import java.time.LocalDateTime;

public class Booking {
    public enum Status { PENDING, CONFIRMED, CANCELLED }

    private static int counter = 0;
    private int bookingId;
    private Traveler traveler;
    private Bookable bookable;
    private LocalDateTime bookingDate;
    private Status status;
    private double totalPrice;

    public Booking(Traveler traveler, Bookable bookable) {
        this.bookingId = ++counter;
        this.traveler = traveler;
        this.bookable = bookable;
        this.bookingDate = LocalDateTime.now();
        this.status = Status.PENDING;
        this.totalPrice = bookable.getPrice();
    }

    public int getBookingId() { return this.bookingId; }
    public Status getStatus() { return this.status; }
    public Bookable getBookable() { return this.bookable; }
    public Traveler getTraveler() { return this.traveler; }


    public void confirm() { this.status = Status.CONFIRMED; }
    public void cancel() { this.status = Status.CANCELLED; }

    @Override
    public String toString() {
        return "Booking[" + bookingId + "] | " + traveler.getFullName() + " | " + status + " | " + totalPrice + " lei";
    }
}