package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;


public class LoginController {

    @FXML
    TextField phonefield ;

    @FXML
    PasswordField passwordfield ;

    @FXML
     Button loginbutton ;

    @FXML
     Button signup_button ;

    @FXML
     Hyperlink signup_link ;

    @FXML
     Label error_lable ;

    @FXML
    ImageView homebutton ;


    @FXML
    void handlehomebutton (MouseEvent event) throws IOException {

        FXMLLoader home = new FXMLLoader(getClass().getResource("/org/Intro-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = home.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);
    }

    @FXML
    void handlesignup_button (MouseEvent event) throws IOException {

        FXMLLoader signup = new FXMLLoader(getClass().getResource("/org/Signup-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = signup.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);
    }


}