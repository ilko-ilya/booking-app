package mate.academy.bookingapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE addresses SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
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

    public Address(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Address address = (Address) o;
        return isDeleted == address.isDeleted
                && Objects.equals(id, address.id)
                && Objects.equals(country, address.country)
                && Objects.equals(city, address.city)
                && Objects.equals(street, address.street)
                && Objects.equals(addressLine, address.addressLine)
                && Objects.equals(zipCode, address.zipCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, country, city, street, addressLine, zipCode, isDeleted);
    }
}

