package DTO;

import DAO.CouponDAO;
import DAO.RestaurantDAO;
import Exceptions.DuplicatedItemexception;
import Exceptions.InvalidInputException;
import Exceptions.NosuchItemException;
import Model.Coupon;
import Model.Coupontype;
import Model.Restaurant;
import Model.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class AdminDTO {


    public static class Getusersresponse {

        private String response ;

       public Getusersresponse(List<User> users) {

            JSONArray jsonArray = new JSONArray();

            for (User user : users) {

                JSONObject jsonObject = new JSONObject();
                JSONObject bankinfo = new JSONObject();

                bankinfo.put("bank_name" , user.getBankinfo().getBankName());
                bankinfo.put("account_number" , user.getBankinfo().getAccountNumber());

                jsonObject.put("id", user.getId());
                jsonObject.put("full_name",user.getfullname());
                jsonObject.put("phone",user.getPhone());
                jsonObject.put("email",user.getEmail());
                jsonObject.put("role",user.role.toString());
                jsonObject.put("address",user.getAddress());
                jsonObject.put("profileImageBase64",user.getProfile());
                jsonObject.put("bankinfo",bankinfo);
                jsonArray.put(jsonObject);

            }

            this.response = jsonArray.toString();
        }

        public String getResponse() {
            return response;
        }

    }

    public static class Create_coupon_request {

        private JSONObject jsonObject ;
        private CouponDAO couponDAO;

        private String code ;
        private String type ;
        private Number value ;
        private int min_price ;
        private int user_count ;
        private String start_time ;
        private String end_time ;




        public Create_coupon_request(JSONObject jsonObject, CouponDAO couponDAO) throws InvalidInputException {

            this.jsonObject = jsonObject;
            this.couponDAO = couponDAO;

            String [] requierd = {"coupon_code" , "type" , "value" , "min_price" , "user_count"};

            for(String input : requierd){

                if(!this.jsonObject.has(input)){
                    throw  new InvalidInputException(input);
                }
            }


            this.code = jsonObject.getString("coupon_code");
            this.type = jsonObject.getString("type");
            this.value = jsonObject.getNumber("value");
            this.min_price = jsonObject.getInt("min_price");
            this.user_count = jsonObject.getInt("user_count");

            if(jsonObject.has("start_date") && jsonObject.has("end_date")){

                this.start_time = jsonObject.getString("start_time");
                this.end_time = jsonObject.getString("end_time");
            }

            else {
                this.start_time = this.end_time = "";
            }
        }

        public void submit_coupon() throws DuplicatedItemexception {
            Coupon coupon = new Coupon(this.code,this.value,this.type,this.min_price,this.user_count,this.start_time,this.end_time);

            if(couponDAO.findCouponByCode(code)!=null){
                throw new DuplicatedItemexception();
            }

            this.couponDAO.saveCoupon(coupon);
        }
    }

    public static class Create_coupon_response{

        private JSONObject jsonObject = new JSONObject() ;
        private CouponDAO couponDAO;
        private String code ;

        public Create_coupon_response(CouponDAO couponDAO , String code) {
            this.jsonObject = jsonObject;
            this.couponDAO = couponDAO;
            this.code = code;
        }

        public String getResponse() {

            Coupon coupon = couponDAO.findCouponByCode(this.code);

            jsonObject.put("id" , coupon.getId());
            jsonObject.put("coupon_code" , coupon.getCode());
            jsonObject.put("type" , coupon.getType());
            jsonObject.put("value" , coupon.getValue());
            jsonObject.put("min_price" , coupon.getMin_price());
            jsonObject.put("user_counts",coupon.getUser_counts());
            jsonObject.put("start_date" , coupon.getStart_time());
            jsonObject.put("end_date" , coupon.getEnd_time());

            return jsonObject.toString();
        }

    }

    public static class Get_coupons_response{

        private String response;

        public Get_coupons_response(List<Coupon> coupons) {

            JSONArray jsonArray = new JSONArray();
            for(Coupon coupon : coupons){

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id" , coupon.getId());
                jsonObject.put("coupon_code" , coupon.getCode());
                jsonObject.put("type" , coupon.getType());
                jsonObject.put("value" , coupon.getValue());
                jsonObject.put("min_price" , coupon.getMin_price());
                jsonObject.put("user_counts" , coupon.getUser_counts());
                jsonObject.put("start_date" , coupon.getStart_time());
                jsonObject.put("end_date" , coupon.getEnd_time());
                jsonArray.put(jsonObject);

            }
            this.response = jsonArray.toString();
        }
        public String getResponse() {
            return response;
        }
    }

    public static class Update_coupon_request{

        private String coupon_code;
        private String type;
        private Number value;
        private int min_price;
        private int user_count;
        private String start_time;
        private String end_time;

        public Update_coupon_request(JSONObject jsonObject, CouponDAO couponDAO , long id) throws IOException {


            Coupon coupon = couponDAO.getCoupon(id);

            if(jsonObject.has("coupon_code"))this.coupon_code = jsonObject.getString("coupon_code");
            else this.coupon_code = coupon.getCode();

            if(jsonObject.has("type")) this.type = jsonObject.getString("type");
            else this.type=coupon.getType().toString();

            if(jsonObject.has("value"))this.value = jsonObject.getNumber("value");
            else this.value=coupon.getValue();

            if(jsonObject.has("min_price")) this.min_price = jsonObject.getInt("min_price");
            else this.min_price=coupon.getMin_price();

            if(jsonObject.has("user_count")) this.user_count = jsonObject.getInt("user_count");
            else this.user_count=coupon.getUser_counts();

            if(jsonObject.has("start_date")) this.start_time = jsonObject.getString("start_date");
            else this.start_time=coupon.getStart_time();

            if(jsonObject.has("end_date")) this.end_time = jsonObject.getString("end_date");
            else this.end_time=coupon.getEnd_time();



            if(coupon==null){
                throw new NosuchItemException();
            }

            if(couponDAO.findCouponByCode(coupon_code)!=null && !coupon_code.equals(coupon.getCode())){
                throw new DuplicatedItemexception();
            }

            coupon.setCode(coupon_code);
            coupon.setType(Coupontype.valueOf(type));
            coupon.setMin_price(min_price);
            coupon.setUser_counts(user_count);
            coupon.setValue(value);
            coupon.set_start_time(start_time);
            coupon.set_end_time(end_time);
            couponDAO.updateCoupon(coupon);
        }
    }


    public static class Get_Restaurants_response{

        private String response;
        private RestaurantDAO restaurantDAO;

        public Get_Restaurants_response(RestaurantDAO restaurantDAO) {

            JSONArray jsonArray = new JSONArray();
            List<Restaurant> restaurants = restaurantDAO.getAllRestaurants();
            for(Restaurant restaurant : restaurants){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id" , restaurant.getId());
                jsonObject.put("name" , restaurant.getName());
                jsonObject.put("address" , restaurant.getAddress());
                jsonObject.put("phone" , restaurant.getPhone());
                jsonObject.put("owner_phone",restaurant.getSeller().getPhone());
                jsonObject.put("owner_name" , restaurant.getSeller().getfullname());
                jsonArray.put(jsonObject);
            }

            this.response = jsonArray.toString();
        }
        public String getResponse() {
            return response;
        }


    }
}
