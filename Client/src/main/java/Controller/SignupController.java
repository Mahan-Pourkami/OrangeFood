package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.JSONObject;


import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Objects;

public class SignupController {



    @FXML
    TextField phonefield;

    @FXML
    TextField namefield;

    @FXML
    TextField passfield ;

    @FXML
    TextField emailfield;

    @FXML
    TextField addfield;

    @FXML
    TextField banknamefield;

    @FXML
    TextField accountnumberfield;

    @FXML
    ComboBox rolechooser;

    @FXML
    Button signupbutton;

    @FXML
    Button profchooser;

    @FXML
    Hyperlink loginlink;

    @FXML
    ImageView profview;

    @FXML
    Label errorlable;

    String selectedImagePath = "";
    Image default_prof = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/asset/images/contact.png")),640,640,true,true);


    @FXML
    void handleSignupButton(MouseEvent event) throws IOException {

        URL address = new URL("http://localhost:8080/auth/register");
        HttpURLConnection connection = (HttpURLConnection) address.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");

        JSONObject request = new JSONObject();
        JSONObject bankinfo = new JSONObject();

        String phone = phonefield.getText();
        String name = namefield.getText();
        String pass = passfield.getText();
        String email = emailfield.getText();
        String add = addfield.getText();
        String bankname = banknamefield.getText();
        String account = accountnumberfield.getText();
        String prof = selectedImagePath;
        String role = (String) rolechooser.getValue();

        request.put("phone", phone);
        request.put("full_name", name);
        request.put("password", pass);
        request.put("email", email);
        bankinfo.put("bank_name", bankname);
        bankinfo.put("account_number", account);
        request.put("bank_info", bankinfo);
        request.put("profileImageBase64", prof);
        request.put("address", add);
        request.put("role", role);

        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = request.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        catch (Exception e) {

            e.printStackTrace();
            SceneManager.showErrorAlert("Connection failed" , "Cannot connect to server");
        }

        JSONObject response = Methods.getJsonResponse(connection);

        int httpCode = connection.getResponseCode();

        if (httpCode == 200) {

            //TODO handle token
            FXMLLoader home = new FXMLLoader(getClass().getResource("/org/Home-view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent root = home.load();
            Scene scene = new Scene(root);
            SceneManager.fadeScene(stage, scene);
        }

        else {
            errorlable.setText(response.getString("error"));
        }
    }


    @FXML
    void handleloginlink(MouseEvent event) throws IOException {

        FXMLLoader newView = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/org/Login-view.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = newView.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);

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
                selectedImagePath = selectedFile.getAbsolutePath();

                Image image = new Image(selectedFile.toURI().toString(),640,640,true,true);
                profview.setImage(image);

                profview.setPreserveRatio(true);
            } catch (Exception e) {
                System.err.println("Error loading image: " + e.getMessage());
            }
        }
    }



    @FXML
    void initialize() {
        profview.setImage(default_prof);
          rolechooser.getItems().addAll("buyer", "seller", "courier");
        rolechooser.getSelectionModel().selectFirst();
        profview.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        profview.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                File file = db.getFiles().get(0);
                selectedImagePath = file.getAbsolutePath();
                profview.setImage(new Image(file.toURI().toString()));
            }
            event.setDropCompleted(true);
            event.consume();
        });

    }

}
