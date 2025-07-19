package Controller.Vendor;

import Controller.Methods;
import Controller.SceneManager;
import Model.Food;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.json.JSONArray;
import org.json.JSONObject;


import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AddFoodMenuController {


    private static String menu_title = "";

    @FXML
    Label title_label;

    @FXML
    TableView <Food> add_table;

    @FXML
    TableView <Food> del_table;

    @FXML
    TableColumn<Food, String> addname_col;

    @FXML
    TableColumn<Food, Long> addid_col;

    @FXML
    TableColumn<Food, String>delname_col;

    @FXML
    TableColumn<Food, Long> delid_col;

    @FXML
    TableColumn<Food, Void> addact_col;

    @FXML
    TableColumn<Food, Void> delact_col;




    @FXML
    void initialize() throws IOException {

          title_label.setText(menu_title);
          initial_columns();
          load_data_add();
          load_data_delete();
          setupactionadd();
          setupactiondel();

    }

    @FXML
    void initial_columns(){
        addname_col.setCellValueFactory(new PropertyValueFactory<>("name"));
        addid_col.setCellValueFactory(new PropertyValueFactory<>("id"));
        delname_col.setCellValueFactory(new PropertyValueFactory<>("name"));
        delid_col.setCellValueFactory(new PropertyValueFactory<>("id"));

    }

    @FXML
    void load_data_add() throws IOException{

        URL get_add_url = new URL(Methods.url+"restaurants/"+Methods.get_restaurant_id()+"/notmenu/"+menu_title);
        HttpURLConnection connection = (HttpURLConnection) get_add_url.openConnection();
        connection.setRequestMethod("GET");
        String token = Methods.Get_saved_token();
        connection.setRequestProperty("Authorization", "Bearer "+token);

        int http_code = connection.getResponseCode();

        if (http_code == 200) {

            JSONArray array = Methods.getJsonArrayResponse(connection);

            List<Food> foods = new ArrayList<>();

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                foods.add(new Food(obj.getLong("id"),
                        obj.getString("name"),
                        "",10));
            }
            add_table.getItems().clear();
            add_table.getItems().addAll(foods);
        }
    }

    @FXML
    void setupactionadd() {
      addact_col.setCellFactory(new Callback<>() {

            @Override
            public TableCell<Food, Void> call(final TableColumn<Food, Void> param) {
                return new TableCell<>() {
                    private final Button addtBtn = new Button("Add");
                    private final HBox pane = new HBox(5, addtBtn);

                    {
                        addtBtn.getStyleClass().add("view-button");
                        addtBtn.setOnAction(event -> {
                            Food food = getTableView().getItems().get(getIndex());
                            try {
                                handleAdd(event,food);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });

                    }
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(pane);
                        }
                    }
                };
            }
        });
    }


    @FXML
    void setupactiondel() {
        delact_col.setCellFactory(new Callback<>() {

            @Override
            public TableCell<Food, Void> call(final TableColumn<Food, Void> param) {
                return new TableCell<>() {
                    private final Button deltBtn = new Button("Delete");
                    private final HBox pane = new HBox(5, deltBtn);

                    {
                        deltBtn.getStyleClass().add("delete-button");
                        deltBtn.setOnAction(event -> {
                            Food food = getTableView().getItems().get(getIndex());
                            try {
                                handleDelete(event,food);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });

                    }
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(pane);
                        }
                    }
                };
            }
        });
    }



    @FXML
    void load_data_delete () throws IOException {

        URL get_add_url = new URL(Methods.url+"restaurants/"+Methods.get_restaurant_id()+"/menu/"+menu_title);
        HttpURLConnection connection = (HttpURLConnection) get_add_url.openConnection();
        connection.setRequestMethod("GET");
        String token = Methods.Get_saved_token();
        connection.setRequestProperty("Authorization", "Bearer "+token);

        int http_code = connection.getResponseCode();

        if (http_code == 200) {

            JSONArray array = Methods.getJsonArrayResponse(connection);

            List<Food> foods = new ArrayList<>();

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                foods.add(new Food(obj.getLong("id"),
                        obj.getString("name"),
                        "",10));
            }
            del_table.getItems().clear();
            del_table.getItems().addAll(foods);
        }
    }


    @FXML
    void handleAdd(ActionEvent event , Food food) throws IOException {

        URL add_to_menu_url = new URL(Methods.url+"restaurants/"+Methods.get_restaurant_id()+"/menu/"+menu_title);
        HttpURLConnection connection = (HttpURLConnection) add_to_menu_url.openConnection();
        connection.setRequestMethod("PUT");
        String token = Methods.Get_saved_token();
        connection.setRequestProperty("Authorization", "Bearer "+token);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        JSONObject obj = new JSONObject();
        obj.put("item_id", food.getId());

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = obj.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        JSONObject response = Methods.getJsonResponse(connection);

        int http_code = connection.getResponseCode();
        if (http_code == 200) {
            initialize();
        }
        else {
            SceneManager.showErrorAlert("Error", response.getString("error"));
        }
    }

    @FXML
    void handleDelete(ActionEvent event, Food food) throws IOException {
        URL delete_req_url = new URL(Methods.url+"restaurants/"+Methods.get_restaurant_id()+"/menu/"+menu_title+"/"+food.getId());
        HttpURLConnection connection = (HttpURLConnection) delete_req_url.openConnection();
        connection.setRequestMethod("DELETE");
        String token = Methods.Get_saved_token();
        connection.setRequestProperty("Authorization", "Bearer "+token);
        int http_code = connection.getResponseCode();
        JSONObject response = Methods.getJsonResponse(connection);

        if (http_code == 200) {
            initialize();
        }
        else {
            SceneManager.showErrorAlert("Error", response.getString("error"));
        }
    }


    public static void SetMenu_title(String menu_title) {
        AddFoodMenuController.menu_title = menu_title;
    }

    @FXML
    void control_back(MouseEvent event) throws IOException {

        FXMLLoader users = new FXMLLoader(getClass().getResource("/org/Vendor/MenuManage-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = users.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);
    }

    @FXML
    void refresh(ActionEvent event) throws IOException {

        FXMLLoader users = new FXMLLoader(getClass().getResource("/org/Vendor/AddFoodtoMenu-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = users.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);
    }

}
