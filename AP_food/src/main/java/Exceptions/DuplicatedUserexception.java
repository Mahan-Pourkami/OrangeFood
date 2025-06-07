package Exceptions;

public class DuplicatedUserexception extends Exception {

    public DuplicatedUserexception()
    {

        super("The User has already been created");
    }
}
