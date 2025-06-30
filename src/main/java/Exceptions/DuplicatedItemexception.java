package Exceptions;

import java.io.IOException;

public class DuplicatedItemexception extends IOException {
    public DuplicatedItemexception() {
        super("Conflict request");
    }
}
