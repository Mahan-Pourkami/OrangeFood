package DTO;

import Model.*;
import Exceptions.*;
import DAO.*;
import Utils.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;

public class UserDTO {

    public static class UserRegisterDTO {

        UserDAO userDAO = new UserDAO();
        SellerDAO sellerDAO = new SellerDAO();
        BuyerDAO buyerDAO = new BuyerDAO();
        CourierDAO courierDAO = new CourierDAO();


        public String fullName;
        public String phone;
        public String password;
        public String role;
        public String address;
        public String email;
        public String profileImageBase64;
        public BankinfoDTO bankinfo;

        public UserRegisterDTO(String fullName, String phone, String password, String role, String address, String email, String profileImageBase64,String bankname , String account) {

            this.fullName = fullName;
            this.phone = phone;
            this.password = password;
            this.role = role;
            this.address = address;
            this.email = email;
            this.profileImageBase64 = profileImageBase64;
            this.bankinfo = new BankinfoDTO();
            this.bankinfo.bankName = bankname;
            this.bankinfo.accountNumber = account;

        }

        public void register() throws DuplicatedUserexception {

            if(userDAO.getUserByPhone(phone) == null) {
                if (role.equals("seller")) {

                    //(String phone,String fullname ,String password,String email ,String address , String prof)
                    //Buyer(String phone, String fullname, String password, String email,String address,String prof)
                    //Courier(String phone , String fullname, String password , String email , String address , String prof)

                    Seller seller = new Seller(phone, fullName, password, email, address, profileImageBase64);
                    Bankinfo sellerBankinfo = new Bankinfo(bankinfo.bankName, bankinfo.accountNumber);
                    seller.setBankinfo(sellerBankinfo);
                    sellerDAO.saveSeller(seller);
                }
                if (role.equals("buyer")) {
                    Buyer buyer = new Buyer(phone, fullName, password, email, address, profileImageBase64);
                    Bankinfo buyerBankinfo = new Bankinfo(bankinfo.bankName, bankinfo.accountNumber);
                    buyer.setBankinfo(buyerBankinfo);
                    buyerDAO.saveBuyer(buyer);
                }

                if (role.equals("courier")) {

                    Courier courier = new Courier(phone, fullName, password, email, address, profileImageBase64);
                    Bankinfo courierBankinfo = new Bankinfo(bankinfo.bankName, bankinfo.accountNumber);
                    courier.setBankinfo(courierBankinfo);
                    courierDAO.saveCourier(courier);
                }
            }
            else throw new DuplicatedUserexception();
        }

    }

    public static class BankinfoDTO {
        public String bankName;
        public String accountNumber;
    }

    public static class UserLoginRequestDTO {

        public String phone;
        public String password;
        UserDAO userDAO;
        public UserLoginRequestDTO(String phone, String password) {
            this.phone = phone;
            this.password = password;
            this.userDAO = new UserDAO();
        }
        public User getUserByPhoneAndPass() {
            User user = userDAO.getUserByPhoneAndPass(phone,password);
            System.out.println("user found");
            return user;
        }
    }

    public static class UserResponprofileDTO {

        UserDAO userDAO = new UserDAO();
        JSONObject jsonObject = new JSONObject();
        JSONObject bankjson = new JSONObject();

        public String id;
        public String fullName;
        public String phone;
        public String role;
        public String address;
        public String email;
        public String profileImageBase64;
        public BankinfoDTO bankinfo;

       public UserResponprofileDTO (String phone) {

           User user = userDAO.getUserByPhone(phone);

           this.id = user.getId();
           this.phone = phone;
           this.fullName=user.getfullname();
           this.email=user.getEmail();
           this.address=user.getAddress();
           this.role = user.role.toString();
           this.profileImageBase64 = user.getProfile();
           this.bankinfo= new BankinfoDTO();
           bankinfo.accountNumber=user.getBankinfo().getAccountNumber();
           bankinfo.bankName=user.getBankinfo().getBankName();

        }

        public String response (){

           jsonObject.put("id", id);
           jsonObject.put("full_name", fullName);
           jsonObject.put("phone", phone);
           jsonObject.put("role", role);
           jsonObject.put("address", address);
           jsonObject.put("profileImageBase64", this.profileImageBase64);
           bankjson.put("bank_name", bankinfo.bankName);
           bankjson.put("account_number", bankinfo.accountNumber);
           jsonObject.put("bank_info", bankjson);

           return jsonObject.toString();
        }


    }



    public static class UserRegResponseDTO {

        public JwtUtil jwtUtil = new JwtUtil();
        public JSONObject jsonObject = new JSONObject();

        public UserRegResponseDTO(String message , String phone , String role ) {

            this.message = message;
            this.user_id = phone.substring(2);
            this.token = jwtUtil.generateToken(phone, role);

        }

        public String message;
        public String user_id;
        public String token;

        public String response() throws JsonProcessingException {

            jsonObject.put("message",this.message);
            jsonObject.put("id",this.user_id);
            jsonObject.put("token",this.token);
            return jsonObject.toString();
        }
    }

    public static class Userupdateprof{

        UserDAO userDAO = new UserDAO();


       public Userupdateprof(String phone , JSONObject jsonObject ) {

            User user = userDAO.getUserByPhone(phone);

            user.setId(jsonObject.getString("id"));
            user.setPhone(jsonObject.getString("phone"));
            user.setfullname(jsonObject.getString("full_name"));
            user.setEmail(jsonObject.getString("email"));
            user.setAddress(jsonObject.getString("address"));
            user.setProfile(jsonObject.getString("profileImageBase64"));
            JSONObject bankobject = jsonObject.optJSONObject("bank_info");
            Bankinfo bankinfo = new Bankinfo(bankobject.getString("bank_name"),bankobject.getString("account_number"));
            user.setBankinfo(bankinfo);


            userDAO.updateUser(user);

        }


    }


}
