package com.nvchung.filemanager_ftp_da3.serverftp.gui;


import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.nvchung.filemanager_ftp_da3.BuildConfig;
import com.nvchung.filemanager_ftp_da3.R;
import com.nvchung.filemanager_ftp_da3.serverftp.FsSettings;


public class AboutActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(FsSettings.getTheme());
        super.onCreate(savedInstanceState);

        setContentView(R.layout.about_layout);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        TextView packageNameText = (TextView) findViewById(R.id.about_package_name);

        TextView versionInfoText = (TextView) findViewById(R.id.about_version_info);
        versionInfoText.setText(BuildConfig.VERSION_NAME + " - " + BuildConfig.VERSION_CODE + " (" + Build.VERSION.RELEASE + "-" + Build.VERSION.SDK_INT + ")");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
