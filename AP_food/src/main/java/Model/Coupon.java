package Model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Entity
@Table(name = "coupons")
@Getter @Setter
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @Column
    private String code;

    @Column
    private Number value ;


    @Column
    private Coupontype type ;

    @Column
    private int min_price ;

    @Column
    private int user_counts ;

    @Column
    private String start_time ;

    @Column
    private String end_time ;

    public Coupon() {}
    public Coupon(String code, Number value, String type , int min_price , int user_counts , String start_time , String end_time ) {
        this.code = code;
        this.value = value;
        this.type= Coupontype.valueOf(type);
        this.min_price = min_price;
        this.user_counts = user_counts;
        this.start_time = start_time;
        this.end_time = end_time;
    }

    /**
     * @param price
     * @throws DateTimeParseException if the text cannot be parsed
     */
    public boolean is_valid(int price) {


        if(!start_time.isEmpty() && !end_time.isEmpty()){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            LocalDateTime start_time = LocalDateTime.parse(this.start_time, formatter);
            LocalDateTime end_time = LocalDateTime.parse(this.end_time, formatter);

            LocalDateTime now = LocalDateTime.now();

            return now.isAfter(start_time) && now.isBefore(end_time) && this.user_counts > 0 && price > this.min_price;
        }

        else return this.user_counts > 0 && this.min_price > 0;
    }

}
