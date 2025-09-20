package com.ibracodeko.layerthirtyfour.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import com.google.gson.Gson;
import com.ibracodeko.layerthirtyfour.models.User;

public class PreferenceManager {
    private static final String PREF_NAME = "34layer_prefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_DATA = "user_data";
    private static final String KEY_THEME_MODE = "theme_mode";
    
    protected SharedPreferences preferences;
    private Gson gson;

    public PreferenceManager(Context context) {
        gson = new Gson();
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

            preferences = EncryptedSharedPreferences.create(
                context,
                PREF_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            // Fallback to regular SharedPreferences
            preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
    }

    public void setLoggedIn(boolean loggedIn) {
        preferences.edit().putBoolean(KEY_IS_LOGGED_IN, loggedIn).apply();
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void saveUser(User user) {
        String userJson = gson.toJson(user);
        preferences.edit().putString(KEY_USER_DATA, userJson).apply();
    }

    public User getUser() {
        String userJson = preferences.getString(KEY_USER_DATA, null);
        if (userJson != null) {
            return gson.fromJson(userJson, User.class);
        }
        return null;
    }

    public void clearUser() {
        preferences.edit().remove(KEY_USER_DATA).apply();
    }

    public void setThemeMode(int themeMode) {
        preferences.edit().putInt(KEY_THEME_MODE, themeMode).apply();
    }

    public int getThemeMode() {
        return preferences.getInt(KEY_THEME_MODE, 0); // 0 = system default
    }

    public void clearAll() {
        preferences.edit().clear().apply();
    }

    // Tambahan agar kompatibel dengan pemanggilan di kode lain
    public boolean getBoolean(String key, boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }

    public void putBoolean(String key, boolean value) {
        preferences.edit().putBoolean(key, value).apply();
    }

    public String getString(String key, String defaultValue) {
        return preferences.getString(key, defaultValue);
    }

    public void putString(String key, String value) {
        preferences.edit().putString(key, value).apply();
    }

    public void clearCache() {
        // Implementasi clear cache sederhana, bisa dikembangkan sesuai kebutuhan
        preferences.edit().clear().apply();
    }
}