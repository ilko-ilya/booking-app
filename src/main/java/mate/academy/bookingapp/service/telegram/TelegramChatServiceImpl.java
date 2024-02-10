package mate.academy.bookingapp.service.telegram;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingapp.repository.TelegramChatRepository;
import mate.academy.bookingapp.telegram.model.TelegramChat;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TelegramChatServiceImpl implements TelegramChatService {
    private final TelegramChatRepository telegramChatRepository;

    @Override
    public void saveChatId(Long chatId) {
        TelegramChat telegramChat = new TelegramChat();
        telegramChat.setChatId(chatId);
        telegramChatRepository.save(telegramChat);
    }

    @Override
    public List<TelegramChat> getAllChats() {
        return telegramChatRepository.findAll();
    }

    @Override
    public Long getTelegramChatIdByUserId(Long userId) {
        return telegramChatRepository.findTelegramChatIdByUserId(userId);
    }
}
