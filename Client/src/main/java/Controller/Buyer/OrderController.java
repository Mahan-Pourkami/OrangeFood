package Controller.Buyer;

import Controller.Methods;
import Controller.SceneManager;
import Model.Order;
import Model.Role;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class OrderController {

    @FXML
    ListView<VBox> order_list ;

    @FXML
    TextField search_field ;

    @FXML
    TextField vendor_field ;

    @FXML
    ImageView no_order ;


    List<Order> orderlist = new ArrayList<>();

    @FXML
    void initialize() throws IOException {

        get_orders();
        List<VBox> cards = convert_to_vbox(orderlist);
        order_list.getItems().clear();
        order_list.getItems().addAll(cards);
        if(!order_list.getItems().isEmpty()) {
            no_order.setVisible(false);
        }

    }



    void get_orders() throws IOException {

        orderlist.clear();
        URL get_url = new URL(Methods.url+"orders/history?search="+search_field.getText()+"&vendor="+vendor_field.getText());
        HttpURLConnection connection = (HttpURLConnection) get_url.openConnection();
        connection.setRequestMethod("GET");
        String token = Methods.Get_saved_token();
        connection.setRequestProperty("Authorization", "Bearer "+token);

        if(connection.getResponseCode() == 200) {

            JSONArray orders = Methods.getJsonArrayResponse(connection);
            for (int i = 0; i < orders.length(); i++) {
                JSONObject obj = orders.getJSONObject(i);
                Order order = new Order(obj.getLong("id"),
                        obj.getLong("vendor_id"),
                        obj.getString("customer_id"),
                        obj.getString("delivery_address"),
                        obj.getString("status"),
                        obj.getString("created_at"),
                        new ArrayList<>(),
                        obj.getInt("pay_price"),
                        obj.getString("vendor_name"));

                JSONArray items = obj.getJSONArray("items");
                List<String> images_links = new ArrayList<>();
                for (int j = 0; j < items.length(); j++) {
                    JSONObject item = items.getJSONObject(j);
                    images_links.add(item.getString("imageBase64"));
                }
                order.setImages(images_links);
                orderlist.add(order);
            }
            }
        else {
            JSONObject response= Methods.getJsonResponse(connection);
            SceneManager.showErrorAlert("Error" , response.getString("error"));
        }
    }


    VBox create_card(Order order) {

        VBox vbox = new VBox(10);
        Label order_label = new Label("Order ID : " + order.getId());
        VBox order_info_box = new VBox(10);
        Label order_status = new Label("Order Status : " + order.getStatus());
        Label price_label = new Label("Price : " + order.getPrice());
        Label vendor_name = new Label(order.getVendor_name());
        vendor_name.setStyle("-fx-font-weight: bold ; -fx-font-size: 16px;");
        Label date = new Label(order.getCreated_at());
        List<ImageView> urls = new ArrayList<>();

        for(String image_url : order.getImages()) {
            Image image ;
            try {
                image = new Image(image_url);
            }
            catch(Exception e) {
                image = new Image(getClass().getResourceAsStream("asset/images/vendoricon.png"));
            }
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(100);
            imageView.setFitWidth(100);
            Rectangle clip = new Rectangle(
                    imageView.getFitWidth(),
                    imageView.getFitHeight()
            );
            clip.setArcWidth(20);
            clip.setArcHeight(20);
            imageView.setClip(clip);
            urls.add(imageView);
        }

        HBox images_box = new HBox(20);
        images_box.setPadding(new Insets(10,10,10,10));
        order_info_box.getChildren().addAll(order_status,price_label,vendor_name);
        images_box.setSpacing(30);
        images_box.getChildren().add(order_info_box);
        images_box.getChildren().addAll(urls);
        order_label.setStyle("-fx-font-weight: bold ; -fx-font-size: 18px;");
        date.setStyle("-fx-text-fill: gray; -fx-font-size: 14px;");

        if(!order.getStatus().equals("waiting"))order_status.setStyle("-fx-text-fill:green; -fx-font-size: 14px;");
        else order_status.setStyle("-fx-text-fill:rgba(255,0,0,0.94); -fx-font-size: 14px; -fx-font-weight: bold;");
        HBox text_box = new HBox(20);
        text_box.setPadding(new Insets(10,10,10,10));
        text_box.getChildren().addAll(order_label , date);
        vbox.getChildren().addAll(images_box, text_box);
        vbox.setOnMousePressed( event -> {
            OrderDetController.setStatus(order.getStatus());
            OrderDetController.setOrder_id(order.getId(), Role.buyer);
            FXMLLoader users = new FXMLLoader(getClass().getResource("/org/Buyer/ OrderDetail-view.fxml"));
            try {
                Methods.switch_page(users,event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return vbox;
    }

    List<VBox> convert_to_vbox(List<Order> orders) {
        List<VBox> vboxs = new ArrayList<>();
        for(Order order : orders) {
            vboxs.add(create_card(order));
        }
        return vboxs;
    }

    @FXML
    void control_back(MouseEvent event) throws IOException {

        FXMLLoader users = new FXMLLoader(getClass().getResource("/org/Buyer/Home-view.fxml"));
        Methods.switch_page(users,event);
    }

    @FXML
    void apply_filter(MouseEvent event) throws IOException {
        initialize();
    }



}
