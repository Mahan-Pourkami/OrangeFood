package Exceptions;

public class EmailException extends OrangeException {

    public EmailException() {

        super("Email Already exists", 409);
    }
}
