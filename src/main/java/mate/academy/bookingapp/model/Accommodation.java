package mate.academy.bookingapp.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE accommodations SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@Entity
@Table(name = "accommodations")
public class Accommodation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(unique = true, name = "type", nullable = false, columnDefinition = "varchar (255)")
    private Type type;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    @ToString.Exclude
    private Address location;
    @Column(nullable = false, name = "size")
    private String size;
    @ElementCollection
    @CollectionTable(name = "accommodations_amenities",
            joinColumns = @JoinColumn(name = "accommodation_id", referencedColumnName = "id"))
    @Column(nullable = false)
    private List<String> amenities;
    @Column(nullable = false, name = "daily_rate")
    @Min(0)
    private BigDecimal dailyRate;
    @Column(nullable = false, name = "availability")
    private Integer availability;
    @Column(nullable = false, name = "is_deleted")
    private boolean isDeleted = false;

    @Override
    public String toString() {
        return "Accommodation{"
                + "id=" + id
                + ", type=" + type
                + ", location=" + location
                + ", size='" + size + '\''
                + ", amenities=" + amenities
                + ", dailyRate=" + dailyRate
                + ", availability=" + availability
                + ", isDeleted=" + isDeleted
                + '}';
    }

    public enum Type {
        HOUSE, APARTMENT, CONDO, VACATION_HOME
    }
}
