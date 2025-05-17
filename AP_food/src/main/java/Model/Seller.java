package Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "seller")
public class Seller extends User{

    @Column(name = "address" , nullable = false)
    String address;

    @Column(name = "brand" , nullable = false)
    String brand_name;

    @Column(name = "bio" , nullable = false , unique = true )
    String bio ;



    public Seller() {
        super();
    } //hibernate use this constructor

    public Seller(String phone, String firstname, String lastname, String password, String email, Integer token , String profile ,String address, String brand_name, String bio) {

        super(phone, firstname, lastname, password, email, token, profile);

        if(address==null || brand_name==null || bio==null) {
            throw new IllegalArgumentException("you should fill the address , brand_name & bio");
        }

        this.address = address;
        this.brand_name = brand_name;
        this.bio = bio;

    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        if(address==null || address.isEmpty()) {
            throw new IllegalArgumentException("the address cannot be empty!!");
        }
        this.address = address;
    }
    public String getBrand_name() {
        return brand_name;
    }
    public void setBrand_name(String brand_name) {
        if(brand_name==null || brand_name.isEmpty()) {
            throw new IllegalArgumentException("the brand_name cannot be empty!!");
        }
        this.brand_name = brand_name;
    }
    public String getBio() {
        return bio;
    }
    public void setBio(String bio) {
        if(bio==null || bio.isEmpty()) {
            throw new IllegalArgumentException("the bio cannot be empty!!");
        }
        this.bio = bio;
    }
}
