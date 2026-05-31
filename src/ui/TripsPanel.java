package ui;

import dao.DestinationDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import models.Budget;
import models.Destination;
import models.Trip;
import services.TripService;

import java.time.LocalDate;

public class TripsPanel extends VBox {

    private final TripService tripService = new TripService();
    private final DestinationDAO destinationDAO = new DestinationDAO();
    private final ObservableList<Trip> items = FXCollections.observableArrayList();
    private final Label status = new Label();

    public TripsPanel() {
        setSpacing(10);
        setPadding(new Insets(12));

        ListView<Trip> list = new ListView<>(items);
        VBox.setVgrow(list, Priority.ALWAYS);
        list.setPrefHeight(200);

        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(8);
        TextField name = new TextField();
        DatePicker start = new DatePicker(LocalDate.now());
        DatePicker end = new DatePicker(LocalDate.now().plusDays(7));
        TextField budget = new TextField();
        budget.setPromptText("e.g. 3000");

        ListView<Destination> destPicker = new ListView<>(
                FXCollections.observableArrayList(destinationDAO.findAll()));
        destPicker.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        destPicker.setPrefHeight(100);
        Button reloadDest = new Button("Reload destinations");
        reloadDest.setOnAction(e ->
                destPicker.setItems(FXCollections.observableArrayList(destinationDAO.findAll())));

        form.add(new Label("Trip name:"), 0, 0); form.add(name, 1, 0);
        form.add(new Label("Start date:"), 0, 1); form.add(start, 1, 1);
        form.add(new Label("End date:"), 0, 2);   form.add(end, 1, 2);
        form.add(new Label("Budget (lei):"), 0, 3); form.add(budget, 1, 3);
        form.add(new Label("Destinations:"), 0, 4); form.add(destPicker, 1, 4);
        form.add(reloadDest, 1, 5);

        Button create = new Button("Create trip");
        Button delete = new Button("Delete selected");
        Button summary = new Button("View summary");

        create.setOnAction(e -> {
            if (TripPlannerApp.currentTraveler == null) {
                status.setText("No traveler logged in.");
                return;
            }
            if (name.getText().isBlank()) { status.setText("Trip name is required."); return; }
            try {
                double b = budget.getText().isBlank() ? 0 : Double.parseDouble(budget.getText().trim());
                Trip.Builder builder = new Trip.Builder(name.getText().trim())
                        .startDate(start.getValue())
                        .endDate(end.getValue())
                        .traveler(TripPlannerApp.currentTraveler)
                        .budget(new Budget(b));
                Trip trip = builder.build();
                for (Destination d : destPicker.getSelectionModel().getSelectedItems()) {
                    trip.getDestinations().add(d);
                }
                tripService.createTrip(trip);
                name.clear(); budget.clear();
                destPicker.getSelectionModel().clearSelection();
                status.setText("Created trip.");
                refresh();
            } catch (NumberFormatException ex) {
                status.setText("Budget must be a number.");
            } catch (Exception ex) {
                status.setText("Error: " + ex.getMessage());
            }
        });

        delete.setOnAction(e -> {
            Trip sel = list.getSelectionModel().getSelectedItem();
            if (sel == null) { status.setText("Select a trip."); return; }
            try { tripService.deleteTrip(sel.getId()); status.setText("Deleted."); refresh(); }
            catch (Exception ex) { status.setText("Error: " + ex.getMessage()); }
        });

        summary.setOnAction(e -> {
            Trip sel = list.getSelectionModel().getSelectedItem();
            if (sel == null) { status.setText("Select a trip."); return; }
            try {
                Alert a = new Alert(Alert.AlertType.INFORMATION, tripService.generateSummary(sel.getId()));
                a.setHeaderText("Trip summary");
                a.setResizable(true);
                a.getDialogPane().setPrefSize(480, 420);
                a.showAndWait();
            } catch (Exception ex) { status.setText("Error: " + ex.getMessage()); }
        });

        getChildren().addAll(
                new Label("My trips (sorted by start date):"), list,
                new Separator(),
                new Label("Create a trip:"), form,
                new HBox(10, create, delete, summary),
                status);

        refresh();
    }

    private void refresh() {
        try {
            if (TripPlannerApp.currentTraveler != null) {
                items.setAll(tripService.getTripsForTraveler(TripPlannerApp.currentTraveler));
            } else {
                items.clear();
            }
        } catch (Exception ex) {
            status.setText("Could not load trips: " + ex.getMessage());
        }
    }
}