package Handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.*;

public class FavoriteHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String request = exchange.getRequestMethod();
        String [] paths = exchange.getRequestURI().getPath().split("/");
        String response = "";

       try {
            switch (request) {
                case "GET":
                    break;

                case "POST":
                    break;

                case "PUT":
                    break;

                case "DELETE":
                    break;

            }
        }
       catch (Exception e) {
           response = "Methode not allowed";
       }
       finally {
           send_Response(exchange,response);
       }
    }



    private static JSONObject getJsonObject(HttpExchange exchange) throws IOException {
        try (InputStream requestBody = exchange.getRequestBody();
             BufferedReader reader = new BufferedReader(new InputStreamReader(requestBody)))
        {
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
            return new JSONObject(body.toString());
        }
    }

    public String generate_error(String error) {

        JSONObject errorJson = new JSONObject();
        errorJson.put("error", error);
        return errorJson.toString();
    }

    public String generate_msg(String msg){
        JSONObject msgJson = new JSONObject();
        msgJson.put("message", msg);
        return msgJson.toString();
    }



    public void send_Response(HttpExchange exchange, String response) throws IOException {
        try(OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
        exchange.close();
    }



}
