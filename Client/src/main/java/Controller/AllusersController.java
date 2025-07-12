package Controller;

import Model.User;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AllusersController {


    @FXML
    TableView<User>userstable;

    @FXML
    TableColumn<User,String> phone_col;

    @FXML
    TableColumn<User,String> id_col;

    @FXML
    TableColumn<User,String> name_col;

    @FXML
    TableColumn<User,String> email_col;


    @FXML
    void initialize() throws IOException {

        URL getusers = new URL("http://localhost:8080/admin/users");
        HttpURLConnection connection = (HttpURLConnection) getusers.openConnection();
        connection.setRequestMethod("GET");

    }



}
