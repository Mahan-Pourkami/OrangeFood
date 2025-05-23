package Model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mahan Pourkami
 * @date : 11:52am ~ 16/05/2025
 */
@Entity
@Table(name = "buyer")

public class Buyer extends User{

    @Column(name = "wallet")
    private  Integer Token ;

    @OneToMany(mappedBy = "buyer")
    private List<Basket> carts ;

    public Buyer(){}

    public Buyer(String phone, String fullname, String password, String email,String prof,String address) {

        super(phone,fullname,password,email,Role.Buyer,address,prof);
        carts = new ArrayList<Basket>();
    }

    public Integer getchargevalue() {
        return Token;
    }

    public void charge(Integer charge) {
        Token += charge;
    }

    public void discharge(Integer discharge) {
        Token -= discharge;
        if(Token < -100)
            throw new ArithmeticException("Not enough money");
    }

    public List<Basket> getcarts() {
        return carts;
    }
    public void addCart(Basket cart) {
        carts.add(cart);
    }


}
