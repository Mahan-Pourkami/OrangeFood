package org;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class OrangeApp extends Application {

    @FXML
    private Button loginbutton;

    @FXML
    private TextField phonefield;

    private boolean buttonWasClicked = false;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Login-view.fxml"));

        // Set this class as the controller
        fxmlLoader.setController(this);
        // Load the FXML first
        Scene scene = new Scene(fxmlLoader.load(), 1280, 860);

        loginbutton.setOnAction(event -> {
            buttonWasClicked = true;
            System.out.println(phonefield.getText());
        });

        stage.setTitle("Orange Food");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}