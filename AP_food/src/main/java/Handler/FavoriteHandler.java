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
import org.json.JSONObject;

import java.io.*;

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
                    break;

                case "POST":
                    break;

                case "PUT":

                    System.out.println("PUT request delivered to favorite endpoints");
                    response=handlePutRequest(exchange,paths);
                    break;

                case "DELETE":
                    break;

            }
        }
       catch (Exception e) {
           response = e.getMessage();
       }

       finally {
           send_Response(exchange,response);
       }
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
        exchange.close();
    }

}
