package Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String phone;

    private String logoUrl;

    private Integer tax_fee ;

    private  Integer additional_fee ;

    @OneToOne
    private Seller seller;

    public Restaurant() {}

    public Restaurant(String name, String address, String phone ,String logoUrl,Integer tax_fee , Integer additional_fee, Seller seller) {
        if (isNullOrEmpty(name) || isNullOrEmpty(address)) {
            throw new IllegalArgumentException("Restaurant name, address, and working hour are required.");
        }
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.logoUrl = logoUrl;
        this.seller = seller;
        this.tax_fee = tax_fee;
        this.additional_fee = additional_fee;
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public void setName(String name) {
        if (isNullOrEmpty(name)) {
            throw new IllegalArgumentException("Restaurant name cannot be null or empty.");
        }
        this.name = name;
    }

    public void setAddress(String address) {
        if (isNullOrEmpty(address)) {
            throw new IllegalArgumentException("Address cannot be null or empty.");
        }
        this.address = address;
    }
}


