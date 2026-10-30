package pages;

import io.appium.java_client.android.AndroidDriver;
import io.qameta.allure.Allure;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import utilities.BaseDriver;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;

public class BasePage extends CommonLocators {

    private static final Logger logger = LoggerFactory.getLogger(BasePage.class);
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    protected AndroidDriver driver;
    protected WebDriverWait wait;
    protected SoftAssert softAssert = new SoftAssert();

    public BasePage() {
        // Intentionally empty. The driver may not exist yet when a page object is
        // created as a field initializer (before @BeforeMethod). It is bound lazily
        // by ensureInitialized() on first use.
    }

    // ─── Driver binding ────────────────────────────────────────────────────────

    /**
     * Binds this page object to the CURRENT thread's driver.
     * A new driver is created for every test, so a page object created once as a
     * class field must re-bind whenever the driver instance changes.
     */
    public void ensureInitialized() {
        AndroidDriver current = BaseDriver.getDriver();
        if (driver != current) {
            driver = current;
            wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
        }
    }

    // ─── Logging ───────────────────────────────────────────────────────────────

    public void logInfo(String info) {
        String border = "-------------------------------------------------";
        logger.info(border);
        logger.info(info);
        logger.info(border);
        Allure.step(info);
    }

    // ─── Helpers ───────────────────────────────────────────────────────────────

