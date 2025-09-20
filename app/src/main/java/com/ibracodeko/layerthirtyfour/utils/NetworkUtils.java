package com.ibracodeko.layerthirtyfour.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkUtils {
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public interface ServerStatusCallback {
        void onResult(boolean isOnline);
    }

    public interface NetworkCallback {
        void onResult(boolean isConnected);
    }

    // Check if device is connected to internet
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = 
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        
        if (connectivityManager == null) {
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            android.net.Network network = connectivityManager.getActiveNetwork();
            if (network == null) {
                return false;
            }
            
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            return capabilities != null && (
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            );
        } else {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
    }

    // Check server status asynchronously
    public static void checkServerStatus(String serverUrl, ServerStatusCallback callback) {
        executor.execute(() -> {
            boolean isOnline = false;
            try {
                URL url = new URL(serverUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("HEAD");
                connection.setConnectTimeout(5000); // 5 seconds
                connection.setReadTimeout(5000);
                connection.connect();
                
                int responseCode = connection.getResponseCode();
                isOnline = (responseCode >= 200 && responseCode < 400);
                
                connection.disconnect();
            } catch (IOException e) {
                isOnline = false;
            }
            
            callback.onResult(isOnline);
        });
    }

    // Check network connectivity asynchronously
    public static void checkNetworkConnectivity(Context context, NetworkCallback callback) {
        executor.execute(() -> {
            boolean isConnected = isNetworkAvailable(context);
            callback.onResult(isConnected);
        });
    }

    // Ping a specific host
    public static void pingHost(String host, int timeout, ServerStatusCallback callback) {
        executor.execute(() -> {
            boolean isReachable = false;
            try {
                Process process = Runtime.getRuntime().exec("ping -c 1 -W " + (timeout / 1000) + " " + host);
                int returnValue = process.waitFor();
                isReachable = (returnValue == 0);
            } catch (Exception e) {
                isReachable = false;
            }
            
            callback.onResult(isReachable);
        });
    }

    // Check if URL is reachable
    public static void isUrlReachable(String urlString, ServerStatusCallback callback) {
        executor.execute(() -> {
            boolean isReachable = false;
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000); // 10 seconds
                connection.setReadTimeout(10000);
                connection.connect();
                
                int responseCode = connection.getResponseCode();
                isReachable = (responseCode >= 200 && responseCode < 400);
                
                connection.disconnect();
            } catch (Exception e) {
                isReachable = false;
            }
            
            callback.onResult(isReachable);
        });
    }

    // Get connection type
    public static String getConnectionType(Context context) {
        ConnectivityManager connectivityManager = 
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        
        if (connectivityManager == null) {
            return "No Connection";
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            android.net.Network network = connectivityManager.getActiveNetwork();
            if (network == null) {
                return "No Connection";
            }
            
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            if (capabilities == null) {
                return "No Connection";
            }
            
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return "WiFi";
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return "Mobile Data";
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return "Ethernet";
            }
        } else {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    return "WiFi";
                } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    return "Mobile Data";
                }
            }
        }
        
        return "Unknown";
    }
}