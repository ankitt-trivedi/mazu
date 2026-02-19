package runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
    features = "src/test/resources/features",
    glue = {"steps"},
    plugin = {"pretty"},
    monochrome = true
)
public class RunCucumberTest extends AbstractTestNGCucumberTests {
	
	public RunCucumberTest() {
        System.out.println("first test");
	
}
}