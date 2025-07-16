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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import org.json.JSONArray;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MenuManageController {



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
            handle_view_button(event,menu_title);
        });

        hbox.getStyleClass().add("card-container");
        label.getStyleClass().add("card-title");
        viewButton.getStyleClass().add("view-button");
        deleteButton.getStyleClass().add("delete-button");
        buttonBox.getChildren().addAll(viewButton, deleteButton);
        hbox.getChildren().addAll(label, buttonBox);

        return hbox;
    }

    void handle_view_button(ActionEvent event , String menu_title) {

    }

    @FXML
    void control_back(MouseEvent event) throws IOException {

        FXMLLoader users = new FXMLLoader(getClass().getResource("/org/Vendor-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = users.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);
    }

}
