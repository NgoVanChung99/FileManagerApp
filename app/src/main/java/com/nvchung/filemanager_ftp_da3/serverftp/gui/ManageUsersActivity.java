package com.nvchung.filemanager_ftp_da3.serverftp.gui;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.nvchung.filemanager_ftp_da3.serverftp.FsSettings;

public class ManageUsersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(FsSettings.getTheme());
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, UserListFragment.newInstance())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // to let android handle fragment back stack
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
