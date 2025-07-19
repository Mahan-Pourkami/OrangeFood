package Controller.Buyer;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

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
}
