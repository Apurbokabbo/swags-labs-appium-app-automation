package testcases;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.LoginPage;
import pages.ProductsPage;
import utilities.BaseDriver;
import utilities.TestData;

@Epic("Swag Labs")
@Feature("Login")
public class LoginPageTestCases extends BaseDriver {

    private final LoginPage loginPage = new LoginPage();

    @Severity(SeverityLevel.CRITICAL)
    @Test(priority = 1, description = "Login page shows username, password and login button", groups = {"regression", "smoke"})
    public void loginPageAppearanceTest() {
        Assert.assertTrue(loginPage.isLoaded(), "Login page did not load all key elements");
    }

    @Severity(SeverityLevel.NORMAL)
    @Test(priority = 2, description = "Login page placeholders and info texts are correct", groups = {"regression"})
    public void loginPageTextAssertionTest() {
        Assert.assertTrue(loginPage.isLoaded(), "Login page did not load");

        SoftAssert soft = new SoftAssert();
        soft.assertEquals(loginPage.getUsernamePlaceholder(), LoginPage.USERNAME_PLACEHOLDER, "Username placeholder");
        soft.assertEquals(loginPage.getPasswordPlaceholder(), LoginPage.PASSWORD_PLACEHOLDER, "Password placeholder");
        soft.assertEquals(loginPage.getUsernameInfoText(), LoginPage.USERNAME_INFO_TEXT, "Usernames info text");
        soft.assertEquals(loginPage.getPasswordInfoText(), LoginPage.PASSWORD_INFO_TEXT, "Password info text");
        soft.assertAll();
    }

    @Severity(SeverityLevel.BLOCKER)
    @Test(priority = 3, description = "Standard user can log in with valid credentials", groups = {"regression", "smoke"})
    public void validLoginTest() {
        loginPage.login(TestData.standardUser(), TestData.password());

        ProductsPage products = new ProductsPage();
        Assert.assertTrue(products.isLoaded(), "Products page should open after a valid login");
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(priority = 4, description = "Locked out user sees the locked-out error", groups = {"regression", "smoke"})
    public void lockedOutUserTest() {
        loginPage.login(TestData.lockedOutUser(), TestData.password());

        Assert.assertEquals(loginPage.getErrorMessage(), LoginPage.LOCKED_OUT);
    }

    @DataProvider(name = "invalidLogins")
    public Object[][] invalidLogins() {
        return new Object[][]{
                // username,                   password,                  expected error
                {"",                           "",                        LoginPage.USERNAME_REQUIRED},
                {"",                           TestData.password(),       LoginPage.USERNAME_REQUIRED},
                {TestData.standardUser(),      "",                        LoginPage.PASSWORD_REQUIRED},
                {TestData.standardUser(),      "wrong_password",          LoginPage.INVALID_CREDENTIALS},
                {"not_a_user",                 TestData.password(),       LoginPage.INVALID_CREDENTIALS},
                {"STANDARD_USER",              TestData.password(),       LoginPage.INVALID_CREDENTIALS}, // case-sensitive
                {" " + "standard_user",        TestData.password(),       LoginPage.INVALID_CREDENTIALS}, // leading space
        };
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(priority = 5, dataProvider = "invalidLogins", description = "Invalid or missing credentials show the right error", groups = {"regression", })
    public void invalidLoginTest(String username, String password, String expectedError) {
        loginPage.login(username, password);

        Assert.assertEquals(loginPage.getErrorMessage(), expectedError,
                "Wrong error for username=[" + username + "] password=[" + password + "]");
    }

    @Severity(SeverityLevel.MINOR)
    @Test(priority = 6, description = "Tapping the standard_user hint autofills the form",
            groups = {"regression"})
    public void autofillStandardUserTest() {
        loginPage.tapStandardUserHint();

        SoftAssert soft = new SoftAssert();
        soft.assertEquals(loginPage.getUsernameValue(), TestData.standardUser(), "Username autofill");
        soft.assertFalse(loginPage.isPasswordEmpty(), "Password should be autofilled");
        soft.assertAll();
    }
}
