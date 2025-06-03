package DTO;

import Model.*;
import Exceptions.*;
import DAO.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONObject;
import Utils.*;



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

            if(profileImageBase64==null){
                profileImageBase64="default.png";
            }

            this.fullName = fullName;
            this.phone = phone;
            this.password = password;
            this.role = role;
            this.address = address;
            this.email = email;
            this.profileImageBase64 = profileImageBase64;
            this.bankinfo = new BankinfoDTO(); // <-- INITIALIZE THE OBJECT HERE
            this.bankinfo.bankName = bankname;
            this.bankinfo.accountNumber = account;
        }

        public void register() throws DuplicatedUserexception {

            if(userDAO.getUserByPhone(phone) == null) {
                if (role.equals("seller")) {

                    Seller seller = new Seller(phone, fullName, password, email, address, profileImageBase64);
                    Bankinfo sellerBankinfo = new Bankinfo(bankinfo.bankName, bankinfo.accountNumber);
                    seller.setBankinfo(sellerBankinfo);
                    sellerDAO.saveSeller(seller);
                }
                else if (role.equals("buyer")) {
                    Buyer buyer = new Buyer(phone, fullName, password, email, address, profileImageBase64);
                    Bankinfo buyerBankinfo = new Bankinfo(bankinfo.bankName, bankinfo.accountNumber);
                    buyer.setBankinfo(buyerBankinfo);
                    buyerDAO.saveBuyer(buyer);
                }

                else if (role.equals("courier")) {

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
    }

    public static class UserLoginResponseDTO {

        public String message;
        public String token;
        public String fullName;
        public String phone;
        public String email;
        public String role;
        public String address;
        public String profileImageBase64;
        public BankinfoDTO bankinfo;
    }

    public static class UserResponprofileDTO {
        public String id;
        public String fullName;
        public String phone;
        public String role;
        public String address;
        public String email;
        public String profileImageBase64;
        public BankinfoDTO bankinfo;
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

        public String respone () throws JsonProcessingException {

            jsonObject.put("message",this.message);
            jsonObject.put("id",this.user_id);
            jsonObject.put("token",this.token);
            return jsonObject.toString();
        }
    }
}