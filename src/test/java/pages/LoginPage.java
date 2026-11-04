package pages;

import io.appium.java_client.AppiumBy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import utilities.TestData;

public class LoginPage extends BasePage {

    // ── Expected texts ────────────────────────────────────────────────────────
    public  final String USERNAME_PLACEHOLDER   = "Username";
    public  final String PASSWORD_PLACEHOLDER   = "Password";
    public  final String USERNAME_INFO_TEXT     = "The currently accepted usernames for this application are (tap to autofill):";
    public  final String PASSWORD_INFO_TEXT     = "And the password for all users is:";
    public  final String USERNAME_REQUIRED      = "Username is required";
    public  final String PASSWORD_REQUIRED      = "Password is required";
    public  final String INVALID_CREDENTIALS    = "Username and password do not match any user in this service";
    public  final String LOCKED_OUT             = "Sorry, this user has been locked out.";

    // ── Locators (accessibility id is faster and more stable than XPath) ──────
    private final By usernameInput    = AppiumBy.accessibilityId("test-Username");
    private final By passwordInput    = AppiumBy.accessibilityId("test-Password");
    private final By loginButton      = AppiumBy.accessibilityId("test-LOGIN");
    // NOTE: the text lives in the CHILD TextView; the ViewGroup itself only has content-desc.
    private final By errorMessage     = By.xpath("//android.view.ViewGroup[@content-desc=\"test-Error message\"]/android.widget.TextView");
    private final By standardUserHint = By.xpath("//android.view.ViewGroup[@content-desc=\"test-standard_user\"]/android.widget.TextView");
    private final By usernameInfoText = By.xpath("//android.widget.TextView[contains(@text,\"accepted usernames\")]");
    private final By passwordInfoText = By.xpath("//android.widget.TextView[@text=\"" + PASSWORD_INFO_TEXT + "\"]");

    // ── State checks ──────────────────────────────────────────────────────────
    public boolean isLoaded() {
        return isElementVisible(usernameInput, 15)
                && isElementVisible(passwordInput, 5)
                && isElementVisible(loginButton, 5);
    }

    public boolean isErrorDisplayed() {
        return isElementVisible(errorMessage, 5);
    }

    // ── Reading ───────────────────────────────────────────────────────────────
    public String getUsernamePlaceholder() { return getTextOrDesc(usernameInput); }
    public String getPasswordPlaceholder() { return getTextOrDesc(passwordInput); }
    public String getUsernameValue()       { return getFieldValue(usernameInput); }
    public boolean isPasswordEmpty()       { return isFieldEmpty(passwordInput); }

    public String getErrorMessage() {
        return getText(errorMessage);
    }

    public String getUsernameInfoText() {
        scrollUntilVisible(usernameInfoText, 3);
        return getText(usernameInfoText);
    }

    public String getPasswordInfoText() {
        scrollUntilVisible(passwordInfoText, 3);
        return getText(passwordInfoText);
    }

    // ── Actions ───────────────────────────────────────────────────────────────
    @Step("Login with username [{username}] and password [{password}]")
    public void login(String username, String password) {
        if (username != null && !username.isEmpty()) clickAndWrite(usernameInput, username);
        if (password != null && !password.isEmpty()) clickAndWrite(passwordInput, password);
        click(loginButton);
    }

    @Step("Login as standard user")
    public ProductsPage loginAsStandardUser() {
        login(TestData.standardUser(), TestData.password());
        ProductsPage products = new ProductsPage();
        products.waitUntilLoaded();
        return products;
    }

    @Step("Tap the 'standard_user' autofill hint")
    public void tapStandardUserHint() {
        scrollUntilVisible(standardUserHint, 3);
        click(standardUserHint);
    }
}
