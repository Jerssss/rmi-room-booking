package util.exception;

public class ModifyReservationException extends Exception{
    public ModifyReservationException(String message) {
        super(message);
    }

    public ModifyReservationException(String message, Throwable cause) {
        super(message, cause);
    }
}
