package mate.academy.bookingapp.repository;

import java.time.LocalDate;
import java.util.List;
import mate.academy.bookingapp.model.Accommodation;
import mate.academy.bookingapp.model.Booking;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findBookingsByUserIdAndStatus(Long userId,
                                                Booking.Status status, Pageable pageable);

    List<Booking> findAllByUserId(Long userId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.accommodation = :accommodation "
            + "AND ((b.checkInDate BETWEEN :checkInDate AND :checkOutDate) OR "
            + "(b.checkOutDate BETWEEN :checkInDate AND :checkOutDate)) "
            + "AND b.status = 'CONFIRMED'")
    List<Booking> findOverlappingBookings(
            @Param("accommodation") Accommodation accommodation,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate
    );

    @Query("SELECT b FROM Booking b WHERE b.checkOutDate <= :tomorrow AND b.status != 'CANCELED'")
    List<Booking> findExpiredBookings(@Param("tomorrow") LocalDate tomorrow);
}

