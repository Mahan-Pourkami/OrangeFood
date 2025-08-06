package Controller;

import Model.*;
import Exceptions.*;
import DAO.*;
import Utils.JwtUtil;
import org.json.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;

public class UserController {

    public static class UserRegisterDTO {

        UserDAO userDAO;
        SellerDAO sellerDAO;
        BuyerDAO buyerDAO;
        CourierDAO courierDAO;
        public String fullName;
        public String phone;
        public String password;
        public String role;
        public String address;
        public String email;
        public String profileImageBase64;
        public BankinfoDTO bankinfo;

        public UserRegisterDTO(String fullName, String phone, String password, String role, String address, String email, String profileImageBase64, String bankname, String account, UserDAO userDAO, SellerDAO sellerDAO, BuyerDAO buyerDAO, CourierDAO courierDAO) throws UnsupportedMediaException, DuplicatedUserexception, EmailException {

            this.userDAO = userDAO;
            this.sellerDAO = sellerDAO;
            this.buyerDAO = buyerDAO;
            this.courierDAO = courierDAO;
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

            if (userDAO.getUserByPhone(phone) != null)
                throw new DuplicatedUserexception();

            if (userDAO.getUserByEmail(email) != null && !email.isEmpty())
                throw new EmailException();

            if (profileImageBase64 != null && !profileImageBase64.isBlank() && !profileImageBase64.endsWith("jpg") && !profileImageBase64.endsWith("jpeg") && !profileImageBase64.endsWith("png") && !profileImageBase64.endsWith("png"))
                throw new UnsupportedMediaException();


        }

        public void register() throws DuplicatedUserexception, InvalidInputException {

            if (userDAO.getUserByPhone(phone) == null) {
                if (role.equals("seller")) {
                    Seller seller = new Seller(phone, fullName, password, email, address, profileImageBase64);
                    if (!bankinfo.bankName.isBlank() && !bankinfo.accountNumber.isBlank()) {
                        Bankinfo sellerBankinfo = new Bankinfo(bankinfo.bankName, bankinfo.accountNumber);
                        seller.setBankinfo(sellerBankinfo);
                    }
                    sellerDAO.saveSeller(seller);
                }
                if (role.equals("buyer")) {
                    Buyer buyer = new Buyer(phone, fullName, password, email, address, profileImageBase64);
                    if (!bankinfo.bankName.isBlank() && !bankinfo.accountNumber.isBlank()) {
                        Bankinfo sellerBankinfo = new Bankinfo(bankinfo.bankName, bankinfo.accountNumber);
                        buyer.setBankinfo(sellerBankinfo);
                    }
                    buyerDAO.saveBuyer(buyer);
                }

                if (role.equals("courier")) {

                    Courier courier = new Courier(phone, fullName, password, email, address, profileImageBase64);
                    if (!bankinfo.bankName.isBlank() && !bankinfo.accountNumber.isBlank()) {
                        Bankinfo sellerBankinfo = new Bankinfo(bankinfo.bankName, bankinfo.accountNumber);
                        courier.setBankinfo(sellerBankinfo);
                    }
                    courierDAO.saveCourier(courier);
                }
            } else throw new DuplicatedUserexception();
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

        public UserLoginRequestDTO(String phone, String password, UserDAO userDAO) {

            this.phone = phone;
            this.password = password;
            this.userDAO = userDAO;

        }

        public User getUserByPhoneAndPass() {

            User user = userDAO.getUserByPhoneAndPass(phone, password);
            System.out.println("user found");
            return user;

        }
    }

    public static class UserResponprofileDTO {

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

        public UserResponprofileDTO(String phone, UserDAO userDAO) {

            User user = userDAO.getUserByPhone(phone);

            this.id = user.getId();
            this.phone = phone;
            this.fullName = user.getfullname();
            this.email = user.getEmail();
            this.address = user.getAddress();
            this.role = user.role.toString();
            this.profileImageBase64 = user.getProfile();
            this.bankinfo = new BankinfoDTO();
            bankinfo.accountNumber = user.getBankinfo().getAccountNumber();
            bankinfo.bankName = user.getBankinfo().getBankName();

        }

        public String response() {

            jsonObject.put("id", id);
            jsonObject.put("full_name", fullName);
            jsonObject.put("phone", phone);
            jsonObject.put("role", role);
            jsonObject.put("address", address);
            jsonObject.put("email", email);
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

        public UserRegResponseDTO(String message, String phone, String role) {

            this.message = message;
            this.user_id = phone.substring(2);
            this.token = jwtUtil.generateToken(phone, role);

        }

        public String message;
        public String user_id;
        public String token;

        public String response() throws JsonProcessingException {

            jsonObject.put("message", this.message);
            jsonObject.put("id", this.user_id);
            jsonObject.put("token", this.token);
            return jsonObject.toString();
        }
    }

    public static class Userupdateprof {

        UserDAO userDAO;


        public Userupdateprof(String phone, JSONObject jsonObject, UserDAO userDAO) throws EmailException, UnsupportedMediaException, InvalidInputException {

            this.userDAO = userDAO;
            User user = userDAO.getUserByPhone(phone);

            if (jsonObject.has("email")) {
                if (!jsonObject.getString("email").equals(user.getEmail()) && userDAO.getUserByEmail(jsonObject.getString("email")) != null && !jsonObject.getString("email").isEmpty()) {
                    throw new EmailException();
                }
                user.setEmail(jsonObject.getString("email"));
            }
            if (jsonObject.has("profileImageBase64")) {
                String prof = jsonObject.getString("profileImageBase64");
                if (prof != null && !prof.isEmpty() && !prof.endsWith("png") && !prof.endsWith("jpg") && !prof.endsWith("jpeg")) {
                    throw new UnsupportedMediaException();
                }
                user.setProfile(prof);
            }

            if (jsonObject.has("full_name")) {

                if (jsonObject.getString("full_name").isEmpty()) {
                    throw new InvalidInputException("full_name");
                }
                user.setfullname(jsonObject.getString("full_name"));
            }
            if (jsonObject.has("address")) {
                if (jsonObject.getString("address").isEmpty()) {
                    throw new InvalidInputException("address");
                }
                user.setAddress(jsonObject.getString("address"));
            }

            JSONObject bankobject = jsonObject.optJSONObject("bank_info");
            if ((bankobject.has("bank_name") && bankobject.has("account_number")) && !bankobject.getString("bank_name").isEmpty() && !bankobject.getString("account_number").isEmpty()) {
                Bankinfo bankinfo = new Bankinfo(bankobject.getString("bank_name"), bankobject.getString("account_number"));
                user.setBankinfo(bankinfo);
            }
            userDAO.updateUser(user);

        }
    }
}
