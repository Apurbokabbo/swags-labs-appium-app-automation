package testcases;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.CartPage;
import utilities.Product;

import java.util.Arrays;
import java.util.HashSet;

@Epic("Swag Labs")
@Feature("Cart")
public class CartPageTestCases extends AuthenticatedTest {

    @Severity(SeverityLevel.NORMAL)
    @Test(priority = 1, description = "Empty cart has no items and Continue Shopping works",
            groups = {"regression"})
    public void emptyCartTest() {
        CartPage cart = productsPage.openCart();

        SoftAssert soft = new SoftAssert();
        soft.assertTrue(cart.isLoaded(), "YOUR CART title");
        soft.assertTrue(cart.getItemNames().isEmpty(), "Cart should be empty");
        soft.assertTrue(cart.continueShopping().isLoaded(), "Continue Shopping returns to products");
        soft.assertAll();
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(priority = 2, description = "Cart lists exactly the products that were added",
            groups = {"regression", "smoke"})
    public void cartShowsAddedProductsTest() {
        productsPage.addToCart(Product.BACKPACK).addToCart(Product.BIKE_LIGHT);

        CartPage cart = productsPage.openCart();

        Assert.assertEquals(new HashSet<>(cart.getItemNames()),
                new HashSet<>(Arrays.asList(Product.BACKPACK.displayName(), Product.BIKE_LIGHT.displayName())),
                "Items in cart");
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(priority = 3, description = "Removing one item leaves the other and updates the badge",
            groups = {"regression"})
    public void removeOneItemFromCartTest() {
        productsPage.addToCart(Product.BACKPACK).addToCart(Product.BIKE_LIGHT);
        CartPage cart = productsPage.openCart();

        cart.removeItem(Product.BACKPACK);

        SoftAssert soft = new SoftAssert();
        soft.assertEquals(cart.getItemNames(), Arrays.asList(Product.BIKE_LIGHT.displayName()), "Remaining items");
        soft.assertEquals(cart.getCartBadgeCount(), 1, "Cart badge");
        soft.assertAll();
    }

    @Severity(SeverityLevel.NORMAL)
    @Test(priority = 4, description = "Removing the last item empties the cart and hides the badge",
            groups = {"regression"})
    public void removeLastItemTest() {
        productsPage.addToCart(Product.ONESIE);
        CartPage cart = productsPage.openCart();

        cart.removeItem(Product.ONESIE);

        SoftAssert soft = new SoftAssert();
        soft.assertTrue(cart.getItemNames().isEmpty(), "Cart should be empty");
        soft.assertEquals(cart.getCartBadgeCount(), 0, "Cart badge");
        soft.assertAll();
    }

    @Severity(SeverityLevel.NORMAL)
    @Test(priority = 5, description = "Continue Shopping keeps the cart content",
            groups = {"regression"})
    public void continueShoppingKeepsCartTest() {
        productsPage.addToCart(Product.BOLT_TSHIRT);

        productsPage = productsPage.openCart().continueShopping();

        Assert.assertEquals(productsPage.getCartBadgeCount(), 1, "Cart badge after returning");
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(priority = 6, description = "Checkout button opens the information form",
            groups = {"regression"})
    public void checkoutOpensInformationPageTest() {
        productsPage.addToCart(Product.BACKPACK);

        Assert.assertTrue(productsPage.openCart().checkout().isLoaded(),
                "CHECKOUT: INFORMATION should open");
    }
}
