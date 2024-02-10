package mate.academy.bookingapp.service.booking;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingapp.dto.booking.BookingDto;
import mate.academy.bookingapp.dto.booking.BookingRequestDto;
import mate.academy.bookingapp.dto.booking.BookingUpdateDto;
import mate.academy.bookingapp.exception.AccommodationNotAvailableException;
import mate.academy.bookingapp.exception.EntityNotFoundException;
import mate.academy.bookingapp.mapper.booking.BookingMapper;
import mate.academy.bookingapp.model.Accommodation;
import mate.academy.bookingapp.model.Booking;
import mate.academy.bookingapp.model.User;
import mate.academy.bookingapp.repository.AccommodationRepository;
import mate.academy.bookingapp.repository.BookingRepository;
import mate.academy.bookingapp.service.telegram.TelegramNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {
    private final Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final AccommodationRepository accommodationRepository;
    private final TelegramNotificationService telegramNotificationService;

    @Override
    public BookingDto createBooking(BookingRequestDto requestDto) {
        Accommodation accommodation =
                getAccommodationByAccommodationId(requestDto.getAccommodationId());
        LocalDate checkInDate = requestDto.getCheckInDate();
        LocalDate checkOutDate = requestDto.getCheckOutDate();

        if (isAccommodationAvailable(accommodation, checkInDate, checkOutDate)) {
            Booking booking = bookingMapper.toModel(requestDto);
            Booking savedBooking = bookingRepository.save(booking);
            BookingDto bookingDtoForNotification = bookingMapper.toDto(savedBooking);

            telegramNotificationService.notifyNewBookingCreated(bookingDtoForNotification);
            logger.info("Booking created successfully: {}", bookingDtoForNotification);

            return bookingDtoForNotification;
        } else {
            throw new AccommodationNotAvailableException(
                    "Accommodation isn't available for the specified dates.");
        }
    }

    @Override
    public List<BookingDto> getBookingsByUserIdAndStatus(
            Long userId,
            Booking.Status status,
            Pageable pageable
    ) {
        return bookingRepository.findBookingsByUserIdAndStatus(userId, status, pageable)
                .stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    public BookingDto updateBooking(Long id, BookingUpdateDto updateDto) {
        Booking bookingFromDb = getBookingByBookingId(id);
        bookingFromDb.setCheckInDate(updateDto.getCheckInDate());
        bookingFromDb.setCheckOutDate(updateDto.getCheckOutDate());
        bookingFromDb.setStatus(Booking.Status.valueOf(updateDto.getStatus()));
        return bookingMapper.toDto(bookingRepository.save(bookingFromDb));
    }

    @Override
    public List<BookingDto> getUserBookings(Authentication authentication, Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        return bookingRepository.findAllByUserId(user.getId(), pageable)
                .stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    public BookingDto getBookingById(Long id) {
        Booking bookingFromDb = getBookingByBookingId(id);
        return bookingMapper.toDto(bookingFromDb);
    }

    @Override
    public void deleteBookingById(Long id) {
        telegramNotificationService.notifyBookingCanceled(getBookingById(id));
        bookingRepository.deleteById(id);
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Override
    public void checkAndProcessExpiredBookings() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        List<Booking> expiredBookings = bookingRepository.findExpiredBookings(tomorrow);
        for (Booking expiredBooking : expiredBookings) {
            processExpiredBooking(expiredBooking);
        }
    }

    private boolean isAccommodationAvailable(
            Accommodation accommodation,
            LocalDate checkInDate,
            LocalDate checkOutDate
    ) {
        return bookingRepository.findOverlappingBookings(
                accommodation,
                checkInDate,
                checkOutDate
        ).isEmpty();
    }

    private void processExpiredBooking(Booking expiredBooking) {
        expiredBooking.setStatus(Booking.Status.EXPIRED);
        bookingRepository.save(expiredBooking);

        telegramNotificationService.notifyExpiredBooking(String.valueOf(expiredBooking.getId()));
    }

    private Accommodation getAccommodationByAccommodationId(Long id) {
        return accommodationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(
                        "Can't fina an accommodation by accommodationID: " + id));
    }

    private Booking getBookingByBookingId(Long id) {
        return bookingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find a booking by bookingID: " + id));
    }
}
