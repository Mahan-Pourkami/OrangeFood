package Model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Objects;

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

    @ElementCollection(fetch = FetchType.EAGER) // Try EAGER for now
    @CollectionTable(name = "basket_foods", joinColumns = @JoinColumn(name = "basket_id"))
    @MapKeyColumn(name = "food_name")
    @Column(name = "quantity")
    private HashMap<String, Integer> foods = new HashMap<>();

    public Basket() {}

    public Basket(String phone, String buyerName, String address) {
        if (isNullOrEmpty(phone) || isNullOrEmpty(buyerName) || isNullOrEmpty(address)) {
            throw new IllegalArgumentException("None of the parameters can be null or empty.");
        }
        LocalDateTime dateTime = LocalDateTime.now();
        this.id = phone + dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm"));
        this.address = address;
        this.buyerPhone = phone;
        this.buyerName = buyerName;
        //this.foods = new HashMap<>();
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

    public HashMap<String, Integer> getFoods() {
        return foods;
    }


    public void addFood(String foodName,String restaurantName, int quantity) {
        if (isNullOrEmpty(foodName)|| isNullOrEmpty(restaurantName) || quantity <= 0) {
            throw new IllegalArgumentException("Food name or restaurant name cannot be null or empty, and quantity must be greater than zero.");
        }
        foods.put(foodName, foods.getOrDefault(foodName, 0) + quantity);
    }

    public void addFood(String foodId){
        if (isNullOrEmpty(foodId)) {
            throw new IllegalArgumentException("Food id cannot be null or empty.");
        }
        foods.put(foodId, foods.getOrDefault(foodId, 0) + 1);
    }

    public void removeFood(String foodId) {
        if (isNullOrEmpty(foodId)) {
            throw new IllegalArgumentException("Food id cannot be null or empty.");
        }
        foods.put(foodId, foods.getOrDefault(foodId, 0) - 1);
        if(foods.getOrDefault(foodId, 0) == 0) {
            foods.remove(foodId);
        }
    }


}
