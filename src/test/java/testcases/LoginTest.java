package testcases;

import org.testng.annotations.Test;
import com.microsoft.playwright.Browser;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import base.BaseTest;

public class LoginTest extends BaseTest {

	private static final Logger log = LogManager.getLogger(LoginTest.class);

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
		type("invoicenumber","invoice16");
		click("billto");
		click("billtooption");
		click("itemname");
		click("itemnameoption");
		click("createinvoicefinal");
			
	}
}

