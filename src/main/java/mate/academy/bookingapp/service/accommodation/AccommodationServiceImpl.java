package mate.academy.bookingapp.service.accommodation;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingapp.dto.accommodation.AccommodationDto;
import mate.academy.bookingapp.dto.accommodation.AccommodationRequestDto;
import mate.academy.bookingapp.dto.accommodation.AccommodationUpdateDto;
import mate.academy.bookingapp.dto.address.AddressUpdateDto;
import mate.academy.bookingapp.exception.EntityNotFoundException;
import mate.academy.bookingapp.mapper.AccommodationMapper;
import mate.academy.bookingapp.mapper.AddressMapper;
import mate.academy.bookingapp.model.Accommodation;
import mate.academy.bookingapp.model.Address;
import mate.academy.bookingapp.repository.AccommodationRepository;
import mate.academy.bookingapp.repository.AddressRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AccommodationServiceImpl implements AccommodationService {
    private final AccommodationRepository accommodationRepository;
    private final AccommodationMapper accommodationMapper;
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    @Override
    public AccommodationDto create(AccommodationRequestDto requestDto) {
        Accommodation accommodation = accommodationMapper.toModel(requestDto);
        Address address = addressRepository.save(addressMapper
                .toModel(requestDto.getLocation()));
        accommodation.setLocation(address);
        return accommodationMapper.toDto(accommodationRepository.save(accommodation));
    }

    @Override
    public AccommodationDto update(Long id, AccommodationUpdateDto updateDto) {
        Accommodation accommodation = updateAccommodation(id, updateDto);
        if (Objects.nonNull(updateDto.getLocation())) {
            Accommodation savedAccommodation = accommodationRepository.save(accommodation);
            return accommodationMapper.toDto(savedAccommodation);
        } else {
            Address address = updateAddress(id, updateDto);
            Accommodation updateAccommodation =
                    accommodationRepository.save(accommodation.setLocation(address));
            return accommodationMapper.toDto(updateAccommodation);
        }
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

    private Address updateAddress(Long id, AccommodationUpdateDto updateDto) {
        Accommodation existingAccommodation = accommodationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find an accommodation by id: " + id));

        Address existingAddress = addressRepository.findById(existingAccommodation
                        .getLocation().getId()).orElseThrow(() -> new EntityNotFoundException(
                                "Can't find an address by id: " + id));

        AddressUpdateDto addressUpdateDto = updateDto.getLocation();

        if (Objects.nonNull(addressUpdateDto.getCountry())) {
            existingAddress.setCountry(addressUpdateDto.getCountry());
        }
        if (Objects.nonNull(addressUpdateDto.getCity())) {
            existingAddress.setCity(addressUpdateDto.getCity());
        }
        if (Objects.nonNull(addressUpdateDto.getStreet())) {
            existingAddress.setStreet(addressUpdateDto.getStreet());
        }
        if (Objects.nonNull(addressUpdateDto.getAddressLine())) {
            existingAddress.setAddressLine(addressUpdateDto.getAddressLine());
        }
        if (Objects.nonNull(addressUpdateDto.getZipCode())) {
            existingAddress.setZipCode(addressUpdateDto.getZipCode());
        }
        return addressRepository.save(existingAddress);
    }

    private Accommodation updateAccommodation(Long id, AccommodationUpdateDto updateDto) {
        Accommodation existingAccommodation = accommodationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find an accommodation by id: " + id));

        if (Objects.nonNull(updateDto.getSize())) {
            existingAccommodation.setSize(updateDto.getSize());
        }
        if (Objects.nonNull(updateDto.getType())) {
            existingAccommodation.setType(updateDto.getType());
        }
        if (Objects.nonNull(updateDto.getAmenities())) {
            existingAccommodation.setAmenities(updateDto.getAmenities());
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
