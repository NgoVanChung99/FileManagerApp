

package com.nvchung.filemanager_ftp_da3.serverftp.server;

import net.vrallev.android.cat.Cat;

public class CmdREST extends FtpCmd implements Runnable {
    protected String input;

    public CmdREST(SessionThread sessionThread, String input) {
        super(sessionThread);
        this.input = input;
    }

    @Override
    public void run() {
        String param = getParameter(input);
        long offset;
        try {
            offset = Long.parseLong(param);
        } catch (NumberFormatException e) {
            sessionThread.writeString("550 No valid restart position\r\n");
            return;
        }
        if (offset < 0) {
            sessionThread.writeString("550 Restart position must be non-negative\r\n");
            return;
        }
        Cat.d("run REST with offset " + offset);
        if (sessionThread.isBinaryMode()) {
            sessionThread.offset = offset;
            sessionThread.writeString("350 Restart position accepted (" + offset + ")\r\n");
        } else {
            // We do not allow a REST in ASCII mode as this this causes problems
            // when server and client use different line ending format
            sessionThread.writeString("550 Restart position not accepted in ASCII mode\r\n");
        }
    }

}
