package Controller;

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
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class ProfileController{

    @FXML
    TextField phonefield ;

    @FXML
    TextField namefield ;

    @FXML
    TextField emailfield ;

    @FXML
    TextField addfield ;

    @FXML
    TextField banknamefield ;

    @FXML
    TextField accountnumberfield;

    @FXML
    Button profchooser ;

    @FXML
    Button updateprof ;

    @FXML
    ImageView profview ;

    @FXML
    Label errorlabel ;

    private String role ;

    Image default_prof = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/asset/images/contact.png")),640,640,true,true);

    private String prof;

    @FXML
    void initialize() throws IOException {

        profview.setFitHeight(150);
        profview.setFitWidth(150);
        Rectangle clip = new Rectangle(
                profview.getFitWidth(),
                profview.getFitHeight()
        );
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        profview.setClip(clip);

        URL get_prof = new URL(Methods.url+"auth/profile");
        HttpURLConnection connection = (HttpURLConnection) get_prof.openConnection();
        connection.setRequestMethod("GET");
        String token = Methods.Get_saved_token();
        connection.setRequestProperty("Authorization", "Bearer "+token);

        int httpCode = connection.getResponseCode();

        JSONObject obj = Methods.getJsonResponse(connection);

        if(httpCode == 200) {


            phonefield.setText(obj.getString("phone"));
            namefield.setText(obj.getString("full_name"));
            emailfield.setText(obj.getString("email"));
            addfield.setText(obj.getString("address"));
            JSONObject bank_info = obj.getJSONObject("bank_info");
            banknamefield.setText(bank_info.getString("bank_name"));
            accountnumberfield.setText(bank_info.getString("account_number"));
            String prof_path = obj.getString("profileImageBase64");
            prof = prof_path;
            role = obj.getString("role");

           try {
                if (prof_path.isEmpty() || prof_path == null) {

                    profview.setImage(default_prof);
                } else {
                    Image image = new Image((prof_path), 640, 640, true, true);
                    profview.setImage(image);
                }
            }
           catch(IllegalArgumentException e){

               profview.setImage(default_prof);
           }
        }
        else SceneManager.showErrorAlert("Error", obj.getString("error"));

    }

    @FXML
    private void handleSelectImage() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        Stage stage = (Stage) profchooser.getScene().getWindow();

        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                prof  = selectedFile.getAbsolutePath();
                Image image = new Image(selectedFile.toURI().toString(),640,640,true,true);
                profview.setImage(image);

                profview.setPreserveRatio(true);
            } catch (Exception e) {
                System.err.println("Error loading image: " + e.getMessage());
            }
        }
    }

    @FXML
    void control_back(MouseEvent event) throws IOException {

        try{
            if (role.equals("buyer")) {
                FXMLLoader users = new FXMLLoader(getClass().getResource("/org/Buyer/Home-view.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Parent root = users.load();
                Scene scene = new Scene(root);
                SceneManager.fadeScene(stage, scene);
            } else if (role.equals("seller")) {

                FXMLLoader users = new FXMLLoader(getClass().getResource("/org/Vendor/Vendor-view.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Parent root = users.load();
                Scene scene = new Scene(root);
                SceneManager.fadeScene(stage, scene);
            }
            else if (role.equals("courier")) {
                FXMLLoader users = new FXMLLoader(getClass().getResource("/org/Courier/Courier-view.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Parent root = users.load();
                Scene scene = new Scene(root);
                SceneManager.fadeScene(stage, scene);
            }
        }
        catch(Exception e){
            redirectToLogin(event);
        }
    }

    @FXML
    void handleupdate(MouseEvent event) throws IOException {

        JSONObject obj = new JSONObject();
        obj.put("full_name", namefield.getText());
        obj.put("phone", phonefield.getText());
        obj.put("email", emailfield.getText());
        obj.put("address", addfield.getText());
        JSONObject bank_info = new JSONObject();
        bank_info.put("bank_name", banknamefield.getText());
        bank_info.put("account_number", accountnumberfield.getText());
        obj.put("profileImageBase64", prof);
        obj.put("bank_info", bank_info);

        URL update_prof = new URL(Methods.url+"auth/profile");
        HttpURLConnection connection = (HttpURLConnection) update_prof.openConnection();
        connection.setRequestMethod("PUT");
        String token = Methods.Get_saved_token();
        connection.setRequestProperty("Authorization", "Bearer "+token);

        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = obj.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        catch (Exception e) {
            SceneManager.showErrorAlert("Connection failed" , "Cannot connect to server");
            control_back(event);
        }

        JSONObject result = Methods.getJsonResponse(connection);

        int httpCode = connection.getResponseCode();
        if(httpCode == 200) {
            refresh(event);
        }
        else {
            errorlabel.setText(result.getString("error"));
        }
    }


    @FXML
    private void refresh(MouseEvent event) throws IOException {

        FXMLLoader users = new FXMLLoader(getClass().getResource("/org/Profile-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = users.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);
    }


    private void redirectToLogin(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Login-view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent root = loader.load();
            Scene scene = new Scene(root);
            SceneManager.fadeScene(stage, scene);
        } catch (IOException e) {
            SceneManager.showErrorAlert("Navigation Error", "Could not load login screen");
        }
    }

}
