package Handler;

import DAO.*;
import Exceptions.*;
import Model.*;
import Utils.JwtUtil;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DeliveryHandler implements HttpHandler {

    UserDAO userDAO;
    CouponDAO couponDAO;
    BasketDAO basketDAO;
    RestaurantDAO restaurantDAO;
    FoodDAO foodDAO;

    public DeliveryHandler(UserDAO userDAO,CouponDAO couponDAO,BasketDAO basketDAO, RestaurantDAO restaurantDAO, FoodDAO foodDAO) {
        this.userDAO = userDAO;
        this.couponDAO = couponDAO;
        this.basketDAO = basketDAO;
        this.restaurantDAO = restaurantDAO;
        this.foodDAO = foodDAO;
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String response = "";
        String methode = exchange.getRequestMethod();
        String []paths = exchange.getRequestURI().getPath().split("/");
        int http_code = 200; // Default success code

        try{
            switch (methode) {
                case "GET":
                    response = handleGetRequest(exchange,paths);
                    break;

                case "PATCH":
                    response = handlePatchRequest(exchange,paths);
                    break;

                default:
                    http_code = 405; // Method Not Allowed
                    response = generate_error("Method not supported");
                    break;
            }
        }
        catch(OrangeException e){
            http_code = e.http_code;
            response = generate_error(e.getMessage());
        }
        catch(Exception e){
            http_code = 500; // Internal Server Error
            response = generate_error("An internal server error occurred.");
            e.printStackTrace();
        }
        finally {
            send_Response(exchange, response, http_code);
        }
    }

    private String handlePatchRequest(HttpExchange exchange , String [] paths) throws IOException, OrangeException {
        String token = JwtUtil.get_token_from_server(exchange);
        String response = "";
        if(paths.length == 3){
            if (!JwtUtil.validateToken(token)) {
                throw new InvalidTokenexception();
            }
            if (!JwtUtil.extractRole(token).equals("courier")) {
                throw new ForbiddenroleException();
            }
            if (!paths[2].matches("\\d+")){
                throw new InvalidInputException("item id");
            }
            JSONObject jsonobject = getJsonObject(exchange);
            Long orderId = Long.valueOf(paths[2]);
            Basket basket = basketDAO.getBasket(orderId);
            String statusString = jsonobject.get("status").toString();

            if(invalidInput(jsonobject).isEmpty()){
                if (statusString.equals("accepted") && basket.getStateofCart()!=StateofCart.served)
                    throw new InvalidInputException("order_id");

                if(statusString.equals("received") && basket.getStateofCart() != StateofCart.acceptedbycourier)
                    throw new InvalidInputException("order_id");

                if(statusString.equals("delivered") && basket.getStateofCart() != StateofCart.received)
                    throw new InvalidInputException("order_id");
                StateofCart state;
                if(statusString.equals("accepted")){
                    basket.setCourier_id(JwtUtil.extractSubject(token));
                    state = StateofCart.acceptedbycourier;
                }
                else {
                    state = StateofCart.valueOf(statusString);
                }
                basket.setStateofCart(state);
                basketDAO.updateBasket(basket);

                Map<Long, Integer> items = basket.getItems();
                JSONArray itemIdsArray = new JSONArray(items.keySet());

                JSONObject responseJson = new JSONObject();
                responseJson.put("message", statusString);
                responseJson.put("order", getBasketJsonObject(basket,itemIdsArray));

                response = responseJson.toString();
            }

            else {
                response = generate_error("Invalid "+invalidInput(jsonobject).toString());
                throw new OrangeException(response, 400);

            }
        }
        else {
            throw new OrangeException("endpoint not supported", 404);
        }
        return response;
    }

    private String handleGetRequest(HttpExchange exchange , String [] paths) throws IOException, OrangeException {
        String token = JwtUtil.get_token_from_server(exchange);
        String response = "";
        if(paths.length == 3 && paths[2].equals("available")){
            if (!JwtUtil.validateToken(token)) {
                throw new InvalidTokenexception();
            }
            if (!JwtUtil.extractRole(token).equals("courier")) {
                throw new ForbiddenroleException();
            }
            List<Basket> servedBaskets = basketDAO.getBasketsByState(StateofCart.served);
            JSONArray servedBasketsJson = new JSONArray();
            for (Basket basket : servedBaskets) {
                Map<Long, Integer> items = basket.getItems();
                JSONArray itemIdsArray = new JSONArray(items.keySet());
                servedBasketsJson.put(getBasketJsonObject(basket,itemIdsArray));
            }
            response =servedBasketsJson.toString();
        }

        else if (paths.length == 3 && paths[2].equals("history")){
            if (!JwtUtil.validateToken(token)) {
                throw new InvalidTokenexception();
            }
            if (!JwtUtil.extractRole(token).equals("courier")) {
                throw new ForbiddenroleException();
            }
            Map<String, String> queryParams = parseQueryParams(exchange.getRequestURI().getQuery());
            String search = queryParams.getOrDefault("search", null);
            String vendor = queryParams.getOrDefault("vendor", null);
            String user = queryParams.getOrDefault("user", null);

            List<Basket> baskets = basketDAO.getAllBasket();
            JSONArray basketsArray = new JSONArray();
            for (Basket basket : baskets) {
                if(basket.getCourier_id().equals(JwtUtil.extractSubject(token))) {
                    boolean matches = true;

                    if (search != null && !search.isEmpty()) {
                        matches &= basket.getAddress().contains(search);
                    }

                    if (vendor != null && !vendor.isEmpty()) {
                        matches &= restaurantDAO.get_restaurant(basket.getRes_id()).getName().contains(vendor);
                    }

                    if (user != null && !user.isEmpty()) {
                        matches &= basket.getBuyerName().contains(user);
                    }

                    if (matches) {
                        Map<Long, Integer> items = basket.getItems();
                        JSONArray itemIdsArray = new JSONArray(items.keySet());
                        basketsArray.put(getBasketJsonObject(basket, itemIdsArray));
                    }
                }
            }
            return basketsArray.toString();
        }
        else {
            throw new OrangeException("endpoint not supported", 404);
        }
        return response;
    }

    private String invalidInput(JSONObject jsonObject) throws OrangeException {
        String result = "" ;

        String [] fields = {"status"};

        for (String field : fields) {
            if (!jsonObject.has(field)) {
                result = "Invalid " + field;
                return result;
            }
        }
        try {
            Object statusObj = jsonObject.get("status");
            if (!(statusObj instanceof String)) {
                return "status";
            }
            if(!isValidState(statusObj.toString())){
                return "status";

            }
        }
        catch(Exception e){
            result = "type";
        }
        return result;
    }

    public static boolean isValidState(String value) {
        for (StateofCart state : StateofCart.values()) {
            if (state.name().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
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

    public void send_Response(HttpExchange exchange, String response, int http_code) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        long responseLength = (response == null || response.isEmpty()) ? -1 : response.getBytes().length;
        exchange.sendResponseHeaders(http_code, responseLength);

        if (responseLength > 0) {
            try(OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } else {
            exchange.getResponseBody().close();
        }
    }

    public JSONObject getBasketJsonObject(Basket basket) {
        JSONObject basketJson = new JSONObject();
        basketJson.put("id", basket.getId());
        basketJson.put("delivery_address", basket.getAddress());
        basketJson.put("customer_id",basket.getBuyerPhone()); //تو yaml نوشته باید int باشه ولی فعلا string میفرستیم
        basketJson.put("vendor_id",basket.getRes_id());
        basketJson.put("coupon_id", basket.getCoupon_id() != null ? basket.getCoupon_id() : JSONObject.NULL);
        JSONArray itemIdsArray = new JSONArray(basket.getItems().keySet());
        basketJson.put("item_ids", itemIdsArray);
        basketJson.put("raw_price",basket.getRawPrice(foodDAO));
        basketJson.put("tax_fee",basket.getTaxFee(restaurantDAO));
        basketJson.put("additional_fee",basket.getAdditionalFee(restaurantDAO));
        basketJson.put("courier_fee",basket.getCOURIER_FEE());
        basketJson.put("pay_price",basket.getPayPrice(restaurantDAO,foodDAO,couponDAO));
        basketJson.put("courier_id",basket.getCourier_id());
        basketJson.put("status",basket.getStateofCart());
        basketJson.put("created_at",basket.getCreated_at());
        basketJson.put("updated_at",basket.getUpadated_at());
        return basketJson;
    }

    public JSONObject getBasketJsonObject(Basket basket,JSONArray itemIdsArray) {
        JSONObject basketJson = new JSONObject();
        basketJson.put("id", basket.getId());
        basketJson.put("delivery_address", basket.getAddress());
        basketJson.put("customer_id",basket.getBuyerPhone()); //تو yaml نوشته باید int باشه ولی فعلا string میفرستیم
        basketJson.put("vendor_id",basket.getRes_id());
        basketJson.put("coupon_id", basket.getCoupon_id() != null ? basket.getCoupon_id() : JSONObject.NULL);
        basketJson.put("item_ids", itemIdsArray);
        basketJson.put("raw_price",basket.getRawPrice(foodDAO));
        basketJson.put("tax_fee",basket.getTaxFee(restaurantDAO));
        basketJson.put("additional_fee",basket.getAdditionalFee(restaurantDAO));
        basketJson.put("courier_fee",basket.getCOURIER_FEE());
        basketJson.put("pay_price",basket.getPayPrice(restaurantDAO,foodDAO,couponDAO));
        basketJson.put("courier_id",basket.getCourier_id());
        basketJson.put("status",basket.getStateofCart());
        basketJson.put("created_at",basket.getCreated_at());
        basketJson.put("updated_at",basket.getUpadated_at());
        return basketJson;
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
}