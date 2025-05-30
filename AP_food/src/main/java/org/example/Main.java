
package org.example;

import DAO.*;
import Model.*;

public class Main {

    public static void main(String[] args) {

        UserDAO userDAO = new UserDAO();
        FoodDAO foodDAO = new FoodDAO();
        BuyerDAO buyerDAO = new BuyerDAO();
        BasketDAO basketDAO = new BasketDAO();
        SellerDAO sellerDAO = new SellerDAO();
        RestaurantDAO restaurantDAO = new RestaurantDAO();

        try {

           Seller u1 = new Seller("09986504331","Mehdi Sedighi" ,"xxxx" , null ,"Tehran","prof");
            Bankinfo b1 = new Bankinfo("blue","6219861806190277");
            Buyer buyer = new Buyer("09204575452","Ali Ahmadi","XXXX",null,"Tehran","prof");
            Basket b2 = new Basket(buyer);
            Restaurant res = new Restaurant("Shila","Tehran",null,"logo",u1);
            u1.setBankinfo(b1);
            sellerDAO.updateSeller(u1);
            Food f1 = foodDAO.getFood(302L);
            res.addFood(f1);
            restaurantDAO.saveRestaurant(res);
            u1.setRestaurant(res);


        } catch (Exception e) {
            e.printStackTrace();
        }


        
    }
}