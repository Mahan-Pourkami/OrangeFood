package Model;

public class Vendor {

    private String name;

    private String address;

    private String phone;

    private Long id ;

    private String owner_name;

    private String owner_phone;

    private String logo ;

     public Vendor(String name, String address, String phone, Long id, String owner_name, String owner_phone) {
         this.name = name;
         this.address = address;
         this.phone = phone;
         this.id = id;
         this.owner_name = owner_name;
         this.owner_phone = owner_phone;
     }

     public Vendor(String name , String address , long id , String logo) {
         this.name = name;
         this.address = address;
         this.id = id;
         this.setLogo(logo);
     }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public String getOwner_phone() {
        return owner_phone;
    }

    public void setOwner_phone(String owner_phone) {
        this.owner_phone = owner_phone;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
