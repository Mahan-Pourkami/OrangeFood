package Model;

public class Menu {

    private String title ;

    private long vendorId ;

    public Menu(String title, long vendorId) {
        this.title = title;
        this.vendorId = vendorId;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getVendorId() {
        return vendorId;
    }

    public void setVendorId(long vendorId) {
        this.vendorId = vendorId;
    }
}
