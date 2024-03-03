package mate.academy.bookingapp.repository;

import java.util.Optional;
import lombok.NonNull;
import mate.academy.bookingapp.model.Accommodation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {
    @EntityGraph(attributePaths = {"location"})
    @NonNull
    Page<Accommodation> findAll(@NonNull Pageable pageable);

    @EntityGraph(attributePaths = {"location"})
    @NonNull
    Optional<Accommodation> findById(@NonNull Long id);
}
