
package org.example;
import DAO.*;
import Model.*;



public class Main {
    public static void main(String[] args) {
        UserDAO userDAO = UserDAO.getInstance();
        BuyerDAO buyerDAO = new BuyerDAO();
        SellerDAO sellerDAO = new SellerDAO();

        try {


            Seller s1 = new Seller("09128888888","Ahmad","Majid","zamani","A8k918978@aputp.com",10,
                    "prof.jpg","Tehran","salam","hello");

            Bankinfo b1 = new Bankinfo(s1,"melli","4","6");
            System.out.println(b1.getPhone());
            s1.setbankinfo(b1);
            sellerDAO.saveSeller(s1);

            
        } catch (Exception e) {
            e.printStackTrace();
        }

        finally {
            userDAO.close();
        }
        
    }
}