package server.error;

public class UserNotFoundException extends IllegalArgumentException {
    public UserNotFoundException() {
    }

    public UserNotFoundException(String s) {
        super(s);
    }
}
