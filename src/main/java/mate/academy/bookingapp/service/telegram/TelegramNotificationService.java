package mate.academy.bookingapp.service.telegram;

import mate.academy.bookingapp.dto.accommodation.AccommodationDto;
import mate.academy.bookingapp.dto.booking.BookingDto;

public interface TelegramNotificationService {
    void notifyNewBookingCreated(BookingDto bookingDto);

    void notifyBookingCanceled(BookingDto bookingDto);

    void notifyNewAccommodationCreated(AccommodationDto accommodationDto);

    void notifyAccommodationReleased(AccommodationDto accommodationDto);

    void notifySuccessfulPayment(String paymentId);

    void notifyExpiredBooking(String bookingId);
}

