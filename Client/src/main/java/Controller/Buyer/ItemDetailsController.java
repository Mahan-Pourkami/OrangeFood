package Controller.Buyer;

import Controller.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;

public class ItemDetailsController {

    @FXML
    ImageView food_image;
    
    
    @FXML
    void initialize() {

        food_image.setFitHeight(250);
        food_image.setFitWidth(250);
        Rectangle clip = new Rectangle(
                food_image.getFitWidth(),
                food_image.getFitHeight()
        );
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        food_image.setClip(clip);
    }


    @FXML
    void control_back(MouseEvent event)throws IOException {

        FXMLLoader users = new FXMLLoader(getClass().getResource("/org/Buyer/ListFoods-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = users.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);

    }
}
