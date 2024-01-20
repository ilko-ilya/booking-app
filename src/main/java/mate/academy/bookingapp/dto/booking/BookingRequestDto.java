package mate.academy.bookingapp.dto.booking;

import java.time.LocalDate;
import lombok.Data;

@Data
public class BookingRequestDto {
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Long userId;
    private Long accommodationId;
}
