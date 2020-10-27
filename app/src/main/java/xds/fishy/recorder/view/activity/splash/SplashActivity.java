package xds.fishy.recorder.view.activity.splash;

import android.content.Intent;
import android.os.Bundle;

import xds.fishy.recorder.view.activity.main.MainActivity;
import xds.fishy.view.activity.BaseActivity;

public class SplashActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
