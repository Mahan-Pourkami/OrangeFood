package Exceptions;

public class InvalidTokenexception extends RuntimeException {
    public InvalidTokenexception() {

        super("Unauthorized request");
    }
}
