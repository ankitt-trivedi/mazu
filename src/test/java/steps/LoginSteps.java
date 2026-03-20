package steps;



import java.util.regex.Pattern;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Locator;

import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;

import base.BaseTest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class LoginSteps extends BaseTest{

    @Given("user is on login page")
    public void user_is_on_login_page() {
    	

		Browser browser = getBrowser("chrome");
		navigate(browser, "https://app.mazu.in/login");
    }	
    
    @Then("create invoice")
    public void create_invoice(){

       clickByRole(AriaRole.TEXTBOX, "Mobile / Email");
       fillByRole(AriaRole.TEXTBOX, "Mobile / Email", "8");

       fillByLocator("#userName", "8960341098");
       getPage().locator("#userName").press("Enter");

       pressByRole(AriaRole.TEXTBOX, "Password", "CapsLock");
       fillByRole(AriaRole.TEXTBOX, "Password", "Ankit@123");
       pressByRole(AriaRole.TEXTBOX, "Password", "Enter");
       
       waitForUiStable();

       clickByLabel("add");
       
       //waitForInvoicePage();
       //waitForUiStable();

       // ✅ Bill To (stable)
       getPage()
           .getByPlaceholder("Name / GSTIN / Mobile")
           .click();

       // ⭐ wait for MUI dropdown
       Locator dropdown = getPage()
               .locator("div[role='presentation']")
               .last();

       dropdown.waitFor(new Locator.WaitForOptions()
               .setState(WaitForSelectorState.VISIBLE)
               .setTimeout(10000));

       getPage().waitForTimeout(500);

       Locator accountOne = dropdown
               .locator("li, div")
               .filter(new Locator.FilterOptions()
                       .setHasText(Pattern.compile("account one", Pattern.CASE_INSENSITIVE)))
               .first();

       safeClick(accountOne, "Account One");

       clickByRole(AriaRole.COMBOBOX, "Add items through Name or");
       clickByText("Item Eight");

       clickByRole(AriaRole.TEXTBOX, "Add Quantity");
       fillByRole(AriaRole.TEXTBOX, "Add Quantity", "1");

       clickByRole(AriaRole.BUTTON, "Add Item");
       clickByRole(AriaRole.BUTTON, "Create Invoice");
    }
	   
   @Then("create receipt")
	   public void userCreatesReceipt() {

	        createReceipt(
	                "Account Two",
	                "100",
	                "Bank Accounts"
	        );
	  }
   
   @Then("create item")
   public void userCreatesItem() {

	   createItem();
	   }
   
   @Then("create account")
   public void userCreatesAccount() {

	   createAccount();
	   }
   
   @Then("generate AI script")
   public void generateAIScript() {
       generateFullScriptWithAI();
   }
  }
   
   
		
	
    	
    
