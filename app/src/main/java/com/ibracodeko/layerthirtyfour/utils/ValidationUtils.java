package com.ibracodeko.layerthirtyfour.utils;

import android.util.Patterns;
import com.ibracodeko.layerthirtyfour.constants.ApiConstants;
import java.util.regex.Pattern;

public class ValidationUtils {
    
    // Email validation
    public static boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Password validation (minimum 6 characters)
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    // URL validation
    public static boolean isValidUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        
        // Add protocol if missing
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }
        
        return Patterns.WEB_URL.matcher(url).matches();
    }

    // Domain validation
    public static boolean isValidDomain(String domain) {
        if (domain == null || domain.trim().isEmpty()) {
            return false;
        }
        
        // Remove protocol if present
        domain = domain.replaceFirst("^https?://", "");
        
        // Domain name pattern
        String domainPattern = "^[a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(domainPattern);
        
        return pattern.matcher(domain).matches();
    }

    // Username validation
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        
        // Username: 3-20 characters, alphanumeric and underscore only
        String usernamePattern = "^[a-zA-Z0-9_]{3,20}$";
        Pattern pattern = Pattern.compile(usernamePattern);
        
        return pattern.matcher(username).matches();
    }

    // Time validation (1-300 seconds)
    public static boolean isValidTime(String timeStr) {
        try {
            int time = Integer.parseInt(timeStr);
            return time >= 1 && time <= 300;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // IP address validation
    public static boolean isValidIpAddress(String ip) {
        if (ip == null || ip.trim().isEmpty()) {
            return false;
        }
        
        return Patterns.IP_ADDRESS.matcher(ip).matches();
    }

    // Port validation
    public static boolean isValidPort(String portStr) {
        try {
            int port = Integer.parseInt(portStr);
            return port >= 1 && port <= 65535;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Method name validation
    public static boolean isValidMethodName(String method) {
        if (method == null || method.trim().isEmpty()) {
            return false;
        }
        
        // Method name: 3-50 characters, alphanumeric, space, and some special characters
        String methodPattern = "^[a-zA-Z0-9\\s\\-_]{3,50}$";
        Pattern pattern = Pattern.compile(methodPattern);
        
        return pattern.matcher(method).matches();
    }

    // API Method validation - check if method exists in available methods
    public static boolean isValidApiMethod(String method) {
        return method != null && ApiConstants.ALL_METHODS.contains(method);
    }

    // Sanitize input to prevent basic injection
    public static String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }
        
        // Remove potentially dangerous characters
        return input.replaceAll("[<>\"'%;()&+]", "").trim();
    }
}