package Controller;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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


  public static  String Get_saved_token () throws IOException {

        String path = "src/main/resources/token.txt";
        return new String(Files.readAllBytes(Paths.get(path)));

  }
}
