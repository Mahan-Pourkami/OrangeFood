package Handler;

import DAO.BuyerDAO;
import DAO.RestaurantDAO;
import DAO.UserDAO;
import Exceptions.*;
import Model.Buyer;
import Model.Restaurant;
import Utils.JwtUtil;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.List;

public class FavoriteHandler implements HttpHandler {

    UserDAO userDAO = new UserDAO();
    BuyerDAO buyerDAO = new BuyerDAO();
    RestaurantDAO restaurantDAO = new RestaurantDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {



        String request = exchange.getRequestMethod();
        String [] paths = exchange.getRequestURI().getPath().split("/");
        String response = "";

       try {
            switch (request) {
                case "GET":
                    System.out.println("GET Request");
                    response=handleGetRequest(exchange, paths);
                    break;

                case "PUT":

                    System.out.println("PUT request delivered to favorite endpoints");
                    response=handlePutRequest(exchange,paths);
                    break;

                case "DELETE":
                    System.out.println("DELETE request delivered to favorite endpoints");
                    response=handleDeleteRequest(exchange,paths);
                    break;

            }
        }
       catch (Exception e) {
           response = e.getMessage();
           e.printStackTrace();
       }

       finally {
           send_Response(exchange,response);
       }
    }

    private String handleGetRequest(HttpExchange exchange, String[] paths) throws IOException {
        String response = "";
        int httpCode = 200;

        if (paths.length == 2) {
            try {
                String token = JwtUtil.get_token_from_server(exchange);
                if (!JwtUtil.validateToken(token)) throw new InvalidTokenexception();
                if (!JwtUtil.extractRole(token).equals("buyer")) throw new ForbiddenroleException();

                String phone = JwtUtil.extractSubject(token);
                Buyer buyer = buyerDAO.getBuyer(phone);
                List<Restaurant> restaurantList = buyer.getfavorite_restaurants();

                // Create a JSON array of restaurants
                JSONArray restaurantsArray = new JSONArray();
                for (Restaurant restaurant : restaurantList) {

                    JSONObject restaurantJson = new JSONObject();
                    restaurantJson.put("id", restaurant.getId());
                    restaurantJson.put("name", restaurant.getName());
                    restaurantJson.put("address", restaurant.getAddress());
                    restaurantJson.put("phone", restaurant.getPhone());
                    restaurantJson.put("logoBase64", restaurant.getLogoUrl());
                    restaurantJson.put("tax_fee", restaurant.getTax_fee());
                    restaurantJson.put("additional_fee", restaurant.getAdditional_fee());
                    restaurantsArray.put(restaurantJson);

                }

                response = restaurantsArray.toString();
                httpCode = 200;
            } catch (InvalidTokenexception e) {
                response = generate_error(e.getMessage());
                httpCode = 401;
            } catch (ForbiddenroleException e) {
                response = generate_error(e.getMessage());
                httpCode = 403;
            } catch (Exception e) {
                response = generate_error(e.getMessage());
                httpCode = 500;
            }
        } else {
            response = generate_error("Invalid path");
            httpCode = 404;
        }


        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", "application/json");
        exchange.sendResponseHeaders(httpCode, response.getBytes().length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }

        return response;
    }

    private String handlePutRequest(HttpExchange exchange , String[] paths) throws IOException {

        String response = "";
        int http_code = 200 ;

        if(paths.length ==3) {

            try{

                String token = JwtUtil.get_token_from_server(exchange);
                Long res_id = Long.parseLong(paths[2]);

                Restaurant res = restaurantDAO.get_restaurant(res_id);

                if (!JwtUtil.validateToken(token)) throw new InvalidTokenexception();
                if (!JwtUtil.extractRole(token).equals("buyer")) throw new ForbiddenroleException();
                if (res == null) throw new NosuchRestaurantException();



                String phone = JwtUtil.extractSubject(token);
                Buyer buyer = buyerDAO.getBuyer(phone);

                if(buyer.getFavorite_restaurants().contains(res_id)){
                    throw new DuplicatedItemexception();
                }

                buyer.add_tofavorite_restaurants(res_id);
                buyerDAO.updateBuyer(buyer);

                response = generate_msg(res.getName() + " Restaurant added to favorite restaurant");
                http_code = 200;

            }

            catch (NumberFormatException e) {
                response = generate_error("Invalid restaurant id");
                http_code = 400;
            }


            catch (InvalidTokenexception e){
                response = generate_error(e.getMessage());
                http_code = 401;
            }
            catch (ForbiddenroleException e){
                response = generate_error(e.getMessage());
                http_code = 403 ;
            }
            catch (NosuchRestaurantException e){
                response = generate_error(e.getMessage());
                http_code = 404 ;
            }

            catch (DuplicatedItemexception e){
                response = generate_error(e.getMessage());
                http_code = 409 ;
            }

            catch (Exception e){
                response = generate_error(e.getMessage());
                http_code = 500 ;
            }
            finally {

                Headers headers = exchange.getResponseHeaders();
                headers.set("Content-Type", "application/json");
                exchange.sendResponseHeaders(http_code, response.getBytes().length);

            }
        }

        else {
            response = generate_error("not found");
            Headers headers = exchange.getRequestHeaders();
            headers.add("Content-Type", "application/json");
            exchange.sendResponseHeaders(404, response.getBytes().length);
        }
        return  response;
    }


    private String handleDeleteRequest(HttpExchange exchange , String[] paths) throws IOException {
        String response = "";
        int http_code = 200;
        if (paths.length == 3) {

            try {
                Long res_id = Long.parseLong(paths[2]);
                String token = JwtUtil.get_token_from_server(exchange);
                if (!JwtUtil.validateToken(token)) throw new InvalidTokenexception();
                if (!JwtUtil.extractRole(token).equals("buyer")) throw new ForbiddenroleException();

                String phone = JwtUtil.extractSubject(token);
                Buyer buyer = buyerDAO.getBuyer(phone);

                if (!buyer.getFavorite_restaurants().contains(res_id)) throw new NosuchRestaurantException();

                buyer.remove_tofavorite_restaurants(res_id);
                buyerDAO.updateBuyer(buyer);

                response = generate_msg("Restaurant removed from favorite restaurants");
                http_code = 200;
            } catch (NumberFormatException e) {
                response = generate_error("Invalid restaurant id");
                http_code = 400;
            } catch (InvalidTokenexception e) {
                response = generate_error(e.getMessage());
                http_code = 401;
            } catch (ForbiddenroleException e) {
                response = generate_error(e.getMessage());
                http_code = 403;
            } catch (NosuchRestaurantException e) {
                response = generate_error(e.getMessage());
                http_code = 404;
            }
        }
        else {
            response = generate_error("endpoint not found");
            http_code = 405 ;
        }

        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", "application/json");
        exchange.sendResponseHeaders(http_code, response.getBytes().length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }


        return  response;
        }


    private String generate_error(String error) {

        JSONObject errorJson = new JSONObject();
        errorJson.put("error", error);
        return errorJson.toString();
    }

    private String generate_msg(String msg){
        JSONObject msgJson = new JSONObject();
        msgJson.put("message", msg);
        return msgJson.toString();
    }

    public void send_Response(HttpExchange exchange, String response) throws IOException {
        try(OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

}
