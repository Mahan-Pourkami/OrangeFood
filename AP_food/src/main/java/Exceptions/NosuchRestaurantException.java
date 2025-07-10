package Exceptions;

import java.io.IOException;

public class NosuchRestaurantException extends OrangeException {
    public NosuchRestaurantException() {


      super("No Restaurant found",404);
    }

    public NosuchRestaurantException(String message) {
        super(message, 404);
    }
}
