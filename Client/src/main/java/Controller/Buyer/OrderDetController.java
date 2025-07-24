package Controller.Buyer;

import Controller.Methods;
import Controller.SceneManager;
import Model.Food;
import Model.Role;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class OrderDetController {


    private static long order_id = 0 ;

    private static String status = "";


    @FXML
    TableView<Food> item_table;

    @FXML
    TableColumn<Food, String> name_col;

    @FXML
    TableColumn<Food, Integer> price_col;

    @FXML
    TableColumn<Food, Integer> quan_col;

    @FXML
    TableColumn<Food, Void> act_col;

    @FXML
    TextField coupon_field;

    @FXML
    Button coupon_button;

    @FXML
    Button pay_button;

    @FXML
    Label id_label;

    @FXML
    Label add_label;

    @FXML
    Label create_label;

    @FXML
    Label update_label;

    @FXML
    Label raw_label;

    @FXML
    Label  courier_label;

    @FXML
    Label addfee_label;

    @FXML
    Label tax_label;

    @FXML
    ImageView logo_view;

    @FXML
    Label couponStatus;

    List<Food> items = new ArrayList<>();

    public static String getStatus() {
        return status;
    }

    public static void setStatus(String status) {
        OrderDetController.status = status;
    }

    private static Role role ;

    public static Role getRole() {
        return role;
    }

    public static void setRole(Role role) {
        OrderDetController.role = role;
    }

    @FXML
    void setcolumns (){
        name_col.setCellValueFactory(new PropertyValueFactory<>("name"));
        price_col.setCellValueFactory(new PropertyValueFactory<>("price"));
        quan_col.setCellValueFactory(new PropertyValueFactory<>("quantity"));
    }

    private void setupActionColumn() {

        if(OrderDetController.status.equals("waiting")){
            act_col.setCellFactory(new Callback<>() {


                @Override
                public TableCell<Food, Void> call(final TableColumn<Food, Void> param) {

                    return new TableCell<>() {
                        private final Button deleteBtn = new Button("Delete");
                        private final HBox pane = new HBox(5, deleteBtn);

                        {
                            deleteBtn.getStyleClass().add("delete-button");
                            pane.setAlignment(Pos.CENTER);

                            deleteBtn.setOnMousePressed(event -> {
                                Food coupon = getTableView().getItems().get(getIndex());
                                try {
                                    handleDeleteFood(coupon, event);
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
        else {
            coupon_button.setVisible(false);
            pay_button.setVisible(false);
            coupon_field.setVisible(false);
        }
    }


    @FXML
    void handleDeleteFood(Food food , MouseEvent event) throws IOException {

        URL delete_url = new URL(Methods.url+"orders/cart/"+food.getId());
        HttpURLConnection connection = (HttpURLConnection) delete_url.openConnection();
        connection.setRequestMethod("DELETE");
        String token = Methods.Get_saved_token();
        connection.setRequestProperty("Authorization", "Bearer "+token);

        int http_code = connection.getResponseCode();
        if(http_code == 200){
            item_table.getItems().remove(food);
            if(item_table.getItems().isEmpty()){
                control_back(event);
                return;
            }
            initialize();
        }
        else {
            JSONObject response = Methods.getJsonResponse(connection);
            SceneManager.showErrorAlert("Error" , response.getString("error"));
        }

    }
    @FXML
    void control_back(MouseEvent event) throws IOException {

        if(role.equals(Role.buyer)){
            FXMLLoader back = new FXMLLoader(getClass().getResource("/org/Buyer/BuyerOrder-view.fxml"));
            Methods.switch_page(back, event);
        }
        else if(role.equals(Role.seller)){
            FXMLLoader back = new FXMLLoader(getClass().getResource("/org/Vendor/RestaurantOrder-view.fxml"));
            Methods.switch_page(back, event);
        }

    }

    public static long getOrder_id() {
        return order_id;
    }

    public static void setOrder_id(long order_id , Role role) {

        OrderDetController.order_id = order_id;
        OrderDetController.role = role;
    }

    @FXML
    void initialize() throws IOException {

        items.clear();
        setcolumns();
        setupActionColumn();
        URL get_order = new URL(Methods.url+"orders/"+order_id);
        HttpURLConnection connection = (HttpURLConnection) get_order.openConnection();
        connection.setRequestMethod("GET");
        String token = Methods.Get_saved_token();
        connection.setRequestProperty("Authorization", "Bearer "+token);
        JSONObject obj = Methods.getJsonResponse(connection);

        URL get_order_info = new URL(Methods.url + "orders/"+ OrderDetController.getOrder_id());
        HttpURLConnection connection2 = (HttpURLConnection) get_order_info.openConnection();
        connection2.setRequestMethod("GET");
        connection2.setRequestProperty("Authorization", "Bearer " + token);
        JSONObject response =  Methods.getJsonResponse(connection2);

        if(connection.getResponseCode() == 200){

            id_label.setText(String.valueOf(obj.getLong("id")));
            create_label.setText(obj.getString("created_at"));
            update_label.setText(obj.getString("updated_at"));
            raw_label.setText(String.valueOf(obj.getInt("pay_price")));
            courier_label.setText(String.valueOf(obj.getInt("courier_fee")));
            addfee_label.setText(String.valueOf(obj.getInt("additional_fee")));
            add_label.setText(obj.getString("delivery_address"));
            tax_label.setText(String.valueOf(obj.getInt("tax_fee")));
            courier_label.setText(String.valueOf(obj.getInt("courier_fee")));
            logo_view.setImage(new Image(obj.getString("restaurant_prof")));

            logo_view.setFitWidth(150);
            logo_view.setFitHeight(150);
            Rectangle clip = new Rectangle(
                    logo_view.getFitWidth(),
                    logo_view.getFitHeight()
            );
            clip.setArcWidth(20);
            clip.setArcHeight(20);
            logo_view.setClip(clip);


            JSONArray arr = obj.getJSONArray("items");
            for (int i = 0; i < arr.length(); i++) {
                JSONObject item = arr.getJSONObject(i);
                items.add(new Food(item.getLong("id"), item.getString("name"), item.getInt("price"), item.getInt("quantity")));
            }

            item_table.getItems().clear();
            item_table.getItems().addAll(items);
            if(!response.get("status").equals("waiting")){
                pay_button.setVisible(false);
            }
        }
        else SceneManager.showErrorAlert("Error" , obj.getString("error"));
    }

    @FXML
    void handleCoupon (MouseEvent event) throws IOException{
        JSONObject json = new JSONObject();
        json.put("order_id", order_id);
        json.put("coupon_code", coupon_field.getText());
        json.put("price",Integer.parseInt(raw_label.getText()));
        URL setcoupon = new URL(Methods.url+"orders/setcoupon");
        HttpURLConnection connection = (HttpURLConnection) setcoupon.openConnection();
        connection.setRequestMethod("POST");
        String token = Methods.Get_saved_token();
        connection.setRequestProperty("Authorization", "Bearer "+ token);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        try (var os = connection.getOutputStream()) {
            byte[] input = json.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        JSONObject response = Methods.getJsonResponse(connection);
        System.out.println(response.toString());
        if(connection.getResponseCode() == 200){
            couponStatus.setText(response.getString("message"));
            couponStatus.setStyle("-fx-text-fill: green;");

        }
        else{
            couponStatus.setText(response.getString("error"));
            couponStatus.setStyle("-fx-text-fill: red;");


        }
        couponStatus.setVisible(true);
        initialize();
    }

    @FXML
    void handlePay (MouseEvent event) throws IOException{
        URL get_order_info = new URL(Methods.url + "orders/"+ OrderDetController.getOrder_id());
        HttpURLConnection connection = (HttpURLConnection) get_order_info.openConnection();
        connection.setRequestMethod("GET");
        String token = Methods.Get_saved_token();
        connection.setRequestProperty("Authorization", "Bearer " + token);
        JSONObject response =  Methods.getJsonResponse(connection);
        if(response.get("status").equals("waiting")) {
            FXMLLoader pay = new FXMLLoader(getClass().getResource("/org/Buyer/Payment-view.fxml"));
            Methods.switch_page(pay, event);
        }
    }
}
