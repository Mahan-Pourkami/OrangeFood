package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController {


    @FXML
    void handlehomebutton (MouseEvent event) throws IOException {

        FXMLLoader home = new FXMLLoader(getClass().getResource("/org/Intro-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = home.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);
    }

}
