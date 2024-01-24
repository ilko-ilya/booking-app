package mate.academy.bookingapp.dto.booking;

import java.time.LocalDate;
import lombok.Data;

@Data

public class BookingUpdateDto {
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String status;
}
