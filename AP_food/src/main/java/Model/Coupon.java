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


    /**
     * @param price
     * @throws DateTimeParseException if the text cannot be parsed
     */
    public boolean is_valid(int price) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDateTime start_time = LocalDateTime.parse(this.start_time, formatter);
        LocalDateTime end_time = LocalDateTime.parse(this.end_time, formatter);

        LocalDateTime now = LocalDateTime.now();

        return now.isAfter(start_time) && now.isBefore(end_time) && this.user_counts > 0 && price > this.min_price;
    }

}
