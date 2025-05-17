package Model;

import jakarta.persistence.*;


/**
 * @author Mahan Pourkami
 * @date : 11:52am ~ 16/05/2025
 */
@Entity
@Table(name = "buyer")

public class Buyer extends User{


    @Column(nullable = false)
    String address;

    public Buyer(){

    }

    public Buyer(String phone, String firstname, String lastname, String password, String email, Integer token,String prof,String Address) {
        super(phone, firstname, lastname, password, email, token,prof);

        if(Address == null || Address.isEmpty()){

            throw new IllegalArgumentException("Address can't be null");
        }
        this.address = Address;

    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        if(address == null || address.isEmpty()){

            throw new IllegalArgumentException("Address can't be null");
        }
        this.address = address;
    }

    @Override
    public String toString() {
        return this.getFirstname() + " " + this.getLastname() + " " + this.getPhone() + "buyer ";
    }

}
