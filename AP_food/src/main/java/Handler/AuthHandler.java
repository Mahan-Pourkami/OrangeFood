package Handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import Model.Validator;
import DTO.UserDTO;
import Exceptions.*;

import java.io.*;

public class AuthHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String request = exchange.getRequestMethod();
        String [] paths = exchange.getRequestURI().getPath().split("/");
        String response = "";

        try {

            switch (request) {

                case "GET":
                    System.out.println("GET request received");
                    break;

                case "POST":
                    System.out.println("POST request received");
                    response=handlePostRequest(exchange,paths);
                    break;

                case "PUT":
                    //TODO
                    break;

                default:
                    response = "Invalid request";
                    exchange.sendResponseHeaders(405, response.length());
            }
        }
        catch (Exception e) {
            response = "Methode not allowed";
            exchange.sendResponseHeaders(500, response.length());
        }

        finally {

            sendResponse(exchange, response);
        }
    }


    private String handleGetRequest(HttpExchange exchange) throws IOException {

        String response = "";


        return exchange.getRequestURI().getPath();
    }

    private String handlePostRequest(HttpExchange exchange , String[] paths) throws IOException  , DuplicatedUserexception {

        String response;
        if(paths.length == 3 && paths[2].equals("register")) {
            try {
                JSONObject jsonobject = getJsonObject(exchange);
                if(!invalid_input_reg(jsonobject).isEmpty()){

                    JSONObject bankobject = jsonobject.optJSONObject("bank_info");

                    UserDTO.UserRegisterDTO userDTOreg = new UserDTO.UserRegisterDTO(
                            jsonobject.getString("full_name"),
                            jsonobject.getString("phone"),
                            jsonobject.getString("password"),
                            jsonobject.getString("role"),
                            jsonobject.getString("address"),
                            jsonobject.getString("email"),
                            jsonobject.getString("profileImageBase64"),
                            bankobject.getString("bank_name"),
                            bankobject.getString("account_number"));

                    userDTOreg.register();
                    response = "User registered successfully";
                    exchange.sendResponseHeaders(200, response.length());
                }
                else {
                    response = invalid_input_reg(jsonobject);
                    exchange.sendResponseHeaders(400, response.length());
                }
            }
            catch (DuplicatedUserexception e) {
                response = "Phone number already exists";
                exchange.sendResponseHeaders(409, response.length());
            }
        }
        else {
            response = "Invalid request";
            exchange.sendResponseHeaders(405, response.length());
        }
        return response;
    }

    private static JSONObject getJsonObject(HttpExchange exchange) throws IOException {
        try (InputStream requestBody = exchange.getRequestBody();
             BufferedReader reader = new BufferedReader(new InputStreamReader(requestBody))) {
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
            return new JSONObject(body.toString());
        }
    }

    private static String invalid_input_reg(JSONObject jsonObject) {

        String result = "" ;

        String [] fields = {"full_name" , "phone" , "email" , "password" , "role" , "address" , "profileImageBase64"};

        for (String field : fields) {
            if(!jsonObject.has(field)) {
                result = "Invalid" + field;
                return result;
            }
        }

        if(!Validator.validateEmail(jsonObject.getString("email"))){
            result = "Invalid email";
        }

        if(!Validator.validatePhone(jsonObject.getString("phone"))){
            result = "Invalid phone";
        }

        JSONObject bankobject = jsonObject.optJSONObject("bank_info");
        if(bankobject != null){
            result = "Invalid bank_info";
            return result;
        }
        if(!bankobject.has("bank_name")){
            result = "Invalid bank_name";
            return result;
        }
        if(!bankobject.has("account_number")){
            result = "Invalid account_number";
            return result;
        }
        return result;
    }

    private void sendResponse(HttpExchange exchange, String response) throws IOException {
        try(OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
        exchange.close();
    }
}
