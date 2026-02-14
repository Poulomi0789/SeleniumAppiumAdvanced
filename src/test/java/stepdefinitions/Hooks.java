package stepdefinitions;

import drivers.DriverFactory;
import io.cucumber.java.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import utils.ConfigReader;

public class Hooks {
    @Before
    public void setup() {
        String browser = ConfigReader.getProperty("browser");
        DriverFactory.initDriver(browser);
    }

    @After
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed()) {
            final byte[] screenshot = ((TakesScreenshot) DriverFactory.getDriver()).getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", "Bug_Screenshot");
        }
        DriverFactory.quitDriver();
    }
}