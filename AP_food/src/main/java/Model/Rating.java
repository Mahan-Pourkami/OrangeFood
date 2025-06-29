package Model;

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

    @ElementCollection
    private List<String> imageBase64;


    public Rating() {}

    public Rating(long item_id , int rating, String comment, List<String> imageBase64) {

        this.item_id = item_id;
        this.rating = rating;
        this.comment = comment;
        this.imageBase64 = imageBase64;
    }

}
