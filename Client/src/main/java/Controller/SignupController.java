package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Objects;

public class SignupController {



    @FXML
    TextField phonefield;

    @FXML
    TextField namefield;

    @FXML
    TextField passfield ;

    @FXML
    TextField emailfield;

    @FXML
    TextField addfield;

    @FXML
    TextField banknamefield;

    @FXML
    TextField accountnumberfield;

    @FXML
    ComboBox rolechooser;

    @FXML
    Button signupbutton;

    @FXML
    Hyperlink loginlink;


    @FXML
    void handleloginlink(MouseEvent event) throws IOException {

        FXMLLoader newView = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/org/Login-view.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = newView.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);

    }








}
