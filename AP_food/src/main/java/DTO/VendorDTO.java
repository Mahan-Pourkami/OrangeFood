package DTO;

import DAO.RestaurantDAO;
import Exceptions.InvalidInputException;
import Model.Restaurant;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class VendorDTO {



    public static class Get_Vendors {

        private RestaurantDAO restaurantDAO;
        private JSONObject jsonObject;
        private String response ;

        public Get_Vendors(JSONObject json , RestaurantDAO restaurantDAO) throws InvalidInputException {

            this.restaurantDAO = restaurantDAO;
            this.jsonObject = json;

            if(!jsonObject.has("search")){
                throw new InvalidInputException("Search");
            }

            List<Restaurant> vendors = restaurantDAO.findbyfilters(jsonObject.getString("search"));

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

}
