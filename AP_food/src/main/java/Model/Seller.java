package Model;

import jakarta.persistence.*;

@Entity
@Table(name = "seller")

public class Seller extends User  {


    @Column(name = "brand", nullable = true)
    private String brand_name;

    @Column(name = "bio", nullable = true)
    private String bio;

    @OneToOne(cascade = CascadeType.ALL ,fetch = FetchType.LAZY)
    @JoinColumn(name = "Restaurant_id",referencedColumnName = "id")
    private Restaurant restaurant ;

    @Column
    private Userstatue statue;


    public Seller() {}

    public Seller(String phone,String fullname ,String password,String email ,String address , String prof) {

        super(phone,fullname,password,email,Role.seller,address,prof);
        this.statue = Userstatue.requested;
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

    public Userstatue getStatue() {
        return statue;
    }

    public void setStatue(String statue) {
        this.statue = Userstatue.valueOf(statue);
    }

}
