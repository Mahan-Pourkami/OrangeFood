package DTO;

import DAO.FoodDAO;
import DAO.RestaurantDAO;
import Exceptions.InvalidInputException;
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
        private JSONObject jsonObject;
        private String response ;

        public Get_Vendors(JSONObject json , RestaurantDAO restaurantDAO , FoodDAO foodDAO) throws InvalidInputException {

            this.restaurantDAO = restaurantDAO;
            this.jsonObject = json;

            if(!jsonObject.has("search")){
                throw new InvalidInputException("Search");
            }

            List<String> keywords = RestaurantDTO.convertjsonarraytolist(jsonObject.getJSONArray("keywords"));
            List<Food> foods = foodDAO.getAllFoods();

            Set<Restaurant> vendors = restaurantDAO.findbyfilters(jsonObject.getString("search"));


            for(String key : keywords){
            for(Food food : foods) {
                if(food.getMenuTitle()!=null && !food.getMenuTitle().isEmpty()) {

                    for (String keyword : food.getKeywords()) {
                        if ( keyword.contains(key)) {
                            vendors.add(restaurantDAO.get_restaurant(food.getRestaurant()));
                        }
                    }
                }
            }
            }

            JSONArray jsonArray = new JSONArray();

            for(Restaurant r : vendors){

                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("id", r.getId());
                jsonObject1.put("name", r.getName());
                jsonObject1.put("address", r.getAddress());
                jsonObject1.put("phone", r.getPhone());
                jsonObject1.put("tax_fee",r.getTax_fee());
                jsonObject1.put("additional_fee",r.getAdditional_fee());
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

        private Map<String,List<Food>> items = new HashMap<>();


        public See_vendor_menu( RestaurantDAO restaurantDAO, FoodDAO foodDAO , long res_id) throws InvalidInputException {


            Restaurant restaurant = restaurantDAO.get_restaurant(res_id);
            this.setVendor(new Get_Restaurants(res_id,restaurantDAO));
            this.setMenu_titles(restaurant.getMenu_titles());

            for (String menu_title : getMenu_titles()) {

                List<Food> foods= foodDAO.getFoodsByMenu(res_id,menu_title);
                getItems().put(menu_title,foods);

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
