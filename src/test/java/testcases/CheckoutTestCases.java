package testcases;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.CheckoutCompletePage;
import pages.CheckoutInformationPage;
import pages.CheckoutOverviewPage;
import utilities.Product;
import utilities.TestData;

import java.util.Arrays;
import java.util.HashSet;

@Epic("Swag Labs")
@Feature("Checkout")
public class CheckoutTestCases extends AuthenticatedTest {

    /** Adds the products and walks to the CHECKOUT: INFORMATION screen. */
    private CheckoutInformationPage goToCheckoutInfo(Product... items) {
        for (Product p : items) productsPage.addToCart(p);
        return productsPage.openCart().checkout();
    }

    @DataProvider(name = "missingCheckoutFields")
    public Object[][] missingCheckoutFields() {
        return new Object[][]{
                // first, last, zip, expected error
                {"",                  TestData.LAST_NAME, TestData.ZIP_CODE, CheckoutInformationPage.FIRST_NAME_REQUIRED},
                {TestData.FIRST_NAME, "",                 TestData.ZIP_CODE, CheckoutInformationPage.LAST_NAME_REQUIRED},
                {TestData.FIRST_NAME, TestData.LAST_NAME, "",                CheckoutInformationPage.POSTAL_CODE_REQUIRED},
                {"",                  "",                 "",                CheckoutInformationPage.FIRST_NAME_REQUIRED},
        };
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(priority = 1, dataProvider = "missingCheckoutFields",
            description = "Checkout form validates required fields",
            groups = {"regression", "negative"})
    public void requiredFieldValidationTest(String first, String last, String zip, String expectedError) {
        CheckoutInformationPage info = goToCheckoutInfo(Product.BACKPACK);

        info.fillInformation(first, last, zip).tapContinue();

        Assert.assertEquals(info.getErrorMessage(), expectedError);
    }

    @Severity(SeverityLevel.NORMAL)
    @Test(priority = 2, description = "Cancel on the information screen returns to the cart",
            groups = {"regression"})
    public void cancelReturnsToCartTest() {
        CheckoutInformationPage info = goToCheckoutInfo(Product.BACKPACK);

        Assert.assertTrue(info.cancel().isLoaded(), "Cart should be shown after Cancel");
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(priority = 3, description = "Overview lists the items and the totals add up",
            groups = {"regression"})
    public void overviewTotalsTest() {
        CheckoutOverviewPage overview = goToCheckoutInfo(Product.BACKPACK, Product.BIKE_LIGHT)
                .continueToOverview(TestData.FIRST_NAME, TestData.LAST_NAME, TestData.ZIP_CODE);

        double expectedItemTotal = Product.BACKPACK.price() + Product.BIKE_LIGHT.price();

        SoftAssert soft = new SoftAssert();
        soft.assertEquals(new HashSet<>(overview.getItemNames()),
                new HashSet<>(Arrays.asList(Product.BACKPACK.displayName(), Product.BIKE_LIGHT.displayName())),
                "Items on overview");
        double itemTotal = overview.getItemTotal();
        double tax = overview.getTax();
        soft.assertEquals(itemTotal, expectedItemTotal, 0.001, "Item total");
        soft.assertEquals(overview.getTotal(), itemTotal + tax, 0.011, "Total = item total + tax");
        soft.assertAll();
    }

    @Severity(SeverityLevel.BLOCKER)
    @Test(priority = 4, description = "E2E: complete a purchase and return home with an empty cart",
            groups = {"regression", "smoke"})
    public void completePurchaseTest() {
        CheckoutCompletePage complete = goToCheckoutInfo(Product.BACKPACK)
                .continueToOverview(TestData.FIRST_NAME, TestData.LAST_NAME, TestData.ZIP_CODE)
                .finish();

        Assert.assertTrue(complete.isThankYouDisplayed(), "Thank-you message");

        productsPage = complete.backHome();
        Assert.assertEquals(productsPage.getCartBadgeCount(), 0, "Cart should be empty after the order");
    }
}
