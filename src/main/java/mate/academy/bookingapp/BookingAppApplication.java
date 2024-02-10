package mate.academy.bookingapp;

import mate.academy.bookingapp.telegram.BookingBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@EnableScheduling
@SpringBootApplication
public class BookingAppApplication {
    private static final Logger logger = LoggerFactory.getLogger(BookingAppApplication.class);

    public static void main(String[] args) {

        SpringApplication.run(BookingAppApplication.class, args);
        logger.info("Telegram Bot has been started.");
    }

    @Bean
    public CommandLineRunner schedulingRunner(ApplicationContext context) {
        return args -> {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            try {
                telegramBotsApi.registerBot(context.getBean(BookingBot.class));
                logger.info("Telegram Bot has been registered.");
            } catch (TelegramApiException e) {
                logger.error("Failed to register Telegram Bot", e);
            }
        };
    }
}
