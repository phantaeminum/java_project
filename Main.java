import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

import model.*;
import dao.*;
import util.*;

public class Main extends Application {
    private Connection conn;
    private VolunteerDao volunteerDao;
    private MealDao mealDao;
    private ServingDao servingDao;
    
    // UI Components
    private TableView<Volunteer> volunteersTable;
    private TableView<Meal> mealsTable;
    private TableView<Serving> servingsTable;
    private ComboBox<Volunteer> volunteerComboBox;
    private ComboBox<Meal> mealComboBox;

    @Override
    public void start(Stage primaryStage) {
        // Initialize database connection and DAOs
        conn = Database.getInstance().getConnection();
        volunteerDao = new VolunteerDao(conn);
        mealDao = new MealDao(conn);
        servingDao = new ServingDao(conn);

        // Create main tab pane
        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(
            createDashboardTab(),
            createVolunteerTab(),
            createMealTab(),
            createServingTab(),
            createReportsTab()
        );

        Scene scene = new Scene(tabPane, 800, 600);
        primaryStage.setTitle("Community Kitchen Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Tab createDashboardTab() {
        Tab tab = new Tab("Dashboard");
        tab.setClosable(false);

        // Volunteers Table
        volunteersTable = new TableView<>();
        volunteersTable.getColumns().addAll(
            createColumn("Name", "name", 150),
            createColumn("Phone", "phone", 100),
            createColumn("Aadhar", "aadhar", 120),
            createColumn("Notes", "notes", 200)
        );

        // Meals Table
        mealsTable = new TableView<>();
        mealsTable.getColumns().addAll(
            createColumn("Name", "name", 150),
            createColumn("Description", "description", 200),
            createColumn("Serving Size", "servingSize", 100)
        );

        // Recent Servings Table
        servingsTable = new TableView<>();
        servingsTable.getColumns().addAll(
            createColumn("Date", "date", 100),
            createColumn("Volunteer", "volunteerName", 150),
            createColumn("Meal", "mealName", 150),
            createColumn("Count", "count", 80)
        );

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(
            new Label("Volunteers"),
            volunteersTable,
            new Label("Meals"),
            mealsTable,
            new Label("Recent Servings"),
            servingsTable
        );

        tab.setContent(new ScrollPane(layout));
        
        // Load initial data
        refreshDashboard();
        return tab;
    }

    private Tab createVolunteerTab() {
        Tab tab = new Tab("Add Volunteer");
        tab.setClosable(false);

        TextField nameField = new TextField();
        TextField phoneField = new TextField();
        TextField aadharField = new TextField();
        TextArea notesArea = new TextArea();
        Button addButton = new Button("Add Volunteer");

        addButton.setOnAction(e -> {
            try {
                // Validate inputs
                if (!Validation.isValidName(nameField.getText())) {
                    showError("Invalid name");
                    return;
                }
                if (!Validation.isValidPhone(phoneField.getText())) {
                    showError("Invalid phone number (must be 10 digits)");
                    return;
                }
                if (!Validation.isValidAadhar(aadharField.getText())) {
                    showError("Invalid Aadhar number (must be 12 digits)");
                    return;
                }

                // Create and save volunteer
                Volunteer volunteer = new Volunteer(
                    nameField.getText(),
                    phoneField.getText(),
                    aadharField.getText(),
                    notesArea.getText()
                );
                
                volunteerDao.add(volunteer);
                showInfo("Volunteer added successfully");
                
                // Clear fields and refresh
                nameField.clear();
                phoneField.clear();
                aadharField.clear();
                notesArea.clear();
                refreshDashboard();
                
            } catch (DuplicateVolunteerException ex) {
                showError(ex.getMessage());
            } catch (Exception ex) {
                showError("Error adding volunteer: " + ex.getMessage());
            }
        });

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(5);
        grid.setHgap(5);

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Phone:"), 0, 1);
        grid.add(phoneField, 1, 1);
        grid.add(new Label("Aadhar:"), 0, 2);
        grid.add(aadharField, 1, 2);
        grid.add(new Label("Notes:"), 0, 3);
        grid.add(notesArea, 1, 3);
        grid.add(addButton, 1, 4);

        tab.setContent(grid);
        return tab;
    }

    private Tab createMealTab() {
        Tab tab = new Tab("Add Meal");
        tab.setClosable(false);

        TextField nameField = new TextField();
        TextArea descriptionArea = new TextArea();
        Spinner<Integer> servingSizeSpinner = new Spinner<>(1, 1000, 1);
        Button addButton = new Button("Add Meal");

        addButton.setOnAction(e -> {
            try {
                if (!Validation.isValidName(nameField.getText())) {
                    showError("Invalid meal name");
                    return;
                }

                Meal meal = new Meal(
                    nameField.getText(),
                    descriptionArea.getText(),
                    servingSizeSpinner.getValue()
                );
                
                mealDao.add(meal);
                showInfo("Meal added successfully");
                
                nameField.clear();
                descriptionArea.clear();
                servingSizeSpinner.getValueFactory().setValue(1);
                refreshDashboard();
                
            } catch (Exception ex) {
                showError("Error adding meal: " + ex.getMessage());
            }
        });

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(5);
        grid.setHgap(5);

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionArea, 1, 1);
        grid.add(new Label("Serving Size:"), 0, 2);
        grid.add(servingSizeSpinner, 1, 2);
        grid.add(addButton, 1, 3);

        tab.setContent(grid);
        return tab;
    }

