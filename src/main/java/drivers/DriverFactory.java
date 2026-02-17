package drivers;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.Duration;
import java.util.HashMap;

public class DriverFactory {
    private static final Logger log = LoggerFactory.getLogger(DriverFactory.class);
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    // Secure credentials: Priority to Jenkins/System Env, fallback to local string
    private static final String USERNAME = System.getenv("BROWSERSTACK_USERNAME") != null ?
            System.getenv("BROWSERSTACK_USERNAME") : "your_local_username";
    private static final String ACCESS_KEY = System.getenv("BROWSERSTACK_ACCESS_KEY") != null ?
            System.getenv("BROWSERSTACK_ACCESS_KEY") : "your_local_key";

    private static final String BS_URL = "https://" + USERNAME + ":" + ACCESS_KEY + "@hub-cloud.browserstack.com/wd/hub";

    public static void initDriver(String platform) throws Exception {
        if (platform == null) {
            throw new RuntimeException("❌ FATAL: Platform property is null! Check your Maven -Dplatform flag.");
        }

        platform = platform.toLowerCase().trim();
        log.info("🚀 INITIALIZING FOR PLATFORM: " + platform);

        if (platform.equalsIgnoreCase("web")) {
            log.info("🌐 Setting up Chrome Browser (Local Headless)...");
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage");
            driver.set(new ChromeDriver(options));

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
            // String appiumUrl = "http://android-container:4723/wd/hub";
//            UiAutomator2Options options = new UiAutomator2Options()
//                    .setDeviceName("Samsung Galaxy S10")
//                    .setAutomationName("UiAutomator2")
//                    .withBrowserName("Chrome") // This opens Mobile Chrome
//                    .setNoReset(true);
//
//            driver.set(new AndroidDriver(new URL(appiumUrl), options));
//            log.info("✅ Android Session created. Mobile App: SwagLabs");

//************************************************************************************
            UiAutomator2Options options = new UiAutomator2Options()
                    .setPlatformName("Android")
                    .setDeviceName("Samsung Galaxy S23")
                    .setPlatformVersion("13.0")
                    .setAutomationName("UiAutomator2")
                    .withBrowserName("Chrome");

            setupBrowserStackOptions(options, "Android Web Test");
            driver.set(new AndroidDriver(new URL(BS_URL), options));

        } else if (platform.equalsIgnoreCase("ios")) {
            log.info("🍎 Connecting to BrowserStack iOS Device...");
            XCUITestOptions options = new XCUITestOptions()
                    .setPlatformName("iOS")
                    .setDeviceName("iPhone 14")
                    .setPlatformVersion("16")
                    .setAutomationName("XCUITest")
                    .withBrowserName("Safari");

            setupBrowserStackOptions(options, "iOS Web Test");
            driver.set(new IOSDriver(new URL(BS_URL), options));
        }

        if (driver.get() != null) {
            driver.get().manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            log.info("✅ " + platform.toUpperCase() + " session created successfully.");
        }
    }

    /**
     * Fixes the 'options' error by using MutableCapabilities.
     * Both UiAutomator2Options and XCUITestOptions inherit from this class.
     */
    private static void setupBrowserStackOptions(MutableCapabilities options, String sessionName) {
        HashMap<String, Object> bstackOptions = new HashMap<>();
        bstackOptions.put("projectName", "Mobile Web Project");
        bstackOptions.put("buildName", "Build_" + System.getProperty("env", "Local"));
        bstackOptions.put("sessionName", sessionName);
        bstackOptions.put("local", "false");
        bstackOptions.put("seleniumVersion", "4.0.0");

        options.setCapability("bstack:options", bstackOptions);
    }

    public static WebDriver getDriver() {
        return driver.get();
    }

    public static void quitDriver() {
        if (driver.get() != null) {
            log.info("🏁 Closing driver session...");
            driver.get().quit();
            driver.remove();
        }
    }
}