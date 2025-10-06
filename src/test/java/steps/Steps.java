package steps;


import io.cucumber.java.After;

import io.cucumber.java.ru.*;
import io.qameta.allure.Step;

import org.junit.jupiter.api.Assertions;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.http.HttpClient;
import pages.RegistrationPage;

import java.io.FileInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Steps {

    private static WebDriver driver;
    private static RegistrationPage registrationPage;

    public static WebDriver getDriver() {
        if (driver == null) {
            открытьФорму();
        }
        return driver;
    }

    @Допустим("Пользователь открыл форму регистрации")
    @Step("Открываем форму регистрации")
    public static void открытьФорму() {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream("src/main/resources/application.properties")) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить application.properties", e);
        }

        String jenkinsDriver = System.getProperty("type.driver");
        String driverType = (jenkinsDriver != null && !jenkinsDriver.isEmpty())
                ? jenkinsDriver
                : properties.getProperty("type.driver");
        String selenoidUrl = properties.getProperty("selenoid.url");

        String browser = System.getProperty("browser", properties.getProperty("browser", "firefox")).toLowerCase();

        if ("remote".equalsIgnoreCase(driverType)) {
            MutableCapabilities options;

            if ("firefox".equals(browser)) {
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.setBrowserVersion("109.0");
                firefoxOptions.addArguments("--headless");

                Map<String, Object> selenoidOptions = new HashMap<>();
                selenoidOptions.put("enableVNC", true);
                selenoidOptions.put("enableVideo", false);
                firefoxOptions.setCapability("selenoid:options", selenoidOptions);

                options = firefoxOptions;
            } else {
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.setBrowserVersion("109.0");
                chromeOptions.addArguments("--headless=new");
                chromeOptions.setCapability("selenoid:options", Map.of("enableVNC", true, "enableVideo", false));
                options = chromeOptions;
            }
            HttpClient.Factory clientFactory = HttpClient.Factory.createDefault();
            try {
                driver = new RemoteWebDriver(new URL(selenoidUrl), options);
            } catch (MalformedURLException e) {
                throw new RuntimeException("Неверный URL Selenoid", e);
            }

        } else {
            if ("firefox".equals(browser)) {
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.addArguments("--start-maximized");
                driver = new FirefoxDriver(firefoxOptions);
            } else {
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--start-maximized");
                driver = new ChromeDriver(chromeOptions);
            }
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.get("http://217.74.37.176/?route=account/register&language=ru-ru");
        registrationPage = new RegistrationPage(driver);
    }

    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
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