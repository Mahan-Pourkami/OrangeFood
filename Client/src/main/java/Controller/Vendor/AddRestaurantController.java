package Controller.Vendor;

import Controller.Methods;
import Controller.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

public class AddRestaurantController {



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



    Image default_logo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/asset/images/vendoricon.png")));
    URL resourceUrl = getClass().getResource("/asset/images/vendoricon.png");
    String prof ="";

    @FXML
    void initialize() throws URISyntaxException {

        prof_view.setImage(default_logo);
        Rectangle clip = new Rectangle(
                prof_view.getFitWidth(),
                prof_view.getFitHeight()
        );
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        prof_view.setClip(clip);
        prof = new File(resourceUrl.toURI()).getAbsolutePath();
        System.out.println(prof);
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
                System.out.println(prof);
                Image image = new Image(selectedFile.toURI().toString(),640,640,true,true);
                prof_view.setImage(image);
                prof_view.setPreserveRatio(true);
            } catch (Exception e) {
                System.err.println("Error loading image: " + e.getMessage());
            }
        }
    }

    @FXML
    void handleSubmit(MouseEvent event) throws IOException {

        String name = "";
        String phone ="";
        String add ="";
        int tax =0;
        int addfee=0;

       try {
             name = name_field.getText();
             phone = phone_field.getText();
             add = add_field.getText();
             tax = Integer.parseInt(tax_field.getText());
             addfee = Integer.parseInt(addfee_field.getText());
        }
        catch (IllegalArgumentException e) {
            SceneManager.showErrorAlert("invalid input" , " Input Number field error");
        }

        URL submit_req = new URL(Methods.url+"restaurants");
        HttpURLConnection connection = (HttpURLConnection) submit_req.openConnection();
        String token = Methods.Get_saved_token();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer "+token);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        JSONObject obj = new JSONObject();
        obj.put("name",name);
        obj.put("phone",phone);
        obj.put("address",add);
        obj.put("tax_fee",tax);
        obj.put("additional_fee",addfee);
        obj.put("logoBase64",prof);
        System.out.println(prof);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = obj.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        JSONObject response = Methods.getJsonResponse(connection);
        int http_code = connection.getResponseCode();
        if (http_code == 200) {
            control_back(event);
        }
        else {
            SceneManager.showErrorAlert("error" , response.getString("error"));
        }
    }

    @FXML
    void control_back(MouseEvent event) throws IOException {

        FXMLLoader users = new FXMLLoader(getClass().getResource("/org/Vendor/Vendor-view.fxml"));
        Methods.switch_page(users, event);
    }

    @FXML
    void handleremovephoto(MouseEvent event) {
        prof_view.setImage(default_logo);
        prof = resourceUrl.getPath();
    }

}
