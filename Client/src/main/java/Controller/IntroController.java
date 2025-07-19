package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.awt.Desktop;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class IntroController {

    @FXML
    private Button continueButton;

    @FXML
    private Hyperlink aboutus;

    @FXML
    private Hyperlink support;

    @FXML
    private Hyperlink community;

    @FXML
    public void handle_aboutus(MouseEvent mouseEvent) {
        try {
            Desktop.getDesktop().browse(new URI("https://github.com/Mahan-Fathollahpour"));
        } catch (IOException | URISyntaxException e) {
            SceneManager.showErrorAlert("Browser Error", "Could not open GitHub profile");
        }
    }

    @FXML
    public void handle_support(MouseEvent mouseEvent) {
        try {
            Desktop.getDesktop().browse(new URI("https://github.com/parsa0s0a"));
        } catch (IOException | URISyntaxException e) {
            SceneManager.showErrorAlert("Browser Error", "Could not open GitHub profile");
        }
    }

    @FXML
    public void handle_community(MouseEvent mouseEvent) {
        try {
            Desktop.getDesktop().browse(new URI("https://www.linkedin.com/in/mahan-fathollahpour-89941722a/"));
        } catch (IOException | URISyntaxException e) {
            SceneManager.showErrorAlert("Browser Error", "Could not open LinkedIn profile");
        }
    }

    @FXML
    public void handle_continue_button(MouseEvent mouseEvent) {
        try {
            String token = Methods.Get_saved_token();
            if (token == null || token.isEmpty()) {
                redirectToLogin(mouseEvent);
                return;
            }

            URL profileUrl = new URL(Methods.url+"auth/profile");
            HttpURLConnection connection = (HttpURLConnection) profileUrl.openConnection();



            try {

                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + token);

                JSONObject obj = Methods.getJsonResponse(connection);

                int httpCode = connection.getResponseCode();
                if (httpCode == HttpURLConnection.HTTP_OK) {
                   if (obj.getString("role").equals("buyer")){
                       redirectToHome(mouseEvent);
                   }
                   else if (obj.getString("role").equals("seller")){
                       redirect_to_vendor(mouseEvent);
                   }
                   else if (obj.getString("role").equals("courier")){
                       redirectToCourier(mouseEvent);

                   }
                } else {
                    redirectToLogin(mouseEvent);
                }

            } finally {
                connection.disconnect();
            }

        } catch (IOException e) {
            SceneManager.showErrorAlert("Connection Error",e.getMessage());
            redirectToLogin(mouseEvent);
        }
    }

    private void redirectToHome(MouseEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Buyer/Home-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = loader.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);
    }

    private void redirect_to_vendor(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Vendor/Vendor-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = loader.load();
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

    private void redirectToCourier(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Courier/Courier-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = loader.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);
    }
}