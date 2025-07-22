package Model;

import java.util.List;

public class Order {

    private long id ;

    private long res_id ;

    private String buyer_name ;

    private String buyer_phone ;

    private String address ;

    private String created_at ;

    private String status ;

    private String vendor_name ;

    private String courier_id ;

    private List<String> images ;

    private int price;


    public Order(long id, long res_id, String buyer_name, String buyer_phone, String address, String created_at, String status) {
        this.setId(id);
        this.setRes_id(res_id);
        this.setBuyer_name(buyer_name);
        this.setBuyer_phone(buyer_phone);
        this.setAddress(address);
        this.setCreated_at(created_at);
        this.setStatus(status);
    }

    public Order(long id , long res_id, String buyer_phone, String address,String status, String created_at,List<String> images , int price , String vendor_name) {
        this.setId(id);
        this.setRes_id(res_id);
        this.setBuyer_phone(buyer_phone);
        this.setAddress(address);
        this.setCreated_at(created_at);
        this.setStatus(status);
        this.setImages(images);
        this.setPrice(price);
        this.setVendor_name(vendor_name);
    }

    public Order(long id , String buyer_phone, long res_id , String address,String status, String created_at , String courier_id) {
        this.setId(id);
        this.setRes_id(res_id);
        this.setBuyer_phone(buyer_phone);
        this.setAddress(address);
        this.setCreated_at(created_at);
        this.setStatus(status);
        this.setCourier_id(courier_id);

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

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getVendor_name() {
        return vendor_name;
    }

    public void setVendor_name(String vendor_name) {
        this.vendor_name = vendor_name;
    }

    public String getCourier_id() {
        return courier_id;
    }

    public void setCourier_id(String courier_id) {
        this.courier_id = courier_id;
    }
}
