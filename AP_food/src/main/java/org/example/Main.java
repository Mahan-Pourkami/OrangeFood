
package org.example;
import DAO.UserDAO;
import Model.User;


public class Main {
    public static void main(String[] args) {
        UserDAO userDAO = new UserDAO();

        try {
            // Create and save new use
//            User mahan = new User("0910*******","Mahan","Pourkami","mahi1385","mahanpourkai@aut.ac.ir",0);
//            userDAO.saveUser(mahan);

            // Retrieve user
            User retrieved = userDAO.getUserByPhone("0910*******");
            System.out.println("Retrieved: " + retrieved.getFirstname());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            userDAO.close();
        }
    }
}