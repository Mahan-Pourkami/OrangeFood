package Model;

import jakarta.persistence.*;

import java.util.Arrays;
import java.util.List;
@Entity
@Table(name = "foods")
public class Food {

    @Id
    @Column(name = "id")
    private String id ;

    @Column(name = "name" , nullable = false)
    private String name;
    @Column(name = "pictureUrl" , nullable = true)
    private String pictureUrl;
    @Column(name = "price" , nullable = false)
    private int price;
    @Column(name = "restaurantName" , nullable = true)
    private String restaurantName;
    @Column(name = "stockQuantity" , nullable = false)
    private int stockQuantity;
    @Column(name = "category" , nullable = true)
    private String category;

    @Column(name = "keyWords" , nullable = true)
    private String keyWords;

    @Column(name = "description" , nullable = true)
    private String description;



    public Food() {}

    public Food(String name,String pictureUrl, int price, String restaurantName, int stockQuantity, String category, String description) {
        if (price <= 0) {
            throw new IllegalArgumentException("Invalid price");
        }
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("Invalid stockQuantity");
        }
        if (name == null || name.trim().isEmpty() || restaurantName == null || restaurantName.trim().isEmpty()) {
            throw new IllegalArgumentException("name or restaurantName cannot be null");
        }


        this.id = name+restaurantName;
        this.name = name;
        this.pictureUrl = pictureUrl;
        this.price = price;
        this.restaurantName = restaurantName;
        this.stockQuantity = stockQuantity;
        this.category = category;
        this.description = description;
    }

    public String getName() { return name; }
    public String getPictureUrl() { return pictureUrl; }
    public int getPrice() { return price; }
    public String getRestaurantName() { return restaurantName; }
    public int getStockQuantity() { return stockQuantity; }
    public String getCategory() { return category; }
    public List<String> getKeyWords() { return Arrays.asList(keyWords.split(",")); } // Convert string to list
    public String getDescription() { return description; }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        this.name = name;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public void setPrice(int price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Invalid price");
        }
        this.price = price;
    }

    public void setRestaurantName(String restaurantName) {
        if (restaurantName == null || restaurantName.trim().isEmpty()) {
            throw new IllegalArgumentException("Restaurant name cannot be empty");
        }
        this.restaurantName = restaurantName;
    }

    public void setStockQuantity(int stockQuantity) {
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("Invalid stock quantity");
        }
        this.stockQuantity = stockQuantity;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setKeyWords(List<String> keyWords) {
        this.keyWords = String.join(",", keyWords);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDetail(){
        String detail = "";
        detail = detail + name + "," + pictureUrl + "," + price + "," + restaurantName + "," + stockQuantity + "," + category + "," + keyWords + "," + description;
        return detail;
    }

    public String getId() {
        return id;
    }
}
