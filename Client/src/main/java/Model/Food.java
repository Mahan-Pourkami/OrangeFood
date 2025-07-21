package Model;

public class Food {

    private long id;

    private long res_id ;

    private String name ;

    private String description ;

    private int price ;

    private int quantity ;

    private String logo ;

    public Food(long id, long res_id, String name, String description, int price, int quantity) {
        this.setId(id);
        this.setRes_id(res_id);
        this.setName(name);
        this.setDescription(description);
        this.setPrice(price);
        this.setQuantity(quantity);

    }

    public Food(long id , String name, int price, int quantity) {
        this.setId(id);
        this.setName(name);
        this.setPrice(price);
        this.setQuantity(quantity);
    }

    public Food(long res_id, String name, String description, int price ) {

        this.setId(res_id);
        this.setName(name);
        this.setDescription(description);
        this.setPrice(price);

    }

    public Food(long id, String name, String description, int price, String logo) {
        this.id = id;
        this.name = name;
        this.price=price;
        this.description = description;
        this.setLogo(logo);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRes_id() {
        return res_id;
    }

    public void setRes_id(long res_id) {
        this.res_id = res_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
