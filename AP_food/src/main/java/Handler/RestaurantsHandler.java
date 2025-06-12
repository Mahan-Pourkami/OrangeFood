package Handler;

import DAO.UserDAO;
import Model.User;
import Model.Validator;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.*;

public class RestaurantsHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String request = exchange.getRequestMethod();
        String [] paths = exchange.getRequestURI().getPath().split("/");
        String response = "";
        int statusCode = 200;
        try {

            switch (request) {

                case "GET":
                    System.out.println("GET res request received");
                    break;

                case "POST":
                    System.out.println("POST res request received");
                    //response=handlePostRequest(exchange,paths);
                    break;

                case "PUT":
                    System.out.println("PUT res request received");
                    break;

                case "DELETE":
                    System.out.println("DELETE res request received");
                    break;

                default:
                    response = "Invalid res request";
                    statusCode = 405;
            }
        }
        catch (Exception e) {
            response = "Methode not allowed res";
            statusCode = 500;
        }

        finally {

            Headers headers = exchange.getResponseHeaders();
            headers.add("Content-Type", "application/json");
            exchange.sendResponseHeaders(statusCode, response.length());
            sendResponse(exchange, response);
        }
    }

    private String handlePostRequest (HttpExchange exchange , String[] paths) throws IOException{
        String response = "";
        int statusCode = 200;
        if(paths.length == 2 && paths[1].equals("restaurants")) {
            JSONObject jsonobject = getJsonObject(exchange);

        }
        return response;
    }
    private static String invalid_input_restaurants(JSONObject jsonObject) {

        String result = "" ;

        String [] fields = {"name" , "address" , "phone" , "logoBase64" , "tax_fee" , "additional_fee"};

        for (String field : fields) {
            if(!jsonObject.has(field)) {
                result = "Invalid" + field;
                return result;
            }
        }


        return result;
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

    private void sendResponse(HttpExchange exchange, String response) throws IOException {
        try(OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
        exchange.close();
    }

}


