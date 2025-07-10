package Exceptions;

import java.io.IOException;


public class NosuchItemException extends OrangeException {
    public NosuchItemException() {

        super("No Such Item or food found",404);
    }

    public NosuchItemException(String message) {
        super(message, 404);
    }
}
