
package com.nvchung.filemanager_ftp_da3.serverftp;

import android.app.Application;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.nvchung.filemanager_ftp_da3.R;
import com.nvchung.filemanager_ftp_da3.serverftp.gui.FsWidgetProvider;

import net.vrallev.android.cat.Cat;

import static androidx.core.app.NotificationCompat.PRIORITY_MIN;
import static com.nvchung.filemanager_ftp_da3.android.BroadcastReceiverUtils.createBroadcastReceiver;

public class App extends Application {

    private static App mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FsService.ACTION_STARTED);
        intentFilter.addAction(FsService.ACTION_STOPPED);
        intentFilter.addAction(FsService.ACTION_FAILEDTOSTART);

        registerReceiver(new AutoConnect.ServerActionsReceiver(), intentFilter);
        registerReceiver(new NsdService.ServerActionsReceiver(), intentFilter);
        registerReceiver(new FsWidgetProvider(), intentFilter);

        AutoConnect.maybeStartService(this);
    }

    /**
     * @return the Context of this application
     */
    public static Context getAppContext() {
        return mInstance.getApplicationContext();
    }

    /**
     * @return true if this is the free version
     */
    public static boolean isFreeVersion() {
        try {
            Context context = getAppContext();
            return context.getPackageName().contains("free");
        } catch (Exception ignored) {
        }
        return false;
    }

    /**
     * @return true if the paid version is installed on this device
     */
    public static boolean isPaidVersionInstalled() {
        return isPackageInstalled("com.nvchung.filemanager_ftp_da3.serverftp");
    }

    /**
     * @param packageName is the name of the package to check
     * @return true if packageName is installed on this device
     */
    public static boolean isPackageInstalled(String packageName) {
        try {
            Context context = getAppContext();
            PackageManager packageManager = context.getPackageManager();
            packageManager.getPackageInfo(packageName, 0);
        } catch (NameNotFoundException e) {
            return false;
        }
        return true;
    }

    /**
     * Get the version from the manifest.
     *
     * @return The version as a String.
     */
    public static String getVersion() {
        Context context = getAppContext();
        String packageName = context.getPackageName();
        try {
            PackageManager pm = context.getPackageManager();
            return pm.getPackageInfo(packageName, 0).versionName;
        } catch (NameNotFoundException e) {
            Cat.e("Unable to find the name " + packageName + " in the package");
            return null;
        }
    }

    /**
     * The class AutoConnect is responsible to start or stop the server depending on network
     * changesimport com.nvchung.filemanager_ftp_da3.R;.
     */
    public static class AutoConnect {

        static final int NOTIFICATION_ID = 20;

        /**
         * Start listening for different wifi connections. When this service
         * is not needed, does not start. When it does start, this needs to
         * run as a foreground service.
         *
         * @param context
         */
        public static void maybeStartService(Context context) {
            Intent autoConnectIntent = new Intent(context,
                    BackgroundService.class);
            if (FsSettings.getAutoConnectList().isEmpty()) {
                context.stopService(autoConnectIntent);
                return;
            }
            ContextCompat.startForegroundService(context, autoConnectIntent);
        }

        public static class BackgroundService extends Service {

            @Override
            public IBinder onBind(Intent intent) {
                return null;
            }

            @Override
            public int onStartCommand(Intent intent, int flags, int startId) {

                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                String channelId = "com.nvchung.filemanager_ftp_da3.serverftp.autoconnect.channelId";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    CharSequence name = "Check the connected wifi networks";
                    String description = "This notification checks the wifi networks and starts/stops the FTP Server when needed.";
                    int importance = NotificationManager.IMPORTANCE_NONE;
                    NotificationChannel channel = new NotificationChannel(channelId, name, importance);
                    channel.setDescription(description);
                    nm.createNotificationChannel(channel);
                }

                Notification notification = new NotificationCompat.Builder(this)
                        .setContentTitle("FTP Server")
                        .setContentText("Auto connect background service")
                        .setSmallIcon(R.mipmap.notification)
                        .setOngoing(true)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setCategory(NotificationCompat.CATEGORY_SERVICE)
                        .setPriority(PRIORITY_MIN)
                        .setShowWhen(false)
                        .setChannelId(channelId)
                        .build();

                startForeground(AutoConnect.NOTIFICATION_ID, notification);

                IntentFilter wifiStateChangedFilter = new IntentFilter();
                wifiStateChangedFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
                wifiStateChangedFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
                registerReceiver(mWifiStateChangedReceiver, wifiStateChangedFilter);

                return START_STICKY;
            }

            @Override
            public void onDestroy() {
                super.onDestroy();
                stopForeground(true);

                try {
                    unregisterReceiver(mWifiStateChangedReceiver);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        }

        private static BroadcastReceiver mWifiStateChangedReceiver = createBroadcastReceiver(
                (context, intent) -> {
                    NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    if (info == null) {
                        Cat.e("Null network info received, bailing");
                        return;
                    }
                    if (info.isConnected()) {
                        Cat.d("We are connecting to a wifi network");
                        Intent startServerIntent = new Intent(context, StartServerService.class);
                        context.startService(startServerIntent);
                    } else if (info.isConnectedOrConnecting()) {
                        Cat.v("Still connecting, ignoring");
                    } else {
                        Cat.d("We are disconnected from wifi network");
                        Intent stopServerIntent = new Intent(context, StopServerService.class);
                        context.startService(stopServerIntent);
                    }
                });

        /**
         * When the ftp stops, for instance when the user stops the server
         * and when we are still connected to an auto connection network
         * we should stop from auto connecting to this network.
         */
        static public class ServerActionsReceiver extends BroadcastReceiver {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() != null
                        && intent.getAction().equals(FsService.ACTION_STOPPED)
                        && FsSettings.getAutoConnectList().contains(getConnectedWifiSSID(context))) {
                    Toast.makeText(context,
                            context.getString(R.string.auto_connect_stop_server_remove_from_list),
                            Toast.LENGTH_LONG)
                            .show();
                    FsSettings.removeFromAutoConnectList(getConnectedWifiSSID(context));
                }
            }
        }

        static public class StartServerService extends IntentService {

            public StartServerService() {
                super(StartServerService.class.getSimpleName());
            }

            @Override
            protected void onHandleIntent(Intent intent) {
                if (FsService.isRunning()) {
                    Cat.v("We are connecting to a new wifi network on a running server, ignore");
                    return;
                }
                if (FsSettings.getAutoConnectList().contains(getConnectedWifiSSID(this))) {
                    // sleep a short while so the network has time to truly connect
                    Util.sleepIgnoreInterrupt(1000);
                    FsService.start();
                }
            }
        }

        static public class StopServerService extends IntentService {

            public StopServerService() {
                super(StopServerService.class.getSimpleName());
            }

            @Override
            protected void onHandleIntent(Intent intent) {
                if (!FsService.isRunning()) {
                    Cat.d("Server no longer running, no need to stop.");
                    return;
                }

                ConnectivityManager conManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = conManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (netInfo.isConnectedOrConnecting()) return;

                Cat.d("Wifi connection disconnected and no longer connecting, stopping the ftp server");
                FsService.stop();
            }
        }

        private static String getConnectedWifiSSID(Context context) {
            Context appContext = context.getApplicationContext();
            WifiManager wifiManager = (WifiManager) appContext.getSystemService(WIFI_SERVICE);
            if (wifiManager == null) {
                Cat.e("Unable to get the WifiManager");
                return "";
            }
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo == null) {
                Cat.e("Null wifi info received, bailing");
                return "";
            }
            Cat.d("We are connected to " + wifiInfo.getSSID());
            return wifiInfo.getSSID();
        }
    }
}
