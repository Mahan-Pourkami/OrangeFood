package Model;

public class Transaction {

    private long id ;

    private String order_id;

    private String methode ;

    private String status ;

    private String user_phone ;


    public Transaction(long id, String order_id, String methode, String status, String user_phone) {
        this.id = id;
        this.order_id = order_id;
        this.methode = methode;
        this.status = status;
        this.user_phone = user_phone;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getMethode() {
        return methode;
    }

    public void setMethode(String methode) {
        this.methode = methode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }
}
