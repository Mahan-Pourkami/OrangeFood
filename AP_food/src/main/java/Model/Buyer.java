package Model;

import DAO.RestaurantDAO;
import jakarta.persistence.*;
import java.util.*;

/**
 * @author Mahan Pourkami
 * @date : 11:52am ~ 16/05/2025
 */
@Entity
@Table(name = "buyer")

public class Buyer extends User{


    @Column(name = "wallet" )
    private  Integer Token ;


    @ElementCollection(fetch = FetchType.EAGER)
    private List<Long> favorite_restaurants;


    public Buyer(){}

    public Buyer(String phone, String fullname, String password, String email,String address,String prof) {

        super(phone,fullname,password,email,Role.buyer,address,prof);
        Token  =  0;
    }

    public Integer getchargevalue() {
        return Token;
    }

    public void charge(Integer charge) {
        Token += charge;
    }

    public void discharge(Integer discharge) {
        if(Token < -100)
            throw new ArithmeticException("Not enough money");
        Token -= discharge;
    }

    public void add_tofavorite_restaurants(long id) {
        favorite_restaurants.add(id);
    }

    public void remove_tofavorite_restaurants(long id) {
        favorite_restaurants.remove(id);
    }

    public List<Long> getFavorite_restaurants() {
        return favorite_restaurants;
    }

    public List<Long> getfavorite_restaurants() {
             return favorite_restaurants;
    }


}
