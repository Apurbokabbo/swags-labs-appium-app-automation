package pages;

import org.openqa.selenium.By;

public class CommonLocators {



    //wait times configuration
    public  int long_wait = 45;
    public  int medium_wait = 20;
    public  int small_wait = 10;


    public By menu_button = By.xpath("//android.view.ViewGroup[@content-desc=\"test-Menu\"]/android.view.ViewGroup/android.widget.ImageView");
    public By cart_badge = By.xpath("//android.view.ViewGroup[@content-desc=\"test-Cart\"]//android.widget.TextView");
    public By cart_button = By.xpath("//android.view.ViewGroup[@content-desc=\"test-Cart\"]/android.view.ViewGroup/android.widget.ImageView");
}
