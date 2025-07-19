package Model;

public class Order {

    private long id ;

    private long res_id ;

    private String buyer_name ;

    private String buyer_phone ;

    private String address ;

    private String created_at ;

    private String status ;


    public Order(long id, long res_id, String buyer_name, String buyer_phone, String address, String created_at, String status) {
        this.setId(id);
        this.setRes_id(res_id);
        this.setBuyer_name(buyer_name);
        this.setBuyer_phone(buyer_phone);
        this.setAddress(address);
        this.setCreated_at(created_at);
        this.setStatus(status);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBuyer_name() {
        return buyer_name;
    }

    public void setBuyer_name(String buyer_name) {
        this.buyer_name = buyer_name;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getRes_id() {
        return res_id;
    }

    public void setRes_id(long res_id) {
        this.res_id = res_id;
    }

    public String getBuyer_phone() {
        return buyer_phone;
    }

    public void setBuyer_phone(String buyer_phone) {
        this.buyer_phone = buyer_phone;
    }
}
