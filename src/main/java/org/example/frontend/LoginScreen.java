package org.example.frontend;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class LoginScreen extends VBox {
    public interface LoginHandler {
        void handleLogin(String phone, String password);
    }

    public interface RegisterHandler {
        void handleRegister();
    }

    public LoginScreen(LoginHandler loginHandler, RegisterHandler registerHandler) {
        setPadding(new Insets(20));
        setSpacing(20);
        setAlignment(Pos.CENTER);

        Text title = new Text("Login");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label phoneLabel = new Label("Phone:");
        TextField phoneField = new TextField();
        phoneField.setPromptText("e.g., 09123456789");
        grid.add(phoneLabel, 0, 0);
        grid.add(phoneField, 1, 0);

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> {
            String phone = phoneField.getText();
            String password = passwordField.getText();

            if (phone.isEmpty() || password.isEmpty()) {
                // Show error
                return;
            }

            loginHandler.handleLogin(phone, password);
        });

        Button registerButton = new Button("Don't have an account? Register");
        registerButton.setOnAction(e -> registerHandler.handleRegister());

        grid.add(loginButton, 1, 2);
        grid.add(registerButton, 1, 3);

        getChildren().addAll(title, grid);
    }
}