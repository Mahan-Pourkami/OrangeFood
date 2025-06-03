package Handler;

import Model.User;
import Utils.JwtUtil;
import com.sun.net.httpserver.Headers;
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
            JSONObject errorJson = new JSONObject();
            errorJson.put("error", "Internal server error");
            response = errorJson.toString();
            Headers headers = exchange.getResponseHeaders();
            headers.add("Content-Type", "application/json");
            exchange.sendResponseHeaders(500, response.getBytes().length);
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
                if(invalid_input_reg(jsonobject).isEmpty()){

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
                    UserDTO.UserRegResponseDTO userRegResponseDTO = new UserDTO.UserRegResponseDTO("User registered successfully", jsonobject.getString("phone"), jsonobject.getString("role"));
                    Headers headers = exchange.getResponseHeaders();
                    headers.add("Content-Type", "application/json");
                    response = userRegResponseDTO.response();
                    exchange.sendResponseHeaders(200, response.getBytes().length);
                }
                else {
                    Headers headers = exchange.getResponseHeaders();
                    headers.add("Content-Type", "application/json");
                    response = generateerror("Invalid "+invalid_input_reg(jsonobject));
                    exchange.sendResponseHeaders(400, response.length());
                }
            }
            catch (DuplicatedUserexception e) {
                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                response = generateerror("Phone number already exists");
                exchange.sendResponseHeaders(409, response.getBytes().length);
            }
        }


        else if(paths.length == 3 && paths[2].equals("login")) {
            System.out.println("Login request received");
            try {
                JSONObject jsonobject = getJsonObject(exchange);
                if(invalid_input_login(jsonobject).isEmpty()){
                    UserDTO.UserLoginRequestDTO userDTOlogin = new UserDTO.UserLoginRequestDTO(
                            jsonobject.getString("phone"),
                            jsonobject.getString("password"));
                    System.out.println("UserDTO made");
                    User user = userDTOlogin.getUserByPhoneAndPass();
                    System.out.println("User found");
                    if (user == null) {
                        JSONObject errorJson = new JSONObject();
                        errorJson.put("error", "Unauthorized request");
                        response = errorJson.toString();
                        Headers headers = exchange.getResponseHeaders();
                        headers.add("Content-Type", "application/json");
                        exchange.sendResponseHeaders(404, response.getBytes().length);
                    } else {
                        String token = JwtUtil.generateToken(user.getPhone(), String.valueOf(user.role));

                        JSONObject json = new JSONObject();
                        json.put("message", "Login successful");
                        json.put("token", token);

                        JSONObject userJson = new JSONObject();
                        userJson.put("id", user.getPhone());
                        userJson.put("full_name", user.getfullname());
                        userJson.put("phone", user.getPhone());
                        userJson.put("email", user.getEmail());
                        userJson.put("role", user.role);
                        userJson.put("address", user.getAddress());
                        userJson.put("profileImageBase64", user.getProfile());

                        JSONObject bankInfo = new JSONObject();
                        bankInfo.put("bank_name", user.getBankinfo().getBankName()); // or user.getBankInfo().getName()
                        bankInfo.put("account_number", user.getBankinfo().getAccountNumber());
                        userJson.put("bank_info", bankInfo);

                        json.put("user", userJson);

                        Headers headers = exchange.getResponseHeaders();
                        headers.add("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, json.toString().getBytes().length);
                        response = json.toString();
                    }

                }
                else {
                    String invalid_part = invalid_input_login(jsonobject);
                    JSONObject errorJson = new JSONObject();
                    errorJson.put("error", "Unauthorized request");
                    response = errorJson.toString();
                    Headers headers = exchange.getResponseHeaders();
                    headers.add("Content-Type", "application/json");
                    exchange.sendResponseHeaders(400, response.length());
                }
            }
            finally {

            }
            /*
            catch () {
                response = "Phone number already exists";
                exchange.sendResponseHeaders(409, response.length());
            }

             */
        }


        else {
            response = "Invalid request";
            exchange.sendResponseHeaders(405, response.length());
        }

        return response;
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

    private static String invalid_input_reg(JSONObject jsonObject) {

        String[] fields = {"full_name", "phone", "email", "password", "role", "address", "profileImageBase64"};
        for (String field : fields) {
            if (!jsonObject.has(field) || jsonObject.getString(field).isEmpty()) {
                return field;
            }
        }
        if (!Validator.validateEmail(jsonObject.getString("email"))) {
            return "email";
        }
        if (!Validator.validatePhone(jsonObject.getString("phone"))) {
            return "phone";
        }
        if (!jsonObject.has("bank_info")) {
            return "bank_info ";
        }
        JSONObject bankObject = jsonObject.optJSONObject("bank_info");
        if (bankObject == null) {
            return "bank_info";
        }
        if (!bankObject.has("bank_name") || bankObject.getString("bank_name").isEmpty()) {
            return "bank_name";
        }
        if (!bankObject.has("account_number") || bankObject.getString("account_number").isEmpty()) {
            return "account_number";
        }
        return ""; // Return an empty string if everything is valid
    }

    private static String invalid_input_login(JSONObject jsonObject) {

        String result = "" ;

        String [] fields = {"phone" , "password"};

        for (String field : fields) {
            if(!jsonObject.has(field)) {
                result = "Invalid" + field;
                return result;
            }
        }

        if(!Validator.validatePhone(jsonObject.getString("phone"))){
            result = "Invalid phone";
        }
        if(jsonObject.getString("password") == "" || jsonObject.getString("password") == null){
            result = "Invalid password";
        }

        return result;
    }

    private void sendResponse(HttpExchange exchange, String response) throws IOException {
        try(OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
        exchange.close();
    }


    private String generateerror(String error) {

        JSONObject errorJson = new JSONObject();
        errorJson.put("error", error);
        return errorJson.toString();
    }
}

