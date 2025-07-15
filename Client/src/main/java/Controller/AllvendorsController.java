package Controller;


import Model.Vendor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

public class AllvendorsController {

    @FXML
    TableView<Vendor> vendor_table;

    @FXML
    TableColumn<Vendor, String> name_col;

    @FXML
    TableColumn<Vendor, String> phone_col;

    @FXML
    TableColumn<Vendor, String> add_col;

    @FXML
    TableColumn<Vendor, Long> id_col;

    @FXML
    TableColumn<Vendor, String> oname_col;

    @FXML
    TableColumn<Vendor, String> ophone_col;

    private final ObservableList<Vendor> vendors = FXCollections.observableArrayList();


    @FXML
    void initialize() throws IOException{

        name_col.setCellValueFactory(new PropertyValueFactory<>("name"));
        phone_col.setCellValueFactory(new PropertyValueFactory<>("phone"));
        add_col.setCellValueFactory(new PropertyValueFactory<>("address"));
        id_col.setCellValueFactory(new PropertyValueFactory<>("id"));
        oname_col.setCellValueFactory(new PropertyValueFactory<>("owner_name"));
        ophone_col.setCellValueFactory(new PropertyValueFactory<>("owner_phone"));
        vendor_table.setItems(vendors);
        loadVendorsData();


    }

    private void loadVendorsData() throws IOException {

        String token = Methods.Get_saved_token();
        URL getusers = new URL("http://localhost:8080/admin/vendors");
        HttpURLConnection connection = (HttpURLConnection) getusers.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + token);

        ArrayList<Vendor> vendors_list = new ArrayList<>();
        if (connection.getResponseCode() == 200) {
            JSONArray jsonarray = Methods.getJsonArrayResponse(connection);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);

                Vendor vendor = new Vendor(jsonobject.getString("name"),
                        jsonobject.getString("address"),
                        jsonobject.getString("phone"),
                        jsonobject.getLong("id"),
                        jsonobject.getString("owner_phone"),
                        jsonobject.getString("owner_name"));

               vendors_list.add(vendor);
            }

         vendors.addAll(vendors_list);
        }
        else {
            SceneManager.showErrorAlert("Task failed" , "Cannot fetch users data");
        }
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
