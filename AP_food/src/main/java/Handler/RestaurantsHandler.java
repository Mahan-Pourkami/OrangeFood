package Handler;

import DAO.*;
import Controller.RestaurantController;
import Exceptions.*;
import Model.*;
import Utils.JwtUtil;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RestaurantsHandler implements HttpHandler {

    private SellerDAO sellerDAO;
    private RestaurantDAO restaurantDAO;
    private FoodDAO foodDAO;
    private CouponDAO couponDAO;
    private BasketDAO basketDAO;
    private TransactionTDAO transactionTDAO;
    private BuyerDAO buyerDAO;
    private UserDAO userDAO;
    private CourierDAO courierDAO;


    public RestaurantsHandler(SellerDAO sellerDAO, RestaurantDAO restaurantDAO, FoodDAO foodDAO, CouponDAO couponDAO, BasketDAO basketDAO, TransactionTDAO transactionTDAO, BuyerDAO buyerDAO, UserDAO userDAO,CourierDAO courierDAO) {


        this.sellerDAO = sellerDAO;
        this.restaurantDAO = restaurantDAO;
        this.foodDAO = foodDAO;
        this.couponDAO = couponDAO;
        this.basketDAO = basketDAO;
        this.transactionTDAO = transactionTDAO;
        this.buyerDAO = buyerDAO;
        this.userDAO = userDAO;
        this.courierDAO = courierDAO;

    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String request = exchange.getRequestMethod();
        String[] paths = exchange.getRequestURI().getPath().split("/");
        for (int i = 0; i < paths.length; i++) {
            paths[i] = URLDecoder.decode(paths[i], StandardCharsets.UTF_8);
        }
        String response = "";

        try {

            switch (request) {

                case "GET":
                    System.out.println("GET res request received");
                    response = handleGetRequest(exchange, paths);
                    break;

                case "POST":
                    System.out.println("POST res request received");
                    response = handlePostRequest(exchange, paths);
                    break;

                case "PUT":
                    System.out.println("PUT res request received");
                    response = handlePutRequest(exchange, paths);
                    break;

                case "DELETE":
                    System.out.println("DELETE res request received");
                    response = handleDeleteRequest(exchange, paths);
                    break;

                case "PATCH":
                    System.out.println("PATCH res request received");
                    response = handlePatchRequest(exchange, paths);
                    break;

                default:
                    response = "Invalid res request";

            }
        } catch (Exception e) {
            response = "Methode not allowed res";
            e.printStackTrace();
        } finally {
            send_Response(exchange, response);
        }

    }

    public String handlePostRequest(HttpExchange exchange, String[] paths) throws IOException {

        String response = "";
        int http_code = 200;
        JSONObject jsonobject = getJsonObject(exchange);
        String token = JwtUtil.get_token_from_server(exchange);
        if (paths.length == 2 && paths[1].equals("restaurants")) {
            try {

                if (!invalid_input_restaurant(jsonobject).isEmpty()) {
                    throw new InvalidInputException(invalid_input_restaurant(jsonobject));
                }

                if (token == null || !JwtUtil.validateToken(token))
                    throw new InvalidTokenexception();

                if (!JwtUtil.extractRole(token).equals("seller"))
                    throw new ForbiddenroleException();


                String phone = JwtUtil.extractSubject(token);

                Seller seller = sellerDAO.getSeller(phone);
                if (!seller.getStatue().equals(Userstatue.approved)) {
                    throw new ForbiddenroleException();
                }

                RestaurantController.AddRestaurantDTO restaurantDTO = new RestaurantController.AddRestaurantDTO(jsonobject, phone, sellerDAO, restaurantDAO);
                restaurantDTO.register();
                System.out.println("Restaurant added");
                RestaurantController.Addrestaurant_response restaurant_response = new RestaurantController.Addrestaurant_response(phone, restaurantDAO, sellerDAO);
                response = restaurant_response.response();
                http_code = 200;

            } catch (OrangeException e) {
                response = generate_error(e.getMessage());
                http_code = e.http_code;
            } catch (IllegalArgumentException e) {
                response = generate_error("Invalid Input for numbers");
                http_code = 400;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                exchange.sendResponseHeaders(http_code, response.length());

            }
        }
        else if (paths.length == 4 && paths[1].equals("restaurants") && paths[3].equals("item")) {

            try {

                if (!JwtUtil.validateToken(token)) {
                    throw new InvalidTokenexception();
                }

                if (!JwtUtil.extractRole(token).equals("seller")) {
                    throw new ForbiddenroleException();
                }

                String phone = JwtUtil.extractSubject(token);
                Seller seller = sellerDAO.getSeller(phone);

                if (!seller.getStatue().equals(Userstatue.approved)) {
                    throw new ForbiddenroleException();
                }

                Long res_id = Long.parseLong(paths[2]);
                if (!seller.getRestaurant().getId().equals(res_id)) {
                    throw new InvalidTokenexception();
                }

                RestaurantController.Add_Item_request req = new RestaurantController.Add_Item_request(jsonobject, res_id, foodDAO);
                System.out.println("Item added");
                RestaurantController.Get_item_response res = new RestaurantController.Get_item_response(jsonobject.getString("name"), res_id, foodDAO);
                System.out.println("Response received");
                response = res.response();
                http_code = 200;


            } catch (OrangeException e) {
                response = generate_error(e.getMessage());
                http_code = e.http_code;
            } catch (IllegalArgumentException e) {
                response = generate_error("Invalid Input for numbers");
                http_code = 400;
            } finally {
                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                exchange.sendResponseHeaders(http_code, response.length());
            }

        }
        else if (paths.length == 4 && paths[1].equals("restaurants") && paths[3].equals("menu")) {

            try {

                if (!jsonobject.has("title") || jsonobject.getString("title").isEmpty()) {
                    throw new InvalidInputException("title");
                }

                String menu_title = jsonobject.getString("title");

                if (!JwtUtil.validateToken(token)) {
                    throw new InvalidTokenexception();
                }
                if (!JwtUtil.extractRole(token).equals("seller")) {
                    throw new ForbiddenroleException();
                }
                String phone = JwtUtil.extractSubject(token);
                Seller seller = sellerDAO.getSeller(phone);

                if (!seller.getStatue().equals(Userstatue.approved)) {
                    throw new ForbiddenroleException();
                }

                Long res_id = Long.parseLong(paths[2]);

                Restaurant restaurant = restaurantDAO.get_restaurant(res_id);

                if (restaurant == null) {
                    throw new NosuchRestaurantException();
                }

                if (!seller.getRestaurant().getId().equals(res_id)) {
                    throw new InvalidTokenexception();
                }

                if (restaurant.getMenu_titles().contains(menu_title)) {
                    throw new DuplicatedItemexception();
                }

                restaurant.add_menu_title(menu_title);
                restaurantDAO.updateRestaurant(restaurant);
                response = generate_msg("Menu with title : " + menu_title + " added successfully");
                http_code = 200;
            } catch (OrangeException e) {
                e.printStackTrace();
                response = generate_error(e.getMessage());
                http_code = e.http_code;
            } finally {
                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                exchange.sendResponseHeaders(http_code, response.length());
            }

        }
        else if (paths.length == 4 && paths[1].equals("restaurants") && paths[2].equals("orders")) {

            if (!JwtUtil.validateToken(token)) {
                throw new InvalidTokenexception();
            }
            if (!JwtUtil.extractRole(token).equals("seller")) {
                throw new ForbiddenroleException();
            }
            if (!paths[3].matches("\\d+")) {
                throw new InvalidInputException("order_id");
            }
            try {
                if (invalid_input_orders_id(jsonobject).isEmpty()) {
                    String order_id = paths[3];
                    Long orderIdLong = Long.valueOf(order_id);
                    Basket basket = basketDAO.getBasket(orderIdLong);
                    String statusString = jsonobject.get("status").toString();
                    Buyer buyer = buyerDAO.getBuyer(basket.getBuyerPhone());
                    Seller seller = sellerDAO.getSeller(JwtUtil.extractSubject(token));
                    if (seller.getRestaurant().getId() != basket.getRes_id()) {
                        throw new InvalidInputException("order_id");
                    }
                    if ((statusString.equals("accepted") || statusString.equals("rejected")) && basket.getStateofCart() != StateofCart.payed) {
                        throw new InvalidInputException("order_id");
                    }

                    if (statusString.equals("served") && basket.getStateofCart() != StateofCart.accepted)
                        throw new InvalidInputException("order_id");
                    StateofCart state = StateofCart.valueOf(statusString);
                    basket.setStateofCart(state);
                    basket.setUpadated_at(LocalDateTime.now().toString());
                    basketDAO.updateBasket(basket);

                    /*
                    if(statusString.equals("served")){
                        foodDAO.getFood()
                    }
                    */

                    if (statusString.equals("rejected")) {
                        int price = basket.getPayPrice(restaurantDAO, foodDAO, couponDAO);
                        buyer.charge(price);
                        buyerDAO.updateBuyer(buyer);
                        String seller_id = JwtUtil.extractSubject(token);
                        TransactionT transaction = new TransactionT(orderIdLong, seller_id, "wallet", "success");
                        transactionTDAO.saveTransaction(transaction);
                        Map<Long,Integer> foods = basket.getItems();
                        for(Long foodId : foods.keySet()) {
                            Food food = foodDAO.getFood(foodId);
                            food.setSupply(food.getSupply() + 1);
                            foodDAO.updateFood(food);
                        }

                    }
                    response = generate_msg(statusString);
                } else {
                    response = generate_error("Invalid " + invalid_input_orders_id(jsonobject));
                    throw new OrangeException(response, 400);
                }
            } catch (OrangeException e) {
                response = generate_error(e.getMessage());
                http_code = e.http_code;
            } finally {
                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                exchange.sendResponseHeaders(http_code, response.length());
            }
        }

        else {
            response = generate_error("Endpoint not found");
            Headers headers = exchange.getResponseHeaders();
            headers.add("Content-Type", "application/json");
            exchange.sendResponseHeaders(404, response.getBytes().length);
        }

        return response;
    }

    public String handleGetRequest(HttpExchange exchange, String[] paths) throws IOException {

        String response = "";

        try {
            if (paths.length == 3 && paths[1].equals("restaurants") && paths[2].equals("mine")) {

                String token = JwtUtil.get_token_from_server(exchange);
                if (!JwtUtil.validateToken(token)) {
                    throw new InvalidTokenexception();
                }
                String phone = JwtUtil.extractSubject(token);
                System.out.println(phone);
                RestaurantController.Addrestaurant_response restaurantDTO = new RestaurantController.Addrestaurant_response(phone, restaurantDAO, sellerDAO);

                response = restaurantDTO.response();
                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);
            }
            else if (paths.length == 3 && paths[1].equals("restaurants") && paths[2].equals("items")) {

                try {
                    String token = JwtUtil.get_token_from_server(exchange);
                    if (!JwtUtil.validateToken(token)) {
                        throw new InvalidTokenexception();
                    }
                    if (!JwtUtil.extractRole(token).equals("seller")) {
                        throw new ForbiddenroleException();
                    }

                    String phone = JwtUtil.extractSubject(token);
                    Seller seller = sellerDAO.getSeller(phone);

                    if (!seller.getStatue().equals(Userstatue.approved)) {
                        throw new ForbiddenroleException();
                    }

                    Restaurant restaurant = seller.getRestaurant();

                    if (restaurant == null) {
                        throw new NosuchRestaurantException();
                    }

                    long res_id = restaurant.getId();
                    RestaurantController.Get_Foods get_req = new RestaurantController.Get_Foods(foodDAO, res_id);
                    response = get_req.getResponse();
                    Headers headers = exchange.getResponseHeaders();
                    headers.add("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, response.getBytes().length);
                } catch (OrangeException e) {
                    response = generate_error(e.getMessage());
                    Headers headers = exchange.getResponseHeaders();
                    headers.add("Content-Type", "application/json");
                    exchange.sendResponseHeaders(e.http_code, response.length());
                }
            }
            else if (paths.length == 5 && paths[1].equals("restaurants") && paths[3].equals("item")) {

                try {
                    long res_id = Long.parseLong(paths[2]);
                    long food_id = Long.parseLong(paths[4]);

                    String token = JwtUtil.get_token_from_server(exchange);
                    if (!JwtUtil.validateToken(token)) {
                        throw new InvalidTokenexception();
                    }
                    if (!JwtUtil.extractRole(token).equals("seller")) {
                        throw new ForbiddenroleException();
                    }
                    String phone = JwtUtil.extractSubject(token);
                    Seller seller = sellerDAO.getSeller(phone);

                    if (!seller.getStatue().equals(Userstatue.approved)) {
                        throw new ForbiddenroleException();
                    }

                    if (res_id != seller.getRestaurant().getId()) {
                        throw new InvalidTokenexception();
                    }

                    RestaurantController.Get_item_spcefic get_res = new RestaurantController.Get_item_spcefic(foodDAO, food_id);
                    response = get_res.getResponse();
                    Headers headers = exchange.getResponseHeaders();
                    headers.add("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, response.getBytes().length);
                } catch (OrangeException e) {
                    response = generate_error(e.getMessage());
                    Headers headers = exchange.getResponseHeaders();
                    headers.add("Content-Type", "application/json");
                    exchange.sendResponseHeaders(e.http_code, response.length());
                }

            }
            else if (paths.length == 5 && paths[1].equals("restaurants") && paths[3].equals("menu")) {

                try {
                    int http_code = 200;
                    String menu_title = paths[4];
                    Long res_id = Long.parseLong(paths[2]);

                    String token = JwtUtil.get_token_from_server(exchange);
                    if (!JwtUtil.validateToken(token)) {
                        throw new InvalidTokenexception();
                    }
                    if (!JwtUtil.extractRole(token).equals("seller")) {
                        throw new ForbiddenroleException();
                    }
                    String phone = JwtUtil.extractSubject(token);
                    Seller seller = sellerDAO.getSeller(phone);
                    if (!seller.getStatue().equals(Userstatue.approved)) {
                        throw new ForbiddenroleException();
                    }
                    List<Food> result = foodDAO.getFoodsByMenu(res_id, menu_title);
                    JSONArray jsonArray = new JSONArray();
                    for (Food food : result) {
                        JSONObject json = new JSONObject();
                        json.put("id", food.getId());
                        json.put("name", food.getName());
                        json.put("price", food.getPrice());
                        json.put("description", food.getDescription());
                        jsonArray.put(json);
                    }
                    http_code = 200;
                    response = jsonArray.toString();
                    Headers headers = exchange.getResponseHeaders();
                    headers.add("Content-Type", "application/json");
                    exchange.sendResponseHeaders(http_code, response.length());
                } catch (OrangeException e) {
                    response = generate_error(e.getMessage());
                    Headers headers = exchange.getResponseHeaders();
                    headers.add("Content-Type", "application/json");
                    exchange.sendResponseHeaders(e.http_code, response.length());
                }
            }
            else if (paths.length == 5 && paths[1].equals("restaurants") && paths[3].equals("notmenu")) {

                try {
                    int http_code = 200;
                    String menu_title = paths[4];
                    Long res_id = Long.parseLong(paths[2]);

                    String token = JwtUtil.get_token_from_server(exchange);
                    if (!JwtUtil.validateToken(token)) {
                        throw new InvalidTokenexception();
                    }
                    if (!JwtUtil.extractRole(token).equals("seller")) {
                        throw new ForbiddenroleException();
                    }
                    String phone = JwtUtil.extractSubject(token);
                    Seller seller = sellerDAO.getSeller(phone);
                    if (!seller.getStatue().equals(Userstatue.approved)) {
                        throw new ForbiddenroleException();
                    }
                    List<Food> result = foodDAO.foodsnotinmenu(res_id, menu_title);
                    JSONArray jsonArray = new JSONArray();
                    for (Food food : result) {
                        JSONObject json = new JSONObject();
                        json.put("id", food.getId());
                        json.put("name", food.getName());
                        json.put("price", food.getPrice());
                        json.put("description", food.getDescription());
                        jsonArray.put(json);
                    }
                    http_code = 200;
                    response = jsonArray.toString();
                    Headers headers = exchange.getResponseHeaders();
                    headers.add("Content-Type", "application/json");
                    exchange.sendResponseHeaders(http_code, response.length());
                } catch (OrangeException e) {
                    response = generate_error(e.getMessage());
                    Headers headers = exchange.getResponseHeaders();
                    headers.add("Content-Type", "application/json");
                    exchange.sendResponseHeaders(e.http_code, response.length());
                }
            }
            else if (paths.length == 3 && paths[1].equals("restaurants") && paths[2].equals("menu")) {


                String token = JwtUtil.get_token_from_server(exchange);
                if (!JwtUtil.validateToken(token)) {
                    throw new InvalidTokenexception();
                }
                if (!JwtUtil.extractRole(token).equals("seller")) {
                    throw new ForbiddenroleException();
                }

                String phone = JwtUtil.extractSubject(token);
                Seller seller = sellerDAO.getSeller(phone);
                Restaurant restaurant = restaurantDAO.get_restaurant(seller.getRestaurant().getId());
                JSONArray jsonArray = new JSONArray();
                for (String menu_title : restaurant.get_menu_titles()) {
                    jsonArray.put(menu_title);
                }
                response = jsonArray.toString();
                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);


            }
            else if (paths.length == 4 && paths[1].equals("restaurants") && paths[3].equals("orders")) {
                String token = JwtUtil.get_token_from_server(exchange);
                if (!JwtUtil.validateToken(token)) {
                    throw new InvalidTokenexception();
                }
                if (!JwtUtil.extractRole(token).equals("seller")) {
                    throw new ForbiddenroleException();
                }
                if (!paths[2].matches("\\d+")) {
                    throw new InvalidInputException("item id");
                }
                Map<String, String> queryParams = parseQueryParams(URLDecoder.decode(exchange.getRequestURI().getQuery(),StandardCharsets.UTF_8));
                String status = queryParams.getOrDefault("status", null);
                String search = queryParams.getOrDefault("search", null);
                String user = queryParams.getOrDefault("user", null);
                String courier = queryParams.getOrDefault("courier", null);

                List<Basket> baskets = basketDAO.getAllBasket();
                JSONArray basketsArray = new JSONArray();
                for (Basket basket : baskets) {
                    if (basket.getRes_id() == Long.parseLong(paths[2])) {
                        boolean matches = true;

                        if (search != null && !search.isEmpty()) {
                            boolean foodMatches = false;

                            Map<Long, Integer> foods = basket.getItems();
                            for (Long foodId : foods.keySet()) {
                                Food food = foodDAO.getFood(foodId);
                                if (food != null && food.getName().toLowerCase().contains(search.toLowerCase())) {
                                    foodMatches = true;
                                    break;
                                }
                            }
                            matches &= foodMatches;
                        }


                        if (status != null && !status.isEmpty()) {
                            matches &= basket.getStateofCart().toString().equals(status);
                        }


                        if (user != null && !user.isEmpty()) {
                            matches &= basket.getBuyerName().contains(user);
                        }
                        if (courier != null && !courier.isEmpty() ) {
                            if (basket.getCourier_id() == null || basket.getCourier_id().isEmpty()) {
                                matches &= false;
                            } else {
                                matches &= userDAO.getUserByPhone(basket.getCourier_id()).getfullname().contains(courier);
                            }
                        }

                        if (matches) {
                            Map<Long, Integer> items = basket.getItems();
                            JSONArray itemIdsArray = new JSONArray(items.keySet());
                            basketsArray.put(getBasketJsonObject(basket, itemIdsArray));
                        }
                    }
                }
                response = basketsArray.toString();
                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);
            }
            else if (paths.length == 4 && paths[1].equals("restaurants") && paths[2].equals("names")){
                Long basket_id = Long.parseLong(paths[3]);
                Basket basket = basketDAO.getBasket(basket_id);
                String username = basket.getBuyerName();
                String courier_id = basket.getCourier_id();
                String courier_name = "";
                if(courier_id!=null){
                    courier_name = courierDAO.getCourier(courier_id).getfullname();
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("user_name", username);
                jsonObject.put("courier_name", courier_name);
                response = jsonObject.toString();
                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);

            }
            else {
                response = generate_error("Endpoint not found");
                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                exchange.sendResponseHeaders(404, response.getBytes().length);
            }


        } catch (NumberFormatException e) {

            response = generate_error("Invalid ID");
            Headers headers = exchange.getResponseHeaders();
            headers.add("Content-Type", "application/json");
            exchange.sendResponseHeaders(400, response.getBytes().length);
        } catch (OrangeException e) {
            response = generate_error(e.getMessage());
            Headers headers = exchange.getResponseHeaders();
            headers.add("Content-Type", "application/json");
            exchange.sendResponseHeaders(e.http_code, response.getBytes().length);
        }
        return response;
    }

    public String handlePutRequest(HttpExchange exchange, String[] paths) throws IOException {
        String response = "";
        int http_code = 200;
        if (paths.length == 3 && paths[1].equals("restaurants")) {

            try {

                JSONObject jsonobject = getJsonObject(exchange);
                Long Id = Long.parseLong(paths[2]);
                String token = JwtUtil.get_token_from_server(exchange);

                if (!JwtUtil.validateToken(token)) {
                    throw new InvalidTokenexception();
                }

                System.out.println(Id);

                String phone = JwtUtil.extractSubject(token);
                System.out.println(phone);

                if (!JwtUtil.extractRole(token).equals("seller")) {
                    throw new ForbiddenroleException();
                }

                Seller seller = sellerDAO.getSeller(phone);

                if (!seller.getStatue().equals(Userstatue.approved)) {
                    throw new ForbiddenroleException();
                }

                if (seller.getRestaurant() != null && !seller.getRestaurant().getId().equals(Id)) {
                    throw new InvalidTokenexception();
                }

                RestaurantController.UpdateRestaurant_request update_req = new RestaurantController.UpdateRestaurant_request(jsonobject, phone, sellerDAO, restaurantDAO);
                RestaurantController.Addrestaurant_response update_response = new RestaurantController.Addrestaurant_response(phone, restaurantDAO, sellerDAO);
                response = update_response.response();
                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);

            } catch (NumberFormatException e) {
                response = generate_error("Invalid Input");
                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                exchange.sendResponseHeaders(400, response.getBytes().length);
            } catch (OrangeException e) {
                response = generate_error(e.getMessage());
                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                exchange.sendResponseHeaders(e.http_code, response.getBytes().length);
            } catch (Exception e) {
                e.printStackTrace();
                response = generate_error("Internal Server Error");
                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                exchange.sendResponseHeaders(500, response.getBytes().length);
            }
        }
        else if (paths.length == 5 && paths[1].equals("restaurants") && paths[3].equals("item")) {


            try {
                JSONObject jsonObject = getJsonObject(exchange);

                Long res_id = Long.parseLong(paths[2]);
                Long food_id = Long.parseLong(paths[4]);

                String token = JwtUtil.get_token_from_server(exchange);

                if (!JwtUtil.validateToken(token)) {
                    throw new InvalidTokenexception();
                }
                if (!JwtUtil.extractRole(token).equals("seller")) {
                    throw new ForbiddenroleException();
                }
                String phone = JwtUtil.extractSubject(token);
                Seller seller = sellerDAO.getSeller(phone);

                if (!seller.getStatue().equals(Userstatue.approved)) {
                    throw new ForbiddenroleException();
                }

                if (!seller.getRestaurant().getId().equals(res_id)) {
                    throw new InvalidTokenexception();
                }

                Food food = foodDAO.getFood(food_id);

                if (food == null) {
                    throw new NosuchItemException();

                }

                if (!food.getRestaurantId().equals(res_id)) {
                    throw new NosuchRestaurantException();
                }


                RestaurantController.Update_Item_request updateItemRequest = new RestaurantController.Update_Item_request(jsonObject, food_id, foodDAO);
                RestaurantController.Get_item_response updateItemrepsonse = new RestaurantController.Get_item_response(jsonObject.getString("name"), res_id, foodDAO);
                response = updateItemrepsonse.response();
                http_code = 200;

            } catch (NumberFormatException e) {

                response = generate_error("Invalid Input");
                http_code = 400;

            } catch (OrangeException e) {
                response = generate_error(e.getMessage());
                http_code = e.http_code;
            } finally {

                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                exchange.sendResponseHeaders(http_code, response.getBytes().length);

            }

        }
        else if (paths.length == 5 && paths[1].equals("restaurants") && paths[3].equals("menu")) {
            try {
                Long res_id = Long.parseLong(paths[2]);
                String token = JwtUtil.get_token_from_server(exchange);
                String menu_title = paths[4];
                if (!JwtUtil.validateToken(token)) {
                    throw new InvalidTokenexception();
                }
                if (!JwtUtil.extractRole(token).equals("seller")) {
                    throw new ForbiddenroleException();
                }

                String phone = JwtUtil.extractSubject(token);
                Seller seller = sellerDAO.getSeller(phone);

                if (!seller.getStatue().equals(Userstatue.approved)) {
                    throw new ForbiddenroleException();
                }

                if (!seller.getRestaurant().getId().equals(res_id)) {
                    throw new InvalidTokenexception();
                }

                Restaurant restaurant = restaurantDAO.get_restaurant(res_id);

                if (!restaurant.menu_titles.contains(menu_title)) {
                    throw new NosuchItemException();
                }

                JSONObject jsonObject = getJsonObject(exchange);
                if (!jsonObject.has("item_id")) {
                    throw new InvalidInputException("Invalid item_id");
                }

                Long item_id = jsonObject.getLong("item_id");
                Food food = foodDAO.getFood(item_id);
                if (food == null || !food.getRestaurantId().equals(res_id)) {
                    throw new NosuchItemException();
                }


                if (food.getMenuTitle().contains(menu_title)) {
                    throw new DuplicatedItemexception();
                }

                food.setMenuTitle(menu_title);
                foodDAO.updateFood(food);
                response = generate_msg("Item with Id :" + item_id + " added to menu with title " + menu_title + " successfully");
                http_code = 200;
            } catch (InvalidInputException | NumberFormatException e) {
                response = generate_error("Invalid item_id");
                http_code = 400;
            } catch (OrangeException e) {
                response = generate_error(e.getMessage());
                http_code = e.http_code;
            } finally {
                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                exchange.sendResponseHeaders(http_code, response.getBytes().length);
            }
        } else {
            response = generate_error("Endpoint not found");
            Headers headers = exchange.getResponseHeaders();
            headers.add("Content-Type", "application/json");
            exchange.sendResponseHeaders(404, response.getBytes().length);
        }


        return response;

    }

    public String handleDeleteRequest(HttpExchange exchange, String[] paths) throws IOException {

        String response = "";
        int http_code = 200;

        if (paths.length == 5 && paths[1].equals("restaurants") && paths[3].equals("item")) {
            try {
                Long res_id = Long.parseLong(paths[2]);
                Long food_id = Long.parseLong(paths[4]);
                String token = JwtUtil.get_token_from_server(exchange);
                if (!JwtUtil.validateToken(token)) {
                    throw new InvalidTokenexception();
                }
                if (!JwtUtil.extractRole(token).equals("seller")) {
                    throw new ForbiddenroleException();
                }
                String phone = JwtUtil.extractSubject(token);
                Food food = foodDAO.getFood(food_id);

                if (food == null) {
                    throw new NosuchItemException();
                }

                if (!food.getRestaurantId().equals(res_id)) {
                    throw new InvalidTokenexception();
                }

                Seller seller = sellerDAO.getSeller(phone);

                if (!seller.getStatue().equals(Userstatue.approved)) {
                    throw new ForbiddenroleException();
                }

                if (!seller.getRestaurant().getId().equals(res_id)) {
                    throw new InvalidTokenexception();
                }
                if (!JwtUtil.extractRole(token).equals("seller")) {
                    throw new ForbiddenroleException();
                }

                if (basketDAO.is_in_the_order(food_id)) {
                    throw new ForbiddenroleException();
                }

                foodDAO.deleteFood(food_id);
                response = generate_msg("Item deleted successfully");
                http_code = 200;
            } catch (OrangeException e) {

                response = generate_error(e.getMessage());
                http_code = e.http_code;
            } finally {
                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                exchange.sendResponseHeaders(http_code, response.getBytes().length);
            }

        } else if (paths.length == 5 && paths[1].equals("restaurants") && paths[3].equals("menu")) {

            try {
                Long res_id = Long.parseLong(paths[2]);
                String menu_title = paths[4];
                String token = JwtUtil.get_token_from_server(exchange);
                if (!JwtUtil.validateToken(token)) {
                    throw new InvalidTokenexception();
                }
                if (!JwtUtil.extractRole(token).equals("seller")) {
                    throw new ForbiddenroleException();
                }
                String phone = JwtUtil.extractSubject(token);
                Seller seller = sellerDAO.getSeller(phone);

                if (!seller.getStatue().equals(Userstatue.approved)) {
                    throw new ForbiddenroleException();
                }

                if (!seller.getRestaurant().getId().equals(res_id)) {
                    throw new InvalidTokenexception();
                }
                Restaurant restaurant = restaurantDAO.get_restaurant(res_id);
                if (!restaurant.get_menu_titles().contains(menu_title)) {
                    throw new NosuchItemException();
                }

                restaurant.menu_titles.remove(menu_title);
                foodDAO.delet_from_menu(menu_title, res_id);
                restaurantDAO.updateRestaurant(restaurant);
                response = generate_msg("menu with title " + menu_title + " deleted successfully");
                http_code = 200;

            } catch (OrangeException e) {
                response = generate_error(e.getMessage());
                http_code = e.http_code;
            } finally {
                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                exchange.sendResponseHeaders(http_code, response.getBytes().length);
            }
        } else if (paths.length == 6 && paths[1].equals("restaurants") && paths[3].equals("menu")) {

            Long res_id = Long.parseLong(paths[2]);
            String menu_title = paths[4];
            Long food_id = Long.parseLong(paths[5]);
            String token = JwtUtil.get_token_from_server(exchange);

            try {
                if (!JwtUtil.validateToken(token)) {
                    throw new InvalidTokenexception();
                }
                if (!JwtUtil.extractRole(token).equals("seller")) {
                    throw new ForbiddenroleException();
                }
                String phone = JwtUtil.extractSubject(token);
                Seller seller = sellerDAO.getSeller(phone);

                if (!seller.getStatue().equals(Userstatue.approved)) {
                    throw new ForbiddenroleException();
                }

                if (!seller.getRestaurant().getId().equals(res_id)) {
                    throw new InvalidTokenexception();
                }

                Restaurant restaurant = restaurantDAO.get_restaurant(res_id);

                if (!restaurant.get_menu_titles().contains(menu_title)) {
                    throw new NosuchItemException();
                }

                Food food = foodDAO.getFood(food_id);
                if (food == null || !food.getRestaurantId().equals(res_id) || !food.getMenuTitle().contains(menu_title)) {
                    throw new NosuchItemException();
                }

                food.removeMenuTitle(menu_title);
                foodDAO.updateFood(food);
                response = generate_msg("Item with Id " + food_id + " deleted from menu with title :" + menu_title + " successfully");
                http_code = 200;
            } catch (OrangeException e) {
                response = generate_error(e.getMessage());
                http_code = e.http_code;
            } finally {

                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                exchange.sendResponseHeaders(http_code, response.getBytes().length);
            }

        } else {
            response = generate_error("Endpoint not found");
            Headers headers = exchange.getResponseHeaders();
            headers.add("Content-Type", "application/json");
            exchange.sendResponseHeaders(404, response.getBytes().length);
        }

        return response;

    }

    public String handlePatchRequest(HttpExchange exchange, String[] paths) throws IOException {
        String response = "";
        int http_code = 200;
        if (paths.length == 4 && paths[1].equals("restaurants") && paths[2].equals("orders")) {
            String token = JwtUtil.get_token_from_server(exchange);

            if (!JwtUtil.validateToken(token)) {
                throw new InvalidTokenexception();
            }
            if (!JwtUtil.extractRole(token).equals("seller")) {
                throw new ForbiddenroleException();
            }
            if (!paths[3].matches("\\d+")) {
                throw new InvalidInputException("order_id");
            }
            JSONObject jsonobject = getJsonObject(exchange);
            try {
                if (invalid_input_orders_id(jsonobject).isEmpty()) {
                    String order_id = paths[3];
                    Long orderIdLong = Long.valueOf(order_id);
                    Basket basket = basketDAO.getBasket(orderIdLong);
                    String statusString = jsonobject.get("status").toString();
                    Buyer buyer = buyerDAO.getBuyer(basket.getBuyerPhone());
                    Seller seller = sellerDAO.getSeller(JwtUtil.extractSubject(token));
                    if (seller.getRestaurant().getId() != basket.getRes_id()) {
                        throw new InvalidInputException("order_id");
                    }
                    if ((statusString.equals("accepted") || statusString.equals("rejected")) && basket.getStateofCart() != StateofCart.payed) {
                        throw new InvalidInputException("order_id");
                    }

                    if (statusString.equals("served") && basket.getStateofCart() != StateofCart.accepted)
                        throw new InvalidInputException("order_id");
                    StateofCart state = StateofCart.valueOf(statusString);
                    basket.setStateofCart(state);
                    basketDAO.updateBasket(basket);
                    if (statusString.equals("rejected")) {
                        int price = basket.getPayPrice(restaurantDAO, foodDAO, couponDAO);
                        buyer.charge(price);
                        buyerDAO.updateBuyer(buyer);
                        String seller_id = JwtUtil.extractSubject(token);
                        TransactionT transaction = new TransactionT(orderIdLong, seller_id, "wallet", "success");
                        transactionTDAO.saveTransaction(transaction);
                    }
                    response = generate_msg(statusString);
                } else {
                    response = generate_error("Invalid " + invalid_input_orders_id(jsonobject));
                    throw new OrangeException(response, 400);
                }
            } catch (OrangeException e) {
                response = generate_error(e.getMessage());
                http_code = e.http_code;
            } finally {
                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                exchange.sendResponseHeaders(http_code, response.length());
            }
        }
        return response;
    }

    private static String invalid_input_restaurants(JSONObject jsonObject) {

        String result = "";

        String[] fields = {"name", "address", "phone", "logoBase64", "tax_fee", "additional_fee"};

        for (String field : fields) {
            if (!jsonObject.has(field)) {
                result = "Invalid " + field;
                return result;
            }
        }
        return result;
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

    public String generate_error(String error) {

        JSONObject errorJson = new JSONObject();
        errorJson.put("error", error);
        return errorJson.toString();
    }

    public String generate_msg(String msg) {
        JSONObject msgJson = new JSONObject();
        msgJson.put("message", msg);
        return msgJson.toString();
    }


    public void send_Response(HttpExchange exchange, String response) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
        exchange.close();
    }


    private String invalid_input_restaurant(JSONObject jsonObject) {

        String result = "";

        String[] keywords = {"name", "phone", "address", "tax_fee", "additional_fee", "logoBase64"};

        for (String keyword : keywords) {
            if (!jsonObject.has(keyword)) {
                return keyword;
            }
        }

        if (jsonObject.getString("name").isEmpty()) return keywords[0];
        if (jsonObject.getString("phone").isEmpty()) return keywords[1];
        if (jsonObject.getString("address").isEmpty()) return keywords[2];

        return result;
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

    private String invalid_input_orders_id(JSONObject jsonObject) {
        String result = "";

        String[] fields = {"status"};

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
        } catch (Exception e) {
            result = "type";
        }
        return result;
    }

}


