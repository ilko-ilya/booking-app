package mate.academy.bookingapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import mate.academy.bookingapp.dto.booking.BookingDto;
import mate.academy.bookingapp.dto.booking.BookingRequestDto;
import mate.academy.bookingapp.dto.booking.BookingUpdateDto;
import mate.academy.bookingapp.exception.AccommodationNotAvailableException;
import mate.academy.bookingapp.exception.EntityNotFoundException;
import mate.academy.bookingapp.mapper.booking.BookingMapper;
import mate.academy.bookingapp.model.Accommodation;
import mate.academy.bookingapp.model.Address;
import mate.academy.bookingapp.model.Booking;
import mate.academy.bookingapp.model.User;
import mate.academy.bookingapp.repository.AccommodationRepository;
import mate.academy.bookingapp.repository.BookingRepository;
import mate.academy.bookingapp.service.booking.BookingServiceImpl;
import mate.academy.bookingapp.service.telegram.TelegramNotificationService;
import mate.academy.bookingapp.service.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private UserService userService;
    @Mock
    private Authentication authentication;
    @Mock
    private AccommodationRepository accommodationRepository;
    @Mock
    private TelegramNotificationService notificationService;
    @InjectMocks
    private BookingServiceImpl bookingService;

    @DisplayName("Create a Booking")
    @Test
    public void createBooking_WithAvailableAccommodation_ShouldCreateBooking() {
        User user = new User();

        final Accommodation accommodation = new Accommodation();

        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setAccommodationId(1L);
        requestDto.setCheckInDate(LocalDate.now());
        requestDto.setCheckOutDate(LocalDate.now().plusDays(3));
        requestDto.setUser(user);

        when(accommodationRepository.findById(requestDto.getAccommodationId()))
                .thenReturn(Optional.of(accommodation));

        when(bookingMapper.toModel(requestDto)).thenReturn(new Booking());

        bookingService.createBooking(requestDto);

        verify(bookingRepository, times(1)).save(any());
        verify(notificationService, times(1)).notifyNewBookingCreated(any());
    }

    @DisplayName("Create Booking with Unavailable Accommodation Should Throw Exception")
    @Test
    public void createBooking_WithUnavailableAccommodation_ShouldThrowException() {
        final User user = new User();

        final Accommodation accommodation = new Accommodation();

        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setAccommodationId(1L);
        requestDto.setCheckInDate(LocalDate.now());
        requestDto.setCheckOutDate(LocalDate.now().plusDays(3));
        requestDto.setUser(user);

        when(accommodationRepository.findById(requestDto.getAccommodationId()))
                .thenReturn(Optional.of(accommodation));

        when(bookingMapper.toModel(requestDto)).thenReturn(new Booking());
        when(bookingRepository.save(any())).thenThrow(new AccommodationNotAvailableException(
                "Accommodation isn't available for the specified dates."));
        assertThrows(AccommodationNotAvailableException.class, () -> {
            bookingService.createBooking(requestDto);
        });
    }

    @DisplayName("Get list of filtered bookings by user ID and status")
    @Test
    public void getBookingsByValidUserIdAndStatus_ShouldReturnFilteredBooking() {
        User user = createUser(
                1L,
                "John",
                "Doe",
                "john.doe@example.com",
                "password123"
        );
        final Booking.Status status = Booking.Status.CONFIRMED;
        final Pageable pageable = PageRequest.of(0, 10);

        Address location = createAddress(
                "Country", "City", "Street", "Address Line", 12345);

        Accommodation accommodation = createAccommodation(
                1L, Accommodation.Type.HOUSE, location, "Large",
                Arrays.asList("WiFi", "Parking"), BigDecimal.valueOf(100), 5);

        Booking booking = createBooking(
                1L,
                LocalDate.now(),
                LocalDate.now().plusDays(3),
                Booking.Status.CONFIRMED,
                user,
                accommodation
        );

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setCheckInDate(booking.getCheckInDate());
        bookingDto.setCheckOutDate(booking.getCheckOutDate());
        bookingDto.setStatus(booking.getStatus().name());
        bookingDto.setAccommodationId(booking.getAccommodation().getId());

        final List<BookingDto> expected = List.of(bookingDto);
        List<Booking> bookingList = List.of(booking);

        when(userService.existsById(user.getId())).thenReturn(true);
        when(bookingRepository.findBookingsByUserIdAndStatus(user.getId(), status, pageable))
                .thenReturn(bookingList);
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

        List<BookingDto> actual = bookingService.getBookingsByUserIdAndStatus(
                user.getId(), status, pageable);

        assertEquals(expected.size(), actual.size());
        assertEquals(expected.get(0).getId(), actual.get(0).getId());
        assertEquals(expected.get(0).getStatus(), actual.get(0).getStatus());

        verify(userService, times(1)).existsById(user.getId());
        verify(bookingRepository, times(1))
                .findBookingsByUserIdAndStatus(user.getId(), status, pageable);
        verify(bookingMapper, times(actual.size())).toDto(any(Booking.class));
    }

    @DisplayName("Get bookings by user ID and status - User not found")
    @Test
    public void getBookingsByNonExistingUserId_ShouldThrowEntityNotFoundException() {
        Long nonExistentUserId = 999L;
        Booking.Status status = Booking.Status.CONFIRMED;
        Pageable pageable = PageRequest.of(0, 10);

        when(userService.existsById(nonExistentUserId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> {
            bookingService.getBookingsByUserIdAndStatus(nonExistentUserId, status, pageable);
        });

        verify(userService, times(1)).existsById(nonExistentUserId);
    }

    @DisplayName("Update a booking with valid bookingID")
    @Test
    public void updateBooking_WithValidId_ShouldReturnUpdatedBookingDto() {
        final Long bookingId = 1L;
        User user = createUser(
                1L,
                "John",
                "Doe",
                "john.doe@example.com",
                "password123"
        );

        Address location = createAddress(
                "Country", "City", "Street", "Address Line", 12345);

        Accommodation accommodation = createAccommodation(
                1L, Accommodation.Type.HOUSE, location, "Large",
                Arrays.asList("WiFi", "Parking"), BigDecimal.valueOf(100), 5);

        final Booking existingBooking = createBooking(
                1L,
                LocalDate.now(),
                LocalDate.now().plusDays(3),
                Booking.Status.CONFIRMED,
                user,
                accommodation
        );

        BookingUpdateDto updateDto = new BookingUpdateDto();
        updateDto.setCheckInDate(LocalDate.now());
        updateDto.setCheckOutDate(LocalDate.now().plusDays(3));
        updateDto.setStatus("CONFIRMED");

        BookingDto updatedBookingDto = new BookingDto();
        updatedBookingDto.setId(existingBooking.getId());
        updatedBookingDto.setUserId(existingBooking.getUser().getId());
        updatedBookingDto.setAccommodationId(existingBooking.getAccommodation().getId());
        updatedBookingDto.setStatus(updateDto.getStatus());
        updatedBookingDto.setCheckInDate(updateDto.getCheckInDate());
        updatedBookingDto.setCheckOutDate(updateDto.getCheckOutDate());

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(existingBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(existingBooking);
        when(bookingMapper.toDto(existingBooking)).thenReturn(updatedBookingDto);

        BookingDto result = bookingService.updateBooking(bookingId, updateDto);

        assertNotNull(result);
        assertEquals(bookingId, result.getId());
        assertEquals(updateDto.getCheckInDate(), existingBooking.getCheckInDate());
        assertEquals(updateDto.getCheckOutDate(), existingBooking.getCheckOutDate());
        assertEquals(Booking.Status.CONFIRMED, existingBooking.getStatus());
    }

    @DisplayName("Update a Booking by Non Existing id, throw EntityNotFoundException")
    @Test
    public void updateBooking_WithNonExistingBookingId_ShouldThrowEntityNotFoundException() {
        Long nonExistingId = 100L;
        BookingUpdateDto updateDto = new BookingUpdateDto();
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.updateBooking(nonExistingId, updateDto),
                "Can't find a booking by bookingID");
    }

    @DisplayName("Get list of user's bookings")
    @Test
    public void getUserBookings_WithValidUserId_ShouldReturnListOfBookingsDtos() {
        User user = createUser(
                1L,
                "John",
                "Doe",
                "john.doe@example.com",
                "password123"
        );
        Pageable pageable = PageRequest.of(0, 10);

        Address locationOne = createAddress(
                "Country", "City", "Street", "Address Line", 12345);

        Address locationTwo = createAddress(
                "CountryTwo", "CityTwo", "Street2", "Address Line2", 123456);

        Accommodation accommodationOne = createAccommodation(
                1L, Accommodation.Type.HOUSE, locationOne, "Large",
                Arrays.asList("WiFi", "Parking"), BigDecimal.valueOf(100), 5);
        Accommodation accommodationTwo = createAccommodation(
                2L, Accommodation.Type.APARTMENT, locationTwo, "Middle",
                Arrays.asList("WiFi", "Parking", "TV", "Swimming pool"), BigDecimal.valueOf(80), 3);

        Booking bookingOne = createBooking(
                1L,
                LocalDate.now(),
                LocalDate.now().plusDays(3),
                Booking.Status.PENDING,
                user,
                accommodationOne);
        Booking bookingTwo = createBooking(
                2L,
                LocalDate.now(),
                LocalDate.now().plusDays(3),
                Booking.Status.PENDING,
                user,
                accommodationTwo);

        BookingDto bookingDtoOne = createBookingDto(bookingOne);
        BookingDto bookingDtoTwo = createBookingDto(bookingTwo);

        List<Booking> userBookingList = List.of(bookingOne, bookingTwo);
        final List<BookingDto> expectedListBookingDto = List.of(bookingDtoOne, bookingDtoTwo);

        when(authentication.getPrincipal()).thenReturn(user);
        when(bookingRepository.findAllByUserId(user.getId(), pageable))
                .thenReturn(userBookingList);
        when(bookingMapper.toDto(bookingOne)).thenReturn(bookingDtoOne);
        when(bookingMapper.toDto(bookingTwo)).thenReturn(bookingDtoTwo);

        List<BookingDto> actual = bookingService.getUserBookings(authentication, pageable);

        assertEquals(expectedListBookingDto.size(), actual.size());
        assertEquals(expectedListBookingDto.get(0).getId(), actual.get(0).getId());
        assertEquals(expectedListBookingDto.get(1).getId(), actual.get(1).getId());

        verify(authentication, times(1)).getPrincipal();
        verify(bookingRepository, times(1))
                .findAllByUserId(user.getId(), pageable);
        verify(bookingMapper, times(actual.size())).toDto(any(Booking.class));
    }

    @DisplayName("Get Booking by Valid BookingID, Should return BookingDto")
    @Test
    public void getBooking_WithValidBookingId_ShouldReturnBookingDto() {
        User user = createUser(
                1L,
                "John",
                "Doe",
                "john.doe@example.com",
                "password123"
        );

        Address location = createAddress(
                "Country", "City", "Street", "Address Line", 12345);

        Accommodation accommodation = createAccommodation(
                1L, Accommodation.Type.HOUSE, location, "Large",
                Arrays.asList("WiFi", "Parking"), BigDecimal.valueOf(100), 5);

        Booking booking = createBooking(
                1L,
                LocalDate.now(),
                LocalDate.now().plusDays(3),
                Booking.Status.CONFIRMED,
                user,
                accommodation
        );
        BookingDto expected = createBookingDto(booking);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(expected);

        BookingDto actual = bookingService.getBookingById(booking.getId());

        assertEquals(expected, actual);

        verify(bookingRepository, times(1)).findById(booking.getId());
        verify(bookingMapper, times(1)).toDto(booking);
    }

    @DisplayName("Get Booking by Non Existing id. Should throw EntityNotFoundException.")
    @Test
    public void getBooking_WithNonExistingId_ShouldThrowEntityNotFoundException() {
        Long nonExistingID = 999L;
        when(bookingRepository.findById(nonExistingID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBookingById(nonExistingID),
                "Can't find a booking by bookingID: " + nonExistingID);
    }

    @DisplayName("Delete a Booking by Valid bookingID")
    @Test
    public void deleteBooking_WithValidBookingId_SuccessFullDeletion() {
        User user = createUser(
                1L,
                "John",
                "Doe",
                "john.doe@example.com",
                "password123"
        );

        Address location = createAddress(
                "Country", "City", "Street", "Address Line", 12345);

        Accommodation accommodation = createAccommodation(
                1L, Accommodation.Type.HOUSE, location, "Large",
                Arrays.asList("WiFi", "Parking"), BigDecimal.valueOf(100), 5);

        Booking booking = createBooking(
                1L,
                LocalDate.now(),
                LocalDate.now().plusDays(3),
                Booking.Status.CONFIRMED,
                user,
                accommodation
        );

        BookingDto bookingDto = createBookingDto(booking);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

        bookingService.deleteBookingById(booking.getId());

        verify(notificationService, times(1)).notifyBookingCanceled(bookingDto);
        verify(bookingRepository, times(1)).findById(booking.getId());
        verify(bookingMapper, times(1)).toDto(booking);
    }

    @DisplayName("Delete a Booking by Non Existing ID. Should throw EntityNotFoundException")
    @Test
    public void deleteBooking_WithNonExistingBookingID_ShouldThrowEntityNotFoundException() {
        Long nonExistingID = 999L;

        when(bookingRepository.findById(nonExistingID)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> bookingService.deleteBookingById(nonExistingID),
                "Can't find a booking by bookingID: " + nonExistingID);

        verify(notificationService, never()).notifyBookingCanceled(any(BookingDto.class));
        verify(bookingRepository, never()).deleteById(anyLong());

    }

    @Test
    @DisplayName("Check and Process Expired Bookings")
    public void checkAndProcessExpiredBookings_ShouldProcessExpiredBookings() {
        User user = createUser(
                1L,
                "John",
                "Doe",
                "john.doe@example.com",
                "password123"
        );

        Address location = createAddress(
                "Country", "City", "Street", "Address Line", 12345);

        Accommodation accommodation = createAccommodation(
                1L, Accommodation.Type.HOUSE, location, "Large",
                Arrays.asList("WiFi", "Parking"), BigDecimal.valueOf(100), 5);

        Booking expiredBooking = createBooking(
                1L,
                LocalDate.now(),
                LocalDate.now().minusDays(1),
                Booking.Status.PENDING,
                user,
                accommodation
        );
        List<Booking> expiredBookingsList = List.of(expiredBooking);

        when(bookingRepository.findExpiredBookings(any(LocalDate.class)))
                .thenReturn(expiredBookingsList);

        bookingService.checkAndProcessExpiredBookings();

        verify(bookingRepository, times(1)).findExpiredBookings(any(LocalDate.class));
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(notificationService, times(1))
                .notifyExpiredBooking(String.valueOf(expiredBooking.getId()));
    }

    private Booking createBooking(
            Long id,
            LocalDate checkInDate,
            LocalDate checkOutDate,
            Booking.Status status,
            User user,
            Accommodation accommodation
    ) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setCheckInDate(checkInDate);
        booking.setCheckOutDate(checkOutDate);
        booking.setStatus(status);
        booking.setUser(user);
        booking.setAccommodation(accommodation);

        return booking;
    }

    private BookingDto createBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setCheckInDate(booking.getCheckInDate());
        bookingDto.setCheckOutDate(booking.getCheckOutDate());
        bookingDto.setUserId(booking.getUser().getId());
        bookingDto.setStatus(booking.getStatus().name());
        bookingDto.setAccommodationId(booking.getAccommodation().getId());
        return bookingDto;
    }

    private User createUser(
            Long id,
            String firstName,
            String lastName,
            String email,
            String password
    ) {
        return new User()
                .setId(id)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setEmail(email)
                .setPassword(password);
    }

    private Accommodation createAccommodation(
            Long id,
            Accommodation.Type type,
            Address location,
            String size,
            List<String> amenities,
            BigDecimal dailyRate,
            Integer availability
    ) {
        Accommodation accommodation = new Accommodation();
        accommodation.setId(id);
        accommodation.setType(type);
        accommodation.setLocation(location);
        accommodation.setSize(size);
        accommodation.setAmenities(amenities);
        accommodation.setDailyRate(dailyRate);
        accommodation.setAvailability(availability);

        return accommodation;
    }

    private Address createAddress(
            String country,
            String city,
            String street,
            String addressLing,
            Integer zipCode
    ) {
        Address address = new Address();
        address.setCountry(country);
        address.setCity(city);
        address.setStreet(street);
        address.setAddressLine(addressLing);
        address.setZipCode(zipCode);

        return address;
    }
}
