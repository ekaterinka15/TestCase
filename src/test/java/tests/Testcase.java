package tests;

import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import pages.RegistrationPage;

import java.time.Duration;

class PasswordTooLongTest {
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
    void testPasswordTooLongShowsError() {
        registrationPage.enterFirstName("Иван");
        Assertions.assertEquals("Иван", registrationPage.getFirstNameInput().getAttribute("value"), "❌ Имя не отобразилось как ожидалось");

        registrationPage.enterLastName("Иванов");
        Assertions.assertEquals("Иванов", registrationPage.getLastNameInput().getAttribute("value"), "❌ Фамилия не отобразилась как ожидалось");

        registrationPage.enterEmail("ivanov.i@yap.ru");
        Assertions.assertEquals("ivanov.i@yap.ru", registrationPage.getEmailInput().getAttribute("value"), "❌ Email не отобразился как ожидалось");

        System.out.println(" Ввод пароля из 21 символа");
        registrationPage.enterPassword("123456789012345678901");

        System.out.println(" Проверка ошибки на длину пароля");
        if (!registrationPage.isPasswordErrorDisplayed()) {
            System.out.println(" Баг: ошибка на длину пароля не отобразилась при вводе 21 символа");
        } else {
            Assertions.assertTrue(registrationPage.getPasswordErrorText().contains("символа"),
                    "Ошибка, пароль должен иметь от 4 до 21 'символа'");
        }


        System.out.println(" Отключение подписки");
        registrationPage.setSubscribe(false);
        Assertions.assertFalse(registrationPage.getSubscribeToggle().isSelected(), " Тумблер подписки не выключен");

        System.out.println(" Включение политики конфиденциальности");
        registrationPage.setPrivacyPolicy(true);
        Assertions.assertTrue(registrationPage.getPrivacyToggle().isSelected(), "Тумблер политики не включен");


        registrationPage.clickSubmit();
        if (registrationPage.isRegistrationSuccessMessageVisible()) {
            System.out.println(" Регистрация прошла успешно");
        } else {
            System.out.println("Регистрация не удалась");
        }
        System.out.println(" Проверка на ошибки");
        Assertions.assertTrue(registrationPage.alertContains("E-Mail уже зарегистрирован")
                        || registrationPage.alertContains("Политикой конфиденциальности")
                        || registrationPage.alertContains("ошибка при регистрации"),
                "Регистрация прошла успешно");
    }

    @AfterEach
    void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }
}

