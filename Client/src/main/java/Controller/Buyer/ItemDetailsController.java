package Controller.Buyer;

import Controller.Methods;
import Controller.SceneManager;
import Model.Rating;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

    @FXML
    ProgressBar rating_avg;

    @FXML
    ListView<VBox> comments_list ;

    private static long item_id = 0;

    @FXML
    void initialize() throws IOException {

        rating_avg.setStyle("-fx-accent: #900a53;");
        List<Rating> ratings = get_ratings_data();
        List<VBox> comment_boxes = convert_tocard(ratings);
        comments_list.setPadding(new Insets(10, 10, 10, 10));
        comments_list.getItems().clear();
        comments_list.getItems().addAll(comment_boxes);
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
        Methods.switch_page(users,event);
    }

    @FXML
    void post_contorl(MouseEvent event) throws IOException {
        FXMLLoader users = new FXMLLoader(getClass().getResource("/org/Buyer/PostRating-view.fxml"));
        Methods.switch_page(users,event);
    }

    @FXML
    VBox create_card (Rating rating) throws IOException {

        VBox card = new VBox(15);
        HBox images_box = new HBox(20);
        images_box.setPadding(new Insets(10,10,10,10));
        HBox name_box = new HBox(2);
        Label name = new Label("     USer :  " + rating.getUser_id()+ "          " + rating.getTime() + "    Rating :" + rating.getRating() + "   ");
        Button edit = new Button("Edit");
        Button delete = new Button("Delete");
        name_box.setSpacing(5);
        edit.getStyleClass().add("edit-button");
        delete.getStyleClass().add("delete-button");

        if(rating.getYours().equals("no")) {
            edit.setVisible(false);
            delete.setVisible(false);
        }

        Image prof = new Image(rating.getProf());
        ImageView profile_view = new ImageView(prof);
        profile_view.setFitHeight(40);
        profile_view.setFitWidth(40);
        Rectangle clip = new Rectangle(
                profile_view.getFitWidth(),
                profile_view.getFitHeight()
        );
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        profile_view.setClip(clip);

        name.setStyle("-fx-font-weight: bold; -fx-text-fill: #05304e;");
        name_box.getChildren().addAll(profile_view,name ,edit , delete);
        List<ImageView> images = new ArrayList<>();
        for(String img_url : rating.getImages()) {

            Image img = new Image(img_url);
            ImageView image = new ImageView(img);
            image.setFitHeight(100);
            image.setFitWidth(100);
            Rectangle edge = new Rectangle(
                    image.getFitWidth(),
                    image.getFitHeight()
            );
            edge.setArcWidth(20);
            edge.setArcHeight(20);
            image.setClip(edge);

            images.add(image);
        }
        images_box.getChildren().setAll(images);
        images_box.getStyleClass().add("dark-button");
        HBox  comment_box = new HBox(5);
        comment_box.setPadding(new Insets(10,10,10,10));
        comment_box.setSpacing(10);
        Label comment_label = new Label(rating.getComment());
        comment_label.setPadding(new Insets(10,10,10,10));
        comment_label.setStyle("-fx-text-fill: gray ; -fx-font-size: 16px ; -fx-font-weight: bold ;");
        comment_box.getChildren().add(comment_label);
        card.getChildren().addAll(name_box,images_box,comment_box);
        return card;
    }

    @FXML
    List<VBox> convert_tocard(List<Rating> ratings) throws IOException {
        List<VBox> cards = new ArrayList<>();
        for(Rating rating : ratings) {
            cards.add(create_card(rating));
        }
        return cards;
    }

    @FXML
    List<Rating> get_ratings_data() throws IOException, JSONException {

        List<Rating> ratings = new ArrayList<>();
        URL get_data = new URL(Methods.url+"rating/items/"+item_id);
        HttpURLConnection connection = (HttpURLConnection) get_data.openConnection();
        connection.setRequestMethod("GET");
        String token = Methods.Get_saved_token();
        connection.setRequestProperty("Authorization", "Bearer "+token);

        if(connection.getResponseCode() == 200) {

            JSONObject response = Methods.getJsonResponse(connection);
            rating_avg.setProgress(response.getDouble("avg_rating")/5);
            JSONArray array = response.getJSONArray("comments");
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                List<String> images = new ArrayList<>();
                JSONArray photos = obj.getJSONArray("imageBase64");
                for (int j = 0; j < photos.length(); j++) {
                    images.add(photos.getString(j));
                }

                ratings.add(new Rating(obj.getLong("id"),
                        obj.getInt("item_id"),
                        obj.getString("comment"),
                        obj.getInt("rating"),
                        images,obj.getString("user_id"),
                        obj.getString("created_at"),
                        obj.getString("profile"),
                        obj.getString("yours")));
            }
        }
        else{
            JSONObject obj = Methods.getJsonResponse(connection);
            SceneManager.showErrorAlert("Error", obj.getString("error"));
        }


        return ratings;
    }

    static void setItemId(long item_id) {
        ItemDetailsController.item_id = item_id;
    }
    static long getItemId() {return ItemDetailsController.item_id;}

}
