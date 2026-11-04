package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class MenuPage extends BasePage {

    private final By allItems       = menuItem("ALL ITEMS");
    private final By about          = menuItem("ABOUT");
    private final By logout         = menuItem("LOGOUT");
    private final By resetAppState  = menuItem("RESET APP STATE");

    private By menuItem(String label) {
        return By.xpath("//android.widget.TextView[@text=\"" + label + "\"]");
    }

    public boolean isOpen() {
        return isElementVisible(logout, 10);
    }

    public boolean isAllItemsDisplayed()      { return isElementVisible(allItems, 5); }
    public boolean isAboutDisplayed()         { return isElementVisible(about, 5); }
    public boolean isLogoutDisplayed()        { return isElementVisible(logout, 5); }
    public boolean isResetAppStateDisplayed() { return isElementVisible(resetAppState, 5); }

    @Step("Menu > ALL ITEMS")
    public ProductsPage openAllItems() {
        click(allItems);
        ProductsPage products = new ProductsPage();
        products.waitUntilLoaded();
        return products;
    }

    @Step("Menu > LOGOUT")
    public LoginPage logout() {
        click(logout);
        return new LoginPage();
    }
}
