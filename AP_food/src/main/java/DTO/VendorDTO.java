package DTO;

import DAO.BuyerDAO;
import DAO.FoodDAO;
import DAO.RestaurantDAO;
import Exceptions.InvalidInputException;
import Model.Buyer;
import Model.Food;
import Model.Restaurant;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class VendorDTO {


    public static class Get_Vendors {

        private RestaurantDAO restaurantDAO;
        private BuyerDAO buyerDAO;
        private String phone;
        private JSONObject jsonObject;
        private String response;

        public Get_Vendors(JSONObject json, RestaurantDAO restaurantDAO, FoodDAO foodDAO, BuyerDAO buyerDAO, String phone) throws InvalidInputException {

            this.restaurantDAO = restaurantDAO;
            this.jsonObject = json;
            this.phone = phone;
            this.buyerDAO = buyerDAO;

            if (!jsonObject.has("search")) {
                throw new InvalidInputException("Search");
            }

            List<String> keywords = new ArrayList<>();

            if (jsonObject.has("keywords")) {
                keywords = RestaurantDTO.convertjsonarraytolist(jsonObject.getJSONArray("keywords"));
            }


            Set<Restaurant> vendors = restaurantDAO.findbyfilters(jsonObject.getString("search"));

            Iterator<Restaurant> iterator = vendors.iterator();
            while (iterator.hasNext()) {
                Restaurant r = iterator.next();
                List<Food> foods = foodDAO.getFoodsByRestaurantId(r.getId());

                if (foods.isEmpty() || r.get_menu_titles().isEmpty()) {
                    iterator.remove();
                    continue;
                }


                boolean shouldRemoveRestaurant = true; // Assume we'll remove unless proven otherwise

                for (Food f : foods) {

                    if(f.getMenuTitle().isEmpty()){
                        break;
                    }
                    boolean containsAllKeywords = true;
                    for (String keyword : keywords) {

                        if (!f.getKeywords().contains(keyword) && !keyword.isEmpty()) {
                            containsAllKeywords = false;
                            break;
                        }
                    }

                    if (containsAllKeywords) {
                        shouldRemoveRestaurant = false;
                        break; // Found at least one matching food, keep the restaurant
                    }
                }

                if (shouldRemoveRestaurant ) {
                    iterator.remove();
                }
            }

            JSONArray jsonArray = new JSONArray();

            Buyer buyer = buyerDAO.getBuyer(phone);

            for (Restaurant r : vendors) {

                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("id", r.getId());
                jsonObject1.put("name", r.getName());
                jsonObject1.put("address", r.getAddress());
                jsonObject1.put("phone", r.getPhone());
                jsonObject1.put("tax_fee", r.getTax_fee());
                jsonObject1.put("additional_fee", r.getAdditional_fee());
                jsonObject1.put("logoBase64", r.getLogoUrl());
                jsonObject1.put("favorite :", buyer.getfavorite_restaurants().contains(r.getId()) ? "yes" : "no");
                jsonArray.put(jsonObject1);

            }

            this.response = jsonArray.toString();
        }

        public String getResponse() {
            return response;
        }
    }


    public static class Get_Restaurants {

        @JsonIgnore
        private RestaurantDAO restaurantDAO;

        @JsonIgnore
        private Restaurant restaurant;

        private long id;
        private String name;
        private String address;
        private String phone;
        private int tax_fee;
        private int additional_fee;

        public Get_Restaurants(long res_id, RestaurantDAO restaurantDAO) throws InvalidInputException {

            this.setId(res_id);
            this.restaurantDAO = restaurantDAO;

            this.restaurant = restaurantDAO.get_restaurant(res_id);
            this.setName(restaurant.getName());
            this.setAddress(restaurant.getAddress());
            this.setPhone(restaurant.getPhone());
            this.setTax_fee(restaurant.getTax_fee());
            this.setAdditional_fee(restaurant.getAdditional_fee());


        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public int getTax_fee() {
            return tax_fee;
        }

        public void setTax_fee(int tax_fee) {
            this.tax_fee = tax_fee;
        }

        public int getAdditional_fee() {
            return additional_fee;
        }

        public void setAdditional_fee(int additional_fee) {
            this.additional_fee = additional_fee;
        }
    }

    public static class See_vendor_menu {

        private Get_Restaurants vendor;

        private List<String> menu_titles;

        private Map<String, List<Food>> items = new HashMap<>();


        public See_vendor_menu(RestaurantDAO restaurantDAO, FoodDAO foodDAO, long res_id) throws InvalidInputException {


            Restaurant restaurant = restaurantDAO.get_restaurant(res_id);
            this.setVendor(new Get_Restaurants(res_id, restaurantDAO));
            this.setMenu_titles(restaurant.getMenu_titles());

            for (String menu_title : getMenu_titles()) {

                List<Food> foods = foodDAO.getFoodsByMenu(res_id, menu_title);
                getItems().put(menu_title, foods);

            }
        }

        public Get_Restaurants getVendor() {
            return vendor;
        }

        public void setVendor(Get_Restaurants vendor) {
            this.vendor = vendor;
        }

        public List<String> getMenu_titles() {
            return menu_titles;
        }

        public void setMenu_titles(List<String> menu_titles) {
            this.menu_titles = menu_titles;
        }

        @JsonAnyGetter
        @JsonUnwrapped
        public Map<String, List<Food>> getItems() {
            return items;
        }

        public void setItems(Map<String, List<Food>> items) {
            this.items = items;
        }
    }

}
