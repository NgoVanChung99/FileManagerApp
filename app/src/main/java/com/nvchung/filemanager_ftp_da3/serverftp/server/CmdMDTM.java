

package com.nvchung.filemanager_ftp_da3.serverftp.server;

import android.util.Log;

import com.nvchung.filemanager_ftp_da3.serverftp.Util;

import java.io.File;

/**
 * Implements File Modification Time
 */
public class CmdMDTM extends FtpCmd implements Runnable {
    private static final String TAG = CmdMDTM.class.getSimpleName();

    private String mInput;

    public CmdMDTM(SessionThread sessionThread, String input) {
        super(sessionThread);
        mInput = input;
    }

    @Override
    public void run() {
        Log.d(TAG, "run: MDTM executing, input: " + mInput);
        String param = getParameter(mInput);
        File file = inputPathToChrootedFile(sessionThread.getChrootDir(), sessionThread.getWorkingDir(), param);

        if (file.exists()) {
            long lastModified = file.lastModified();
            String response = "213 " + Util.getFtpDate(lastModified) + "\r\n";
            sessionThread.writeString(response);
        } else {
            Log.w(TAG, "run: file does not exist");
            sessionThread.writeString("550 file does not exist\r\n");
        }

        Log.d(TAG, "run: MDTM completed");
    }

}

