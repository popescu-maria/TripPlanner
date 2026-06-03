package ui;

import dao.AccommodationDAO;
import dao.ActivityDAO;
import dao.DestinationDAO;
import dao.TransportationDAO;
import dao.TripDAO;
import services.TripService;
import interfaces.Bookable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import models.Accommodation;
import models.Activity;
import models.Booking;
import models.Destination;
import models.Trip;
import models.transportation.BusTransportation;
import models.transportation.FlightTransportation;
import models.transportation.TrainTransportation;
import models.transportation.Transportation;
import services.BookingService;

public class BookingsPanel extends VBox {

    private final BookingService bookingService = new BookingService();
    private final TripDAO tripDAO = new TripDAO();
    private final TripService tripService = new TripService();
    private final AccommodationDAO accommodationDAO = new AccommodationDAO();
    private final ActivityDAO activityDAO = new ActivityDAO();
    private final TransportationDAO transportationDAO = new TransportationDAO();
    private final DestinationDAO destinationDAO = new DestinationDAO();

    private final ObservableList<Booking> items = FXCollections.observableArrayList();
    private final Label status = new Label();

    private final ComboBox<Trip> tripBox = new ComboBox<>();
    private final ComboBox<String> typeBox = new ComboBox<>();
    private final ComboBox<Destination> destBox = new ComboBox<>();
    private final GridPane dynamicForm = new GridPane();

    private final TextField f1 = new TextField();
    private final TextField f2 = new TextField();
    private final TextField f3 = new TextField();
    private final TextField f4 = new TextField();
    private final TextField f5 = new TextField();
    private final TextField price = new TextField();

    public BookingsPanel() {
        setSpacing(10);
        setPadding(new Insets(12));

        ListView<Booking> list = new ListView<>(items);
        VBox.setVgrow(list, Priority.ALWAYS);
        list.setPrefHeight(180);

        tripBox.setItems(FXCollections.observableArrayList(myTrips()));
        typeBox.setItems(FXCollections.observableArrayList(
                "Flight", "Train", "Bus", "Accommodation", "Activity"));
        typeBox.getSelectionModel().selectFirst();
        reloadDestinations();

        dynamicForm.setHgap(10);
        dynamicForm.setVgap(8);
        rebuildForm("Flight");
        typeBox.setOnAction(e -> rebuildForm(typeBox.getValue()));

        Button reloadTrips = new Button("Reload trips");
        reloadTrips.setOnAction(e ->
                tripBox.setItems(FXCollections.observableArrayList(myTrips())));

        Button reloadDest = new Button("Reload destinations");
        reloadDest.setOnAction(e -> reloadDestinations());

        Button book = new Button("Book it");
        book.setOnAction(e -> doBooking());

        Button cancel = new Button("Cancel selected booking");
        cancel.setOnAction(e -> {
            Booking sel = list.getSelectionModel().getSelectedItem();
            if (sel == null) { status.setText("Select a booking to cancel."); return; }
            try {
                bookingService.cancelBooking(sel.getBookingId());
                status.setText("Cancelled.");
                refresh();
            } catch (Exception ex) { status.setText("Error: " + ex.getMessage()); }
        });

        GridPane top = new GridPane();
        top.setHgap(10); top.setVgap(8);
        top.add(new Label("Trip:"), 0, 0);  top.add(tripBox, 1, 0); top.add(reloadTrips, 2, 0);
        top.add(new Label("Type:"), 0, 1);  top.add(typeBox, 1, 1); top.add(reloadDest, 2, 1);

        getChildren().addAll(
                new Label("Bookings:"), list,
                new HBox(10, cancel),
                new Separator(),
                new Label("Create a booking:"), top, dynamicForm,
                new HBox(10, book),
                status);

        refresh();
    }

    private void reloadDestinations() {
        destBox.setItems(FXCollections.observableArrayList(destinationDAO.findAll()));
    }

