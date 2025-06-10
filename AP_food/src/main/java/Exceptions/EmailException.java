package Exceptions;

public class EmailException extends Exception {
    public EmailException() {

        super("Email Already exists");
    }
}
