package Handler;


import DAO.UserDAO;
import DTO.AdminDTO;
import Exceptions.ForbiddenroleException;
import Exceptions.InvalidTokenexception;
import Model.User;
import Utils.JwtUtil;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class AdminHandler implements HttpHandler {

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
}
