package Controller.Vendor;

import Controller.Methods;
import Controller.SceneManager;
import Model.Food;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FoodManageController {


    @FXML
    TableView<Food> food_table;

    @FXML
    TableColumn<Food, String> name_col;

    @FXML
    TableColumn<Food, Integer> price_col;

    @FXML
    TableColumn<Food, Integer> sup_col;

    @FXML
    TableColumn<Food, String> des_col;

    @FXML
    TableColumn<Food, Long> id_col;

    @FXML
    TableColumn<Food, Long> res_col;

    @FXML
    TableColumn<Food, Void> action_col;



    private static long res_id ;

    @FXML
    void initialize() throws IOException {

        URL get_restaurantinfo = new URL(Methods.url+"restaurants/mine");
        HttpURLConnection connection = (HttpURLConnection) get_restaurantinfo.openConnection();
        connection.setRequestMethod("GET");
        String token = Methods.Get_saved_token();
        connection.setRequestProperty("Authorization", "Bearer "+token);

        int http_code = connection.getResponseCode();

        if(http_code == 200) {
            JSONObject obj = Methods.getJsonResponse(connection);
            res_id= obj.getLong("id");
        }

        initialize_columns();
        load_data();
    }

    @FXML
    void initialize_columns() {

        name_col.setCellValueFactory( new PropertyValueFactory<>("name"));
        price_col.setCellValueFactory( new PropertyValueFactory<>("price"));
        sup_col.setCellValueFactory( new PropertyValueFactory<>("quantity"));
        des_col.setCellValueFactory( new PropertyValueFactory<>("description"));
        id_col.setCellValueFactory( new PropertyValueFactory<>("id"));
        res_col.setCellValueFactory( new PropertyValueFactory<>("res_id"));


    }

    @FXML
    void load_data() throws IOException {

        URL get_food_url = new URL(Methods.url+"restaurants/items");
        HttpURLConnection connection = (HttpURLConnection) get_food_url.openConnection();
        connection.setRequestMethod("GET");
        String token = Methods.Get_saved_token();
        connection.setRequestProperty("Authorization", "Bearer "+token);

        int http_code = connection.getResponseCode();

        if(http_code == 200) {

            JSONArray array = Methods.getJsonArrayResponse(connection);

            List<Food> foods = new ArrayList<>();

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);

                Food food = new Food(
                        obj.getLong("id"),
                        obj.getLong("res_id"),
                        obj.getString("name"),
                        obj.getString("description"),
                        obj.getInt("price"),
                        obj.getInt("supply")
                );

                foods.add(food);
            }

            food_table.getItems().clear();
            food_table.getItems().addAll(foods);
        }

    }



    @FXML
    void control_back(MouseEvent event) throws IOException {

        FXMLLoader users = new FXMLLoader(getClass().getResource("/org/Vendor-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = users.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);
    }

    @FXML
    void control_save_food(MouseEvent event) throws IOException {




    }
}
