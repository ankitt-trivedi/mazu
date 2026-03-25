package steps;




import com.microsoft.playwright.Page;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Locator;

import com.microsoft.playwright.options.AriaRole;


import base.BaseTest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class LoginSteps extends BaseTest{

    @Given("user is on login page")
    public void user_is_on_login_page() {
    	

		Browser browser = getBrowser("chrome");
		navigate(browser, "https://app.mazu.in/login");
    }	
    
    @SuppressWarnings("deprecation")
	@Then("create invoice")
    public void create_invoice(){

       clickByRole(AriaRole.TEXTBOX, "Mobile / Email");
       /*fillByRole(AriaRole.TEXTBOX, "Mobile / Email", "8");

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
       clickByRole(AriaRole.BUTTON, "Create Invoice");*/
       
    // Login
    // Login
       fillByLocator("#userName", "8960341098");
       getPage().locator("#userName").press("Enter");

       fillByRole(AriaRole.TEXTBOX, "Password", "Ankit@123");
       pressByRole(AriaRole.TEXTBOX, "Password", "Enter");

       waitForUiStable();
       Locator addBtn = getPage().getByLabel("add");

    // force click (important for MUI)
    addBtn.waitFor();
    addBtn.click(new Locator.ClickOptions().setForce(true));

    // 🔥 WAIT FOR INVOICE PAGE (REAL FIX)
    getPage().waitForSelector("text=Create Invoice",
            new Page.WaitForSelectorOptions().setTimeout(15000));

    // optional stabilize
    waitForUiStable();

       Locator customerInput = getPage()
    		    .locator("input[placeholder='Name / GSTIN / Mobile']");

    		customerInput.waitFor();
    		customerInput.click();
    		customerInput.fill("Account");

    		// MUI dropdown
    		Locator dropdown = getPage()
    		    .locator("div[role='presentation']")
    		    .last();

    		dropdown.waitFor();

    		// click option
    		dropdown.locator("li")
    		    .filter(new Locator.FilterOptions().setHasText("Account Two"))
    		    .first()
    		    .click();

    		waitForUiStable();

    		// ✅ validate
    		if (!customerInput.inputValue().toLowerCase().contains("account")) {
    		    throw new RuntimeException("Customer selection failed");
    		}
       
    		Locator itemInput = getPage()
    			    .locator("input[placeholder='Search or Create Item (CTRL + I)']");

    			// wait for input
    			itemInput.waitFor();

    			// focus
    			itemInput.click();

    			// 🔥 type slowly (IMPORTANT)
    			itemInput.type("Item", new Locator.TypeOptions().setDelay(100));

    			// 🔥 handle MUI dropdown (PORTAL)
    			Locator itemDropdown = getPage()
    			    .locator("div[role='presentation']")
    			    .last();

    			itemDropdown.waitFor();

    			// 🔥 CLICK exact item (NO keyboard)
    			itemDropdown.locator("li")
    			    .filter(new Locator.FilterOptions().setHasText("Item Eight"))
    			    .first()
    			    .click();

    			// stabilize
    			waitForUiStable();

       fillByRole(AriaRole.TEXTBOX, "Add Quantity", "1");

       clickByRole(AriaRole.BUTTON, "Add Item");

       // Wait for item added
       getPage().locator("text=Item Eight").waitFor();

       // Create Invoice
       Locator createBtn = getPage().getByRole(
           AriaRole.BUTTON,
           new Page.GetByRoleOptions().setName("Create Invoice")
       );

       createBtn.waitFor();

       getPage().waitForTimeout(500);

       if (!createBtn.isEnabled()) {
           throw new RuntimeException("Create Invoice button disabled");
       }

       createBtn.click();
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
   
   
		
	
    	
    
