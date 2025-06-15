package DTO;

import DAO.*;
import Exceptions.DuplicatedUserexception;
import Exceptions.UnsupportedMediaException;
import Model.*;
import org.json.JSONObject;

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
            this.logoBase64 = json.getString("logoBase64");
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

        UserDAO userDAO = new UserDAO() ;
        SellerDAO sellerDAO = new SellerDAO() ;
        RestaurantDAO restaurantDAO = new RestaurantDAO() ;

        public Long id ;
        public String name ;
        public String address ;
        public String phone ;
        public String logoBase64 ;
        public Integer tax_fee ;
        public Integer additional_fee;

        public Addrestaurant_response(String phone){

            Seller seller = sellerDAO.getSeller(phone);
            Restaurant res = seller.getRestaurant();
            System.out.println(res.getId());

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
}
