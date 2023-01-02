package server.error;

public class NickAlreadyIsBusyException extends IllegalArgumentException {
    public NickAlreadyIsBusyException() {
    }

    public NickAlreadyIsBusyException(String s) {
        super(s);
    }
}
