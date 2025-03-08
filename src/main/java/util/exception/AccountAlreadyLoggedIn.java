package util.exception;

public class AccountAlreadyLoggedIn extends RuntimeException {
    public AccountAlreadyLoggedIn(String message) {
        super(message);
    }
}
