package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Methods {


    public final static String url = "http://localhost:8080/";

    public static JSONObject getJsonResponse(HttpURLConnection connection) throws IOException {

        boolean isSuccess = (connection.getResponseCode() >= 200 && connection.getResponseCode() < 300);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        isSuccess ? connection.getInputStream() : connection.getErrorStream(),
                        "utf-8"
                )
        )) {

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line.trim());
            }

            return new JSONObject(response.toString());
        }
    }
    public static JSONArray getJsonArrayResponse(HttpURLConnection connection) throws IOException {
        boolean isSuccess = (connection.getResponseCode() >= 200 && connection.getResponseCode() < 300);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        isSuccess ? connection.getInputStream() : connection.getErrorStream(),
                        "utf-8"
                )
        )) {

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line.trim());
            }
            return new JSONArray(response.toString());
        }
    }

   public static void switch_page(FXMLLoader page , MouseEvent event) throws IOException {

       Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
       Parent root = page.load();
       Scene scene = new Scene(root);
       SceneManager.fadeScene(stage, scene);
   }

    public static void switch_page(FXMLLoader page , ActionEvent event) throws IOException {

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = page.load();
        Scene scene = new Scene(root);
        SceneManager.fadeScene(stage, scene);
    }



  public static  String Get_saved_token () throws IOException {

        String path = "src/main/resources/token.txt";
        return new String(Files.readAllBytes(Paths.get(path)));

  }

  public static Long get_restaurant_id () throws IOException {

        URL url = new URL(Methods.url+"restaurants/mine");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        String token = Get_saved_token();
        connection.setRequestProperty("Authorization", "Bearer "+token);

        JSONObject response = getJsonResponse(connection);


        if(connection.getResponseCode()==200)  return response.getLong("id");

        else return null;

  }

}
