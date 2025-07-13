package Handler;


import DAO.*;
import DTO.AdminDTO;
import Exceptions.*;
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
    CouponDAO couponDAO = new CouponDAO();
    RestaurantDAO restaurantDAO = new RestaurantDAO();


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
                    System.out.println("POST request received");
                    response = handlePostRequest(exchange,paths);
                    break;

                case "PUT":
                    System.out.println("PUT request received");
                    response = handlePutRequest(exchange,paths);
                    break;

                case "DELETE":
                    System.out.println("DELETE request received");
                    response = handleDeleteRequest(exchange,paths);
                    break;

                case "PATCH" :
                    System.out.println("PATCH request received");
                    response = handlePatchRequest(exchange,paths);
                    break;

                default:
                    System.out.println("Unsupported request method");
                    response = generate_error("Unsupported request method");
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(405, response.getBytes().length);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        finally {
            send_Response(exchange,response);
        }
    }


    private String handlePostRequest(HttpExchange exchange , String [] paths) throws IOException {

        String response = "";
        int http_code = 200;
        JSONObject json = getJsonObject(exchange);


        if(paths.length == 3 && paths[2].equals("coupons")){

            try{
                String token = JwtUtil.get_token_from_server(exchange);
                if (!JwtUtil.validateToken(token)) {
                    throw new InvalidTokenexception();
                }
                if (!JwtUtil.extractRole(token).equals("admin")) {
                    throw new ForbiddenroleException();
                }

                AdminDTO.Create_coupon_request req = new AdminDTO.Create_coupon_request(json, couponDAO);
                req.submit_coupon();
                AdminDTO.Create_coupon_response res = new AdminDTO.Create_coupon_response(couponDAO,json.getString("coupon_code"));
                response = res.getResponse();
                http_code = 201;
            }

            catch (IllegalArgumentException e){

                response = generate_error("Invalid date");
                http_code = 400;
            }

            catch (OrangeException e){

                response = generate_error(e.getMessage());
                http_code = e.http_code;
            }
        }

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(http_code, response.length());


        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }


        return  response;
    }


    private String handlePutRequest(HttpExchange exchange , String [] paths) throws IOException {

        String response = "";
        int http_code = 200;
        JSONObject jsonObject = getJsonObject(exchange);
        String token = JwtUtil.get_token_from_server(exchange);

        if(paths.length == 4 && paths[2].equals("coupons")){

            try{
                Long coupon_id = Long.parseLong(paths[3]);

                if (!JwtUtil.validateToken(token)) {
                    throw new InvalidTokenexception();
                }

                if (!JwtUtil.extractRole(token).equals("admin")) {
                    throw new ForbiddenroleException();
                }

                AdminDTO.Update_coupon_request update_req = new AdminDTO.Update_coupon_request(jsonObject, couponDAO, coupon_id);
                AdminDTO.Create_coupon_response update_res = new AdminDTO.Create_coupon_response(couponDAO, couponDAO.getCoupon(coupon_id).getCode());
                response = update_res.getResponse();
                http_code = 200;

            }
            catch (IllegalArgumentException e){
                response = generate_error("Invalid input");
                http_code = 400;
            }
            catch (OrangeException e){
                response = generate_error(e.getMessage());
                http_code = e.http_code;
            }

        }
        else if(paths.length==5 && paths[2].equals("users") && paths[4].equals("status")){

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

                if (user == null ) {
                    throw new NosuchItemException("User not found");
                }

                if (user.role.equals(Role.seller)) {
                    Seller seller = sellerDAO.getSeller(phone);
                    seller.setStatue(status);

                    if(status.equals("approved")) {
                        sellerDAO.updateSeller(seller);
                    }
                    else {
                        sellerDAO.deleteSeller(phone);
                    }
                }
                else if (user.role.equals(Role.courier)) {

                    Courier courier = courierDAO.getCourier(phone);
                    courier.setStatue(status);
                    if(status.equals("approved")) {
                        courierDAO.updateCourier(courier);
                    }
                    else {
                       courierDAO.deleteCourier(phone);
                    }
                }

                else throw new ForbiddenroleException();

                response = generate_msg("Status of User :" + paths[3] + " is " + status);
                http_code = 200;

            }
            catch (OrangeException e){
                response = generate_error(e.getMessage());
                http_code = e.http_code;
            }
        }



        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(http_code, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }

        return  response;
    }



    private String handleGetRequest(HttpExchange exchange , String [] paths) throws IOException {


        String response = "";
        int http_code = 200 ;
        String token = JwtUtil.get_token_from_server(exchange);
        if(paths.length == 3 && paths[2].equals("users")){

            try{

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

        else if (paths.length == 3 && paths[2].equals("coupons")){


            try{
                if (!JwtUtil.validateToken(token)) {
                    throw new InvalidTokenexception();
                }
                if (!JwtUtil.extractRole(token).equals("admin")) {
                    throw new ForbiddenroleException();
                }

                List<Coupon> coupons = couponDAO.getAllCoupons();
                AdminDTO.Get_coupons_response getcoupons = new AdminDTO.Get_coupons_response(coupons);
                response = getcoupons.getResponse();
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

        else if (paths.length == 4 && paths[2].equals("coupons")){

            try{
                Long coupon_id = Long.parseLong(paths[3]);

                if (!JwtUtil.validateToken(token)) {
                    throw new InvalidTokenexception();
                }
                if (!JwtUtil.extractRole(token).equals("admin")) {
                    throw new ForbiddenroleException();
                }

                Coupon coupon = couponDAO.getCoupon(coupon_id);

                if(coupon == null){
                    throw new NosuchItemException();
                }

                AdminDTO.Create_coupon_response res = new AdminDTO.Create_coupon_response(couponDAO, coupon.getCode());
                response = res.getResponse();
                http_code = 200;
            }

            catch (IllegalArgumentException e){
                response = generate_error("Invalid coupon id");
                http_code = 400;
            }
            catch (OrangeException e){
                response = generate_error(e.getMessage());
                http_code = e.http_code;
            }
        }

        else if (paths.length == 3 && paths[2].equals("vendors")){

           try {
                if (!JwtUtil.validateToken(token)) {
                    throw new InvalidTokenexception();
                }
                if (!JwtUtil.extractRole(token).equals("admin")) {
                    throw new ForbiddenroleException();
                }

                AdminDTO.Get_Restaurants_response res = new AdminDTO.Get_Restaurants_response(restaurantDAO);
                response = res.getResponse();
                http_code = 200;
            }
           catch (OrangeException e){
               response = generate_error(e.getMessage());
               http_code = e.http_code;
           }
        }

        else if (paths.length == 3 && paths[2].equals("approvals")){

            try{
                if (!JwtUtil.validateToken(token)) {
                    throw new InvalidTokenexception();
                }
                if (!JwtUtil.extractRole(token).equals("admin")) {
                    throw new ForbiddenroleException();
                }

                AdminDTO.Get_approval_request request = new AdminDTO.Get_approval_request(sellerDAO, courierDAO);
                response = request.getResponse();
                http_code = 200;
            }
            catch (OrangeException e){
                response = generate_error(e.getMessage());
                http_code = e.http_code;
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


    private String handleDeleteRequest(HttpExchange exchange , String [] paths) throws IOException {

        String response = "";
        int http_code = 200 ;
        String token = JwtUtil.get_token_from_server(exchange);

        if(paths.length == 4 && paths[2].equals("coupons")){

            try{
                Long coupon_id = Long.parseLong(paths[3]);

                if (!JwtUtil.validateToken(token)) {
                    throw new InvalidTokenexception();
                }
                if (!JwtUtil.extractRole(token).equals("admin")) {
                    throw new ForbiddenroleException();
                }

                Coupon coupon = couponDAO.getCoupon(coupon_id);
                if (coupon == null) {
                    throw new NosuchItemException();
                }

                couponDAO.deleteCoupon(coupon_id);
                http_code = 200;
                response = generate_msg("Coupon deleted successfully");
            }
            catch (IllegalArgumentException e){
                response = generate_error("Invalid coupon id");
                http_code = 400;
            }
            catch (OrangeException e){
                response = generate_error(e.getMessage());
                http_code = e.http_code;
            }
        }

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(http_code, response.length());

        try (OutputStream os = exchange.getResponseBody()){
            os.write(response.getBytes());
        }
        return response;
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

                if (user == null ) {
                    throw new NosuchItemException("User not found");
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
            catch (OrangeException e){
                response = generate_error(e.getMessage());
                http_code = e.http_code;
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
