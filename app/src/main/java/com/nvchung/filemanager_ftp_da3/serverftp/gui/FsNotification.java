
package com.nvchung.filemanager_ftp_da3.serverftp.gui;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.nvchung.filemanager_ftp_da3.R;
import com.nvchung.filemanager_ftp_da3.serverftp.FsService;
import com.nvchung.filemanager_ftp_da3.serverftp.FsSettings;

import net.vrallev.android.cat.Cat;

import java.net.InetAddress;

import static android.content.Context.NOTIFICATION_SERVICE;

public class FsNotification {

    public static final int NOTIFICATION_ID = 7890;

    public static Notification setupNotification(Context context) {
        Cat.d("Setting up the notification");
        // Get NotificationManager reference
        NotificationManager nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        // get ip address
        InetAddress address = FsService.getLocalInetAddress();
        String ipText;
        if (address == null) {
            ipText = "-";
        } else {
            ipText = "ftp://" + address.getHostAddress() + ":"
                    + FsSettings.getPortNumber() + "/";
        }

        // Instantiate a Notification
        int icon = R.mipmap.notification;
        CharSequence tickerText = String.format(context.getString(R.string.notification_server_starting), ipText);
        long when = System.currentTimeMillis();

        // Define Notification's message and Intent
        CharSequence contentTitle = context.getString(R.string.notification_title);
        CharSequence contentText = String.format(context.getString(R.string.notification_text), ipText);

        Intent notificationIntent = new Intent(context, MainActivityFTP.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        int stopIcon = android.R.drawable.ic_menu_close_clear_cancel;
        CharSequence stopText = context.getString(R.string.notification_stop_text);
        Intent stopIntent = new Intent(context, FsService.class);
        stopIntent.setAction(FsService.ACTION_REQUEST_STOP);
        PendingIntent stopPendingIntent = PendingIntent.getService(context, 0,
                stopIntent, PendingIntent.FLAG_ONE_SHOT);

        int preferenceIcon = android.R.drawable.ic_menu_preferences;
        CharSequence preferenceText = context.getString(R.string.notif_settings_text);
        Intent preferenceIntent = new Intent(context, MainActivityFTP.class);
        preferenceIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent preferencePendingIntent = PendingIntent.getActivity(context, 0, preferenceIntent, 0);

        int priority = FsSettings.showNotificationIcon() ? Notification.PRIORITY_DEFAULT
                : Notification.PRIORITY_MIN;

        String channelId = "com.nvchung.filemanager_ftp_da3.serverftp.notification.channelId";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Show FTP Server state";
            String description = "This notification shows the current state of the FTP Server";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            nm.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setContentIntent(contentIntent)
                .setSmallIcon(icon)
                .setTicker(tickerText)
                .setWhen(when)
                .setOngoing(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setPriority(priority)
                .addAction(stopIcon, stopText, stopPendingIntent)
                .addAction(preferenceIcon, preferenceText, preferencePendingIntent)
                .setShowWhen(false)
                .setChannelId(channelId)
                .build();

        // Pass Notification to NotificationManager
        return notification;
    }

}
