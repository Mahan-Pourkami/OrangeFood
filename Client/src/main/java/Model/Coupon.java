package Model;

public class Coupon {

    private String code ;

    private long id ;

    private String type ;

    private Number value ;

    private int user_counts ;

    private String start_time ;

    private String end_time ;

    public Coupon(String code, long id, String type, Number value, int user_counts, String start_time, String end_time){

        this.code = code;
        this.id = id;
        this.type = type;
        this.value = value;
        this.user_counts = user_counts;
        this.start_time = start_time;
        this.end_time = end_time;

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

    public int getUser_counts() {
        return user_counts;
    }

    public void setUser_counts(int user_counts) {
        this.user_counts = user_counts;
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
}
