package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import models.Traveler;
import services.TravelerService;

public class TravelersPanel extends VBox {

    private final TravelerService travelerService = new TravelerService();
    private final ObservableList<Traveler> items = FXCollections.observableArrayList();
    private final Label status = new Label();

    public TravelersPanel() {
        setSpacing(10);
        setPadding(new Insets(12));

        ListView<Traveler> list = new ListView<>(items);
        VBox.setVgrow(list, Priority.ALWAYS);
        list.setPrefHeight(240);

        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(8);
        TextField first = new TextField();
        TextField last = new TextField();
        TextField email = new TextField();
        TextField phone = new TextField();
        form.add(new Label("First name:"), 0, 0); form.add(first, 1, 0);
        form.add(new Label("Last name:"), 0, 1);  form.add(last, 1, 1);
        form.add(new Label("Email:"), 0, 2);      form.add(email, 1, 2);
        form.add(new Label("Phone:"), 0, 3);      form.add(phone, 1, 3);

        Button register = new Button("Register");
        Button delete = new Button("Delete selected");

        register.setOnAction(e -> {
            if (first.getText().isBlank() || email.getText().isBlank()) {
                status.setText("First name and email are required.");
                return;
            }
            try {
                travelerService.registerTraveler(
                        new Traveler(first.getText().trim(), last.getText().trim(),
                                email.getText().trim(), phone.getText().trim()));
                first.clear(); last.clear(); email.clear(); phone.clear();
                status.setText("Registered.");
                refresh();
            } catch (Exception ex) {
                status.setText("Error: " + ex.getMessage());
            }
        });

        delete.setOnAction(e -> {
            Traveler sel = list.getSelectionModel().getSelectedItem();
            if (sel == null) { status.setText("Select a traveler to delete."); return; }
            try {
                travelerService.deleteTraveler(sel.getId(), TripPlannerApp.currentTraveler.getId());
                status.setText("Deleted.");
                refresh();
            } catch (Exception ex) {
                status.setText(ex.getMessage());
            }
        });

        getChildren().addAll(
                new Label("Travelers:"), list,
                new Separator(),
                new Label("Register a traveler:"), form,
                new HBox(10, register, delete),
                status);

        refresh();
    }

    private void refresh() {
        try {
            items.setAll(travelerService.getAllTravelers());
        } catch (Exception ex) {
            status.setText("Could not load travelers: " + ex.getMessage());
        }
    }
}