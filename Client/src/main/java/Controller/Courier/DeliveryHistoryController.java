package Controller.Courier;

import Model.Order;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class DeliveryHistoryController {


    @FXML
    TableView<Order> order_table ;

    @FXML
    TableColumn<Order,Long> id_col ;

    @FXML
    TableColumn<Order, String> buyer_phone_col ;

    @FXML
    TableColumn<Order, String> buyer_name_col ;

    @FXML
    TableColumn<Order, String> add_col ;

    @FXML
    TableColumn<Order, Long> res_col ;

    @FXML
    TableColumn<Order, String> state_col ;

    @FXML
    TableColumn<Order, String> create_col ;

    @FXML
    TableColumn<Order, Void> act_col ;




}
