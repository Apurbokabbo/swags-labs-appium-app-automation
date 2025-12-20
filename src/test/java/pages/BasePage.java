package pages;

import io.qameta.allure.Allure;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import utilities.BaseDriver;
import java.io.*;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BasePage extends BaseDriver {
    public File USER_Info_PATH = new File("src/test/resources/userInfo.txt");
    public String USER_NUMBER_1 = "";

    public SoftAssert softAssert = new SoftAssert();

    // Common Locators
    public By MEDIA_PERMISSION_ALLOW_ALL_BUTTON = By.id("com.android.permissioncontroller:id/permission_allow_all_button");

    /**
     * Find a single element
     */
    public WebElement findElement(By locator) {
        return getDriver().findElement(locator);
    }

    /**
     * Write text to an element
     */
    public void writeText(By locator, String text) {
        waitForVisibilityOfElement(locator, 5);
        findElement(locator).click();
        findElement(locator).clear();
        findElement(locator).sendKeys(text);
        try {
            getDriver().hideKeyboard();
        } catch (Exception e) {
            // Keyboard already hidden
        }
    }

    /**
     * Get text from element
     */
    public String getText(By locator) {
        return findElement(locator).getText();
    }

    /**
     * Scroll until element is found
     */
    public void scrollUntilElementFound(By locator, String direction, int maxAttempts) throws InterruptedException {
        int attempts = 0;
        while (attempts < maxAttempts) {
            try {
                // Check if element is visible
                WebElement element = findElement(locator);
                if (element.isDisplayed()) {
                    return;
                }
            } catch (Exception e) {
                // Element not found, continue scrolling
            }

            // Perform scrolling in the given direction
            if ("DOWN".equalsIgnoreCase(direction)) {
                scrollDown();
                Thread.sleep(1000);
            } else if ("UP".equalsIgnoreCase(direction)) {
                scrollUp();
            } else {
                throw new IllegalArgumentException("Invalid scroll direction: " + direction);
            }

            attempts++;
        }
        System.out.println("Element not found after " + maxAttempts + " attempts.");
    }

    /**
     * Scrolls down a half-screen length using W3C Actions
     */
    private void scrollDown() {
        Dimension size = getDriver().manage().window().getSize();
        int width = size.width / 2;
        int startY = (int) (size.height * 0.60);
        int endY = (int) (size.height * 0.40);

        try {
            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence swipe = new Sequence(finger, 1);

            swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), width, startY));
            swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            swipe.addAction(finger.createPointerMove(Duration.ofMillis(500), PointerInput.Origin.viewport(), width, endY));
            swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

            getDriver().perform(Arrays.asList(swipe));
        } catch (Exception e) {
            System.out.println("Error while scrolling down: " + e.getMessage());
        }
    }

    /**
     * Scrolls up a half-screen length using W3C Actions
     */
    private void scrollUp() {
        Dimension size = getDriver().manage().window().getSize();
        int width = size.width / 2;
        int startY = (int) (size.height * 0.30);
        int endY = (int) (size.height * 0.70);

        try {
            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence swipe = new Sequence(finger, 1);

            swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), width, startY));
            swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            swipe.addAction(finger.createPointerMove(Duration.ofMillis(500), PointerInput.Origin.viewport(), width, endY));
            swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

            getDriver().perform(Arrays.asList(swipe));
        } catch (Exception e) {
            System.out.println("Error while scrolling up: " + e.getMessage());
        }
    }

    /**
     * Take screenshot and attach to Allure report
     */
    public void takeScreenShotAllureAttach(String screenshotname) {
        Allure.addAttachment(screenshotname, new ByteArrayInputStream(getDriver().getScreenshotAs(OutputType.BYTES)));
    }

    /**
     * Save balance to file
     */
    public void saveToFile(int balance, File BALANCE_FILE_PATH) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BALANCE_FILE_PATH))) {
            writer.write(String.valueOf(balance));
            System.out.println("Balance saved to file successfully.");
        } catch (IOException e) {
            System.err.println("Error saving balance to file: " + e.getMessage());
        }
    }

    /**
     * Read multiple values from file
     */
    public String[] readMultipleValues(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        }

        return content.toString().split(",");  // Split into array
    }

    /**
     * Wait for visibility of element
     */
    public void waitForVisibilityOfElement(By locator, int timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeoutInSeconds));
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Fluent wait and click on element
     */
    public void fluentWaitClickOnElement(By locator, int holdInSecond) {
        FluentWait<AndroidDriver> wait = new FluentWait<>(getDriver())
                .withTimeout(Duration.ofSeconds(holdInSecond))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(org.openqa.selenium.NoSuchElementException.class);

        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        element.click();
    }

    /**
     * Write two values to file (number and password)
     */
    public void writeToFileDoubleValue(File filePath, String number, String password) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            writer.write(number + "," + password);
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generate random password
     */
    public String generatePassword(int length) {
        if (length < 8) {
            length = 8; // Ensure minimum length of 8
        }

        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+<>?";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            password.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        return password.toString();
    }

    /**
     * Fluent wait for element visibility
     */
    public static void fluentWaitForElement(By locator, int timeoutInSeconds, int pollingTimeInMillis) {
        Wait<AndroidDriver> wait = new FluentWait<>(getDriver())
                .withTimeout(Duration.ofSeconds(timeoutInSeconds))
                .pollingEvery(Duration.ofMillis(pollingTimeInMillis))
                .ignoring(NoSuchElementException.class);

        wait.until(driver -> {
            WebElement element = getDriver().findElement(locator);
            return (element.isDisplayed()) ? element : null;
        });
    }

    /**
     * Check if element is visible within timeout
     */
    public boolean isElementVisible(By locator, int timeoutInSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeoutInSeconds));
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Search in text field
     */
    public void searchTextField(By searchFieldLocator, String searchText) {
        try {
            WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
            WebElement searchField = wait.until(ExpectedConditions.elementToBeClickable(searchFieldLocator));

            Thread.sleep(1000);
            searchField.click();
            searchField.clear();
            searchField.sendKeys(searchText);
        } catch (Exception e) {
            System.out.println("Error interacting with search field: " + e.getMessage());
        }
    }

    /**
     * Get text using content-desc attribute
     */
    public String getTextUsingAttribute(By locator) {
        return findElement(locator).getAttribute("content-desc");
    }

    /**
     * Validate password visibility with eye button
     */
    public void passwordEyeButtonValidation(By eyeButton, By passwordField, String password) throws InterruptedException {
        Thread.sleep(3000);
        fluentWaitClickOnElement(eyeButton, 10);
        Thread.sleep(3000);
        Assert.assertEquals(findElement(passwordField).getText(), password, "Password is not visible");
    }

    /**
     * Allow media permissions
     */
    public void mediaPermissionAllower() {
        waitForVisibilityOfElement(MEDIA_PERMISSION_ALLOW_ALL_BUTTON, 5);
        findElement(MEDIA_PERMISSION_ALLOW_ALL_BUTTON).click();
    }

    /**
     * Assert toaster message
     */
    public void toasterMessageAssertion(By toasterLocator, String expectedMessage) {
        Assert.assertTrue(isElementVisible(toasterLocator, 10), "Toaster message did not appear!");
        Assert.assertEquals(findElement(toasterLocator).getAttribute("content-desc"), expectedMessage, "Toaster message did not match!");
    }

    /**
     * Soft assert text
     */
    public void assertText(By locator, String expected) {
        waitForVisibilityOfElement(locator, 10);
        softAssert.assertEquals(findElement(locator).getAttribute("content-desc"), expected, "Text did not match!");
    }

    /**
     * Click random element and get its text
     */
    public String clickRandomElementAndGetText(By locator, int size) throws InterruptedException {
        Thread.sleep(1000);
        List<WebElement> elements = getDriver().findElements(locator);
        if (!elements.isEmpty()) {
            int randomIndex = new Random().nextInt(size);
            WebElement selectedElement = elements.get(randomIndex);

            String elementText = selectedElement.getAttribute("content-desc");
            selectedElement.click();

            return elementText;
        } else {
            throw new RuntimeException("No elements found for locator: " + locator.toString());
        }
    }

    /**
     * Normalize text by taking first line
     */
    public String normalizeText(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.split("\n")[0].trim();
    }

    /**
     * Tap by coordinates using W3C Actions
     */
    public void tapByCoordinates(int x, int y) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1);

        tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
        tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        getDriver().perform(Arrays.asList(tap));
    }

    /**
     * Scroll up multiple times
     */
    public void scrollUpBlindly(int numberOfScrolls) throws InterruptedException {
        for (int i = 0; i < numberOfScrolls; i++) {
            scrollUp();
            Thread.sleep(1000);
        }
    }

    /**
     * Scroll down multiple times
     */
    public void scrollDownBlindly(int numberOfScrolls) throws InterruptedException {
        for (int i = 0; i < numberOfScrolls; i++) {
            scrollDown();
            Thread.sleep(1000);
        }
    }
}