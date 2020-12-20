
package com.nvchung.filemanager_ftp_da3.serverftp.locale;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nvchung.filemanager_ftp_da3.R;
import com.nvchung.filemanager_ftp_da3.serverftp.FsSettings;
import com.nvchung.filemanager_ftp_da3.serverftp.server.CmdMLST;
import com.twofortyfouram.locale.sdk.client.ui.activity.AbstractAppCompatPluginActivity;

import net.vrallev.android.cat.Cat;

import static com.nvchung.filemanager_ftp_da3.serverftp.server.CmdMLST.SettingsBundleHelper.BUNDLE_BOOLEAN_RUNNING;
import static com.nvchung.filemanager_ftp_da3.serverftp.server.CmdMLST.SettingsBundleHelper.generateBundle;
import static com.nvchung.filemanager_ftp_da3.serverftp.server.CmdMLST.SettingsBundleHelper.getBundleRunningState;

public class EditActivity extends AbstractAppCompatPluginActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(FsSettings.getTheme());
        super.onCreate(savedInstanceState);

        setContentView(R.layout.locale_edit_layout);

        CharSequence callingApplicationLabel = null;
        try {
            PackageManager pm = getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(getCallingPackage(), 0);
            callingApplicationLabel = pm.getApplicationLabel(ai);
        } catch (final PackageManager.NameNotFoundException e) {
            Cat.e("Calling package couldn't be found%s", e); //$NON-NLS-1$
        }
        if (null != callingApplicationLabel) {
            setTitle(callingApplicationLabel);
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(R.string.swiftp_name);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean isBundleValid(@NonNull Bundle bundle) {
        return CmdMLST.SettingsBundleHelper.isBundleValid(bundle);
    }

    @Override
    public void onPostCreateWithPreviousResult(@NonNull Bundle previousBundle,
                                               @NonNull String previousBlurb) {
        if (!isBundleValid(previousBundle)) {
            Cat.e("Invalid bundle received, repairing to default");
            previousBundle = generateBundle(this, false);
        }
        boolean running = previousBundle.getBoolean(BUNDLE_BOOLEAN_RUNNING);
        RadioButton radioButton =
                (RadioButton) findViewById(running ? R.id.radio_server_running :
                                R.id.radio_server_stopped);
        radioButton.setChecked(true);
    }

    @Nullable
    @Override
    public Bundle getResultBundle() {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_server_state_group);
        int checkedId = radioGroup.getCheckedRadioButtonId();
        boolean running = (checkedId == R.id.radio_server_running);

        return generateBundle(this, running);
    }

    @NonNull
    @Override
    public String getResultBlurb(@NonNull Bundle bundle) {
        boolean running = getBundleRunningState(bundle);
        return running ? "Running" : "Stopped";
    }
}
