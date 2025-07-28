package Controller.Courier;

import Controller.Methods;
import Controller.SceneManager;
import Model.Order;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DeliveryHistoryController {


    @FXML
    TableView<Order> order_table ;

    @FXML
    TableColumn<Order,Long> id_col ;

    @FXML
    TableColumn<Order, String> buyer_phone_col ;

    @FXML
    TableColumn<Order, String> buyer_name_col ;

    @FXML
    TableColumn<Order, String> add_col ;

    @FXML
    TableColumn<Order, Long> res_col ;

    @FXML
    TableColumn<Order, String> state_col ;

    @FXML
    TableColumn<Order, String> create_col ;

    @FXML
    TextField search_field ;

    @FXML
    TextField vendor_field ;

    @FXML
    TextField user_field ;

    List<Order> delivery_orders = new ArrayList<>();


    @FXML
    void initialize() throws IOException {

        setup_columns();
        load_data();

    }

    void setup_columns(){

        id_col.setCellValueFactory(new PropertyValueFactory<>("id"));
        res_col.setCellValueFactory(new PropertyValueFactory<>("res_id"));
        buyer_phone_col.setCellValueFactory(new PropertyValueFactory<>("buyer_phone"));
        buyer_name_col.setCellValueFactory(new PropertyValueFactory<>("buyer_name"));
        add_col.setCellValueFactory(new PropertyValueFactory<>("address"));
        state_col.setCellValueFactory(new PropertyValueFactory<>("status"));
        create_col.setCellValueFactory(new PropertyValueFactory<>("created_at"));

    }

    void load_data() throws IOException {

       delivery_orders.clear();

       String search = URLEncoder.encode(search_field.getText(), StandardCharsets.UTF_8);
       String vendor = URLEncoder.encode(vendor_field.getText(), StandardCharsets.UTF_8);
       String user = URLEncoder.encode(user_field.getText(), StandardCharsets.UTF_8);

       URL data_url = new URL(Methods.url+"deliveries/history?search="+search+
               "&vendor="+vendor+"&user="+user);

        HttpURLConnection connection = (HttpURLConnection) data_url.openConnection();
        connection.setRequestMethod("GET");
        String token = Methods.Get_saved_token();
        connection.setRequestProperty("Authorization", "Bearer "+token);

        if (connection.getResponseCode() == 200) {

            JSONArray order_array = Methods.getJsonArrayResponse(connection);
            for (int i = 0; i < order_array.length(); i++) {
                JSONObject obj = order_array.getJSONObject(i);
                delivery_orders.add(new Order(obj.getLong("id"),
                        obj.getLong("vendor_id"),
                        obj.getString("buyer_name"),
                        obj.getString("buyer_phone"),
                        obj.getString("delivery_address"),
                        obj.getString("created_at"),
                        obj.getString("status")));
            }
            order_table.getItems().clear();
            order_table.getItems().addAll(delivery_orders);
        }
        else {
            JSONObject response = Methods.getJsonResponse(connection);
            SceneManager.showErrorAlert("Error", response.getString("error"));
        }
    }

    @FXML
    void apply_filter(MouseEvent event) throws IOException {
        load_data();
    }

    @FXML
    void control_back(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Courier/Courier-view.fxml"));
        Methods.switch_page(loader,event);
    }


}
