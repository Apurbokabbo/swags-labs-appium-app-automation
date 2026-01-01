package pages;

import org.openqa.selenium.By;

public class LoginPage extends BasePage {

    public String user_input_field_placeholder_text = "Username";
    public String password_input_field_placeholder_text = "Password";
    public String password_info_text = "And the password for all users is:";
    public String user_name_info_text = "The currently accepted usernames for this application are (tap to autofill):";
    public String user_name_required_error_message = "Username is required";
    public String password_required_error_message = "Password is required";
    public String invalid_credentials_error_message = "Username and password do not match any user in this service";


   public By user_name_input_field = By.xpath("//android.widget.EditText[@content-desc=\"test-Username\"]");
   public By password_input_field = By.xpath("//android.widget.EditText[@content-desc=\"test-Password\"]");
   public By login_button = By.xpath("//android.view.ViewGroup[@content-desc=\"test-LOGIN\"]");
   public By face_recognition_button = By.xpath("//android.view.ViewGroup[@content-desc=\"test-Login with Face ID\"]");
   public By error_message_locator = By.xpath("//android.view.ViewGroup[@content-desc=\"test-Error message\"]");
   public By stabdard_user_name_location = By.xpath("//android.view.ViewGroup[@content-desc=\"test-standard_user\"]/android.widget.TextView");
   public  By locked_out_user_name_location = By.xpath("//android.view.ViewGroup[@content-desc=\"test-locked_out_user\"]/android.widget.TextView");
   public  By problem_user_name_location = By.xpath("//android.view.ViewGroup[@content-desc=\"test-problem_user\"]/android.widget.TextView");
   public  By password_locator = By.xpath("//android.widget.ScrollView[@content-desc=\"test-Login\"]/android.view.ViewGroup/android.view.ViewGroup[4]/android.widget.TextView[3]");
   public By password_info_text_locator = By.xpath("//android.widget.ScrollView[@content-desc=\"test-Login\"]/android.view.ViewGroup/android.view.ViewGroup[4]/android.widget.TextView[2]");
   public By user_name_info_text_locator = By.xpath("//android.widget.ScrollView[@content-desc=\"test-Login\"]/android.view.ViewGroup/android.view.ViewGroup[4]/android.widget.TextView[1]");

    public void loginWithValidCredentials(String userName, String password) {
        writeText(user_name_input_field, userName);
        writeText(password_input_field, password);
        fluentWaitClickOnElement(login_button, 4);
        isElementVisible(menu_button,10);
    }
}