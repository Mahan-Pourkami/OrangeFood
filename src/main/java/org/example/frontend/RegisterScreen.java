package org.example.frontend;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.json.JSONObject;

public class RegisterScreen extends VBox {
    public interface RegisterSubmitHandler {
        void handleSubmit(JSONObject userData);
    }

    public interface BackHandler {
        void handleBack();
    }

    public RegisterScreen(RegisterSubmitHandler submitHandler, BackHandler backHandler) {
        setPadding(new Insets(20));
        setSpacing(20);
        setAlignment(Pos.CENTER);

        Text title = new Text("Register");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        // Personal Info
        Label fullNameLabel = new Label("Full Name:");
        TextField fullNameField = new TextField();
        grid.add(fullNameLabel, 0, 0);
        grid.add(fullNameField, 1, 0);

        Label phoneLabel = new Label("Phone:");
        TextField phoneField = new TextField();
        phoneField.setPromptText("e.g., 09123456789");
        grid.add(phoneLabel, 0, 1);
        grid.add(phoneField, 1, 1);

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        grid.add(emailLabel, 0, 2);
        grid.add(emailField, 1, 2);

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        grid.add(passwordLabel, 0, 3);
        grid.add(passwordField, 1, 3);

        Label addressLabel = new Label("Address:");
        TextField addressField = new TextField();
        grid.add(addressLabel, 0, 4);
        grid.add(addressField, 1, 4);

        Label roleLabel = new Label("Role:");
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("buyer", "seller", "courier");
        grid.add(roleLabel, 0, 5);
        grid.add(roleComboBox, 1, 5);

        // Bank Info
        Text bankTitle = new Text("Bank Information");
        bankTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        grid.add(bankTitle, 0, 6, 2, 1);

        Label bankNameLabel = new Label("Bank Name:");
        TextField bankNameField = new TextField();
        grid.add(bankNameLabel, 0, 7);
        grid.add(bankNameField, 1, 7);

        Label accountNumberLabel = new Label("Account Number:");
        TextField accountNumberField = new TextField();
        grid.add(accountNumberLabel, 0, 8);
        grid.add(accountNumberField, 1, 8);

        // Profile Image
        Label profileImageLabel = new Label("Profile Image (Base64):");
        TextArea profileImageArea = new TextArea();
        profileImageArea.setPrefRowCount(3);
        grid.add(profileImageLabel, 0, 9);
        grid.add(profileImageArea, 1, 9);

        Button submitButton = new Button("Register");
        submitButton.setOnAction(e -> {
            JSONObject userData = new JSONObject();
            userData.put("full_name", fullNameField.getText());
            userData.put("phone", phoneField.getText());
            userData.put("email", emailField.getText());
            userData.put("password", passwordField.getText());
            userData.put("address", addressField.getText());
            userData.put("role", roleComboBox.getValue());
            userData.put("profileImageBase64", profileImageArea.getText());

            JSONObject bankInfo = new JSONObject();
            bankInfo.put("bank_name", bankNameField.getText());
            bankInfo.put("account_number", accountNumberField.getText());

            userData.put("bank_info", bankInfo);

            submitHandler.handleSubmit(userData);
        });

        Button backButton = new Button("Back to Login");
        backButton.setOnAction(e -> backHandler.handleBack());

        grid.add(submitButton, 1, 10);
        grid.add(backButton, 1, 11);

        getChildren().addAll(title, grid);
    }
}