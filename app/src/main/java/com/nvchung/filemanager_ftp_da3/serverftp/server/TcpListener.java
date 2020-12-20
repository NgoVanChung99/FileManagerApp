

package com.nvchung.filemanager_ftp_da3.serverftp.server;

import android.util.Log;

import com.nvchung.filemanager_ftp_da3.serverftp.FsService;

import java.net.ServerSocket;
import java.net.Socket;

public class TcpListener extends Thread {
    private static final String TAG = TcpListener.class.getSimpleName();

    ServerSocket listenSocket;
    FsService ftpServerService;

    public TcpListener(ServerSocket listenSocket, FsService ftpServerService) {
        this.listenSocket = listenSocket;
        this.ftpServerService = ftpServerService;
    }

    public void quit() {
        try {
            listenSocket.close(); // if the TcpListener thread is blocked on accept,
                                  // closing the socket will raise an exception
        } catch (Exception e) {
            Log.d(TAG, "Exception closing TcpListener listenSocket");
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                Socket clientSocket = listenSocket.accept();
                Log.i(TAG, "New connection, spawned thread");
                SessionThread newSession = new SessionThread(clientSocket, new LocalDataSocket());
                newSession.start();
                ftpServerService.registerSessionThread(newSession);
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception in TcpListener");
        }
    }
}
