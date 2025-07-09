package Controller;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class Methods {


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
}
