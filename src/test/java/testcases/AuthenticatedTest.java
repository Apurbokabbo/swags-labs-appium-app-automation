package testcases;

import org.testng.annotations.BeforeMethod;
import pages.LoginPage;
import pages.ProductsPage;
import utilities.BaseDriver;

/**
 * Base class for every test that needs a logged-in user.
 * TestNG runs BaseDriver.setDriver() (superclass) first, then this method.
 */
public abstract class AuthenticatedTest extends BaseDriver {

    protected final LoginPage loginPage = new LoginPage();
    protected ProductsPage productsPage;

    @BeforeMethod(alwaysRun = true)
    public void loginAsStandardUser() {
        productsPage = loginPage.loginAsStandardUser();
    }
}
