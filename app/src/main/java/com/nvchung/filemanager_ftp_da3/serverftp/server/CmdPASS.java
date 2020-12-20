

package com.nvchung.filemanager_ftp_da3.serverftp.server;

import android.util.Log;

import com.nvchung.filemanager_ftp_da3.serverftp.FsSettings;
import com.nvchung.filemanager_ftp_da3.serverftp.Util;

public class CmdPASS extends FtpCmd implements Runnable {
    private static final String TAG = CmdPASS.class.getSimpleName();

    String input;

    public CmdPASS(SessionThread sessionThread, String input) {
        super(sessionThread);
        this.input = input;
    }

    @Override
    public void run() {
        Log.d(TAG, "Executing PASS");
        String attemptPassword = getParameter(input, true); // silent
        // Always first USER command, then PASS command
        String attemptUsername = sessionThread.getUserName();
        if (attemptUsername == null) {
            sessionThread.writeString("503 Must send USER first\r\n");
            return;
        }
        if (attemptUsername.equals("anonymous") && FsSettings.allowAnonymous()) {
            Log.i(TAG, "Guest logged in with email: " + attemptPassword);
            sessionThread.writeString("230 Guest login ok, read only access.\r\n");
            return;
        }
        FtpUser user = FsSettings.getUser(attemptUsername);
        if (user == null) {
            Log.i(TAG, "Failed authentication, username does not exist!");
            Util.sleepIgnoreInterrupt(1000); // sleep to foil brute force attack
            sessionThread.writeString("500 Login incorrect.\r\n");
            sessionThread.authAttempt( false);
        } else if (user.getPassword().equals(attemptPassword)) {
            Log.i(TAG, "User " + user.getUsername() + " password verified");
            sessionThread.writeString("230 Access granted\r\n");
            sessionThread.authAttempt(true);
            sessionThread.setChrootDir(user.getChroot());
        } else {
            Log.i(TAG, "Failed authentication, incorrect password");
            Util.sleepIgnoreInterrupt(1000); // sleep to foil brute force attack
            sessionThread.writeString("530 Login incorrect.\r\n");
            sessionThread.authAttempt(false);
        }
    }
}
