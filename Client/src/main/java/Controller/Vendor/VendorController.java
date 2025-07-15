package Controller.Vendor;

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

public class VendorController {



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

    @FXML
    void handle_item_button (MouseEvent event) throws IOException {

            FXMLLoader users = new FXMLLoader(getClass().getResource("/org/FoodManage-view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent root = users.load();
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
    void handle_add_restaurant_button (MouseEvent event) throws IOException {

        URL check_have_restaurant_url = new URL(Methods.url+"restaurants/mine");
        HttpURLConnection connection = (HttpURLConnection) check_have_restaurant_url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + Methods.Get_saved_token());

        int http_code = connection.getResponseCode();

        if(http_code == 200){
            SceneManager.showErrorAlert("Conflict Request" , "Restaurant Added before");
        }
    }

}
