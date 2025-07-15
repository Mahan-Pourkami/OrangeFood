package Model;

public class Approvals {


    private String name ;

    private String phone ;

    private String role ;

    private String id ;


    public Approvals(String name, String phone, String role, String id) {
        this.name = name;
        this.phone = phone;
        this.role = role;
        this.id = id;

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
