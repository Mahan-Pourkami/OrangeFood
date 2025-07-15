package Controller;

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

public class HomeController {


    @FXML
    void handlehomebutton (MouseEvent event) throws IOException {

        FXMLLoader home = new FXMLLoader(getClass().getResource("/org/Intro-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = home.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);
    }

    @FXML
    void handlelogoutbutton (MouseEvent event) throws IOException {

        try{
            String token = Methods.Get_saved_token();
            if(token == null || token.isEmpty()){
                redirectToLogin(event);
                return;
            }

            URL logouturl = new URL("http://localhost:8080/auth/logout");
            HttpURLConnection connection = (HttpURLConnection) logouturl.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + token);

            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                redirectToLogin(event);
            }
        }
        catch(Exception e){
            SceneManager.showErrorAlert("Connection failed" , "Cannot connect to server");
            redirectToLogin(event);
        }

    }

    private void redirectToLogin(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Login-view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent root = loader.load();
            Scene scene = new Scene(root);
            SceneManager.fadeScene(stage, scene);
        } catch (IOException e) {
            SceneManager.showErrorAlert("Navigation Error", "Could not load login screen");
        }
    }

}
