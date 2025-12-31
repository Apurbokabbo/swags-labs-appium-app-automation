package pages;

import org.openqa.selenium.By;

public class LoginPage extends BasePage{

    public String user_input_field_placeholder_text = "Username";
    public String password_input_field_placeholder_text = "Password";
    public String password_info_text = "And the password for all users is:";
    public String user_name_info_text = "The currently accepted usernames for this application are (tap to autofill):";



    By user_name_input_field = By.xpath("//android.widget.EditText[@content-desc=\"test-Username\"]");
    By password_input_field = By.xpath("//android.widget.EditText[@content-desc=\"test-Password\"]");
    By login_button = By.xpath("//android.view.ViewGroup[@content-desc=\"test-LOGIN\"]");
    By face_recognition_button = By.xpath("//android.view.ViewGroup[@content-desc=\"test-Login with Face ID\"]");
    By error_message = By.xpath("//android.view.ViewGroup[@content-desc=\"test-Error message\"]/android.widget.TextView");
    By stabdard_user_name_location = By.xpath("//android.view.ViewGroup[@content-desc=\"test-standard_user\"]/android.widget.TextView");
    By locked_out_user_name_location = By.xpath("//android.view.ViewGroup[@content-desc=\"test-locked_out_user\"]/android.widget.TextView");
    By problem_user_name_location = By.xpath("//android.view.ViewGroup[@content-desc=\"test-problem_user\"]/android.widget.TextView");
    By password_locator = By.xpath("//android.widget.ScrollView[@content-desc=\"test-Login\"]/android.view.ViewGroup/android.view.ViewGroup[4]/android.widget.TextView[3]");
    By password_info_text_locator = By.xpath("//android.widget.ScrollView[@content-desc=\"test-Login\"]/android.view.ViewGroup/android.view.ViewGroup[4]/android.widget.TextView[2]");
    By user_name_info_text_locator = By.xpath("//android.widget.ScrollView[@content-desc=\"test-Login\"]/android.view.ViewGroup/android.view.ViewGroup[4]/android.widget.TextView[1]");


}
