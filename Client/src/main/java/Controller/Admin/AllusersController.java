package Controller.Admin;

import Controller.Methods;
import Controller.SceneManager;
import Model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AllusersController {

    @FXML
    private TableView<User> userstable;

    @FXML
    private TableColumn<User, String> phone_col;

    @FXML
    private TableColumn<User, String> id_col;

    @FXML
    private TableColumn<User, String> name_col;

    @FXML
    private TableColumn<User, String> email_col;

    @FXML
    private TableColumn<User, String> role_col;



    private final ObservableList<User> users = FXCollections.observableArrayList();

    @FXML
    void initialize() throws IOException {

        phone_col.setCellValueFactory(new PropertyValueFactory<>("phone"));
        id_col.setCellValueFactory(new PropertyValueFactory<>("id"));
        name_col.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        email_col.setCellValueFactory(new PropertyValueFactory<>("email"));
        role_col.setCellValueFactory(new PropertyValueFactory<>("role"));


        userstable.setItems(users);
        loadUserData();
    }

    private void loadUserData() throws IOException {

        String token = Methods.Get_saved_token();
        URL getusers = new URL(Methods.url+"admin/users");
        HttpURLConnection connection = (HttpURLConnection) getusers.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + token);
        ArrayList<User> users_list = new ArrayList<>();
        if (connection.getResponseCode() == 200) {
            JSONArray jsonarray = Methods.getJsonArrayResponse(connection);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                User user = new User(
                        jsonobject.getString("phone"),
                        jsonobject.getString("id"),
                        jsonobject.getString("full_name"),
                        jsonobject.getString("email"),
                        jsonobject.getString("role")
                );
                users_list.add(user);
            }
            users.addAll(users_list);
        }
        else {
            SceneManager.showErrorAlert("Task failed" , "Cannot fetch users data");
        }
    }
    @FXML
    void control_back(MouseEvent event) throws IOException {

        FXMLLoader admin_view = new FXMLLoader(getClass().getResource("/org/Admin/Admin-view.fxml"));
        Methods.switch_page(admin_view,event);
    }
}