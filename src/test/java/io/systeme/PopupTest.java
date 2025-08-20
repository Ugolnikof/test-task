package io.systeme;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

@Epic("Тестирование всплывающих окон")
@Feature("Проверка функциональности popup")
public class PopupTest {
    public static final By POPAP_GREEN_BUTTON = By.xpath("//div[contains(text(), 'I want to receive my copy')]");
    public static final By IFRAME = By.xpath("//iframe[contains(@id, 'systemeio-iframe-')]");
    public static final By CLOSE_POPAP_LOCATOR = By.xpath("//button[@data-testid='popup-close-icon']");
    public static final String TEXT_PLAIN = "text/plain";

    private WebDriver driver;
    private WebDriverWait wait;
    private final String URL = "https://systeme.io/blog/cost-of-online-course";

    @BeforeEach
    @Step("Настройка драйвера и открытие браузера")
    void setup() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Для запуска в Docker
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(50));
    }

    @Test
    @Story("Проверка всплывающего окна")
    @Description("Тест проверяет наличие кнопки в popup и функциональность закрытия")
    @Severity(SeverityLevel.CRITICAL)
    void testPopupFunctionality() {
        // 1. Открываем страницу
        driver.get(URL);
        Allure.addAttachment("Страница открыта", TEXT_PLAIN, URL);

        // 2. Ждём появления popup и переключаемся на iframe
        WebElement iframe = wait.until(ExpectedConditions.visibilityOfElementLocated(IFRAME));
        driver.switchTo().frame(iframe);

        // 3. Проверяем кнопку в popup
        checkPopupButton();

        // 4. Закрываем popup и проверяем
        closeAndVerifyPopup();
        driver.switchTo().defaultContent();
    }

    @Step("Проверка кнопки 'I want to receive my copy'")
    private void checkPopupButton() {
        try {
            WebElement receiveButton = wait.until(ExpectedConditions.visibilityOfElementLocated(POPAP_GREEN_BUTTON));
            Allure.addAttachment("Кнопка найдена", "Кнопка: " + receiveButton.getText());
        } catch (TimeoutException e) {
            Allure.addAttachment("Ошибка", TEXT_PLAIN, "Всплывающее окно не появилось");
            throw new AssertionError("Всплывающее окно не появилось или кнопка не найдена");
        }
    }

    @Step("Закрытие popup и проверка")
    private void closeAndVerifyPopup() {
        try {
            WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(CLOSE_POPAP_LOCATOR));
            closeButton.click();
            Allure.addAttachment("Popup закрыт", TEXT_PLAIN, "Кнопка закрытия сработала");

            wait.until(ExpectedConditions.invisibilityOfElementLocated(POPAP_GREEN_BUTTON));
        } catch (TimeoutException e) {
            Allure.addAttachment("Ошибка закрытия", TEXT_PLAIN, "Popup не закрылся");
            throw new AssertionError("Всплывающее окно не закрылось");
        }
    }

    @AfterEach
    @Step("Закрытие браузера")
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}