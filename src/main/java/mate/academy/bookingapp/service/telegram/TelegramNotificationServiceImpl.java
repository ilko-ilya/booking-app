package mate.academy.bookingapp.service.telegram;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingapp.dto.accommodation.AccommodationDto;
import mate.academy.bookingapp.dto.booking.BookingDto;
import mate.academy.bookingapp.telegram.BookingBot;
import mate.academy.bookingapp.telegram.model.TelegramChat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TelegramNotificationServiceImpl implements TelegramNotificationService {
    private final TelegramChatService telegramChatService;
    private final BookingBot bookingBot;
    private final Logger logger =
            LoggerFactory.getLogger(TelegramNotificationServiceImpl.class);

    @Override
    public void notifyNewBookingCreated(BookingDto bookingDto) {
        for (TelegramChat telegramChat: telegramChatService.getAllChats()) {
            sendTextMessage(telegramChat.getChatId(),
                    "New booking created:\n" + bookingDto);
            logger.info("Notification about new booking created sent.");
        }
    }

    @Override
    public void notifyBookingCanceled(BookingDto bookingDto) {
        List<TelegramChat> chatList = telegramChatService.getAllChats();
        for (TelegramChat telegramChat: chatList) {
            sendTextMessage(telegramChat.getChatId(),
                    "Booking canceled:\n" + bookingDto.toString());
            logger.info("Notification about booking canceled sent.");
        }
    }

    @Override
    public void notifyNewAccommodationCreated(AccommodationDto accommodationDto) {
        for (TelegramChat telegramChat: telegramChatService.getAllChats()) {
            sendTextMessage(telegramChat.getChatId(), "New accommodation created:\n"
                    + accommodationDto.toString());
            logger.info("Notification about new accommodation created sent.");
        }
    }

    @Override
    public void notifyAccommodationReleased(AccommodationDto accommodationDto) {
        for (TelegramChat telegramChat: telegramChatService.getAllChats()) {
            sendTextMessage(telegramChat.getChatId(), "Accommodation released:\n"
                    + accommodationDto.toString());
            logger.info("Notification about accommodation released sent.");
        }
    }

    @Override
    public void notifySuccessfulPayment(String paymentId) {
        for (TelegramChat telegramChat: telegramChatService.getAllChats()) {
            sendTextMessage(telegramChat.getChatId(),
                    "Successful payment:\nPaymentID: " + paymentId);
            logger.info("Notification about successful payment sent.");
        }
    }

    @Override
    public void notifyExpiredBooking(String bookingId) {
        for (TelegramChat telegramChat: telegramChatService.getAllChats()) {
            sendTextMessage(telegramChat.getChatId(),
                    "Booking is expired:\nBookingID: " + bookingId);
            logger.info("Notification about expired booking sent.");
        }
    }

    private void sendTextMessage(Long chatId, String text) {
        bookingBot.sendTextMessage(chatId, text);
    }
}
