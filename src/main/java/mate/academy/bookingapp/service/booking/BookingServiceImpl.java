package mate.academy.bookingapp.service.booking;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingapp.dto.booking.BookingDto;
import mate.academy.bookingapp.dto.booking.BookingRequestDto;
import mate.academy.bookingapp.dto.booking.BookingUpdateDto;
import mate.academy.bookingapp.exception.EntityNotFoundException;
import mate.academy.bookingapp.mapper.booking.BookingMapper;
import mate.academy.bookingapp.model.Booking;
import mate.academy.bookingapp.model.User;
import mate.academy.bookingapp.repository.BookingRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto createBooking(BookingRequestDto requestDto) {
        Booking booking = bookingMapper.toModel(requestDto);
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public List<BookingDto> getBookingsByUserIdAndStatus(Long userId,
                                                         Booking.Status status,
                                                         Pageable pageable) {
        return bookingRepository.findBookingsByUserIdAndStatus(userId, status, pageable)
                .stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    public BookingDto updateBooking(Long id, BookingUpdateDto updateDto) {
        Booking bookingFromDb = bookingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find a booking by id: " + id));
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
        Booking booking = bookingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find a booking by id: " + id));
        return bookingMapper.toDto(booking);
    }

    @Override
    public void deleteBookingById(Long id) {
        bookingRepository.deleteById(id);
    }
}
