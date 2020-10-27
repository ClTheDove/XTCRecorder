package xds.fishy.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;

interface InitActivity {
    void initView();

    void initObject();

    void initVars();
}

public class BaseActivity extends Activity implements InitActivity {
    private boolean isIntentAvailable(Intent intent) {
        if (intent == null) return false;
        return getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }

    @Override
    public void onBackPressed() {
        try {
            super.onBackPressed();
        } catch (IllegalStateException e) {
            if ("Can not perform this action after onSaveInstanceState"
                    .equals(e.getMessage())) {
                finish();
                return;
            }
            throw e;
        }
    }

    @Override
    public void startActivity(Intent intent) {
        if (isIntentAvailable(intent)) super.startActivity(intent);
    }

    @Override
    public void startActivityForResult(Intent intent, int i) {
        if (isIntentAvailable(intent)) super.startActivityForResult(intent, i);
    }

    @Override
    public void initView() {
    }

    @Override
    public void initObject() {
    }

    @Override
    public void initVars() {
    }
}