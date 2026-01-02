package testcases;

import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.Test;
import pages.LoginPage;
import utilities.BaseDriver;

public class LoginPageTestCases extends BaseDriver {

    LoginPage loginPageObj = new LoginPage();

    @Severity(SeverityLevel.CRITICAL)
    @Test (priority = 1, description = "Login Page Appearance Test", groups = {"regression","smoke"})
    public void loginPageAppeatanceTest() throws InterruptedException {
        loginPageObj.isElementVisible(loginPageObj.user_name_input_field,6);
        loginPageObj.isElementVisible(loginPageObj.password_input_field,6);
        loginPageObj.isElementVisible(loginPageObj.login_button,6);
    }

    @Test (priority = 2, description = "Login Page Text Assertion Test", groups = {"regression"})
    public void loginPageTextAssertionTest() throws InterruptedException {
        loginPageObj.assertText(loginPageObj.user_name_input_field, loginPageObj.user_input_field_placeholder_text);
        loginPageObj.assertText(loginPageObj.password_input_field, loginPageObj.password_input_field_placeholder_text);
        try {
            loginPageObj.findElement(loginPageObj.password_info_text_locator);

        } catch (Exception e) {
            loginPageObj.scrollDownBlindly(1);
            loginPageObj.assertText(loginPageObj.password_info_text_locator, loginPageObj.password_info_text);
            loginPageObj.assertText(loginPageObj.user_name_info_text_locator, loginPageObj.user_name_info_text);


        }
    }

}
