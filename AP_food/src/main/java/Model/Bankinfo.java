package Model;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

@Entity
@Table(name = "bankinfo")
public class Bankinfo {

    @Id
    @Column(name = "phone")
    private String phone;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "sheba")
    private String sheba;

    @Column(name = "card")
    private String card;



    @OneToOne
    @JoinColumn(name = "phone")
    private Seller seller;

    public Bankinfo() {}

    public Bankinfo(@NotNull Seller seller, String bankName, String sheba, String card) {
        this.phone = seller.getPhone();
        this.bankName = bankName;
        this.sheba = sheba;
        this.card = card;
        this.seller = seller;
    }

    // متدهای getter و setter
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getSheba() {
        return sheba;
    }

    public void setSheba(String sheba) {
        this.sheba = sheba;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
        if (seller != null) {
            this.phone = seller.getPhone();
        }
    }
}