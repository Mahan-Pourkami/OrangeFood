package Controller.Vendor;

import Controller.Methods;
import Controller.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MenuManageController {


    @FXML
    TextField title_field ;

    @FXML
    Button submit_button;

    @FXML
    ListView<HBox> list_menu;


    @FXML
    void initialize() throws IOException {

        URL get_menu_request = new URL(Methods.url+"restaurants/menu");
        HttpURLConnection connection = (HttpURLConnection) get_menu_request.openConnection();
        connection.setRequestMethod("GET");
        String token = Methods.Get_saved_token();
        connection.setRequestProperty("Authorization", "Bearer "+token);

        JSONArray array = Methods.getJsonArrayResponse(connection);
        List<HBox> menu_list = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            menu_list.add(create_card(array.getString(i)));
        }
        list_menu.getItems().clear();
        list_menu.getItems().addAll(menu_list);
    }


    @FXML
    private HBox create_card(String menu_title) {

        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setPadding(new Insets(5, 10, 5, 10));
        hbox.setSpacing(10);

        Label label = new Label(menu_title);
        label.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(label, Priority.ALWAYS);


        HBox buttonBox = new HBox(5);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);


        Button viewButton = new Button("View");
        Button deleteButton = new Button("Delete");

        viewButton.setOnAction(event -> {
            try {
                handle_view_button(event,menu_title);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        deleteButton.setOnAction(event -> {
            try {
                handle_delete_button(event,menu_title);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        hbox.getStyleClass().add("card-container");
        label.getStyleClass().add("card-title");
        viewButton.getStyleClass().add("view-button");
        deleteButton.getStyleClass().add("delete-button");
        buttonBox.getChildren().addAll(viewButton, deleteButton);
        hbox.getChildren().addAll(label, buttonBox);

        return hbox;
    }

    @FXML
    void handle_add_menu(MouseEvent event) throws IOException {

        URL add_url = new URL(Methods.url+"restaurants/" + Methods.get_restaurant_id()+"/menu");
        HttpURLConnection connection = (HttpURLConnection) add_url.openConnection();
        connection.setRequestMethod("POST");
        String token = Methods.Get_saved_token();
        connection.setRequestProperty("Authorization", "Bearer "+token);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        JSONObject obj = new JSONObject();
        obj.put("title", title_field.getText());

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = obj.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        JSONObject response = Methods.getJsonResponse(connection);

        int http_code = connection.getResponseCode();
        if (http_code == 200) {
            refresh(event);
        }
        else {
            SceneManager.showErrorAlert("Error", response.getString("error"));
        }

    }


    @FXML
    void handle_view_button(ActionEvent event , String menu_title) throws IOException {

        AddFoodMenuController.SetMenu_title(menu_title);
        FXMLLoader users = new FXMLLoader(getClass().getResource("/org/Vendor/AddFoodtoMenu-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = users.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);
    }

    @FXML
    void handle_delete_button(ActionEvent event , String title) throws IOException {

        URL delete_url = new URL(Methods.url + "restaurants/" + Methods.get_restaurant_id() + "/menu/" + title);
        HttpURLConnection connection = (HttpURLConnection) delete_url.openConnection();
        connection.setRequestMethod("DELETE");
        String token = Methods.Get_saved_token();
        connection.setRequestProperty("Authorization", "Bearer " + token);

        int http_code = connection.getResponseCode();
        if (http_code == 200) {
            refresh(event);
        } else {
            SceneManager.showErrorAlert("Error", Methods.getJsonResponse(connection).getString("error"));
        }
    }

    @FXML
    void control_back(MouseEvent event) throws IOException {

        FXMLLoader users = new FXMLLoader(getClass().getResource("/org/Vendor/Vendor-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = users.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);
    }

    @FXML
    void refresh(ActionEvent event) throws IOException {

        FXMLLoader users = new FXMLLoader(getClass().getResource("/org/Vendor/MenuManage-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = users.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);
    }

    @FXML
    void refresh(MouseEvent event) throws IOException {

        FXMLLoader users = new FXMLLoader(getClass().getResource("/org/Vendor/MenuManage-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = users.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);
    }

}
