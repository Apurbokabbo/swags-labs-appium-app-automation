package pages;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

public class CommonLocators {



    //wait times configuration
    public  int long_wait = 45;
    public  int medium_wait = 20;
    public  int small_wait = 10;


    public By menu_button = By.xpath("//android.view.ViewGroup[@content-desc=\"test-Menu\"]/android.view.ViewGroup/android.widget.ImageView");
    public By cart_badge = By.xpath("//android.view.ViewGroup[@content-desc=\"test-Cart\"]//android.widget.TextView");
    public By cart_button = By.xpath("//android.view.ViewGroup[@content-desc=\"test-Cart\"]/android.view.ViewGroup/android.widget.ImageView");
    public final By sortButton    = AppiumBy.accessibilityId("test-Modal Selector Button");
    public final By cancelButton   = AppiumBy.accessibilityId("test-CANCEL");

}
