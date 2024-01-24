package mate.academy.bookingapp.dto.booking;

import java.time.LocalDate;
import lombok.Data;
import mate.academy.bookingapp.model.Accommodation;
import mate.academy.bookingapp.model.User;

@Data
public class BookingDto {
    private Long id;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private User user;
    private Accommodation accommodation;
    private String status;
}
