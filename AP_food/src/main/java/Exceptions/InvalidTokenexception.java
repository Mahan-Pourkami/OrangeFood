package Exceptions;

import java.io.IOException;

public class InvalidTokenexception extends IOException {
    public InvalidTokenexception() {

        super("Unauthorized request");
    }
}
