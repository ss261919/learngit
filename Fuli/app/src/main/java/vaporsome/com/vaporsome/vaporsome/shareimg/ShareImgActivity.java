package vaporsome.com.vaporsome.vaporsome.shareimg;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.constants.Constants;
import vaporsome.com.vaporsome.common.parser.JsonUtils;
import vaporsome.com.vaporsome.common.utils.BitMapUtils;
import vaporsome.com.vaporsome.common.utils.GetTypeByHead;
import vaporsome.com.vaporsome.common.utils.HttpUtils;
import vaporsome.com.vaporsome.common.utils.PreferencesUtils;
import vaporsome.com.vaporsome.common.utils.RandomUtils;
import vaporsome.com.vaporsome.common.utils.TokenUtlils;
import vaporsome.com.vaporsome.vaporsome.base.BaseActivity;
import vaporsome.com.vaporsome.vaporsome.login.LoginActivity;

public class ShareImgActivity extends BaseActivity {
    private final int SELECT_PIC_BY_PICK_PHOTO = 1;
    @Bind(R.id.shareImageActivityFinishTV)
    TextView shareImageActivityFinishTV;
    @Bind(R.id.shareImageActivityBackLL)
    LinearLayout shareImageActivityBackLL;
    @Bind(R.id.shareImageActivityET)
    EditText shareImageActivityET;
    @Bind(R.id.shareImageActivityImg)
    ImageView shareImageActivityImg;

