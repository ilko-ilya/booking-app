package mate.academy.bookingapp.telegram;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import mate.academy.bookingapp.config.TelegramBotConfig;
import mate.academy.bookingapp.dto.booking.BookingDto;
import mate.academy.bookingapp.model.User;
import mate.academy.bookingapp.service.booking.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class BookingBot extends TelegramLongPollingBot implements BookingBotService {

    private static final String GREETING_MESSAGE =
            "Hi! I am your booking bot. How can I assist you?";
    private static final String HELP_MESSAGE =
            "Here are some available commands:\n"
                    + "/start - Start the bot\n"
                    + "/help - Display available commands\n"
                    + "/book - Book an accommodation\n"
                    + "/mybookings - Get all bookings";

    private static final String COMMAND_START = "/start";
    private static final String COMMAND_HELP = "/help";
    private static final String COMMAND_BOOK = "/book";
    private static final String COMMAND_MY_BOOKINGS = "/mybookings";
    private static final String COMMAND_MY_DATA = "/mydata";
    private static final String COMMAND_DELETE_MY_DATA = "/deletemydata";
    private static final String ERROR_MESSAGE =
            "I'm sorry, I don't understand that command. Type /help for available commands.";
    private static final String UNABLE_TO_RETRIEVE_INFO_MESSAGE =
            "Unable to retrieve user information.";
    private static final String BOOKING_SUCCESSFULLY_REQUEST =
            "Booking request handled successfully.";

    private final TelegramBotConfig botConfig;
    private final BookingService bookingService;

    private final Logger logger = LoggerFactory.getLogger(BookingBot.class);

    public BookingBot(TelegramBotConfig botConfig, @Lazy BookingService bookingService) {
        this.botConfig = botConfig;
        this.bookingService = bookingService;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand(COMMAND_START, "get a welcome message"));
        listOfCommands.add(new BotCommand(COMMAND_MY_DATA, "get your data stored"));
        listOfCommands.add(new BotCommand(COMMAND_HELP, "how to use this bot"));
        listOfCommands.add(new BotCommand(COMMAND_DELETE_MY_DATA, "delete all my data"));
        listOfCommands.add(new BotCommand(COMMAND_MY_BOOKINGS, "set your preferences"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            logger.error("Error settings bot's command list: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        logger.info("Update received: {}", update);
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            logger.info("Received command '{}' from chat ID: {}", messageText, chatId);

            switch (messageText.toLowerCase()) {
                case COMMAND_START:
                    sendTextMessage(chatId, GREETING_MESSAGE);
                    logger.info("Handling /start command");
                    break;
                case COMMAND_HELP:
                    sendTextMessage(chatId, HELP_MESSAGE);
                    logger.info("Handling /help command");
                    break;
                case COMMAND_BOOK:
                    sendTextMessage(update.getMessage().getChatId(), BOOKING_SUCCESSFULLY_REQUEST);
                    logger.info("Handling /book command");
                    break;
                case COMMAND_MY_BOOKINGS:
                    handleMyBookingsCommand(chatId);
                    logger.info("Handling /mybookings command");
                    break;
                case COMMAND_MY_DATA:
                    handleMyDataCommand(chatId);
                    logger.info("Handling /mydata command");
                    break;
                case COMMAND_DELETE_MY_DATA:
                    handleDeleteMyDataCommand(chatId);
                    logger.info("Handling /deletemydata command");
                    break;
                default:
                    sendTextMessage(chatId, ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void sendTextMessage(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(text);

        try {
            execute(sendMessage);
            logger.info("Message sent successfully to chat ID: {}", chatId);
        } catch (TelegramApiException e) {
            logger.error("Failed to send message", e);
            throw new RuntimeException("Failed to send message " + e);
        }
    }

    private void handleMyBookingsCommand(Long chatId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof User) {
                List<BookingDto> userBookings =
                        bookingService.getUserBookings(authentication, Pageable.ofSize(10));
                sendBookingsInfo(chatId, userBookings);
            } else {
                sendTextMessage(chatId, UNABLE_TO_RETRIEVE_INFO_MESSAGE);
            }
        } catch (Exception e) {
            handleError(chatId, e.getMessage());
        }
    }

    private void sendBookingsInfo(Long chatId, List<BookingDto> bookings) {
        String message = "Your bookings:\n"
                + bookings.stream()
                .map(BookingDto::toString)
                .collect(Collectors.joining("\n"));
        sendTextMessage(chatId, message);
    }

    private void handleError(Long chatId, String errorMessage) {
        sendTextMessage(chatId, "Error: " + errorMessage);
    }

    private void handleMyDataCommand(Long chatId) {
        sendTextMessage(chatId, "My Data: Your data will be displayed here.");
    }

    private void handleDeleteMyDataCommand(Long chatId) {
        sendTextMessage(chatId, "Delete My Data: Your data will be deleted here.");
    }
}




