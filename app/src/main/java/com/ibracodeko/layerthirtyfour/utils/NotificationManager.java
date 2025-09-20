package com.ibracodeko.layerthirtyfour.utils;

import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.ibracodeko.layerthirtyfour.R;
import com.ibracodeko.layerthirtyfour.activities.DashboardActivity;

public class NotificationManager {
    private static final String CHANNEL_ID = "34layer_channel";
    private static final String CHANNEL_NAME = "34Layer Notifications";
    private static final String CHANNEL_DESCRIPTION = "Notifications for 34Layer app";
    private static final int NOTIFICATION_ID_SCAN_COMPLETE = 1001;
    private static final int NOTIFICATION_ID_SERVER_STATUS = 1002;
    private static final int NOTIFICATION_ID_METHOD_UPDATE = 1003;

    private Context context;
    private android.app.NotificationManager notificationManager;
    private PreferenceManager prefManager;

    public NotificationManager(Context context) {
        this.context = context;
        this.notificationManager = (android.app.NotificationManager) 
            context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.prefManager = new PreferenceManager(context);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                android.app.NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(CHANNEL_DESCRIPTION);
            channel.enableLights(true);
            channel.setLightColor(context.getColor(R.color.primary_color));
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400});
            
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void showScanCompleteNotification(String scanType, String status, String target) {
        if (!areNotificationsEnabled()) return;

        Intent intent = new Intent(context, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 0, intent, 
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE : 
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(largeIcon)
            .setContentTitle("Scan Selesai - " + scanType)
            .setContentText("Status: " + status + " | Target: " + target)
            .setStyle(new NotificationCompat.BigTextStyle()
                .bigText("Scan " + scanType + " telah selesai.\nStatus: " + status + "\nTarget: " + target + "\nTap untuk melihat hasil lengkap."))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(new long[]{100, 200, 300})
            .setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI);

        if (status.equalsIgnoreCase("success")) {
            builder.setColor(context.getColor(R.color.green_500));
        } else {
            builder.setColor(context.getColor(R.color.red_500));
        }

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_SCAN_COMPLETE, builder.build());
            SoundManager.getInstance().playSound(SoundManager.SOUND_NOTIFICATION);
        } catch (SecurityException e) {
            // Notification permission not granted
        }
    }

    public void showServerStatusNotification(boolean isOnline) {
        if (!areNotificationsEnabled()) return;

        String title = isOnline ? "Server Online" : "Server Offline";
        String message = isOnline ? 
            "34Layer server kembali online dan siap digunakan." :
            "34Layer server sedang offline. Beberapa fitur mungkin tidak tersedia.";

        Intent intent = new Intent(context, DashboardActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE : 
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(isOnline ? R.drawable.ic_server_online : R.drawable.ic_server_offline)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setColor(context.getColor(isOnline ? R.color.green_500 : R.color.red_500));

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_SERVER_STATUS, builder.build());
        } catch (SecurityException e) {
            // Notification permission not granted
        }
    }

    public void showMethodUpdateNotification(int newMethodsCount) {
        if (!areNotificationsEnabled()) return;

        String title = "Method Baru Tersedia";
        String message = newMethodsCount + " method testing baru telah ditambahkan. Update aplikasi untuk menggunakan fitur terbaru.";

        Intent intent = new Intent(context, DashboardActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE : 
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_update)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setColor(context.getColor(R.color.primary_color));

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_METHOD_UPDATE, builder.build());
        } catch (SecurityException e) {
            // Notification permission not granted
        }
    }

    public void showCustomNotification(String title, String message, int iconResId) {
        if (!areNotificationsEnabled()) return;

        Intent intent = new Intent(context, DashboardActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE : 
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(iconResId != 0 ? iconResId : R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setColor(context.getColor(R.color.primary_color));

        try {
            NotificationManagerCompat.from(context).notify(
                (int) System.currentTimeMillis(), builder.build());
        } catch (SecurityException e) {
            // Notification permission not granted
        }
    }

    public void cancelAllNotifications() {
        notificationManager.cancelAll();
    }

    public void cancelNotification(int notificationId) {
        notificationManager.cancel(notificationId);
    }

    private boolean areNotificationsEnabled() {
        // Check user preference
        boolean userEnabled = prefManager.getBoolean("notifications_enabled", true);
        
        // Check system permission
        boolean systemEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled();
        
        return userEnabled && systemEnabled;
    }

    public static boolean hasNotificationPermission(Context context) {
        return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }

    public void setNotificationsEnabled(boolean enabled) {
        prefManager.putBoolean("notifications_enabled", enabled);
    }

    public boolean isNotificationsEnabled() {
        return prefManager.getBoolean("notifications_enabled", true);
    }
}