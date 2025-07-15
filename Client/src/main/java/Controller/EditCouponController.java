package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.format.DateTimeFormatter;

public class EditCouponController {


 private static long couponID = 0 ;


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
    void initialize() throws IOException {

        URL get_url = new URL("http://localhost:8080/admin/coupons/"+couponID);
        HttpURLConnection connection = (HttpURLConnection) get_url.openConnection();
        connection.setRequestMethod("GET");
        String token = Methods.Get_saved_token();
        connection.setRequestProperty("Authorization", "Bearer " + token);

        typechooser.getItems().addAll("fixed","percent");
        typechooser.getSelectionModel().selectFirst();

        JSONObject obj = Methods.getJsonResponse(connection);
        code_field.setText(obj.getString("coupon_code"));
        value_field.setText(obj.getNumber("value").toString());
        min_field.setText(obj.getNumber("min_price").toString());
        count_field.setText(obj.getNumber("user_counts").toString());

    }


    @FXML
    void handle_save() throws IOException {
        URL edit_url = new URL("http://localhost:8080/admin/coupons/"+couponID);
        HttpURLConnection connection = (HttpURLConnection) edit_url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", "application/json");
        String token = Methods.Get_saved_token();
        connection.setRequestProperty("Authorization", "Bearer " + token);
        connection.setDoOutput(true);

    }

    @FXML
    private void control_back(MouseEvent event) throws IOException {
        FXMLLoader users = new FXMLLoader(getClass().getResource("/org/Coupon-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = users.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);
    }

    @FXML
    void handle_edit(MouseEvent event) throws IOException {

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

        URL edit_url = new URL("http://localhost:8080/admin/coupons/"+couponID);
        HttpURLConnection connection = (HttpURLConnection) edit_url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", "application/json");
        String token = Methods.Get_saved_token();
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
            control_back(event);
        }
        else if(http_code == 401 ) {
            login_back(event);
        }
        else {
            error_label.setText(response.getString("error"));
        }
    }


    @FXML
    private void login_back(MouseEvent event) throws IOException {
        FXMLLoader users = new FXMLLoader(getClass().getResource("/org/Login-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = users.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);
    }

    public static void SetCouponID(long couponID) {
     EditCouponController.couponID = couponID;
    }
}
