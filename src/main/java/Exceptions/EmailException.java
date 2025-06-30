package Exceptions;

import java.io.IOException;

public class EmailException extends IOException {
    public EmailException() {

        super("Email Already exists");
    }
}
