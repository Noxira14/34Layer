package com.ibracodeko.layerthirtyfour.utils;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.ibracodeko.layerthirtyfour.R;

public class ThemeManager {
    private static ThemeManager instance;
    private PreferenceManager prefManager;
    private Context context;

    public static final int THEME_LIGHT = 0;
    public static final int THEME_DARK = 1;
    public static final int THEME_SYSTEM = 2;

    private ThemeManager() {}

    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    public void initialize(Context context) {
        this.context = context;
        this.prefManager = new PreferenceManager(context);
        applyTheme();
    }

    public void applyTheme() {
        int themeMode = prefManager.getThemeMode();
        switch (themeMode) {
            case THEME_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case THEME_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case THEME_SYSTEM:
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    public void applyTheme(AppCompatActivity activity) {
        int themeMode = prefManager.getThemeMode();
        switch (themeMode) {
            case THEME_LIGHT:
                activity.setTheme(R.style.AppTheme_Light);
                break;
            case THEME_DARK:
                activity.setTheme(R.style.AppTheme_Dark);
                break;
            case THEME_SYSTEM:
            default:
                activity.setTheme(R.style.AppTheme);
                break;
        }
    }

    public void setTheme(int themeMode) {
        prefManager.setThemeMode(themeMode);
        applyTheme();
    }

    public void toggleTheme() {
        int currentTheme = prefManager.getThemeMode();
        int newTheme = (currentTheme == THEME_LIGHT) ? THEME_DARK : THEME_LIGHT;
        setTheme(newTheme);
    }

    public int getCurrentTheme() {
        return prefManager.getThemeMode();
    }

    public boolean isDarkTheme() {
        return prefManager.getThemeMode() == THEME_DARK;
    }
}