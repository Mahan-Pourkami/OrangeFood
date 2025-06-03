
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

            Seller u2 = new Seller("09986404331","Mehdi Sedighi" ,"xxxx" , null ,"Tehran","prof");

//          sellerDAO.saveSeller(u1);
//
//          Restaurant res = new Restaurant("Kababi","Tehran","logo",u1);

            u1.setBankinfo(b1);

//            u1.setRestaurant(res);
            sellerDAO.updateSeller(u1);
//            restaurantDAO.saveRestaurant(res);
            sellerDAO.updateSeller(u2);
            Restaurant res = restaurantDAO.get(852L);
           // Food f1 = new Food("sushi" , res , "pizza.jpg" , 120000 , 120 , "pizza" , "nothing anymore !");

//            Food f2 = foodDAO.getFood(452L);
            res.removeFood(1152);

            restaurantDAO.updateRestaurant(res);


        } catch (Exception e) {
            e.printStackTrace();
        }


        
    }
}



//Food(String name,Restaurant res,String pictureUrl, int price, String restaurantName, int stockQuantity, String category, String description)