package mate.academy.bookingapp.repository;

import java.util.List;
import mate.academy.bookingapp.model.Booking;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findBookingsByUserIdAndStatus(Long userId,
                                                Booking.Status status, Pageable pageable);

    List<Booking> findAllByUserId(Long userId, Pageable pageable);
}
