package Exceptions;

import java.io.IOException;

public class DuplicatedItemexception extends OrangeException {
    public DuplicatedItemexception() {
        super("Conflict request" , 409);
    }
}
