package Model;

import jakarta.persistence.*;
import java.util.regex.*;


@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "users")
public class User {


    @Id
    @Column(name = "phone" ,length = 11)
    private String phone;

    @Column(name = "fullname" , nullable = false)
    private String fullname;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "email", unique = true,nullable = true, length = 100)
    private String email;

    @Column(name = "role")
    private Role role;

    @Column(name = "prof" , nullable = true)
    private String profile;


    @JoinColumn(name = "bankinfo_id", referencedColumnName = "id")
    @OneToOne(cascade = CascadeType.ALL , optional = true)
    private Bankinfo bankinfo;

    @Column(name = "address")
    private String address;


    public User() {}


    public User(String phone, String fullname, String password, String email , Role role, String address , String profile) {

        if(!validator.validatePhone(phone))
            throw new IllegalArgumentException("Invalid phone number");

        if(email!= null && !validator.validateEmail(email))
            throw new IllegalArgumentException("Invalid email format");

        this.phone = phone;
        this.password = password;
        this.email = email;
        this.setProfile(profile);
        this.fullname=fullname;
        this.role = role;
        this.address = address;

    }

    // Getters and setters
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }


    public String getfullname() {
        return fullname;
    }

    public Bankinfo getBankinfo() {
        return bankinfo;
    }
    public void setBankinfo(Bankinfo bankinfo) {
        this.bankinfo = bankinfo;
    }

    public void setfullname(String fullname) {
        this.fullname = fullname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

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