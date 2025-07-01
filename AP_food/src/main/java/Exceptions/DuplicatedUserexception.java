package Exceptions;

public class DuplicatedUserexception extends OrangeException {

    public DuplicatedUserexception()
    {

        super("Phone number already exists",409);
    }
}
