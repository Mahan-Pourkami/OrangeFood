package Model;

import jakarta.persistence.*;
import java.util.Map;
import java.util.Objects;

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
    private Map<Long,Integer> items;

    @Column
    private long res_id ;


    @Column(name = "State")
    private StateofCart stateofCart;



    public Basket() {
    }

    public Basket(Buyer buyer) {

        this.address = buyer.getAddress();
        this.buyerPhone = buyer.getPhone();
        this.buyerName = buyer.getfullname();

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