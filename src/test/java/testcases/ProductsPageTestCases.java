package testcases;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.ProductsPage.SortOption;
import utilities.Product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Epic("Swag Labs")
@Feature("Products")
public class ProductsPageTestCases extends AuthenticatedTest {

    @Severity(SeverityLevel.CRITICAL)
    @Test(priority = 1, description = "Products page shows header controls", groups = {"regression", "smoke"})
    public void productsPageAppearanceTest() {
        SoftAssert soft = new SoftAssert();
        soft.assertTrue(productsPage.isLoaded(), "PRODUCTS title");
        soft.assertTrue(productsPage.isMenuButtonDisplayed(), "Menu button");
        soft.assertTrue(productsPage.isCartButtonDisplayed(), "Cart button");
        soft.assertTrue(productsPage.isSortButtonDisplayed(), "Sort button");
        soft.assertTrue(productsPage.isLayoutToggleDisplayed(), "Layout toggle");
        soft.assertAll();
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(priority = 2, description = "All 6 expected products are listed", groups = {"regression"})
    public void allProductsListedTest() {
        List<String> expected = Arrays.stream(Product.values()).map(Product::displayName).collect(Collectors.toList());
        List<String> actual = productsPage.getAllProductNames();

        Assert.assertEquals(new HashSet<>(actual), new HashSet<>(expected), "Products shown in the list");
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(priority = 3, description = "Adding one product updates the cart badge and swaps ADD with REMOVE", groups = {"regression", "smoke"})
    public void addSingleProductTest() {
        productsPage.addToCart(Product.BACKPACK);

        SoftAssert soft = new SoftAssert();
        soft.assertEquals(productsPage.getCartBadgeCount(), 1, "Cart badge");
        soft.assertTrue(productsPage.isRemoveButtonDisplayed(Product.BACKPACK), "REMOVE button");
        soft.assertAll();
    }

    @Severity(SeverityLevel.NORMAL)
    @Test(priority = 4, description = "Adding several products increments the cart badge", groups = {"regression"})
    public void addMultipleProductsTest() {
        productsPage.addToCart(Product.BACKPACK)
                .addToCart(Product.BIKE_LIGHT)
                .addToCart(Product.ONESIE);

        Assert.assertEquals(productsPage.getCartBadgeCount(), 3, "Cart badge");
    }

    @Severity(SeverityLevel.NORMAL)
    @Test(priority = 5, description = "Removing a product from the list clears the badge", groups = {"regression"})
    public void removeProductFromListTest() {
        productsPage.addToCart(Product.BACKPACK);
        productsPage.removeFromCart(Product.BACKPACK);

        SoftAssert soft = new SoftAssert();
        soft.assertEquals(productsPage.getCartBadgeCount(), 0, "Cart badge");
        soft.assertTrue(productsPage.isAddButtonDisplayed(Product.BACKPACK), "ADD TO CART button is back");
        soft.assertAll();
    }

    @Severity(SeverityLevel.NORMAL)
    @Test(priority = 6, description = "Sort by name Z to A, then A to Z", groups = {"regression"})
    public void sortByNameTest() {
        List<String> ascending = Arrays.stream(Product.values()).map(Product::displayName).sorted().collect(Collectors.toList());
        List<String> descending = new ArrayList<>(ascending);
        java.util.Collections.reverse(descending);

        // Z-A first: A-Z is the default order, so testing it alone could never fail
        productsPage.sortBy(SortOption.NAME_DESC);
        Assert.assertEquals(productsPage.getAllProductNames(), descending, "Name (Z to A)");

        productsPage.sortBy(SortOption.NAME_ASC);
        Assert.assertEquals(productsPage.getAllProductNames(), ascending, "Name (A to Z)");
    }

    @Severity(SeverityLevel.NORMAL)
    @Test(priority = 7, description = "Sort by price low to high and high to low", groups = {"regression"})
    public void sortByPriceTest() {
        productsPage.sortBy(SortOption.PRICE_LOW_HIGH);
        List<Double> low = prices(productsPage.getAllProductNames());
        Assert.assertEquals(low.size(), Product.values().length, "All products listed");
        Assert.assertTrue(isNonDecreasing(low), "Price (low to high) but got " + low);

        productsPage.sortBy(SortOption.PRICE_HIGH_LOW);
        List<Double> high = prices(productsPage.getAllProductNames());
        List<Double> reversed = new ArrayList<>(high);
        java.util.Collections.reverse(reversed);
        Assert.assertTrue(isNonDecreasing(reversed), "Price (high to low) but got " + high);
    }

    // Two products cost 15.99, so compare prices (not names) to avoid tie-order flakiness.
    private List<Double> prices(List<String> names) {
        return names.stream().map(n -> Product.fromName(n).price()).collect(Collectors.toList());
    }

    private boolean isNonDecreasing(List<Double> values) {
        for (int i = 1; i < values.size(); i++) {
            if (values.get(i) < values.get(i - 1)) return false;
        }
        return true;
    }
}
