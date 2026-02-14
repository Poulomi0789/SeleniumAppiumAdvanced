package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {
    private By usernameField = By.id("user-name");
    private By passwordField = By.id("password");
    private By loginButton = By.id("login-button");

    public LoginPage(WebDriver driver) { super(driver); }

    public void login(String user, String pass) {
        sendKeys(usernameField, user);
        sendKeys(passwordField, pass);
        click(loginButton);
    }
}