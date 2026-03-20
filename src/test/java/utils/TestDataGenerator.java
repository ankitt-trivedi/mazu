package utils;

public class TestDataGenerator {

    public static String getValue(String field) {

        field = field.toLowerCase();

        if (field.contains("email")) return "test" + System.currentTimeMillis() + "@mail.com";
        if (field.contains("mobile")) return "9" + (long)(Math.random()*1000000000L);
        if (field.contains("name")) return "Test User";
        if (field.contains("amount")) return "1000";
        if (field.contains("password")) return "Password@123";

        return "test";
    }
}