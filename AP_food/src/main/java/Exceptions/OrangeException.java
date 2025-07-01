package Exceptions;

import java.io.IOException;

public class OrangeException extends IOException {

    public int http_code ;

    public OrangeException(String message , int http_code)
    {
        super(message);
        this.http_code = http_code;
    }
}
