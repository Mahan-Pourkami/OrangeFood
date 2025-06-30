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

public class ProfileScreen extends VBox {
    public interface UpdateHandler {
        void handleUpdate(JSONObject profileData);
    }

    public interface LogoutHandler {
        void handleLogout();
    }

    public ProfileScreen(String authToken, UpdateHandler updateHandler, LogoutHandler logoutHandler) {
        setPadding(new Insets(20));
        setSpacing(20);
        setAlignment(Pos.CENTER);

        Text title = new Text("Your Profile");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        // Fetch user data from server
        JSONObject userData = fetchUserData(authToken);

        // Personal Info
        Label fullNameLabel = new Label("Full Name:");
        TextField fullNameField = new TextField(userData.getString("full_name"));
        grid.add(fullNameLabel, 0, 0);
        grid.add(fullNameField, 1, 0);

        Label phoneLabel = new Label("Phone:");
        TextField phoneField = new TextField(userData.getString("phone"));
        phoneField.setEditable(false);
        grid.add(phoneLabel, 0, 1);
        grid.add(phoneField, 1, 1);

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField(userData.getString("email"));
        grid.add(emailLabel, 0, 2);
        grid.add(emailField, 1, 2);

        Label addressLabel = new Label("Address:");
        TextField addressField = new TextField(userData.getString("address"));
        grid.add(addressLabel, 0, 3);
        grid.add(addressField, 1, 3);

        Label roleLabel = new Label("Role:");
        TextField roleField = new TextField(userData.getString("role"));
        roleField.setEditable(false);
        grid.add(roleLabel, 0, 4);
        grid.add(roleField, 1, 4);

        // Bank Info
        Text bankTitle = new Text("Bank Information");
        bankTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        grid.add(bankTitle, 0, 5, 2, 1);

        JSONObject bankInfo = userData.getJSONObject("bank_info");

        Label bankNameLabel = new Label("Bank Name:");
        TextField bankNameField = new TextField(bankInfo.getString("bank_name"));
        grid.add(bankNameLabel, 0, 6);
        grid.add(bankNameField, 1, 6);

        Label accountNumberLabel = new Label("Account Number:");
        TextField accountNumberField = new TextField(bankInfo.getString("account_number"));
        grid.add(accountNumberLabel, 0, 7);
        grid.add(accountNumberField, 1, 7);

        // Profile Image
        Label profileImageLabel = new Label("Profile Image (Base64):");
        TextArea profileImageArea = new TextArea(userData.getString("profileImageBase64"));
        profileImageArea.setPrefRowCount(3);
        grid.add(profileImageLabel, 0, 8);
        grid.add(profileImageArea, 1, 8);

        Button updateButton = new Button("Update Profile");
        updateButton.setOnAction(e -> {
            JSONObject profileData = new JSONObject();
            profileData.put("full_name", fullNameField.getText());
            profileData.put("phone", phoneField.getText());
            profileData.put("email", emailField.getText());
            profileData.put("address", addressField.getText());
            profileData.put("role", roleField.getText());
            profileData.put("profileImageBase64", profileImageArea.getText());

            JSONObject bankInfoUpdate = new JSONObject();
            bankInfoUpdate.put("bank_name", bankNameField.getText());
            bankInfoUpdate.put("account_number", accountNumberField.getText());

            profileData.put("bank_info", bankInfoUpdate);

            updateHandler.handleUpdate(profileData);
        });

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> logoutHandler.handleLogout());

        grid.add(updateButton, 1, 9);
        grid.add(logoutButton, 1, 10);

        getChildren().addAll(title, grid);
    }

    private JSONObject fetchUserData(String authToken) {
        // In a real app, you would make an HTTP request to get the profile data
        // For now, return a mock object
        JSONObject userData = new JSONObject();
        userData.put("full_name", "John Doe");
        userData.put("phone", "09123456789");
        userData.put("email", "john@example.com");
        userData.put("address", "123 Main St");
        userData.put("role", "buyer");
        userData.put("profileImageBase64", "");

        JSONObject bankInfo = new JSONObject();
        bankInfo.put("bank_name", "Example Bank");
        bankInfo.put("account_number", "1234567890");

        userData.put("bank_info", bankInfo);

        return userData;
    }
}