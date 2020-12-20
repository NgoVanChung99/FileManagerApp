

package com.nvchung.filemanager_ftp_da3.serverftp.server;


public class CmdAPPE extends CmdAbstractStore implements Runnable {
	protected String input;

	public CmdAPPE(SessionThread sessionThread, String input) {
		super(sessionThread, CmdAPPE.class.toString());
		this.input = input;
	}

	@Override
    public void run() {
		doStorOrAppe(getParameter(input), true);
	}
}
