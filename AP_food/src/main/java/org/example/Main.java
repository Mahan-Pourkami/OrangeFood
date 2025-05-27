
package org.example;
import DAO.*;
import Model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.Optional;


public class Main {
    public static void main(String[] args) {
        /*
        UserDAO userDAO = UserDAO.getInstance();
        BasketDAO basketDAO = new BasketDAO();
         */
        FoodDAO foodDAO = new FoodDAO();
        BuyerDAO buyerDAO = new BuyerDAO();
        try {
            /*
            User user1 = new User("09989504331","Mehdi Sedighi" ,"xxxx" , null ,Role.Buyer,"Tehran","prof" );
            Bankinfo bankinfo1 = new Bankinfo("blue","6219861806190277");
            Buyer buyer = new Buyer("09804573457","Ali Ahmadi","XXXX","ALpi@gpail.com","Tehran","prof");
            Basket basket1 = new Basket(buyer);
            Food food1 = new Food("Persian_kebab","kebab.jpg",120000,"Toranj",20,"Persian","nothing");
            Food food2 = new Food("Persian_pizza","pizza.jpg",120000,"Toranj",20,"Persian","nothing");

            foodDAO.saveFood(food1);
            foodDAO.saveFood(food2);
            buyer.addCart(basket1);
            basket1.addFood(food1);
            basket1.addFood(food2);
            buyerDAO.saveBuyer(buyer);


            Buyer b1 = buyerDAO.getBuyer("09804573456");
            System.out.println(b1.getfullname());
            b1.setfullname("parsa samareh");
            buyerDAO.updateBuyer(b1);
            Buyer b2 = buyerDAO.getBuyer("09804573456");
            System.out.println(b2.getfullname());

             */

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}