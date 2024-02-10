package mate.academy.bookingapp.exception;

public class ExpiredBookingProcessingException extends RuntimeException {
    public ExpiredBookingProcessingException(String message) {
        super(message);
    }
}
