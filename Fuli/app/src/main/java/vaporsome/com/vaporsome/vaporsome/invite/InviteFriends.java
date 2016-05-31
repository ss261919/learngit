package vaporsome.com.vaporsome.vaporsome.invite;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.constants.Constants;
import vaporsome.com.vaporsome.common.utils.BitMapUtils;
import vaporsome.com.vaporsome.common.utils.WXUtils;
import vaporsome.com.vaporsome.vaporsome.base.BaseActivity;
import vaporsome.com.vaporsome.vaporsome.base.IApplication;
import vaporsome.com.vaporsome.vaporsome.setting.SettingActivity;
import vaporsome.com.vaporsome.vaporsome.shareimg.ShareImgActivity;

public class InviteFriends extends BaseActivity {

    @Bind(R.id.inviteFriendsImgBack)
    LinearLayout inviteFriendsImgBack;
    @Bind(R.id.inviteBottomLayoutShareWXFriends)
    LinearLayout inviteBottomLayoutShareWXFriends;
    @Bind(R.id.inviteBottomLayoutShareWXFriendsCircle)
    LinearLayout inviteBottomLayoutShareWXFriendsCircle;
    private boolean isShowBottomLL = false;
    private int height;
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friends);
        ButterKnife.bind(this);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
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

    @OnClick({R.id.inviteFriendsImgBack, R.id.inviteBottomLayoutShareWXFriends, R.id.inviteBottomLayoutShareWXFriendsCircle})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.inviteFriendsImgBack:
                finish();
                overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
                break;
            case R.id.inviteBottomLayoutShareWXFriends:
                shareWXFriends();
                break;
            case R.id.inviteBottomLayoutShareWXFriendsCircle:
                shareWXFriendsCircle();
                break;
        }
    }

    private void shareWXFriends() {
        String webPageUrl = "https://www.qckj1688.com/app/share.html";
        WXUtils.shareWXWebPage(SendMessageToWX.Req.WXSceneSession, webPageUrl, "看广告～抢免费红包，尽在疯狂红包APP", "每天看广告来赚钱，我已经赚到150啦，送你...");
    }

    private void shareWXFriendsCircle() {
        String webPageUrl = "https://www.qckj1688.com/app/share.html";
        WXUtils.shareWXWebPage(SendMessageToWX.Req.WXSceneTimeline, webPageUrl, "每天看广告来赚钱，我已经赚到150啦，送你...", "每天看广告来赚钱，我已经赚到150啦，送你...");
    }

    @Override
    public void finish() {
        super.finish();
        startActivity(new Intent(this, SettingActivity.class));
        overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
    }
}
