package runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"steps"},   // keep this
        plugin = {"pretty","html:target/cucumber-report.html"},
        monochrome = true
)
public class RunCucumberTest extends AbstractTestNGCucumberTests {
}