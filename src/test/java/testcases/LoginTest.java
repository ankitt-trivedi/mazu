package testcases;

import org.testng.annotations.Test;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import base.BaseTest;

public class LoginTest extends BaseTest {

	/*private static final Logger log = LogManager.getLogger(LoginTest.class);

	@Test
	public void dologin() {
		
		log.info("test");

		Browser browser = getBrowser("chrome");
		navigate(browser, "https://app.mazu.in/login");
		
		type("username", "8960341098");
		click("Login");
		type("password","Ankit@123");
		click("Login2");
		click("createinvoice");
		type("invoicenumber","invoice12");
		click("billto");
		click("billtooption");
		click("itemname");
		click("itemnameoption");
		click("createinvoicefinal");*/
		
		
		

		@Test
		   public void dologin() {
		    try (Playwright playwright = Playwright.create()) {
		      Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
		        .setHeadless(false));
		      BrowserContext context = browser.newContext();
		     
		     
		      Page page = context.newPage();
		      page.navigate("https://app.mazu.in/login");
		      page.locator("#userName").fill("8960341098");
		      page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login").setExact(true)).click();
		      page.getByPlaceholder("Password").fill("Ankit@123");
		      page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login").setExact(true)).click();
		      page.getByLabel("add").click();
		      page.getByLabel("Inv. No: *").fill("invoice21");
		      page.getByPlaceholder("Name / GSTIN / Mobile").click();
		      page.getByText("Sundry Creditors").click();
		      page.getByPlaceholder("Item Name").click();
		      page.locator("#item0-option-1").getByText("Sale Price -").click();
		      page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create Invoice")).click();
		    }
		  }
		
		
	}


