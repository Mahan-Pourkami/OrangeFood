package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;


public class LoginController {

    @FXML
    TextField phonefield ;

    @FXML
    PasswordField passwordfield ;

    @FXML
     Button loginbutton ;

    @FXML
     Button signup_button ;

    @FXML
     Hyperlink signup_link ;

    @FXML
     Label error_lable ;

    @FXML
    ImageView homebutton ;


    @FXML
    void handlelogin(MouseEvent event) throws IOException {


        System.out.println(phonefield.getText());
        URL address = new URL("http://localhost:8080/auth/login");
        HttpURLConnection connection = (HttpURLConnection) address.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        JSONObject request = new JSONObject();
        String phone = phonefield.getText();
        String password = passwordfield.getText();
        request.put("phone", phone);
        request.put("password", password);

        connection.setDoOutput(true);


        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = request.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        catch (Exception e) {
            SceneManager.showErrorAlert("Connection failed" , "Cannot connect to server");
        }

        JSONObject response = Methods.getJsonResponse(connection);
        int httpCode = connection.getResponseCode();
        if(httpCode == 200) {

            File tokenFile = new File("src/main/resources/token.txt");
            try (FileWriter writer = new FileWriter(tokenFile)) {
                writer.write(response.getString("token"));
            }
            error_lable.setVisible(false);

            String role = response.getString("role");

            if(role.equals("admin")) {

                int seller_count = response.getInt("seller_counts");
                int buyer_count = response.getInt("buyer_counts");
                int courier_count = response.getInt("courier_counts");
                AdminController.setvalues(buyer_count, seller_count, courier_count);
                FXMLLoader home = new FXMLLoader(getClass().getResource("/org/Admin-view.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Parent root = home.load();
                Scene scene = new Scene(root);
                SceneManager.fadeScene(stage, scene);

            }
            else {
                FXMLLoader home = new FXMLLoader(getClass().getResource("/org/Home-view.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Parent root = home.load();
                Scene scene = new Scene(root);
                SceneManager.fadeScene(stage, scene);
            }
        }
        else {
            error_lable.setVisible(true);
            error_lable.setText(response.getString("error"));
        }

    }


    @FXML
    void handlehomebutton (MouseEvent event) throws IOException {

        FXMLLoader home = new FXMLLoader(getClass().getResource("/org/Intro-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = home.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);
    }

    @FXML
    void handlesignup_button (MouseEvent event) throws IOException {

        FXMLLoader signup = new FXMLLoader(getClass().getResource("/org/Signup-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = signup.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);
    }


}