package pages;

import io.appium.java_client.AppiumBy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import utilities.Product;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CheckoutOverviewPage extends BasePage {

    private final By screenTitle  = By.xpath("//android.widget.TextView[@text=\"CHECKOUT: OVERVIEW\"]");
    private final By itemTitles   = AppiumBy.accessibilityId("test-Item title");
    private final By itemTotal    = By.xpath("//android.widget.TextView[starts-with(@text,\"Item total\")]");
    private final By tax          = By.xpath("//android.widget.TextView[starts-with(@text,\"Tax\")]");
    private final By total        = By.xpath("//android.widget.TextView[starts-with(@text,\"Total:\")]");
    private final By finishButton = AppiumBy.accessibilityId("test-FINISH");

    public void waitUntilLoaded() {

        waitForVisibility(screenTitle, 15);
    }

    public boolean isLoaded() {

        return isElementVisible(screenTitle, 10);
    }

    public List<String> getItemNames() {
        Set<String> found = new LinkedHashSet<>();
        for (int i = 0; i < 4; i++) {                       // check each screen while scrolling down
            for (Product product : Product.values()) {
                By title = By.xpath("//android.widget.TextView[@text=\"" + product.displayName() + "\"]");
                if (!findElements(title).isEmpty()) {
                    found.add(product.displayName());
                }
            }
            scrollDown();
        }
        return new ArrayList<>(found);
    }

    private double money(String labelStart) {
        ensureInitialized();
        WebElement line = driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true).instance(0))"
                        + ".scrollIntoView(new UiSelector().textStartsWith(\"" + labelStart + "\"))"));
        return Double.parseDouble(line.getText().replaceAll("[^0-9.]", ""));
    }

    public double getItemTotal() { return money("Item total"); }
    public double getTax()       { return money("Tax"); }
    public double getTotal()     { return money("Total:"); }

    @Step("Finish the order")
    public CheckoutCompletePage finish() {
        scrollUntilVisible(finishButton, 5);
        click(finishButton);
        CheckoutCompletePage complete = new CheckoutCompletePage();
        complete.waitUntilLoaded();
        return complete;
    }
}
