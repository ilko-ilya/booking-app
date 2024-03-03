package mate.academy.bookingapp;

import mate.academy.bookingapp.config.CustomMySqlContainer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;

@SpringBootTest
class BookingAppApplicationTests {
    @Test
    void contextLoad() {

    }
    @Bean
    CustomMySqlContainer customMySqlContainer() {
        return CustomMySqlContainer.getInstance();
    }

}
