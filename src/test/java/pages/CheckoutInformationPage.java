package pages;

import io.appium.java_client.AppiumBy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class CheckoutInformationPage extends BasePage {

    public static final String FIRST_NAME_REQUIRED  = "First Name is required";
    public static final String LAST_NAME_REQUIRED   = "Last Name is required";
    public static final String POSTAL_CODE_REQUIRED = "Postal Code is required";

    private final By screenTitle    = By.xpath("//android.widget.TextView[@text=\"CHECKOUT: INFORMATION\"]");
    private final By firstNameInput = AppiumBy.accessibilityId("test-First Name");
    private final By lastNameInput  = AppiumBy.accessibilityId("test-Last Name");
    private final By zipInput       = AppiumBy.accessibilityId("test-Zip/Postal Code");
    private final By continueButton = AppiumBy.accessibilityId("test-CONTINUE");
    private final By cancelButton   = AppiumBy.accessibilityId("test-CANCEL");
    private final By errorMessage   = By.xpath("//android.view.ViewGroup[@content-desc=\"test-Error message\"]/android.widget.TextView");

    public void waitUntilLoaded() {
        waitForVisibility(screenTitle, 15);
    }

    public boolean isLoaded() {
        return isElementVisible(screenTitle, 10);
    }

    public String getErrorMessage() {
        return getText(errorMessage);
    }

    @Step("Fill checkout info: [{first}] [{last}] [{zip}]")
    public CheckoutInformationPage fillInformation(String first, String last, String zip) {
        if (first != null && !first.isEmpty()) clickAndWrite(firstNameInput, first);
        if (last != null && !last.isEmpty())   clickAndWrite(lastNameInput, last);
        if (zip != null && !zip.isEmpty())     clickAndWrite(zipInput, zip);
        return this;
    }

    /** Taps CONTINUE and stays on this page type (use for validation-error checks). */
    @Step("Tap CONTINUE")
    public CheckoutInformationPage tapContinue() {
        click(continueButton);
        return this;
    }

    /** Fills valid data, taps CONTINUE and returns the overview screen. */
    @Step("Continue to overview with valid data")
    public CheckoutOverviewPage continueToOverview(String first, String last, String zip) {
        fillInformation(first, last, zip);
        click(continueButton);
        CheckoutOverviewPage overview = new CheckoutOverviewPage();
        overview.waitUntilLoaded();
        return overview;
    }

    @Step("Cancel checkout")
    public CartPage cancel() {
        click(cancelButton);
        CartPage cart = new CartPage();
        cart.waitUntilLoaded();
        return cart;
    }
}
