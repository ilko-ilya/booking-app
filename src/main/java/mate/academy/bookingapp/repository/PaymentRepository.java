package mate.academy.bookingapp.repository;

import java.util.List;
import java.util.Optional;
import mate.academy.bookingapp.model.Payment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findPaymentById(Long id);

    List<Payment> findPaymentsByBookingId(Long bookingId, Pageable pageable);

    List<Payment> findPaymentsByStatus(Payment.Status status, Pageable pageable);

    @Query("SELECT p FROM Payment p "
            + "JOIN Booking b ON p.bookingId = b.id "
            + "WHERE b.user.id = :userId")
    List<Payment> findPaymentsByUserId(@Param("userId") Long userId);
}
