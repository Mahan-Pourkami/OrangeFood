package Handler;


import DAO.FoodDAO;
import Exceptions.*;
import Model.Food;
import Utils.JwtUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemsHandler implements HttpHandler {

    FoodDAO foodDAO;

    public ItemsHandler(FoodDAO foodDAO) {

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

                case "POST":
                    response = handlePostRequest(exchange, paths);
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

    private String handleGetRequest(HttpExchange exchange, String[] paths) throws IOException, OrangeException {

        String token = JwtUtil.get_token_from_server(exchange);

        if (paths.length == 3) {
            if (!JwtUtil.validateToken(token)) {
                throw new InvalidTokenexception();
            }
            if (!JwtUtil.extractRole(token).equals("buyer")) {
                throw new ForbiddenroleException();
            }
            if (!paths[2].matches("\\d+")) {
                throw new InvalidInputException("food id");
            }

            long foodId = Long.parseLong(paths[2]);

            Food food = foodDAO.getFood(foodId);

            if (food == null) {
                throw new NosuchItemException();
            }

            // Construct the JSON object with food details
            JSONObject foodJson = new JSONObject();
            foodJson.put("id", food.getId());
            foodJson.put("name", food.getName());
            foodJson.put("imageBase64", food.getPictureUrl());
            foodJson.put("description", food.getDescription());
            foodJson.put("vendor_id", food.getRestaurant());
            foodJson.put("price", food.getPrice());
            foodJson.put("supply", food.getSupply());
            foodJson.put("keywords", food.getKeywords());

            return foodJson.toString();
        } else {
            throw new OrangeException("endpoint not supported", 404);
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
            JSONObject jsonobject = getJsonObject(exchange);

            if (invalidInputItems(jsonobject).isEmpty()) {
                List<Food> foods = foodDAO.getAllFoods();
                ArrayList<Food> foundFoods = new ArrayList<>();
                JSONArray keywordsArray = jsonobject.getJSONArray("keywords");

                //اگر قسمتی از نام و قیمتی کمتر از قیمت مضخص و تمام کی ورد های مشخص شده را به طور کامل (نه فقط قسمتی) بدون توجه به upper or lower case داشته باشد
                //اگر چیزی پیدا نشود لیست خالی
                for (Food food : foods) {
                    if (food.getName().toLowerCase().contains(jsonobject.getString("search").toLowerCase()) &&
                            (food.getPrice() <= jsonobject.getInt("price") || jsonobject.getInt("price") == 0)) {

                        if (keywordsArray.isEmpty()) {
                            foundFoods.add(food);
                            continue;
                        }

                        List<String> foodKeywords = food.getKeywords().stream()
                                .map(String::toLowerCase)
                                .collect(Collectors.toList());

                        boolean allKeywordsMatch = true;
                        for (int i = 0; i < keywordsArray.length(); i++) {
                            Object keywordObj = keywordsArray.get(i);
                            if (keywordObj instanceof String) {
                                String keyword = ((String) keywordObj).toLowerCase();
                                if (!foodKeywords.contains(keyword) && !keyword.isEmpty()) {
                                    allKeywordsMatch = false;
                                    break;
                                }
                            }
                        }

                        if (allKeywordsMatch && !food.getMenuTitle().isEmpty()) {
                            foundFoods.add(food);
                        }
                    }
                }


                JSONArray resultArray = new JSONArray();
                for (Food food : foundFoods) {
                    JSONObject foodJson = new JSONObject();
                    foodJson.put("id", food.getId());
                    foodJson.put("name", food.getName());
                    foodJson.put("imageBase64", food.getPictureUrl());
                    foodJson.put("description", food.getDescription());
                    foodJson.put("vendor_id", food.getRestaurant());
                    foodJson.put("price", food.getPrice());
                    foodJson.put("supply", food.getSupply());
                    foodJson.put("keywords", food.getKeywords());
                    resultArray.put(foodJson);
                }
                response = resultArray.toString();
            } else {
                response = generate_error("Invalid " + invalidInputItems(jsonobject));
                throw new OrangeException(response, 400);

            }

        } else {
            throw new OrangeException("endpoint not supported", 404);
        }
        return response;
    }

    private String invalidInputItems(JSONObject jsonObject) throws OrangeException {
        String[] requiredFields = {"search", "price", "keywords"};

        if (jsonObject.length() != 3) {
            return "fields.";
        }

        for (String field : requiredFields) {
            if (!jsonObject.has(field)) {
                return field;
            }
        }

        try {
            // Check 'search' is a string (can be empty)
            Object searchObj = jsonObject.get("search");
            if (!(searchObj instanceof String)) {
                return "Search";
            }

            Object priceObj = jsonObject.get("price");
            if (!(priceObj instanceof Integer)) {
                return "Price";
            }

            Object keywordsObj = jsonObject.get("keywords");
            if (!(keywordsObj instanceof JSONArray)) {
                return "Keywords";
            }

            JSONArray keywordsArray = (JSONArray) keywordsObj;
            for (int i = 0; i < keywordsArray.length(); i++) {
                if (!(keywordsArray.get(i) instanceof String)) {
                    return "keywords";
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
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        long responseLength = (response == null || response.isEmpty()) ? -1 : response.getBytes().length;
        exchange.sendResponseHeaders(http_code, responseLength);

        if (responseLength > 0) {
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } else {
            exchange.getResponseBody().close();
        }
    }
}
