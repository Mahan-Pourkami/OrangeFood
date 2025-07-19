package Controller.Buyer;

import Controller.Methods;
import Controller.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;

public class ItemDetailsController {

    @FXML
    ImageView food_image;

    @FXML
    Label name_label;

    @FXML
    Label price_label;

    @FXML
    Label des_label;

    @FXML
    Label key_label;

    @FXML
    Label cat_label;


    private static long item_id = 0;




    
    @FXML
    void initialize() throws IOException {

        cat_label.setText("Category :" + ListFoodsController.get_menu_title());
        food_image.setFitHeight(250);
        food_image.setFitWidth(250);
        Rectangle clip = new Rectangle(
                food_image.getFitWidth(),
                food_image.getFitHeight()
        );
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        food_image.setClip(clip);

        URL get_item = new URL(Methods.url+"items/"+item_id);
        HttpURLConnection connection = (HttpURLConnection) get_item.openConnection();
        connection.setRequestMethod("GET");
        String token = Methods.Get_saved_token();
        connection.setRequestProperty("Authorization", "Bearer "+token);

        JSONObject obj = Methods.getJsonResponse(connection);

       if(connection.getResponseCode() == 200) {

            name_label.setText(obj.getString("name"));
            price_label.setText(String.valueOf(obj.getInt("price")+"$"));
            des_label.setText(obj.getString("description"));
            food_image.setImage(new Image(obj.getString("imageBase64")));
            JSONArray keys = obj.getJSONArray("keywords");
            String keyword = "";
            for (int i = 0; i < keys.length(); i++) {
                keyword += "-" + keys.getString(i);
            }
            key_label.setText(keyword);

        }
       else {
           SceneManager.showErrorAlert("Error", obj.getString("error"));
       }
    }


    @FXML
    void control_back(MouseEvent event)throws IOException {

        FXMLLoader users = new FXMLLoader(getClass().getResource("/org/Buyer/ListFoods-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = users.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);

    }

    static void setItemId(long item_id) {

        ItemDetailsController.item_id = item_id;
    }
}
