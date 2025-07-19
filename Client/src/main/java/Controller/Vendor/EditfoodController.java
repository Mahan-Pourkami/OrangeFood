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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class EditfoodController {


    @FXML
    Button profchooser;

    @FXML
    ImageView prof_view;

    @FXML
    TextField name_field;

    @FXML
    TextField price_field;

    @FXML
    TextField sup_field;

    @FXML
    TextField des_field;

    @FXML
    TextArea key_box;

    @FXML
    Label error_label;

   Image default_img = new Image(getClass().getResourceAsStream("/asset/images/vendoricon.png"));

   String image_path = "" ;


    private  static long res_id ;

    private  static long item_id ;



    @FXML
    void initialize() throws IOException {

        URL get_item_url = new URL(Methods.url+"restaurants/"+res_id+"/item/" + item_id);
        HttpURLConnection connection = (HttpURLConnection) get_item_url.openConnection();
        connection.setRequestMethod("GET");
        String token = Methods.Get_saved_token();
        connection.setRequestProperty("Authorization", "Bearer "+token);

        JSONObject obj = Methods.getJsonResponse(connection);
        int http_code = connection.getResponseCode();

        if (http_code == 200) {

            name_field.setText(obj.getString("name"));
            price_field.setText(String.valueOf(obj.getInt("price")));
            des_field.setText(obj.getString("description"));
            sup_field.setText(String.valueOf(obj.getInt("supply")));
            image_path = obj.getString("imageBase64");


           try {
               Image image = new Image(image_path);
                prof_view.setImage(image);
                image_path = image.getUrl();
            }
           catch (Exception e) {
               prof_view.setImage(default_img);
               image_path = default_img.getUrl();
           }
            JSONArray array = obj.getJSONArray("keywords");
            StringBuilder text = new StringBuilder();
            for (int i = 0; i < array.length(); i++) {

                text.append(array.getString(i)).append(" ");
            }
            key_box.setText(text.toString());

        }
        else if (http_code == 401) {
            SceneManager.showErrorAlert("Unauthorized" , "Invalid token");
        }

    }


    @FXML
    void control_back(MouseEvent event) throws IOException {

        FXMLLoader users = new FXMLLoader(getClass().getResource("/org/Vendor/FoodManage-view.fxml"));
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

        Stage stage = (Stage) profchooser.getScene().getWindow();

        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {

                image_path = selectedFile.getAbsolutePath();
                System.out.println(image_path);

                Image image = new Image(selectedFile.toURI().toString(),640,640,true,true);
                prof_view.setImage(image);

                prof_view.setPreserveRatio(true);
            } catch (Exception e) {
                System.err.println("Error loading image: " + e.getMessage());
            }
        }
    }

    @FXML
    void handleupdate(MouseEvent event) throws IOException {

        URL update_url = new URL(Methods.url+"restaurants/"+res_id+"/item/"+item_id);
        HttpURLConnection connection = (HttpURLConnection) update_url.openConnection();
        connection.setRequestMethod("PUT");
        String token = Methods.Get_saved_token();
        connection.setRequestProperty("Authorization", "Bearer "+token);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        JSONObject obj = new JSONObject();

        obj.put("name",name_field.getText());
        obj.put("price",price_field.getText());
        obj.put("description",des_field.getText());
        obj.put("logoBase64",image_path);
        obj.put("supply",sup_field.getText());
        JSONArray array = new JSONArray();
        List<String> list = List.of(key_box.getText().split(" "));
        for (String s : list) {
            array.put(s);
        }
        obj.put("keywords",array);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = obj.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        JSONObject response = Methods.getJsonResponse(connection);

        int http_code = connection.getResponseCode();
        if (http_code == 200) {
            control_back(event);
        }
        else if (http_code == 401) {
            SceneManager.showErrorAlert("Unauthorized" , "Invalid token");
        }
        else {
            error_label.setText(response.getString("error"));
        }

    }

    public static void set_id(long res_id , long item_id) {

        EditfoodController.res_id = res_id;
        EditfoodController.item_id = item_id;
    }



}
