

package com.nvchung.filemanager_ftp_da3.serverftp.server;

import android.util.Log;

import com.nvchung.filemanager_ftp_da3.serverftp.App;
import com.nvchung.filemanager_ftp_da3.serverftp.MediaUpdater;
import com.nvchung.filemanager_ftp_da3.serverftp.list_utils.FileUtil;

import java.io.File;

public class CmdDELE extends FtpCmd implements Runnable {
    private static final String TAG = CmdDELE.class.getSimpleName();

    protected String input;

    public CmdDELE(SessionThread sessionThread, String input) {
        super(sessionThread);
        this.input = input;
    }

    @Override
    public void run() {
        Log.d(TAG, "DELE executing");
        String param = getParameter(input);
        File storeFile = inputPathToChrootedFile(sessionThread.getChrootDir(), sessionThread.getWorkingDir(), param);
        String errString = null;
        if (violatesChroot(storeFile)) {
            errString = "550 Invalid name or chroot violation\r\n";
        } else if (storeFile.isDirectory()) {
            errString = "550 Can't DELE a directory\r\n";
        } else if (!FileUtil.deleteFile(storeFile, App.getAppContext())) {
            errString = "450 Error deleting file\r\n";
        }

        if (errString != null) {
            sessionThread.writeString(errString);
            Log.i(TAG, "DELE failed: " + errString.trim());
        } else {
            sessionThread.writeString("250 File successfully deleted\r\n");
            MediaUpdater.notifyFileDeleted(storeFile.getPath());
        }
        Log.d(TAG, "DELE finished");
    }

}
