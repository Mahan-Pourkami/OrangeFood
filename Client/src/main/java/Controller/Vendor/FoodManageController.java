package Controller.Vendor;

import Controller.Methods;
import Controller.SceneManager;
import Model.Coupon;
import Model.Food;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @FXML
    TextField name_field;

    @FXML
    TextField price_field;

    @FXML
    TextField sup_field;

    @FXML
    TextField des_field;

    @FXML
    TextArea key_box;

    @FXML
    Label error_label;

    @FXML
    Button profchooser;

    @FXML
    ImageView prof_view;


    Image default_img = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/asset/images/vendoricon.png")));
    URL resourceUrl = getClass().getResource("/asset/images/vendoricon.png");
    String image_path = "";
    private static long res_id ;


    @FXML
    void initialize() throws IOException, URISyntaxException {

        prof_view.setImage(default_img);
        String image_path = new File(resourceUrl.toURI()).getAbsolutePath();
        System.out.println(image_path);
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
        setupActionColumn();

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

    private void setupActionColumn() {

        action_col.setCellFactory(new Callback<>() {

            @Override
            public TableCell<Food, Void> call(final TableColumn<Food, Void> param) {

                return new TableCell<>() {
                    private final Button editBtn = new Button("Edit");
                    private final Button deleteBtn = new Button("Delete");
                    private final HBox pane = new HBox(5, editBtn, deleteBtn);

                    {

                        editBtn.getStyleClass().add("edit-button");
                        deleteBtn.getStyleClass().add("delete-button");
                        pane.setAlignment(Pos.CENTER);

                        editBtn.setOnAction(event -> {
                           Food food = getTableView().getItems().get(getIndex());
                            try {
                                handleEditFood(food,event);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });

                        deleteBtn.setOnAction(event -> {
                            Food coupon = getTableView().getItems().get(getIndex());
                            try {
                                handleDeleteFood(coupon);
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


    void handleDeleteFood(Food food) throws IOException {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Food: " + food.getName());
        alert.setContentText("Are you sure you want to delete this Food?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {

                try {
                    URL delete_url = new URL(Methods.url+"restaurants/" + food.getRes_id() + "/item/" + food.getId());
                    HttpURLConnection connection = (HttpURLConnection) delete_url.openConnection();
                    String token = Methods.Get_saved_token();
                    connection.setRequestProperty("Authorization", "Bearer " + token);
                    connection.setRequestMethod("DELETE");
                    connection.setDoOutput(true);

                    if(connection.getResponseCode() >= 200 && connection.getResponseCode() < 300) {

                        food_table.getItems().remove(food);
                    }

                    else if(connection.getResponseCode() >= 401) {
                        SceneManager.showErrorAlert("Failed" , "Unauthorized Request");

                    }

                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    void handleEditFood(Food food, ActionEvent event) throws IOException {


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


        URL save_food_url = new URL(Methods.url+"restaurants/"+res_id+"/item");
        HttpURLConnection connection = (HttpURLConnection) save_food_url.openConnection();
        connection.setRequestMethod("POST");
        String token = Methods.Get_saved_token();
        connection.setRequestProperty("Authorization", "Bearer "+token);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        JSONObject obj = new JSONObject();
        obj.put("name",name_field.getText());
        obj.put("price",Integer.parseInt(price_field.getText()));
        obj.put("supply",Integer.parseInt(sup_field.getText()));
        obj.put("imageBase64",image_path);
        obj.put("description",des_field.getText());
        List<String> keywords = List.of(key_box.getText().split(" "));
        JSONArray array = new JSONArray();
        for (String keyword : keywords) {
            array.put(keyword);
        }
        obj.put("keywords",array);



        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = obj.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }


        int http_code = connection.getResponseCode();


        JSONObject response = Methods.getJsonResponse(connection);

        if(http_code == 200) {
            refresh(event);
        }
        else if(http_code == 401) {
            SceneManager.showErrorAlert("Unauthorized", "Invalid Token");
        }
        else {
            error_label.setText(response.getString("error"));
        }
    }

    @FXML
    void refresh(MouseEvent event) throws IOException {

        FXMLLoader users = new FXMLLoader(getClass().getResource("/org/FoodManage-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = users.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);
    }

    @FXML
    private void handleSelectImage() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        Stage stage = (Stage) profchooser.getScene().getWindow();

        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {

                image_path = selectedFile.getAbsolutePath();

                Image image = new Image(selectedFile.toURI().toString(),640,640,true,true);
                 prof_view.setImage(image);

                prof_view.setPreserveRatio(true);
            } catch (Exception e) {
                System.err.println("Error loading image: " + e.getMessage());
            }
        }
    }
}
