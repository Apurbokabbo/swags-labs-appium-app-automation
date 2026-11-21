package pages;

import io.appium.java_client.AppiumBy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import utilities.Product;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    /** Run with -Ddebug=true to print what the list scrolling sees. */
    private static final boolean DEBUG = Boolean.getBoolean("debug");

    private static final int MAX_SCROLL_PASSES = 15;

    private final By screenTitle   = By.xpath("//android.widget.TextView[@text=\"PRODUCTS\"]");
    private final By productTitles = AppiumBy.accessibilityId("test-Item title");
    private final By layoutToggle  = AppiumBy.accessibilityId("test-Toggle");

    // ── Dynamic locators ──────────────────────────────────────────────────────
    private By productTitle(String name) {
        return By.xpath("//android.widget.TextView[@content-desc=\"test-Item title\" and @text=\"" + name + "\"]");
    }

    /** The button (ADD TO CART / REMOVE) that belongs to the same card as the product title. */
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

    /**
     * Scrolls the (lazily rendered) list from top to bottom and returns every
     * product name in on-screen order.
     */
    public List<String> getAllProductNames() {
        Set<String> names = new LinkedHashSet<>();

        scrollListToTop();

        int pass = 0;
        int passesWithoutNewItems = 0;
        boolean canScrollMore;
        do {
            int before = names.size();
            collectVisibleProductNames(names);
            passesWithoutNewItems = (names.size() == before) ? passesWithoutNewItems + 1 : 0;

            canScrollMore = scrollList("down", 0.5);
            pause(700); // let the list render the newly revealed cards
            debug("pass " + (++pass) + " canScrollMore=" + canScrollMore + " names=" + names);
        } while (canScrollMore && passesWithoutNewItems < 3 && pass < MAX_SCROLL_PASSES);

        collectVisibleProductNames(names); // last screen after the final scroll
        debug("FINAL names=" + names);
        return new ArrayList<>(names);
    }

    private void collectVisibleProductNames(Set<String> names) {
        for (WebElement el : findElements(productTitles)) {
            String text = el.getText();
            if (text != null && !text.trim().isEmpty()) {
                names.add(text.trim());
            }
        }
    }

    public void scrollListToTop() {
        for (int i = 0; i < 5 && scrollList("up", 1.0); i++) {
            pause(300);
        }
    }

    /** Native scroll of the real list view; returns true if it can still scroll in that direction. */
    private boolean scrollList(String direction, double percent) {
        ensureInitialized();
        try {
            List<WebElement> scrollables = driver.findElements(
                    AppiumBy.androidUIAutomator("new UiSelector().scrollable(true)"));
            debug("scrollable elements on screen: " + scrollables.size());
            if (scrollables.isEmpty()) {
                return false;
            }
            WebElement list = scrollables.get(0);
            debug("scrolling " + direction + " on class=" + list.getAttribute("class")
                    + " desc=" + list.getAttribute("content-desc"));
            Object result = driver.executeScript("mobile: scrollGesture", Map.of(
                    "elementId", ((RemoteWebElement) list).getId(),
                    "direction", direction,
                    "percent", percent));
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            debug("scroll failed: " + e);
            return false;
        }
    }

    private void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void debug(String message) {
        if (DEBUG) System.out.println("[ProductsPage] " + message);
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

    // ── Sort-check helpers ────────────────────────────────────────────────────
    // Two products cost 15.99, so compare prices (not names) to avoid tie-order flakiness.
    public List<Double> prices(List<String> names) {
        return names.stream().map(n -> Product.fromName(n).price()).collect(Collectors.toList());
    }

    public boolean isNonDecreasing(List<Double> values) {
        for (int i = 1; i < values.size(); i++) {
            if (values.get(i) < values.get(i - 1)) return false;
        }
        return true;
    }
}