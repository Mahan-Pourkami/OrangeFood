package Controller.Buyer;

import Controller.Methods;
import Controller.SceneManager;
import Model.Transaction;
import Model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class TransactionController {

    @FXML
    private TableView<Transaction> TranactionTable;

    @FXML
    private TableColumn<Transaction, String> Idcolumn;

    @FXML
    private TableColumn<Transaction, String> OrderIdColumn;

    @FXML
    private TableColumn<Transaction, String> MethodColumn;

    @FXML
    private TableColumn<Transaction, String> StatusColumn;

    private final ObservableList<Transaction> transactions = FXCollections.observableArrayList();

    @FXML
    void initialize() throws IOException {

        Idcolumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        OrderIdColumn.setCellValueFactory(new PropertyValueFactory<>("order_id"));
        MethodColumn.setCellValueFactory(new PropertyValueFactory<>("methode"));
        StatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        TranactionTable.setItems(transactions);
        loadTransactioData();
    }

    private void loadTransactioData() throws IOException {

        String token = Methods.Get_saved_token();
        URL getTransactions = new URL(Methods.url+"transactions");
        HttpURLConnection connection = (HttpURLConnection) getTransactions.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + token);
        ArrayList<Transaction> trasactions_list = new ArrayList<>();
        if (connection.getResponseCode() == 200) {
            JSONArray jsonarray = Methods.getJsonArrayResponse(connection);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                Transaction transaction = new Transaction(
                        jsonobject.getLong("id"),
                        Integer.toString(jsonobject.getInt("order_id")),
                        jsonobject.getString("method"),
                        jsonobject.getString("status"),
                        jsonobject.getString("user_id")
                );
                trasactions_list.add(transaction);
            }
            transactions.addAll(trasactions_list);
        }
        else {
            SceneManager.showErrorAlert("Task failed" , "Cannot fetch users data");
        }
    }
    @FXML
    void control_back(MouseEvent event) throws IOException {
        FXMLLoader hom_view = new FXMLLoader(getClass().getResource("/org/Buyer/Home-view.fxml"));
        Methods.switch_page(hom_view,event);
    }
}