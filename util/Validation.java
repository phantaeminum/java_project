package util;

import java.util.regex.Pattern;

public class Validation {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");
    private static final Pattern AADHAR_PATTERN = Pattern.compile("^\\d{12}$");
    
    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;
        return PHONE_PATTERN.matcher(phone).matches();
    }
    
    public static boolean isValidAadhar(String aadhar) {
        if (aadhar == null) return false;
        return AADHAR_PATTERN.matcher(aadhar).matches();
    }
    
    public static boolean isValidServingCount(int count) {
        return count > 0;
    }
    
    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty();
    }
}