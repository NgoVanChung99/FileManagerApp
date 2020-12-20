package com.nvchung.filemanager_ftp_da3.serverftp.gui;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.nvchung.filemanager_ftp_da3.BuildConfig;
import com.nvchung.filemanager_ftp_da3.R;
import com.nvchung.filemanager_ftp_da3.serverftp.App;
import com.nvchung.filemanager_ftp_da3.serverftp.FsSettings;

import net.vrallev.android.cat.Cat;

import java.util.Arrays;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivityFTP extends AppCompatActivity {

    final static int PERMISSIONS_REQUEST_CODE = 12;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Cat.d("created");
        setTheme(FsSettings.getTheme());
        super.onCreate(savedInstanceState);

        if (!haveReadWritePermissions()) {
            requestReadWritePermissions();
        }


        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PreferenceFragment())
                .commit();
    }

    private boolean haveReadWritePermissions() {
        if (VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED
                    && checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    private void requestReadWritePermissions() {
        if (VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        String[] permissions = new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};
        requestPermissions(permissions, PERMISSIONS_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        return true;
    }
}
