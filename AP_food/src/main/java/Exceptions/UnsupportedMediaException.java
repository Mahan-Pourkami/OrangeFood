package Exceptions;

import java.io.IOException;

public class UnsupportedMediaException extends OrangeException {
    public UnsupportedMediaException() {

        super("Unsupported Media Type",415);
    }
}
