package com.nvchung.filemanager_ftp_da3.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BroadcastReceiverUtils {

    public static BroadcastReceiver createBroadcastReceiver(Receiver receiver) {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                receiver.onReceive(context, intent);
            }
        };
    }

    public interface Receiver {
        void onReceive(Context context, Intent intent);
    }

}
