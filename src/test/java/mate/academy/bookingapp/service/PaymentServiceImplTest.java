package mate.academy.bookingapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import mate.academy.bookingapp.dto.payment.CancelledPaymentResponseDto;
import mate.academy.bookingapp.dto.payment.CreatePaymentRequestDto;
import mate.academy.bookingapp.dto.payment.PaymentDto;
import mate.academy.bookingapp.dto.payment.PaymentSessionDto;
import mate.academy.bookingapp.dto.payment.SuccessfulPaymentResponseDto;
import mate.academy.bookingapp.exception.EntityNotFoundException;
import mate.academy.bookingapp.mapper.payment.PaymentMapper;
import mate.academy.bookingapp.model.Accommodation;
import mate.academy.bookingapp.model.Address;
import mate.academy.bookingapp.model.Booking;
import mate.academy.bookingapp.model.Payment;
import mate.academy.bookingapp.model.User;
import mate.academy.bookingapp.repository.BookingRepository;
import mate.academy.bookingapp.repository.PaymentRepository;
import mate.academy.bookingapp.service.payment.PaymentProcessor;
import mate.academy.bookingapp.service.payment.PaymentServiceImpl;
import mate.academy.bookingapp.service.stripe.StripePaymentServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private StripePaymentServiceImpl stripePaymentService;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PaymentProcessor paymentProcessor;
    @InjectMocks
    private PaymentServiceImpl paymentService;

    @DisplayName("Get all User's payments by Valid userID."
            + " Should return List of User's paymentDto's")
    @Test
    public void getPaymentsForUser_WithValidUserId_ShouldReturnListOfUserPaymentDto()
            throws MalformedURLException {
        Long userId = 1L;
        Payment paymentOne =
                createPayment(
                        1L,
                        Payment.Status.PENDING,
                        1L,
                        new URL("http://example.com/1"),
                        BigDecimal.valueOf(200));

        Payment paymentTwo =
                createPayment(
                        2L,
                        Payment.Status.PAID,
                        2L,
                        new URL("http://example.com/2"),
                        BigDecimal.valueOf(300));
        Payment paymentThree =
                createPayment(
                        3L,
                        Payment.Status.CANCELED,
                        3L,
                        new URL("http://example.com/3"),
                        BigDecimal.valueOf(400));

        final PaymentDto paymentDtoOne = createPaymentDto(paymentOne);
        final PaymentDto paymentDtoTwo = createPaymentDto(paymentTwo);
        final PaymentDto paymentDtoThree = createPaymentDto(paymentThree);

        Pageable pageable = Pageable.unpaged();

        List<Payment> paymentList = List.of(paymentOne, paymentTwo, paymentThree);

        when(paymentRepository.findPaymentsByUserId(eq(userId), any(Pageable.class)))
                .thenReturn(new PageImpl<>(paymentList, pageable, paymentList.size()));

        for (Payment payment : paymentList) {
            when(paymentMapper.toDto(payment)).thenReturn(createPaymentDto(payment));
        }

        List<PaymentDto> actual = paymentService.getPaymentsForUser(userId, Pageable.unpaged());

        verify(paymentRepository, times(1)).findPaymentsByUserId(userId, pageable);

        for (Payment payment : paymentList) {
            verify(paymentMapper, times(1)).toDto(payment);
        }

        List<PaymentDto> expectedPaymentDtoList = List.of(
                paymentDtoOne,
                paymentDtoTwo,
                paymentDtoThree
        );
        assertEquals(expectedPaymentDtoList, actual);
    }

    @DisplayName("Initiate Payment Session")
    @Test
    public void initiatePaymentSession_ShouldCreatePaymentAndReturnSessionId() {
        User user = createUser(
                1L,
                "John",
                "Doe",
                "john.doe@example.com",
                "password123"
        );

        Address addressOne = createAddress(
                "Country",
                "City",
                "Street",
                "Address line",
                123
        );

        Accommodation accommodation
                = createAccommodation(
                1L,
                List.of("TV", "Air conditioner"),
                Accommodation.Type.APARTMENT,
                addressOne,
                5,
                BigDecimal.valueOf(100),
                "80");

        Booking booking = createBooking(
                1L,
                LocalDate.now(),
                LocalDate.now().plusDays(3),
                Booking.Status.PENDING,
                user,
                accommodation
        );
        String clientSecret = "testClientSecret";
        String sessionUrl = "testSessionUrl";
        PaymentSessionDto paymentSessionDto = new PaymentSessionDto(clientSecret, sessionUrl);
        paymentSessionDto.setSessionId("testSessionId");

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(stripePaymentService.createPaymentSession(booking.getId()))
                .thenReturn(paymentSessionDto);

        final String sessionId = paymentService.initiatePaymentSession(
                new CreatePaymentRequestDto(booking.getId()));

        verify(bookingRepository, times(1)).findById(booking.getId());
        verify(stripePaymentService, times(1))
                .createPaymentSession(booking.getId());
        verify(paymentRepository, times(1)).save(any(Payment.class));

        assertEquals(paymentSessionDto.getSessionId(), sessionId);
    }

    @DisplayName("Initiate paymentSession with Non Existing BookingID."
            + " Should throw EntityNotFoundException")
    @Test
    public void initiatePaymentSession_WithNonExistingBookingId_ShouldThrowException() {
        Long nonExistingBookingId = 999L;

        when(bookingRepository.findById(nonExistingBookingId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            paymentService.initiatePaymentSession(
                    new CreatePaymentRequestDto(nonExistingBookingId));
        });
    }

    @DisplayName("Handle Successful payment")
    @Test
    public void handleSuccessfulPayment_ShouldInvokePaymentProcessor() {
        String paymentId = "Test paymentID";

        SuccessfulPaymentResponseDto expectedResponseDto = new SuccessfulPaymentResponseDto();
        expectedResponseDto.setPaymentId(paymentId);
        expectedResponseDto.setStatus("Success");
        expectedResponseDto.setSessionUrl("https://example.com/session");

        when(paymentProcessor.processSuccessfulPayment(paymentId))
                .thenReturn(expectedResponseDto);

        SuccessfulPaymentResponseDto actualResponseDto = paymentService
                .handleSuccessfulPayment(paymentId);

        assertEquals(expectedResponseDto, actualResponseDto);
        verify(paymentProcessor, times(1)).processSuccessfulPayment(paymentId);
    }

    @DisplayName("Handle Canceled payment")
    @Test
    public void handleCanceledPayment_ShouldInvokePaymentProcessor() {
        String paymentId = "Test paymentID";

        CancelledPaymentResponseDto expectedResponseDto = new CancelledPaymentResponseDto();
        expectedResponseDto.setPaymentId(paymentId);
        expectedResponseDto.setStatus("Canceled");

        when(paymentProcessor.processCancelledPayment(paymentId)).thenReturn(expectedResponseDto);

        CancelledPaymentResponseDto actualResponseDto =
                paymentService.handleCancelledPayment(paymentId);

        assertEquals(expectedResponseDto, actualResponseDto);
        verify(paymentProcessor, times(1)).processCancelledPayment(paymentId);

    }

    private Payment createPayment(
            Long id,
            Payment.Status status,
            Long bookingId,
            URL url,
            BigDecimal amountToPay
    ) {
        Payment payment = new Payment();
        payment.setId(id);
        payment.setStatus(status);
        payment.setBookingId(bookingId);
        payment.setSessionUrl(url);
        payment.setAmountToPay(amountToPay);

        return payment;
    }

    private PaymentDto createPaymentDto(Payment payment) {
        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setId(payment.getId());
        paymentDto.setStatus(String.valueOf(payment.getStatus()));
        paymentDto.setBookingId(payment.getBookingId());
        paymentDto.setSessionId(String.valueOf(payment.getSessionUrl()));
        paymentDto.setAmountToPay(payment.getAmountToPay());

        return paymentDto;
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
            List<String> amenities,
            Accommodation.Type type,
            Address address,
            Integer availability,
            BigDecimal dailyRate,
            String size
    ) {
        Accommodation accommodation = new Accommodation();
        accommodation.setId(id);
        accommodation.setAmenities(amenities);
        accommodation.setType(type);
        accommodation.setLocation(address);
        accommodation.setAvailability(availability);
        accommodation.setDailyRate(dailyRate);
        accommodation.setSize(size);

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
