package Model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.*;

@Entity
@Table
public class Basket {
    @Id
    @Column(name = "id", nullable = false)
    private String id;
    @Column(name = "address", nullable = false)
    private String address;
    @Column(name = "buyer_phone", nullable = false)
    private String buyerPhone;
    @Column(name = "buyer_name", nullable = false)
    private String buyerName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private Buyer buyer;

    @ElementCollection(fetch = FetchType.EAGER) // Try EAGER for now
    @CollectionTable(name = "basket_foods", joinColumns = @JoinColumn(name = "basket_id"))
    @MapKeyColumn(name = "food_name")
    @Column(name = "quantity")
    private Map<String,Food> foods = new HashMap<>();

    public Basket() {}

    public Basket(Buyer buyer) {
        if (buyer == null)
            throw new IllegalArgumentException("None of the parameters can be null or empty.");

        LocalDateTime dateTime = LocalDateTime.now();
        this.buyer = buyer;
        this.id = buyer.getPhone() + dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm"));
        this.address = buyer.getAddress();
        this.buyerPhone = buyer.getPhone();
        this.buyerName = buyer.getfullname();
        this.buyer.addCart(this);

    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public String getId() {
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

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public void setBuyerPhone(String phone) {
        if (isNullOrEmpty(phone)) {
            throw new IllegalArgumentException("Buyer phone cannot be null or empty.");
        }
        this.buyerPhone = phone;
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

    public Map<String, Food> getFoods() {
        return foods;
    }


    public void addFood(Food food) {
        if (food == null || food.getStockQuantity()==0) {
            throw new IllegalArgumentException("Food name or restaurant name cannot be null or empty, and quantity must be greater than zero.");
        }
        foods.put(food.getId(),food);
    }

//    public void addFood(String foodId){
//        if (isNullOrEmpty(foodId)) {
//            throw new IllegalArgumentException("Food id cannot be null or empty.");
//        }
//        foods.put(foodId, foods.getOrDefault(foodId, 0) + 1);
//    }

    public void removeFood(String foodId) {
        if (isNullOrEmpty(foodId) || !foods.containsKey(foodId)) {
            throw new IllegalArgumentException("Food id cannot be null or empty.");
        }
        foods.remove(foodId);
    }
}
