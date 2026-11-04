package pages;

import io.appium.java_client.AppiumBy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class CheckoutCompletePage extends BasePage {

    private final By screenTitle  = By.xpath("//android.widget.TextView[@text=\"CHECKOUT: COMPLETE!\"]");
    // the app's own text has a typo ("YOU ORDER"), so match only the stable start
    private final By thankYouText = By.xpath("//android.widget.TextView[contains(@text,\"THANK YOU\")]");
    private final By backHome     = AppiumBy.accessibilityId("test-BACK HOME");

    public void waitUntilLoaded() {
        waitForVisibility(screenTitle, 15);
    }

    public boolean isLoaded() {
        return isElementVisible(screenTitle, 10);
    }

    public boolean isThankYouDisplayed() {
        return isElementVisible(thankYouText, 5);
    }

    @Step("Back home")
    public ProductsPage backHome() {
        scrollUntilVisible(backHome, 3);
        click(backHome);
        ProductsPage products = new ProductsPage();
        products.waitUntilLoaded();
        return products;
    }
}
