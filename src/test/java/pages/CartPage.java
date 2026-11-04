package pages;

import io.appium.java_client.AppiumBy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import utilities.Product;

import java.util.ArrayList;
import java.util.List;

public class CartPage extends BasePage {

    private final By screenTitle      = By.xpath("//android.widget.TextView[@text=\"YOUR CART\"]");
    private final By itemTitles       = AppiumBy.accessibilityId("test-Item title");
    private final By continueShopping = AppiumBy.accessibilityId("test-CONTINUE SHOPPING");
    private final By checkoutButton   = AppiumBy.accessibilityId("test-CHECKOUT");

    private By removeButtonFor(Product product) {
        return By.xpath("//android.widget.TextView[@content-desc=\"test-Item title\" and @text=\"" + product.displayName() + "\"]"
                + "/ancestor::android.view.ViewGroup[.//android.view.ViewGroup[@content-desc=\"test-REMOVE\"]][1]"
                + "//android.view.ViewGroup[@content-desc=\"test-REMOVE\"]");
    }

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

    @Step("Remove '{product}' from the cart")
    public CartPage removeItem(Product product) {
        click(removeButtonFor(product));
        return this;
    }

    @Step("Open side menu from cart")
    public MenuPage openMenu() {
        click(menu_button);
        return new MenuPage();
    }

    @Step("Continue shopping")
    public ProductsPage continueShopping() {
        scrollUntilVisible(continueShopping, 3);
        click(continueShopping);
        ProductsPage products = new ProductsPage();
        products.waitUntilLoaded();
        return products;
    }

    @Step("Proceed to checkout")
    public CheckoutInformationPage checkout() {
        scrollUntilVisible(checkoutButton, 3);
        click(checkoutButton);
        CheckoutInformationPage info = new CheckoutInformationPage();
        info.waitUntilLoaded();
        return info;
    }
}
