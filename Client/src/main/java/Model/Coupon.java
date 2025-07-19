package Model;

public class Coupon {

    private int min_price;

    private String code ;

    private long id ;

    private String type ;

    private Number value ;

    private int user_count;

    private String start_time ;

    private String end_time ;

    public Coupon(String code, long id, String type, Number value, int user_counts, int min_price, String start_time, String end_time){

        this.code = code;
        this.id = id;
        this.type = type;
        this.value = value;
        this.user_count = user_counts;
        this.start_time = start_time;
        this.end_time = end_time;
        this.setMin_price(min_price);

    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Number getValue() {
        return value;
    }

    public void setValue(Number value) {
        this.value = value;
    }

    public int getUser_count() {
        return user_count;
    }

    public void setUser_count(int user_count) {
        this.user_count = user_count;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public int getMin_price() {
        return min_price;
    }

    public void setMin_price(int min_price) {
        this.min_price = min_price;
    }
}
