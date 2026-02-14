package stepdefinitions;

import io.cucumber.java.en.*;
import pages.LoginPage;
import utils.ConfigReader;

public class LoginSteps extends BaseSteps {
    LoginPage loginPage = new LoginPage(getDriver());

    @Given("I navigate to the login page")
    public void navigateToLogin() {
        getDriver().get(ConfigReader.getProperty("url"));
    }

    @When("I enter valid credentials")
    public void enterCredentials() {
        loginPage.login(ConfigReader.getProperty("username"), ConfigReader.getProperty("password"));
    }

    @When("I login with {string} and {string}")
    public void loginWithCredentials(String username, String password) {
        loginPage.login(username, password);
    }
    @Then("I should see the products page")
    public void verifyDashboard() {
        assert getDriver().getCurrentUrl().contains("inventory.html");
    }
}