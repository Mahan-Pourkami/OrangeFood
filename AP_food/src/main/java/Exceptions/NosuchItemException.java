package Exceptions;

import java.io.IOException;


public class NosuchItemException extends IOException {
    public NosuchItemException() {

        super("No Such Item or food found");
    }
}
