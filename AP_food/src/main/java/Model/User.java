package Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "\"Users\"")
public class User {
    @Column(name = "firstname", nullable = false, length = Integer.MAX_VALUE)
    private String firstname;

    @Column(name = "lastname", nullable = false, length = Integer.MAX_VALUE)
    private String lastname;

    @Column(name = "phone", length = Integer.MAX_VALUE)
    private String phone;

    @Column(name = "password", nullable = false, length = Integer.MAX_VALUE)
    private String password;

    @Column(name = "email", length = Integer.MAX_VALUE)
    private String email;

    @Column(name = "token", nullable = false)
    private Integer token;

    public User(String firstname, String lastname, String phone, String password, String email, Integer token) {

        this.firstname = firstname;
        this.lastname = lastname;
        this.phone = phone;
        this.password = password;
        this.email = email;
        this.token = token;
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

    public Integer getToken() {
        return token;
    }

    public void setToken(Integer token) {
        this.token = token;
    }

}