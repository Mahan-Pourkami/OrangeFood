
package org.example;


import DAO.BasketDAO;
import DTO.RestaurantDTO;
import Model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Main {
    public static JSONObject getBasketJsonObject(Basket basket, JSONArray itemIdsArray) {
        JSONObject basketJson = new JSONObject();
        basketJson.put("id", basket.getId());
        basketJson.put("delivery_address", basket.getAddress());
        basketJson.put("customer_id",basket.getBuyerPhone()); //تو yaml نوشته باید int باشه ولی فعلا string میفرستیم
        basketJson.put("vendor_id",basket.getRes_id());
        basketJson.put("coupon_id", basket.getCoupon_id() != null ? basket.getCoupon_id() : JSONObject.NULL);
        basketJson.put("item_ids", itemIdsArray);
        basketJson.put("raw_price",basket.getRawPrice());
        basketJson.put("tax_fee",basket.getTaxFee());
        basketJson.put("additional_fee",basket.getAdditionalFee());
        basketJson.put("courier_fee",basket.getCOURIER_FEE());
        basketJson.put("pay_price",basket.getPayPrice());
        basketJson.put("courier_id",basket.getCourier_id());
        basketJson.put("status",basket.getStateofCart());
        basketJson.put("created_at",basket.getCreated_at());
        basketJson.put("updated_at",basket.getUpadated_at());
        return basketJson;
    }
    public static void main(String[] args) {
        BasketDAO basketDAO = new BasketDAO();
        List<Object[]> basketsSum = basketDAO.getBasketIdAndPhone();
        JSONArray basketArray = new JSONArray();
        for (Object[] row : basketsSum) {
            String phone = (String) row[1];
            if(phone.equals("09122222222")){
                Basket basket = basketDAO.getBasket((Long) row[0]);
                Map<Long, Integer> items = basket.getItems();
                JSONArray itemIdsArray = new JSONArray(items.keySet());
                basketArray.put(getBasketJsonObject(basket,itemIdsArray));
            }
        }
        System.out.println(basketArray.toString());
/*
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


     */
    }
}



//Food(String name,Restaurant res,String pictureUrl, int price, String restaurantName, int stockQuantity, String category, String description)