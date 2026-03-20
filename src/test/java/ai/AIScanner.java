package ai;

import com.microsoft.playwright.*;
import java.util.List;

public class AIScanner {

    public static String scan(Page page) {

        List<ElementHandle> elements =
                page.querySelectorAll("input:visible, button:visible, textarea:visible");

        StringBuilder sb = new StringBuilder();

        for (ElementHandle el : elements) {
            try {
                String tag = el.evaluate("e => e.tagName").toString();
                String id = el.getAttribute("id");
                String name = el.getAttribute("name");
                String placeholder = el.getAttribute("placeholder");
                String text = el.textContent();

                sb.append("TAG: ").append(tag)
                  .append(" | ID: ").append(id)
                  .append(" | NAME: ").append(name)
                  .append(" | PLACEHOLDER: ").append(placeholder)
                  .append(" | TEXT: ").append(text)
                  .append("\n");

            } catch (Exception ignored) {}
        }

        return sb.toString();
    }
}