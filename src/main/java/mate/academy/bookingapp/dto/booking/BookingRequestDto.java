package mate.academy.bookingapp.dto.booking;

import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.Data;
import mate.academy.bookingapp.model.User;

@Data
public class BookingRequestDto {
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    @Valid
    private User user;
    private Long accommodationId;
}
