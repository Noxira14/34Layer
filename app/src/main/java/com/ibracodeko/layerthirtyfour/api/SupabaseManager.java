package com.ibracodeko.layerthirtyfour.api;

import com.ibracodeko.layerthirtyfour.models.User;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SupabaseManager {
    private static final String SUPABASE_URL = "https://xdkwewqepaewfvuobkqy.supabase.co";
    private static final String SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inhka3dld3FlcGFld2Z2dW9ia3F5Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTgzMDIyODMsImV4cCI6MjA3Mzg3ODI4M30.4sF2LvmTKKiCZSWd62P62uyTvglzBJfnX3eHZGmVr0A";
    
    private ExecutorService executor;

    public interface AuthCallback {
        void onSuccess(User user);
        void onError(String error);
    }

    public SupabaseManager() {
        executor = Executors.newCachedThreadPool();
        // Initialize Supabase client here
        // Note: You would need to properly initialize Supabase client with the credentials
    }

    public void signIn(String username, String password, AuthCallback callback) {
        executor.execute(() -> {
            try {
                // Simulate API call delay
                Thread.sleep(2000);
                
                // Mock authentication - replace with actual Supabase auth
                if (isValidCredentials(username, password)) {
                    User user = createMockUser(username);
                    callback.onSuccess(user);
                } else {
                    callback.onError("Username atau password salah");
                }
            } catch (Exception e) {
                callback.onError("Terjadi kesalahan: " + e.getMessage());
            }
        });
    }

    public void signUp(String username, String email, String password, AuthCallback callback) {
        executor.execute(() -> {
            try {
                // Simulate API call delay
                Thread.sleep(2000);
                
                // Mock registration - replace with actual Supabase auth
                if (isUsernameAvailable(username) && isEmailAvailable(email)) {
                    User user = new User();
                    user.setId(generateUserId());
                    user.setUsername(username);
                    user.setEmail(email);
                    user.setUserType(User.UserType.USER); // Default to regular user
                    user.setCreatedAt(getCurrentTimestamp());
                    
                    callback.onSuccess(user);
                } else {
                    callback.onError("Username atau email sudah digunakan");
                }
            } catch (Exception e) {
                callback.onError("Terjadi kesalahan: " + e.getMessage());
            }
        });
    }

    public void signOut(AuthCallback callback) {
        executor.execute(() -> {
            try {
                // Simulate sign out
                Thread.sleep(1000);
                callback.onSuccess(null);
            } catch (Exception e) {
                callback.onError("Gagal logout: " + e.getMessage());
            }
        });
    }

    // Mock methods - replace with actual Supabase implementation
    private boolean isValidCredentials(String username, String password) {
        // Mock validation
        return !username.isEmpty() && password.length() >= 6;
    }

    private boolean isUsernameAvailable(String username) {
        // Mock availability check
        return !username.equalsIgnoreCase("admin") && !username.equalsIgnoreCase("test");
    }

    private boolean isEmailAvailable(String email) {
        // Mock availability check
        return !email.equalsIgnoreCase("admin@test.com");
    }

    private User createMockUser(String username) {
        User user = new User();
        user.setId(generateUserId());
        user.setUsername(username);
        user.setEmail(username + "@example.com");
        
        // Determine user type based on username
        if (username.equalsIgnoreCase("admin") || username.equalsIgnoreCase("developer")) {
            user.setUserType(User.UserType.ADMIN);
        } else if (username.toLowerCase().contains("premium")) {
            user.setUserType(User.UserType.PREMIUM);
        } else {
            user.setUserType(User.UserType.USER);
        }
        
        user.setCreatedAt(getCurrentTimestamp());
        return user;
    }

    private String generateUserId() {
        return "user_" + System.currentTimeMillis();
    }

    private String getCurrentTimestamp() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", 
            java.util.Locale.getDefault()).format(new java.util.Date());
    }
}