package mate.academy.bookingapp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.MySQLContainer;

public class CustomMySqlContainer extends MySQLContainer<CustomMySqlContainer> {
    private static final Logger logger = LoggerFactory.getLogger(CustomMySqlContainer.class);

    private static final String DB_IMAGE = "mysql:8";

    private static CustomMySqlContainer mySqlContainer;

    private CustomMySqlContainer() {
        super(DB_IMAGE);
    }

    public static synchronized CustomMySqlContainer getInstance() {
        if (mySqlContainer == null) {
            mySqlContainer = new CustomMySqlContainer();
        }
        return mySqlContainer;
    }

    @Override
    public void start() {
        super.start();
        logger.info("MySQL Container Logs: \n{}", mySqlContainer.getLogs());
        System.setProperty("TEST_DB_URL", mySqlContainer.getJdbcUrl());
        logger.info("Jdbc url: \n{}", mySqlContainer.getJdbcUrl());
        System.setProperty("TEST_DB_USERNAME", mySqlContainer.getUsername());
        System.setProperty("TEST_DB_PASSWORD", mySqlContainer.getPassword());
    }

    @Override
    public void stop() {
        super.stop();
    }
}
