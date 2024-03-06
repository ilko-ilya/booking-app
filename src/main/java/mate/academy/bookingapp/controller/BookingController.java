package mate.academy.bookingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingapp.dto.booking.BookingDto;
import mate.academy.bookingapp.dto.booking.BookingRequestDto;
import mate.academy.bookingapp.dto.booking.BookingUpdateDto;
import mate.academy.bookingapp.model.Booking;
import mate.academy.bookingapp.service.booking.BookingService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Booking management", description = "Endpoints for managing user's bookings")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;

    @Operation(summary = "Create a booking", description = "Create a new booking")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public BookingDto create(@RequestBody @Valid BookingRequestDto requestDto) {
        return bookingService.createBooking(requestDto);
    }

    @Operation(summary = "Get bookings based on user ID and status",
            description = "Retrieve bookings based on user ID and status")
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping
    List<BookingDto> getBookingsByUserIdAndStatus(
            @RequestParam Long userId,
            @RequestParam Booking.Status status,
            Pageable pageable) {
        return bookingService.getBookingsByUserIdAndStatus(userId, status, pageable);
    }

    @Operation(summary = "Update a booking", description = "Update the details of a booking")
    @PreAuthorize("hasRole('CUSTOMER')")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    public BookingDto updateBooking(
            @PathVariable Long id,
            @RequestBody @Valid BookingUpdateDto updateDto
    ) {
        return bookingService.updateBooking(id, updateDto);
    }

    @Operation(summary = "Get user's booking", description = "Get list of user's bookings")
    @GetMapping("/my")
    public List<BookingDto> getUserBookings(Authentication authentication, Pageable pageable) {
        return bookingService.getUserBookings(authentication, pageable);
    }

    @Operation(summary = "Get a booking by id",
            description = "Get information about a specific booking by id")
    @GetMapping("/{id}")
    public BookingDto getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id);
    }

    @Operation(summary = "Delete the booking by id",
            description = "Delete specific booking by id")
    @PreAuthorize("hasRole('MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        bookingService.deleteBookingById(id);
    }
}
