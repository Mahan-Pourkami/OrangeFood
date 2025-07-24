package Model;

import java.util.List;

public class Rating {

    private long comment_id ;

    private long item_id ;

    private String user_id ;

    private String comment ;

    private int rating ;

    private String time ;

    private String prof ;

    private String yours;

    private List<String> images ;

    public Rating(long comment_id, long item_id, String comment, int rating, List<String> images,String user_id,String time , String prof ,String yours) {
        this.setComment_id(comment_id);
        this.setItem_id(item_id);
        this.setComment(comment);
        this.setRating(rating);
        this.setImages(images);
        this.setTime(time);
        this.setUser_id(user_id);
        this.setProf(prof);
        this.setYours(yours);

    }

    public long getComment_id() {
        return comment_id;
    }

    public void setComment_id(long comment_id) {
        this.comment_id = comment_id;
    }

    public long getItem_id() {
        return item_id;
    }

    public void setItem_id(long item_id) {
        this.item_id = item_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getProf() {
        return prof;
    }

    public void setProf(String prof) {
        this.prof = prof;
    }

    public String getYours() {
        return yours;
    }

    public void setYours(String yours) {
        this.yours = yours;
    }
}
