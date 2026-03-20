package base;
import com.microsoft.playwright.Page;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.log4testng.Logger;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;

import ai.AIAgent;
import ai.AIParser;
import ai.AIScriptExecutor;
import listeners.ExtentListeners;

public class BaseTest {
	protected Page page; 
	private static ThreadLocal<BrowserContext> ctx = new ThreadLocal<>();
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
    	
    	
    	PropertyConfigurator.configure(
    		    BaseTest.class
    		        .getClassLoader()
    		        .getResource("log4j.properties")
    		);
        log.info("Test Execution started !!!");
    }

    // ================== SAFE GETTERS ==================

    public Page getPage() {
        Page page = pg.get();
        if (page == null) {
            throw new RuntimeException("❌ Page is null — browser not initialized");
        }
        return page;
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
    
    protected void selectPaymentMode(String text) {

        Page page = getPage();

        // wait for dropdown listbox
        page.waitForSelector("[role='listbox']",
                new Page.WaitForSelectorOptions().setTimeout(10000));

        // primary — role option (best for MUI)
        Locator option = page
                .getByRole(AriaRole.OPTION,
                        new Page.GetByRoleOptions().setName(text))
                .first();

        // fallback — partial text match
        if (option.count() == 0) {
            option = page.locator("li:has-text('" + text + "')").first();
        }

        safeClick(option, "Payment Mode: " + text);

        waitForUiStable();
    }

 // ================== BROWSER ==================

    public Browser getBrowser(String browserName) {

        if (pw.get() == null) {
            pw.set(Playwright.create());
        }

        Playwright playwright = pw.get();
        Browser browser;

        switch (browserName.toLowerCase()) {

            case "chrome":
                log.info("Launching chrome browser");
                browser = playwright.chromium().launch(
                	    new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setArgs(java.util.Arrays.asList(
                            "--disable-dev-shm-usage",
                            "--disable-background-networking",
                            "--disable-background-timer-throttling"
                        ))
                );
                
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

        BrowserContext context = browser.newContext();
        ctx.set(context);

        Page page = context.newPage();
        pg.set(page);

        page.setDefaultTimeout(60000);
        page.setDefaultNavigationTimeout(60000);

        page.navigate(url);
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        page.waitForTimeout(1000);
    }
    
   

    public void openReceipts() {

        Locator receipts = getPage()
                .getByRole(AriaRole.LINK,
                        new Page.GetByRoleOptions().setName("Receipts"))
                .first();

        safeClick(receipts, "Receipts Menu");

        waitForUiStable();
    }
    
    public void openItem() {

        Locator items = getPage()
                .getByRole(AriaRole.LINK,
                        new Page.GetByRoleOptions().setName("Items"))
                .first();

        safeClick(items, "Items Menu");

        waitForUiStable();
    }
    
    public void clickByRole(AriaRole role, String name) {
        try {
            Locator element = getPage()
                    .getByRole(role, new Page.GetByRoleOptions().setName(name))
                    .first();

            safeClick(element, "Role: " + name);

            waitForUiStable();

            log.info("Click by role: " + name);
            ExtentListeners.getTest().info("Click by role: " + name);

        } catch (Exception e) {
            throw new RuntimeException("Failed to click by role: " + name, e);
        }
    }
    
    public void waitForInvoicePage() {
        Page page = getPage();

        try {
            // ⭐ MOST RELIABLE signal in your app
            Locator billTo = page.getByPlaceholder("Name / GSTIN / Mobile");

            billTo.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(60000));

            waitForUiStable();

            log.info("✅ Invoice page loaded (Bill To visible)");

        } catch (Exception e) {
            throw new RuntimeException("Invoice page did not load", e);
        }
    }
    
    public void clickFromFloatingMenu(String text) {
        Page page = getPage();

        // wait for animation
        page.waitForTimeout(800);

        Locator option = page.locator("text=" + text).first();

        option.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(15000));

        safeClick(option, "Floating menu: " + text);

        waitForUiStable();
    }
    
    public void clickFloatingAddButton() {

        Locator fab = getPage()
            .locator("button:has(svg.fui-Icon)")
            .last();

        fab.waitFor(new Locator.WaitForOptions()
            .setState(WaitForSelectorState.VISIBLE)
            .setTimeout(15000));

        // ⭐ get center of button
        var box = fab.boundingBox();
        if (box == null) {
            throw new RuntimeException("FAB bounding box is null");
        }

        double x = box.x + box.width / 2;
        double y = box.y + box.height / 2;

        // ⭐ REAL USER CLICK (most important)
        getPage().mouse().move(x, y);
        getPage().mouse().click(x, y);

        log.info("✅ Floating Add Button clicked via mouse");

        waitForUiStable();
    }
    
    public void fillByRole(AriaRole role, String name, String value) {
        try {
            getPage()
                .getByRole(role, new Page.GetByRoleOptions().setName(name))
                .fill(value);

            log.info("Fill by role: " + name);

        } catch (Exception e) {
            throw new RuntimeException("Failed to fill by role: " + name, e);
        }
    }
    
    public void fillById(String id, String value) {
        Locator field = getPage().locator("#" + id);
        field.waitFor();
        field.fill(value);
    }
    
    public void pressByRole(AriaRole role, String name, String key) {
        try {
            getPage()
                .getByRole(role, new Page.GetByRoleOptions().setName(name))
                .press(key);

            log.info("Press key on: " + name);

        } catch (Exception e) {
            throw new RuntimeException("Failed to press key on: " + name, e);
        }
    }
    
    public void clickByText(String text) {
        try {
            Locator element = getPage().getByText(text).first();
            safeClick(element, "Text: " + text);
            waitForUiStable();
            log.info("Click by text: " + text);
        } catch (Exception e) {
            throw new RuntimeException("Failed to click text: " + text, e);
        }
    }
    
    public void fillByLocator(String selector, String value) {
        try {
            getPage().locator(selector).fill(value);
            log.info("Fill locator: " + selector);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fill locator: " + selector, e);
        }
    }
    
    
    protected void clickByLabel(String label) {
        try {
            Locator element = getPage()
                    .getByLabel(label, new Page.GetByLabelOptions().setExact(true))
                    .first();

            element.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(60000));

            element.scrollIntoViewIfNeeded();

            safeClick(element, "Label: " + label);
            waitForUiStable();

            log.info("Click by label: " + label);
            ExtentListeners.getTest().info("Click by label: " + label);

        } catch (Exception e) {
            log.error("Failed to click by label: " + label, e);
            ExtentListeners.getTest().fail("Click by label failed: " + label);
            throw new RuntimeException(e);
        }
    }
        
    protected void fillByLabel(String label, String value) {
        try {
            Locator field = getPage().getByLabel(label);

            field.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(60000));

            field.fill(value);

            log.info("Fill by label: " + label);
            ExtentListeners.getTest().info("Fill by label: " + label);

        } catch (Exception e) {
            log.error("Failed to fill by label: " + label, e);
            ExtentListeners.getTest().fail("Fill by label failed: " + label);
            throw new RuntimeException(e);
        }
    }
    protected void selectFromMuiDropdown(String text) {

        Page page = getPage();

        // wait for dropdown panel to appear
        Locator option = page
                .getByRole(AriaRole.OPTION,
                        new Page.GetByRoleOptions().setName(text))
                .first();

        // fallback for non-role options (very important)
        if (option.count() == 0) {
            option = page.locator("text=" + text).first();
        }

        safeClick(option, "Dropdown option: " + text);

        waitForUiStable();
    }
        
    protected void checkRowCheckbox(String rowName) {
        Locator checkbox = getPage()
                .getByRole(AriaRole.ROW,
                        new Page.GetByRoleOptions().setName(rowName))
                .getByRole(AriaRole.CHECKBOX)
                .first();

        safeClick(checkbox, "Checkbox: " + rowName);
    }
        
        public void clickByCss(String cssSelector) {
            try {
                Locator element = getPage().locator(cssSelector).first();

                element.waitFor(new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(60000));

                element.scrollIntoViewIfNeeded();

                element.click(new Locator.ClickOptions().setTimeout(60000));

                log.info("Click by CSS: " + cssSelector);
                ExtentListeners.getTest().info("Click CSS: " + cssSelector);

            } catch (Exception e) {
                log.error("Failed to click CSS: " + cssSelector, e);
                ExtentListeners.getTest().fail("Click CSS failed: " + cssSelector);
                throw new RuntimeException(e);
            }
        }
        
        
        
        public void safeClick(Locator locator, String elementName) {

            int attempts = 0;
            Exception lastError = null;

            while (attempts < 3) {
                try {
                    locator.waitFor(new Locator.WaitForOptions()
                            .setState(WaitForSelectorState.ATTACHED)
                            .setTimeout(60000));

                    locator.scrollIntoViewIfNeeded();

                    locator.click(new Locator.ClickOptions()
                            .setTimeout(60000)
                            .setForce(true));   // ⭐ CRITICAL for MUI

                    log.info("Safe click success: " + elementName);
                    return;

                } catch (Exception e) {
                    lastError = e;
                    attempts++;
                    log.warn("Retry click (" + attempts + "): " + elementName);
                    getPage().waitForTimeout(700);
                }
            }

            throw new RuntimeException("Safe click failed: " + elementName, lastError);
        }
        
        public void waitForUiStable() {
            try {
                getPage().waitForLoadState(LoadState.NETWORKIDLE,
                        new Page.WaitForLoadStateOptions().setTimeout(5000));
            } catch (Exception ignored) {
                // SPA apps may never reach network idle — ignore
            }

            // MUI animation buffer
            getPage().waitForTimeout(300);
        }
        
        public void ensureSidebarExpanded() {

            waitForUiStable();

            // ✅ STRICT + SCOPED locator (NO getByText)
            Locator salesNav = getPage()
            		.locator(".leftContainer li.nav p.navLabel")
                    .filter(new Locator.FilterOptions()
                            .setHasText(Pattern.compile("^Sales$")))
                    .first();

            try {
                if (salesNav.isVisible()) {
                    log.info("Sidebar already expanded");
                    return;
                }
            } catch (Exception ignored) {
                // element not present → sidebar collapsed
            }

            Locator toggle = getPage()
                    .locator(".leftContainer button")
                    .first();

            safeClick(toggle, "Sidebar Toggle");

            waitForUiStable();
        }
        
        public void expandSalesMenu() {

            waitForUiStable();

            Locator salesNav = getPage()
                    .getByText("Sales", new Page.GetByTextOptions().setExact(true))
                    .first();

            safeClick(salesNav, "Sales Menu");

            // wait until submenu appears
            getPage()
                    .getByRole(AriaRole.LINK,
                            new Page.GetByRoleOptions().setName("Receipts"))
                    .waitFor(new Locator.WaitForOptions()
                            .setState(WaitForSelectorState.VISIBLE));

            waitForUiStable();
        }
        
        public void expandItemsMenu() {

            waitForUiStable();

            Locator itemNav = getPage()
                    .getByText("Item", new Page.GetByTextOptions().setExact(true))
                    .first();

            safeClick(itemNav, "Items Menu");

            // wait until submenu appears
            getPage()
                    .getByRole(AriaRole.LINK,
                            new Page.GetByRoleOptions().setName("Items"))
                    .waitFor(new Locator.WaitForOptions()
                            .setState(WaitForSelectorState.VISIBLE));

            waitForUiStable();
        }
        
        public void expandAccountsMenu() {

            waitForUiStable();

            Locator accountNav = getPage()
                    .getByText("Account", new Page.GetByTextOptions().setExact(true))
                    .first();

            safeClick(accountNav, "Accounts Menu");

            // wait until submenu appears
            getPage()
                    .getByRole(AriaRole.LINK,
                            new Page.GetByRoleOptions().setName("Accounts"))
                    .waitFor(new Locator.WaitForOptions()
                            .setState(WaitForSelectorState.VISIBLE));

            waitForUiStable();
        }
        
        public String generateUniqueItemName() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String timestamp = LocalDateTime.now().format(formatter);
            return "Item_" + timestamp;
        }
        
        public String generateUniqueAccountName() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String timestamp = LocalDateTime.now().format(formatter);
            return "Account_" + timestamp;
        }
        
        public String generateUniqueAccountAlias() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String timestamp = LocalDateTime.now().format(formatter);
            return "AccountAlias_" + timestamp;
        }
        
        public void generateFullScriptWithAI() {

            // ✅ Step 1: Extract UI from current page
            String html = extractUI(getPage());

            // ✅ Step 2: Call AI
            String response = AIAgent.generateScript(html);

            // ✅ Step 3: Parse AI response
            List<String> steps = AIParser.extractSteps(response);
            List<String> code = AIParser.extractCode(response);

            // ✅ Step 4: Filter only valid Playwright commands
            code = code.stream()
                    .filter(s -> s.contains("page.fill") || s.contains("page.click"))
                    .collect(Collectors.toList());

            // ✅ Step 5: Print for debug
            System.out.println("===== AI STEPS =====");
            steps.forEach(System.out::println);

            System.out.println("===== AI CODE =====");
            code.forEach(System.out::println);

            // ✅ Step 6: Execute
            AIScriptExecutor.execute(getPage(), code);
        }
        
        public String extractUI(Page page) {

            String inputs = page.locator("input").evaluateAll(
                "els => els.map(e => ({placeholder: e.placeholder, name: e.name, id: e.id}))"
            ).toString();

            String buttons = page.locator("button").allTextContents().toString();

            String links = page.locator("a").allTextContents().toString();

            return "INPUTS: " + inputs +
                   "\nBUTTONS: " + buttons +
                   "\nLINKS: " + links;
        }
        
        
        
     // ================== BUSINESS FLOWS ==================
        
        

        public void createReceipt(String accountName, String amount, String bankText) {

            ensureSidebarExpanded();

            clickByRole(AriaRole.BUTTON, "Exit without Saving");

            expandSalesMenu();

            openReceipts();

            clickByLabel("add");

            selectFromMuiDropdown(accountName);

            fillByLabel("Amt. Received *", amount);

            checkRowCheckbox("Date Ref. No. Due Date");

            clickByLabel("Payment Mode *");

            selectPaymentMode(bankText);

            clickByRole(AriaRole.BUTTON, "Create Receipt");

            log.info("✅ Receipt creation flow executed");
        }
        
        public void createItem() {
        	
        	String itemName = generateUniqueItemName();
        	
        	System.out.println(itemName);

            expandItemsMenu();

            clickByLabel("add");

            fillByLocator("#name", itemName);
            
            String codegen = "code_" + System.currentTimeMillis();

            fillByLocator("#item_code", codegen);
            
            fillByLabel("Description", "Automation Item");

            fillByLocator("#main_unit_sale_price", "100");
            
            fillByLocator("#main_unit_sale_discount", "10");
            
            fillByLocator("#main_unit_msp", "10");

            fillByLabel("MRP", "1000");
            
            fillByLocator("#main_unit_purchase_price", "200");
            
            fillByLocator("#main_unit_purchase_discount", "20");
            
            fillByLocator("#alternate_unit_conversion_factor", "2");
            
         // scroll page down to reveal alternate section
            getPage().mouse().wheel(0, 1200);
            getPage().waitForTimeout(500);
            
            fillByLocator("#alternate_unit_sale_price", "300");
            
            fillByLocator("#alternate_unit_sale_discount", "30");
            
           fillByLocator("#alternate_unit_msp", "10");
            
           fillByLocator("#alternate_unit_purchase_price", "400");
            
            fillByLocator("#alternate_unit_purchase_discount", "40");
            
           fillByLocator("#packaging_unit_sale_price", "500");
            
            fillByLocator("#packaging_unit_sale_discount", "50");
            
           fillByLocator("#packaging_unit_msp", "10");
            
           fillByLocator("#packaging_unit_purchase_price", "600");
            
            fillByLocator("#packaging_unit_purchase_discount", "60");

            clickByRole(AriaRole.BUTTON, "Create Item");
            
          
            
        }
        

  
  public void createAccount() {

	    String accountName = generateUniqueAccountName();
	    
	    String accountAlias = generateUniqueAccountAlias();

	    expandAccountsMenu();

	    clickByLabel("add");

	    // Basic Details
	    //fillByLabel("GSTIN / GST Number", "29ABCDE1234F1Z5");
	    fillById("name", accountName);
	    fillByLabel("Mobile Number", "9876543210");

	    //clickByLabel("Account Group");
	    //selectFromMuiDropdown("Sundry Debtors");

	    fillByLabel("Alias", accountAlias);
	    fillByLabel("Display Name", accountName);

	    // Billing Address
	    fillById("address", "Delhi");
	    fillByLabel("Pincode", "560001");
	    

	    /*clickByLabel("State");
	    selectFromMuiDropdown("Karnataka");

	    clickByLabel("Country");
	    selectFromMuiDropdown("India");*/

	    fillByLabel("Location / Place of Supply","Karnataka");

	    // Contact Details
	    fillByLabel("Primary Contact Person", "Test Person");
	    fillByLabel("Email Address", "test@automation.com");

	    // Payment Section
	    fillByLabel("Opening Balance Amount", "1000");

	   /* clickByLabel("Opening Balance Type");
	    selectFromMuiDropdown("Debit Balance");

	    // Toggle
	    clickByText("Bill-by-Bill Reconciliation");*/

	    // Create Account
	    clickByRole(AriaRole.BUTTON, "Create Account");

	    log.info("✅ Account creation flow executed");
	}
        
  

    // ================== TEARDOWN ==================
    @AfterSuite(alwaysRun = true)
    public void quit() {

        try {
            if (ctx.get() != null) {
                ctx.get().close();   // ⭐ CLOSE CONTEXT FIRST
            }
        } catch (Exception ignored) {}

        try {
            if (br.get() != null) {
                br.get().close();
            }
        } catch (Exception ignored) {}

        try {
            if (pw.get() != null) {
                pw.get().close();
            }
        } catch (Exception ignored) {}
    }
}