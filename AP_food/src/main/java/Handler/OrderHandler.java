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
import java.util.List;
import java.util.Map;

public class OrderHandler implements HttpHandler {

    UserDAO userDAO;
    CouponDAO couponDAO;
    BasketDAO basketDAO;
    RestaurantDAO restaurantDAO;
    FoodDAO foodDAO;

    public OrderHandler(UserDAO userDAO,CouponDAO couponDAO,BasketDAO basketDAO, RestaurantDAO restaurantDAO, FoodDAO foodDAO) {
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

                case "POST":
                    response = handlePostRequest(exchange,paths);
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

    private String handlePostRequest(HttpExchange exchange , String [] paths) throws IOException, OrangeException {
        String token = JwtUtil.get_token_from_server(exchange);
        String response = "";
        if(paths.length == 2){
            if (!JwtUtil.validateToken(token)) {
                throw new InvalidTokenexception();
            }
            if (!JwtUtil.extractRole(token).equals("buyer")) {
                throw new ForbiddenroleException();
            }

            //create basket object
            JSONObject jsonrequest = getJsonObject(exchange);
            if(invalidInputItemsSubmit(jsonrequest).isEmpty()) {
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
                if(basket.getCoupon_id()!=null&&couponDAO.getCoupon(basket.getCoupon_id())!=null){
                    if(!couponDAO.getCoupon(basket.getCoupon_id()).is_valid(basket.getPayPrice(restaurantDAO,foodDAO,couponDAO))) {
                        response = generate_error("Coupon not valid");
                        throw new OrangeException(response, 400);
                    }
                    couponDAO.use_Coupon(basket.getCoupon_id());
                }
                basketDAO.saveBasket(basket);
                response = getBasketJsonObject(basket).toString();
            }
            else {
                response = generate_error("Invalid "+invalidInputItemsSubmit(jsonrequest).toString());
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
        if(paths.length == 3 && paths[2].equals("history")){
            if (!JwtUtil.validateToken(token)) {
                throw new InvalidTokenexception();
            }
            if (!JwtUtil.extractRole(token).equals("buyer")) {
                throw new ForbiddenroleException();
            }
            String userphone = JwtUtil.extractSubject(token);
            List<Object[]> basketsSum = basketDAO.getBasketIdAndPhone();
            JSONArray basketArray = new JSONArray();
            for (Object[] row : basketsSum) {
                String phone = (String) row[1];
                if(phone.equals(userphone)){
                    Basket basket = basketDAO.getBasket((Long) row[0]);
                    Map<Long, Integer> items = basket.getItems();
                    JSONArray itemIdsArray = new JSONArray(items.keySet());
                    basketArray.put(getBasketJsonObject(basket,itemIdsArray));
                }
            }
            response = basketArray.toString();
        }
        //orders/{id}
        else if(paths.length == 3){
            if (!JwtUtil.validateToken(token)) {
                throw new InvalidTokenexception();
            }
            if (!JwtUtil.extractRole(token).equals("buyer")) {
                throw new ForbiddenroleException();
            }
            if (!paths[2].matches("\\d+")){
                throw new InvalidInputException("item id");
            }
            long itemId = Integer.parseInt(paths[2]);

            if(basketDAO.getBasket(itemId)==null){
                throw new InvalidInputException("item id not found");
            }
            Basket basket = basketDAO.getBasket(itemId);
            Map<Long, Integer> items = basket.getItems();
            JSONArray itemIdsArray = new JSONArray(items.keySet());

            response = getBasketJsonObject(basket,itemIdsArray).toString();
        }
        else {
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
            if(restaurantDAO.get_restaurant((vendorIdObj != null) ? ((Integer) vendorIdObj).longValue() : null )==null){
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

        }
        catch (Exception e) {
            return "types";
        }
        return "";
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
}