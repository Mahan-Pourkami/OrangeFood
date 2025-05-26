
package org.example;
import DAO.*;
import Model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.Optional;


public class Main {
    public static void main(String[] args) {
        UserDAO userDAO = UserDAO.getInstance();
        FoodDAO foodDAO = new FoodDAO();
        BuyerDAO buyerDAO = new BuyerDAO();
        BasketDAO basketDAO = new BasketDAO();
        try {

           User u1 = new User("09989504331","Mehdi Sedighi" ,"xxxx" , null ,Role.Buyer,"Tehran","prof" );
            Bankinfo b1 = new Bankinfo("blue","6219861806190277");
            Buyer buyer = new Buyer("09204575452","Ali Ahmadi","XXXX",null,"Tehran","prof");
            Basket b2 = new Basket(buyer);
            Food f1 = new Food("Persian_kebab","kebab.jpg",120000,"Toranj",20,"Persian","nothing");
            foodDAO.saveFood(f1);
            b2.addFood(f1);
            basketDAO.saveBasket(b2);
            System.out.println(basketDAO.getBasket(5L));




//

//            foodDAO.saveFood(f1);


        } catch (Exception e) {
            e.printStackTrace();
        }


        
    }
}