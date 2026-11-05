package testcases;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.ProductDetailsPage;
import utilities.Product;

@Epic("Swag Labs")
@Feature("Product details")
public class ProductDetailsTestCases extends AuthenticatedTest {

    @Severity(SeverityLevel.CRITICAL)
    @Test(priority = 1, description = "Details screen shows the same name and price as the catalog",
            groups = {"regression", "smoke"})
    public void productDetailsContentTest() {
        ProductDetailsPage details = productsPage.openDetails(Product.BACKPACK);

        SoftAssert soft = new SoftAssert();
        soft.assertEquals(details.getName(), Product.BACKPACK.displayName(), "Name");
        soft.assertEquals(details.getPrice(), Product.BACKPACK.price(), 0.001, "Price");
        soft.assertFalse(details.getDescription().isEmpty(), "Description should not be empty");
        soft.assertAll();
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(priority = 2, description = "Add to cart from the details screen is reflected in the list",
            groups = {"regression"})
    public void addToCartFromDetailsTest() {
        ProductDetailsPage details = productsPage.openDetails(Product.BIKE_LIGHT);
        details.addToCart();

        SoftAssert soft = new SoftAssert();
        soft.assertEquals(details.getCartBadgeCount(), 1, "Cart badge on details");
        soft.assertTrue(details.isRemoveButtonDisplayed(), "REMOVE on details");

        productsPage = details.backToProducts();
        soft.assertTrue(productsPage.isRemoveButtonDisplayed(Product.BIKE_LIGHT), "REMOVE in the list");
        soft.assertAll();
    }

    @Severity(SeverityLevel.NORMAL)
    @Test(priority = 3, description = "Remove from the details screen clears the badge",
            groups = {"regression"})
    public void removeFromDetailsTest() {
        ProductDetailsPage details = productsPage.openDetails(Product.ONESIE);
        details.addToCart().removeFromCart();

        Assert.assertEquals(details.getCartBadgeCount(), 0, "Cart badge");
    }

    @Severity(SeverityLevel.NORMAL)
    @Test(priority = 4, description = "Back button returns to the products list",
            groups = {"regression"})
    public void backToProductsTest() {
        ProductDetailsPage details = productsPage.openDetails(Product.FLEECE_JACKET);

        Assert.assertTrue(details.backToProducts().isLoaded(), "Products page should be shown");
    }
}
