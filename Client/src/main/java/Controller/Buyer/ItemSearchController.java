package Controller.Buyer;

import Controller.Methods;
import Controller.SceneManager;
import Model.Food;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ItemSearchController {

    @FXML
    TextField search_field ;

    @FXML
    TextField keywords ;

    @FXML
    Slider price_bar ;

    @FXML
    Label price_label;

    @FXML
    ListView<HBox> food_list;

    @FXML
    void initialize() throws IOException {
        loadItems("", new JSONArray(), (int) price_bar.getValue());
        price_bar.valueProperty().addListener((observable, oldValue, newValue) -> {
            int currentPrice = newValue.intValue();
            price_label.setText(String.valueOf(currentPrice));
        });
    }

    @FXML
    void Search_refresh(MouseEvent event) throws IOException {
        String search = search_field.getText().trim();
        String keywordInput = keywords.getText().trim();

        JSONArray keywordsArray = new JSONArray();
        if (!keywordInput.isEmpty()) {
            String[] keywordTokens = keywordInput.split("\\s+");
            for (String keyword : keywordTokens) {
                if (!keyword.isBlank()) {
                    keywordsArray.put(keyword);
                }
            }
        }

        loadItems(search, keywordsArray, (int) price_bar.getValue());
    }

    private void loadItems(String search, JSONArray keywordsArray, int price) throws IOException {
        URL get_foods_url = new URL(Methods.url + "items");
        String token = Methods.Get_saved_token();

        JSONObject json = new JSONObject();
        json.put("search", search);
        json.put("keywords", keywordsArray);
        json.put("price", price);

        System.out.println("Sending JSON: " + json);

        HttpURLConnection connection = (HttpURLConnection) get_foods_url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + token);
        connection.setDoOutput(true);

        try (var os = connection.getOutputStream()) {
            byte[] input = json.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        if (connection.getResponseCode() == 200) {
            List<Food> foods = new ArrayList<>();
            JSONArray array = Methods.getJsonArrayResponse(connection);
            for (int i = 0; i < array.length(); i++) {
                JSONObject food = array.getJSONObject(i);
                foods.add(new Food(
                        food.getLong("id"),
                        food.getString("name"),
                        food.getString("description"),
                        food.getInt("price"),
                        food.getString("imageBase64")
                ));
            }
            List<HBox> cards = convert_tocard(foods);
            food_list.getItems().setAll(cards);
            price_label.setText(Integer.toString(price));
        }
    }




    private List<HBox> convert_tocard(List<Food> foods) {
        List<HBox> cards = new ArrayList<>();
        for(Food food : foods) {
            cards.add(generate_card(food));
        }
        return cards;
    }
    @FXML
    private HBox generate_card(Food food) {
        HBox card = new HBox(10);
        card.setPadding(new Insets(18));

        ImageView image = new ImageView(food.getLogo());
        image.setFitHeight(150);
        image.setFitWidth(150);
        Rectangle clip = new Rectangle(
                image.getFitWidth(),
                image.getFitHeight()
        );
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        image.setClip(clip);

        VBox textVBox = new VBox(10);
        Label name = new Label(food.getName());
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 30px;");
        Label description = new Label(food.getDescription());
        description.setStyle("-fx-text-fill: #8d8383; -fx-font-size: 15px; -fx-font-weight: bold;");
        name.setPadding(new Insets(20));
        description.setPadding(new Insets(5, 20, 5, 20));
        textVBox.getChildren().addAll(name, description);


        Label price = new Label("price :"+food.getPrice() + "$");
        price.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: #900a53");
        price.setPadding(new Insets(20));
        VBox vbox3 = new VBox(10);
        vbox3.setPadding(new Insets(20));
        vbox3.getChildren().addAll(price);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        card.getChildren().addAll(image, textVBox, spacer,vbox3);
        card.setSpacing(10);
        card.setOnMouseClicked((MouseEvent event) -> {
            ItemDetailsController.setItemId(food.getId());
            FXMLLoader users = new FXMLLoader(getClass().getResource("/org/Buyer/Itemdetails-view.fxml"));
            try {
                Methods.switch_page(users,event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return card;
    }

    @FXML
    void control_back(MouseEvent event) throws IOException {
        FXMLLoader users = new FXMLLoader(getClass().getResource("/org/Buyer/Home-view.fxml"));
        Methods.switch_page(users,event);
    }
}
