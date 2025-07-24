package Controller.Admin;

import Controller.Methods;
import Controller.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
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
            URL logout_link = new URL("http://localhost:8081/auth/logout");
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

    private void redirectToLogin(MouseEvent event) throws IOException {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Login-view.fxml"));
            Methods.switch_page(loader,event);
    }

    @FXML
    void handlehomebutton (MouseEvent event) throws IOException {

        FXMLLoader home = new FXMLLoader(getClass().getResource("/org/Intro-view.fxml"));
        Methods.switch_page(home,event);
    }

    @FXML
    void handleGetAllusers(MouseEvent event) throws IOException {
        FXMLLoader users = new FXMLLoader(getClass().getResource("/org/Admin/Alluser-view.fxml"));
        Methods.switch_page(users,event);
    }

    @FXML
    private void handle_approve(MouseEvent event) throws IOException {
        FXMLLoader approve = new FXMLLoader(getClass().getResource("/org/Admin/Approval-view.fxml"));
        Methods.switch_page(approve,event);
    }


    @FXML
    void handleGetAllvendors(MouseEvent event) throws IOException {
        FXMLLoader vendors = new FXMLLoader(getClass().getResource("/org/Admin/Allvendors-view.fxml"));
        Methods.switch_page(vendors,event);
    }

    @FXML
    void handleGetAllcoupons(MouseEvent event) throws IOException {
        FXMLLoader coupons = new FXMLLoader(getClass().getResource("/org/Admin/Coupon-view.fxml"));
        Methods.switch_page(coupons,event);
    }

    @FXML
    void handleGetTransactions(MouseEvent event) throws IOException {

        FXMLLoader transactions_view = new FXMLLoader(getClass().getResource("/org/Admin/Transactions-view.fxml"));
        Methods.switch_page(transactions_view,event);
    }

    @FXML
    void handleorders(MouseEvent event) throws IOException {
        FXMLLoader orders = new FXMLLoader(getClass().getResource("/org/Admin/AdminOrder-view.fxml"));
        Methods.switch_page(orders,event);
    }
}
