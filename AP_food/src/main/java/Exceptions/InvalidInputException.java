package Exceptions;


public class InvalidInputException extends OrangeException{
    public InvalidInputException(String message) {

        super("Invalid " + message , 400);
    }
}