    private boolean isFirstGoToLogin = true;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 2:
                    progressDialog.dismiss();
                    progressDialog = null;
                    openToast("图像上传失败");
                    shareImageActivityFinishTV.setEnabled(true);
                    break;
//                case 1:
//                    progressDialog.setMessage("以上传" + msg.getData().getString("PERCENT").toString());
//                    break;
                case 0:
                    openToast("图像上传成功");
                    shareImageActivityFinishTV.setEnabled(true);
                    progressDialog.dismiss();
                    progressDialog = null;
                    finish();
                    break;
                case 3:
                    if (isFirstGoToLogin) {
                        startActivity(new Intent(ShareImgActivity.this, LoginActivity.class));
                        PreferencesUtils.putBoolean(ShareImgActivity.this, "isFirstLogin", true);
                        isFirstGoToLogin = false;
                    }
                    break;
            }
        }
    };
    private HashMap<String, String> params;
    private Map<String, String> map1;
    private Bundle bundle;
    private Message message;
    private int imgHeitht;
    private int imgWidth;

    private void openToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    Intent intent = null;
    private ProgressDialog progressDialog;

    @OnClick(R.id.shareImageActivityBackLL)
    void goBack() {
        finish();
        overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
    }

    @OnClick(R.id.shareImageActivityFinishTV)
    void upload() {
        if (TextUtils.equals(GetTypeByHead.getFileType(imgPath), "gif")) {
            Toast.makeText(this, "图片不能为gif格式", Toast.LENGTH_SHORT).show();

        } else if (imgHeitht > 3000 || imgWidth > 2000) {
            Toast.makeText(this, "图片尺寸过大，请重新选择图片！", Toast.LENGTH_SHORT).show();
        } else if (imgPath == null) {
            Toast.makeText(this, "请选择图片后上传！", Toast.LENGTH_SHORT).show();
        } else {
            shareImg();
            shareImageActivityFinishTV.setEnabled(false);
        }
    }

    @OnClick(R.id.shareImageActivityImg)
    void pickPhoto() {
        //从相册中选择照片
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, SELECT_PIC_BY_PICK_PHOTO);
    }

    private String imgPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_img);
        ButterKnife.bind(this);
        initListener();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {        //此处的 RESULT_OK 是系统自定义得一个常量
            return;
        }
        Bitmap bm = null;
        //外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
        ContentResolver resolver = getContentResolver();
        //此处的用于判断接收的Activity是不是你想要的那个
        if (requestCode == SELECT_PIC_BY_PICK_PHOTO) {
            try {
                Uri originalUri = data.getData();        //获得图片的uri
                bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                //显得到bitmap图片
                shareImageActivityImg.setImageBitmap(BitMapUtils.getRoundedCornerBitmap(bm, 10, 10));
                //  这里开始的第二部分，获取图片的路径：
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(originalUri, null, null, null, null);

                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                //最后根据索引值获取图片路径
                String path = cursor.getString(column_index);
                String imgPath1 = cursor.getString(1); // 图片文件路径
                String imgSize = cursor.getString(2); // 图片大小
                String imgName = cursor.getString(3); // 图片文件名
                imgPath = imgPath1;

                BitmapFactory.Options options = new BitmapFactory.Options();

                // 此时把options.inJustDecodeBounds 设回true，即只读边不读内容
                options.inJustDecodeBounds = true;
                // 默认是Bitmap.Config.ARGB_8888
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                //此时不会把图片读入内存，只会获取图片宽高等信息
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver()
                        .openInputStream(originalUri), null, options);
                //上面一句和下面的类似
                //Bitmap bitmap = BitmapFactory.decodeFile(imgPath,options);
                imgHeitht = options.outHeight;
                imgWidth = options.outWidth;
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void finish() {
        if (!(progressDialog == null)) {
            if (progressDialog.isShowing()) {
            } else {
                super.finish();
                overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
            }
        } else {
            super.finish();
            overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
        }
    }

    private void shareImg() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("等待");
        progressDialog.setMessage("正在上传...");
        progressDialog.show();
        params = new HashMap<>();
        params.put("comment", shareImageActivityET.getText().toString());
       /*
         * 上传图片到七牛
	     */
        new Thread(new Runnable() {
            @Override
            public void run() {
                //获得七牛上传凭证uploadToken
                final String token = TokenUtlils.shareImgQiNiuToken();
                try {
                    if (token != null) {
                        String data = imgPath;
                        //图片名称为当前日期+随机数生成+图片uri哈希码
                        String key = String.valueOf(System.currentTimeMillis());
                        key = key + imgPath.hashCode() + RandomUtils.getRandomIntString();
                        UploadManager uploadManager = new UploadManager();
                        uploadManager.put(data, key, token,
                                new UpCompletionHandler() {
                                    @Override
                                    public void complete(String arg0, ResponseInfo info, JSONObject response) {
                                        // TODO Auto-generated method stub
                                        try {
                                            map1 = JsonUtils.getMapStr(response.toString());
                                            if (map1.get("status").equals("false")) {
                                                Message message = new Message();
                                                message.what = 2;
                                                bundle = new Bundle();
                                                bundle.putString("data", map1.get("data").toString());
                                                message.setData(bundle);
                                                handler.sendMessage(message);
                                            } else {
                                                ShareImgSaveThread thread = new ShareImgSaveThread();
                                                thread.start();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                new UploadOptions(null, null, false,
                                        new UpProgressHandler() {
                                            Bundle bundle;

                                            @Override
                                            public void progress(String s, double v) {
//                                                    Message message = new Message();
//                                                    message.what = 1;
//                                                    bundle = new Bundle();
//                                                    bundle.putString("PERCENT", String.valueOf(v));
//                                                    handler.sendMessage(message);
                                            }
                                        }, null));
                    } else {
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    }
                } catch (Exception e) {
                    Message message = new Message();
                    message.what = 2;
                    handler.sendMessage(message);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private class ShareImgSaveThread extends Thread {
        @Override
        public void run() {
            try {
                if (TextUtils.equals(map1.get("status").toString(), "true")) {
                    params.put("file-url", JsonUtils.getMapStr(map1.get("data").toString()).get("file_url"));
                    String strUrlPath;
                    if (PreferencesUtils.getBoolean(ShareImgActivity.this, "isOtherLogin")) {
                        strUrlPath = Constants.shareImgUri + PreferencesUtils.getString(ShareImgActivity.this, "otherToken");
                    } else {
                        strUrlPath = Constants.shareImgUri + PreferencesUtils.getString(ShareImgActivity.this, "token");
                    }
                    String strResult = HttpUtils.submitPostData(strUrlPath, params, "utf-8");
                    try {
                        Map<String, Object> map = JsonUtils.getMapObj(strResult);
                        if (TextUtils.equals(map.get("status").toString(), "true")) {
                            message = new Message();
                            message.what = 0;
                            handler.sendMessage(message);
                        } else if (TextUtils.equals(map.get("status").toString(), "false") && TextUtils.equals(map.get("data").toString(), "invalid token")) {
                            strUrlPath = Constants.shareImgUri + TokenUtlils.getToken();
                            strResult = HttpUtils.submitPostData(strUrlPath, params, "utf-8");
                            map = JsonUtils.getMapObj(strResult);
                            if (TextUtils.equals(map.get("status").toString(), "true")) {
                                message = new Message();
                                message.what = 0;
                                handler.sendMessage(message);
                            } else {
                                message = new Message();
                                message.what = 3;
                                handler.sendMessage(message);
                            }
                        } else {
                            message = new Message();
                            message.what = 2;
                            handler.sendMessage(message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    message = new Message();
                    message.what = 2;
                    handler.sendMessage(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}