package Handler;

import DAO.*;
import Exceptions.*;
import Model.*;
import Utils.JwtUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderHandler implements HttpHandler {

    UserDAO userDAO;
    CouponDAO couponDAO;
    BasketDAO basketDAO;
    RestaurantDAO restaurantDAO;
    FoodDAO foodDAO;
    private static final int CHUNK_SIZE = 8192;
    private static final int MAX_IN_MEMORY_SIZE = 1024*1024;


    public OrderHandler(UserDAO userDAO, CouponDAO couponDAO, BasketDAO basketDAO, RestaurantDAO restaurantDAO, FoodDAO foodDAO) {
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
        String[] paths = exchange.getRequestURI().getPath().split("/");
        int http_code = 200; // Default success code

        try {
            switch (methode) {
                case "GET":
                    response = handleGetRequest(exchange, paths);
                    break;

                case "PUT" :
                    response = handlePutRequest(exchange, paths);
                    break;


                case "POST":
                    response = handlePostRequest(exchange, paths);
                    break;

                case "DELETE":
                    response = handleDeleteRequest(exchange, paths);
                    break;

                default:
                    http_code = 405; // Method Not Allowed
                    response = generate_error("Method not supported");
                    break;
            }
        } catch (OrangeException e) {
            http_code = e.http_code;
            response = generate_error(e.getMessage());
        } catch (Exception e) {
            http_code = 500; // Internal Server Error
            response = generate_error("An internal server error occurred.");
            e.printStackTrace();
        } finally {
            send_Response(exchange, response, http_code);
        }
    }

    private String handlePostRequest(HttpExchange exchange, String[] paths) throws IOException, OrangeException {
        String token = JwtUtil.get_token_from_server(exchange);
        String response = "";
        if (paths.length == 2) {
            if (!JwtUtil.validateToken(token)) {
                throw new InvalidTokenexception();
            }
            if (!JwtUtil.extractRole(token).equals("buyer")) {
                throw new ForbiddenroleException();
            }

            JSONObject jsonrequest = getJsonObject(exchange);
            if (invalidInputItemsSubmit(jsonrequest).isEmpty()) {
                User buyer = userDAO.getUserByPhone(JwtUtil.extractSubject(token));

                Basket basket = new Basket(
                        buyer,
                        (String) jsonrequest.get("delivery_address"),
                        (int) jsonrequest.get("vendor_id"),
                        jsonrequest.isNull("coupon_id") ? null : (Integer) jsonrequest.get("coupon_id"));

                JSONArray itemsArray = jsonrequest.getJSONArray("items");
                for (int i = 0; i < itemsArray.length(); i++) {
                    JSONObject item = itemsArray.getJSONObject(i);
                    int itemId = item.getInt("item_id");
                    int quantity = item.getInt("quantity");
                    basket.addItem(itemId, quantity);
                    foodDAO.add_to_cart(itemId, quantity);
                }

                if (basket.getCoupon_id() != null && couponDAO.getCoupon(basket.getCoupon_id()) != null) {
                    if (!couponDAO.getCoupon(basket.getCoupon_id()).is_valid(basket.getPayPrice(restaurantDAO, foodDAO, couponDAO))) {
                        response = generate_error("Coupon not valid");
                        throw new OrangeException(response, 400);
                    }
                    couponDAO.use_Coupon(basket.getCoupon_id());
                }
                basketDAO.saveBasket(basket);
                response = getBasketJsonObject(basket).toString();
            } else {
                response = generate_error("Invalid " + invalidInputItemsSubmit(jsonrequest).toString());
                throw new OrangeException(response, 400);

            }
        }
        if(paths.length == 3 && paths[2].equals("setcoupon")) { //{"oreder_id" : 0 , "coupon_code" : "", "price" : 0}
            if (!JwtUtil.validateToken(token)) {
                throw new InvalidTokenexception();
            }
            if (!JwtUtil.extractRole(token).equals("buyer")) {
                throw new ForbiddenroleException();
            }

            JSONObject jsonrequest = getJsonObject(exchange);
            Basket basket = basketDAO.getBasket(jsonrequest.getLong("order_id"));
            String coupon_code = jsonrequest.getString("coupon_code");
            Coupon coupon = couponDAO.findCouponByCode(coupon_code);
            if (coupon == null) {
                    throw new OrangeException("invalid code", 404);
            }
            if(!coupon.is_valid(jsonrequest.getInt("price"))){
                throw new OrangeException("invalid code", 404);
            }
            if(basket.getStateofCart()!=StateofCart.waiting){
                throw new OrangeException("invalid basket", 404);
            }
            if(basket.getCoupon_id() == coupon.getId()){
                throw new OrangeException("code used", 404);
            }
            basket.setCoupon_id(coupon.getId());
            basketDAO.updateBasket(basket);
            response = generate_msg(Integer.toString(basket.getPayPrice(restaurantDAO, foodDAO, couponDAO)));

        }
        else {
            throw new OrangeException("endpoint not supported", 404);
        }
        return response;
    }

    private String handlePutRequest(HttpExchange exchange, String[] paths) throws IOException, OrangeException {

        String response = "";
        String token = JwtUtil.get_token_from_server(exchange);

        if(paths.length == 4 && paths[2].equals("cart")) {

            if (!JwtUtil.validateToken(token)) {
                throw new InvalidTokenexception();
            }
            if (!JwtUtil.extractRole(token).equals("buyer")) {
                throw new ForbiddenroleException();
            }

            User user = userDAO.getUserByPhone(JwtUtil.extractSubject(token));
            long item_id = Long.parseLong(paths[3]);
            Food food = foodDAO.getFood(item_id);
            if(basketDAO.getOpenBasket(food.getRestaurantId())==null) {

                Basket basket = new Basket(user,user.getAddress(), Math.toIntExact(food.getRestaurantId()),0);
                basket.addItem(item_id, 1);
                basketDAO.saveBasket(basket);
                foodDAO.add_to_cart(item_id, 1);
                return generate_msg("item added");
            }
            else {

                Basket basket = basketDAO.getOpenBasket(food.getRestaurantId());
                basket.addItem(item_id, 1);
                basketDAO.updateBasket(basket);
                foodDAO.add_to_cart(item_id, 1);
                return generate_msg("item updated");
            }
        }

        return response;
    }

    private String handleDeleteRequest(HttpExchange exchange, String[] paths) throws IOException, OrangeException {

        String response = "";
        String token = JwtUtil.get_token_from_server(exchange);

        if(paths.length == 4 && paths[2].equals("cart")) {
            if (!JwtUtil.validateToken(token)) {
                throw new InvalidTokenexception();
            }

            if (!JwtUtil.extractRole(token).equals("buyer")) {
                throw new ForbiddenroleException();
            }

            User user = userDAO.getUserByPhone(JwtUtil.extractSubject(token));
            long item_id = Long.parseLong(paths[3]);
            Food food = foodDAO.getFood(item_id);

            if(basketDAO.getOpenBasket(food.getRestaurantId())==null) {
                throw new NosuchItemException("Basket does not exist");
            }

            Basket basket = basketDAO.getOpenBasket(food.getRestaurantId());
            food.setSupply(food.getSupply() +basket.getItems().get(food.getId()));
            basket.removeItem(item_id);

            foodDAO.updateFood(food);
            if(basket.getItems().isEmpty()) {
                basketDAO.deleteBasket(basket.getId());
            }
            else {
                basketDAO.updateBasket(basket);
            }
            return generate_msg("item removed");
        }

        return response;
    }

    private String handleGetRequest(HttpExchange exchange, String[] paths) throws IOException, OrangeException {
        String token = JwtUtil.get_token_from_server(exchange);
        String response = "";
        if (paths.length == 3 && paths[2].equals("history")) {
            if (!JwtUtil.validateToken(token)) {
                throw new InvalidTokenexception();
            }
            if (!JwtUtil.extractRole(token).equals("buyer")) {
                throw new ForbiddenroleException();
            }
            Map<String, String> queryParams = parseQueryParams(exchange.getRequestURI().getQuery());
            String search = queryParams.getOrDefault("search", null);
            String vendor = queryParams.getOrDefault("vendor", null);

            List<Basket> baskets = basketDAO.getbuyersbasket(JwtUtil.extractSubject(token));
            JSONArray basketsArray = new JSONArray();
            for (Basket basket : baskets) {
                String buyerPhone = basket.getBuyerPhone();
                if (buyerPhone != null && buyerPhone.equals(JwtUtil.extractSubject(token))) {
                    boolean matches = true;

                    if (search != null && !search.isEmpty()) {
                        boolean match_food = false;

                        for (long food_id : basket.getItems().keySet()) {
                            Food food = foodDAO.getFood(food_id);
                            if (food == null) {
                                continue;
                            }
                            if (food.getName().toLowerCase().contains(search.toLowerCase())) {
                                match_food = true;
                                break;
                            }
                        }
                        matches &= match_food;

                        if (!matches) {
                            continue;
                        }
                    }

                    if (vendor != null && !vendor.isEmpty()) {
                        matches &= restaurantDAO.get_restaurant(basket.getRes_id()).getName().toLowerCase().contains(vendor.toLowerCase());
                    }

                    if (matches) {
                        Map<Long, Integer> items = basket.getItems();
                        JSONArray itemIdsArray = new JSONArray(items.keySet());
                        basketsArray.put(getBasketJsonObject(basket, itemIdsArray));
                    }
                }
            }
            response = basketsArray.toString();
        }
        else if(paths.length == 4 && paths[2].equals("orderitems")) {
            Long order_id = Long.valueOf(paths[3]);
            if(!basketDAO.existBasket(order_id)) {
                throw new NosuchItemException("Basket does not exist");
            }
            Basket basket = basketDAO.getBasket(order_id);
            Map<Long,Integer> foods = basket.getItems();
            JSONArray foodsArray = new JSONArray();
            for(Long id : foods.keySet()) {
                JSONObject food = new JSONObject();
                food.put("id", id);
                food.put("quantity", foods.get(id));
                foodsArray.put(food);
            }
            response = foodsArray.toString();
        }
        else if(paths.length == 4 && paths[2].equals("item")){
            Long itemId = Long.valueOf(paths[3]);
            Food food = foodDAO.getFood(itemId);
            response = getFoodJsonObject(food).toString();
        }
        else if (paths.length == 3) {
            if (!JwtUtil.validateToken(token)) {
                throw new InvalidTokenexception();
            }
            if (!JwtUtil.extractRole(token).equals("buyer")) {
                throw new ForbiddenroleException();
            }
            if (!paths[2].matches("\\d+")) {
                throw new InvalidInputException("item id");
            }
            long itemId = Integer.parseInt(paths[2]);

            if (basketDAO.getBasket(itemId) == null) {
                throw new InvalidInputException("item id not found");
            }
            Basket basket = basketDAO.getBasket(itemId);
            Map<Long, Integer> items = basket.getItems();
            JSONArray itemIdsArray = new JSONArray(items.keySet());

            response = getBasketJsonObject(basket, itemIdsArray).toString();
        } else {
            throw new OrangeException("endpoint not supported", 404);
        }
        return response;
    }

    private String invalidInputItemsSubmit(JSONObject jsonObject) throws OrangeException {
        String[] requiredFields = {"delivery_address", "vendor_id", "coupon_id", "items"};

        if (jsonObject.length() != 4) {
            return "fields.";
        }

        for (String field : requiredFields) {
            if (!jsonObject.has(field)) {
                return field;
            }
        }

        try {
            Object deliveryObj = jsonObject.get("delivery_address");
            if (!(deliveryObj instanceof String)) {
                return "delivery_address";
            }
            Object vendorIdObj = jsonObject.get("vendor_id");
            if (!(vendorIdObj instanceof Integer)) {
                return "vendor_id";
            }
            if (restaurantDAO.get_restaurant((vendorIdObj != null) ? ((Integer) vendorIdObj).longValue() : null) == null) {
                return "vendor_id";
            }

            if (!jsonObject.isNull("coupon_id")) {
                Object couponIdObj = jsonObject.get("coupon_id");
                if (!(couponIdObj instanceof Integer)) {
                    return "coupon_id";
                }
            }


            Object itemsObj = jsonObject.get("items");
            if (!(itemsObj instanceof JSONArray)) {
                return "items";
            }

            JSONArray itemsArray = (JSONArray) itemsObj;
            for (int i = 0; i < itemsArray.length(); i++) {
                Object itemObj = itemsArray.get(i);
                if (!(itemObj instanceof JSONObject)) {
                    return "items[" + i + "]";
                }

                JSONObject itemJson = (JSONObject) itemObj;

                Object itemIdObj = itemJson.opt("item_id");
                Object quantityObj = itemJson.opt("quantity");

                if (!(itemIdObj instanceof Integer)) {
                    return "items[" + i + "].item_id";
                }

                if (!(quantityObj instanceof Integer)) {
                    return "items[" + i + "].quantity";
                }

                Long itemId = (itemIdObj != null) ? ((Integer) itemIdObj).longValue() : null;
                if (foodDAO.getFood(itemId) == null) {
                    return "items[" + i + "].item_id";
                }

            }

        } catch (Exception e) {
            return "types";
        }
        return "";
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

    private String generate_error(String error) {

        JSONObject errorJson = new JSONObject();
        errorJson.put("error", error);
        return errorJson.toString();
    }

    private String generate_msg(String msg) {
        JSONObject msgJson = new JSONObject();
        msgJson.put("message", msg);
        return msgJson.toString();
    }

    public void send_Response(HttpExchange exchange, String response, int http_code) throws IOException {
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");

        if (responseBytes.length <= MAX_IN_MEMORY_SIZE) {
            exchange.sendResponseHeaders(http_code, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        }
        else {
            exchange.sendResponseHeaders(http_code, 0);
            try (OutputStream os = exchange.getResponseBody()) {
                int offset = 0;
                while (offset < responseBytes.length) {
                    int length = Math.min(CHUNK_SIZE, responseBytes.length - offset);
                    os.write(responseBytes, offset, length);
                    offset += length;
                }
            }
        }
    }

    public JSONObject getBasketJsonObject(Basket basket) {
        JSONObject basketJson = new JSONObject();
        basketJson.put("id", basket.getId());
        basketJson.put("delivery_address", basket.getAddress());
        basketJson.put("customer_id", basket.getBuyerPhone()); //تو yaml نوشته باید int باشه ولی فعلا string میفرستیم
        basketJson.put("vendor_id", basket.getRes_id());
        basketJson.put("coupon_id", basket.getCoupon_id() != null ? basket.getCoupon_id() : JSONObject.NULL);
        JSONArray itemIdsArray = new JSONArray(basket.getItems().keySet());
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

    public JSONObject getBasketJsonObject(Basket basket, JSONArray itemIdsArray) {
        JSONObject basketJson = new JSONObject();
        basketJson.put("id", basket.getId());
        basketJson.put("delivery_address", basket.getAddress());
        basketJson.put("customer_id", basket.getBuyerPhone()); //تو yaml نوشته باید int باشه ولی فعلا string میفرستیم
        basketJson.put("vendor_id", basket.getRes_id());
        basketJson.put("coupon_id", basket.getCoupon_id() != null ? basket.getCoupon_id() : JSONObject.NULL);
        basketJson.put("item_ids", itemIdsArray);
        JSONArray foods = new JSONArray();
        for (int i = 0; i < itemIdsArray.length(); i++) {
            Food food = foodDAO.getFood(itemIdsArray.getLong(i));
            if(food == null) continue;
            JSONObject foodJson = new JSONObject();
            foodJson.put("id", food.getId());
            foodJson.put("name", food.getName());
            foodJson.put("price", food.getPrice());
            foodJson.put("imageBase64",food.getPictureUrl());
            foodJson.put("quantity",basket.getItems().get(food.getId()));
            foods.put(foodJson);
        }
        basketJson.put("items", foods);
        basketJson.put("raw_price", basket.getRawPrice(foodDAO));
        basketJson.put("tax_fee", basket.getTaxFee(restaurantDAO));
        basketJson.put("additional_fee", basket.getAdditionalFee(restaurantDAO));
        basketJson.put("courier_fee", basket.getCOURIER_FEE());
        basketJson.put("pay_price", basket.getPayPrice(restaurantDAO, foodDAO, couponDAO));
        basketJson.put("courier_id", basket.getCourier_id());
        basketJson.put("vendor_name", restaurantDAO.get_restaurant(basket.getRes_id()).getName());
        basketJson.put("status", basket.getStateofCart());
        basketJson.put("created_at", basket.getCreated_at());
        basketJson.put("restaurant_prof" , restaurantDAO.get_restaurant(basket.getRes_id()).getLogoUrl());
        basketJson.put("updated_at", basket.getUpadated_at());
        return basketJson;
    }

    public JSONObject getFoodJsonObject(Food food) {
        JSONObject foodJson = new JSONObject();
        foodJson.put("id", food.getId());
        foodJson.put("name", food.getName());
        foodJson.put("price", food.getPrice());
        foodJson.put("imageBase64",food.getPictureUrl());
        foodJson.put("stockquantity",food.getSupply());
        return foodJson;
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