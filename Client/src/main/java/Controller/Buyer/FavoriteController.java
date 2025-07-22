package Controller.Buyer;

import Controller.Methods;
import Controller.SceneManager;
import Model.Vendor;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FavoriteController {

    @FXML
    ListView<HBox> res_list;

    @FXML
    void initialize() throws IOException {

        URL get_restaurants = new URL(Methods.url+"favorites");
        HttpURLConnection connection = (HttpURLConnection) get_restaurants.openConnection();
        connection.setRequestMethod("GET");
        String token = Methods.Get_saved_token();
        connection.setRequestProperty("Authorization", "Bearer "+token);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
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
                        json.getString("logoBase64")
                ));
            }
            List<HBox> cards = convert_to_card(vendors);
            res_list.getItems().clear();
            res_list.getItems().addAll(cards);
            System.out.println("done");
        }
    }
    private HBox create_cell (Vendor vendor) {

        HBox cell = new HBox(10);
        cell.setPadding(new Insets(18));
        cell.setSpacing(10);
        VBox vbox = new VBox(10);
        ImageView image ;
        try {
            image = new ImageView(vendor.getLogo());
        }
        catch (Exception e) {
            image = new ImageView(getClass().getResource("/asset/images/vendoricon.png").toExternalForm());
        }
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
        Button delete = new Button("Delete from Favorites");
        delete.getStyleClass().add("delete-button");
        delete.setOnMouseClicked((MouseEvent event) -> {

            try {
                URL add_to_favorite = new URL(Methods.url+"favorites/"+vendor.getId());
                HttpURLConnection connection = (HttpURLConnection) add_to_favorite.openConnection();
                connection.setRequestMethod("DELETE");
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
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        cell.getChildren().addAll(image,vbox,spacer,delete);
        cell.setOnMouseClicked(event -> {

            ViewMenuController.setRes_id(vendor.getId());
            FXMLLoader menu_view = new FXMLLoader(getClass().getResource("/org/Buyer/Menu-view.fxml"));
            try {
                Methods.switch_page(menu_view,event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
    void control_back(MouseEvent event) throws IOException {
        FXMLLoader hom_view = new FXMLLoader(getClass().getResource("/org/Buyer/Home-view.fxml"));
        Methods.switch_page(hom_view,event);
    }
}