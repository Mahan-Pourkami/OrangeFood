package Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private boolean confirmed;

    private String name;

    private String address;

    private String logoUrl;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Food> foods;

    @OneToOne
    private Seller seller;

    //TODO list of baskets


    public Restaurant() {}

    public Restaurant(String name, String address, String logoUrl, Seller seller) {
        if (isNullOrEmpty(name) || isNullOrEmpty(address) ) {
            throw new IllegalArgumentException("Restaurant name, address, and working hour are required.");
        }
        this.confirmed = false;
        this.name = name;
        this.address = address;
        this.logoUrl = logoUrl;
        this.foods = new ArrayList<>();
        this.seller = seller;
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    // Getters
//    public String getFormattedWorkingHour() {
//        return workingHour.format(dateFormatter);
//    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }


    public String getLogoUrl() {
        return logoUrl;
    }

//    public HashSet<Food> getFoodList() {
//        return new HashSet<>(foodList); // Return a copy to avoid external modification
//    }
    public boolean isConfirmed() {
        return confirmed;
    }

    // Setters
    public void setName(String name) {
        if (isNullOrEmpty(name)) {
            throw new IllegalArgumentException("Restaurant name cannot be null or empty.");
        }
        this.name = name;
    }

    public void setAddress(String address) {
        if (isNullOrEmpty(address)) {
            throw new IllegalArgumentException("Address cannot be null or empty.");
        }
        this.address = address;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    // Food-related methods
    public void addFood(Food food) {
       if (food == null) {
           throw new IllegalArgumentException("Food cannot be null.");
        }
        foods.add(food);
    }


    public void removeFood(long id) {

        foods.removeIf(food -> food.getId().equals(id));
    }

    public Food findFoodByName(String name) {
        if (isNullOrEmpty(name)) return null;
        for (Food food : foods) {
            if (food.getName().equalsIgnoreCase(name)) {
                return food;
            }
        }
        return null;
    }

    public String getMenuString() {
        String menu = "";
        for (Food food : foods) {
            menu = menu + (food.getName()) +"," ;
        }
        return menu;
    }

    public String getFoodDetail(String foodName){
        Food result = findFoodByName(foodName);
        if (result == null) return null;
        return result.getDetail();
    }
}
