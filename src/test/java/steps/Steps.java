package steps;

import io.cucumber.java.After;

import io.cucumber.java.ru.*;
import io.qameta.allure.Step;

import org.junit.jupiter.api.Assertions;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import pages.RegistrationPage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Steps {

    private WebDriver driver;
    private RegistrationPage registrationPage;



    @Допустим("Пользователь открыл форму регистрации")
    @Step("Открываем форму регистрации")
    public void открытьФорму() {

        Properties properties = new Properties();
        try {
            InputStream input = new FileInputStream("src/main/resources/application.properties");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        String driverType = properties.getProperty("type.driver");
        String browser = properties.getProperty("type.browser");
        String selenoidUrl = properties.getProperty("selenoid.url");
        if ("remote".equalsIgnoreCase(driverType)) {

            DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
            Map<String, Object> selenoidOptions = new HashMap<>();
            desiredCapabilities.setBrowserName(browser);
            desiredCapabilities.setVersion("109.0");
            desiredCapabilities.setCapability("enableVNC",true);
            desiredCapabilities.setCapability("enableVideo",false);
            desiredCapabilities.setCapability("selenoid:options", selenoidOptions);

            try {
                driver = new RemoteWebDriver(URI.create("selenoidUrl").toURL(), desiredCapabilities);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }

        } else {

            driver = new ChromeDriver();
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            driver.get("http://217.74.37.176/?route=account/register&language=ru-ru");
            registrationPage = new RegistrationPage(driver);
        }
    }


    @Когда("он вводит имя {string} и фамилию {string}")
    @Step("Вводим имя: {0}, фамилию: {1}")
    public void вводИмениФамилии(String firstName, String lastName) {
        registrationPage.enterFirstName(firstName);
        registrationPage.enterLastName(lastName);
        Assertions.assertEquals(firstName, registrationPage.getFirstNameInput().getAttribute("value"));
        Assertions.assertEquals(lastName, registrationPage.getLastNameInput().getAttribute("value"));
    }

    @И("он вводит уже зарегистрированный email {string}")
    @Step("Вводим зарегистрированный email: {0}")
    public void вводEmail(String email) {
        registrationPage.enterEmail(email);
        Assertions.assertEquals(email, registrationPage.getEmailInput().getAttribute("value"));
    }
    @И("он вводит уникальный email")
    @Step("Введён email: {string}")
    public void вводУникальногоEmail() {
        String email = "user" + System.currentTimeMillis() + "@test.ru";
        registrationPage.enterEmail(email);
    }
    @И("он вводит пароль {string}")
    @Step("Вводим пароль: {0}")
    public void вводПароля(String password) {
        registrationPage.enterPassword(password);
        Assertions.assertEquals(password, registrationPage.getPasswordInput().getAttribute("value"));
    }

    @И("он отключает подписку на новости")
    @Step("Отключаем подписку на новости")
    public void отключитьПодписку() {
        registrationPage.setSubscribe(false);
        Assertions.assertFalse(registrationPage.getSubscribeToggle().isSelected());
    }

    @И("он включает политику конфиденциальности")
    @Step("Включаем политику конфиденциальности")
    public void включитьПолитику() {
        registrationPage.setPrivacyPolicy(true);
        Assertions.assertTrue(registrationPage.getPrivacyToggle().isSelected());
    }
    @И("он не соглашается с политикой конфиденциальности")
    @Step
    public void отключитьПолитику() {
        registrationPage.setPrivacyPolicy(false);
        Assertions.assertFalse(registrationPage.getPrivacyToggle().isSelected());
    }

    @И("он нажимает кнопку \"Продолжить\"")
    @Step("Нажимаем кнопку 'Продолжить'")
    public void нажатьПродолжить() {
        registrationPage.clickSubmit();
    }

    @Тогда("отображается ошибка о повторном email")
    @Step("Проверяем сообщение об ошибке")
    public void проверитьОшибку() {
        String errorText = registrationPage.getAlertText();
        Assertions.assertTrue(errorText.contains("зарегистрирован") || errorText.contains("уже существует"));
        Assertions.assertTrue(registrationPage.alertContains("E-Mail уже зарегистрирован")
                || registrationPage.alertContains("уже существует"));
    }
    @Тогда("не появляется сообщение об ошибке {string}")
    public void ошибкаНеПоявляется(String текст) {
        Assertions.assertFalse(registrationPage.alertContains(текст),
                "Нужно ознакомиться с Privacy Policy и вкл его для успешной регистрации");
    }


    @Тогда("появляется сообщение об ошибке {string}")
    @Step
    public void проверитьСообщение(String текст) {
        Assertions.assertTrue(registrationPage.alertContains(текст));
    }

    @И("пользователь остаётся на странице регистрации")
    @Step("Проверяем, что остались на странице регистрации")
    public void осталсяНаСтранице() {
        Assertions.assertTrue(driver.getCurrentUrl().contains("register"));

    }
    @Тогда("появляется ошибка о длине пароля")
    @Step("Проверка ошибки на длину пароля")
    public void ошибкаДлиныПароля() {
        if (!registrationPage.isPasswordErrorDisplayed()) {
            Assertions.fail("Баг: ошибка на длину пароля не отобразилась при вводе 21 символа");
        } else {
            Assertions.assertTrue(registrationPage.getPasswordErrorText().contains("символов"),
                    "Ошибка: пароль имеет ограничение от 4 до 20 символов");
        }
    }
    @Тогда("не появляется ошибка о длине пароля")
    @Step("Проверка отсутствия ошибки на длину пароля")
    public void ошибкаДлиныПароляНеПоявилась() {
        Assertions.assertFalse(registrationPage.isPasswordErrorDisplayed(),
                "Ошибки не выявлено");
    }


    @Тогда("Ошибка не появляется")
    @Step("Проверяем, что ошибка не появилась")
    public void ошибкаНеПоявилась() {
        Assertions.assertFalse(registrationPage.alertContains("зарегистрирован")
                        || registrationPage.alertContains("уже существует"),
                " Ошибка о повторном email появилась ");
    }

    @И("регистрация проходит успешно")
    @Step("Проверяем, что регистрация прошла успешно")
    public void регистрацияПрошла() {
        boolean successLink = registrationPage.isSuccessLinkVisible();
        boolean redirected = driver.getCurrentUrl().contains("route=account/success");

        if (!(successLink || redirected)) {
            Assertions.fail("Регистрация не прошла");
        }

        Assertions.assertTrue(successLink || redirected, "Регистрация прошла успешно");
    }


    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();

        }
    }
}