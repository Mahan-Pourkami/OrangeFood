package Handler;

import DAO.FoodDAO;
import DAO.RatingDAO;
import DAO.UserDAO;
import DTO.RatingDTO;
import Exceptions.*;
import Utils.JwtUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.*;

public class RatingHandler implements HttpHandler {

    RatingDAO ratingDAO = new RatingDAO();
    FoodDAO foodDAO = new FoodDAO();
    UserDAO userDAO = new UserDAO();

    @Override
    public void handle (HttpExchange exchange) throws IOException {

        String response = "";
        String method = exchange.getRequestMethod();
        String []paths = exchange.getRequestURI().getPath().split("/");

        try{
            switch (method) {
                case "GET":
                    break;

                case "POST":
                    System.out.println("POST request received");
                    response = handlePostRequest(exchange,paths);
                    break;

                case "PUT":
                    break;

                case "DELETE":
                    break;

                default:
                    break;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally{
            send_Response(exchange,response);
        }
    }


    private String handlePostRequest(HttpExchange exchange , String []paths) throws IOException {

        String response = "";
        String token = JwtUtil.get_token_from_server(exchange);
        JSONObject jsonObject = getJsonObject(exchange);
        int http_code = 200 ;

        if(paths.length == 2){

            try{
                if (!JwtUtil.validateToken(token)) {
                    throw new InvalidTokenexception();
                }
                if (!JwtUtil.extractRole(token).equals("buyer")) {
                    throw new ForbiddenroleException();
                }

                String phone = JwtUtil.extractSubject(token);
                String name = userDAO.getUserByPhone(phone).getfullname();

                RatingDTO.Submit_Rating sub_requestt = new RatingDTO.Submit_Rating(jsonObject, phone, name, ratingDAO, foodDAO);
                http_code = 200;
                response = generate_msg("Rating submitted");
            }
            catch (InvalidInputException e ) {
                response = generate_error(e.getMessage());
                http_code = 400;
            }
            catch (InvalidTokenexception e) {
                response = generate_error(e.getMessage());
                http_code = 401;
            }
            catch (ForbiddenroleException e) {
                response = generate_error(e.getMessage());
                http_code = 403;
            }
            catch (NosuchItemException e){
                response = generate_error(e.getMessage());
                http_code = 404;
            }
            catch (UnsupportedMediaException e) {
                response = generate_error(e.getMessage());
                http_code = 415;
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
