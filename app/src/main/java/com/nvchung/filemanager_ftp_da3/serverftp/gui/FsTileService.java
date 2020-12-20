package com.nvchung.filemanager_ftp_da3.serverftp.gui;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import androidx.annotation.RequiresApi;

import com.nvchung.filemanager_ftp_da3.R;
import com.nvchung.filemanager_ftp_da3.serverftp.FsService;
import com.nvchung.filemanager_ftp_da3.serverftp.FsSettings;

import net.vrallev.android.cat.Cat;

import java.net.InetAddress;

import static com.nvchung.filemanager_ftp_da3.android.BroadcastReceiverUtils.createBroadcastReceiver;

@RequiresApi(api = Build.VERSION_CODES.N)
public class FsTileService extends TileService {

    @Override
    public void onClick() {
        if (getQsTile().getState() == Tile.STATE_INACTIVE)
            FsService.start();
        else if (getQsTile().getState() == Tile.STATE_ACTIVE)
            FsService.stop();
    }

    @Override
    public void onStartListening() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FsService.ACTION_STARTED);
        intentFilter.addAction(FsService.ACTION_STOPPED);
        intentFilter.addAction(FsService.ACTION_FAILEDTOSTART);
        registerReceiver(mFsActionsReceiver, intentFilter);
        updateTileState();
    }

    @Override
    public void onStopListening() {
        unregisterReceiver(mFsActionsReceiver);
    }

    private void updateTileState() {
        Tile tile = getQsTile();
        if (FsService.isRunning()) {
            tile.setState(Tile.STATE_ACTIVE);
            // Fill in the FTP server address
            InetAddress address = FsService.getLocalInetAddress();
            if (address == null) {
                Cat.v("Unable to retrieve wifi ip address");
                tile.setLabel(getString(R.string.swiftp_name));
                return;
            }
            tile.setLabel(address.getHostAddress() + ":" + FsSettings.getPortNumber());
        } else {
            tile.setState(Tile.STATE_INACTIVE);
            tile.setLabel(getString(R.string.swiftp_name));
        }
        tile.updateTile();
    }

    BroadcastReceiver mFsActionsReceiver = createBroadcastReceiver(
                    (context, intent) -> updateTileState()
    );
}
