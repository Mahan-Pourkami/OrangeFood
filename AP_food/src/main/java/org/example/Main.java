
package org.example;
import DAO.*;
import Model.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Optional;


public class Main {
    public static void main(String[] args) {
        UserDAO userDAO = UserDAO.getInstance();
        BuyerDAO buyerDAO = new BuyerDAO();
        SellerDAO sellerDAO = new SellerDAO();

        try {

            Buyer b1 = new Buyer("09104659331","Ahmad","Akbari","AK","A8k1il@aut.com",10,
                    "prof.jpg","Tehran");


            Seller s1 = new Seller("09114679831","Ahmad","Majid","zamani","A8k81il@aputp.com",10,
                    "prof.jpg","Tehran","salam","hello");

            sellerDAO.saveSeller(s1);



        } catch (Exception e) {
            e.printStackTrace();
        }

        finally {
            userDAO.close();
        }
        
    }
}