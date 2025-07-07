package Handler;

import DAO.RestaurantDAO;
import DTO.VendorDTO;
import Exceptions.ForbiddenroleException;
import Exceptions.InvalidInputException;
import Exceptions.InvalidTokenexception;
import Exceptions.OrangeException;
import Model.Restaurant;
import Utils.JwtUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class VendorHandler implements HttpHandler {

    RestaurantDAO restaurantDAO = new RestaurantDAO();

    @Override
    public void handle (HttpExchange exchange) throws IOException {

        String method = exchange.getRequestMethod();
        String []paths = exchange.getRequestURI().getPath().split("/");
        String response = "";

        try{
            switch (method) {
                case "GET":
                    break;

                case "POST":
                    System.out.println("POST request recieved");
                    response = handlePostRequest(exchange,paths);
                    break;

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        finally {
            send_Response(exchange, response);
        }

    }


    private String handlePostRequest(HttpExchange exchange , String [] paths) throws IOException {

        String response = "";
        JSONObject jsonObject = getJsonObject(exchange);
        int http_code = 200;

        if(paths.length == 2){

            String token = JwtUtil.get_token_from_server(exchange);

            try{
                if (!JwtUtil.validateToken(token)) {
                    throw new InvalidTokenexception();
                }

                if (!JwtUtil.extractRole(token).equals("buyer")) {
                    throw new ForbiddenroleException();
                }

                VendorDTO.Get_Vendors vendors = new VendorDTO.Get_Vendors(jsonObject, restaurantDAO);

                response = vendors.getResponse();
                http_code = 200;
            }
            catch (OrangeException e){
                response = generate_error(e.getMessage());
                http_code = e.http_code;
            }
        }

        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(http_code, response.length());

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }

        return response;
    }

    public static JSONObject getJsonObject(HttpExchange exchange) throws IOException {

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

    public void send_Response(HttpExchange exchange, String response) throws IOException {
        try(OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
        exchange.close();
    }

    public String generate_msg(String msg){
        JSONObject msgJson = new JSONObject();
        msgJson.put("message", msg);
        return msgJson.toString();
    }

    public String generate_error(String error) {

        JSONObject errorJson = new JSONObject();
        errorJson.put("error", error);
        return errorJson.toString();
    }
}
