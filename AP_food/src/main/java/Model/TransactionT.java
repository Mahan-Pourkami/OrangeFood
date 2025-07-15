package Model;

import jakarta.persistence.*;

@Entity
@Table(name = "transactions")

public class TransactionT {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "order_id", length = 255)
        private Long orderId;

        @Column(name = "user_id", length = 20)
        private String userId;

        @Column(name = "method_t", length = 100)
        private String method;


        @Column
        private String status;

    public TransactionT() {}

    public TransactionT(Long orederId, String userId, String method, String status) {
        this.orderId = orederId;
        this.userId = userId;
        this.method = method;
        this.status = status;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getOrderId() {
        return orderId;
    }
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = method;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

}
