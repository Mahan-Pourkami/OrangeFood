package Handler;

import DTO.RestaurantDTO;
import Exceptions.DuplicatedUserexception;
import Exceptions.ForbiddenroleException;
import Exceptions.InvalidTokenexception;
import Exceptions.UnsupportedMediaException;
import Utils.JwtUtil;
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
                    response=handlePostRequest(exchange,paths);
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
        }
        finally {
            send_Response(exchange, response);
        }

    }

    private String handlePostRequest (HttpExchange exchange , String[] paths) throws IOException{
        String response = "";
        try {
            System.out.println("Hello");
            if (paths.length == 2 && paths[1].equals("restaurants")) {
                JSONObject jsonobject = getJsonObject(exchange);
                String token = JwtUtil.get_token_from_server(exchange);

                if (token == null || !JwtUtil.validateToken(token))
                    throw new InvalidTokenexception();

                if (!JwtUtil.extractRole(token).equals("seller"))
                    throw new ForbiddenroleException();

                String phone = JwtUtil.extractSubject(token);
                RestaurantDTO.AddRestaurantDTO restaurantDTO = new RestaurantDTO.AddRestaurantDTO(jsonobject,phone);
                restaurantDTO.register();
                System.out.println("Restaurant added");
                RestaurantDTO.Addrestaurant_response restaurant_response = new RestaurantDTO.Addrestaurant_response(phone);
                response = restaurant_response.response();
                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);

            }
            else {

                response = "Invalid request";
                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                exchange.sendResponseHeaders(405, response.getBytes().length);
            }
        }
        catch (ForbiddenroleException e) {

            response = generate_error("Forbidden request");
            Headers headers = exchange.getResponseHeaders();
            headers.add("Content-Type", "application/json");
            exchange.sendResponseHeaders(403, response.getBytes().length);
        }
        catch (InvalidTokenexception e) {

            response = generate_error("Unauthorized request");
            Headers headers = exchange.getResponseHeaders();
            headers.add("Content-Type", "application/json");
            exchange.sendResponseHeaders(401, response.getBytes().length);

        } catch (DuplicatedUserexception e) {

            response = generate_error("Conflict request");
            Headers headers = exchange.getResponseHeaders();
            headers.add("Content-Type", "application/json");
            exchange.sendResponseHeaders(409, response.getBytes().length);

        }
        catch (UnsupportedMediaException e) {

            response=generate_error("Unsupported media type");
            Headers headers = exchange.getResponseHeaders();
            headers.add("Content-Type", "application/json");
            exchange.sendResponseHeaders(415, response.getBytes().length);
        }
        return response;
    }
    private static String invalid_input_restaurants(JSONObject jsonObject) {

        String result = "" ;

        String [] fields = {"name" , "address" , "phone" , "logoBase64" , "tax_fee" , "additional_fee"};

        for (String field : fields) {
            if(!jsonObject.has(field)) {
                result = "Invalid " + field;
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

    private String generate_error(String error) {

        JSONObject errorJson = new JSONObject();
        errorJson.put("error", error);
        return errorJson.toString();
    }

    private void send_Response(HttpExchange exchange, String response) throws IOException {
        try(OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
        exchange.close();
    }

}


