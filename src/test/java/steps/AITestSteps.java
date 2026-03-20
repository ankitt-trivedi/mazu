package steps;

import generator.TestGenerator;
import io.cucumber.java.en.Then;

public class AITestSteps extends TestGenerator {

    @Then("AI generates and runs test")
    public void runAITest() {
        generateAndRunTest();
    }
}