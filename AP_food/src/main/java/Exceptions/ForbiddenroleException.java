package Exceptions;

import java.io.IOException;

public class ForbiddenroleException extends OrangeException {
    public ForbiddenroleException() {

        super("Forbidden request",403);

    }

    public ForbiddenroleException(String message) {
        super(message, 403);
    }
}

