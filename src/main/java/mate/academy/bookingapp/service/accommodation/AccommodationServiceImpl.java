package mate.academy.bookingapp.service.accommodation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingapp.dto.accommodation.AccommodationDto;
import mate.academy.bookingapp.dto.accommodation.AccommodationRequestDto;
import mate.academy.bookingapp.dto.accommodation.AccommodationUpdateDto;
import mate.academy.bookingapp.dto.address.AddressRequestDto;
import mate.academy.bookingapp.exception.EntityNotFoundException;
import mate.academy.bookingapp.mapper.AccommodationMapper;
import mate.academy.bookingapp.model.Accommodation;
import mate.academy.bookingapp.model.Address;
import mate.academy.bookingapp.repository.AccommodationRepository;
import mate.academy.bookingapp.service.address.AddressService;
import mate.academy.bookingapp.service.telegram.TelegramNotificationService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AccommodationServiceImpl implements AccommodationService {
    private final AccommodationRepository accommodationRepository;
    private final AccommodationMapper accommodationMapper;
    private final AddressService addressService;
    private final TelegramNotificationService telegramNotificationService;

    @Override
    public AccommodationDto create(AccommodationRequestDto requestDto) {
        Accommodation accommodation = accommodationMapper.toModel(requestDto);
        AddressRequestDto addressRequestDto = requestDto.getLocation();
        Address savedAddress = addressService.save(addressRequestDto);

        accommodation.setLocation(savedAddress);

        Accommodation savedAccommodation = accommodationRepository.save(accommodation);

        telegramNotificationService.notifyNewAccommodationCreated(
                accommodationMapper.toDto(savedAccommodation));
        return accommodationMapper.toDto(savedAccommodation);
    }

    @Override
    public AccommodationDto update(Long id, AccommodationUpdateDto updateDto) {
        Accommodation accommodation = updateAccommodation(id, updateDto);
        Accommodation savedAccommodation = accommodationRepository.save(accommodation);

        return accommodationMapper.toDto(savedAccommodation);
    }

    @Override
    public List<AccommodationDto> getAll(Pageable pageable) {
        return accommodationRepository.findAll(pageable)
                .stream()
                .map(accommodationMapper::toDto)
                .toList();
    }

    @Override
    public AccommodationDto getById(Long id) {
        Accommodation accommodation = accommodationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find accommodation by id: " + id));
        return accommodationMapper.toDto(accommodation);
    }

    @Override
    public void deleteById(Long id) {
        accommodationRepository.deleteById(id);
    }

    private Accommodation updateAccommodation(Long id, AccommodationUpdateDto updateDto) {
        Accommodation existingAccommodation = accommodationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find an accommodation by id: " + id));

        if (Objects.nonNull(updateDto.getAmenities())) {
            existingAccommodation.setAmenities(new ArrayList<>(updateDto.getAmenities()));
        }
        if (Objects.nonNull(updateDto.getDailyRate())) {
            existingAccommodation.setDailyRate(updateDto.getDailyRate());
        }
        if (Objects.nonNull(updateDto.getAvailability())) {
            existingAccommodation.setAvailability(updateDto.getAvailability());
        }
        return existingAccommodation;
    }
}
