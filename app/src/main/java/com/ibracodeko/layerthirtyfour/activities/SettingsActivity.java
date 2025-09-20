package com.ibracodeko.layerthirtyfour.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.ibracodeko.layerthirtyfour.R;
import com.ibracodeko.layerthirtyfour.models.User;
import com.ibracodeko.layerthirtyfour.utils.NotificationManager;
import com.ibracodeko.layerthirtyfour.utils.OfflineModeManager;
import com.ibracodeko.layerthirtyfour.utils.PreferenceManager;
import com.ibracodeko.layerthirtyfour.utils.SoundManager;
import com.ibracodeko.layerthirtyfour.utils.ThemeManager;

public class SettingsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Switch switchNotifications, switchSounds, switchVibration, switchOfflineMode;
    private CardView cardTheme, cardClearCache, cardExportData, cardAbout;
    private TextView tvCurrentTheme, tvCacheSize, tvUserInfo;
    private MaterialButton btnLogout;
    private LottieAnimationView settingsAnimation;
    
    private PreferenceManager prefManager;
    private NotificationManager notificationManager;
    private OfflineModeManager offlineManager;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.getInstance().applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initViews();
        initManagers();
        setupToolbar();
        loadSettings();
        setupClickListeners();
        startAnimation();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        switchNotifications = findViewById(R.id.switchNotifications);
        switchSounds = findViewById(R.id.switchSounds);
        switchVibration = findViewById(R.id.switchVibration);
        switchOfflineMode = findViewById(R.id.switchOfflineMode);
        cardTheme = findViewById(R.id.cardTheme);
        cardClearCache = findViewById(R.id.cardClearCache);
        cardExportData = findViewById(R.id.cardExportData);
        cardAbout = findViewById(R.id.cardAbout);
        tvCurrentTheme = findViewById(R.id.tvCurrentTheme);
        tvCacheSize = findViewById(R.id.tvCacheSize);
        tvUserInfo = findViewById(R.id.tvUserInfo);
        btnLogout = findViewById(R.id.btnLogout);
        settingsAnimation = findViewById(R.id.settingsAnimation);
    }

    private void initManagers() {
        prefManager = new PreferenceManager(this);
        notificationManager = new NotificationManager(this);
        offlineManager = OfflineModeManager.getInstance();
        currentUser = prefManager.getUser();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Pengaturan");
        
        toolbar.setNavigationOnClickListener(v -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_CLICK);
            onBackPressed();
        });
    }

    private void loadSettings() {
        // Load user info
        if (currentUser != null) {
            tvUserInfo.setText(currentUser.getUsername() + " (" + currentUser.getUserType() + ")");
        }

        // Load switch states
        switchNotifications.setChecked(notificationManager.isNotificationsEnabled());
        switchSounds.setChecked(SoundManager.getInstance().isSoundEnabled());
        switchVibration.setChecked(SoundManager.getInstance().isVibrationEnabled());
        switchOfflineMode.setChecked(offlineManager.isOfflineModeEnabled());

        // Load theme info
        updateThemeDisplay();
        
        // Load cache info
        updateCacheInfo();
    }

    private void setupClickListeners() {
        // Notification switch
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_CLICK);
            notificationManager.setNotificationsEnabled(isChecked);
            
            if (isChecked && !NotificationManager.hasNotificationPermission(this)) {
                showNotificationPermissionDialog();
            }
        });

        // Sound switch
        switchSounds.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_CLICK);
            SoundManager.getInstance().setSoundEnabled(isChecked);
        });

        // Vibration switch
        switchVibration.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_CLICK);
            SoundManager.getInstance().setVibrationEnabled(isChecked);
        });

        // Offline mode switch
        switchOfflineMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_CLICK);
            offlineManager.setOfflineModeEnabled(isChecked);
            
            if (isChecked) {
                Toast.makeText(this, "Mode offline diaktifkan. Data akan disimpan lokal saat tidak ada koneksi.", 
                    Toast.LENGTH_LONG).show();
            }
        });

        // Theme card
        cardTheme.setOnClickListener(v -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_CLICK);
            showThemeSelectionDialog();
        });

        // Clear cache card
        cardClearCache.setOnClickListener(v -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_CLICK);
            showClearCacheDialog();
        });

        // Export data card
        cardExportData.setOnClickListener(v -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_CLICK);
            exportUserData();
        });

        // About card
        cardAbout.setOnClickListener(v -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_CLICK);
            showAboutDialog();
        });

        // Logout button
        btnLogout.setOnClickListener(v -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_CLICK);
            showLogoutConfirmation();
        });
    }

    private void startAnimation() {
        settingsAnimation.setAnimation("settings_animation.json");
        settingsAnimation.playAnimation();
    }

    private void updateThemeDisplay() {
        int currentTheme = ThemeManager.getInstance().getCurrentTheme();
        String themeText;
        
        switch (currentTheme) {
            case ThemeManager.THEME_LIGHT:
                themeText = "Light";
                break;
            case ThemeManager.THEME_DARK:
                themeText = "Dark";
                break;
            case ThemeManager.THEME_SYSTEM:
            default:
                themeText = "System Default";
                break;
        }
        
        tvCurrentTheme.setText(themeText);
    }

    private void updateCacheInfo() {
        // Calculate cache size (simplified)
        long cacheSize = getCacheSize();
        String cacheSizeText = formatFileSize(cacheSize);
        tvCacheSize.setText(cacheSizeText);
    }

    private long getCacheSize() {
        // Simplified cache size calculation
        // In real implementation, you would calculate actual cache size
        return 1024 * 1024 * 5; // 5MB placeholder
    }

    private String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        int exp = (int) (Math.log(size) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", size / Math.pow(1024, exp), pre);
    }

    private void showThemeSelectionDialog() {
        String[] themeOptions = {"System Default", "Light", "Dark"};
        int currentSelection = ThemeManager.getInstance().getCurrentTheme();

        new MaterialAlertDialogBuilder(this)
            .setTitle("Pilih Theme")
            .setSingleChoiceItems(themeOptions, currentSelection, (dialog, which) -> {
                ThemeManager.getInstance().setTheme(which);
                updateThemeDisplay();
                dialog.dismiss();
                
                // Recreate activity to apply theme
                recreate();
            })
            .setNegativeButton("Batal", null)
            .show();
    }

    private void showClearCacheDialog() {
        new MaterialAlertDialogBuilder(this)
            .setTitle("Hapus Cache")
            .setMessage("Apakah Anda yakin ingin menghapus semua data cache? Ini akan menghapus data sementara seperti method cache dan pending results.")
            .setPositiveButton("Hapus", (dialog, which) -> {
                clearAllCache();
                SoundManager.getInstance().playSound(SoundManager.SOUND_SUCCESS);
                Toast.makeText(this, "Cache berhasil dihapus", Toast.LENGTH_SHORT).show();
                updateCacheInfo();
            })
            .setNegativeButton("Batal", null)
            .show();
    }

    private void clearAllCache() {
        // Clear offline manager cache
        offlineManager.clearPendingScanResults();
        
        // Clear app cache
        prefManager.clearCache();
        
        // Clear other cached data
        deleteCache();
    }

    private void deleteCache() {
        try {
            // Clear app cache directory
            deleteDir(getCacheDir());
            
            // Clear external cache if available
            if (getExternalCacheDir() != null) {
                deleteDir(getExternalCacheDir());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean deleteDir(java.io.File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String child : children) {
                    boolean success = deleteDir(new java.io.File(dir, child));
                    if (!success) {
                        return false;
                    }
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        }
        return false;
    }

    private void exportUserData() {
        if (currentUser == null) {
            Toast.makeText(this, "Tidak ada data user untuk diekspor", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create export data
        StringBuilder exportData = new StringBuilder();
        exportData.append("=== 34Layer User Data Export ===\n");
        exportData.append("Export Date: ").append(new java.util.Date().toString()).append("\n\n");
        exportData.append("User Information:\n");
        exportData.append("Username: ").append(currentUser.getUsername()).append("\n");
        exportData.append("Email: ").append(currentUser.getEmail()).append("\n");
        exportData.append("User Type: ").append(currentUser.getUserType()).append("\n");
        exportData.append("Created At: ").append(currentUser.getCreatedAt()).append("\n\n");
        
        exportData.append("Settings:\n");
        exportData.append("Notifications: ").append(switchNotifications.isChecked() ? "Enabled" : "Disabled").append("\n");
        exportData.append("Sounds: ").append(switchSounds.isChecked() ? "Enabled" : "Disabled").append("\n");
        exportData.append("Vibration: ").append(switchVibration.isChecked() ? "Enabled" : "Disabled").append("\n");
        exportData.append("Offline Mode: ").append(switchOfflineMode.isChecked() ? "Enabled" : "Disabled").append("\n");
        exportData.append("Theme: ").append(tvCurrentTheme.getText()).append("\n\n");
        
        exportData.append("=== End of Export ===\n");

        // Share export data
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, exportData.toString());
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "34Layer User Data Export");
        
        startActivity(Intent.createChooser(shareIntent, "Export User Data"));
        
        SoundManager.getInstance().playSound(SoundManager.SOUND_SUCCESS);
        Toast.makeText(this, "Data user berhasil diekspor", Toast.LENGTH_SHORT).show();
    }

    private void showAboutDialog() {
        new MaterialAlertDialogBuilder(this)
            .setTitle("Tentang 34Layer")
            .setMessage("34Layer v1.0\n\n" +
                       "Network Testing Suite untuk Android\n\n" +
                       "Fitur:\n" +
                       "• Web MultiPorting\n" +
                       "• IP Troubleshooting\n" +
                       "• Proxy Server Testing\n" +
                       "• Admin Panel\n" +
                       "• Offline Mode Support\n" +
                       "• Push Notifications\n\n" +
                       "Created by:\n" +
                       "Ibra Decode & Komodigi Project's\n\n" +
                       "© 2024 All Rights Reserved")
            .setPositiveButton("OK", null)
            .show();
    }

    private void showNotificationPermissionDialog() {
        new MaterialAlertDialogBuilder(this)
            .setTitle("Izin Notifikasi")
            .setMessage("Untuk menerima notifikasi dari 34Layer, silakan aktifkan izin notifikasi di pengaturan sistem.")
            .setPositiveButton("Pengaturan", (dialog, which) -> {
                // Open app settings
                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(android.net.Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            })
            .setNegativeButton("Nanti", null)
            .show();
    }

    private void showLogoutConfirmation() {
        new MaterialAlertDialogBuilder(this)
            .setTitle("Logout")
            .setMessage("Apakah Anda yakin ingin logout dari 34Layer?")
            .setPositiveButton("Logout", (dialog, which) -> {
                performLogout();
            })
            .setNegativeButton("Batal", null)
            .show();
    }

    private void performLogout() {
        // Clear user data
        prefManager.setLoggedIn(false);
        prefManager.clearUser();
        
        // Clear notifications
        notificationManager.cancelAllNotifications();
        
        // Clear cache
        clearAllCache();
        
        SoundManager.getInstance().playSound(SoundManager.SOUND_SUCCESS);
        Toast.makeText(this, "Logout berhasil", Toast.LENGTH_SHORT).show();
        
        // Navigate to login
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}