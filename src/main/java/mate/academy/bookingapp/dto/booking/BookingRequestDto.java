package mate.academy.bookingapp.dto.booking;

import java.time.LocalDate;
import lombok.Data;
import mate.academy.bookingapp.model.User;

@Data
public class BookingRequestDto {
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private User user;
    private Long accommodationId;
}
