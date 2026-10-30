package utilities;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.screenrecording.CanRecordScreen;
import io.appium.java_client.android.AndroidStartScreenRecordingOptions;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Base64;
import java.util.Properties;

public class BaseDriver {

    private static final String DEVICE = System.getProperty("device", "Motorola");
    private static final String CAPABILITIES_FILE_PATH = "src/test/resources/capabilities_" + DEVICE + ".properties";
    private static final ThreadLocal<AndroidDriver> DRIVER_THREAD_LOCAL = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setDriver() throws MalformedURLException {

        PropertyReader.loadProperties(CAPABILITIES_FILE_PATH);
        Properties props = PropertyReader.getAllProperties();

        UiAutomator2Options options = new UiAutomator2Options();
        props.forEach((k, v) -> options.setCapability(k.toString(), v.toString()));

        URL remoteUrl = new URL("http://127.0.0.1:4723");
        AndroidDriver driver = new AndroidDriver(remoteUrl, options);
        DRIVER_THREAD_LOCAL.set(driver);

        // Start recording for every test (discarded later if the test passes)
        try {
            driver.startRecordingScreen(
                    new AndroidStartScreenRecordingOptions()
                            .withTimeLimit(Duration.ofMinutes(3))); // Android max is 3 min
        } catch (Exception e) {
            System.out.println("Could not start screen recording: " + e.getMessage());
        }
    }

    @AfterMethod(alwaysRun = true)
    public void quitDriver(ITestResult result) {
        AndroidDriver driver = DRIVER_THREAD_LOCAL.get();
        if (driver == null) return;

        boolean failed = result.getStatus() == ITestResult.FAILURE;
        String testName = result.getName();

        try {
            // 1) Screenshot only on failure (must happen BEFORE quit)
            if (failed) {
                byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                Allure.addAttachment("Screenshot - " + testName, "image/png",
                        new ByteArrayInputStream(png), "png");
                saveFile("target/screenshots", testName + ".png", png);
            }
        } catch (Exception e) {
            System.out.println("Screenshot failed: " + e.getMessage());
        }

        try {
            // 2) Always stop recording; keep the video only on failure
            String base64Video = ((CanRecordScreen) driver).stopRecordingScreen();
            if (failed && base64Video != null && !base64Video.isEmpty()) {
                byte[] video = Base64.getMimeDecoder().decode(base64Video);
                Allure.addAttachment("Video - " + testName, "video/mp4",
                        new ByteArrayInputStream(video), "mp4");
                saveFile("target/videos", testName + ".mp4", video);
            }
        } catch (Exception e) {
            System.out.println("Video failed: " + e.getMessage());
        }

        try {
            driver.quit();
        } catch (Exception ignored) {
        } finally {
            DRIVER_THREAD_LOCAL.remove();
        }
    }

    private static void saveFile(String dir, String fileName, byte[] data) {
        try {
            Path folder = Paths.get(dir);
            Files.createDirectories(folder);
            Files.write(folder.resolve(fileName), data);
        } catch (Exception e) {
            System.out.println("Could not save " + fileName + ": " + e.getMessage());
        }
    }

    public static AndroidDriver getDriver() {
        AndroidDriver driver = DRIVER_THREAD_LOCAL.get();
        if (driver == null) {
            throw new IllegalStateException(
                    "Driver not initialised. Ensure @BeforeMethod ran before calling getDriver().");
        }
        return driver;
    }
}