

package com.nvchung.filemanager_ftp_da3.serverftp.server;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.nvchung.filemanager_ftp_da3.serverftp.Util;
import com.twofortyfouram.assertion.BundleAssertions;
import com.twofortyfouram.spackle.AppBuildInfo;

import net.vrallev.android.cat.Cat;

import java.io.File;

/**
 * Implements MLST command
 */
public class CmdMLST extends FtpCmd implements Runnable {
    private static final String TAG = CmdMLST.class.getSimpleName();

    private String mInput;

    public CmdMLST(SessionThread sessionThread, String input) {
        super(sessionThread);
        mInput = input;
    }

    @Override
    public void run() {
        Log.d(TAG, "run: LIST executing, input: " + mInput);
        String param = getParameter(mInput);
        
        File fileToFormat = null;
        if(param.equals("")){
            fileToFormat = sessionThread.getWorkingDir();
            param = "/";
        }else{
            fileToFormat = inputPathToChrootedFile(sessionThread.getChrootDir(), sessionThread.getWorkingDir(), param);
        }
        
        if (fileToFormat.exists()) {
            sessionThread.writeString("250- Listing " + param + "\r\n");
            sessionThread.writeString(makeString(fileToFormat) + "\r\n");
            sessionThread.writeString("250 End\r\n");
        } else {
            Log.w(TAG, "run: file does not exist");
            sessionThread.writeString("550 file does not exist\r\n");
        }

        Log.d(TAG, "run: LIST completed");
    }

    public String makeString(File file){
        StringBuilder response = new StringBuilder();
        
        String[] selectedTypes = sessionThread.getFormatTypes();
        if(selectedTypes != null){
            for (int i = 0; i < selectedTypes.length; ++i) {
                String type = selectedTypes[i];
                if (type.equalsIgnoreCase("size")) {
                    response.append("Size=" + String.valueOf(file.length()) + ';');
                } else if (type.equalsIgnoreCase("modify")) {
                    String timeStr = Util.getFtpDate(file.lastModified());
                    response.append("Modify=" + timeStr + ';');
                } else if (type.equalsIgnoreCase("type")) {
                    if (file.isFile()) {
                        response.append("Type=file;");
                    } else if (file.isDirectory()) {
                        response.append("Type=dir;");
                    }
                } else if (type.equalsIgnoreCase("perm")) {
                    response.append("Perm=");
                    if (file.canRead()) {
                        if (file.isFile()) {
                            response.append('r');
                        } else if (file.isDirectory()) {
                            response.append("el");
                        }
                    }
                    if (file.canWrite()) {
                        if (file.isFile()) {
                            response.append("adfw");
                        } else if (file.isDirectory()) {
                            response.append("fpcm");
                        }
                    }
                    response.append(';');
                }
            }
        }

        response.append(' ');    
        response.append(file.getName());
        response.append("\r\n");
        return response.toString();
    }

    public static final class SettingsBundleHelper {

        public static final String BUNDLE_BOOLEAN_RUNNING = "com.nvchung.filemanager_ftp_da3.serverftp.BOOLEAN_RUNNING";
        public static final String BUNDLE_VERSION_CODE = "com.nvchung.filemanager_ftp_da3.serverftp.VERSION_CODE";


        static public boolean isBundleValid(Bundle bundle) {
            if (bundle == null) {
                return false;
            }
            try {
                BundleAssertions.assertHasBoolean(bundle, BUNDLE_BOOLEAN_RUNNING);
                BundleAssertions.assertHasInt(bundle, BUNDLE_VERSION_CODE);
            } catch (AssertionError e) {
                Cat.e("Bundle failed verification");
                return false;
            }
            return true;
        }

        static public Bundle generateBundle(Context context, boolean running) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(BUNDLE_BOOLEAN_RUNNING, running);
            bundle.putInt(BUNDLE_VERSION_CODE, AppBuildInfo.getVersionCode(context));
            return bundle;
        }

        static public boolean getBundleRunningState(@NonNull Bundle bundle) {
            return bundle.getBoolean(BUNDLE_BOOLEAN_RUNNING, false);
        }

        private SettingsBundleHelper() {
        }

    }
}

