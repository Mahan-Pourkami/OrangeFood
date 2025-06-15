package Exceptions;

import java.io.IOException;

public class NosuchRestaurantException extends IOException {
    public NosuchRestaurantException() {


      super("No Restaurant found");
    }
}