    private void rebuildForm(String type) {
        dynamicForm.getChildren().clear();
        f1.clear(); f2.clear(); f3.clear(); f4.clear(); f5.clear(); price.clear();

        switch (type) {
            case "Flight", "Train", "Bus" -> {
                dynamicForm.add(new Label("Provider:"), 0, 0);     dynamicForm.add(f1, 1, 0);
                dynamicForm.add(new Label("Origin:"), 0, 1);       dynamicForm.add(f2, 1, 1);
                dynamicForm.add(new Label("Destination:"), 0, 2);  dynamicForm.add(f3, 1, 2);
                dynamicForm.add(new Label("Price (lei):"), 0, 3);  dynamicForm.add(price, 1, 3);
                if (type.equals("Flight")) {
                    dynamicForm.add(new Label("Flight number:"), 0, 4); dynamicForm.add(f4, 1, 4);
                    dynamicForm.add(new Label("Class:"), 0, 5);          dynamicForm.add(f5, 1, 5);
                } else if (type.equals("Train")) {
                    dynamicForm.add(new Label("Train number:"), 0, 4);  dynamicForm.add(f4, 1, 4);
                    dynamicForm.add(new Label("Wagon number:"), 0, 5);  dynamicForm.add(f5, 1, 5);
                } else {
                    dynamicForm.add(new Label("Bus number:"), 0, 4);    dynamicForm.add(f4, 1, 4);
                }
            }
            case "Accommodation" -> {
                dynamicForm.add(new Label("Name:"), 0, 0);          dynamicForm.add(f1, 1, 0);
                dynamicForm.add(new Label("Type:"), 0, 1);          dynamicForm.add(f2, 1, 1);
                dynamicForm.add(new Label("Destination:"), 0, 2);   dynamicForm.add(destBox, 1, 2);
                dynamicForm.add(new Label("Price/night:"), 0, 3);   dynamicForm.add(price, 1, 3);
                dynamicForm.add(new Label("Nights:"), 0, 4);        dynamicForm.add(f4, 1, 4);
            }
            case "Activity" -> {
                dynamicForm.add(new Label("Name:"), 0, 0);          dynamicForm.add(f1, 1, 0);
                dynamicForm.add(new Label("Destination:"), 0, 1);   dynamicForm.add(destBox, 1, 1);
                dynamicForm.add(new Label("Price (lei):"), 0, 2);   dynamicForm.add(price, 1, 2);
                dynamicForm.add(new Label("Max participants:"), 0, 3); dynamicForm.add(f4, 1, 3);
            }
        }
    }

    private double parsePrice() {
        return price.getText().isBlank() ? 0 : Double.parseDouble(price.getText().trim());
    }

    private void doBooking() {
        Trip trip = tripBox.getValue();
        if (trip == null) { status.setText("Select a trip."); return; }
        String type = typeBox.getValue();
        try {
            Bookable bookable = buildAndSaveBookable(type);
            if (bookable == null) return;
            Booking booking = bookingService.createBooking(trip, bookable);
            status.setText("Booked: " + booking);
            refresh();
        } catch (NumberFormatException ex) {
            status.setText("Numeric fields must be numbers.");
        } catch (Exception ex) {
            status.setText("Error: " + ex.getMessage());
        }
    }

    private Bookable buildAndSaveBookable(String type) {
        switch (type) {
            case "Flight" -> {
                Transportation t = new FlightTransportation.Builder(f1.getText().trim())
                        .origin(f2.getText().trim()).destination(f3.getText().trim())
                        .price(parsePrice())
                        .flightNumber(f4.getText().trim())
                        .airlineClass(f5.getText().isBlank() ? "economy" : f5.getText().trim())
                        .build();
                transportationDAO.save(t);
                return t;
            }
            case "Train" -> {
                int wagon = f5.getText().isBlank() ? 0 : Integer.parseInt(f5.getText().trim());
                Transportation t = new TrainTransportation.Builder(f1.getText().trim())
                        .origin(f2.getText().trim()).destination(f3.getText().trim())
                        .price(parsePrice())
                        .trainNumber(f4.getText().trim())
                        .wagonNumber(wagon)
                        .build();
                transportationDAO.save(t);
                return t;
            }
            case "Bus" -> {
                Transportation t = new BusTransportation.Builder(f1.getText().trim())
                        .origin(f2.getText().trim()).destination(f3.getText().trim())
                        .price(parsePrice())
                        .busNumber(f4.getText().trim())
                        .build();
                transportationDAO.save(t);
                return t;
            }
            case "Accommodation" -> {
                if (destBox.getValue() == null) { status.setText("Pick a destination."); return null; }
                int nights = f4.getText().isBlank() ? 1 : Integer.parseInt(f4.getText().trim());
                Accommodation a = new Accommodation.Builder(f1.getText().trim())
                        .type(f2.getText().isBlank() ? "hotel" : f2.getText().trim())
                        .destination(destBox.getValue())
                        .pricePerNight(parsePrice())
                        .numberOfNights(nights)
                        .build();
                accommodationDAO.save(a);
                return a;
            }
            case "Activity" -> {
                if (destBox.getValue() == null) { status.setText("Pick a destination."); return null; }
                int maxp = f4.getText().isBlank() ? 10 : Integer.parseInt(f4.getText().trim());
                Activity a = new Activity.Builder(f1.getText().trim())
                        .destination(destBox.getValue())
                        .price(parsePrice())
                        .maxParticipants(maxp)
                        .build();
                activityDAO.save(a);
                return a;
            }
            default -> { status.setText("Unknown type."); return null; }
        }
    }

    private java.util.List<Trip> myTrips() {
        if (TripPlannerApp.currentTraveler == null) return new java.util.ArrayList<>();
        return tripService.getTripsForTraveler(TripPlannerApp.currentTraveler);
    }

    private void refresh() {
        try {
            java.util.List<Booking> mine = new java.util.ArrayList<>();
            for (Trip t : myTrips()) {
                mine.addAll(bookingService.getBookingsForTrip(t));
            }
            items.setAll(mine);
        } catch (Exception ex) {
            status.setText("Could not load bookings: " + ex.getMessage());
        }
    }
}