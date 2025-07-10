package Exceptions;



public class InvalidTokenexception extends OrangeException {
    public InvalidTokenexception() {

        super("Unauthorized request",401);
    }
}
