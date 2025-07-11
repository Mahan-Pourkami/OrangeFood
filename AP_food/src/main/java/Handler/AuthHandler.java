package Handler;

import DAO.CourierDAO;
import DAO.SellerDAO;
import Model.*;
import Utils.JwtUtil;
import DTO.UserDTO;
import Exceptions.*;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.hibernate.Session;
import org.json.JSONObject;
import java.io.*;



public class AuthHandler implements HttpHandler {

    CourierDAO courierDAO = new CourierDAO();
    SellerDAO  sellerDAO = new SellerDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String request = exchange.getRequestMethod();
        String [] paths = exchange.getRequestURI().getPath().split("/");
        String response = "";
  
        try {

            switch (request) {

                case "GET":
                    System.out.println("GET request received");
                    response=handleGetRequest(exchange, paths);
                    break;

                case "POST":
                    System.out.println("POST request received");
                    response=handlePostRequest(exchange,paths);
                    break;

                case "PUT":
                    System.out.println("PUT request received");
                    response=handlePutRequest(exchange,paths);
                    break;

                default:
                    response = "Invalid request";
                    exchange.sendResponseHeaders(405, response.length());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {

            send_Response(exchange, response);
        }
    }

    private String handlePutRequest(HttpExchange exchange , String[] paths) throws IOException, UnsupportedMediaException, EmailException {

        String response = "";

        if(paths.length == 3 && paths[2].equals("profile")){
            String token = JwtUtil.get_token_from_server(exchange);

            if(JwtUtil.validateToken(token)) {

                JSONObject profilejson = getJsonObject(exchange);

                try {

                    if (invalid_input_update(profilejson).isEmpty()) {

                        UserDTO.Userupdateprof userdto = new UserDTO.Userupdateprof(JwtUtil.extractSubject(token), profilejson);
                        Headers headers = exchange.getResponseHeaders();
                        headers.add("Content-Type", "application/json");
                        JSONObject json = new JSONObject();
                        json.put("message", "Profile updated successfully");
                        response = json.toString();
                        exchange.sendResponseHeaders(200, response.getBytes().length);

                    } else {
                        response = generate_error("Invalid " + invalid_input_update(profilejson));
                        Headers headers = exchange.getResponseHeaders();
                        headers.add("Content-Type", "application/json");
                        exchange.sendResponseHeaders(400, response.getBytes().length);
                    }

                }

                catch (OrangeException e) {
                    response = generate_error(e.getMessage());
                    Headers headers = exchange.getResponseHeaders();
                    headers.add("Content-Type", "application/json");
                    exchange.sendResponseHeaders(e.http_code, response.getBytes().length);
                }
            }

            else{
                response = generate_error("Unauthorized request");
                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                exchange.sendResponseHeaders(401, response.getBytes().length);

            }

        }

        else {
            response = generate_error("Invalid request");
            Headers headers = exchange.getResponseHeaders();
            headers.add("Content-Type", "application/json");
            exchange.sendResponseHeaders(405, response.getBytes().length);
        }

        return response;
    }

    private String handleGetRequest(HttpExchange exchange , String[] paths) throws IOException {

        String response = "";
        if(paths.length == 3 && paths[2].equals("profile")) {

            String token = JwtUtil.get_token_from_server(exchange);

            if(JwtUtil.validateToken(token)) {

                UserDTO.UserResponprofileDTO userdto = new UserDTO.UserResponprofileDTO(JwtUtil.extractSubject(token));
                response = userdto.response();
                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);
            }
            else {
                response= generate_error("Unauthorized request");
                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                exchange.sendResponseHeaders(401, response.getBytes().length);
            }
        }

        return response;
    }

