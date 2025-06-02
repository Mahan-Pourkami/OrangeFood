package Execptions;

public class DuplicatedUser extends Exception {
    public DuplicatedUser()
    {
        super("The User has already been created");
    }
}
