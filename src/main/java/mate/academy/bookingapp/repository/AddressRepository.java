package mate.academy.bookingapp.repository;

import mate.academy.bookingapp.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
