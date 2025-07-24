package Controller.Admin;

import Controller.Methods;
import Controller.SceneManager;
import Model.Approvals;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ApprovalController {


    @FXML
    TableView<Approvals> app_table;

    @FXML
    TableColumn<Approvals, String> name_col;

    @FXML
    TableColumn<Approvals, String> phone_col;

    @FXML
    TableColumn<Approvals, String> role_col;

    @FXML
    TableColumn<Approvals, String> id_col;

    @FXML
    TableColumn<Approvals, Void> action_col;

    private final ObservableList<Approvals> approvals = FXCollections.observableArrayList();

    @FXML
    void initialize() throws IOException {

        setupColumns();
        setupactionbutton();
        load_data();
    }
    void setupColumns() {

        name_col.setCellValueFactory(new PropertyValueFactory<>("name"));
        phone_col.setCellValueFactory(new PropertyValueFactory<>("phone"));
        role_col.setCellValueFactory(new PropertyValueFactory<>("role"));
        id_col.setCellValueFactory(new PropertyValueFactory<>("id"));
    }

    void load_data() throws IOException {

        URL request = new URL(Methods.url + "admin/approvals");
        HttpURLConnection connection = (HttpURLConnection) request.openConnection();
        connection.setRequestMethod("GET");
        String token = Methods.Get_saved_token();
        connection.setRequestProperty("Authorization", "Bearer " + token);
        int httpCode = connection.getResponseCode();

        if (httpCode == 200) {

            JSONArray array = Methods.getJsonArrayResponse(connection);
            List<Approvals> approvalsList = new ArrayList<>();

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String name = obj.getString("full_name");
                String phone = obj.getString("phone");
                String role = obj.getString("role");
                String id = obj.getString("id");
                approvalsList.add(new Approvals(name, phone, role, id));
            }
            app_table.getItems().clear();
            app_table.getItems().addAll(approvalsList);

        } else SceneManager.showErrorAlert("Error", "Can't fetch data");
    }

    private void setupactionbutton() {

        action_col.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Approvals, Void> call(final TableColumn<Approvals, Void> param) {
                return new TableCell<>() {
                    private final Button acceptBtn = new Button("Accept");
                    private final Button rejectBtn = new Button("Reject");
                    private final HBox pane = new HBox(5, acceptBtn, rejectBtn);
                    {
                        acceptBtn.getStyleClass().add("edit-button");
                        rejectBtn.getStyleClass().add("delete-button");
                        pane.setAlignment(Pos.CENTER);

                        acceptBtn.setOnAction(event -> {
                            Approvals approvals = getTableView().getItems().get(getIndex());
                            try {
                                handleAccept(approvals);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });

                        rejectBtn.setOnAction(event -> {
                            Approvals approvals = getTableView().getItems().get(getIndex());
                            try {
                                handleReject(approvals);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(pane);
                        }
                    }};
            }
        });
    }
    private void handleAccept(Approvals approvals) throws IOException {

        String id = approvals.getId();
        URL request = new URL(Methods.url+"admin/users/" + id + "/status");
        HttpURLConnection connection = (HttpURLConnection) request.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", "application/json");
        String token = Methods.Get_saved_token();
        connection.setRequestProperty("Authorization", "Bearer " + token);
        connection.setDoOutput(true);

        JSONObject obj = new JSONObject();
        obj.put("status", "approved");

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = obj.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        int httpCode = connection.getResponseCode();
        if (httpCode == 200) {
            app_table.getItems().remove(approvals);
        } else {
            SceneManager.showErrorAlert("Error", "Can't update data");
        }
    }

    private void handleReject(Approvals approvals) throws IOException {

        String id = approvals.getId();
        URL request = new URL(Methods.url+"admin/users/" + id + "/status");
        HttpURLConnection connection = (HttpURLConnection) request.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", "application/json");
        String token = Methods.Get_saved_token();
        connection.setRequestProperty("Authorization", "Bearer " + token);
        connection.setDoOutput(true);
        JSONObject obj = new JSONObject();
        obj.put("status", "rejected");
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = obj.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        int httpCode = connection.getResponseCode();
        if (httpCode == 200) {
            app_table.getItems().remove(approvals);
        }  else {
            SceneManager.showErrorAlert("Error", "Can't update data");
        }
    }
    @FXML
    private void control_back(MouseEvent event) throws IOException {
        FXMLLoader users = new FXMLLoader(getClass().getResource("/org/Admin/Admin-view.fxml"));
        Methods.switch_page(users,event);
    }
    @FXML
    private void login_back(MouseEvent event) throws IOException {
        FXMLLoader users = new FXMLLoader(getClass().getResource("/org/Login-view.fxml"));
        Methods.switch_page(users,event);
    }
}
