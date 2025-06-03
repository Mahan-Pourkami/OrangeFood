package DTO;

import DAO.*;
import Exceptions.DuplicatedUserexception;
import Model.Bankinfo;
import Model.Buyer;
import Model.Courier;
import Model.Seller;

public class RestaurantDTO {
    public static class AddRestaurantDTO {

        UserDAO userDAO ;
        SellerDAO sellerDAO;
        RestaurantDAO restaurantDAO;


        public String name;
        public String address;
        public String phone;
        public String logoBase64;
        public String tax_fee;
        public String additional_fee;

        public AddRestaurantDTO(String fullName, String phone, String password, String role, String address, String email, String profileImageBase64,String bankname , String account) {

            this.name = fullName;
            this.phone = phone;
            this.address = password;
            this.logoBase64 = role;
            this.tax_fee = address;
            this.additional_fee = email;
            this.userDAO = new UserDAO();
            this.sellerDAO = new SellerDAO();
            this.restaurantDAO = new RestaurantDAO();
        }

        public void addRestaurant() throws DuplicatedUserexception {

        }

    }
}
