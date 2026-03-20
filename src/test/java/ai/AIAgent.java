package ai;

import com.theokanning.openai.completion.chat.*;
import com.theokanning.openai.service.OpenAiService;

import java.time.Duration;
import java.util.Arrays;

public class AIAgent {

    // ✅ ALWAYS use environment variable
	static String apiKey = "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";

    public static String generateScript(String html) {

        OpenAiService service = new OpenAiService(apiKey, Duration.ofSeconds(60));

        String prompt =
                "You are an automation expert.\n\n" +

                "Analyze the following UI and generate Playwright Java code.\n\n" +

                "STRICT RULES:\n" +
                "1. Use only valid Playwright Java syntax\n" +
                "2. Use page.fill() and page.click()\n" +
                "3. Generate REALISTIC TEST DATA:\n" +
                "   - Email → test@example.com\n" +
                "   - Phone → 9876543210\n" +
                "   - Name → Test User\n" +
                "   - Amount → 1000\n" +
                "4. Use BEST LOCATORS:\n" +
                "   - Prefer placeholder\n" +
                "   - Then label\n" +
                "   - Then role\n" +
                "   - Avoid generic selectors\n\n" +

                "5. Return ONLY JSON format:\n" +
                "{\n" +
                "  \"steps\": [],\n" +
                "  \"code\": []\n" +
                "}\n\n" +

                "UI DATA:\n" + html;

        ChatMessage message = new ChatMessage("user", prompt);

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(Arrays.asList(message))
                .maxTokens(800)
                .temperature(0.2)
                .build();

        return service.createChatCompletion(request)
                .getChoices()
                .get(0)
                .getMessage()
                .getContent();
    }
}