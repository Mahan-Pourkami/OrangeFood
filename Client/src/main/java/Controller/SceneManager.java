package Controller;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
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

    public static void showAlert(String title, String message, Alert.AlertType type) {

        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        ImageView image = new ImageView(new Image(
                SceneManager.class.getResourceAsStream("/asset/images/logo.png") // Correct path
        ));
        image.setFitWidth(48);  // Optional: Set size
        image.setFitHeight(48);
        alert.setGraphic(image);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showErrorAlert(String title, String message) {
        showAlert(title, message, Alert.AlertType.ERROR);
    }

    public  static  void animateChart(PieChart pieChart) {
        for (PieChart.Data data : pieChart.getData()) {
            Timeline timeline = new Timeline();
            timeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(data.pieValueProperty(), 0)),
                    new KeyFrame(Duration.seconds(1),
                            new KeyValue(data.pieValueProperty(), data.getPieValue()))
            );
            timeline.play();
        }
    }


}
