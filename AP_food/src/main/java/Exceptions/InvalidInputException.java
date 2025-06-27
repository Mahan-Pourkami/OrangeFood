package Exceptions;

import java.io.IOException;

public class InvalidInputException extends IOException {
    public InvalidInputException(String message) {
        super("Invalid" + message);
    }
}
