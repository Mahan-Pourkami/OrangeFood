
package org.example;


import DTO.RestaurantDTO;
import Model.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;


public class Main {

    public static void main(String[] args) {

        try {

            Seller u1 = new Seller("09121111111","Parsa" ,"xxxx" , "parsa@t" ,"Tehran","prof");

            Bankinfo b1 = new Bankinfo("kesh","1111111111111111");
            u1.setBankinfo(b1);

            List<String> keyword = new ArrayList<String>();
            keyword.add("kesh");
            keyword.add("parsa");

            System.out.println(RestaurantDTO.convertlisttojsonarray(keyword));



        } catch (Exception e) {
            e.printStackTrace();
        }


        
    }
}



//Food(String name,Restaurant res,String pictureUrl, int price, String restaurantName, int stockQuantity, String category, String description)