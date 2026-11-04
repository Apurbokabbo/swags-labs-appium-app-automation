package pages;

import io.appium.java_client.AppiumBy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class ProductDetailsPage extends BasePage {

    private final By backButton  = AppiumBy.accessibilityId("test-BACK TO PRODUCTS");
    private final By name        = By.xpath("//android.view.ViewGroup[@content-desc=\"test-Description\"]/android.widget.TextView[1]");
    private final By description = By.xpath("//android.view.ViewGroup[@content-desc=\"test-Description\"]/android.widget.TextView[2]");
    private final By price       = AppiumBy.accessibilityId("test-Price");
    private final By addButton   = AppiumBy.accessibilityId("test-ADD TO CART");
    private final By removeButton = AppiumBy.accessibilityId("test-REMOVE");

    public void waitUntilLoaded() {
        waitForVisibility(backButton, 15);
    }

    public boolean isLoaded() {
        return isElementVisible(backButton, 10);
    }

    public String getName()        { return getText(name); }
    public String getDescription() { return getText(description); }

    public double getPrice() {
        scrollUntilVisible(price, 3);
        return Double.parseDouble(getText(price).replaceAll("[^0-9.]", ""));
    }

    public boolean isRemoveButtonDisplayed() {
        return scrollUntilVisible(removeButton, 3);
    }

    @Step("Add product to cart from details screen")
    public ProductDetailsPage addToCart() {
        scrollUntilVisible(addButton, 3);
        click(addButton);
        return this;
    }

    @Step("Remove product from cart from details screen")
    public ProductDetailsPage removeFromCart() {
        scrollUntilVisible(removeButton, 3);
        click(removeButton);
        return this;
    }

    @Step("Back to products")
    public ProductsPage backToProducts() {
        click(backButton);
        ProductsPage products = new ProductsPage();
        products.waitUntilLoaded();
        return products;
    }
}
