package tests;

import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import pages.RegistrationPage;

import java.time.Duration;


public class PrivacyPolicyUncheckedTest {
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
    void testPrivacyPolicyUncheckedShowsError() {

        registrationPage.enterFirstName("Иван");
        Assertions.assertEquals("Иван", registrationPage.getFirstNameInput().getAttribute("value"));


        registrationPage.enterLastName("Иванов");
        Assertions.assertEquals("Иванов", registrationPage.getLastNameInput().getAttribute("value"));


        registrationPage.enterEmail("iva@yap.ru");
        Assertions.assertEquals("iva@yap.ru", registrationPage.getEmailInput().getAttribute("value"));


        registrationPage.enterPassword("1234");
        Assertions.assertEquals("1234", registrationPage.getPasswordInput().getAttribute("value"));


        System.out.println(" Отключение подписки");
        registrationPage.setSubscribe(false);
        Assertions.assertFalse(registrationPage.getSubscribeToggle().isSelected(), " Тумблер подписки не выключен");

        System.out.println(" Выключение политики конфиденциальности");
        registrationPage.setPrivacyPolicy(false);
        Assertions.assertFalse(registrationPage.getPrivacyToggle().isSelected(), "Тумблер политики не выключен");


        registrationPage.clickSubmit();
        if (registrationPage.isRegistrationSuccessMessageVisible()) {
            System.out.println(" Регистрация прошла успешно");
        } else {
            System.out.println("Регистрация не удалась");
        }




        Assertions.assertTrue(registrationPage.alertContains("Privacy Policy"),
                "Ошибка не появилась");
        Assertions.assertTrue(driver.getCurrentUrl().contains("register"),
                "Регистрация прошла успешно");

    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}

