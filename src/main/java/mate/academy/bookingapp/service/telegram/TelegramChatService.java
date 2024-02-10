package mate.academy.bookingapp.service.telegram;

import java.util.List;
import mate.academy.bookingapp.telegram.model.TelegramChat;

public interface TelegramChatService {
    void saveChatId(Long chatId);

    List<TelegramChat> getAllChats();

    Long getTelegramChatIdByUserId(Long userId);
}
