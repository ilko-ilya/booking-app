package mate.academy.bookingapp.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import mate.academy.bookingapp.model.Accommodation;
import mate.academy.bookingapp.model.Address;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AccommodationRepositoryTest {
    @Autowired
    private AccommodationRepository accommodationRepository;

    @DisplayName("Get all accommodations")
    @Test
    public void getAll() {
        Address location = new Address();
        location.setId(1L);
        location.setCountry("Italy");
        location.setCity("Milan");
        location.setStreet("Taras Shevchenko");
        location.setAddressLine("123");
        location.setZipCode(4400);

        Address locationTwo = new Address();
        locationTwo.setId(2L);
        locationTwo.setCountry("Germany");
        locationTwo.setCity("Berlin");
        locationTwo.setStreet("Stepana Banderi");
        locationTwo.setAddressLine("456");
        locationTwo.setZipCode(9800);

        Accommodation accommodation1 = new Accommodation();
        accommodation1.setId(4L);
        accommodation1.setType(Accommodation.Type.APARTMENT);
        accommodation1.setAmenities(Arrays.asList("TV", "Air-conditioner"));
        accommodation1.setDailyRate(BigDecimal.valueOf(100));
        accommodation1.setAvailability(5);
        accommodation1.setLocation(location);
        accommodation1.setSize("100");

        Accommodation accommodation2 = new Accommodation();
        accommodation2.setId(5L);
        accommodation2.setType(Accommodation.Type.HOUSE);
        accommodation2.setAmenities(List.of("Wi-Fi", "Swimming pool"));
        accommodation2.setDailyRate(BigDecimal.valueOf(150));
        accommodation2.setAvailability(8);
        accommodation2.setLocation(locationTwo);
        accommodation2.setSize("150");

        accommodationRepository.save(accommodation1);
        accommodationRepository.save(accommodation2);

        Page<Accommodation> accommodationsPage =
                accommodationRepository.findAll(PageRequest.of(0, 10));

        assertNotNull(accommodationsPage);
        assertEquals(5, accommodationsPage.getTotalElements());
    }

    @DisplayName("Get Accommodation by AccommodationID")
    @Test
    public void getAccommodationById() {
        Accommodation accommodation = getAccommodation();

        accommodationRepository.save(accommodation);

        Optional<Accommodation> result = accommodationRepository.findById(accommodation.getId());
        System.out.println(result);
        assertTrue(result.isPresent());

        assertEquals(result.get().getId(), accommodation.getId());
        assertEquals(result.get().getSize(), accommodation.getSize());
        assertEquals(result.get().getType(), accommodation.getType());
        assertEquals(result.get().getLocation(), accommodation.getLocation());
        assertEquals(result.get().getDailyRate(), accommodation.getDailyRate());
        assertEquals(result.get().getAmenities(), accommodation.getAmenities());
    }

    @NonNull
    private static Accommodation getAccommodation() {
        Address location = new Address();
        location.setId(1L);
        location.setCountry("Italy");
        location.setCity("Milan");
        location.setStreet("Taras Shevchenko");
        location.setAddressLine("123");
        location.setZipCode(4400);

        Accommodation accommodation1 = new Accommodation();
        accommodation1.setId(6L);
        accommodation1.setType(Accommodation.Type.APARTMENT);
        accommodation1.setAmenities(Arrays.asList("TV", "Air-conditioner"));
        accommodation1.setDailyRate(BigDecimal.valueOf(100));
        accommodation1.setAvailability(5);
        accommodation1.setLocation(location);
        accommodation1.setSize("100");
        return accommodation1;
    }
}
