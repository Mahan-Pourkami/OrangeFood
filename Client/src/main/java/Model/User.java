package Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class User {

    private StringProperty phone;

    private StringProperty id;

    private StringProperty full_name;

    private StringProperty email;

    private StringProperty role ;

    public User(String phone,String id , String full_name, String email, String role) {
        this.id = new SimpleStringProperty(id);
        this.phone = new SimpleStringProperty(phone);
        this.full_name = new SimpleStringProperty(full_name);
        this.email = new SimpleStringProperty(email);
        this.role = new SimpleStringProperty(role);
    }




}
