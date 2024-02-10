package mate.academy.bookingapp.repository;

import mate.academy.bookingapp.telegram.model.TelegramChat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TelegramChatRepository extends JpaRepository<TelegramChat, Long> {
    Long findTelegramChatIdByUserId(Long userId);
}
