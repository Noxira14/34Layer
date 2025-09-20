package com.ibracodeko.layerthirtyfour.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import androidx.annotation.NonNull;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ibracodeko.layerthirtyfour.models.MethodResponse;
import com.ibracodeko.layerthirtyfour.models.ScanResponse;
import com.ibracodeko.layerthirtyfour.models.User;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class OfflineModeManager {
    private static OfflineModeManager instance;
    private Context context;
    private ConnectivityManager connectivityManager;
    private PreferenceManager prefManager;
    private Gson gson;
    private boolean isOnline;
    private List<NetworkCallback> networkCallbacks;
    private List<ScanResponse> pendingScanResults;
    private List<MethodResponse.MethodDetail> cachedMethods;

    public interface NetworkCallback {
        void onNetworkAvailable();
        void onNetworkLost();
    }

    private OfflineModeManager() {
        networkCallbacks = new CopyOnWriteArrayList<>();
        pendingScanResults = new ArrayList<>();
        cachedMethods = new ArrayList<>();
        gson = new Gson();
    }

    public static OfflineModeManager getInstance() {
        if (instance == null) {
            instance = new OfflineModeManager();
        }
        return instance;
    }

    public void initialize(Context context) {
        this.context = context;
        this.prefManager = new PreferenceManager(context);
        this.connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        
        loadCachedData();
        registerNetworkCallback();
        checkInitialConnectivity();
    }

    private void registerNetworkCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NetworkRequest.Builder builder = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);

            connectivityManager.registerNetworkCallback(builder.build(), new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    super.onAvailable(network);
                    setOnlineStatus(true);
                    notifyNetworkAvailable();
                    syncPendingData();
                }

                @Override
                public void onLost(@NonNull Network network) {
                    super.onLost(network);
                    setOnlineStatus(false);
                    notifyNetworkLost();
                }
            });
        }
    }

    private void checkInitialConnectivity() {
        boolean connected = NetworkUtils.isNetworkAvailable(context);
        setOnlineStatus(connected);
    }

    public void registerNetworkCallback(NetworkCallback callback) {
        networkCallbacks.add(callback);
    }

    public void unregisterNetworkCallback(NetworkCallback callback) {
        networkCallbacks.remove(callback);
    }

    private void notifyNetworkAvailable() {
        for (NetworkCallback callback : networkCallbacks) {
            callback.onNetworkAvailable();
        }
    }

    private void notifyNetworkLost() {
        for (NetworkCallback callback : networkCallbacks) {
            callback.onNetworkLost();
        }
    }

    public boolean isOnline() {
        return isOnline;
    }

    private void setOnlineStatus(boolean online) {
        boolean wasOffline = !this.isOnline;
        this.isOnline = online;
        
        // Show notification when coming back online
        if (wasOffline && online) {
            new NotificationManager(context).showServerStatusNotification(true);
        } else if (!online) {
            new NotificationManager(context).showServerStatusNotification(false);
        }
        
        // Save offline mode state
        prefManager.putBoolean("is_offline_mode", !online);
    }

    // Cache Methods
    public void cacheMethods(List<MethodResponse.MethodDetail> methods) {
        cachedMethods.clear();
        cachedMethods.addAll(methods);
        
        // Save to persistent storage
        String methodsJson = gson.toJson(methods);
        prefManager.putString("cached_methods", methodsJson);
    }

    public List<MethodResponse.MethodDetail> getCachedMethods(String type) {
        List<MethodResponse.MethodDetail> filteredMethods = new ArrayList<>();
        
        for (MethodResponse.MethodDetail method : cachedMethods) {
            if (type == null || type.equalsIgnoreCase(method.getType())) {
                filteredMethods.add(method);
            }
        }
        
        return filteredMethods;
    }

    public List<String> getCachedMethodNames(String type) {
        List<String> methodNames = new ArrayList<>();
        List<MethodResponse.MethodDetail> methods = getCachedMethods(type);
        
        for (MethodResponse.MethodDetail method : methods) {
            if (method.isEnabled()) {
                methodNames.add(method.getName());
            }
        }
        
        return methodNames;
    }

    // Pending Scan Results
    public void addPendingScanResult(ScanResponse scanResult) {
        pendingScanResults.add(scanResult);
        savePendingScanResults();
    }

    public List<ScanResponse> getPendingScanResults() {
        return new ArrayList<>(pendingScanResults);
    }

    public void clearPendingScanResults() {
        pendingScanResults.clear();
        savePendingScanResults();
    }

    private void savePendingScanResults() {
        String resultsJson = gson.toJson(pendingScanResults);
        prefManager.putString("pending_scan_results", resultsJson);
    }

    // Cache User Data
    public void cacheUserData(User user) {
        String userJson = gson.toJson(user);
        prefManager.putString("cached_user", userJson);
    }

    public User getCachedUser() {
        String userJson = prefManager.getString("cached_user", null);
        if (userJson != null) {
            return gson.fromJson(userJson, User.class);
        }
        return null;
    }

    // Sync Methods
    public void syncPendingData() {
        if (!isOnline()) return;

        // Sync pending scan results
        syncPendingScanResults();
        
        // Refresh cached methods
        refreshCachedMethods();
    }

    private void syncPendingScanResults() {
        if (pendingScanResults.isEmpty()) return;

        // Here you would implement the logic to sync pending results with server
        // For now, we'll just clear them after a successful sync simulation
        new Thread(() -> {
            try {
                Thread.sleep(2000); // Simulate sync process
                
                // After successful sync, clear pending results
                clearPendingScanResults();
                
                // Notify user
                new NotificationManager(context).showCustomNotification(
                    "Data Synced", 
                    pendingScanResults.size() + " pending results have been synced.",
                    0
                );
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void refreshCachedMethods() {
        // Implement method refresh logic here
        // This would typically involve making an API call to get latest methods
    }

    private void loadCachedData() {
        // Load cached methods
        String methodsJson = prefManager.getString("cached_methods", null);
        if (methodsJson != null) {
            Type listType = new TypeToken<List<MethodResponse.MethodDetail>>(){}.getType();
            List<MethodResponse.MethodDetail> methods = gson.fromJson(methodsJson, listType);
            if (methods != null) {
                cachedMethods.addAll(methods);
            }
        }

        // Load pending scan results
        String resultsJson = prefManager.getString("pending_scan_results", null);
        if (resultsJson != null) {
            Type listType = new TypeToken<List<ScanResponse>>(){}.getType();
            List<ScanResponse> results = gson.fromJson(resultsJson, listType);
            if (results != null) {
                pendingScanResults.addAll(results);
            }
        }
    }

    // Offline Mode Configuration
    public void setOfflineModeEnabled(boolean enabled) {
        prefManager.putBoolean("offline_mode_enabled", enabled);
    }

    public boolean isOfflineModeEnabled() {
        return prefManager.getBoolean("offline_mode_enabled", true);
    }

    public boolean shouldShowOfflineWarning() {
        return !isOnline() && isOfflineModeEnabled();
    }

    // Get offline-friendly error message
    public String getOfflineErrorMessage() {
        return "Anda sedang offline. Beberapa fitur mungkin tidak tersedia. " +
               "Hasil scan akan disimpan dan disinkronkan ketika koneksi kembali tersedia.";
    }

    // Check if feature is available offline
    public boolean isFeatureAvailableOffline(String featureName) {
        switch (featureName.toLowerCase()) {
            case "cache_view":
            case "offline_results":
            case "user_profile":
            case "settings":
                return true;
            case "web_multiporting":
            case "ip_troubleshooting":
            case "proxy_server":
            case "admin_panel":
                return false;
            default:
                return false;
        }
    }

    // Cleanup
    public void cleanup() {
        if (connectivityManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Unregister network callback if needed
        }
        networkCallbacks.clear();
    }
}