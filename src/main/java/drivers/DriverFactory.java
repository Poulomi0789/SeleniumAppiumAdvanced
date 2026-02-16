package drivers;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.net.URL;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DriverFactory {
    private static final Logger log = LoggerFactory.getLogger(DriverFactory.class);
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public static void initDriver(String platform) throws Exception {
        if (platform == null) {
        throw new RuntimeException("❌ FATAL: Platform property is null! Check your Maven -Dplatform flag.");
    }
    platform = platform.toLowerCase().trim();
    log.info("🚀 INITIALIZING FOR PLATFORM: " + platform);
        if (platform.equalsIgnoreCase("web")) {
            log.info("🌐 Setting up Chrome Browser...");
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage");
            driver.set(new ChromeDriver(options));
            log.info("✅ Chrome Browser is up and running.");

        } else if (platform.equalsIgnoreCase("android")) {
            // // Android App Settings
            // log.info("🤖 Connecting to Android Device/Emulator via Appium...");
            // UiAutomator2Options options = new UiAutomator2Options()
            //         .setDeviceName("Android Device")
            //         .withBrowserName("Chrome")
            //         .setAutomationName("UiAutomator2");
            // // .setApp(System.getProperty("user.dir") + "/apps/saucedemo.apk")

            // driver.set(new AndroidDriver(new URL("http://127.0.0.1:4723"), options));
            // log.info("✅ Android Session created. Mobile App: SwagLabs");

//************************************************************************************
            // For Local Execution (Running from IntelliJ on the same machine)
            //String appiumUrl = "http://localhost:4723/wd/hub";
            // OR: For Jenkins Execution (Jenkins and Android are in the same Docker network)
            String appiumUrl = "http://android-container:4723/wd/hub";

            UiAutomator2Options options = new UiAutomator2Options()
                    .setPlatformName("Android")
                    .setDeviceName("Samsung Galaxy S10")
                    .setAutomationName("UiAutomator2")
                    .withBrowserName("Chrome") // This opens Mobile Chrome
                    .setNoReset(true)
                    .setFullReset(false);

            driver.set(new AndroidDriver(new URL(appiumUrl), options));
            log.info("✅ Android Session created. Mobile App: SwagLabs");
            

        } else if (platform.equalsIgnoreCase("ios")) {
            // iOS App Settings
            log.info("🤖 Connecting to iOS Device/Emulator via Appium...");
            XCUITestOptions options = new XCUITestOptions()
                    .setDeviceName("iPhone 15")
                    .withBrowserName("Safari")
                    .setAutomationName("XCUITest");
            //.setApp(System.getProperty("user.dir") + "/apps/saucedemo.app")
            driver.set(new IOSDriver(new URL("http://127.0.0.1:4723"), options));
            log.info("✅ iOS Session created. Mobile App: SwagLabs");
        }

        driver.get().manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    public static WebDriver getDriver() { return driver.get(); }

    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }
}
