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

public class ProductsPage extends BasePage {

    public enum SortOption {
        NAME_ASC("Name (A to Z)"),
        NAME_DESC("Name (Z to A)"),
        PRICE_LOW_HIGH("Price (low to high)"),
        PRICE_HIGH_LOW("Price (high to low)");

        private final String label;
        SortOption(String label) { this.label = label; }
        public String label() { return label; }
    }

    private static final String ADD_DESC    = "test-ADD TO CART";
    private static final String REMOVE_DESC = "test-REMOVE";

    private final By screenTitle   = By.xpath("//android.widget.TextView[@text=\"PRODUCTS\"]");
    private final By productTitles = AppiumBy.accessibilityId("test-Item title");
    private final By sortButton    = AppiumBy.accessibilityId("test-Modal Selector Button");
    private final By layoutToggle  = AppiumBy.accessibilityId("test-Toggle");

    // ── Dynamic locators ──────────────────────────────────────────────────────
    private By productTitle(String name) {
        return By.xpath("//android.widget.TextView[@content-desc=\"test-Item title\" and @text=\"" + name + "\"]");
    }

    /** Finds the button (ADD TO CART / REMOVE) that belongs to the same card as the product title. */
    private By productButton(String name, String buttonDesc) {
        return By.xpath("//android.widget.TextView[@content-desc=\"test-Item title\" and @text=\"" + name + "\"]"
                + "/ancestor::android.view.ViewGroup[.//android.view.ViewGroup[@content-desc=\"" + buttonDesc + "\"]][1]"
                + "//android.view.ViewGroup[@content-desc=\"" + buttonDesc + "\"]");
    }

    private By sortOptionLocator(SortOption option) {
        return By.xpath("//android.widget.TextView[@text=\"" + option.label() + "\"]");
    }

    private void scrollToProduct(String name) {
        if (!isElementVisible(productTitle(name), 1)) {
            scrollToText(name);
        }
    }

    // ── State checks ──────────────────────────────────────────────────────────
    public void waitUntilLoaded() {
        waitForVisibility(screenTitle, 20);
    }

    public boolean isLoaded() {
        return isElementVisible(screenTitle, 15);
    }

    public boolean isSortButtonDisplayed()   { return isElementVisible(sortButton, 5); }
    public boolean isLayoutToggleDisplayed() { return isElementVisible(layoutToggle, 5); }
    public boolean isMenuButtonDisplayed()   { return isElementVisible(menu_button, 5); }
    public boolean isCartButtonDisplayed()   { return isElementVisible(cart_button, 5); }

    public boolean isRemoveButtonDisplayed(Product product) {
        scrollToProduct(product.displayName());
        return isElementVisible(productButton(product.displayName(), REMOVE_DESC), 3);
    }

    public boolean isAddButtonDisplayed(Product product) {
        scrollToProduct(product.displayName());
        return isElementVisible(productButton(product.displayName(), ADD_DESC), 3);
    }

    // ── Reading ───────────────────────────────────────────────────────────────
    /** Scrolls through the (virtualised) list and returns every product name in on-screen order. */
    public List<String> getAllProductNames() {
        Set<String> names = new LinkedHashSet<>();
        int rounds = 0;
        int noNewItemRounds = 0;
        while (rounds++ < 10 && noNewItemRounds < 2) {
            int before = names.size();
            for (WebElement el : findElements(productTitles)) {
                String text = el.getText();
                if (text != null && !text.trim().isEmpty()) names.add(text.trim());
            }
            noNewItemRounds = (names.size() == before) ? noNewItemRounds + 1 : 0;
            scrollDown();
        }
        return new ArrayList<>(names);
    }

    // ── Actions ───────────────────────────────────────────────────────────────
    @Step("Add '{product}' to cart")
    public ProductsPage addToCart(Product product) {
        scrollToProduct(product.displayName());
        click(productButton(product.displayName(), ADD_DESC));
        return this;
    }

    @Step("Remove '{product}' from the products list")
    public ProductsPage removeFromCart(Product product) {
        scrollToProduct(product.displayName());
        click(productButton(product.displayName(), REMOVE_DESC));
        return this;
    }

    @Step("Sort products by {option}")
    public ProductsPage sortBy(SortOption option) {
        click(sortButton);
        click(sortOptionLocator(option));
        return this;
    }

    @Step("Open details of '{product}'")
    public ProductDetailsPage openDetails(Product product) {
        scrollToProduct(product.displayName());
        click(productTitle(product.displayName()));
        ProductDetailsPage details = new ProductDetailsPage();
        details.waitUntilLoaded();
        return details;
    }

    @Step("Open cart")
    public CartPage openCart() {
        click(cart_button);
        CartPage cart = new CartPage();
        cart.waitUntilLoaded();
        return cart;
    }

    @Step("Open side menu")
    public MenuPage openMenu() {
        click(menu_button);
        return new MenuPage();
    }
}
