package generator;

import ai.AIAgent;
import ai.AIParser;
import ai.AIScriptExecutor;
import base.BaseTest;

import java.util.List;

import com.microsoft.playwright.Page;

public class TestGenerator extends BaseTest {

    public void generateAndRunTest() {

        Page page = getPage();

        // 🔹 Extract UI
        String html = extractUI(page);

        // 🔹 AI call
        String response = AIAgent.generateScript(html);

        // 🔹 Parse
        //List<String> steps = AIParser.extractSteps(response);
        List<String> code = AIParser.extractCode(response);

        // 🔹 Print
        System.out.println("===== AI STEPS =====");
       // steps.forEach(System.out::println);

        System.out.println("===== AI CODE =====");
        code.forEach(System.out::println);

        // 🔹 Execute
        AIScriptExecutor.execute(page, code);
    }

  
}