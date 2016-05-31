package vaporsome.com.vaporsome.vaporsome.setting;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.utils.PreferencesUtils;
import vaporsome.com.vaporsome.vaporsome.about.About;
import vaporsome.com.vaporsome.vaporsome.base.BaseActivity;
import vaporsome.com.vaporsome.vaporsome.changeuserinfo.ChangeUserInfo;
import vaporsome.com.vaporsome.vaporsome.invite.InviteFriends;
import vaporsome.com.vaporsome.vaporsome.main.activity.MainActivity;
import vaporsome.com.vaporsome.vaporsome.redresultactivity.RedResultActivity;

public class SettingActivity extends BaseActivity {

    @Bind(R.id.settingCancel)
    LinearLayout settingCancel;
    @Bind(R.id.settingChangeUserInfoPsdInside)
    RelativeLayout settingChangeUserInfoPsdInside;
    @Bind(R.id.settingAboutCrazyRed)
    RelativeLayout settingAboutCrazyRed;
    @Bind(R.id.settingInviteFriends)
    RelativeLayout settingInviteFriends;
    @Bind(R.id.settingQuit)
    RelativeLayout settingQuit;
    @Bind(R.id.settingBGLL)
    LinearLayout settingBGLL;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {

    }

    @OnClick({R.id.settingCancel, R.id.settingChangeUserInfoPsdInside, R.id.settingAboutCrazyRed, R.id.settingInviteFriends, R.id.settingQuit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.settingCancel:
                finish();
                overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
                break;
            case R.id.settingChangeUserInfoPsdInside:
                intent = new Intent(this, ChangeUserInfo.class);
                startActivityForResult(intent, 0);
                overridePendingTransition(R.anim.in_to_left, R.anim.out_to_left);
                break;
            case R.id.settingAboutCrazyRed:
                startActivity(new Intent(SettingActivity.this, About.class));
                overridePendingTransition(R.anim.in_to_left, R.anim.out_to_left);
                break;
            case R.id.settingInviteFriends:
                startActivity(new Intent(SettingActivity.this, InviteFriends.class));
                overridePendingTransition(R.anim.in_to_left, R.anim.out_to_left);
                break;
            case R.id.settingQuit:
                settingBGLL.setVisibility(View.VISIBLE);
                quit();
                break;
        }
    }

    private void quit() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.quit_layout);
        TextView okTV = (TextView) window.findViewById(R.id.quitOk);
        TextView cancelTV = (TextView) window.findViewById(R.id.quitCancel);
        okTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferencesUtils.putBoolean(SettingActivity.this, "isLogin", false);
                PreferencesUtils.putBoolean(SettingActivity.this, "isOtherLogin", false);
                exit();
            }
        });
        cancelTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingBGLL.setVisibility(View.GONE);
                alertDialog.cancel();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0) {
            setResult(0);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
    }
}
