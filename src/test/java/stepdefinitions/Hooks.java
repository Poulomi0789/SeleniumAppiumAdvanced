package stepdefinitions;

import drivers.DriverFactory;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.cucumber.java.*;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import utils.ConfigReader;

import java.io.File;

public class Hooks {

    private static AppiumDriverLocalService service;

    @Before
    public void setup() throws Exception {
        // Read from Maven command line: -Dplatform=android
        String platform = System.getProperty("platform");
        Allure.parameter("Platform", platform.toUpperCase());
        System.out.println("LOG: Framework is starting on " + platform);
        DriverFactory.initDriver(platform);
    }

    @After
    public void tearDown(Scenario scenario) {
        String platform = System.getProperty("platform");
        Allure.addAttachment("Execution Platform", platform.toUpperCase());
        if (scenario.isFailed() || DriverFactory.getDriver() != null) {
            final byte[] screenshot = ((TakesScreenshot) DriverFactory.getDriver()).getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", "Bug_Screenshot");
        }
        DriverFactory.quitDriver();
    }
    // @BeforeAll
    // public static void startAppiumServer() {
    //     String platform = System.getProperty("platform", "web");
    //     if (!platform.equalsIgnoreCase("web")) {
    //         service = new AppiumServiceBuilder()
    //                 .withAppiumJS(new File("/usr/local/lib/node_modules/appium/build/lib/main.js"))
    //                 .withIPAddress("127.0.0.1")
    //                 .usingPort(4723)
    //                 .build();
    //         service.start();
    //     }
    // }

    @AfterAll
    public static void stopAppiumServer() {
        if (service != null && service.isRunning()) {
            service.stop();
        }
    }

}
