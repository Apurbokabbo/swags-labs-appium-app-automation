package testcases;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.MenuPage;

@Epic("Swag Labs")
@Feature("Side menu")
public class MenuTestCases extends AuthenticatedTest {

    @Severity(SeverityLevel.NORMAL)
    @Test(priority = 1, description = "Side menu shows its main entries",
            groups = {"regression"})
    public void menuItemsDisplayedTest() {
        MenuPage menu = productsPage.openMenu();

        SoftAssert soft = new SoftAssert();
        soft.assertTrue(menu.isOpen(), "Menu is open");
        soft.assertTrue(menu.isAllItemsDisplayed(), "ALL ITEMS");
        soft.assertTrue(menu.isAboutDisplayed(), "ABOUT");
        soft.assertTrue(menu.isLogoutDisplayed(), "LOGOUT");
        soft.assertTrue(menu.isResetAppStateDisplayed(), "RESET APP STATE");
        soft.assertAll();
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(priority = 2, description = "Logout returns to the login screen",
            groups = {"regression", "smoke"})
    public void logoutTest() {
        Assert.assertTrue(productsPage.openMenu().logout().isLoaded(), "Login page should be shown after logout");
    }

    @Severity(SeverityLevel.NORMAL)
    @Test(priority = 3, description = "ALL ITEMS from the cart screen goes back to products",
            groups = {"regression"})
    public void allItemsNavigationTest() {
        MenuPage menu = productsPage.openCart().openMenu();

        Assert.assertTrue(menu.openAllItems().isLoaded(), "Products page should be shown");
    }
}
