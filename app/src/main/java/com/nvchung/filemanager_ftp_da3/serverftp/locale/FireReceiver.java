
package com.nvchung.filemanager_ftp_da3.serverftp.locale;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.nvchung.filemanager_ftp_da3.serverftp.FsService;
import com.nvchung.filemanager_ftp_da3.serverftp.server.CmdMLST;
import com.twofortyfouram.locale.sdk.client.receiver.AbstractPluginSettingReceiver;

import static com.nvchung.filemanager_ftp_da3.serverftp.server.CmdMLST.SettingsBundleHelper.getBundleRunningState;


public class FireReceiver extends AbstractPluginSettingReceiver {
    @Override
    protected boolean isBundleValid(@NonNull Bundle bundle) {
        return CmdMLST.SettingsBundleHelper.isBundleValid(bundle);
    }

    @Override
    protected boolean isAsync() {
        return false;
    }

    @Override
    protected void firePluginSetting(@NonNull Context context, @NonNull Bundle bundle) {
        boolean running = getBundleRunningState(bundle);
        if (running && !FsService.isRunning()) {
            FsService.start();
        } else if (!running && FsService.isRunning()) {
            FsService.stop();
        }
    }
}
