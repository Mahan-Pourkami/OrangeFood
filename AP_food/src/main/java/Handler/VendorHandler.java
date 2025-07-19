package Handler;

import DAO.BuyerDAO;
import DAO.FoodDAO;
import DAO.RestaurantDAO;
import DTO.VendorDTO;
import Exceptions.ForbiddenroleException;
import Exceptions.InvalidTokenexception;
import Exceptions.NosuchRestaurantException;
import Exceptions.OrangeException;
import Model.Food;
import Utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class VendorHandler implements HttpHandler {

    RestaurantDAO restaurantDAO ;
    FoodDAO foodDAO ;
    BuyerDAO buyerDAO ;
    private static final int CHUNK_SIZE = 8192;
    private static final int MAX_IN_MEMORY_SIZE = 1024*1024;

    public VendorHandler(RestaurantDAO restaurantDAO, FoodDAO foodDAO , BuyerDAO buyerDAO) {
        this.restaurantDAO = restaurantDAO;
        this.foodDAO = foodDAO;
        this.buyerDAO = buyerDAO;

    }

    @Override
    public void handle (HttpExchange exchange) throws IOException {

        String method = exchange.getRequestMethod();
        String []paths = exchange.getRequestURI().getPath().split("/");
        String response = "";

        try{
            switch (method) {
                case "GET":
                    System.out.println("GET request received");
                    response = handleGetRequest(exchange,paths);
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

    }

    private String handleGetRequest(HttpExchange exchange, String []paths) throws IOException {

        String response = "";
        int http_code = 200;

        if(paths.length == 3){

            try{
                Long res_id = Long.parseLong(paths[2]);
                String token = JwtUtil.get_token_from_server(exchange);

                if (restaurantDAO.get_restaurant(res_id) == null) {
                    throw new NosuchRestaurantException();
                }

                if (!JwtUtil.validateToken(token)) {
                    throw new InvalidTokenexception();
                }
                if (!JwtUtil.extractRole(token).equals("buyer")) {
                    throw new ForbiddenroleException();
                }

                VendorDTO.See_vendor_menu vendorMenu = new VendorDTO.See_vendor_menu(restaurantDAO, foodDAO, res_id);
                ObjectMapper mapper = new ObjectMapper();
                response = mapper.writeValueAsString(vendorMenu);
                http_code = 200;
            }
            catch (OrangeException e){
                response = generate_error(e.getMessage());
                http_code = e.http_code;
            }

            send_Response(exchange,http_code,response);
        }

        else if (paths.length == 5 && paths[3].equals("menu")){

            try{
                Long res_id = Long.parseLong(paths[2]);
                String menu_title = paths[4];
                String token = JwtUtil.get_token_from_server(exchange);

                if (restaurantDAO.get_restaurant(res_id) == null) {
                    throw new NosuchRestaurantException();
                }

                if (!JwtUtil.validateToken(token)) {
                    throw new InvalidTokenexception();
                }
                if (!JwtUtil.extractRole(token).equals("buyer")) {
                    throw new ForbiddenroleException();
                }

                List<Food> foods = foodDAO.getFoodsByMenu(res_id, menu_title);
                JSONArray array = new JSONArray();
                for (Food food : foods) {
                    JSONObject obj = new JSONObject();
                    obj.put("id", food.getId());
                    obj.put("name", food.getName());
                    obj.put("price", food.getPrice());
                    obj.put("imageBase64", food.getPictureUrl());
                    obj.put("description", food.getDescription());
                    array.put(obj);
                }
                response = array.toString();
                http_code = 200;
            }
            catch (IllegalArgumentException e){
                response = generate_error(e.getMessage());
                http_code = 400;
            }

            catch (OrangeException e){
                response = generate_error(e.getMessage());
                http_code = e.http_code;
            }

            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(http_code, response.length());

            try(OutputStream os = exchange.getResponseBody()){
                os.write(response.getBytes());
            }
        }


        return response;
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

                String phone = JwtUtil.extractSubject(token);

                VendorDTO.Get_Vendors vendors = new VendorDTO.Get_Vendors(jsonObject, restaurantDAO ,foodDAO , buyerDAO ,phone);

                response = vendors.getResponse();
                http_code = 200;
            }
            catch (OrangeException e){
                response = generate_error(e.getMessage());
                http_code = e.http_code;
            }
        }
        send_Response(exchange,http_code,response);

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

    public void send_Response(HttpExchange exchange,int http_code,String response) throws IOException {

        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");

        if (responseBytes.length <= MAX_IN_MEMORY_SIZE) {
            exchange.sendResponseHeaders(http_code, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        }
        else {
            exchange.sendResponseHeaders(http_code, 0);
            try (OutputStream os = exchange.getResponseBody()) {
                int offset = 0;
                while (offset < responseBytes.length) {
                    int length = Math.min(CHUNK_SIZE, responseBytes.length - offset);
                    os.write(responseBytes, offset, length);
                    offset += length;
                }
            }
        }
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
