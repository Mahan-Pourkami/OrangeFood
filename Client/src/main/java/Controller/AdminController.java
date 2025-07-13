package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class AdminController {


    @FXML
    Button logout_button;

    @FXML
    Label seller_count ;

    @FXML
    Label buyer_count ;

    @FXML
    Label courier_count ;

    @FXML
    Label total_count ;

    @FXML
    PieChart userschart;

    @FXML
    Label vendor_count ;


    private static int buyer_number ;

    private static int seller_number  ;

    private static int courier_number  ;

    private static int vendors_number;

    private static int total_number;


    @FXML
    public void initialize() {

        PieChart.Data buyer_data = new PieChart.Data("Buyer", buyer_number);
        PieChart.Data seller_data = new PieChart.Data("Seller", seller_number);
        PieChart.Data courier_data = new PieChart.Data("Courier", courier_number);
        userschart.getData().addAll(buyer_data, seller_data, courier_data);
        userschart.setClockwise(true);
        userschart.setLabelsVisible(true);
        buyer_count.setText(""+ buyer_number);
        seller_count.setText(""+ seller_number);
        courier_count.setText(""+ courier_number);
        vendor_count.setText(""+ vendors_number);
        total_number = buyer_number + seller_number + courier_number;
        total_count.setText(""+ total_number);

    }

    @FXML
    void handle_logout(MouseEvent event) throws IOException {

       try {
            URL logout_link = new URL("http://localhost:8080/auth/logout");
            HttpURLConnection connection = (HttpURLConnection) logout_link.openConnection();

            String token = Methods.Get_saved_token();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + token);

            if (connection.getResponseCode() == 200) {
                redirectToLogin(event);
            } else if (connection.getResponseCode() == 401) {
                SceneManager.showErrorAlert("Unauthorized", "You are not allowed to access this resource");
                redirectToLogin(event);
            }
        }
       catch (IOException e) {
           SceneManager.showErrorAlert("Connection failed", "Cannot connect to the server");
           redirectToLogin(event);
       }
    }

    public static void setvalues(int buyer_count, int seller_count, int courier_count,int vendors_number) {

        AdminController.buyer_number = buyer_count;
        AdminController.seller_number = seller_count;
        AdminController.courier_number = courier_count;
        AdminController.vendors_number = vendors_number;


    }

    private void redirectToLogin(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Login-view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent root = loader.load();
            Scene scene = new Scene(root);
            SceneManager.fadeScene(stage, scene);
        } catch (IOException e) {
            SceneManager.showErrorAlert("Navigation Error", "Could not load login screen");
        }
    }

    @FXML
    void handlehomebutton (MouseEvent event) throws IOException {

        FXMLLoader home = new FXMLLoader(getClass().getResource("/org/Intro-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = home.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);
    }

    @FXML
    void handleGetAllusers(MouseEvent event) throws IOException {

        FXMLLoader users = new FXMLLoader(getClass().getResource("/org/Alluser-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = users.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);
    }

    @FXML
    private void handle_approve(MouseEvent event) throws IOException {
        FXMLLoader approve = new FXMLLoader(getClass().getResource("/org/Approval-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = approve.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);
    }


    @FXML
    void handleGetAllvendors(MouseEvent event) throws IOException {
        FXMLLoader vendors = new FXMLLoader(getClass().getResource("/org/Allvendors-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = vendors.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);
    }

    @FXML
    void handleGetAllcoupons(MouseEvent event) throws IOException {
        FXMLLoader vendors = new FXMLLoader(getClass().getResource("/org/Coupon-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = vendors.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);

    }



}
