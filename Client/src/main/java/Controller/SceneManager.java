package Controller;

import javafx.animation.FadeTransition;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Objects;

public class SceneManager {



    public static void fadeScene(Stage stage, Scene scene) {

        FadeTransition ftOut = new FadeTransition(Duration.millis(50), stage.getScene().getRoot());
        ftOut.setFromValue(1.0);
        ftOut.setToValue(0.0);
        ftOut.play();
        ftOut.setOnFinished(event ->
        {
            stage.setScene(scene);
            scene.getRoot().setOpacity(0);
            stage.show();
            FadeTransition ftIn = new FadeTransition(Duration.millis(500), scene.getRoot());
            ftIn.setFromValue(0.0);
            ftIn.setToValue(1.0);
            ftIn.play();
        });
    }

    private static void showAlert(String title, String message, Alert.AlertType type) {

        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        ImageView icon = new ImageView( new Image(Objects.requireNonNull(SceneManager.class.getResourceAsStream("/asset/images/logo.png"))));
        icon.setFitHeight(48);
        icon.setFitWidth(48);
        alert.setGraphic(icon);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showErrorAlert(String title, String message) {
        showAlert(title, message, Alert.AlertType.ERROR);
    }


}
