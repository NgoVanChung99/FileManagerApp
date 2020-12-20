

package com.nvchung.filemanager_ftp_da3.serverftp.server;

import net.vrallev.android.cat.Cat;

import java.io.File;

/**
 * CmdRNFR implements RENAME FROM (RNFR)
 * This command specifies the old pathname of the file which is
 * to be renamed. This command must be immediately followed by
 * a RNTO command specifying the new file pathname.
 */
public class CmdRNFR extends FtpCmd implements Runnable {

    protected String input;

    public CmdRNFR(SessionThread sessionThread, String input) {
        super(sessionThread);
        this.input = input;
    }

    @Override
    public void run() {
        Cat.d("Executing RNFR");
        String param = getParameter(input);
        String errString = null;
        File file = null;
        mainblock:
        {
            file = inputPathToChrootedFile(sessionThread.getChrootDir(), sessionThread.getWorkingDir(), param);
            if (violatesChroot(file)) {
                errString = "550 Invalid name or chroot violation\r\n";
                break mainblock;
            }
            if (!file.exists()) {
                errString = "450 Cannot rename nonexistent file\r\n";
            }
        }
        if (errString != null) {
            sessionThread.writeString(errString);
            Cat.d("RNFR failed: " + errString.trim());
            sessionThread.setRenameFrom(null);
        } else {
            sessionThread.writeString("350 Filename noted, now send RNTO\r\n");
            sessionThread.setRenameFrom(file);
        }
    }
}
