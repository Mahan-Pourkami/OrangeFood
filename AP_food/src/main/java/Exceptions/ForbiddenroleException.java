package Exceptions;

import java.io.IOException;

public class ForbiddenroleException extends OrangeException {
    public ForbiddenroleException() {

        super("Forbidden request",403);

    }
}
