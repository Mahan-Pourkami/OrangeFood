package Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "users")
public class User {


    @Setter
    @Getter
    @Id
    @Column(name = "phone" ,length = 11)
    private String phone;

    @Setter
    @Getter
    @Column(name = "id")
    private String id ;

    @Column(name = "fullname" , nullable = false)
    private String fullname;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "email",nullable = true, length = 100)
    private String email;

    @Column(name = "role")
    public Role role;

    @Column(name = "prof" , nullable = true)
    private String profile;


    @JoinColumn(name = "bankinfo_id", referencedColumnName = "id")
    @OneToOne(cascade = CascadeType.ALL , optional = true)
    private Bankinfo bankinfo;

    @Column(name = "address")
    private String address;


    public User() {}


    public User(String phone, String fullname, String password, String email , Role role, String address , String profile) {


        this.phone = phone;
        this.password = password;
        this.email = email;
        this.setProfile(profile);
        this.fullname=fullname;
        this.role = role;
        this.address = address;
        this.id = phone.substring(2);

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

