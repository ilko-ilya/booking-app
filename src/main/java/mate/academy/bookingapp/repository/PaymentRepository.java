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

    @Query("SELECT p FROM Payment p "
            + "JOIN Booking b ON p.bookingId = b.id "
            + "WHERE b.user.id = :userId AND p.status = :status")
    List<Payment> findPaymentsByUserIdAndStatus(
            @Param("userId") Long userId,
            @Param("status") Payment.Status status,
            Pageable pageable);

    @Query("SELECT p FROM Payment p "
            + "JOIN Booking b ON p.bookingId = b.id "
            + "WHERE b.user.id = :userId")
    List<Payment> findPaymentsByUserId(@Param("userId") Long userId);

    @Query("SELECT p FROM Payment p "
            + "JOIN Booking b ON p.bookingId = b.id "
            + "WHERE b.user.id = :userId AND p.status = 'PENDING'")
    List<Payment> findPendingPaymentsByUserId(@Param("userId") Long userId);
}
