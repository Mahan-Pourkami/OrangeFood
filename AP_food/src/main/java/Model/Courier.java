package Model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "couriers")
@Getter
@Setter
public class Courier extends User {



    @Column
    private Userstatue statue;

    //TODO add a list of orders

    public Courier() {}

    public Courier(String phone , String fullname, String password , String email , String address , String prof) {
        super(phone,fullname,password,email,Role.courier,address,prof);
    }

    public Userstatue getStatue() {
        return statue;
    }

    public void setStatue(String statue) {
        this.statue = Userstatue.valueOf(statue);
    }


}
