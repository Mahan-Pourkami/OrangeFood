package Controller.Vendor;

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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class UpdateRestaurantController {

    @FXML
    TextField name_field ;

    @FXML
    TextField phone_field ;

    @FXML
    TextField add_field ;

    @FXML
    TextField tax_field ;

    @FXML
    TextField addfee_field ;

    @FXML
    Button submit_button ;

    @FXML
    Button prof_chooser ;

    @FXML
    ImageView prof_view;

    @FXML
    Label error_label ;

    Image default_img = new Image(getClass().getResourceAsStream("/asset/images/vendoricon.png"));

    String prof = default_img.getUrl() ;


    @FXML
    void initialize() throws IOException {


        URL get_info = new URL(Methods.url+"restaurants/mine");
        HttpURLConnection connection = (HttpURLConnection) get_info.openConnection();
        connection.setRequestMethod("GET");
        String token = Methods.Get_saved_token();
        connection.setRequestProperty("Authorization", "Bearer "+token);

        JSONObject obj = Methods.getJsonResponse(connection);
        int http_code = connection.getResponseCode();

        if(http_code == 200) {
            name_field.setText(obj.getString("name"));
            phone_field.setText(obj.getString("phone"));
            tax_field.setText(String.valueOf(obj.getInt("tax_fee")));
            add_field.setText(obj.getString("address"));
            addfee_field.setText(String.valueOf(obj.getInt("additional_fee")));

            try {
                Image image = new Image(obj.getString("logoBase64"));
                prof_view.setImage(image);
                prof = image.getUrl();
            }
            catch (Exception e) {
                prof_view.setImage(default_img);
            }
        }

        else if (http_code == 401) {
            SceneManager.showErrorAlert("Unauthorized" , "Invalid Token");
        }
        else {
            SceneManager.showErrorAlert("Error" , obj.getString("error"));
        }

    }

    @FXML
    void handle_update(MouseEvent event) throws IOException {

        URL update_url = new URL(Methods.url+"restaurants/" +Methods.get_restaurant_id());
        HttpURLConnection connection = (HttpURLConnection) update_url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        String token = Methods.Get_saved_token();
        connection.setRequestProperty("Authorization", "Bearer "+token);
        connection.setDoOutput(true);

       try {

            JSONObject obj = new JSONObject();
            obj.put("name", name_field.getText());
            obj.put("phone", phone_field.getText());
            obj.put("tax_fee", Integer.parseInt(tax_field.getText()));
            obj.put("address", add_field.getText());
            obj.put("additional_fee", Integer.parseInt(addfee_field.getText()));
            obj.put("logoBase64", prof);
            System.out.println(prof);

           try (OutputStream os = connection.getOutputStream()) {
               byte[] input = obj.toString().getBytes("utf-8");
               os.write(input, 0, input.length);
           }

           JSONObject response = Methods.getJsonResponse(connection);

           int http_code = connection.getResponseCode();

           if(http_code == 200) {
               control_back(event);
           }
           else if (http_code == 401) {
               SceneManager.showErrorAlert("Unauthorized" , "Invalid Token");
           }
           else {
               error_label.setText(response.getString("error"));
           }
        }
       catch (IllegalArgumentException e) {
           SceneManager.showErrorAlert("Error" , e.getMessage());
       }

    }

    @FXML
    void control_back(MouseEvent event) throws IOException {

        FXMLLoader users = new FXMLLoader(getClass().getResource("/org/Vendor-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = users.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);
    }


    @FXML
    private void handleSelectImage() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        Stage stage = (Stage) prof_chooser.getScene().getWindow();

        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                prof  = selectedFile.getAbsolutePath();
                Image image = new Image(selectedFile.toURI().toString(),640,640,true,true);
                prof_view.setImage(image);

                prof_view.setPreserveRatio(true);
            } catch (Exception e) {
                System.err.println("Error loading image: " + e.getMessage());
            }
        }
    }

}
