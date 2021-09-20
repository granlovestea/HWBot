package ru.granby;

import okhttp3.OkHttpClient;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.granby.bot.Bot;
import ru.granby.model.UserStorage;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);
    private static final String CHROMEDRIVER_PATH = System.getenv("CHROMEDRIVER_PATH");
    private static final String SOLVER_EXTENSION_PATH = System.getenv("SOLVER_EXTENSION_PATH");
    private static Bot bot;
    private static UserStorage userStorage;
    private static OkHttpClient webClient;
    private static WebDriver webDriver;

    public static void main(String[] args) {
        userStorage = new UserStorage();
        webClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        startWebDriver();
        startBot();
    }

    public static UserStorage getUserStorage() {
        return userStorage;
    }

    public static OkHttpClient getWebClient() {
        return webClient;
    }

    public static WebDriver getWebDriver() {
        return webDriver;
    }

    private static void startBot() {
        bot = new Bot();
        bot.start();
    }

    private static void startWebDriver() {
        System.setProperty("webdriver.chrome.driver", CHROMEDRIVER_PATH);

        ChromeOptions options = new ChromeOptions();
//        options.addExtensions(new File(SOLVER_EXTENSION_PATH));

        webDriver = new ChromeDriver(options);
    }
}