package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentManager {

	private static ExtentReports extent;

	public static ExtentReports createInstance() {
		if (extent == null) {
			String reportPath = System.getProperty("user.dir") + "/reports/ExtentReport.html";
			ExtentSparkReporter reporter = new ExtentSparkReporter(reportPath);

			extent = new ExtentReports();
			extent.attachReporter(reporter);
		}
		return extent;
	}
}