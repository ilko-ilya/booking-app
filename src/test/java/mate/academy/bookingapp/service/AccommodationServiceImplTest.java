package mate.academy.bookingapp.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import mate.academy.bookingapp.dto.accommodation.AccommodationDto;
import mate.academy.bookingapp.dto.accommodation.AccommodationRequestDto;
import mate.academy.bookingapp.dto.accommodation.AccommodationUpdateDto;
import mate.academy.bookingapp.dto.address.AddressRequestDto;
import mate.academy.bookingapp.exception.EntityNotFoundException;
import mate.academy.bookingapp.mapper.AccommodationMapper;
import mate.academy.bookingapp.model.Accommodation;
import mate.academy.bookingapp.model.Address;
import mate.academy.bookingapp.repository.AccommodationRepository;
import mate.academy.bookingapp.repository.AddressRepository;
import mate.academy.bookingapp.service.accommodation.AccommodationServiceImpl;
import mate.academy.bookingapp.service.address.AddressService;
import mate.academy.bookingapp.service.telegram.TelegramNotificationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class AccommodationServiceImplTest {
    private static final Logger log = LoggerFactory.getLogger(AccommodationServiceImplTest.class);
    private static final Long NOT_EXISTING_ACCOMMODATION_ID = 10L;
    @Mock
    private AccommodationRepository accommodationRepository;
    @Mock
    private AccommodationMapper accommodationMapper;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private AddressService addressService;
    @Mock
    private TelegramNotificationService telegramNotificationService;
    @InjectMocks
    private AccommodationServiceImpl accommodationService;
    private Accommodation testAccommodationOne;
    private Accommodation testAccommodationTwo;
    private AccommodationRequestDto accommodationOneRequestDto;
    private AccommodationDto accommodationOneDto;
    private AccommodationDto accommodationTwoDto;
    private AddressRequestDto addressOneRequestDto;
    private AddressRequestDto addressTwoRequestDto;
    private Address testAddressOne;
    private Address testAddressTwo;

    @BeforeEach
    public void setUp() {
        log.info("Setting up test data...");

        testAddressOne = new Address();
        testAddressOne.setId(1L);
        testAddressOne.setCountry("Ukraine");
        testAddressOne.setCity("Donetsk");
        testAddressOne.setStreet("Polockaya");
        testAddressOne.setAddressLine("44");
        testAddressOne.setZipCode(23232);
        testAddressOne.setDeleted(false);

        testAddressTwo = new Address();
        testAddressTwo.setId(2L);
        testAddressTwo.setCountry("USA");
        testAddressTwo.setCity("Washington");
        testAddressTwo.setStreet("Slava Ukraini");
        testAddressTwo.setAddressLine("55");
        testAddressTwo.setZipCode(54321);
        testAddressTwo.setDeleted(false);

        testAccommodationOne = new Accommodation();
        testAccommodationOne.setId(1L);
        testAccommodationOne.setAmenities(List.of("TV", "Air-conditioner"));
        testAccommodationOne.setType(Accommodation.Type.APARTMENT);
        testAccommodationOne.setLocation(testAddressOne);
        testAccommodationOne.setAvailability(5);
        testAccommodationOne.setDailyRate(BigDecimal.valueOf(100));
        testAccommodationOne.setSize("100");
        testAccommodationOne.setDeleted(false);

        testAccommodationTwo = new Accommodation();
        testAccommodationTwo.setId(2L);
        testAccommodationTwo.setAmenities(List.of("internet", "swimming pool", "gym"));
        testAccommodationTwo.setType(Accommodation.Type.HOUSE);
        testAccommodationTwo.setSize("150");
        testAccommodationTwo.setAvailability(8);
        testAccommodationTwo.setLocation(testAddressTwo);
        testAccommodationTwo.setDailyRate(BigDecimal.valueOf(200));
        testAccommodationTwo.setDeleted(false);

        addressOneRequestDto = new AddressRequestDto();
        addressOneRequestDto.setCountry(testAddressOne.getCountry());
        addressOneRequestDto.setCity(testAddressOne.getCity());
        addressOneRequestDto.setStreet(testAddressOne.getStreet());
        addressOneRequestDto.setAddressLine(testAddressOne.getAddressLine());
        addressOneRequestDto.setZipCode(testAddressOne.getZipCode());

        addressTwoRequestDto = new AddressRequestDto();
        addressTwoRequestDto.setCountry(testAddressTwo.getCountry());
        addressTwoRequestDto.setCity(testAddressTwo.getCity());
        addressTwoRequestDto.setStreet(testAddressTwo.getStreet());
        addressTwoRequestDto.setAddressLine(testAddressTwo.getAddressLine());
        addressTwoRequestDto.setZipCode(testAddressTwo.getZipCode());

        accommodationOneDto = new AccommodationDto();
        accommodationOneDto.setId(testAccommodationOne.getId());
        accommodationOneDto.setType(String.valueOf(testAccommodationOne.getType()));
        accommodationOneDto.setSize(testAccommodationOne.getSize());
        accommodationOneDto.setDailyRate(testAccommodationOne.getDailyRate());
        accommodationOneDto.setAmenities(testAccommodationOne.getAmenities());
        accommodationOneDto.setAvailability(testAccommodationOne.getAvailability());
        accommodationOneDto.setLocationId(testAccommodationOne.getLocation().getId());

        accommodationTwoDto = new AccommodationDto();
        accommodationTwoDto.setId(testAccommodationTwo.getId());
        accommodationTwoDto.setType(String.valueOf(testAccommodationTwo.getType()));
        accommodationTwoDto.setSize(testAccommodationTwo.getSize());
        accommodationTwoDto.setDailyRate(testAccommodationTwo.getDailyRate());
        accommodationTwoDto.setAmenities(testAccommodationTwo.getAmenities());
        accommodationTwoDto.setAvailability(testAccommodationTwo.getAvailability());
        accommodationTwoDto.setLocationId(testAccommodationTwo.getLocation().getId());

        accommodationOneRequestDto = new AccommodationRequestDto();
        accommodationOneRequestDto.setType(String.valueOf(testAccommodationOne.getType()));
        accommodationOneRequestDto.setLocation(addressOneRequestDto);
        accommodationOneRequestDto.setDailyRate(testAccommodationOne.getDailyRate());
        accommodationOneRequestDto.setAvailability(testAccommodationOne.getAvailability());
        accommodationOneRequestDto.setSize(testAccommodationOne.getSize());

        AccommodationRequestDto accommodationTwoRequestDto = new AccommodationRequestDto();
        accommodationTwoRequestDto.setType(String.valueOf(testAccommodationTwo.getType()));
        accommodationTwoRequestDto.setLocation(addressTwoRequestDto);
        accommodationTwoRequestDto.setDailyRate(testAccommodationTwo.getDailyRate());
        accommodationTwoRequestDto.setAvailability(testAccommodationTwo.getAvailability());
        accommodationTwoRequestDto.setSize(testAccommodationTwo.getSize());

        log.info("Test data setup complete.");
    }

    @AfterEach
    public void tearDown() {
        accommodationRepository.deleteAll();
        addressRepository.deleteAll();
    }

    @DisplayName("Create an accommodation")
    @Test
    public void createAccommodation_WithValidData_ShouldReturnSavedAccommodation() {
        when(accommodationMapper.toModel(accommodationOneRequestDto))
                .thenReturn(testAccommodationOne);
        when(accommodationRepository.save(testAccommodationOne))
                .thenReturn(testAccommodationOne);
        when(accommodationMapper.toDto(testAccommodationOne))
                .thenReturn(accommodationOneDto);
        when(addressService.save(addressOneRequestDto)).thenReturn(testAddressOne);

        AccommodationDto actual = accommodationService.create(accommodationOneRequestDto);
        assertEquals(accommodationOneDto, actual);
        verify(accommodationRepository, times(1)).save(testAccommodationOne);
        verify(addressService, times(1)).save(addressOneRequestDto);
        verify(telegramNotificationService, times(1))
                .notifyNewAccommodationCreated(accommodationOneDto);
    }

    @DisplayName("Verify the correct accommodation was returned when accommodation exists")
    @Test
    public void getAccommodation_ByExistingId_ShouldReturnAccommodation() {
        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(testAccommodationOne));
        AccommodationDto expected = accommodationOneDto;

        when(accommodationMapper.toDto(testAccommodationOne)).thenReturn(accommodationOneDto);

        AccommodationDto actual = accommodationService.getById(1L);
        assertEquals(expected, actual);
    }

    @DisplayName("Verify that book with such ID doesn't exist")
    @Test
    public void getAccommodation_ByNonExistingId_ShouldReturnException() {
        when(accommodationRepository.findById(NOT_EXISTING_ACCOMMODATION_ID))
                .thenReturn(Optional.empty());
        Exception exception = assertThrows(RuntimeException.class,
                () -> accommodationService.getById(NOT_EXISTING_ACCOMMODATION_ID));

        assertEquals("Can't find accommodation by id: "
                + NOT_EXISTING_ACCOMMODATION_ID, exception.getMessage());

    }

    @DisplayName("Get all accommodations")
    @Test
    public void getAllAccommodation_WithValidPageable_ShouldReturnAllAccommodations() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Accommodation> list = List.of(testAccommodationOne, testAccommodationTwo);
        Page<Accommodation> accommodationPage = new PageImpl<>(list, pageable, list.size());

        when(accommodationRepository.findAll(pageable)).thenReturn(accommodationPage);

        when(accommodationMapper.toDto(testAccommodationOne)).thenReturn(accommodationOneDto);
        when(accommodationMapper.toDto(testAccommodationTwo)).thenReturn(accommodationTwoDto);

        List<AccommodationDto> accommodationDtos = accommodationService.getAll(pageable);

        assertFalse(accommodationDtos.isEmpty());
        assertEquals(2, accommodationDtos.size());
        assertEquals(accommodationOneDto, accommodationDtos.get(0));
        assertEquals(accommodationTwoDto, accommodationDtos.get(1));
    }

    @DisplayName("Delete an accommodation by existing ID")
    @Test
    public void deleteAccommodationById_WithValidId_SuccessFullDeletion() {

        Long idToDelete = testAccommodationOne.getId();

        assertDoesNotThrow(() -> accommodationService.deleteById(idToDelete));

        assertThrows(EntityNotFoundException.class, () -> accommodationService.getById(idToDelete));
    }

    @DisplayName("Update an accommodation")
    @Test
    public void updateAccommodation_ShouldReturnUpdateAccommodation() {
        final Long idToUpdate = 1L;
        AccommodationUpdateDto updateDto = new AccommodationUpdateDto();
        updateDto.setAmenities(Arrays.asList("New TV", "New Air-conditioner"));
        updateDto.setDailyRate(BigDecimal.valueOf(150));
        updateDto.setAvailability(8);

        Accommodation existingAccommodation = new Accommodation();
        existingAccommodation.setId(idToUpdate);
        existingAccommodation.setAmenities(Arrays.asList("TV", "Air-conditioner"));
        existingAccommodation.setType(Accommodation.Type.APARTMENT);
        existingAccommodation.setLocation(testAddressOne);
        existingAccommodation.setAvailability(5);
        existingAccommodation.setDailyRate(BigDecimal.valueOf(100));
        existingAccommodation.setSize("100");
        existingAccommodation.setDeleted(false);

        AccommodationDto expectedAccommodationDto = new AccommodationDto();
        expectedAccommodationDto.setId(idToUpdate);
        expectedAccommodationDto.setType(String.valueOf(existingAccommodation.getType()));
        expectedAccommodationDto.setSize(existingAccommodation.getSize());
        expectedAccommodationDto.setDailyRate(updateDto.getDailyRate());
        expectedAccommodationDto.setAmenities(updateDto.getAmenities());
        expectedAccommodationDto.setAvailability(updateDto.getAvailability());
        expectedAccommodationDto.setLocationId(existingAccommodation.getLocation().getId());

        when(accommodationRepository.findById(idToUpdate))
                .thenReturn(Optional.of(existingAccommodation));
        when(accommodationRepository.save(existingAccommodation))
                .thenReturn(existingAccommodation);
        when(accommodationMapper.toDto(existingAccommodation))
                .thenReturn(expectedAccommodationDto);

        AccommodationDto updatedAccommodationDto =
                accommodationService.update(idToUpdate, updateDto);

        assertNotNull(updatedAccommodationDto,
                "Expected non-null AccommodationDto after update");
        assertEquals(expectedAccommodationDto, updatedAccommodationDto);

        verify(accommodationRepository, times(1)).findById(idToUpdate);
        verify(accommodationRepository, times(1)).save(existingAccommodation);
        verify(accommodationMapper, times(1)).toDto(existingAccommodation);
    }

    @DisplayName(
            "Update an accommodation with non-existing ID should throw EntityNotFoundException"
    )
    @Test
    public void updateAccommodationByNonExistingId_ShouldThrowEntityNotFoundException() {
        Long nonExistingId = 100L;
        AccommodationUpdateDto updateDto = new AccommodationUpdateDto();
        when(accommodationRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> accommodationService.update(nonExistingId, updateDto),
                "Can't find accommodation by id: ");

    }
}
