

package com.nvchung.filemanager_ftp_da3.serverftp.server;

public class CmdSITE extends FtpCmd implements Runnable {

    public CmdSITE(SessionThread sessionThread, String input) {
        super(sessionThread);
    }

    @Override
    public void run() {
        sessionThread.writeString("250 SITE is a NOP\r\n");
    }

}
