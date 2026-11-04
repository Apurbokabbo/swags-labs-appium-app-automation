package pages;

import io.appium.java_client.AppiumBy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

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
        List<String> names = new ArrayList<>();
        for (WebElement el : findElements(itemTitles)) {
            String text = el.getText();
            if (text != null && !text.trim().isEmpty()) names.add(text.trim());
        }
        return names;
    }

    private double money(By locator) {
        scrollUntilVisible(locator, 4);
        return Double.parseDouble(getText(locator).replaceAll("[^0-9.]", ""));
    }

    public double getItemTotal() { return money(itemTotal); }
    public double getTax()       { return money(tax); }
    public double getTotal()     { return money(total); }

    @Step("Finish the order")
    public CheckoutCompletePage finish() {
        scrollUntilVisible(finishButton, 5);
        click(finishButton);
        CheckoutCompletePage complete = new CheckoutCompletePage();
        complete.waitUntilLoaded();
        return complete;
    }
}
