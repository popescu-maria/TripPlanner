package ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Traveler;
import services.TravelerService;

public class TripPlannerApp extends Application {

    public static Traveler currentTraveler = null;

    private final TravelerService travelerService = new TravelerService();
    private BorderPane root;
    private Label sessionLabel;

    @Override
    public void start(Stage stage) {
        showLoginWindow(stage);
    }

    // ---------------- LOGIN / REGISTER GATE ----------------

    private void showLoginWindow(Stage stage) {
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Label msg = new Label();

        // ----- Login tab (by email) -----
        GridPane loginForm = new GridPane();
        loginForm.setHgap(10); loginForm.setVgap(10); loginForm.setPadding(new Insets(20));
        TextField loginEmail = new TextField();
        Button loginBtn = new Button("Log in");
        loginForm.add(new Label("Email:"), 0, 0); loginForm.add(loginEmail, 1, 0);
        loginForm.add(loginBtn, 1, 1);
        Tab loginTab = new Tab("Log in", loginForm);

        // ----- Register tab -----
        GridPane regForm = new GridPane();
        regForm.setHgap(10); regForm.setVgap(10); regForm.setPadding(new Insets(20));
        TextField first = new TextField();
        TextField last = new TextField();
        TextField email = new TextField();
        TextField phone = new TextField();
        Button regBtn = new Button("Register & log in");
        regForm.add(new Label("First name:"), 0, 0); regForm.add(first, 1, 0);
        regForm.add(new Label("Last name:"), 0, 1);  regForm.add(last, 1, 1);
        regForm.add(new Label("Email:"), 0, 2);      regForm.add(email, 1, 2);
        regForm.add(new Label("Phone:"), 0, 3);      regForm.add(phone, 1, 3);
        regForm.add(regBtn, 1, 4);
        Tab regTab = new Tab("Register", regForm);

        tabs.getTabs().addAll(loginTab, regTab);

        loginBtn.setOnAction(e -> {
            String mail = loginEmail.getText().trim();
            if (mail.isEmpty()) { msg.setText("Enter your email."); return; }
            try {
                Traveler t = travelerService.findByEmail(mail);
                if (t == null) {
                    msg.setText("No account with that email. Register instead.");
                } else {
                    currentTraveler = t;
                    openMainWindow(stage);
                }
            } catch (Exception ex) { msg.setText("Error: " + ex.getMessage()); }
        });

        regBtn.setOnAction(e -> {
            if (first.getText().isBlank() || email.getText().isBlank()) {
                msg.setText("First name and email are required."); return;
            }
            try {
                if (travelerService.findByEmail(email.getText().trim()) != null) {
                    msg.setText("That email is already registered. Log in instead.");
                    return;
                }
                Traveler t = travelerService.registerTraveler(
                        new Traveler(first.getText().trim(), last.getText().trim(),
                                email.getText().trim(), phone.getText().trim()));
                currentTraveler = t;
                openMainWindow(stage);
            } catch (Exception ex) { msg.setText("Error: " + ex.getMessage()); }
        });

        VBox box = new VBox(12, new Label("Welcome to Trip Planner"), tabs, msg);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20));

        Scene scene = new Scene(box, 420, 360);
        stage.setTitle("Trip Planner - Sign in");
        stage.setScene(scene);
        stage.show();
    }

    // ---------------- MAIN WINDOW ----------------

    private void openMainWindow(Stage stage) {
        root = new BorderPane();

        MenuBar menuBar = new MenuBar();

        Menu navMenu = new Menu("Navigate");
        MenuItem travelersItem = new MenuItem("Travelers");
        MenuItem destinationsItem = new MenuItem("Destinations");
        MenuItem tripsItem = new MenuItem("Trips");
        MenuItem bookingsItem = new MenuItem("Bookings");
        navMenu.getItems().addAll(travelersItem, destinationsItem, tripsItem, bookingsItem);

        Menu fileMenu = new Menu("File");
        MenuItem logoutItem = new MenuItem("Log out");
        MenuItem exitItem = new MenuItem("Exit");
        fileMenu.getItems().addAll(logoutItem, exitItem);

        menuBar.getMenus().addAll(navMenu, fileMenu);

        travelersItem.setOnAction(e -> show(new TravelersPanel()));
        destinationsItem.setOnAction(e -> show(new DestinationsPanel()));
        tripsItem.setOnAction(e -> show(new TripsPanel()));
        bookingsItem.setOnAction(e -> show(new BookingsPanel()));
        logoutItem.setOnAction(e -> { currentTraveler = null; showLoginWindow(stage); });
        exitItem.setOnAction(e -> stage.close());

        sessionLabel = new Label("  Logged in as: " + currentTraveler.getFullName());

        BorderPane top = new BorderPane();
        top.setTop(menuBar);
        top.setBottom(sessionLabel);
        root.setTop(top);

        show(new TripsPanel());

        Scene scene = new Scene(root, 720, 640);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setTitle("Trip Planner");
        stage.setScene(scene);
        stage.show();
    }

    private void show(Node panel) {
        root.setCenter(panel);
    }

    public static void main(String[] args) {
        launch(args);
    }
}