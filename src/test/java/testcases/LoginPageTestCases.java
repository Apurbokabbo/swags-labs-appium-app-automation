package testcases;

import org.testng.annotations.Test;
import pages.LoginPage;
import utilities.BaseDriver;

public class LoginPageTestCases extends BaseDriver {

    LoginPage loginPageObj = new LoginPage();

    @Test
    public void loginPageAppeatanceTest() throws InterruptedException {
        loginPageObj.isElementVisible(loginPageObj.user_name_input_field,6);
        loginPageObj.isElementVisible(loginPageObj.password_input_field,6);
        loginPageObj.isElementVisible(loginPageObj.login_button,6);
    }

}
