package Controller;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class IntroController {


    @FXML
    Button continueButton;

    @FXML
    Hyperlink aboutus;

    @FXML
    Hyperlink support;



    public void handle_aboutus(MouseEvent mouseEvent) throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI("https://github.com/Mahan-Fathollahpour"));
    }
    public void handle_support(MouseEvent mouseEvent) throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI("https://github.com/parsa0s0a"));
    }

    public void handleButtonClick(MouseEvent mouseEvent) throws IOException {

        FXMLLoader newView = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/org/Login-view.fxml")));
        Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
        Parent root = newView.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);
    }


}
