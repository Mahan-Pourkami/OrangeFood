package Handler;


import DAO.CourierDAO;
import DAO.SellerDAO;
import DAO.UserDAO;
import DTO.AdminDTO;
import Exceptions.ForbiddenroleException;
import Exceptions.InvalidInputException;
import Exceptions.InvalidTokenexception;
import Model.*;
import Utils.JwtUtil;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.*;
import java.util.List;

public class AdminHandler implements HttpHandler {

    UserDAO userDAO = new UserDAO();
    SellerDAO sellerDAO = new SellerDAO();
    CourierDAO courierDAO = new CourierDAO();

    @Override
    public void handle (HttpExchange exchange) throws IOException {

    String response = "";
    String method = exchange.getRequestMethod();
    String []paths = exchange.getRequestURI().getPath().split("/");

        try{
            switch (method) {
                case "GET":

                    System.out.println("GET request received");
                    response = handleGetRequest(exchange,paths);
                    break;

                case "POST":
                    //TODO
                    break;

                case "PUT":
                    //TODO
                    break;

                case "DELETE":
                    //TODO
                    break;

                case "PATCH" :
                    System.out.println("PATCH request received");
                    response = handlePatchRequest(exchange,paths);
                    break;

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        finally {

            send_Response(exchange,response);
        }
    }

    private String handleGetRequest(HttpExchange exchange , String [] paths) throws IOException {

        UserDAO userDAO = new UserDAO();
        String response = "";
        int http_code = 200 ;
        if(paths.length == 3 && paths[2].equals("users")){

            try{

             String token = JwtUtil.get_token_from_server(exchange);

            if(!JwtUtil.validateToken(token)){
                throw new InvalidTokenexception();
            }
            if(!JwtUtil.extractRole(token).equals("admin")){
                throw new ForbiddenroleException();
            }

                List<User> users = userDAO.getAllUsers();
                AdminDTO.Getusersresponse getall = new AdminDTO.Getusersresponse(users);
                response = getall.getResponse();
                http_code = 200;

            }
            catch (InvalidTokenexception e){

                response = generate_error(e.getMessage());
                http_code = 401;
            }

            catch (ForbiddenroleException e){
                response = generate_error(e.getMessage());
                http_code = 403;
            }

        }

        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", "application/json");
        exchange.sendResponseHeaders(http_code, response.getBytes().length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }


        return  response;
    }

    private String handlePatchRequest(HttpExchange exchange , String [] paths) throws IOException {

        String response = "";
        String token = JwtUtil.get_token_from_server(exchange);
        JSONObject jsonObject = getJsonObject(exchange);
        int http_code = 200 ;

        if(paths.length==5 && paths[2].equals("users") && paths[4].equals("status")){

            try{
                if (!jsonObject.has("status")) {
                    throw new InvalidInputException("status");
                }

                String status = jsonObject.getString("status");

                if (!JwtUtil.validateToken(token)) {
                    throw new InvalidTokenexception();
                }
                if (!JwtUtil.extractRole(token).equals("admin")) {
                    throw new ForbiddenroleException();
                }

                String phone = "09" + paths[3];
                User user = userDAO.getUserByPhone(phone);

                if (user instanceof Buyer) {
                    throw new ForbiddenroleException();
                }
                if (user.role.equals(Role.seller)) {
                    Seller seller = sellerDAO.getSeller(phone);
                    seller.setStatue(status);
                    sellerDAO.updateSeller(seller);
                }
                else if (user.role.equals(Role.courier)) {

                    Courier courier = courierDAO.getCourier(phone);
                    courier.setStatue(status);
                    courierDAO.updateCourier(courier);
                }

                else throw new ForbiddenroleException();

                response = generate_msg("Status of User :" + paths[3] + " is " + status);
                http_code = 200;

            }
            catch (InvalidInputException e){
                response = generate_error(e.getMessage());
                http_code = 400;
            }

            catch (InvalidTokenexception e){
                response = generate_error(e.getMessage());
                http_code = 401;
            }
            catch (ForbiddenroleException e){
                response = generate_error(e.getMessage());
                http_code = 403;
            }
            catch (IllegalArgumentException e){
                response = generate_error("Invalid status");
            }
        }

        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", "application/json");
        exchange.sendResponseHeaders(http_code, response.getBytes().length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }

        return response;
    }


    public void send_Response(HttpExchange exchange, String response) throws IOException {
        try(OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
        exchange.close();
    }

    public String generate_error(String error) {

        JSONObject errorJson = new JSONObject();
        errorJson.put("error", error);
        return errorJson.toString();
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

    public String generate_msg(String msg){
        JSONObject msgJson = new JSONObject();
        msgJson.put("message", msg);
        return msgJson.toString();
    }
}