    public String handlePostRequest(HttpExchange exchange , String[] paths) throws IOException  , DuplicatedUserexception {

        String response ="";

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
                    response = generate_error("Invalid "+invalid_input_reg(jsonobject));
                    exchange.sendResponseHeaders(400, response.length());
                }
            }
            catch (IllegalArgumentException e){
                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                response = generate_error(e.getMessage());
                exchange.sendResponseHeaders(400, response.getBytes().length);
            }

            catch (UnsupportedMediaException e){

                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                response = generate_error(e.getMessage());
                exchange.sendResponseHeaders(415, response.getBytes().length);
            }
            catch (DuplicatedUserexception | EmailException e ) {
                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                response = generate_error(e.getMessage());
                exchange.sendResponseHeaders(409, response.getBytes().length);
            }
        }

        else if(paths.length == 3 && paths[2].equals("login")) {

            try{
                System.out.println("Login request received");

                JSONObject jsonobject = getJsonObject(exchange);
                if (invalid_input_login(jsonobject).isEmpty()) {

                    if (!jsonobject.get("phone").equals("admin")) {

                        UserDTO.UserLoginRequestDTO userDTOlogin = new UserDTO.UserLoginRequestDTO(
                                jsonobject.getString("phone"),
                                jsonobject.getString("password"));

                        System.out.println("UserDTO made");
                        User user = userDTOlogin.getUserByPhoneAndPass();
                        System.out.println("User found");

                        if (user == null) {

                            response = generate_error("User not found");
                            Headers headers = exchange.getResponseHeaders();
                            headers.add("Content-Type", "application/json");
                            exchange.sendResponseHeaders(404, response.getBytes().length);

                        } else {

                            if (user.role.equals(Role.courier)) {

                                Courier courier = courierDAO.getCourier(user.getPhone());
                                if (courier.getStatue()==null || !courier.getStatue().equals(Userstatue.approved)) {

                                    throw new ForbiddenroleException();
                                }
                            }

                            if (user.role.equals(Role.seller)) {
                                Seller seller = sellerDAO.getSeller(user.getPhone());
                                if (seller.getStatue()==null || !seller.getStatue().equals(Userstatue.approved)) {
                                    throw new ForbiddenroleException();
                                }
                            }

                            String token = JwtUtil.generateToken(user.getPhone(), String.valueOf(user.role));
                            JSONObject json = new JSONObject();
                            json.put("message", "Login successful");
                            json.put("token", token);
                            JSONObject userJson = new JSONObject();
                            userJson.put("id", user.getPhone().substring(2));
                            userJson.put("full_name", user.getfullname());
                            userJson.put("phone", user.getPhone());
                            userJson.put("email", user.getEmail());
                            userJson.put("role", user.role);
                            userJson.put("address", user.getAddress());
                            userJson.put("profileImageBase64", user.getProfile());
                            JSONObject bankInfo = new JSONObject();
                            bankInfo.put("bank_name", user.getBankinfo().getBankName());
                            bankInfo.put("account_number", user.getBankinfo().getAccountNumber());
                            userJson.put("bank_info", bankInfo);
                            json.put("user", userJson);

                            Headers headers = exchange.getResponseHeaders();
                            headers.add("Content-Type", "application/json");
                            exchange.sendResponseHeaders(200, json.toString().getBytes().length);
                            response = json.toString();
                        }
                    } else {

                        if (!jsonobject.get("password").equals("adminpass")) {

                            response = generate_error("Invlid password");
                            Headers headers = exchange.getResponseHeaders();
                            headers.add("Content-Type", "application/json");
                            exchange.sendResponseHeaders(500, response.getBytes().length);

                        } else {

                            JSONObject json = new JSONObject();
                            json.put("message", "Welcome dear admin!");
                            json.put("token", JwtUtil.generateToken("admin", "admin"));
                            Headers headers = exchange.getResponseHeaders();
                            headers.add("Content-Type", "application/json");
                            exchange.sendResponseHeaders(200, json.toString().getBytes().length);
                            response = json.toString();
                        }

                    }
                } else {
                    String invalid_part = invalid_input_login(jsonobject);
                    response = generate_error(invalid_part);
                    Headers headers = exchange.getResponseHeaders();
                    headers.add("Content-Type", "application/json");
                    exchange.sendResponseHeaders(400, response.length());
                }

            }
            catch (ForbiddenroleException e){
                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                response = generate_error("Your account is pending to be approved by admin");
                exchange.sendResponseHeaders(403, response.getBytes().length);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        else if(paths.length == 3 && paths[2].equals("logout")) {

            String token = JwtUtil.get_token_from_server(exchange);

            if(JwtUtil.validateToken(token)) {

                JSONObject messageobj = new JSONObject();
                JwtUtil.expireToken(token);
                messageobj.put("message", "User logged out successfully");
                response = messageobj.toString();
                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);


            }
            else {
                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                response = generate_error("Unauthorized request");
                exchange.sendResponseHeaders(401, response.getBytes().length);
            }
        }
        else {
            response = "Invalid request";
            exchange.sendResponseHeaders(405, response.length());
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

    private static String invalid_input_reg(JSONObject jsonObject) {

        String[] fields = {"full_name", "phone", "email", "password", "role", "address", "profileImageBase64"};
        for (String field : fields) {
            if (!jsonObject.has(field)) {
                return field;
            }

        if(jsonObject.getString("role").equals("buyer")
        && jsonObject.getString("role").equals("seller")
        && jsonObject.getString("role").equals("courier")) {

            return "role";
        }
        }
        if (!Validator.validateEmail(jsonObject.getString("email")) && !jsonObject.getString("email").isEmpty()) {
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

    private static String invalid_input_update(JSONObject jsonObject) {

        String[] fields = {"full_name", "phone", "email", "address", "profileImageBase64"};
        for (String field : fields) {
            if (!jsonObject.has(field)) {
                return field;
            }
        }


        if (!jsonObject.getString("email").isEmpty() && !Validator.validateEmail(jsonObject.getString("email"))) {
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
        return "";

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

        if(!Validator.validatePhone(jsonObject.getString("phone")) && !jsonObject.getString("phone").equals("admin")){
            result = "Invalid phone";
        }
        if(jsonObject.getString("password").isEmpty()|| jsonObject.getString("password") == null){
            result = "Invalid password";
        }

        return result;
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