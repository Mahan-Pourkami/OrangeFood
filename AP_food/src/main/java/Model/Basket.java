package Model;

import DAO.CouponDAO;
import DAO.CourierDAO;
import DAO.FoodDAO;
import DAO.RestaurantDAO;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static Model.StateofCart.waiting;

@Entity
@Table(name = "baskets")

public class Basket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "buyer_phone", length = 20)
    private String buyerPhone;

    @Column(name = "buyer_name", length = 100)
    private String buyerName;

    @ElementCollection
    @CollectionTable(
            name = "basket_items",
            joinColumns = @JoinColumn(name = "basket_id")
    )
    @MapKeyColumn(name = "item_id")
    @Column(name = "quantity")
    private Map<Long, Integer> items = new HashMap<>();


    @Column
    private long res_id ;

    @Column
    private Long coupon_id ;

    @Column
    private String courier_id ;

    @Column
    private String created_at ;

    @Column
    private String upadated_at ;

    @Column(name = "State")
    private StateofCart stateofCart;

    private final int COURIER_FEE = 5;

    public Basket() {
    }

    public Basket(User buyer,String address, int vendor_id , Integer coupon_id) {

        this.address = address;
        this.buyerPhone = buyer.getPhone();
        this.buyerName = buyer.getfullname();
        this.res_id = vendor_id;
        this.items = new HashMap<>();
        this.coupon_id = (coupon_id != null) ? coupon_id.longValue() : null;
        this.stateofCart = waiting;
        this.created_at = LocalDateTime.now().toString();
        this.upadated_at = LocalDateTime.now().toString();
        this.courier_id = null;
    }



    public void addItem(long id, int quantity) {
        items.put(id, quantity);
    }
    public void removeItem(Long id) {
        items.remove(id);
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public Long getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        if (isNullOrEmpty(address)) {
            throw new IllegalArgumentException("Address cannot be null or empty.");
        }
        this.address = address;
    }

    public void ChangeState(StateofCart stateofCart) {
        this.stateofCart = stateofCart;
    }

    public void setCourier_id(String courier_id) {
        this.courier_id = courier_id;
    }

    public String getCourier_id() {
        if(courier_id==null) {
            return null;
        }
        return courier_id;
    }

    public void setStateofCart(StateofCart stateofCart) {
        this.stateofCart = stateofCart;
    }

    public StateofCart getStateofCart() {
        return stateofCart;
    }

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public void setBuyerPhone(String phone) {
        if (isNullOrEmpty(phone)) {
            throw new IllegalArgumentException("Buyer phone cannot be null or empty.");
        }
        this.buyerPhone = phone;
    }

    public Long getCoupon_id() {
        return coupon_id;
    }

    public void setCoupon_id(long coupon_id) {
        this.coupon_id = coupon_id;
    }

    public long getRes_id() {
        return res_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpadated_at() {
        return upadated_at;
    }

    public void setUpadated_at(String upadated_at) {
        this.upadated_at = upadated_at;
    }

    public int getCOURIER_FEE() {
        return COURIER_FEE;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        if (isNullOrEmpty(buyerName)) {
            throw new IllegalArgumentException("Buyer name cannot be null or empty.");
        }
        this.buyerName = buyerName;
    }

    public Map<Long,Integer> getItems() {
        return items;
    }

    public int getRawPrice(FoodDAO foodDAO){
        int rawPrice=0;
        for(Map.Entry<Long,Integer> item : items.entrySet()){
            rawPrice += foodDAO.getFood(item.getKey()).getPrice()*item.getValue();
        }
        return rawPrice;
    }

    public int getTaxFee(RestaurantDAO restaurantDAO){
        int taxFee = restaurantDAO.get_restaurant(res_id).getTax_fee();
        return taxFee;
    }

    public int getAdditionalFee(RestaurantDAO restaurantDAO){
        int additionalFee = restaurantDAO.get_restaurant(res_id).getAdditional_fee();
        return additionalFee;

    }

    public int getPayPrice(RestaurantDAO restaurantDAO,FoodDAO foodDAO){
        int payPrice = 0 ;
        int additionalFee = getAdditionalFee(restaurantDAO);
        int taxFee = getTaxFee(restaurantDAO);
        CouponDAO couponDAO = new CouponDAO();
        payPrice = getRawPrice(foodDAO)+additionalFee+taxFee+COURIER_FEE;
        if(coupon_id!=null&&couponDAO.getCoupon(coupon_id)!=null) {
            Coupon cp = couponDAO.getCoupon(coupon_id);
            if (payPrice > cp.getMin_price()) {
                Coupontype couponType = cp.getType();
                if (couponType == Coupontype.fixed) {
                    payPrice = getRawPrice(foodDAO) + additionalFee + taxFee + COURIER_FEE - (int) cp.getValue();
                }
                if (couponType == Coupontype.percent) {
                    payPrice = (getRawPrice(foodDAO) + additionalFee + taxFee + COURIER_FEE) * ((100 - (int) cp.getValue()) / 100);
                }
            }
        }
        else{
            payPrice = getRawPrice(foodDAO) + additionalFee + taxFee + COURIER_FEE;
        }
        return payPrice;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Basket basket = (Basket) o;
        if (id == null) {
            return Objects.equals(buyerPhone, basket.buyerPhone) &&
                    Objects.equals(address, basket.address) &&
                    Objects.equals(buyerName, basket.buyerName);
        }
        return Objects.equals(id, basket.id);
    }

    @Override
    public int hashCode() {
        if (id == null) {
            return Objects.hash(buyerPhone, address, buyerName);
        }
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Basket{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", buyerPhone='" + buyerPhone + '\'' +
                ", buyerName='" + buyerName + '\'' +
                '}';
    }
}