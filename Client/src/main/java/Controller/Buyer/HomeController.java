package Controller.Buyer;

import Controller.Methods;
import Controller.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HomeController {


    @FXML
    void handlehomebutton (MouseEvent event) throws IOException {

        FXMLLoader home = new FXMLLoader(getClass().getResource("/org/Intro-view.fxml"));
        Methods.switch_page(home,event);
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
    void handleOrderbutton (MouseEvent event) throws IOException {
        FXMLLoader order = new FXMLLoader(getClass().getResource("/org/Buyer/BuyerOrder-view.fxml"));
        Methods.switch_page(order,event);
    }

    @FXML
    void handleprofilebutton (MouseEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Profile-view.fxml"));
        Methods.switch_page(loader,event);
    }

    private void redirectToLogin(MouseEvent event) throws IOException {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Login-view.fxml"));
            Methods.switch_page(loader,event);
    }

    @FXML
    void handlecharge(MouseEvent event) throws IOException {

        FXMLLoader users = new FXMLLoader(getClass().getResource("/org/Buyer/Wallet-view.fxml"));
        Methods.switch_page(users,event);
    }

    @FXML
    void handleSearchVendor(MouseEvent event) throws IOException {
        FXMLLoader users = new FXMLLoader(getClass().getResource("/org/Buyer/SearchRestaurants-view.fxml"));
        Methods.switch_page(users,event);
    }
    @FXML
    void handleFavorites (MouseEvent event) throws IOException {

        FXMLLoader users = new FXMLLoader(getClass().getResource("/org/Buyer/Favorite-view.fxml"));
        Methods.switch_page(users,event);
    }
    @FXML
    void handleSearchItem (MouseEvent event) throws IOException {
        FXMLLoader users = new FXMLLoader(getClass().getResource("/org/Buyer/ItemSearch-view.fxml"));
        Methods.switch_page(users,event);
    }
}
