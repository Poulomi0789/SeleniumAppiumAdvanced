package stepdefinitions;

import drivers.DriverFactory;
import org.openqa.selenium.WebDriver;

public class BaseSteps {
    protected WebDriver getDriver() {
        return DriverFactory.getDriver();
    }
}