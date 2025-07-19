package Controller.Admin;

import Controller.Methods;
import Controller.SceneManager;
import Model.Transaction;
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
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TransactionController {


    @FXML
    TableView<Transaction> trans_table;

    @FXML
    TableColumn<Transaction, Long> id_col;

    @FXML
    TableColumn<Transaction, String> order_col;

    @FXML
    TableColumn<Transaction,String> state_col;

    @FXML
    TableColumn<Transaction, String> phone_col;

    @FXML
    TableColumn<Transaction, String> methode_col;


    @FXML
    void setupColumns(){

        id_col.setCellValueFactory(new PropertyValueFactory<>("id"));
        order_col.setCellValueFactory(new PropertyValueFactory<>("order_id"));
        state_col.setCellValueFactory(new PropertyValueFactory<>("status"));
        phone_col.setCellValueFactory(new PropertyValueFactory<>("user_phone"));
        methode_col.setCellValueFactory(new PropertyValueFactory<>("methode"));

    }

    @FXML
    void initialize() throws IOException {

        URL get_transactions_url = new URL(Methods.url+"admin/transactions");
        HttpURLConnection connection = (HttpURLConnection) get_transactions_url.openConnection();
        connection.setRequestMethod("GET");
        String token = Methods.Get_saved_token();
        connection.setRequestProperty("Authorization", "Bearer "+token);

        int http_code = connection.getResponseCode();

        if(http_code == 200){
            JSONArray array = Methods.getJsonArrayResponse(connection);
            List<Transaction> transactions = new ArrayList<>();
            for(int i = 0; i < array.length(); i++){
                JSONObject obj = array.getJSONObject(i);
                transactions.add(new Transaction(obj.getLong("id"),obj.getString("order_id"),obj.getString("Methode"),obj.getString("status"),obj.getString("User Phone")));
            }
            setupColumns();
            trans_table.getItems().clear();
            trans_table.getItems().addAll(transactions);
        }
    }

    @FXML
    void control_back(MouseEvent event) throws IOException {
        FXMLLoader users = new FXMLLoader(getClass().getResource("/org/Admin/Admin-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = users.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);
    }
}
