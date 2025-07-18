package Controller.Buyer;

import Controller.Methods;
import Controller.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WalletController {


    @FXML
    Label quantity_label;

    @FXML
    TextField acc_field;

    @FXML
    TextField amount_field;

    @FXML
    Button pay_button;




    @FXML
    void initialize() throws IOException {

        URL get_info_url = new URL(Methods.url + "wallet/quantity");
        HttpURLConnection connection = (HttpURLConnection) get_info_url.openConnection();
        connection.setRequestMethod("GET");
        String token = Methods.Get_saved_token();
        connection.setRequestProperty("Authorization", "Bearer " + token);

        int http_code = connection.getResponseCode();

        JSONObject obj = Methods.getJsonResponse(connection);

        if (http_code == 200) {

            quantity_label.setText(String.valueOf(obj.getInt("quantity"))+" $");
            acc_field.setText(obj.getString("account_number"));

        }
        else  SceneManager.showErrorAlert("Unauthorized", "Invalid token");
    }


    @FXML
    void handle_paybutton(MouseEvent event) throws IOException {

        try{
            URL charge_url = new URL(Methods.url + "wallet/top-up");
            HttpURLConnection connection = (HttpURLConnection) charge_url.openConnection();
            connection.setRequestMethod("POST");
            String token = Methods.Get_saved_token();

            connection.setRequestProperty("Authorization", "Bearer " + token);

            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            JSONObject obj = new JSONObject();
            obj.put("amount", Integer.parseInt(amount_field.getText()));


            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = obj.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int http_code = connection.getResponseCode();

            if(http_code == 200) {
                control_back(event);
            }
            else {
                SceneManager.showErrorAlert("Unauthorized", "Invalid token");
                redirectToLogin(event);
            }
        }
        catch (NumberFormatException e) {

            SceneManager.showErrorAlert("Invalid Input", "Invalid amount");
        }
    }


    @FXML
    void control_back(MouseEvent event) throws IOException {

            FXMLLoader users = new FXMLLoader(getClass().getResource("/org/Home-view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent root = users.load();
            Scene scene = new Scene(root);
            SceneManager.fadeScene(stage, scene);
    }

    @FXML
    void redirectToLogin(MouseEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Login-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = loader.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);


    }

}
