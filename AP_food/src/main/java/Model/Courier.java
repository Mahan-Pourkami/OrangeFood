package Model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
public class Courier extends User {



    //TODO add a list of orders

    public Courier() {}

    public Courier(String phone , String fullname, String password , String email , String address , String prof) {
        super(phone,fullname,password,email,Role.courier,address,prof);
    }


}
