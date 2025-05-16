
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

        try {

//            Buyer b1 = new Buyer("09104554331","Ahmad","Akbari","AK","A8k@gmail.com",10,"" +
//                    "Tehran");
//
//            buyerDAO.deleteBuyer("09104254331");


        } catch (Exception e) {
            e.printStackTrace();
        }

        finally {
            userDAO.close();
        }
        
    }
}