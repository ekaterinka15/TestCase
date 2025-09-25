package tests;

import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import pages.RegistrationPage;

import java.time.Duration;


public class DuplicateEmailTest {
    private WebDriver driver;
    private RegistrationPage registrationPage;

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.get("http://217.74.37.176/?route=account/register&language=ru-ru");

        registrationPage = new RegistrationPage(driver);
        System.out.println("🔄 Открыта страница регистрации");
    }

    @Test
    void testDuplicateEmailShowsError() {

        registrationPage.enterFirstName("Иван");
        Assertions.assertEquals("Иван", registrationPage.getFirstNameInput().getAttribute("value"));


        registrationPage.enterLastName("Иванов");
        Assertions.assertEquals("Иванов", registrationPage.getLastNameInput().getAttribute("value"));

        System.out.println(" Ввод уже зарегистрированного email: ivanov.i@yap.ru");
        registrationPage.enterEmail("ivanov.i@yap.ru");
        Assertions.assertEquals("ivanov.i@yap.ru", registrationPage.getEmailInput().getAttribute("value"));


        registrationPage.enterPassword("1234");
        Assertions.assertEquals("1234", registrationPage.getPasswordInput().getAttribute("value"));

        System.out.println(" Включение подписки");
        registrationPage.setSubscribe(false);
        Assertions.assertFalse(registrationPage.getSubscribeToggle().isSelected(), " Тумблер подписки не включен");

        System.out.println(" Включение политики конфиденциальности");
        registrationPage.setPrivacyPolicy(true);
        Assertions.assertTrue(registrationPage.getPrivacyToggle().isSelected(), "Тумблер политики не включен");

        registrationPage.enterEmail("ivanov.i@yap.ru");
        registrationPage.clickSubmit();

        String errorText = registrationPage.getAlertText();
        System.out.println("📛 Ошибка при вводе email: " + errorText);

        Assertions.assertTrue(errorText.contains("зарегистрирован") || errorText.contains("уже существует"),
                " Ожидаемая ошибка о повторном email не найдена");


        if (registrationPage.isRegistrationSuccessMessageVisible()) {
            System.out.println(" Регистрация прошла успешно");
        } else {
            System.out.println("Регистрация не удалась");
        }

        Assertions.assertTrue(registrationPage.alertContains("E-Mail уже зарегистрирован")
                        || registrationPage.alertContains("уже существует"),
                " Ошибка о повторном email не появилась");

        Assertions.assertTrue(driver.getCurrentUrl().contains("register"),
                " Регистрация прошла, хотя email уже зарегистрирован");
    }

    @AfterEach
    void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }
}
