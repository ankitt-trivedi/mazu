package base;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.log4testng.Logger;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;

import listeners.ExtentListeners;

public class BaseTest {

    private static ThreadLocal<Playwright> pw = new ThreadLocal<>();
    private static ThreadLocal<Browser> br = new ThreadLocal<>();
    private static ThreadLocal<Page> pg = new ThreadLocal<>();

    private static Properties OR = new Properties();
    private Logger log = Logger.getLogger(this.getClass());
    
    static {
        try {
            InputStream is = BaseTest.class
                    .getClassLoader()
                    .getResourceAsStream("properties/OR.properties");

            if (is == null) {
                throw new RuntimeException("❌ OR.properties NOT FOUND in classpath");
            }

            OR.load(is);
            System.out.println("✅ OR loaded keys: " + OR.keySet());

        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to load OR.properties", e);
        }
    }

    // ================== SETUP ==================

    @BeforeSuite
    public void setup() {
        PropertyConfigurator.configure("./src/test/resources/properties/log4j.properties");
        log.info("Test Execution started !!!");
    }

    // ================== SAFE GETTERS ==================

    public Page getPage() {
        return pg.get();
    }

    public Browser getBrowserInstance() {
        return br.get();
    }

    // ================== ACTIONS ==================

    public void click(String locatorKey) {
        String selector = OR.getProperty(locatorKey);

        if (selector == null) {
            throw new RuntimeException("❌ Locator not found: " + locatorKey);
        }

        try {
            Locator element = getPage().locator(selector);

            element.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(60000));

            element.click();

            log.info("Clicking on element: " + locatorKey);
            ExtentListeners.getTest().info("Click: " + locatorKey);

        } catch (Throwable t) {
            log.error("Error while clicking: " + locatorKey, t);
            ExtentListeners.getTest().fail("Click failed: " + locatorKey);
            throw new RuntimeException(t);
        }
    }
    

    public void type(String locatorKey, String value) {
    	
    	
        String selector = OR.getProperty(locatorKey);
        
        if (selector == null) {
            throw new RuntimeException("❌ Locator not found: " + locatorKey);
        }

        try {
            getPage().locator(selector).fill(value);
            log.info("Typing in element: " + locatorKey);
            ExtentListeners.getTest().info("Type: " + locatorKey);
        } catch (Throwable t) {
            log.error("Error while typing: " + locatorKey, t);
            ExtentListeners.getTest().fail("Type failed: " + locatorKey);
            throw new RuntimeException(t); // ✅ VERY IMPORTANT
        }
    }

    // ================== BROWSER ==================

    public Browser getBrowser(String browserName) {
        Playwright playwright = Playwright.create();
        pw.set(playwright);

        Browser browser;

        switch (browserName.toLowerCase()) {
            case "chrome":
                log.info("Launching chrome browser");
                browser = playwright.chromium()
                        .launch(new BrowserType.LaunchOptions().setHeadless(false));
                break;

            case "headless":
                browser = playwright.chromium()
                        .launch(new BrowserType.LaunchOptions().setHeadless(true));
                break;

            case "firefox":
                browser = playwright.firefox()
                        .launch(new BrowserType.LaunchOptions().setHeadless(false));
                break;

            case "webkit":
                browser = playwright.webkit()
                        .launch(new BrowserType.LaunchOptions().setHeadless(false));
                break;

            default:
                throw new IllegalArgumentException("Invalid browser: " + browserName);
        }

        br.set(browser);
        return browser;
    }

    public void navigate(Browser browser, String url) {
        Page page = browser.newPage();
        pg.set(page);
        page.navigate(url);
        log.info("Navigate to: " + url);
    }

    // ================== TEARDOWN ==================

    @AfterSuite(alwaysRun = true)
    public void quit() {
        if (pg.get() != null) pg.get().close();
        if (br.get() != null) br.get().close();
        if (pw.get() != null) pw.get().close();
    }
}