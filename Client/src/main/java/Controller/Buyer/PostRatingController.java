package Controller.Buyer;

import Controller.Methods;
import Controller.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PostRatingController {

    @FXML
    ListView <HBox> images_list;

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

    @FXML
    Label click_label;

    @FXML
    Slider point_slider;

    @FXML
    Label rating_value;

    @FXML
    TextArea comment_area;

    List<String> image_urls = new ArrayList<>();

    @FXML
    void initialize() throws IOException {


        setup_slider();
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
        List<HBox> cards = new ArrayList<>();

        for(String url : image_urls) {
            cards.add(load_images(url));
        }
        images_list.getItems().clear();
        images_list.getItems().addAll(cards);
        if(!image_urls.isEmpty()) {
            click_label.setVisible(false);
        }
        setup_slider();
        URL get_item = new URL(Methods.url+"items/"+ItemDetailsController.getItemId());
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

    HBox load_images(String filename) {

        Image selected_img = new Image(filename);
        HBox hbox = new HBox(10);
        hbox.setPadding(new Insets(5,10,5,10));
        ImageView imageView = new ImageView();
        imageView.setFitHeight(150);
        imageView.setFitWidth(150);
        Rectangle clip = new Rectangle(
                imageView.getFitWidth(),
                imageView.getFitHeight()
        );
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        imageView.setClip(clip);
        imageView.setImage(selected_img);
        hbox.getChildren().add(imageView);
        return hbox;
    }

    @FXML
    private void handleSelectImage() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        Stage stage = (Stage) images_list.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
               image_urls.add(selectedFile.getAbsolutePath());
               initialize();
            } catch (Exception e) {
                System.err.println("Error loading image: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleClearImages() {
        images_list.getItems().clear();
        image_urls.clear();
    }

    @FXML
    void control_back(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Buyer/Itemdetails-view.fxml"));
        Methods.switch_page(loader,event);
    }

    @FXML
    void handle_post_comment(MouseEvent event) throws IOException {

        URL post_comment_url = new URL(Methods.url+"rating");
        HttpURLConnection connection = (HttpURLConnection) post_comment_url.openConnection();
        connection.setRequestMethod("POST");
        String token = Methods.Get_saved_token();
        connection.setRequestProperty("Authorization", "Bearer "+token);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        JSONObject obj = new JSONObject();
        obj.put("comment", comment_area.getText());
        obj.put("rating",Integer.parseInt(rating_value.getText()));
        obj.put("item_id",ItemDetailsController.getItemId());
        JSONArray images = new JSONArray();
        for(String url : image_urls) {
            images.put(url);
        }
        obj.put("imageBase64", images);
        Methods.send_data(connection,obj.toString());

        int http_code = connection.getResponseCode();
        if(http_code == 200) {
            control_back(event);
        }
        else {
            JSONObject response = Methods.getJsonResponse(connection);
            SceneManager.showErrorAlert("Error", response.getString("error"));
        }
    }

    @FXML
    void setup_slider() {

        point_slider.setMin(1);
        point_slider.setMax(5);
        point_slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int value = (int) Math.round(newVal.doubleValue());
            rating_value.setText(String.valueOf(value));
            if(value>=1 && value<=2) {
                rating_value.setStyle("-fx-text-fill: red");
            }
            else if(value>=3 && value<=4) {
                rating_value.setStyle("-fx-text-fill: yellow");
            }
            else if(value>=5 && value<=6) {
                rating_value.setStyle("-fx-text-fill: #0ea80e");
            }
        });
    }
}