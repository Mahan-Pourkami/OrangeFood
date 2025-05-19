
package org.example;
import DAO.*;
import Model.*;

import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        UserDAO userDAO = UserDAO.getInstance();
        BuyerDAO buyerDAO = new BuyerDAO();
        SellerDAO sellerDAO = new SellerDAO();
        FoodDAO foodDAO = new FoodDAO();
        BasketDAO basketDAO = new BasketDAO();
        try {
            Basket basket = new Basket("09120287349","parsa","address");
            basketDAO.saveBasket(basket);
        } catch (Exception e) {
            e.printStackTrace();
        }

        finally {
            basketDAO.close();
        }
        
    }
}