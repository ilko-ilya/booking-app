package mate.academy.bookingapp.telegram;

public interface BookingBotService {
    void sendTextMessage(Long chatId, String text);
}
