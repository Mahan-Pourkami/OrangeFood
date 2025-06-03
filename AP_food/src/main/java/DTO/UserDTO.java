package DTO;

import Model.*;
import Exceptions.*;
import DAO.*;

public class UserDTO {

    public static class UserRegisterDTO {

        UserDAO userDAO ;
        SellerDAO sellerDAO;
        BuyerDAO buyerDAO ;
        CourierDAO courierDAO ;


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
            this.bankinfo.bankName = bankname;
            this.bankinfo.accountNumber = account;
            this.userDAO = new UserDAO();
            this.sellerDAO = new SellerDAO();
            this.buyerDAO = new BuyerDAO();
            this.courierDAO = new CourierDAO();

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

    public static class UserAuthResponseDTO {
        public String message;
        public String user_id;
        public String token;
    }
}