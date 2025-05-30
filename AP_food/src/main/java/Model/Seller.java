package Model;

import jakarta.persistence.*;

@Entity
@Table(name = "seller")

public class Seller extends User  {

    @Column(name = "address", nullable = true)
    private String address;

    @Column(name = "brand", nullable = true)
    private String brand_name;

    @Column(name = "bio", nullable = true)
    private String bio;

    @OneToOne(cascade = CascadeType.ALL ,fetch = FetchType.LAZY)
    @JoinColumn(name = "Restaurant_id",referencedColumnName = "id")
    private Restaurant restaurant ;


    public Seller() {}

    public Seller(String phone,String fullname ,String password,String email ,String address , String prof) {

        super(phone,fullname,password,email,Role.Seller,address,prof);
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        if (address == null || address.isEmpty()) {
            throw new IllegalArgumentException("The address cannot be empty!");
        }
        this.address = address;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        if (brand_name == null || brand_name.isEmpty()) {
            throw new IllegalArgumentException("The brand_name cannot be empty!");
        }
        this.brand_name = brand_name;
    }



    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {

        this.bio = bio;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }
    public void setRestaurant(Restaurant restaurant) {

        this.restaurant = restaurant;
        this.brand_name = restaurant.getName();
    }

}
