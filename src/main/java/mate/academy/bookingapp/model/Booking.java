package mate.academy.bookingapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SQLDelete(sql = "UPDATE bookings SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Entity
@Accessors(chain = true)
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;
    @Column(name = "check_out_date", nullable = false)
    private LocalDate checkOutDate;
    @Column(nullable = false, name = "user_id")
    private Long userId;
    @Column(nullable = false, name = "accommodation_id")
    private Long accommodationId;
    @Column(unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(nullable = false, name = "is_deleted")
    private boolean isDeleted = false;

    public enum Status {
        PENDING, CONFIRMED, CANCELED, EXPIRED
    }
}
