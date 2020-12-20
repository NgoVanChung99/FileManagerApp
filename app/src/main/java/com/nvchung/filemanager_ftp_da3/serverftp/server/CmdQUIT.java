

package com.nvchung.filemanager_ftp_da3.serverftp.server;

import android.util.Log;

public class CmdQUIT extends FtpCmd implements Runnable {
    private static final String TAG = CmdQUIT.class.getSimpleName();

    public CmdQUIT(SessionThread sessionThread, String input) {
        super(sessionThread);
    }

    @Override
    public void run() {
        Log.d(TAG, "QUIT executing");
        sessionThread.writeString("221 Goodbye\r\n");
        sessionThread.closeSocket();
    }

}
