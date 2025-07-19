package Controller.Buyer;

import Controller.Methods;
import Controller.SceneManager;
import Model.Menu;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ViewMenuController {


    @FXML
    ListView<HBox> menu_list;

    @FXML
    Label res_name ;

    private static long res_id = 0 ;



    @FXML
    void initialize() throws IOException {

        URL menu_req = new URL(Methods.url+"vendors/"+res_id);
        HttpURLConnection connection = (HttpURLConnection) menu_req.openConnection();
        connection.setRequestMethod("GET");
        String token = Methods.Get_saved_token();
        connection.setRequestProperty("Authorization", "Bearer "+token);

        int http_code = connection.getResponseCode();

        if (http_code == 200) {
            List<Menu> menuList = new ArrayList<>();
            JSONObject obj = Methods.getJsonResponse(connection);
            JSONArray menu_titles = obj.getJSONArray("menu_titles");
            JSONObject vendor = obj.getJSONObject("vendor");
            res_name.setText(vendor.getString("name"));
            for (int i = 0; i < menu_titles.length(); i++) {
                menuList.add(new Menu(menu_titles.getString(i),ViewMenuController.res_id));
            }

            List<HBox> cards = convert_to_card(menuList);
            menu_list.getItems().clear();
            menu_list.getItems().addAll(cards);
        }
        else {
            JSONObject response = Methods.getJsonResponse(connection);
            SceneManager.showErrorAlert("Error", response.getString("error"));
        }
    }

    private HBox generate_card  (Menu menu) {

        HBox card = new HBox(18);
        Label title = new Label(menu.getTitle());
        title.setStyle("-fx-font-weight: bold ; -fx-font-size: 18px;");
        title.setPadding(new Insets(18));
        title.setAlignment(Pos.BASELINE_CENTER);

        card.getChildren().add(title);
        card.setOnMouseClicked((MouseEvent event) -> {

            ListFoodsController.set_Value(menu.getVendorId(),menu.getTitle());
            FXMLLoader users = new FXMLLoader(getClass().getResource("/org/ListFoods-view.fxml"));
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
        return card;
    }



    public static void setRes_id(long res_id) {
        ViewMenuController.res_id = res_id;
    }


    @FXML
    void control_back(MouseEvent event) throws IOException {

        FXMLLoader users = new FXMLLoader(getClass().getResource("/org/SearchRestaurants-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = users.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);

    }

    List <HBox> convert_to_card(List<Menu> menus) {

        List <HBox> cards = new ArrayList<>();
        for (Menu menu : menus) {
            cards.add(generate_card(menu));
        }
        return cards;
    }

}
