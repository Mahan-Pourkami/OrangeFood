package Controller.Buyer;

import Controller.Methods;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PaymentController {
    @FXML
    private Label walletcash;
    @FXML
    private Label basketprice;
    @FXML
    private Label walleterror;
    @FXML private TextField cvv;
    @FXML private TextField exp;
    @FXML private TextField card;

    @FXML
    void initialize() throws IOException {
        URL get_order_info = new URL(Methods.url + "orders/"+ OrderDetController.getOrder_id());
        HttpURLConnection connection = (HttpURLConnection) get_order_info.openConnection();
        connection.setRequestMethod("GET");
        String token = Methods.Get_saved_token();
        connection.setRequestProperty("Authorization", "Bearer " + token);
        JSONObject response =  Methods.getJsonResponse(connection);
        basketprice.setText(response.get("pay_price").toString());

        URL get_wallet_info = new URL(Methods.url + "wallet/quantity");
        HttpURLConnection connection2 = (HttpURLConnection) get_wallet_info.openConnection();
        connection2.setRequestMethod("GET");
        connection2.setRequestProperty("Authorization", "Bearer " + token);
        JSONObject response2 =  Methods.getJsonResponse(connection2);
        walletcash.setText(response2.get("quantity").toString());

        cvv.setText("");
        exp.setText("");
        card.setText("");
    }

    @FXML
    void handleonline(MouseEvent event) throws IOException{
        if(!cvv.getText().isEmpty() && !exp.getText().isEmpty() && !card.getText().isEmpty() && card.getText().length()==16) {
            URL get_info_url = new URL(Methods.url + "payment/online");
            HttpURLConnection connection = (HttpURLConnection) get_info_url.openConnection();
            connection.setRequestMethod("POST");
            String token = Methods.Get_saved_token();
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            JSONObject obj = new JSONObject();
            obj.put("order_id", OrderDetController.getOrder_id());
            obj.put("method", "online");
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = obj.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            int http_code = connection.getResponseCode();
            if (http_code == 200) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Buyer/ OrderDetail-view.fxml"));
                Methods.switch_page(loader, event);
            } else {
                initialize();
            }
        }
    }

    @FXML
    void handlewallet(MouseEvent event) throws IOException{
        URL get_info_url = new URL(Methods.url + "payment/online");
        HttpURLConnection connection = (HttpURLConnection) get_info_url.openConnection();
        connection.setRequestMethod("POST");
        String token = Methods.Get_saved_token();
        connection.setRequestProperty("Authorization", "Bearer " + token);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        JSONObject obj = new JSONObject();
        obj.put("order_id",OrderDetController.getOrder_id());
        obj.put("method","wallet");
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = obj.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        int http_code = connection.getResponseCode();
        if (http_code == 200) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Buyer/ OrderDetail-view.fxml"));
            Methods.switch_page(loader,event);
        }
        else{
            JSONObject response =  Methods.getJsonResponse(connection);
            walleterror.setText(response.get("error").toString());
            walleterror.setVisible(true);
            initialize();
        }
    }
    @FXML
    void control_back(MouseEvent event) throws IOException {

        FXMLLoader back = new FXMLLoader(getClass().getResource("/org/Buyer/ OrderDetail-view.fxml"));
        Methods.switch_page(back,event);

    }

}
