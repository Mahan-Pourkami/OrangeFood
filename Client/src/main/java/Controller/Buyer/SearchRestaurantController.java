package Controller.Buyer;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class SearchRestaurantController {


    @FXML
    TextField search_field ;

    @FXML
    TextArea key_area ;

    @FXML
    ListView<VBox> res_list;

    @FXML
    void initialize() throws IOException {


    }

}
