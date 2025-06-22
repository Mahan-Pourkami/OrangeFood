package DTO;

import DAO.*;
import Exceptions.DuplicatedItemexception;
import Exceptions.DuplicatedUserexception;
import Exceptions.NosuchRestaurantException;
import Exceptions.UnsupportedMediaException;
import Model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RestaurantDTO {

    public static class AddRestaurantDTO {

        SellerDAO sellerDAO = new SellerDAO() ;
        RestaurantDAO restaurantDAO = new RestaurantDAO() ;

        public String name;
        public String address;
        public String phone;
        public String logoBase64;
        public Integer tax_fee;
        public Integer additional_fee;
        public String seller_phone ;

        public AddRestaurantDTO(JSONObject json,String seller_phone) throws DuplicatedUserexception, UnsupportedMediaException {

            String logo_img = json.getString("logoBase64");
            if(logo_img!=null && !logo_img.isEmpty() && !logo_img.endsWith(".png") && !logo_img.endsWith(".jpg") && !logo_img.endsWith(".jpeg")) {
                throw new UnsupportedMediaException();
            }


            this.name = json.getString("name");
            this.address = json.getString("address");
            this.phone = json.getString("phone");

            if(json.getString("logoBase64")==null || json.getString("logoBase64").isEmpty()) {
                this.logoBase64 = json.getString("logoBase64");
            }
            else this.logoBase64 = "default.png";


            this.tax_fee = json.getInt("tax_fee");
            this.additional_fee = json.getInt("additional_fee");
            this.seller_phone = seller_phone;

        }

        public void register() throws DuplicatedUserexception {

            Seller seller = sellerDAO.getSeller(seller_phone);
            if(seller.getRestaurant() != null){
                throw  new DuplicatedUserexception();
            }
            Restaurant restaurant = new Restaurant(name,address,phone,logoBase64,tax_fee,additional_fee,seller);
            restaurantDAO.saveRestaurant(restaurant);
            seller.setRestaurant(restaurant);
            sellerDAO.updateSeller(seller);

        }
    }

    public static class Addrestaurant_response {

        UserDAO userDAO = new UserDAO();
        SellerDAO sellerDAO = new SellerDAO() ;
        RestaurantDAO restaurantDAO = new RestaurantDAO() ;

        public Long id ;
        public String name ;
        public String address ;
        public String phone ;
        public String logoBase64 ;
        public Integer tax_fee ;
        public Integer additional_fee;

        public Addrestaurant_response(String phone) throws NosuchRestaurantException {

            Seller seller = sellerDAO.getSeller(phone);
            Restaurant res = seller.getRestaurant();

            if(res == null){
                throw new NosuchRestaurantException();
            }

            res = restaurantDAO.get_restaurant(res.getId());

            if(res.getLogoUrl() == null){
                res.setLogoUrl("default.png");
            }
            if(res.getTax_fee() == null){
                res.setTax_fee(0);
            }
            if(res.getAdditional_fee() == null){
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

        public String response(){

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

        SellerDAO sellerDAO = new SellerDAO() ;
        RestaurantDAO restaurantDAO = new RestaurantDAO() ;


        public String name ;
        public String address ;
        public String phone ;
        public String logoBase64 ;
        public Integer tax_fee;
        public Integer additional_fee;

        public  UpdateRestaurant_request(JSONObject json,String phone) throws NosuchRestaurantException, UnsupportedMediaException {

            String logo_img = json.getString("logoBase64");
            Seller seller = sellerDAO.getSeller(phone);
            System.out.println(seller.getfullname());
            if(seller.getRestaurant() == null){
                throw new NosuchRestaurantException();
            }
            System.out.println("Done 1");
            if(logo_img!=null && !logo_img.isEmpty() && !logo_img.endsWith(".png") && !logo_img.endsWith(".jpg") && !logo_img.endsWith(".jpeg")) {
                throw new UnsupportedMediaException();
            }
            this.name = json.getString("name");
            this.address = json.getString("address");
            this.phone = json.getString("phone");
            if(json.getString("logoBase64")!=null && !json.getString("logoBase64").isEmpty()) {
                this.logoBase64 = json.getString("logoBase64");
            }
            else this.logoBase64 = "default.png";
            this.tax_fee = json.getInt("tax_fee");
            this.additional_fee = json.getInt("additional_fee");
            Restaurant res = seller.getRestaurant();

            if(res == null) throw new NosuchRestaurantException();


            res = restaurantDAO.get_restaurant(res.getId());

            if(!this.name.isEmpty())res.setName(this.name);
            if(!this.address.isEmpty())res.setAddress(this.address);
            if(!this.phone.isEmpty())res.setPhone(this.phone);
            if(!this.logoBase64.isEmpty())res.setLogoUrl(this.logoBase64);

            res.setTax_fee(this.tax_fee);
            res.setAdditional_fee(this.additional_fee);

            restaurantDAO.updateRestaurant(res);


        }

        public void update() throws NosuchRestaurantException {

            Seller seller = sellerDAO.getSeller(phone);
        }
    }


    public static class Add_Item_request {

        SellerDAO sellerDAO = new SellerDAO() ;
        RestaurantDAO restaurantDAO = new RestaurantDAO() ;
        FoodDAO foodDAO = new FoodDAO() ;

        public String name ;
        public String logoBase64 ;
        public String description ;
        public int price;
        public int supply;
        public List<String> keywords ;

        public Add_Item_request(JSONObject json,long id) throws IOException {

            System.out.println("Done 1");
            this.name = json.getString("name");
            this.logoBase64 = json.getString("imageBase64");
            this.description = json.getString("description");
            this.price = json.getInt("price");
            this.supply = json.getInt("supply");
            System.out.println("Done 3");
            this.keywords=convertjsonarraytolist(json.getJSONArray("keywords"));
            System.out.println("Done 2");

            if(foodDAO.findFoodByName(name,id)!=null){
              throw new DuplicatedItemexception();
            }

            Food food = new Food(name,id,description,logoBase64,price,supply);
            food.setkeywords(keywords);
            foodDAO.saveFood(food);
        }
    }

    public static class Add_item_response {

        FoodDAO foodDAO = new FoodDAO() ;

        public Long id ;
        public String name ;
        public String logoBase64 ;
        public String description ;
        public int price;
        public int supply;
        public List<String> keywords ;

        public Add_item_response(String name , long res_id){

            Food food = foodDAO.findFoodByName(name,res_id);
            this.id = food.getId();
            this.name = food.getName();
            this.logoBase64=food.getPictureUrl();
            this.description = food.getDescription();
            this.price = food.getPrice();
            this.supply = food.getSupply();
            this.keywords = food.getKeywords();
        }

        public String response(long res_id) throws JsonProcessingException {

            JSONObject json = new JSONObject();
            json.put("id", this.id);
            json.put("name", this.name);
            json.put("logoBase64",this.logoBase64);
            json.put("description",this.description);
            json.put("vendor_id",res_id);
            json.put("price", this.price);
            json.put("supply", this.supply);
//   todo         System.out.println("Done`");
//            json.put("keywords", convertlisttojsonarray(this.keywords));`
            return json.toString();

        }
    }

    public static <T> List<T> convertjsonarraytolist(JSONArray js){

        List <T> list = new ArrayList<>();
        for(int i=0;i<js.length();i++){
            list.add((T)js.get(i));
        }
        return list;
    }

    public static  String convertlisttojsonarray(List<String> list) throws JsonProcessingException {

         ObjectMapper obj = new ObjectMapper();

        String msg = obj.writeValueAsString(list);
        System.out.println(msg);

        return msg;

    }

    public static class Update_Item_request {

        FoodDAO foodDAO = new FoodDAO() ;
        public String name ;
        public String logoBase64 ;
        public String description ;
        public int price;
        public int supply;
        public List<String> keywords ;

        public Update_Item_request(JSONObject json,long id) throws IOException {

            Food food = foodDAO.getFood(id);
            this.name = json.getString("name");
            this.logoBase64 = json.getString("imageBase64");
            this.description = json.getString("description");
            this.price = json.getInt("price");
            this.supply = json.getInt("supply");
            this.keywords=convertjsonarraytolist(json.getJSONArray("keywords"));


            food.setName(this.name);
            food.setDescription(this.description);
            food.setPrice(this.price);
            food.setSupply(this.supply);
            food.setPictureUrl(this.logoBase64);
            food.setkeywords(this.keywords);
            foodDAO.updateFood(food);
        }
    }

}
