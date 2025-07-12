package Model;

import javafx.beans.property.StringProperty;

public class User {

    private String phone;

    private String id;

    private String fullName;

    private String email;

    private String role ;

    public User(String phone, String id , String fullName, String email, String role) {

        this.phone = phone;
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;

    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
