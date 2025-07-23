package Controller.Admin;

import Controller.Methods;
import Controller.SceneManager;
import Model.Order;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class AdminOrderController {


    @FXML
    TableView<Order> order_table;

    @FXML
    TableColumn<Order, Long> id_col;

    @FXML
    TableColumn<Order, String> buyer_col;

    @FXML
    TableColumn<Order, String> add_col;

    @FXML
    TableColumn<Order, String> vendor_col;

    @FXML
    TableColumn<Order, String> create_col;

    @FXML
    TableColumn<Order, String> state_col;

    @FXML
    TableColumn<Order, String> courier_col;

    @FXML
    TextField search_field ;

    @FXML
    TextField vendor_field;

    @FXML
    ComboBox<String> status_box;

    @FXML
    TextField courier_field;

    @FXML
    TextField customer_field;

    List<Order> orders_list = new ArrayList<>();


    @FXML
    void initialize() throws IOException{

        status_box.getItems().addAll("" ,"waiting", "payed" ,"accepted" , "acceptedbycourier" ,"received" , "rejected" , "served" , "delivered");
        status_box.getSelectionModel().selectFirst();
        adjut_table();





    }

    @FXML
    void control_back(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/Admin/Admin-view.fxml"));
        Methods.switch_page(fxmlLoader,event);
    }

    @FXML
    void setup_columns() throws IOException {

        id_col.setCellValueFactory(new PropertyValueFactory<>("id"));
        vendor_col.setCellValueFactory(new PropertyValueFactory<>("res_id"));
        buyer_col.setCellValueFactory(new PropertyValueFactory<>("buyer_phone"));
        add_col.setCellValueFactory(new PropertyValueFactory<>("address"));
        state_col.setCellValueFactory(new PropertyValueFactory<>("status"));
        courier_col.setCellValueFactory(new PropertyValueFactory<>("courier_id"));
        create_col.setCellValueFactory(new PropertyValueFactory<>("created_at"));

    }


    void load_data () throws IOException {

        URL fetch_url = new URL(Methods.url+"admin/orders?search="+search_field.getText()+
                "&vendor="+vendor_field.getText()+"&customer="+customer_field.getText()+"&status="+(String)status_box.getValue()
                +"&courier="+courier_field.getText() );

        HttpURLConnection connection = (HttpURLConnection) fetch_url.openConnection();
        String token = Methods.Get_saved_token();
        connection.setRequestProperty("Authorization", "Bearer "+token);
        connection.setRequestMethod("GET");

        if(connection.getResponseCode() == 200){
            JSONArray array = Methods.getJsonArrayResponse(connection);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                orders_list.add(new Order(obj.getLong("id"),
                        obj.getString("customer_id")
                        ,obj.getLong("vendor_id"),
                        obj.getString("delivery_address"),
                        obj.getString("status"),
                        obj.getString("created_at"),
                        obj.has("courier_id") ? obj.getString("courier_id"):""));
            }
            order_table.getItems().clear();
            order_table.getItems().addAll(orders_list);
        }
        else {
            JSONObject response = Methods.getJsonResponse(connection);
            SceneManager.showErrorAlert("Error", response.getString("error"));
        }

    }

    @FXML
    void search(MouseEvent event) throws IOException {
        adjut_table();
    }

    void adjut_table () throws IOException {
        orders_list.clear();
        setup_columns();
        load_data();
    }

}
