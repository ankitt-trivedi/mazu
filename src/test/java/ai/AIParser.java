package ai;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AIParser {

	public static List<String> extractCode(String response) {

	    List<String> steps = new ArrayList<>();

	    try {
	        JSONObject json = new JSONObject(response);
	        JSONArray codeArray = json.getJSONArray("code");

	        for (int i = 0; i < codeArray.length(); i++) {
	            steps.add(cleanLine(codeArray.getString(i)));
	        }

	    } catch (Exception e) {

	        System.out.println("⚠ Not JSON, fallback to raw parsing");

	        String[] lines = response.split("\n");

	        for (String line : lines) {
	            if (line.contains("page.")) {
	                steps.add(cleanLine(line.trim()));
	            }
	        }
	    }

	    return steps;
	}
	public static List<String> extractSteps(String response) {
	    List<String> steps = new ArrayList<>();

	    try {
	        JSONObject json = new JSONObject(response);
	        JSONArray arr = json.getJSONArray("steps");

	        for (int i = 0; i < arr.length(); i++) {
	            steps.add(arr.getString(i));
	        }

	    } catch (Exception e) {
	        System.out.println("⚠ Steps parsing failed");
	    }

	    return steps;
	}
	private static String cleanLine(String line) {

	    // ❌ remove invalid getByRole inside string
	    line = line.replaceAll("button:getByRole\\((.*?)\\)", "text=$1");

	    // ❌ remove invalid syntax
	    line = line.replaceAll("button:", "");
	    line = line.replaceAll("a:", "");

	    // ❌ fix missing quotes
	    line = line.replaceAll("text=([A-Za-z ]+)", "text=$1");

	    return line;
	}
}