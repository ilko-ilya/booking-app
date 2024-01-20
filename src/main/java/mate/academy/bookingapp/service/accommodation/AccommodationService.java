package mate.academy.bookingapp.service.accommodation;

import java.util.List;
import mate.academy.bookingapp.dto.accommodation.AccommodationDto;
import mate.academy.bookingapp.dto.accommodation.AccommodationRequestDto;
import mate.academy.bookingapp.dto.accommodation.AccommodationUpdateDto;
import org.springframework.data.domain.Pageable;

public interface AccommodationService {
    AccommodationDto create(AccommodationRequestDto requestDto);

    AccommodationDto update(Long id, AccommodationUpdateDto updateDto);

    List<AccommodationDto> getAll(Pageable pageable);

    AccommodationDto getById(Long id);

    void deleteById(Long id);
}
