package steps;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.options.LoadState;

import base.BaseTest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class LoginSteps extends BaseTest{

    @Given("user is on login page")
    public void user_is_on_login_page() {
    	

		Browser browser = getBrowser("chrome");
		navigate(browser, "https://app.mazu.in/login");
    }	
    
   @Then("Enter username")
    public void enter_username(){
    	type("username","8960341098");
		click("Login");
		type("password","Ankit@123");
		click("Login2");
		click("createinvoice");
		//type("invoicenumber","invoice50");
		click("billto");
		click("billtooption");
		click("itemname");
		type("itemname", "item eight");
		click("itemnameoption");
		type("itemqty","2");
		click("additemBtn");
		click("createinvoicefinal");
		
	
    	
    }
}