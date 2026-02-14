package pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePage {

    @FindBy(id = "user-name") // WEB
    // @AndroidFindBy(accessibility = "test-Username") // ANDROID
    // @iOSXCUITFindBy(accessibility = "test-Username") // IOS
    private WebElement usernameField;

    @FindBy(id = "password")
    // @AndroidFindBy(accessibility = "test-Password")
    // @iOSXCUITFindBy(accessibility = "test-Password")
    private WebElement passwordField;

    @FindBy(id = "login-button")
    // @AndroidFindBy(accessibility = "test-LOGIN")
    // @iOSXCUITFindBy(accessibility = "test-LOGIN")
    private WebElement loginButton;

    public LoginPage(WebDriver driver) { super(driver); }

    public void login(String user, String pass) {
        usernameField.sendKeys(user);
        passwordField.sendKeys(pass);
        loginButton.click();
    }
}
