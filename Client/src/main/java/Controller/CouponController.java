package Controller;

import Model.Coupon;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class CouponController {


    @FXML
    TableView<Coupon> coupon_table;

    @FXML
    TableColumn<Coupon, String> code_col;

    @FXML
    TableColumn<Coupon, Long> id_col;

    @FXML
    TableColumn<Coupon, Number> val_col;

    @FXML
    TableColumn<Coupon, String> start_col;

    @FXML
    TableColumn<Coupon, String> end_col;

    @FXML
    TableColumn<Coupon, Number> count_col;


    @FXML
    void initialize() {

        code_col.setCellValueFactory(new PropertyValueFactory<>("code"));
        id_col.setCellValueFactory(new PropertyValueFactory<>("id"));
        val_col.setCellValueFactory(new PropertyValueFactory<>("value"));
        start_col.setCellValueFactory(new PropertyValueFactory<>("start_time"));
        end_col.setCellValueFactory(new PropertyValueFactory<>("end_time"));
        count_col.setCellValueFactory(new PropertyValueFactory<>("user_count"));


    }



    @FXML
    void control_back(MouseEvent event) throws IOException {

        FXMLLoader users = new FXMLLoader(getClass().getResource("/org/Admin-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = users.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);
    }

}
