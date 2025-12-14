package base;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.log4testng.Logger;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import listeners.ExtentListeners;

public class BaseTest {

	private Playwright playwright;
	public Browser browser;
	public Page page;

	private static ThreadLocal<Playwright> pw = new ThreadLocal<>();
	private static ThreadLocal<Browser> br = new ThreadLocal<>();
	private static ThreadLocal<Page> pg = new ThreadLocal<>();

	private static Properties OR = new Properties();
	private static FileInputStream fis;
	private Logger log = Logger.getLogger(this.getClass());

	@BeforeSuite
	public void setup() throws FileNotFoundException {
		PropertyConfigurator.configure("./src/test/resources/properties/log4j.properties");
		log.info("Test Execution started !!!");

		fis = new FileInputStream("./src/test/resources/properties/OR.properties");
		try {
			OR.load(fis);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void click(String locatorKey) {

		try {
			page.locator(OR.getProperty(locatorKey)).click();
			log.info("clicking on an element :" + locatorKey);
			ExtentListeners.getTest().info("clicking on an element :" + locatorKey);

		} catch (Throwable t) {
			log.error("Error while clicking on element :" + t.getMessage());
			ExtentListeners.getTest().fail("Error while clicking on element :" + t.getMessage());
		}

	}

	public void type(String locatorKey, String value) {

		try {
			page.locator(OR.getProperty(locatorKey)).fill(value);

			log.info("Typing`in an element :" + locatorKey + " and entered the value as " + value);
			ExtentListeners.getTest().info("Typing in an element :" + locatorKey);
		} catch (Throwable t) {
			log.error("Error while Typing on element :" + t.getMessage());
			ExtentListeners.getTest().fail("Error while typing on element :" + t.getMessage());
		}

	}

	public Browser getBrowser(String browserName) {
		playwright = Playwright.create();
		pw.set(playwright);

		switch (browserName) {
		case "chrome":
			log.info("Launching chrome browser");
			return playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
		case "headless":
			log.info("Launching headless mode");
			return playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
		case "firefox":
			log.info("Launching firefox browser");
			return playwright.firefox()
					.launch(new BrowserType.LaunchOptions().setChannel("firefox").setHeadless(false));
		case "webkit":
			log.info("Launching safari browser");
			return playwright.webkit().launch(new BrowserType.LaunchOptions().setHeadless(false));
		default:
			throw new IllegalArgumentException();
		}
	}

	public void navigate(Browser browser, String url) {
		this.browser = browser;
		br.set(browser);
		page = browser.newPage();
		pg.set(page);
		page.navigate(url);
		log.info("Navigate to : " + url);
	}

	@AfterSuite(alwaysRun = true)
	public void quit() {
	    if (page != null) {
	        page.close();
	    }
	    if (browser != null) {
	        browser.close();
	    }
	    if (playwright != null) {
	        playwright.close();
	    }
	}


	}

