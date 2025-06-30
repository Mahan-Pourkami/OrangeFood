package Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "comments")
@Getter
@Setter
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private Long item_id ;

    @Column
    private int rating;

    @Column
    private String comment;

    @Column
    private String author_phone;

    @Column
    private String author_name;

    @Column
    private String date_added;


    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> imageBase64;


    public Rating() {}

    public Rating(String phone , String name ,long item_id , int rating, String comment, List<String> imageBase64) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        this.date_added = now.format(formatter);
        this.author_phone = phone;
        this.author_name = name;
        this.item_id = item_id;
        this.rating = rating;
        this.comment = comment;
        this.imageBase64 = imageBase64;
    }

}