    /** Treats null, empty and the literal string "null" (returned by UiAutomator2) as blank. */
    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty() || s.trim().equalsIgnoreCase("null");
    }

    /** Reads an attribute without ever throwing; returns "" if missing or unsupported. */
    private String safeAttribute(WebElement el, String attribute) {
        try {
            String value = el.getAttribute(attribute);
            return isBlank(value) ? "" : value.trim();
        } catch (Exception e) {
            return "";
        }
    }

    private void hideKeyboardQuietly() {
        try {
            ensureInitialized();
            driver.hideKeyboard();
        } catch (Exception ignored) {
            // keyboard may already be hidden
        }
    }

    // ─── Waits ─────────────────────────────────────────────────────────────────

    public WebElement waitForVisibility(By locator) {
        ensureInitialized();
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement waitForVisibility(By locator, int seconds) {
        ensureInitialized();
        return new WebDriverWait(driver, Duration.ofSeconds(seconds))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement waitForClickable(By locator) {
        ensureInitialized();
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public void waitForInvisibility(By locator) {
        ensureInitialized();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public boolean isElementVisible(By locator, int timeoutSeconds) {
        try {
            waitForVisibility(locator, timeoutSeconds);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isElementInvisible(By locator, int timeoutSeconds) {
        ensureInitialized();
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                    .until(ExpectedConditions.invisibilityOfElementLocated(locator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Waits until every given element is visible (anchors of a loaded screen). */
    public void waitForScreenLoaded(By... keyElements) {
        for (By locator : keyElements) {
            waitForVisibility(locator, 15);
        }
    }

    /** Waits until the page source stops changing (UI finished loading). */
    public void waitForScreenStable(int timeoutSeconds) {
        ensureInitialized();
        String previous = "";
        int stableCount = 0;
        long end = System.currentTimeMillis() + timeoutSeconds * 1000L;

        while (System.currentTimeMillis() < end) {
            String current = driver.getPageSource();
            if (current.equals(previous)) {
                if (++stableCount >= 2) return; // unchanged twice in a row = loaded
            } else {
                stableCount = 0;
            }
            previous = current;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        logger.warn("Screen did not become stable within {}s", timeoutSeconds);
    }

    // ─── Find / Click ──────────────────────────────────────────────────────────

    public WebElement findElement(By locator) {
        ensureInitialized();
        return driver.findElement(locator);
    }

    public void click(By locator) {
        waitForVisibility(locator).click();
    }

    public void safeClick(By locator) {
        try {
            click(locator);
        } catch (Exception e) {
            logger.warn("Retrying click: {}", locator);
            fluentWaitClick(locator, 5);
        }
    }

    public void fluentWaitClick(By locator, int timeoutSeconds) {
        ensureInitialized();
        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeoutSeconds))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class)
                .until(d -> {
                    WebElement el = d.findElement(locator);
                    if (el.isDisplayed() && el.isEnabled()) {
                        el.click();
                        return true;
                    }
                    return false;
                });
    }

    public void pressBack() {
        ensureInitialized();
        driver.navigate().back();
    }

    // ─── Typing ────────────────────────────────────────────────────────────────

    /**
     * Taps the field, then types into the FOCUSED element. Re-locating by a positional
     * XPath after the tap is unreliable in Flutter (the node tree shifts when the
     * keyboard opens).
     */

    // Taps the field and types into the focused element (no keyboard handling)
    private void tapAndType(By locator, String text) {
        waitForClickable(locator).click();

        WebElement el;
        try {
            el = driver.switchTo().activeElement();
            if (!"android.widget.EditText".equals(el.getAttribute("class"))) {
                throw new NoSuchElementException("Focused element is not an input");
            }
        } catch (Exception e) {
            el = new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.presenceOfElementLocated(locator));
        }
        el.sendKeys(text);
    }

    // Normal screens: type, then hide the keyboard
    public void clickAndWrite(By locator, String text) {
        tapAndType(locator, text);
        hideKeyboardQuietly();
    }

    public void clickAndWriteInModal(By locator, String text) {
        tapAndType(locator, text);
    }

    public void writeTextAndKeyboardHide(By locator, String text) {
        WebElement element = waitForVisibility(locator);
        element.clear();
        element.sendKeys(text);
        hideKeyboardQuietly();
    }

    // ─── Reading text ──────────────────────────────────────────────────────────

    public String getText(By locator) {
        return waitForVisibility(locator).getText();
    }

    /** Reads text; falls back to content-desc, then hint. */
    public String getTextOrDesc(By locator) {
        WebElement element = waitForVisibility(locator);

        String value = safeAttribute(element, "text");
        if (value.isEmpty()) value = safeAttribute(element, "content-desc");
        if (value.isEmpty()) value = safeAttribute(element, "hint");
        return value;
    }

    /** Returns the typed value; "" if the field is empty or only shows its hint. */
    public String getFieldValue(By locator) {
        WebElement el = waitForVisibility(locator);

        String text = safeAttribute(el, "text");
        String hint = safeAttribute(el, "hint");

        if (text.isEmpty()) return "";
        if (!hint.isEmpty() && text.equals(hint)) return ""; // placeholder, not user input
        return text;
    }

    public boolean isFieldEmpty(By locator) {
        return getFieldValue(locator).isEmpty();
    }

    // ─── Toast ─────────────────────────────────────────────────────────────────

    public By toastByText(String message) {
        return By.xpath("//android.widget.Toast[@text=\"" + message + "\"]");
    }

    public By toastContainingText(String partialMessage) {
        return By.xpath("//android.widget.Toast[contains(@text,\"" + partialMessage + "\")]");
    }

    /** Toasts are not "visible" in the normal sense, so check presence. */
    public boolean isToastDisplayed(String message, int timeoutSeconds) {
        ensureInitialized();
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                    .until(ExpectedConditions.presenceOfElementLocated(toastByText(message)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getToastText(int timeoutSeconds) {
        ensureInitialized();
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//android.widget.Toast")))
                .getAttribute("text");
    }

    // ─── Assertions ────────────────────────────────────────────────────────────

    /** Hard assertion: stops the test immediately on failure. */
    public void assertTextEquals(By locator, String expectedText) {
        String actual = getTextOrDesc(locator);
        logger.info("Hard assert | expected: [{}] | actual: [{}]", expectedText, actual);
        Assert.assertEquals(actual, expectedText.trim(), "Text mismatch for locator: " + locator);
    }

    /** Soft assertion: records the failure; call assertAll() at the end of the test. */
    public void softAssertTextEquals(By locator, String expectedText) {
        String actual = getTextOrDesc(locator);
        logger.info("Soft assert | expected: [{}] | actual: [{}]", expectedText, actual);
        softAssert.assertEquals(actual, expectedText.trim(), "Text mismatch for locator: " + locator);
    }

    public void assertAll() {
        try {
            softAssert.assertAll();
        } finally {
            softAssert = new SoftAssert(); // reset: page objects are reused across tests
        }
    }

    public boolean isElementGone(By locator, int timeoutSeconds) {
        ensureInitialized();
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds)).until(d -> {
                List<WebElement> els = d.findElements(locator);
                if (els.isEmpty()) return true;
                try {
                    WebElement el = els.get(0);
                    Rectangle r = el.getRect();
                    return !el.isDisplayed() || r.getWidth() == 0 || r.getHeight() == 0;
                } catch (StaleElementReferenceException e) {
                    return true;
                }
            });
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void assertFieldEmpty(By locator, String fieldName) {
        Assert.assertTrue(isFieldEmpty(locator), fieldName + " should be empty");
        logInfo(fieldName + " is empty");
    }

    public void assertFieldNotEmpty(By locator, String fieldName) {
        Assert.assertFalse(isFieldEmpty(locator), fieldName + " should contain the typed value");
    }

    // ─── Scrolling ─────────────────────────────────────────────────────────────

    public void scrollDown() {
        scroll(0.7, 0.3);
    }

    public void scrollUp() {
        scroll(0.3, 0.7);
    }

    private void scroll(double startRatio, double endRatio) {
        ensureInitialized();
        Dimension size = driver.manage().window().getSize();
        int x = size.width / 2;
        int startY = (int) (size.height * startRatio);
        int endY = (int) (size.height * endRatio);

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1)
                .addAction(finger.createPointerMove(Duration.ZERO,
                        PointerInput.Origin.viewport(), x, startY))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerMove(Duration.ofMillis(600),
                        PointerInput.Origin.viewport(), x, endY))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Collections.singletonList(swipe));
    }

    // ─── Screenshot ────────────────────────────────────────────────────────────

    public void takeScreenshot(String name) {
        ensureInitialized();
        try {
            Allure.addAttachment(name, "image/png",
                    new ByteArrayInputStream(driver.getScreenshotAs(OutputType.BYTES)),
                    "png");
        } catch (Exception e) {
            logger.error("Screenshot failed: {}", e.getMessage());
        }
    }
    public  String generateRandomPin() {
        Random random = new Random();

        StringBuilder pin = new StringBuilder();

        for (int i = 0; i < 5; i++) {
            pin.append(random.nextInt(10)); // 0-9
        }

        return pin.toString();
    }

    public static void writeDataToFile(String filePath, String key, String value) {
        try {
            Map<String, String> data = new LinkedHashMap<>();

            File file = new File(filePath);

            if (file.exists()) {
                List<String> lines = Files.readAllLines(Paths.get(filePath));

                for (String line : lines) {
                    if (line.contains("=")) {
                        String[] parts = line.split("=", 2);
                        data.put(parts[0].trim(), parts[1].trim());
                    }
                }
            }

// Add or update key
            data.put(key, value);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    writer.write(entry.getKey() + "=" + entry.getValue());
                    writer.newLine();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readDataFromFile(String filePath, String key) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));

            for (String line : lines) {
                if (line.contains("=")) {
                    String[] parts = line.split("=", 2);

                    if (parts[0].trim().equals(key)) {
                        return parts[1].trim();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void slideToCheckIn(By locator) {
        WebElement slider = new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));

        Rectangle r = slider.getRect();
        Dimension screen = driver.manage().window().getSize();

        int y      = r.getY() + r.getHeight() / 2;
        int startX = r.getX() + r.getHeight() / 2;   // round handle on the left
        int endX   = screen.getWidth() - 5;          // overshoot to the screen edge

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 0);

        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, y));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(new Pause(finger, Duration.ofMillis(300)));

        // many small moves so Flutter receives continuous drag events
        int steps = 20;
        for (int i = 1; i <= steps; i++) {
            int x = startX + (endX - startX) * i / steps;
            swipe.addAction(finger.createPointerMove(Duration.ofMillis(40), PointerInput.Origin.viewport(), x, y));
        }

        swipe.addAction(new Pause(finger, Duration.ofMillis(200)));   // hold at the end
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Arrays.asList(swipe));
    }

    public void slideToCheckInV2(By locator) {
        WebElement slider = new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
        Rectangle r = slider.getRect();

        ((JavascriptExecutor) driver).executeScript("mobile: dragGesture", java.util.Map.of(
                "startX", r.getX() + r.getHeight() / 2,
                "startY", r.getY() + r.getHeight() / 2,
                "endX",   r.getX() + r.getWidth() - 10,
                "endY",   r.getY() + r.getHeight() / 2,
                "speed",  500
        ));
    }
}