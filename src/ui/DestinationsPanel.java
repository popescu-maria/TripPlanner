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
import models.Destination;

public class DestinationsPanel extends VBox {

    private final DestinationDAO destinationDAO = new DestinationDAO();
    private final ObservableList<Destination> items = FXCollections.observableArrayList();
    private final Label status = new Label();

    public DestinationsPanel() {
        setSpacing(10);
        setPadding(new Insets(12));

        ListView<Destination> list = new ListView<>(items);
        VBox.setVgrow(list, Priority.ALWAYS);
        list.setPrefHeight(260);

        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(8);
        TextField city = new TextField();
        TextField country = new TextField();
        form.add(new Label("City:"), 0, 0);    form.add(city, 1, 0);
        form.add(new Label("Country:"), 0, 1); form.add(country, 1, 1);

        Button add = new Button("Add destination");
        Button delete = new Button("Delete selected");

        add.setOnAction(e -> {
            if (city.getText().isBlank() || country.getText().isBlank()) {
                status.setText("City and country are required.");
                return;
            }
            try {
                destinationDAO.save(new Destination(city.getText().trim(), country.getText().trim()));
                city.clear(); country.clear();
                status.setText("Added.");
                refresh();
            } catch (Exception ex) { status.setText("Error: " + ex.getMessage()); }
        });

        delete.setOnAction(e -> {
            Destination sel = list.getSelectionModel().getSelectedItem();
            if (sel == null) { status.setText("Select one to delete."); return; }
            try {
                destinationDAO.delete(sel.getId());
                status.setText("Deleted.");
                refresh();
            } catch (Exception ex) { status.setText("Error: " + ex.getMessage()); }
        });

        getChildren().addAll(
                new Label("Destinations:"), list,
                new Separator(),
                new Label("Add a destination:"), form,
                new HBox(10, add, delete),
                status);

        refresh();
    }

    private void refresh() {
        try { items.setAll(destinationDAO.findAll()); }
        catch (Exception ex) { status.setText("Could not load: " + ex.getMessage()); }
    }
}