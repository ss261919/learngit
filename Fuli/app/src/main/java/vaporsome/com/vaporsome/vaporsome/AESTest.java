package vaporsome.com.vaporsome.vaporsome;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vaporsome.com.vaporsome.BuildConfig;
import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.utils.AESUtils;
import vaporsome.com.vaporsome.common.utils.AESUtils2;
import vaporsome.com.vaporsome.vaporsome.base.BaseActivity;

public class AESTest extends BaseActivity {

    @Bind(R.id.aesWaitEncryptET)
    EditText aesWaitEncryptET;
    @Bind(R.id.aesWaitEncryptedTV)
    TextView aesWaitEncryptedTV;
    @Bind(R.id.aesDecryptTV)
    TextView aesDecryptTV;
    @Bind(R.id.aesEncryptBT)
    Button aesEncryptBT;
    @Bind(R.id.aesDecryptBT)
    Button aesDecryptBT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aestest);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void initView() {
        aesWaitEncryptET.setText("e10adc3949ba59abbe56e057f20f883e");
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {

    }

    @OnClick({R.id.aesEncryptBT, R.id.aesDecryptBT})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.aesEncryptBT:
                try {
                    aesWaitEncryptedTV.setText(AESUtils2.encrypt(aesWaitEncryptET.getText().toString()));
                    if (BuildConfig.DEBUG)
                        Log.d("--encrypt", AESUtils2.encrypt(aesWaitEncryptET.getText().toString()));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case R.id.aesDecryptBT:
                try {
                    aesDecryptTV.setText(AESUtils2.decrypt(aesWaitEncryptedTV.getText().toString()));
                    Log.d("--decrypt", AESUtils2.decrypt(aesWaitEncryptedTV.getText().toString()));
                } catch (Exception e) {
                }
                break;
        }
    }
}
