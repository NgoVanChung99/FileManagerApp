

package com.nvchung.filemanager_ftp_da3.serverftp.server;

import android.os.Build;

import com.nvchung.filemanager_ftp_da3.serverftp.App;
import com.nvchung.filemanager_ftp_da3.serverftp.list_utils.FileUtil;

import net.vrallev.android.cat.Cat;

import java.io.File;
import java.io.IOException;

/**
 * CmdRNTO implements RENAME TO (RNTO)
 * This command specifies the new pathname of the file
 * specified in the immediately preceding "rename from"
 * command. Together the two commands cause a file to be
 * renamed.
 */
public class CmdRNTO extends FtpCmd implements Runnable {

    protected String input;

    public CmdRNTO(SessionThread sessionThread, String input) {
        super(sessionThread);
        this.input = input;
    }

    @Override
    public void run() {
        Cat.d("RNTO executing");
        String param = getParameter(input);
        String errString = null;
        File toFile = null;
        mainblock:
        {
            toFile = inputPathToChrootedFile(sessionThread.getChrootDir(), sessionThread.getWorkingDir(), param);
            Cat.i("RNTO to file: " + toFile.getPath());
            if (violatesChroot(toFile)) {
                errString = "550 Invalid name or chroot violation\r\n";
                break mainblock;
            }
            File fromFile = sessionThread.getRenameFrom();
            if (fromFile == null) {
                errString = "550 Rename error, maybe RNFR not sent\r\n";
                break mainblock;
            }
            Cat.i("RNTO from file: " + fromFile.getPath());

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                // TODO: this code is working around a bug that java6 and before cannot
                // reliable move a file, once java7 is supported by Dalvik, this code can
                // be replaced with Files.move()
                File tmpFile = null;
                try {
                    tmpFile = File.createTempFile("temp_" + fromFile.getName(), null,
                            sessionThread.getWorkingDir());
                    if (fromFile.isDirectory()) {
                        String tmpFilePath = tmpFile.getPath();
                        tmpFile.delete();
                        tmpFile = new File(tmpFilePath);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    errString = "550 Error during rename operation\r\n";
                    break mainblock;
                }
                if (!fromFile.renameTo(tmpFile)) {
                    errString = "550 Error during rename operation\r\n";
                    break mainblock;
                }
                fromFile.delete();
                if (!tmpFile.renameTo(toFile)) {
                    errString = "550 Error during rename operation\r\n";
                    break mainblock;
                }
            } else {
                if (fromFile.isDirectory()) {
                    FileUtil.renameFolder(fromFile, toFile, App.getAppContext());
                } else {
                    FileUtil.moveFile(fromFile, toFile, App.getAppContext());
                }
            }

        }
        if (errString != null) {
            sessionThread.writeString(errString);
            Cat.i("RNFR failed: " + errString.trim());
        } else {
            sessionThread.writeString("250 rename successful\r\n");
        }
        sessionThread.setRenameFrom(null);
        Cat.d("RNTO finished");
    }
}
