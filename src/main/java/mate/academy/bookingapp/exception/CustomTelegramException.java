package mate.academy.bookingapp.exception;

public class CustomTelegramException extends RuntimeException {
    public CustomTelegramException(String message) {
        super(message);
    }
}
