package Handler;


import DAO.*;
import DTO.AdminDTO;
import Exceptions.*;
import Model.*;
import Utils.JwtUtil;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
تغییرات ادمین :
foodDAO و basketDAO در constructor
handlegetrequest : history + کامنت شده try در انتها
new nethods : parseQueryParams + getBasketJsonObject
 */
public class AdminHandler implements HttpHandler {

    UserDAO userDAO;
    SellerDAO sellerDAO;
    CourierDAO courierDAO;
    CouponDAO couponDAO;
    RestaurantDAO restaurantDAO;
    FoodDAO foodDAO;
    BasketDAO basketDAO;
    TransactionTDAO transactiontDAO;

    public AdminHandler(UserDAO userDAO, SellerDAO sellerDAO, CourierDAO courierDAO, CouponDAO couponDAO, RestaurantDAO restaurantDAO, FoodDAO foodDAO, BasketDAO basketDAO, TransactionTDAO transactionTDAO) {
        this.userDAO = userDAO;
        this.sellerDAO = sellerDAO;
        this.courierDAO = courierDAO;
        this.couponDAO = couponDAO;
        this.restaurantDAO = restaurantDAO;
        this.foodDAO = foodDAO;
        this.basketDAO = basketDAO;
        this.transactiontDAO = transactionTDAO;
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String response = "";
        String method = exchange.getRequestMethod();
        String[] paths = exchange.getRequestURI().getPath().split("/");

        try {
            switch (method) {
                case "GET":

                    System.out.println("GET request received");
                    response = handleGetRequest(exchange, paths);
                    break;

                case "POST":
                    System.out.println("POST request received");
                    response = handlePostRequest(exchange, paths);
                    break;

                case "PUT":
                    System.out.println("PUT request received");
                    response = handlePutRequest(exchange, paths);
                    break;

                case "DELETE":
                    System.out.println("DELETE request received");
                    response = handleDeleteRequest(exchange, paths);
                    break;

                case "PATCH":
                    System.out.println("PATCH request received");
                    response = handlePatchRequest(exchange, paths);
                    break;

                default:
                    System.out.println("Unsupported request method");
                    response = generate_error("Unsupported request method");
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(405, response.getBytes().length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            send_Response(exchange, response);
        }
    }


    private String handlePostRequest(HttpExchange exchange, String[] paths) throws IOException {

        String response = "";
        int http_code = 200;
        JSONObject json = getJsonObject(exchange);


        if (paths.length == 3 && paths[2].equals("coupons")) {

            try {
                String token = JwtUtil.get_token_from_server(exchange);
                if (!JwtUtil.validateToken(token)) {
                    throw new InvalidTokenexception();
                }
                if (!JwtUtil.extractRole(token).equals("admin")) {
                    throw new ForbiddenroleException();
                }

                AdminDTO.Create_coupon_request req = new AdminDTO.Create_coupon_request(json, couponDAO);
                req.submit_coupon();
                AdminDTO.Create_coupon_response res = new AdminDTO.Create_coupon_response(couponDAO, json.getString("coupon_code"));
                response = res.getResponse();
                http_code = 201;
            } catch (IllegalArgumentException e) {

                response = generate_error("Invalid date");
                http_code = 400;
            } catch (OrangeException e) {

                response = generate_error(e.getMessage());
                http_code = e.http_code;
            }
        }

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(http_code, response.length());


        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }


        return response;
    }


    private String handlePutRequest(HttpExchange exchange, String[] paths) throws IOException {

        String response = "";
        int http_code = 200;
        JSONObject jsonObject = getJsonObject(exchange);
        String token = JwtUtil.get_token_from_server(exchange);

        if (paths.length == 4 && paths[2].equals("coupons")) {

            try {
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

            } catch (IllegalArgumentException e) {
                response = generate_error("Invalid input");
                http_code = 400;
            } catch (OrangeException e) {
                response = generate_error(e.getMessage());
                http_code = e.http_code;
            }

        } else if (paths.length == 5 && paths[2].equals("users") && paths[4].equals("status")) {

            try {
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

                if (user == null) {
                    throw new NosuchItemException("User not found");
                }

                if (user.role.equals(Role.seller)) {
                    Seller seller = sellerDAO.getSeller(phone);
                    seller.setStatue(status);

                    if (status.equals("approved")) {
                        sellerDAO.updateSeller(seller);
                    } else {
                        sellerDAO.deleteSeller(phone);
                    }
                } else if (user.role.equals(Role.courier)) {

                    Courier courier = courierDAO.getCourier(phone);
                    courier.setStatue(status);
                    if (status.equals("approved")) {
                        courierDAO.updateCourier(courier);
                    } else {
                        courierDAO.deleteCourier(phone);
                    }
                } else throw new ForbiddenroleException();

                response = generate_msg("Status of User :" + paths[3] + " is " + status);
                http_code = 200;

            } catch (OrangeException e) {
                response = generate_error(e.getMessage());
                http_code = e.http_code;
            }
        }


        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(http_code, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }

        return response;
    }


    private String handleGetRequest(HttpExchange exchange, String[] paths) throws IOException {


        String response = "";
        int http_code = 200;
        String token = JwtUtil.get_token_from_server(exchange);
        if (paths.length == 3 && paths[2].equals("users")) {

            try {

                if (!JwtUtil.validateToken(token)) {
                    throw new InvalidTokenexception();
                }
                if (!JwtUtil.extractRole(token).equals("admin")) {
                    throw new ForbiddenroleException();
                }

                List<User> users = userDAO.getAllUsers();
                AdminDTO.Getusersresponse getall = new AdminDTO.Getusersresponse(users);
                response = getall.getResponse();
                http_code = 200;

            } catch (InvalidTokenexception e) {

                response = generate_error(e.getMessage());
                http_code = 401;
            } catch (ForbiddenroleException e) {
                response = generate_error(e.getMessage());
                http_code = 403;
            }

        } else if (paths.length == 3 && paths[2].equals("coupons")) {


            try {
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
            } catch (InvalidTokenexception e) {
                response = generate_error(e.getMessage());
                http_code = 401;
            } catch (ForbiddenroleException e) {
                response = generate_error(e.getMessage());
                http_code = 403;
            }

        } else if (paths.length == 4 && paths[2].equals("coupons")) {

            try {
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

                AdminDTO.Create_coupon_response res = new AdminDTO.Create_coupon_response(couponDAO, coupon.getCode());
                response = res.getResponse();
                http_code = 200;
            } catch (IllegalArgumentException e) {
                response = generate_error("Invalid coupon id");
                http_code = 400;
            } catch (OrangeException e) {
                response = generate_error(e.getMessage());
                http_code = e.http_code;
            }
        } else if (paths.length == 3 && paths[2].equals("vendors")) {

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
            } catch (OrangeException e) {
                response = generate_error(e.getMessage());
                http_code = e.http_code;
            }
        } else if (paths.length == 3 && paths[2].equals("approvals")) {

            try {
                if (!JwtUtil.validateToken(token)) {
                    throw new InvalidTokenexception();
                }
                if (!JwtUtil.extractRole(token).equals("admin")) {
                    throw new ForbiddenroleException();
                }

                AdminDTO.Get_approval_request request = new AdminDTO.Get_approval_request(sellerDAO, courierDAO);
                response = request.getResponse();
                http_code = 200;
            } catch (OrangeException e) {
                response = generate_error(e.getMessage());
                http_code = e.http_code;
            }
        } else if (paths.length == 3 && paths[2].equals("orders")) {

            try {
                if (!JwtUtil.validateToken(token)) {
                    throw new InvalidTokenexception();
                }
                if (!JwtUtil.extractRole(token).equals("admin")) {
                    throw new ForbiddenroleException();
                }

                Map<String, String> queryParams = parseQueryParams(exchange.getRequestURI().getQuery());
                String search = queryParams.getOrDefault("search", null);
                String vendor = queryParams.getOrDefault("vendor", null);
                String courier = queryParams.getOrDefault("courier", null);
                String customer = queryParams.getOrDefault("customer", null);
                String status = queryParams.getOrDefault("status", null);

                List<Basket> baskets = basketDAO.getAllBasket();
                JSONArray basketsArray = new JSONArray();
                for (Basket basket : baskets) {
                    boolean matches = true;

                    if (search != null && !search.isEmpty()) {
                        boolean match_food = false;

                        for (long food_id : basket.getItems().keySet()) {
                            Food food = foodDAO.getFood(food_id);
                            if (food == null) {
                                break;
                            }
                            if (food.getName().contains(search)) {
                                match_food = true;
                                break;
                            }
                        }
                        matches &= match_food;
                    }

                    if (vendor != null && !vendor.isEmpty()) {
                        matches &= restaurantDAO.get_restaurant(basket.getRes_id()).getName().contains(vendor);
                    }

                    if (courier != null && !courier.isEmpty()) {
                        matches &= userDAO.getUserByPhone(basket.getCourier_id()).getfullname().contains(courier);
                    }

                    if (customer != null && !customer.isEmpty()) {
                        matches &= basket.getBuyerName().contains(customer);
                    }

                    if (status != null && !status.isEmpty()) {
                        if (status.equals("accepted")) {
                            matches &= basket.getStateofCart() == StateofCart.accepted
                                    || basket.getStateofCart() == StateofCart.acceptedbycourier;
                        } else {
                            matches &= basket.getStateofCart().toString().equals(status);
                        }
                    }

                    if (matches) {
                        Map<Long, Integer> items = basket.getItems();
                        JSONArray itemIdsArray = new JSONArray(items.keySet());
                        basketsArray.put(getBasketJsonObject(basket, itemIdsArray));
                    }
                }
                response = basketsArray.toString();
            } catch (OrangeException e) {
                response = generate_error(e.getMessage());
                http_code = e.http_code;
            }
        } else if (paths.length == 3 && paths[2].equals("transactions")) {

            try {
                if (!JwtUtil.validateToken(token)) {
                    throw new InvalidTokenexception();
                }
                if (!JwtUtil.extractRole(token).equals("admin")) {
                    throw new ForbiddenroleException();
                }

                List<TransactionT> transactionTList = transactiontDAO.getAllTransactions();
                JSONArray transactionsArray = new JSONArray();
                for (TransactionT transactionT : transactionTList) {
                    JSONObject transactionJson = new JSONObject();
                    transactionJson.put("id", transactionT.getId());
                    transactionJson.put("order_id", transactionT.getOrderId() == 0 ? "Charge Wallet" : transactionT.getOrderId().toString());
                    transactionJson.put("Methode", transactionT.getMethod());
                    transactionJson.put("User Phone", transactionT.getUserId());
                    transactionJson.put("status", transactionT.getStatus());
                    transactionsArray.put(transactionJson);
                }
                response = transactionsArray.toString();
                http_code = 200;
            } catch (OrangeException e) {
                response = generate_error(e.getMessage());
                http_code = e.http_code;
            }

        }

        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", "application/json");
        exchange.sendResponseHeaders(http_code, response.getBytes().length);
        return response;
    }


