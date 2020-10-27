package xds.fishy.recorder.view.activity.menu;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.xtc.shareapi.share.communication.BaseResponse;
import com.xtc.shareapi.share.communication.SendMessageToXTC;
import com.xtc.shareapi.share.communication.ShowMessageFromXTC;
import com.xtc.shareapi.share.interfaces.IResponseCallback;
import com.xtc.shareapi.share.interfaces.IXTCCallback;
import com.xtc.shareapi.share.manager.ShareMessageManager;
import com.xtc.shareapi.share.manager.XTCCallbackImpl;
import com.xtc.shareapi.share.shareobject.XTCAppExtendObject;
import com.xtc.shareapi.share.shareobject.XTCShareMessage;

import java.util.Random;

import xds.fishy.recorder.R;
import xds.fishy.recorder.view.activity.about.AboutActivity;
import xds.fishy.recorder.view.activity.list.ListActivity;
import xds.fishy.recorder.view.activity.main.MainActivity;
import xds.fishy.recorder.view.activity.menu.adapter.MenuItemListAdapter;
import xds.fishy.recorder.view.activity.menu.bean.MenuItem;
import xds.fishy.secret.AppKey;
import xds.fishy.view.activity.BaseActivity;

public class MenuActivity extends BaseActivity implements IResponseCallback {
    public static final int MENU_ITEM_SAVED = 1;
    public static final int MENU_ITEM_SHARE_THIS_APP = 2;
    public static final int MENU_ITEM_ABOUT = 4;
    private MenuItemListAdapter menuItemListAdapter;
    private TextView tv_no_menu_item;
    private ListView lv_menu_item_list;
    private int[] shareDescriptionResource;
    private IXTCCallback xtcCallback;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_menu);
        initVars();
        initView();
        xtcCallback = new XTCCallbackImpl();
        xtcCallback.handleIntent(getIntent(), this);
    }

    @Override
    public void initVars() {
        MenuItem[] menuItems = new MenuItem[]{
                new MenuItem(MENU_ITEM_SAVED, R.drawable.bg_folder, R.string.my_recording),
                new MenuItem(MENU_ITEM_SHARE_THIS_APP, R.drawable.bg_share, R.string.share_this_app),
                new MenuItem(MENU_ITEM_ABOUT, R.drawable.bg_about, R.string.about)
        };
        menuItemListAdapter = new MenuItemListAdapter(this, menuItems);
        shareDescriptionResource = new int[]{
                R.string.ad_0,
                R.string.ad_1
        };
    }

    @Override
    public void initView() {
        lv_menu_item_list = findViewById(R.id.lv_menu_item_list);
        tv_no_menu_item = findViewById(R.id.tv_no_menu_item);
        lv_menu_item_list.setAdapter(menuItemListAdapter);
        lv_menu_item_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MenuItem item = menuItemListAdapter.data[i];
                switch (item.getId()) {
                    case MENU_ITEM_SAVED:
                        startActivity(new Intent(MenuActivity.this, ListActivity.class));
                        finish();
                        break;
                    case MENU_ITEM_SHARE_THIS_APP:
                        shareThisApp();
                        break;
                    case MENU_ITEM_ABOUT:
                        startActivity(new Intent(MenuActivity.this, AboutActivity.class));
                        finish();
                        break;
                }
            }
        });
        updateList();
    }

    private void updateList() {
        if (menuItemListAdapter != null) {
            if (menuItemListAdapter.data.length != 0) {
                tv_no_menu_item.setVisibility(View.GONE);
                lv_menu_item_list.setVisibility(View.VISIBLE);
                return;
            }
        }
        lv_menu_item_list.setVisibility(View.GONE);
        tv_no_menu_item.setVisibility(View.VISIBLE);
    }

    private void shareThisApp() {
        XTCAppExtendObject xtcAppExtendObject = new XTCAppExtendObject();
        xtcAppExtendObject.setStartActivity(MainActivity.class.getName());
        XTCShareMessage xtcShareMessage = new XTCShareMessage();
        xtcShareMessage.setShareObject(xtcAppExtendObject);
        xtcShareMessage.setThumbImage(BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_launcher_share));
        xtcShareMessage.setDescription(getResources().getString(
                shareDescriptionResource[new Random().nextInt(shareDescriptionResource.length)]));
        SendMessageToXTC.Request request = new SendMessageToXTC.Request();
        request.setMessage(xtcShareMessage);
        new ShareMessageManager(this).sendRequestToXTC(request, AppKey.APP_KEY);
    }

    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        xtcCallback.handleIntent(intent, this);
    }

    @Override
    public void onReq(ShowMessageFromXTC.Request request) {
    }

    @Override
    public void onResp(boolean b, BaseResponse baseResponse) {
        finish();
    }

}
