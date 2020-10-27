package xds.fishy.recorder.view.activity.about;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import xds.fishy.recorder.R;
import xds.fishy.view.activity.BaseActivity;

public class AboutActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_about);
        initView();
    }

    @Override
    public void initView() {
        TextView tv_app_version = findViewById(R.id.tv_app_version);
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            ApplicationInfo applicationInfo = getApplicationInfo();
            boolean appDebugRelease = (applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
            tv_app_version.setText(String.format(getResources().getString(appDebugRelease ?
                    R.string.app_version_debug : R.string.app_version_release), packageInfo.versionName, packageInfo.versionCode));
        } catch (PackageManager.NameNotFoundException ignore) {
        }
    }
}
