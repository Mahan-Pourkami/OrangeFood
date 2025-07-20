package Controller.Buyer;

import Controller.Methods;
import Controller.SceneManager;
import Model.Food;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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

public class ListFoodsController {

    @FXML
    ListView<HBox> food_list;

    private static long res_id = 0;
    private static String menu_title  = "";



   public static void set_Value(long res_id, String menu_title) {
       ListFoodsController.res_id = res_id;
       ListFoodsController.menu_title = menu_title;

   }

   public static String get_menu_title() {
       return menu_title;
   }

   @FXML
   void initialize() throws IOException{

       URL get_foods_url = new URL(Methods.url+"vendors/"+res_id+"/menu/"+menu_title);
       HttpURLConnection connection = (HttpURLConnection) get_foods_url.openConnection();
       connection.setRequestMethod("GET");
       String token = Methods.Get_saved_token();
       connection.setRequestProperty("Authorization", "Bearer "+token);

       if(connection.getResponseCode() == 200) {

           List<Food> foods = new ArrayList<>();

           JSONArray array = Methods.getJsonArrayResponse(connection);
           for (int i = 0; i < array.length(); i++) {
               JSONObject food = array.getJSONObject(i);
               foods.add(new Food(food.getLong("id"),
                       food.getString("name")
                       ,food.getString("description"),
                       food.getInt("price"),
                       food.getString("imageBase64")));
           }
           List<HBox> cards = convert_tocard(foods);
           food_list.getItems().clear();
           food_list.getItems().addAll(cards);
       }



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

   private List<HBox> convert_tocard(List<Food> foods) {
       List<HBox> cards = new ArrayList<>();
       for(Food food : foods) {
           cards.add(generate_card(food));
       }
       return cards;
   }

   @FXML
   void control_back(MouseEvent event) throws IOException {
       FXMLLoader users = new FXMLLoader(getClass().getResource("/org/Buyer/Menu-view.fxml"));
       Methods.switch_page(users,event);
   }
}