    private String handleDeleteRequest(HttpExchange exchange, String[] paths) throws IOException {

        String response = "";
        int http_code = 200;
        String token = JwtUtil.get_token_from_server(exchange);

        if (paths.length == 4 && paths[2].equals("coupons")) {

            try {
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
            } catch (IllegalArgumentException e) {
                response = generate_error("Invalid coupon id");
                http_code = 400;
            } catch (OrangeException e) {
                response = generate_error(e.getMessage());
                http_code = e.http_code;
            }
        }

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(http_code, response.length());

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
        return response;
    }


    private String handlePatchRequest(HttpExchange exchange, String[] paths) throws IOException {

        String response = "";
        String token = JwtUtil.get_token_from_server(exchange);
        JSONObject jsonObject = getJsonObject(exchange);
        int http_code = 200;

        if (paths.length == 5 && paths[2].equals("users") && paths[4].equals("status")) {

            try {
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

                if (user == null) {
                    throw new NosuchItemException("User not found");
                }

                if (user.role.equals(Role.seller)) {
                    Seller seller = sellerDAO.getSeller(phone);
                    seller.setStatue(status);
                    sellerDAO.updateSeller(seller);
                } else if (user.role.equals(Role.courier)) {

                    Courier courier = courierDAO.getCourier(phone);
                    courier.setStatue(status);
                    courierDAO.updateCourier(courier);
                } else throw new ForbiddenroleException();

                response = generate_msg("Status of User :" + paths[3] + " is " + status);
                http_code = 200;

            } catch (OrangeException e) {
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
        try (OutputStream os = exchange.getResponseBody()) {
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
             BufferedReader reader = new BufferedReader(new InputStreamReader(requestBody))) {
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
            return new JSONObject(body.toString());
        }
    }

    public String generate_msg(String msg) {
        JSONObject msgJson = new JSONObject();
        msgJson.put("message", msg);
        return msgJson.toString();
    }

    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        if (query != null && !query.isEmpty()) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] kv = pair.split("=", 2); // only split on first '='
                String key = kv[0];
                String value = kv.length > 1 ? kv[1] : "";
                params.put(key, value);
            }
        }
        return params;
    }

    public JSONObject getBasketJsonObject(Basket basket, JSONArray itemIdsArray) {
        JSONObject basketJson = new JSONObject();
        basketJson.put("id", basket.getId());
        basketJson.put("delivery_address", basket.getAddress());
        basketJson.put("customer_id", basket.getBuyerPhone()); //تو yaml نوشته باید int باشه ولی فعلا string میفرستیم
        basketJson.put("vendor_id", basket.getRes_id());
        basketJson.put("coupon_id", basket.getCoupon_id() != null ? basket.getCoupon_id() : JSONObject.NULL);
        basketJson.put("item_ids", itemIdsArray);
        basketJson.put("raw_price", basket.getRawPrice(foodDAO));
        basketJson.put("tax_fee", basket.getTaxFee(restaurantDAO));
        basketJson.put("additional_fee", basket.getAdditionalFee(restaurantDAO));
        basketJson.put("courier_fee", basket.getCOURIER_FEE());
        basketJson.put("pay_price", basket.getPayPrice(restaurantDAO, foodDAO, couponDAO));
        basketJson.put("courier_id", basket.getCourier_id());
        basketJson.put("status", basket.getStateofCart());
        basketJson.put("created_at", basket.getCreated_at());
        basketJson.put("updated_at", basket.getUpadated_at());
        return basketJson;
    }
}
