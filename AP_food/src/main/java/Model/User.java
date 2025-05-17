package Model;

import jakarta.persistence.*;

import java.util.regex.*;


class validator {

    protected static boolean validateEmail(String email) {

        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();

    }

    protected static boolean validatePhone(@org.jetbrains.annotations.NotNull String phone) {

        if(!phone.startsWith("09")){
            return false;
        }

        if(phone.length() != 11) {
            return false;
        }
        for(char c : phone.toCharArray()) {
            if(!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

}



@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "users")
abstract public class User {
    @Id
    @Column(name = "phone", unique = true ,length = 20)
    private String phone;  // Primary key

    @Column(name = "firstname", nullable = false, length = 50)
    private String firstname;

    @Column(name = "lastname", nullable = false, length = 50)
    private String lastname;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "email", unique = true, length = 100)
    private String email;

    @Column(name = "token", nullable = false)
    private Integer token;

    @Column(name = "prof" , nullable = true)
    private String profile;


    // Constructors
    public User() {
        // Hibernate will use this to instantiate objects
    }

    // Your existing constructors


    public User(String phone, String firstname, String lastname, String password, String email, Integer token , String profile) {


        if(!validator.validatePhone(phone))
            throw new IllegalArgumentException("Invalid phone number");

        if(!validator.validateEmail(email))
            throw new IllegalArgumentException("Invalid email format");

        this.phone = phone;
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = password;
        this.email = email;
        this.token = token;
        this.profile = profile;

    }

    // Getters and setters
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getFirstname() {
        return firstname;
    }
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {

        return lastname;
    }
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public Integer getToken() {
        return token;
    }
    public void setToken(Integer token) {
        this.token = token;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}

