package Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;

public class Restaurant {

    private boolean confirmed;

    private String name;

    private String address;

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm");
    private LocalDateTime workingHour;
    private String logoUrl;
    private HashSet<Food> foodList;
    private Seller seller;
    public Restaurant(String name, String address, LocalDateTime workingHour, String logoUrl, Seller seller) {
        if (isNullOrEmpty(name) || isNullOrEmpty(address) || workingHour == null) {
            throw new IllegalArgumentException("Restaurant name, address, and working hour are required.");
        }
        this.confirmed = false;
        this.name = name;
        this.address = address;
        this.workingHour = workingHour;
        this.logoUrl = logoUrl;
        this.foodList = new HashSet<>();
        this.seller = seller;
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    // Getters
    public String getFormattedWorkingHour() {
        return workingHour.format(dateFormatter);
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public LocalDateTime getWorkingHour() {
        return workingHour;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public HashSet<Food> getFoodList() {
        return new HashSet<>(foodList); // Return a copy to avoid external modification
    }
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

    public void setWorkingHour(LocalDateTime workingHour) {
        if (workingHour == null) {
            throw new IllegalArgumentException("Working hour cannot be null.");
        }
        this.workingHour = workingHour;
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
        foodList.add(food);
    }


    public boolean removeFood(Food food) {
        return foodList.remove(food);
    }

    public Food findFoodByName(String name) {
        if (isNullOrEmpty(name)) return null;
        for (Food food : foodList) {
            if (food.getName().equalsIgnoreCase(name)) {
                return food;
            }
        }
        return null; // not found
    }

    public String getMenuString() {
        String menu = "";
        for (Food food : foodList) {
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
