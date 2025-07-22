package Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Entity
@Table(name = "coupons")
@Getter @Setter
public class Coupon {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String code;

    @Column
    private Number value;

    @Column
    private Coupontype type;

    @Column
    private int min_price;

    @Column
    private int user_counts;

    @Column
    private String start_time;

    @Column
    private String end_time;

    public Coupon() {}

    public Coupon(String code, Number value, String type, int min_price, int user_counts, String start_time, String end_time) {
        this.code = code;
        this.value = value;
        this.type = Coupontype.valueOf(type);
        this.min_price = min_price;
        this.user_counts = user_counts;
        this.set_start_time(start_time); // Use setters to ensure validation
        this.set_end_time(end_time);
    }

    public boolean is_valid(int price) {

        if (price < 0) {
            return false;
        }

        if (start_time != null && end_time != null &&
                !start_time.isEmpty() && !end_time.isEmpty()) {
            try {
                LocalDate start = LocalDate.parse(start_time, DATE_FORMATTER);
                LocalDate end = LocalDate.parse(end_time, DATE_FORMATTER);
                LocalDate now = LocalDate.now();
                return now.isAfter(start) && now.isBefore(end) &&
                        this.user_counts >= 0 && price >= this.min_price;
            } catch (DateTimeParseException e) {
                return false;
            }
        }
        return this.user_counts >= 0 && price >= this.min_price;
    }

    public void set_start_time(String start_time) {
        if (start_time != null && !start_time.isEmpty()) {
            try {
                DATE_FORMATTER.parse(start_time);
                this.start_time = start_time;
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid start_time format. Expected yyyy-MM-dd");
            }
        } else {
            this.start_time = null;
        }
    }

    public void set_end_time(String end_time) {
        if (end_time != null && !end_time.isEmpty()) {
            try {
                DATE_FORMATTER.parse(end_time);
                this.end_time = end_time;
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid end_time format. Expected yyyy-MM-dd");
            }
        } else {
            this.end_time = null;
        }
    }
}