    private Tab createServingTab() {
        Tab tab = new Tab("Record Serving");
        tab.setClosable(false);

        volunteerComboBox = new ComboBox<>();
        mealComboBox = new ComboBox<>();
        DatePicker datePicker = new DatePicker(LocalDate.now());
        Spinner<Integer> countSpinner = new Spinner<>(1, 1000, 1);
        Button addButton = new Button("Record Serving");

        addButton.setOnAction(e -> {
            try {
                if (volunteerComboBox.getValue() == null) {
                    showError("Please select a volunteer");
                    return;
                }
                if (mealComboBox.getValue() == null) {
                    showError("Please select a meal");
                    return;
                }
                if (datePicker.getValue() == null) {
                    showError("Please select a date");
                    return;
                }

                Serving serving = new Serving(
                   ,
                    mealComboBox.getValue().getId(),
                    datePicker.getValue(),
                    countSpinner.getValue()
                );
                
                servingDao.add(serving);
                showInfo("Serving recorded successfully");
                
                countSpinner.getValueFactory().setValue(1);
                refreshDashboard();
                
            } catch (InvalidServingException ex) {
                showError(ex.getMessage());
            } catch (Exception ex) {
                showError("Error recording serving: " + ex.getMessage());
            }
        });

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(5);
        grid.setHgap(5);

        grid.add(new Label("Volunteer:"), 0, 0);
        grid.add(volunteerComboBox, 1, 0);
        grid.add(new Label("Meal:"), 0, 1);
        grid.add(mealComboBox, 1, 1);
        grid.add(new Label("Date:"), 0, 2);
        grid.add(datePicker, 1, 2);
        grid.add(new Label("Count:"), 0, 3);
        grid.add(countSpinner, 1, 3);
        grid.add(addButton, 1, 4);

        tab.setContent(grid);
        
        // Load combo box data
        refreshComboBoxes();
        return tab;
    }

    private Tab createReportsTab() {
        Tab tab = new Tab("Reports");
        tab.setClosable(false);

        ComboBox<String> monthComboBox = new ComboBox<>(FXCollections.observableArrayList(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        ));
        monthComboBox.setValue(LocalDate.now().getMonth().toString());

        Spinner<Integer> yearSpinner = new Spinner<>(2000, 2100, LocalDate.now().getYear());
        Button generateButton = new Button("Generate Report");

        TableView<Serving> reportTable = new TableView<>();
        reportTable.getColumns().addAll(
            createColumn("Date", "date", 100),
            createColumn("Volunteer", "volunteerName", 150),
            createColumn("Meal", "mealName", 150),
            createColumn("Count", "count", 80)
        );

        generateButton.setOnAction(e -> {
            try {
                int month = monthComboBox.getSelectionModel().getSelectedIndex() + 1;
                int year = yearSpinner.getValue();
                
                List<Serving> servings = servingDao.getMonthlyReport(year, month);
                reportTable.setItems(FXCollections.observableArrayList(servings));
                
            } catch (Exception ex) {
                showError("Error generating report: " + ex.getMessage());
            }
        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        HBox controls = new HBox(10);
        controls.getChildren().addAll(
            new Label("Month:"),
            monthComboBox,
            new Label("Year:"),
            yearSpinner,
            generateButton
        );

        layout.getChildren().addAll(controls, reportTable);
        tab.setContent(layout);
        return tab;
    }

    private void refreshDashboard() {
        try {
            volunteersTable.setItems(FXCollections.observableArrayList(volunteerDao.getAll()));
            mealsTable.setItems(FXCollections.observableArrayList(mealDao.getAll()));
            servingsTable.setItems(FXCollections.observableArrayList(servingDao.getRecentServings(10)));
            refreshComboBoxes();
        } catch (Exception e) {
            showError("Error refreshing dashboard: " + e.getMessage());
        }
    }

    private void refreshComboBoxes() {
        try {
            volunteerComboBox.setItems(FXCollections.observableArrayList(volunteerDao.getAll()));
            mealComboBox.setItems(FXCollections.observableArrayList(mealDao.getAll()));
        } catch (Exception e) {
            showError("Error refreshing selections: " + e.getMessage());
        }
    }

    private <T> TableColumn<T, ?> createColumn(String title, String property, double width) {
        TableColumn<T, ?> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setPrefWidth(width);
        return column;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void stop() {
        Database.getInstance().close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}