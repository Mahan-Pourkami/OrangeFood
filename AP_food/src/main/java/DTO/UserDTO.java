package DTO;

public class UserDTO {

    public static class UserRegisterDTO {
        public String fullName;
        public String phone;
        public String password;
        public String role;
        public String address;
        public String email;
        public String profileImageBase64;
        public BankinfoDTO bankinfo;
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

    public static class UserAuthResponseDTO {
        public String message;
        public String user_id;
        public String token;
    }
}