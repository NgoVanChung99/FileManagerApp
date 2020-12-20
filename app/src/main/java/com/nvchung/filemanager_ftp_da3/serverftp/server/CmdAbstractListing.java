

package com.nvchung.filemanager_ftp_da3.serverftp.server;

import android.util.Log;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public abstract class CmdAbstractListing extends FtpCmd {
    // TODO: .class.getSimpleName() from abstract class?
    private static String TAG = "CmdAbstractListing";

    public CmdAbstractListing(SessionThread sessionThread, String input) {
        super(sessionThread);
    }

    abstract String makeLsString(File file);

    // Creates a directory listing by finding the contents of the directory,
    // calling makeLsString on each file, and concatenating the results.
    // Returns an error string if failure, returns null on success. May be
    // called by CmdLIST or CmdNLST, since they each override makeLsString
    // in a different way.
    public String listDirectory(StringBuilder response, File dir) {
        if (!dir.isDirectory()) {
            return "500 Internal error, listDirectory on non-directory\r\n";
        }
        Log.d(TAG, "Listing directory: " + dir.toString());

        // Get a listing of all files and directories in the path
        File[] entries = dir.listFiles();
        if (entries == null) {
            return "500 Couldn't list directory. Check config and mount status.\r\n";
        }
        Log.d(TAG, "Dir len " + entries.length);
        try {
            Arrays.sort(entries, listingComparator);
        } catch (Exception e) {
            // once got a FC on this, seems it is possible to have a dir that
            // breaks the listing comparator (unable to reproduce)
            Log.e(TAG, "Unable to sort the listing: " + e.getMessage());
            // play for sure, and get back the entries
            entries = dir.listFiles();
        }
        for (File entry : entries) {
            String curLine = makeLsString(entry);
            if (curLine != null) {
                response.append(curLine);
            }
        }
        return null;
    }

    // Send the directory listing over the data socket. Used by CmdLIST and CmdNLST.
    // Returns an error string on failure, or returns null if successful.
    protected String sendListing(String listing) {
        if (sessionThread.openDataSocket()) {
            Log.d(TAG, "LIST/NLST done making socket");
        } else {
            sessionThread.closeDataSocket();
            return "425 Error opening data socket\r\n";
        }
        String mode = sessionThread.isBinaryMode() ? "BINARY" : "ASCII";
        sessionThread.writeString("150 Opening " + mode
                + " mode data connection for file list\r\n");
        Log.d(TAG, "Sent code 150, sending listing string now");
        if (!sessionThread.sendViaDataSocket(listing)) {
            Log.d(TAG, "sendViaDataSocket failure");
            sessionThread.closeDataSocket();
            return "426 Data socket or network error\r\n";
        }
        sessionThread.closeDataSocket();
        Log.d(TAG, "Listing sendViaDataSocket success");
        sessionThread.writeString("226 Data transmission OK\r\n");
        return null;
    }

    /**
     * Comparator to sort file listings. Sorts directories before files, sorts
     * alphabetical ignoring case
     */
    static final Comparator<File> listingComparator = (lhs, rhs) -> {
        if (lhs.isDirectory() && rhs.isFile()) {
            return -1;
        } else if (lhs.isFile() && rhs.isDirectory()) {
            return 1;
        } else {
            return lhs.getName().compareToIgnoreCase(rhs.getName());
        }
    };
}
