package DTO;

import DAO.*;
import Exceptions.*;
import Model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RestaurantDTO {

    public static class AddRestaurantDTO {

        SellerDAO sellerDAO;
        RestaurantDAO restaurantDAO;

        public String name;
        public String address;
        public String phone;
        public String logoBase64;
        public Integer tax_fee;
        public Integer additional_fee;
        public String seller_phone;

        public AddRestaurantDTO(JSONObject json, String seller_phone, SellerDAO sellerDAO, RestaurantDAO restaurantDAO) throws DuplicatedUserexception, UnsupportedMediaException {

            this.sellerDAO = sellerDAO;
            this.restaurantDAO = restaurantDAO;

            String logo_img = json.getString("logoBase64");
            if (logo_img != null && !logo_img.isEmpty() && !logo_img.endsWith(".png") && !logo_img.endsWith(".jpg") && !logo_img.endsWith(".jpeg")) {
                throw new UnsupportedMediaException();
            }


            this.name = json.getString("name");
            this.address = json.getString("address");
            this.phone = json.getString("phone");

            if (json.getString("logoBase64") != null && !json.getString("logoBase64").isEmpty()) {
                this.logoBase64 = json.getString("logoBase64");
            }

            this.tax_fee = json.getInt("tax_fee");
            this.additional_fee = json.getInt("additional_fee");
            this.seller_phone = seller_phone;

        }

        public void register() throws DuplicatedUserexception {

            Seller seller = sellerDAO.getSeller(seller_phone);
            if (seller.getRestaurant() != null) {
                throw new DuplicatedUserexception();
            }
            Restaurant restaurant = new Restaurant(name, address, phone, logoBase64, tax_fee, additional_fee, seller);
            restaurantDAO.saveRestaurant(restaurant);
            seller.setRestaurant(restaurant);
            sellerDAO.updateSeller(seller);

        }
    }

    public static class Addrestaurant_response {

        SellerDAO sellerDAO;
        RestaurantDAO restaurantDAO;

        public Long id;
        public String name;
        public String address;
        public String phone;
        public String logoBase64;
        public Integer tax_fee;
        public Integer additional_fee;

        public Addrestaurant_response(String phone, RestaurantDAO restaurantDAO, SellerDAO sellerDAO) throws NosuchRestaurantException {


            this.sellerDAO = sellerDAO;
            this.restaurantDAO = restaurantDAO;

            Seller seller = sellerDAO.getSeller(phone);
            Restaurant res = seller.getRestaurant();

            if (res == null) {
                throw new NosuchRestaurantException();
            }

            res = restaurantDAO.get_restaurant(res.getId());

            if (res.getLogoUrl() == null) {
                res.setLogoUrl("default.png");
            }
            if (res.getTax_fee() == null) {
                res.setTax_fee(0);
            }
            if (res.getAdditional_fee() == null) {
                res.setAdditional_fee(0);
            }

            this.id = res.getId();
            this.name = res.getName();
            this.address = res.getAddress();
            this.phone = res.getPhone();
            this.logoBase64 = res.getLogoUrl();
            this.tax_fee = res.getTax_fee();
            this.additional_fee = res.getAdditional_fee();

        }

        public String response() {

            JSONObject json = new JSONObject();
            json.put("id", id);
            json.put("name", name);
            json.put("address", address);
            json.put("phone", phone);
            json.put("logoBase64", logoBase64);
            json.put("tax_fee", tax_fee);
            json.put("additional_fee", additional_fee);
            System.out.println(json.toString());
            return json.toString();
        }
    }

    public static class UpdateRestaurant_request {

        SellerDAO sellerDAO;
        RestaurantDAO restaurantDAO;


        public UpdateRestaurant_request(JSONObject json, String phone, SellerDAO sellerDAO, RestaurantDAO restaurantDAO) throws NosuchRestaurantException, UnsupportedMediaException, InvalidInputException {


            this.sellerDAO = sellerDAO;
            this.restaurantDAO = restaurantDAO;


            Seller seller = sellerDAO.getSeller(phone);
            System.out.println(seller.getfullname());
            if (seller.getRestaurant() == null) {
                throw new NosuchRestaurantException();
            }

            Restaurant res = seller.getRestaurant();

            if (res == null) throw new NosuchRestaurantException();

            res = restaurantDAO.get_restaurant(res.getId());

            if (json.has("logoBase64")) {

                String logo_img = json.getString("logoBase64");
                if (logo_img != null && !logo_img.isEmpty() && !logo_img.endsWith(".png") && !logo_img.endsWith(".jpg") && !logo_img.endsWith(".jpeg")) {
                    throw new UnsupportedMediaException();
                }
                res.setLogoUrl(logo_img);
            }

            if (json.has("name")) {
                String name = json.getString("name");
                if (name.isEmpty()) throw new InvalidInputException("name");
                res.setName(name);
            }
            if (json.has("address")) {
                String address = json.getString("address");
                if (address.isEmpty()) throw new InvalidInputException("address");
                res.setAddress(address);
            }
            if (json.has("phone")) {
                String res_phone = json.getString("phone");
                if (res_phone.isEmpty()) throw new InvalidInputException("phone");
                res.setPhone(res_phone);
            }
            if (json.has("tax_fee")) {
                int tax_fee = json.getInt("tax_fee");
                if (tax_fee < 0) throw new InvalidInputException("tax_fee");
                res.setTax_fee(tax_fee);
            }
            if (json.has("additional_fee")) {
                int additional_fee = json.getInt("additional_fee");
                if (additional_fee < 0) throw new InvalidInputException("additional_fee");
                res.setAdditional_fee(additional_fee);
            }

            restaurantDAO.updateRestaurant(res);
        }

    }


    public static class Add_Item_request {

        FoodDAO foodDAO;

        public String name;
        public String logoBase64;
        public String description;
        public int price;
        public int supply;
        public List<String> keywords;

        public Add_Item_request(JSONObject json, long id, FoodDAO foodDAO) throws IOException {


            this.foodDAO = foodDAO;

            this.name = json.getString("name");
            this.logoBase64 = json.getString("imageBase64");
            this.description = json.getString("description");
            this.price = json.getInt("price");
            this.supply = json.getInt("supply");
            this.keywords = convertjsonarraytolist(json.getJSONArray("keywords"));

            if (name.isEmpty()) throw new InvalidInputException("Name");
            if (description.isEmpty()) throw new InvalidInputException("Description");

            if (foodDAO.findFoodByName(name, id) != null) {
                throw new DuplicatedItemexception();
            }

            if (!this.logoBase64.endsWith("png") && !this.logoBase64.endsWith("jpeg") && !this.logoBase64.endsWith("jpg") && !this.logoBase64.isEmpty()) {
                throw new UnsupportedMediaException();
            }

            Food food = new Food(name, id, description, logoBase64, price, supply);
            food.setkeywords(keywords);
            foodDAO.saveFood(food);
        }
    }

    public static class Get_item_response {

        FoodDAO foodDAO;

        public Long id;
        public String name;
        public String logoBase64;
        public String description;
        public int price;
        public int supply;
        public List<String> keywords;
        private long res_id;

        public Get_item_response(String name, long res_id, FoodDAO foodDAO) {

            this.foodDAO = foodDAO;
            Food food = foodDAO.findFoodByName(name, res_id);
            this.res_id = res_id;
            this.id = food.getId();
            this.name = food.getName();
            this.logoBase64 = food.getPictureUrl();
            this.description = food.getDescription();
            this.price = food.getPrice();
            this.supply = food.getSupply();
            this.keywords = food.getKeywords();
        }

        public String response() throws JsonProcessingException {

            JSONObject json = new JSONObject();
            json.put("id", this.id);
            json.put("name", this.name);
            json.put("logoBase64", this.logoBase64);
            json.put("description", this.description);
            json.put("vendor_id", this.res_id);
            json.put("price", this.price);
            json.put("supply", this.supply);
            json.put("keywords", convertlisttojsonarray(this.keywords));
            return json.toString();

        }
    }

    public static <T> List<T> convertjsonarraytolist(JSONArray js) {

        List<T> list = new ArrayList<>();
        for (int i = 0; i < js.length(); i++) {
            list.add((T) js.get(i));
        }
        return list;
    }

    public static <T> JSONArray convertlisttojsonarray(List<T> list) throws JsonProcessingException {

        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            jsonArray.put(list.get(i));
        }

        return jsonArray;

    }

    public static class Update_Item_request {

        FoodDAO foodDAO;


        public Update_Item_request(JSONObject json, long id, FoodDAO foodDAO) throws IOException {


            this.foodDAO = foodDAO;
            Food food = foodDAO.getFood(id);


            if (json.has("name")) {
                String name = json.getString("name");
                if (!name.equals(food.getName()) && foodDAO.findFoodByName(name, food.getRestaurantId()) != null) {
                    throw new DuplicatedItemexception();
                }
                if (name.isEmpty()) throw new InvalidInputException("Name");
                food.setName(name);
            }

            if (json.has("description")) {
                String description = json.getString("description");
                if (description.isEmpty()) throw new InvalidInputException("Description");
                food.setDescription(description);
            }

            if (json.has("logoBase64")) {
                String logoBase64 = json.getString("logoBase64");
                if (!logoBase64.endsWith(".png") && !logoBase64.endsWith(".jpg") && !logoBase64.endsWith(".jpeg"))
                    throw new UnsupportedMediaException();

                food.setPictureUrl(logoBase64);
            }

            if (json.has("price")) {
                int price = json.getInt("price");
                food.setPrice(price);
            }
            if (json.has("supply")) {
                int supply = json.getInt("supply");
                food.setSupply(supply);
            }
            if (json.has("keywords")) {
                JSONArray keywords = json.getJSONArray("keywords");
                List<String> keywordslist = convertjsonarraytolist(keywords);
                food.setkeywords(keywordslist);
            }

            foodDAO.updateFood(food);
        }
    }

    public static class Get_Foods {

        FoodDAO foodDAO;
        private String response;

        public Get_Foods(FoodDAO foodDAO, long id) {

            this.foodDAO = foodDAO;
            List<Food> foods = foodDAO.getFoodsByRestaurantId(id);
            JSONArray jsonArray = new JSONArray();
            for (Food food : foods) {
                JSONObject js = new JSONObject();
                js.put("id", food.getId());
                js.put("name", food.getName());
                js.put("imageBase64", food.getPictureUrl());
                js.put("description", food.getDescription());
                js.put("res_id", food.getRestaurantId());
                js.put("price", food.getPrice());
                js.put("supply", food.getSupply());
                jsonArray.put(js);
            }
            this.response = jsonArray.toString();
        }

        public String getResponse() {
            return response;
        }
    }

    public static class Get_item_spcefic {

        private FoodDAO foodDAO;
        private String response;

        public Get_item_spcefic(FoodDAO foodDAO, long id) {

            this.foodDAO = foodDAO;
            Food food = foodDAO.getFood(id);

            JSONObject js = new JSONObject();
            js.put("id", food.getId());
            js.put("name", food.getName());
            js.put("imageBase64", food.getPictureUrl());
            js.put("description", food.getDescription());
            js.put("price", food.getPrice());
            js.put("supply", food.getSupply());

            JSONArray jsarray = new JSONArray();
            List<String> keywords = food.getKeywords();
            for (String keyword : keywords) {
                jsarray.put(keyword);
            }
            js.put("keywords", jsarray);
            this.response = js.toString();
        }

        public String getResponse() {
            return response;
        }

    }

}
