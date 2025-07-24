package Controller.Courier;

import Controller.Methods;
import Controller.SceneManager;
import Model.Order;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PendingController {


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
    TableColumn<Order, Void> act_col ;

    @FXML
    void initialize() throws IOException, JSONException {

        adjust_columns();
        load_data();
        setupActionColumn();

    }

    @FXML
    void adjust_columns(){

        id_col.setCellValueFactory(new PropertyValueFactory<>("id"));
        buyer_phone_col.setCellValueFactory(new PropertyValueFactory<>("buyer_phone"));
        add_col.setCellValueFactory(new PropertyValueFactory<>("address"));
        res_col.setCellValueFactory(new PropertyValueFactory<>("res_id"));
        state_col.setCellValueFactory(new PropertyValueFactory<>("status"));
        create_col.setCellValueFactory(new PropertyValueFactory<>("created_at"));
        buyer_name_col.setCellValueFactory(new PropertyValueFactory<>("buyer_name"));

    }


    private void setupActionColumn() {

        act_col.setCellFactory(new Callback<>() {

            @Override
            public TableCell<Order, Void> call(final TableColumn<Order, Void> param) {

                return new TableCell<>() {
                    private final Button receiveBtn = new Button("Receive");
                    private final Button deliverBtn = new Button("deliver");
                    private final HBox pane = new HBox(5, receiveBtn,deliverBtn);
                    {
                        receiveBtn.getStyleClass().add("view-button");
                        pane.setAlignment(Pos.CENTER);
                        deliverBtn.getStyleClass().add("view-button");
                        Order order = getTableView().getItems().get(getIndex());
                        if(order.getStatus().equals("acceptedbycourier")){
                            deliverBtn.setVisible(false);
                            receiveBtn.setVisible(true);
                        }
                        else if(order.getStatus().equals("received")){
                            receiveBtn.setVisible(false);
                            deliverBtn.setVisible(true);
                        }
                        receiveBtn.setOnAction(event -> {

                            if(order.getStatus().equals("acceptedbycourier")) {
                                try {
                                    handle_buttons(order,0);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            else {
                                SceneManager.showErrorAlert("Notice" , "You 've received this order before");
                            }
                        });

                        deliverBtn.setOnAction(event -> {
                            if(order.getStatus().equals("received")) {
                                try {
                                    handle_buttons(order,1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
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


    @FXML
    void load_data() throws IOException{

        URL get_available_orders = new URL(Methods.url+"deliveries/pending");
        HttpURLConnection connection = (HttpURLConnection) get_available_orders.openConnection();
        connection.setRequestMethod("GET");
        String token = Methods.Get_saved_token();
        connection.setRequestProperty("Authorization", "Bearer "+token);

        int http_code = connection.getResponseCode();

        if(http_code == 200){
            List<Order> orders = new ArrayList<>();
            JSONArray array = Methods.getJsonArrayResponse(connection);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                orders.add(new Order(obj.getLong("id"),
                        obj.getLong("vendor_id"),
                        obj.getString("buyer_name"),
                        obj.getString("buyer_phone"),
                        obj.getString("delivery_address"),
                        obj.getString("created_at"),
                        obj.getString("status")));
            }

            order_table.getItems().clear();
            order_table.getItems().addAll(orders);
        }
        else {
            JSONObject response = Methods.getJsonResponse(connection);
            SceneManager.showErrorAlert("Error" , response.getString("error"));
        }
    }


    @FXML
    void handle_buttons(Order order , int mode ) throws IOException {

        URL change_status_url = new URL(Methods.url+"deliveries/"+order.getId());
        HttpURLConnection connection = (HttpURLConnection) change_status_url.openConnection();
        connection.setRequestMethod("PUT");
        String token = Methods.Get_saved_token();
        connection.setRequestProperty("Authorization", "Bearer "+token);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        JSONObject obj = new JSONObject();
        if(mode == 0) {
            obj.put("status", "received");
        }
        else {
            obj.put("status", "delivered");
        }
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = obj.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int http_code = connection.getResponseCode();
        if(http_code == 200){
            initialize();
        }
        else {
            JSONObject response = Methods.getJsonResponse(connection);
            SceneManager.showErrorAlert("Error" , response.getString("error"));
        }

    }
    @FXML
    void control_back (MouseEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Courier/Courier-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = loader.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);
    }
}
