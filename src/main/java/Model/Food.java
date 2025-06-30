package Model;

import DAO.RestaurantDAO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "foods")
public class Food {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id ;

    @Column(name = "name" , nullable = false)
    private String name;

    @Column(name = "pictureUrl" , nullable = true)
    private String pictureUrl;

    @Column(name = "price" , nullable = false)
    private int price;

    @Getter
    @Column(name = "stockQuantity" , nullable = false)
    private int supply;

    @Column
    private String description;

    @Column (name = "menu_title")
    private String menuTitle;

    @Column (name = "restaurant_id")
    private Long restaurantId;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> keywords;


    public Food() {}

    public Food(String name, long res_id,String description,String pictureUrl, int price, int supply) {
        if (price <= 0) {
            throw new IllegalArgumentException("Invalid price");
        }
        if (supply < 0) {
            throw new IllegalArgumentException("Invalid stockQuantity");
        }

        this.name = name;
        this.pictureUrl = pictureUrl;
        this.price = price;
        this.supply = supply;
        this.restaurantId = res_id ;
        this.description = description;
        this.keywords = new ArrayList<String>();

    }


    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setSupply(int stockQuantity) {
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("Invalid stock quantity");
        }
        this.supply = stockQuantity;
    }


    public void setMenuTitle(String menuTitle) {

        this.menuTitle = menuTitle;
    }

    public void setkeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public Restaurant getRestaurant() {
        RestaurantDAO restaurantDAO = new RestaurantDAO();
        Restaurant restaurant = restaurantDAO.get_restaurant(restaurantId);
        return restaurant;
    }

}
