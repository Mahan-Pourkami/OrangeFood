package Controller.Admin;

import Controller.Methods;
import Controller.SceneManager;
import Model.Coupon;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CouponController {

    @FXML
    private TableView<Coupon> coupon_table;

    @FXML
    private TableColumn<Coupon, String> code_col;

    @FXML
    private TableColumn<Coupon, Long> id_col;

    @FXML
    private TableColumn<Coupon, Number> val_col;

    @FXML
    private TableColumn<Coupon, String> start_col;

    @FXML
    private TableColumn<Coupon, String> end_col;

    @FXML
    private TableColumn<Coupon, Integer> min_col;

    @FXML
    private TableColumn<Coupon, Number> count_col;

    @FXML
    private TableColumn<Coupon, Void> action_col;

    @FXML
    private TableColumn<Coupon, String> type_col;

    @FXML
    TextField code_field ;

    @FXML
    ComboBox typechooser;

    @FXML
    TextField value_field;

    @FXML
    TextField min_field;

    @FXML
    TextField count_field;

    @FXML
    DatePicker start_chooser;

    @FXML
    DatePicker end_chooser;

    @FXML
    Label error_label;


    @FXML
    private void initialize() throws IOException {
        setupTableColumns();
        setupActionColumn();
        load_data();
        typechooser.getItems().addAll("fixed","percent");
        typechooser.getSelectionModel().selectFirst();
    }

    private void setupTableColumns() {

        code_col.setCellValueFactory(new PropertyValueFactory<>("code"));
        id_col.setCellValueFactory(new PropertyValueFactory<>("id"));
        val_col.setCellValueFactory(new PropertyValueFactory<>("value"));
        start_col.setCellValueFactory(new PropertyValueFactory<>("start_time"));
        end_col.setCellValueFactory(new PropertyValueFactory<>("end_time"));
        count_col.setCellValueFactory(new PropertyValueFactory<>("user_count"));
        type_col.setCellValueFactory(new PropertyValueFactory<>("type"));
        min_col.setCellValueFactory(new PropertyValueFactory<>("min_price"));
    }

    private void setupActionColumn() {

        action_col.setCellFactory(new Callback<>() {

            @Override
            public TableCell<Coupon, Void> call(final TableColumn<Coupon, Void> param) {
                return new TableCell<>() {
                    private final Button editBtn = new Button("Edit");
                    private final Button deleteBtn = new Button("Delete");
                    private final HBox pane = new HBox(5, editBtn, deleteBtn);

                    {

                        editBtn.getStyleClass().add("edit-button");
                        deleteBtn.getStyleClass().add("delete-button");
                        pane.setAlignment(Pos.CENTER);

                        editBtn.setOnAction(event -> {
                            Coupon coupon = getTableView().getItems().get(getIndex());
                            try {
                                handleEditCoupon(coupon,event);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });

                        deleteBtn.setOnAction(event -> {
                            Coupon coupon = getTableView().getItems().get(getIndex());
                            try {
                                handleDeleteCoupon(coupon);
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

    private void load_data() throws IOException {

        URL get_coupons_url = new URL(Methods.url+"admin/coupons");
        HttpURLConnection connection = (HttpURLConnection) get_coupons_url.openConnection() ;
        connection.setRequestMethod("GET");
        String token = Methods.Get_saved_token();
        connection.setRequestProperty("Authorization", "Bearer " + token);
        List<Coupon> coupons = new ArrayList<>();
        JSONArray array = Methods.getJsonArrayResponse(connection);

        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);

            Coupon coupon = new Coupon(obj.getString("coupon_code"),
                    obj.getLong("id"),
                    obj.getString("type"),
                    obj.getNumber("value"),
                    obj.getInt("user_counts"),
                    obj.getInt("min_price"),
                    obj.has("start_date")? obj.getString("start_date") : "",
                    obj.has("end_date")? obj.getString("end_date") : "");
            coupons.add(coupon);
        }
        coupon_table.getItems().clear();
        coupon_table.getItems().addAll(coupons);
    }

    private void handleEditCoupon(Coupon coupon, ActionEvent event) throws IOException {
        long id = coupon.getId();
        EditCouponController.SetCouponID(id);
        FXMLLoader users = new FXMLLoader(getClass().getResource("/org/Admin/EditCoupon-view.fxml"));
        Methods.switch_page(users,event);

    }

    private void handleDeleteCoupon(Coupon coupon) throws IOException {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Coupon: " + coupon.getCode());
        alert.setContentText("Are you sure you want to delete this coupon?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {

                try {
                    URL delete_url = new URL("http://localhost:8080/admin/coupons/"+coupon.getId());
                    HttpURLConnection connection = (HttpURLConnection) delete_url.openConnection();
                    String token = Methods.Get_saved_token();
                    connection.setRequestProperty("Authorization", "Bearer " + token);
                    connection.setRequestMethod("DELETE");
                    connection.setDoOutput(true);

                    if(connection.getResponseCode() >= 200 && connection.getResponseCode() < 300) {

                        coupon_table.getItems().remove(coupon);
                    }
                    else if(connection.getResponseCode() >= 401) {
                        SceneManager.showErrorAlert("Failed" , "Unauthorized Request");
                    }
                }
                 catch (IOException e) {
                    e.printStackTrace();
                }}});}

    @FXML
    private void handle_submit(MouseEvent event) throws IOException {

        String code = new String ();
        String type = new String ();
        Integer value = 0;
        String start_time = new String ();;
        String end_time = new String (); ;
        Integer min_price = 0;
        Integer count = 0;

        try{
             code = code_field.getText();
             type = (String) typechooser.getValue();
             value = Integer.parseInt(value_field.getText());
             min_price = Integer.parseInt(min_field.getText());
             count = Integer.parseInt(count_field.getText());
             start_time = start_chooser.getValue() == null ? "" : start_chooser.getValue().format((DateTimeFormatter.ofPattern("yyyy-MM-dd")));
             end_time = end_chooser.getValue() == null ? "" : end_chooser.getValue().format((DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        catch(IllegalArgumentException e){
            error_label.setText("Invalid Input");
        }
        String token = Methods.Get_saved_token();
        URL submiturl = new URL(Methods.url+"admin/coupons");
        HttpURLConnection connection = (HttpURLConnection) submiturl.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + token);
        JSONObject obj = new JSONObject();
        obj.put("coupon_code", code);
        obj.put("type", type);
        obj.put("value", value);
        obj.put("start_date", start_time);
        obj.put("end_date", end_time);
        obj.put("user_count", count);
        obj.put("min_price", min_price);
        connection.setDoOutput(true);
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = obj.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        catch (NumberFormatException e){
            error_label.setText("Please enter a number value for value and user count and min price ");
        }
        catch (Exception e) {
            SceneManager.showErrorAlert("Connection failed" , "Cannot connect to server");
        }
        int http_code = connection.getResponseCode();

        JSONObject response = Methods.getJsonResponse(connection);
        if(http_code >= 200 && http_code < 300) {
           initialize();
        }
        else if(http_code == 401 ) {
            login_back(event);
        }
        else {
            error_label.setText(response.getString("error"));
        }
    }

    @FXML
    private void control_back(MouseEvent event) throws IOException {

        FXMLLoader users = new FXMLLoader(getClass().getResource("/org/Admin/Admin-view.fxml"));
        Methods.switch_page(users,event);
    }

    @FXML
    private void login_back(MouseEvent event) throws IOException {
        FXMLLoader users = new FXMLLoader(getClass().getResource("/org/Login-view.fxml"));
        Methods.switch_page(users,event);
    }

}