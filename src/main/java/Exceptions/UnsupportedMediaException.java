package Exceptions;

import java.io.IOException;

public class UnsupportedMediaException extends IOException {
    public UnsupportedMediaException() {

        super("Unsupported Media Type");
    }
}
