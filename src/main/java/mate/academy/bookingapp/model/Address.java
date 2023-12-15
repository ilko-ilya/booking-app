package mate.academy.bookingapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE addresses SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Data
@Entity
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, name = "country")
    private String country;
    @Column(nullable = false, name = "city")
    private String city;
    @Column(nullable = false, name = "street")
    private String street;
    @Column(nullable = false, name = "address_line")
    private String addressLine;
    @Column(nullable = false, name = "zip_code")
    private Integer zipCode;
    @Column(nullable = false, name = "is_deleted")
    private boolean isDeleted = false;
    @OneToOne(mappedBy = "location")
    private Accommodation accommodation;
}
