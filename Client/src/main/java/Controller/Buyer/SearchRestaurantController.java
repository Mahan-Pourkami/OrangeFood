package Controller.Buyer;

import Controller.Methods;
import Controller.SceneManager;
import Model.Vendor;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;


import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SearchRestaurantController {


    @FXML
    TextField search_field ;

    @FXML
    TextField key_area ;

    @FXML
    ListView<HBox> res_list;

    @FXML
    void initialize() throws IOException {


        search_field.setOnKeyPressed(event -> {
           if(event.getCode() == KeyCode.ENTER) {
                try {
                    initialize();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        key_area.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER) {
                try {
                    initialize();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });



        URL get_restaurants = new URL(Methods.url+"vendors");
        HttpURLConnection connection = (HttpURLConnection) get_restaurants.openConnection();

        connection.setRequestMethod("POST");
        String token = Methods.Get_saved_token();

        connection.setRequestProperty("Authorization", "Bearer "+token);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        JSONObject obj = new JSONObject();
        obj.put("search", search_field.getText());
        JSONArray arr = new JSONArray();
        List<String> keywords = List.of(key_area.getText().split("-"));
        for (String keyword : keywords) {
            arr.put(keyword);
        }
        obj.put("keywords", arr);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = obj.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int http_code = connection.getResponseCode();

        if (http_code == 200) {

            List<Vendor> vendors = new ArrayList<>();

            JSONArray array  = Methods.getJsonArrayResponse(connection);
            for (int i = 0; i < array.length(); i++) {
                JSONObject json = array.getJSONObject(i);
                vendors.add(new Vendor(
                        json.getString("name"),
                        json.getString("address"),
                        json.getLong("id"),
                        json.getString("logoBase64"),
                        json.getString("favorite :")
                ));
            }
            List<HBox> cards = convert_to_card(vendors);
            res_list.getItems().clear();
            res_list.getItems().addAll(cards);
        }

    }


    private HBox create_cell (Vendor vendor) {

        HBox cell = new HBox(10);
        cell.setPadding(new Insets(18));
        cell.setSpacing(10);
        VBox vbox = new VBox(10);
        ImageView image = new ImageView(vendor.getLogo());
        Label label = new Label(vendor.getName());
        Label address = new Label(vendor.getAddress());
        address.setStyle("-fx-text-fill: #a88787; -fx-font-weight: bold ; -fx-font-size: 16px;");
        label.setPadding(new Insets(20));
        address.setPadding(new Insets(5,20,5,20));
        vbox.getChildren().addAll(label,address);
        label.setStyle("-fx-font-weight: bold ; -fx-font-size: 24px;");
        image.setFitHeight(100);
        image.setFitWidth(100);
        Rectangle clip = new Rectangle(
                image.getFitWidth(),
                image.getFitHeight()
        );
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        image.setClip(clip);
        image.setPreserveRatio(true);
        Button favorite = new Button("Add to Favorite");
        favorite.setOnAction(event -> {

            try {
                URL add_to_favorite = new URL(Methods.url+"favorites/"+vendor.getId());
                HttpURLConnection connection = (HttpURLConnection) add_to_favorite.openConnection();
                connection.setRequestMethod("PUT");
                String token = Methods.Get_saved_token();
                connection.setRequestProperty("Authorization", "Bearer "+token);

                int http_code = connection.getResponseCode();
                if (http_code == 200) {
                    initialize();
                }
                else {
                    JSONObject obj = Methods.getJsonResponse(connection);
                    SceneManager.showErrorAlert("Error",obj.getString("error"));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        favorite.getStyleClass().add("view-button");

        if(vendor.getFavorite().equals("yes")) favorite.setVisible(false);


        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        cell.getChildren().addAll(image,vbox,spacer,favorite);
        cell.setOnMouseClicked(event -> {

            ViewMenuController.setRes_id(vendor.getId());
            FXMLLoader users = new FXMLLoader(getClass().getResource("/org/Buyer/Menu-view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent root = null;
            try {
                root = users.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Scene scene = new Scene(root);
            SceneManager.fadeScene(stage, scene);

        });

        return cell;
    }

    private List<HBox> convert_to_card(List<Vendor> vendors) {
        List<HBox> cells = new ArrayList<>();
        for (Vendor vendor : vendors) {
            cells.add(create_cell(vendor));
        }
        return cells;
    }



    @FXML
    void refresh(MouseEvent event) throws IOException {
        initialize();
    }

    @FXML
    void control_back(MouseEvent event) throws IOException {

        FXMLLoader users = new FXMLLoader(getClass().getResource("/org/Buyer/Home-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = users.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);

    }


}
