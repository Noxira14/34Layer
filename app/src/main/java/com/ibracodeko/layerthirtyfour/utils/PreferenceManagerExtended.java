package com.ibracodeko.layerthirtyfour.utils;

import android.content.Context;

/**
 * Extended PreferenceManager with additional utility methods
 */
public class PreferenceManagerExtended extends PreferenceManager {
    
    public PreferenceManagerExtended(Context context) {
        super(context);
    }

    // Additional boolean methods
    public void putBoolean(String key, boolean value) {
        preferences.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }

    // Additional string methods
    public void putString(String key, String value) {
        preferences.edit().putString(key, value).apply();
    }

    public String getString(String key, String defaultValue) {
        return preferences.getString(key, defaultValue);
    }

    // Additional int methods
    public void putInt(String key, int value) {
        preferences.edit().putInt(key, value).apply();
    }

    public int getInt(String key, int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    // Additional long methods
    public void putLong(String key, long value) {
        preferences.edit().putLong(key, value).apply();
    }

    public long getLong(String key, long defaultValue) {
        return preferences.getLong(key, defaultValue);
    }

    // Additional float methods
    public void putFloat(String key, float value) {
        preferences.edit().putFloat(key, value).apply();
    }

    public float getFloat(String key, float defaultValue) {
        return preferences.getFloat(key, defaultValue);
    }

    // Remove specific key
    public void remove(String key) {
        preferences.edit().remove(key).apply();
    }

    // Check if key exists
    public boolean contains(String key) {
        return preferences.contains(key);
    }

    // Clear cache data only (not user data)
    public void clearCache() {
        preferences.edit()
            .remove("cached_methods")
            .remove("pending_scan_results")
            .remove("server_status_cache")
            .remove("last_sync_time")
            .apply();
    }

    // App configuration keys
    private static final String KEY_FIRST_LAUNCH = "first_launch";
    private static final String KEY_APP_VERSION = "app_version";
    private static final String KEY_LAST_UPDATE_CHECK = "last_update_check";
    private static final String KEY_TUTORIAL_SHOWN = "tutorial_shown";

    public boolean isFirstLaunch() {
        boolean isFirst = getBoolean(KEY_FIRST_LAUNCH, true);
        if (isFirst) {
            putBoolean(KEY_FIRST_LAUNCH, false);
        }
        return isFirst;
    }

    public void setAppVersion(String version) {
        putString(KEY_APP_VERSION, version);
    }

    public String getAppVersion() {
        return getString(KEY_APP_VERSION, "1.0");
    }

    public void setLastUpdateCheck(long timestamp) {
        putLong(KEY_LAST_UPDATE_CHECK, timestamp);
    }

    public long getLastUpdateCheck() {
        return getLong(KEY_LAST_UPDATE_CHECK, 0);
    }

    public boolean wasTutorialShown() {
        return getBoolean(KEY_TUTORIAL_SHOWN, false);
    }

    public void setTutorialShown(boolean shown) {
        putBoolean(KEY_TUTORIAL_SHOWN, shown);
    }

    // Performance tracking
    public void incrementLaunchCount() {
        int count = getInt("launch_count", 0);
        putInt("launch_count", count + 1);
    }

    public int getLaunchCount() {
        return getInt("launch_count", 0);
    }

    public void setLastActiveTime(long timestamp) {
        putLong("last_active_time", timestamp);
    }

    public long getLastActiveTime() {
        return getLong("last_active_time", System.currentTimeMillis());
    }
}