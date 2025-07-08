package Model;

import Exceptions.InvalidInputException;
import jakarta.persistence.*;

@Entity
@Table(name = "bankinfo")
public class Bankinfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "account_number")
    private String accountNumber;

    @OneToOne(mappedBy = "bankinfo")
    private User user;


    public Bankinfo() {}

    public Bankinfo(String bankName, String accountNumber) throws InvalidInputException {

        if(bankName.length()<3)
            throw new InvalidInputException("Invalid Bank name");

        if(accountNumber.length()!=16)
            throw new InvalidInputException("Account number must be 16 characters");

        this.setBankName(bankName);
        this.setAccountNumber(accountNumber);
    }


    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}