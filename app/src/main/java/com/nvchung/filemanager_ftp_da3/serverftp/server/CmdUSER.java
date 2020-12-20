

package com.nvchung.filemanager_ftp_da3.serverftp.server;

import android.util.Log;

public class CmdUSER extends FtpCmd implements Runnable {
    private static final String TAG = CmdUSER.class.getSimpleName();

    protected String input;

    public CmdUSER(SessionThread sessionThread, String input) {
        super(sessionThread);
        this.input = input;

    }

    @Override
    public void run() {
        Log.d(TAG, "USER executing");
        String userName = FtpCmd.getParameter(input);
        if (!userName.matches("[A-Za-z0-9]+")) {
            sessionThread.writeString("530 Invalid username\r\n");
            return;
        }
        sessionThread.writeString("331 Send password\r\n");
        sessionThread.setUserName(userName);
    }

}
