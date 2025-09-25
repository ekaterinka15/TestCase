package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class RegistrationPage {
    private WebDriver driver;

    public RegistrationPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    // ---------- Локаторы ---------
    @FindBy(id = "input-firstname")
    private WebElement firstNameInput;

    @FindBy(id = "input-lastname")
    private WebElement lastNameInput;

    @FindBy(id = "input-email")
    private WebElement emailInput;

    @FindBy(id = "input-password")
    private WebElement passwordInput;

    @FindBy(name = "newsletter")
    private WebElement subscribeToggle;

    @FindBy(name = "agree")
    private WebElement privacyToggle;

    @FindBy(css = ".btn.btn-primary")
    private WebElement submitButton;

    @FindBy(css = ".form-control.is-invalid")
    private WebElement passwordError;

    @FindBy(css = ".alert.alert-danger.alert-dismissible")
    private List<WebElement> alertBanners;

    public boolean isSuccessLinkVisible() {
        try {
            WebElement successLink = driver.findElement(By.cssSelector("a[href*='route=account/success']"));
            return successLink.getText().contains("Ваша учетная запись создана!");
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public void safeClick(WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOf(element));
        wait.until(ExpectedConditions.elementToBeClickable(element));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);

        try {
            element.click();
        } catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }


    public void enterFirstName(String name) {
        firstNameInput.clear();
        firstNameInput.sendKeys(name);
    }

    public void enterLastName(String surname) {
        lastNameInput.clear();
        lastNameInput.sendKeys(surname);
    }

    public void enterEmail(String email) {
        emailInput.clear();
        emailInput.sendKeys(email);
    }

    public void enterPassword(String password) {
        passwordInput.clear();
        passwordInput.sendKeys(password);
    }

    // ---------- Тумблеры ----------
    public void setSubscribe(boolean value) {
        if (subscribeToggle.isSelected() != value) {
            safeClick(subscribeToggle);
        }
    }

    public void setPrivacyPolicy(boolean value) {
        if (privacyToggle.isSelected() != value) {
            safeClick(privacyToggle);
        }
    }


    public void clickSubmit() {
        safeClick(submitButton);
    }

    // ---------- Проверка ошибок ----------
    public boolean isPasswordErrorDisplayed() {
        try {
            return passwordError.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public String getPasswordErrorText() {
        return passwordError.getText().trim();
    }

    // ---------- Проверка баннеров ----------
    public List<String> getAllAlertTexts() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfAllElements(alertBanners));
        return alertBanners.stream()
                .map(WebElement::getText)
                .map(String::trim)
                .collect(Collectors.toList());
    }

    public boolean alertContains(String expectedText) {
        return getAllAlertTexts().stream().anyMatch(text -> text.contains(expectedText));
    }

    // ---------- Геттеры для проверки значений ----------
    public WebElement getFirstNameInput() {
        return firstNameInput;
    }

    public WebElement getLastNameInput() {
        return lastNameInput;
    }

    public WebElement getEmailInput() {
        return emailInput;
    }

    public WebElement getSubscribeToggle() {
        return subscribeToggle;
    }

    public WebElement getPrivacyToggle() {
        return privacyToggle;
    }

    public WebElement getPasswordInput() {
        return passwordInput;
    }

    public boolean isRegistrationSuccessMessageVisible() {
        try {
            WebElement successMessage = new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h1")));
            return successMessage.getText().contains("Ваш аккаунт создан")
                    || successMessage.getText().contains("Регистрация завершена");
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String getAlertText() {
        try {
            WebElement alert = new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".alert.alert-danger")));
            return alert.getText().trim();
        } catch (TimeoutException e) {
            return " Ошибка не появилась";
        }
    }

    }









