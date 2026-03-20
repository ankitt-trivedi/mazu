package ai;

import com.microsoft.playwright.Page;
import java.util.List;
import utils.TestDataGenerator;

public class AIScriptExecutor {

    public static void execute(Page page, List<String> steps) {

        for (String step : steps) {

            System.out.println("Executing: " + step);

            try {

                // ================== FILL ==================
                if (step.contains("page.fill")) {

                    // split into selector + value
                    String inside = step.substring(
                            step.indexOf("(") + 1,
                            step.lastIndexOf(")")
                    );

                    String[] parts = inside.split(",", 2);

                    String selector = clean(parts[0]);
                    String value = clean(parts[1]);

                    // 🔥 AUTO TEST DATA
                    if (value.equalsIgnoreCase("value") ||
                        value.equalsIgnoreCase("username")) {

                        value = TestDataGenerator.getValue(selector);
                    }

                    // 🔥 FIX SELECTOR (important)
                    selector = fixSelector(selector);

                    page.fill(selector, value);
                }

                // ================== CLICK ==================
                else if (step.contains("page.click")) {

                    String inside = step.substring(
                            step.indexOf("(") + 1,
                            step.lastIndexOf(")")
                    );

                    String selector = clean(inside);

                    selector = fixSelector(selector);

                    page.click(selector);
                }

            } catch (Exception e) {
                System.out.println("❌ Failed step: " + step);
                e.printStackTrace();
            }
        }
    }

    // ================== CLEAN STRING ==================
    private static String clean(String text) {
        text = text.trim();

        if ((text.startsWith("\"") && text.endsWith("\"")) ||
            (text.startsWith("'") && text.endsWith("'"))) {

            text = text.substring(1, text.length() - 1);
        }

        return text;
    }

    // ================== FIX SELECTOR ==================
    private static String fixSelector(String selector) {

        // fix placeholder without quotes
        if (selector.contains("placeholder=") && !selector.contains("\"")) {

            String value = selector
                    .split("=")[1]
                    .replace("]", "")
                    .trim();

            return "input[placeholder=\"" + value + "\"]";
        }

        return selector;
    }
}