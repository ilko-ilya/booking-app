package mate.academy.bookingapp.service.booking;

import java.util.List;
import mate.academy.bookingapp.dto.booking.BookingDto;
import mate.academy.bookingapp.dto.booking.BookingRequestDto;
import mate.academy.bookingapp.dto.booking.BookingUpdateDto;
import mate.academy.bookingapp.model.Booking;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface BookingService {
    BookingDto createBooking(BookingRequestDto requestDto);

    List<BookingDto> getBookingsByUserIdAndStatus(
            Long userId,
            Booking.Status status,
            Pageable pageable
    );

    BookingDto updateBooking(Long id, BookingUpdateDto updateDto);

    List<BookingDto> getUserBookings(Authentication authentication, Pageable pageable);

    BookingDto getBookingById(Long id);

    void deleteBookingById(Long id);
}
