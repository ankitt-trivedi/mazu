package listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import utils.ExtentManager;
import org.testng.*;
import java.util.concurrent.ConcurrentHashMap;

public class ExtentListeners implements ITestListener {

	private static ExtentReports extent = ExtentManager.createInstance();

	// Thread-safe map of tests
	private static ConcurrentHashMap<Long, ExtentTest> testMap = new ConcurrentHashMap<>();

	@SuppressWarnings("deprecation")
	public static ExtentTest getTest() {
		return testMap.get(Thread.currentThread().getId());
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onTestStart(ITestResult result) {
		ExtentTest test = extent.createTest(result.getMethod().getMethodName());
		testMap.put(Thread.currentThread().getId(), test);
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		getTest().pass("Test passed");
	}

	@Override
	public void onTestFailure(ITestResult result) {
		getTest().fail(result.getThrowable());
	}

	@Override
	public void onFinish(ITestContext context) {
		extent.flush();
	}
}