package utilities;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import io.appium.java_client.android.AndroidDriver;
import pages.BasePage;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Properties;

public class BaseDriver {

    private static AndroidDriver driver;
    private static final String CAPABILITIES_FILE_PATH = "src/test/resources/capabilities_oppo.properties";
    public static Properties props;

    @BeforeMethod(alwaysRun = true)
    public void setDriver() throws MalformedURLException {
        File f = new File("resources");
        File fs = new File(f, "Android.SauceLabs.Mobile.Sample.app.2.7.1.apk");

        PropertyReader.loadProperties(CAPABILITIES_FILE_PATH);
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();

        props = PropertyReader.getAllProperties();
        props.forEach((key, value) ->
                desiredCapabilities.setCapability(key.toString(), value.toString()));

        URL remoteUrl = new URL("http://127.0.0.1:4723");
        driver = new AndroidDriver(remoteUrl, desiredCapabilities);

        // Set implicit wait
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    public static AndroidDriver getDriver() {
        return driver;
    }

    @AfterMethod(alwaysRun = true)
    public synchronized void closeBrowser(ITestResult result) {
        try {
            if (result.getStatus() == ITestResult.FAILURE) {

                BasePage basePage = new BasePage();
                basePage.takeScreenShotAllureAttach(result.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            getDriver().quit();
        }
    }
}