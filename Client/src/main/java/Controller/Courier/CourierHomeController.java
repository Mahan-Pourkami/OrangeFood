package Controller.Courier;

import Controller.Methods;
import Controller.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class CourierHomeController {

    @FXML
    void handlelogoutbutton  (MouseEvent event) throws IOException {

        try{
            String token = Methods.Get_saved_token();
            if(token == null || token.isEmpty()){
                redirectToLogin(event);
                return;
            }

            URL logouturl = new URL(Methods.url+"auth/logout");
            HttpURLConnection connection = (HttpURLConnection) logouturl.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + token);

            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                SceneManager.showErrorAlert("Unauthorized" , "Invalid Token");
            }
            redirectToLogin(event);
        }
        catch(Exception e){
            SceneManager.showErrorAlert("Connection failed" , "Cannot connect to server");
            redirectToLogin(event);
        }

    }
    @FXML
    void handleprofilebutton (MouseEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Profile-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = loader.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);

    }

    private void redirectToLogin(MouseEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Login-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = loader.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);

    }

    @FXML
    void handleAvailable (MouseEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Courier/AvailableDeliv-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = loader.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);
    }

    @FXML
    void handlePending (MouseEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Courier/PendingOrder-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = loader.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);
    }


}
