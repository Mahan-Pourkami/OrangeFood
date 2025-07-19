package Controller.Vendor;

import Controller.Methods;
import Controller.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class VendorController {


    @FXML
    Label add_text;

    @FXML
    Button add_button;

    @FXML
    ImageView plus_image;


    @FXML
    void initialize() throws IOException {

        Long res_id = Methods.get_restaurant_id();

        if (res_id != null) {
            add_text.setVisible(false);
            add_button.setVisible(false);
            plus_image.setVisible(false);
        }

    }



    @FXML
    void handlelogoutbutton (MouseEvent event) throws IOException {

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

    @FXML
    void handle_item_button (MouseEvent event) throws IOException {


        if(Methods.get_restaurant_id() == null){
            SceneManager.showErrorAlert("No Restaurant" , "First submit your restaurant ");
            return;
        }

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

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/AddRestaurant-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = loader.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);
    }

    @FXML
    void handle_menu_button (MouseEvent event) throws IOException {

        if(Methods.get_restaurant_id() == null){
            SceneManager.showErrorAlert("No Restaurant" , "First submit your restaurant ");
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/MenuManage-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = loader.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);

    }

    @FXML
    void handle_edit_button (MouseEvent event) throws IOException {

        if(Methods.get_restaurant_id() == null){
            SceneManager.showErrorAlert("No Restaurant" , "First submit your restaurant ");
            return;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/UpdateRestaurant-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = loader.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);

    }

}
