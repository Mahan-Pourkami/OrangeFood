package Handler;

import DAO.*;
import DTO.RestaurantDTO;
import Exceptions.*;
import Model.*;
import Utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RestaurantsHandler implements HttpHandler {

    private SellerDAO sellerDAO ;
    private RestaurantDAO restaurantDAO ;
    private FoodDAO foodDAO ;


    public RestaurantsHandler(SellerDAO sellerDAO, RestaurantDAO restaurantDAO , FoodDAO foodDAO ) {
        this.sellerDAO = sellerDAO;
        this.restaurantDAO = restaurantDAO;
        this.foodDAO = foodDAO;


    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String request = exchange.getRequestMethod();
        String [] paths = exchange.getRequestURI().getPath().split("/");
        String response = "";

        try {

            switch (request) {

                case "GET":
                    System.out.println("GET res request received");
                    response=handleGetRequest(exchange, paths);
                    break;

                case "POST":
                    System.out.println("POST res request received");
                    response=handlePostRequest(exchange,paths);
                    break;

                case "PUT":
                    System.out.println("PUT res request received");
                    response=handlePutRequest(exchange,paths);
                    break;

                case "DELETE":
                    System.out.println("DELETE res request received");
                    response = handleDeleteRequest(exchange,paths);
                    break;

                default:
                    response = "Invalid res request";

            }
        }
        catch (Exception e) {
            response = "Methode not allowed res";
            e.printStackTrace();
        }
        finally {
            send_Response(exchange, response);
        }

    }

    public String handlePostRequest (HttpExchange exchange , String[] paths) throws IOException{

        String response = "";
        int http_code = 200;
        JSONObject jsonobject = getJsonObject(exchange);
        String token = JwtUtil.get_token_from_server(exchange);
        if(paths.length == 2 && paths[1].equals("restaurants")) {
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
                if(!seller.getStatue().equals(Userstatue.approved)){
                    throw new ForbiddenroleException();
                }

                RestaurantDTO.AddRestaurantDTO restaurantDTO = new RestaurantDTO.AddRestaurantDTO(jsonobject, phone , sellerDAO ,restaurantDAO);
                restaurantDTO.register();
                System.out.println("Restaurant added");
                RestaurantDTO.Addrestaurant_response restaurant_response = new RestaurantDTO.Addrestaurant_response(phone,restaurantDAO,sellerDAO);
                response = restaurant_response.response();
                http_code = 200;

            }
            catch (OrangeException e) {
                response = generate_error(e.getMessage());
                http_code = e.http_code;
            }
            catch (IllegalArgumentException e) {
                response = generate_error("Invalid Input for numbers");
                http_code = 400;
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            finally {

                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                exchange.sendResponseHeaders(http_code, response.length());

            }
        }

        else if (paths.length==4 && paths[1].equals("restaurants") && paths[3].equals("item")) {

            try {

            if(!JwtUtil.validateToken(token)) {
                throw new InvalidTokenexception();
            }

            if(!JwtUtil.extractRole(token).equals("seller")){
                throw new ForbiddenroleException();
            }

            String phone = JwtUtil.extractSubject(token);
            Seller seller = sellerDAO.getSeller(phone);

                if(!seller.getStatue().equals(Userstatue.approved)){
                    throw new ForbiddenroleException();
                }

            Long res_id = Long.parseLong(paths[2]);
            if(!seller.getRestaurant().getId().equals(res_id)) {
                throw new InvalidTokenexception();
            }

            RestaurantDTO.Add_Item_request req = new RestaurantDTO.Add_Item_request(jsonobject,res_id,foodDAO);
            System.out.println("Item added");
            RestaurantDTO.Get_item_response res = new RestaurantDTO.Get_item_response(jsonobject.getString("name"),res_id,foodDAO);
            System.out.println("Response received");
            response = res.response();
            http_code = 200;


            }

            catch (OrangeException e){
                response = generate_error(e.getMessage());
                http_code = e.http_code;
            }

            catch (IllegalArgumentException e) {
                response = generate_error("Invalid Input for numbers");
                http_code = 400;
            }

            finally {
                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                exchange.sendResponseHeaders(http_code, response.length());
            }

        }

        else if(paths.length==4 && paths[1].equals("restaurants") && paths[3].equals("menu")) {

            try{

                if(!jsonobject.has("title") || jsonobject.getString("title").isEmpty()) {
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

                if(!seller.getStatue().equals(Userstatue.approved)){
                    throw new ForbiddenroleException();
                }

                Long res_id = Long.parseLong(paths[2]);

                Restaurant restaurant = restaurantDAO.get_restaurant(res_id);

                if(restaurant == null){
                    throw new NosuchRestaurantException();
                }

                if (!seller.getRestaurant().getId().equals(res_id)) {
                    throw new InvalidTokenexception();
                }

                if(restaurant.getMenu_titles().contains(menu_title)){
                    throw new DuplicatedItemexception();
                }

                restaurant.add_menu_title(menu_title);
                restaurantDAO.updateRestaurant(restaurant);
                response = generate_msg("Menu with title : " + menu_title+ " added successfully");
                http_code = 200;
            }

            catch (OrangeException e){
                e.printStackTrace();
                response = generate_error(e.getMessage());
                http_code = e.http_code;
            }

            finally {
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

    public String handleGetRequest (HttpExchange exchange , String[] paths) throws IOException{

        String response = "";

        try {
            if(paths.length == 3 && paths[1].equals("restaurants") && paths[2].equals("mine")) {

                String token = JwtUtil.get_token_from_server(exchange);
                if(!JwtUtil.validateToken(token)){
                    throw new InvalidTokenexception();
                }
                String phone = JwtUtil.extractSubject(token);
                System.out.println(phone);
                RestaurantDTO.Addrestaurant_response restaurantDTO = new RestaurantDTO.Addrestaurant_response(phone,restaurantDAO,sellerDAO);

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
                    RestaurantDTO.Get_Foods get_req = new RestaurantDTO.Get_Foods(foodDAO, res_id);
                    response = get_req.getResponse();
                    Headers headers = exchange.getResponseHeaders();
                    headers.add("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, response.getBytes().length);
                }
                catch (OrangeException e){
                    response = generate_error(e.getMessage());
                    Headers headers = exchange.getResponseHeaders();
                    headers.add("Content-Type", "application/json");
                    exchange.sendResponseHeaders(e.http_code, response.length());
                }
            }

            else if (paths.length == 5 && paths[1].equals("restaurants") && paths[3].equals("item")) {

                try{
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

                    RestaurantDTO.Get_item_spcefic get_res = new RestaurantDTO.Get_item_spcefic(foodDAO, food_id);
                    response = get_res.getResponse();
                    Headers headers = exchange.getResponseHeaders();
                    headers.add("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, response.getBytes().length);
                }
                catch (OrangeException e){
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
                }
               catch (OrangeException e){
                   response = generate_error(e.getMessage());
                   Headers headers = exchange.getResponseHeaders();
                   headers.add("Content-Type", "application/json");
                   exchange.sendResponseHeaders(e.http_code, response.length());
               }
            }

            else if (paths.length == 5 && paths[1].equals("restaurants") && paths[3].equals("notmenu")){

                try{
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
                }
                catch (OrangeException e){
                    response = generate_error(e.getMessage());
                    Headers headers = exchange.getResponseHeaders();
                    headers.add("Content-Type", "application/json");
                    exchange.sendResponseHeaders(e.http_code, response.length());
                }
            }


            else if (paths.length ==3 && paths[1].equals("restaurants") && paths[2].equals("menu")) {


                String token = JwtUtil.get_token_from_server(exchange);
                if(!JwtUtil.validateToken(token)){
                    throw new InvalidTokenexception();
                }
                if(!JwtUtil.extractRole(token).equals("seller")) {
                    throw new ForbiddenroleException();
                }

                String phone = JwtUtil.extractSubject(token);
                Seller seller = sellerDAO.getSeller(phone);
                Restaurant restaurant = restaurantDAO.get_restaurant(seller.getRestaurant().getId());
                JSONArray jsonArray = new JSONArray();
                for(String menu_title : restaurant.get_menu_titles()){
                    jsonArray.put(menu_title);
                }
                response = jsonArray.toString();
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



        }
        catch (NumberFormatException e){

            response = generate_error("Invalid ID");
            Headers headers = exchange.getResponseHeaders();
            headers.add("Content-Type", "application/json");
            exchange.sendResponseHeaders(400, response.getBytes().length);
        }

        catch (OrangeException e) {
            response = generate_error(e.getMessage());
            Headers headers = exchange.getResponseHeaders();
            headers.add("Content-Type", "application/json");
            exchange.sendResponseHeaders(e.http_code, response.getBytes().length);
        }
        return response;
    }

    public String handlePutRequest (HttpExchange exchange , String[] paths) throws IOException {
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

                if(!seller.getStatue().equals(Userstatue.approved)){
                    throw new ForbiddenroleException();
                }

                if (seller.getRestaurant() != null && !seller.getRestaurant().getId().equals(Id)) {
                    throw new InvalidTokenexception();
                }

                RestaurantDTO.UpdateRestaurant_request update_req = new RestaurantDTO.UpdateRestaurant_request(jsonobject, phone,sellerDAO,restaurantDAO);
                RestaurantDTO.Addrestaurant_response update_response = new RestaurantDTO.Addrestaurant_response(phone,restaurantDAO,sellerDAO);
                response = update_response.response();
                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);

            }


        catch (NumberFormatException e) {
            response = generate_error("Invalid Input");
            Headers headers = exchange.getResponseHeaders();
            headers.add("Content-Type", "application/json");
            exchange.sendResponseHeaders(400, response.getBytes().length);
        }

        catch (OrangeException e) {
                response = generate_error(e.getMessage());
                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                exchange.sendResponseHeaders(e.http_code, response.getBytes().length);
        }

        catch(Exception e){
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
                if(!JwtUtil.extractRole(token).equals("seller")) {
                    throw new ForbiddenroleException();
                }
                String phone = JwtUtil.extractSubject(token);
                Seller seller = sellerDAO.getSeller(phone);

                if(!seller.getStatue().equals(Userstatue.approved)){
                    throw new ForbiddenroleException();
                }

                if (!seller.getRestaurant().getId().equals(res_id)) {
                    throw new InvalidTokenexception();
                }

                Food food = foodDAO.getFood(food_id);

                if(food==null){
                    throw new NosuchItemException();

                }

                if (!food.getRestaurantId().equals(res_id)) {
                    throw new NosuchRestaurantException();
                }


                RestaurantDTO.Update_Item_request updateItemRequest = new RestaurantDTO.Update_Item_request(jsonObject, food_id,foodDAO);
                RestaurantDTO.Get_item_response updateItemrepsonse = new RestaurantDTO.Get_item_response(jsonObject.getString("name"), res_id,foodDAO);
                response = updateItemrepsonse.response();
                http_code = 200 ;

            }

            catch (NumberFormatException e) {

                response = generate_error("Invalid Input");
                http_code = 400;

            }
            catch (OrangeException e){
                response = generate_error(e.getMessage());
                http_code = e.http_code;
            }
            finally {

                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                exchange.sendResponseHeaders(http_code, response.getBytes().length);

            }

        }

        else if(paths.length==5 && paths[1].equals("restaurants") && paths[3].equals("menu")) {
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

                if(!seller.getStatue().equals(Userstatue.approved)){
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


                if(food.getMenuTitle().contains(menu_title)){
                    throw new DuplicatedItemexception();
                }

                food.setMenuTitle(menu_title);
                foodDAO.updateFood(food);
                response = generate_msg("Item with Id :" + item_id + " added to menu with title " + menu_title + " successfully");
                http_code = 200;
            }
            catch (InvalidInputException | NumberFormatException e) {
                response = generate_error("Invalid item_id");
                http_code=400;
            }

            catch (OrangeException e){
                response = generate_error(e.getMessage());
                http_code=e.http_code;
            }

            finally {
                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                exchange.sendResponseHeaders(http_code, response.getBytes().length);
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

    public String handleDeleteRequest (HttpExchange exchange , String[] paths) throws IOException {

        String response = "";
        int http_code = 200;

        if(paths.length == 5 && paths[1].equals("restaurants") && paths[3].equals("item")) {
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

                if(food==null){
                    throw new NosuchItemException();
                }

                if (!food.getRestaurantId().equals(res_id)) {
                    throw new InvalidTokenexception();
                }

                Seller seller = sellerDAO.getSeller(phone);

                if(!seller.getStatue().equals(Userstatue.approved)){
                    throw new ForbiddenroleException();
                }

                if (!seller.getRestaurant().getId().equals(res_id)) {
                    throw new InvalidTokenexception();
                }
                if (!JwtUtil.extractRole(token).equals("seller")) {
                    throw new ForbiddenroleException();
                }

                foodDAO.deleteFood(food_id);
                response = generate_msg("Item deleted successfully");
                http_code = 200 ;
            }

            catch (OrangeException e){

                response = generate_error(e.getMessage());
                http_code=e.http_code;
            }

            finally {
                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                exchange.sendResponseHeaders(http_code, response.getBytes().length);
            }

        }

        else if(paths.length==5 && paths[1].equals("restaurants") && paths[3].equals("menu")) {

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

                if(!seller.getStatue().equals(Userstatue.approved)){
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
                foodDAO.delet_from_menu(menu_title,res_id);
                restaurantDAO.updateRestaurant(restaurant);
                response = generate_msg("menu with title " + menu_title + " deleted successfully");
                http_code = 200;

            }
            catch (OrangeException e) {
                response = generate_error(e.getMessage());
                http_code=e.http_code;
            }
            finally {
                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                exchange.sendResponseHeaders(http_code, response.getBytes().length);
            }
        }

        else if(paths.length==6 && paths[1].equals("restaurants") && paths[3].equals("menu")) {

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

                if(!seller.getStatue().equals(Userstatue.approved)){
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
            }
            catch (OrangeException e) {
                response = generate_error(e.getMessage());
                http_code=e.http_code;
            }
            finally {

                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                exchange.sendResponseHeaders(http_code, response.getBytes().length);
            }

        }

        else {
            response = generate_error("Endpoint not found");
            Headers headers = exchange.getResponseHeaders();
            headers.add("Content-Type", "application/json");
            exchange.sendResponseHeaders(404, response.getBytes().length);
        }

        return  response;

    }

    private static String invalid_input_restaurants(JSONObject jsonObject) {

        String result = "" ;

        String [] fields = {"name" , "address" , "phone" , "logoBase64" , "tax_fee" , "additional_fee"};

        for (String field : fields) {
            if(!jsonObject.has(field)) {
                result = "Invalid " + field;
                return result;
            }
        }
        return result;
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

    public String generate_error(String error) {

        JSONObject errorJson = new JSONObject();
        errorJson.put("error", error);
        return errorJson.toString();
    }

    public String generate_msg(String msg){
        JSONObject msgJson = new JSONObject();
        msgJson.put("message", msg);
        return msgJson.toString();
    }



    public void send_Response(HttpExchange exchange, String response) throws IOException {
        try(OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
        exchange.close();
    }


    private  String invalid_input_restaurant(JSONObject jsonObject) {

        String result = "" ;

        String []keywords = {"name","phone","address","tax_fee","additional_fee","logoBase64"};

        for (String keyword : keywords) {
            if(!jsonObject.has(keyword)) {
                return keyword;
            }
        }

        if(jsonObject.getString("name").isEmpty()) return keywords[0];
        if(jsonObject.getString("phone").isEmpty()) return keywords[1];
        if(jsonObject.getString("address").isEmpty()) return keywords[2];

        return result;
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


}


