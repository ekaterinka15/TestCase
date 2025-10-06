package steps;


import io.cucumber.java.After;
import io.cucumber.java.ru.Допустим;
import io.cucumber.java.ru.И;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Тогда;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
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
        // Загружаем настройки
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream("src/main/resources/application.properties")) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить application.properties", e);
        }

        // Получаем параметры
        String driverType = System.getProperty("type.driver", properties.getProperty("type.driver", "local")).toLowerCase();
        String browser = System.getProperty("browser", properties.getProperty("browser", "chrome")).toLowerCase();
        String selenoidUrl = properties.getProperty("selenoid.url");

        // Фикс ошибки netty
        System.setProperty("webdriver.http.factory", "apache");


        // Создание драйвера
        if ("remote".equals(driverType)) {
            driver = createRemoteDriver(browser, selenoidUrl);
        } else {
            driver = createLocalDriver(browser);
        }

        // Открытие формы регистрации
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.get("http://217.74.37.176/?route=account/register&language=ru-ru");

        // Инициализация страницы
        registrationPage = new RegistrationPage(driver);
    }

    private static WebDriver createRemoteDriver(String browser, String selenoidUrl) {
        Map<String, Object> selenoidOptions = new HashMap<>();
        selenoidOptions.put("enableVNC", true);
        selenoidOptions.put("enableVideo", false);

        try {
            switch (browser) {
                case "chrome":
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--disable-gpu", "--headless=new");
                    chromeOptions.setBrowserVersion("109.0");
                    chromeOptions.setCapability("selenoid:options", selenoidOptions);
                    return new RemoteWebDriver(new URL(selenoidUrl), chromeOptions);

                case "firefox":
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    firefoxOptions.addArguments("--headless");
                    firefoxOptions.setBrowserVersion("118.0");
                    firefoxOptions.setCapability("selenoid:options", selenoidOptions);
                    return new RemoteWebDriver(new URL(selenoidUrl), firefoxOptions);

                default:
                    throw new RuntimeException("Неизвестный браузер: " + browser);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Неверный URL Selenoid", e);
        }
    }

    private static WebDriver createLocalDriver(String browser) {
        switch (browser) {
            case "chrome":
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--start-maximized");
                return new ChromeDriver(chromeOptions);

            case "firefox":
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.addArguments("--start-maximized");
                return new FirefoxDriver(firefoxOptions);

            default:
                throw new RuntimeException("Неизвестный локальный браузер: " + browser);
        }
    }

    public static RegistrationPage getRegistrationPage() {
        return registrationPage;
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