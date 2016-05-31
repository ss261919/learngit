package vaporsome.com.vaporsome.vaporsome.outofmoney;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.constants.Constants;
import vaporsome.com.vaporsome.common.parser.JsonUtils;
import vaporsome.com.vaporsome.common.utils.HttpUtils;
import vaporsome.com.vaporsome.common.utils.IsMatchUtil;
import vaporsome.com.vaporsome.common.utils.NetWorkInfo;
import vaporsome.com.vaporsome.common.utils.PreferencesUtils;
import vaporsome.com.vaporsome.common.utils.TokenUtlils;
import vaporsome.com.vaporsome.vaporsome.base.BaseActivity;
import vaporsome.com.vaporsome.vaporsome.login.LoginActivity;

public class OutofMoneyActivity extends BaseActivity {

    @Bind(R.id.personPopWindowCancle)
    LinearLayout personPopWindowCancle;
    @Bind(R.id.personPopWindowUserName)
    EditText personPopWindowUserName;
    @Bind(R.id.personPopWindowOutputMoneyBankSpinner)
    Spinner personPopWindowOutputMoneyBankSpinner;
    @Bind(R.id.personPopWindowCardId)
    EditText personPopWindowCardId;
    @Bind(R.id.personPopWindowMoneyNumberET)
    EditText personPopWindowMoneyNumberET;
    @Bind(R.id.personPopWindowUserNotice)
    TextView personPopWindowUserNotice;
    @Bind(R.id.personPopWindowOutputMoneyPostBT)
    Button personPopWindowOutputMoneyPostBT;
    @Bind(R.id.outOfMoneyLoadRL)
    RelativeLayout outOfMoneyLoadRL;
    @Bind(R.id.personPopWindowCancle2)
    LinearLayout personPopWindowCancle2;
    @Bind(R.id.outOfMoneyLoadImg)
    ImageView outOfMoneyLoadImg;
    @Bind(R.id.out_of_moneySRL)
    SwipeRefreshLayout out_of_moneySRL;
    @Bind(R.id.personPopWindowOutofMoney)
    LinearLayout personPopWindowOutofMoney;
    @Bind(R.id.outOfMoneyLoadTV)
    TextView outOfMoneyLoadTV;
    private List<Map<String, Object>> dataList;
    private List<String> dataIDList = new ArrayList<>();
    private List<String> dataNameList = new ArrayList<>();
    private boolean isSelectBankInfo;
    ArrayAdapter<String> adapter;
    private String bankId = "0";
    private Map<String, String> params;
    private boolean isLoading;
    private String bankName;

    @OnClick(R.id.personPopWindowCancle)
    void back() {
        setResult(0);
        finish();
        overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
    }

    @OnClick(R.id.personPopWindowOutputMoneyPostBT)
    void post() {
        postOutMoney();
    }

    private void postOutMoney() {
        if (TextUtils.equals("111111", bankId)) {
            Toast.makeText(this, "请先选择银行后提交！", Toast.LENGTH_SHORT).show();
        } else if (!IsMatchUtil.isNumer(personPopWindowCardId.getText().toString())) {
            Toast.makeText(this, "请填写正确银行卡号信息！", Toast.LENGTH_SHORT).show();
        } else if (personPopWindowUserName.getText().length() == 0) {
            Toast.makeText(this, "姓名不能为空！", Toast.LENGTH_SHORT).show();
        } else {
            SaveOutofMoneyThread thread = new SaveOutofMoneyThread();
            thread.start();
            personPopWindowOutputMoneyPostBT.setEnabled(false);
        }
    }

    private boolean isGetBankInfo;
    private boolean isFirstGoToLogin = true;
    private Bundle bundle;
    private boolean isGetHintText;
    private boolean isGetBankInfoMes;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    dataIDList.clear();
                    dataNameList.clear();
                    dataIDList.add("111111");
                    dataNameList.add("所属银行");
                    out_of_moneySRL.setEnabled(false);
                    out_of_moneySRL.setRefreshing(false);
                    out_of_moneySRL.post(new Runnable() {
                        @Override
                        public void run() {
                            out_of_moneySRL.setEnabled(true);
                        }
                    });
                    for (Map<String, Object> map :
                            dataList) {
                        dataIDList.add(map.get("id").toString());
                        dataNameList.add(map.get("name").toString());
                    }
                    adapter.notifyDataSetChanged();
                    isLoading = false;
                    isGetBankInfo = true;
                    isGetBankInfoMes = true;
                    GetMoneyNumberLimitThread thread2 = new GetMoneyNumberLimitThread();
                    thread2.start();
                    break;
                case 1:
                    out_of_moneySRL.setEnabled(false);
                    out_of_moneySRL.setRefreshing(false);
                    out_of_moneySRL.post(new Runnable() {
                        @Override
                        public void run() {
                            out_of_moneySRL.setEnabled(true);
                        }
                    });
                    Toast.makeText(OutofMoneyActivity.this, "获取银行信息失败，请重试。", Toast.LENGTH_SHORT).show();
                    isLoading = false;
                    isGetBankInfo = false;
                    isGetBankInfoMes = true;
                    break;
                case 2:
                    bundle = msg.getData();
                    setResult(0);
                    Intent intent = new Intent(OutofMoneyActivity.this, OutOfMoneySuccessActivity.class);
                    String mesgStr = bundle.getString("data");
                    bundle.clear();
                    bundle.putString("meshStr", mesgStr);
                    bundle.putString("bankName", bankName);
                    bundle.putString("bankIdLast", personPopWindowCardId.getText().toString().substring(personPopWindowCardId.getText().toString().trim().length() - 4, personPopWindowCardId.getText().toString().trim().length()));
                    bundle.putString("moneyNumber", personPopWindowMoneyNumberET.getText().toString());
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                    break;
                case 3:
                    bundle = msg.getData();
                    personPopWindowOutputMoneyPostBT.setEnabled(true);
                    final AlertDialog alertDialog = new AlertDialog.Builder(OutofMoneyActivity.this).create();
                    alertDialog.show();
                    Window window = alertDialog.getWindow();
                    window.setContentView(R.layout.outof_money_fali_dialog);
                    TextView mesg = (TextView) window.findViewById(R.id.outOfMoneyDialogMessageTV);
                    mesg.setText(bundle.get("data").toString());
                    Button okBT = (Button) window.findViewById(R.id.outOfMoneyDialogOKBT);
                    okBT.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });
                    break;
                case 4:
                    personPopWindowOutputMoneyBankSpinner.setFocusable(true);
                    adapter.notifyDataSetChanged();
                    break;
                case 5:
                    isLoading = true;
                    downDada();
                    break;
                case 6:
                    if (isFirstGoToLogin) {
                        startActivity(new Intent(OutofMoneyActivity.this, LoginActivity.class));
                        PreferencesUtils.putBoolean(OutofMoneyActivity.this, "isFirstLogin", true);
                        isFirstGoToLogin = false;
                        personPopWindowOutputMoneyPostBT.setEnabled(true);
                    }
                    break;
                case 7:
                    bundle = msg.getData();
                    if (!(bundle == null)) {
                        personPopWindowMoneyNumberET.setHint(bundle.get("data").toString());
                        isGetHintText = true;
                    }
                    if (isGetBankInfoMes) {
                        if (isGetBankInfo && isGetHintText) {
                            outOfMoneyLoadRL.setVisibility(View.GONE);
                            personPopWindowOutofMoney.setVisibility(View.VISIBLE);
                            out_of_moneySRL.setEnabled(false);
                        } else {
                            outOfMoneyLoadImg.clearAnimation();
                            outOfMoneyLoadImg.setVisibility(View.GONE);
                            outOfMoneyLoadTV.setVisibility(View.VISIBLE);
                        }
                    }
                    break;
                case 8:
                    isGetHintText = false;
                    if (isGetBankInfoMes) {
                        if (isGetBankInfo && isGetHintText) {
                            outOfMoneyLoadRL.setVisibility(View.GONE);
                            personPopWindowOutofMoney.setVisibility(View.VISIBLE);
                        } else {
                            outOfMoneyLoadImg.clearAnimation();
                            outOfMoneyLoadImg.setVisibility(View.GONE);
                            outOfMoneyLoadTV.setVisibility(View.VISIBLE);
                        }
                    }
                    break;
                case 9:

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outof_money);
        ButterKnife.bind(this);
        initView();
        initData();
        initListener();
    }

    @Override
    public void initView() {
        Animation anim = AnimationUtils.loadAnimation(OutofMoneyActivity.this, R.anim.load);
        anim.start();
        outOfMoneyLoadImg.setAnimation(anim);
    }

    @Override
    public void initData() {
        dataIDList.add("111111");
        dataNameList.add("所属银行");
        personPopWindowOutputMoneyBankSpinner.setFocusable(false);
        adapter = new ArrayAdapter<>(getBaseContext(), R.layout.my_spinner, dataNameList);
        personPopWindowOutputMoneyBankSpinner.setAdapter(adapter);
        downDada();
    }

    private void downDada() {
        if (NetWorkInfo.isNetworkAvailable(OutofMoneyActivity.this)) {
            GetBankInfoThread thread = new GetBankInfoThread();
            thread.start();
        } else {
            Toast.makeText(this, "网络有问题，请检查无误后下拉刷新！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void initListener() {

        personPopWindowOutputMoneyBankSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                isSelectBankInfo = true;
                bankId = dataIDList.get(i);
                bankName = dataNameList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                isSelectBankInfo = false;
            }
        });

        out_of_moneySRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetWorkInfo.isNetworkAvailable(OutofMoneyActivity.this)) {
                    out_of_moneySRL.setEnabled(true);
                    if (!isLoading) {
                        handler.sendEmptyMessageAtTime(5, 10);
                    } else
                        Toast.makeText(OutofMoneyActivity.this, "正在刷新", Toast.LENGTH_SHORT).show();
                } else {
                    out_of_moneySRL.setEnabled(false);
                    out_of_moneySRL.setRefreshing(false);
                    out_of_moneySRL.post(new Runnable() {
                        @Override
                        public void run() {
                            out_of_moneySRL.setEnabled(true);
                        }
                    });
                    Toast.makeText(OutofMoneyActivity.this, "网络未连接，请检查无误后重试！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        setResult(0);
        overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
    }

    /**
     * 校验银行卡卡号
     */
    public boolean checkBankCard(String cardId) {
        char bit = getBankCardCheckCode(cardId.substring(0, cardId.length() - 1));
        if (bit == 'N') {
            return false;
        }
        return cardId.charAt(cardId.length() - 1) == bit;
    }

    /**
     * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
     */
    public char getBankCardCheckCode(String nonCheckCodeCardId) {
        if (nonCheckCodeCardId == null || nonCheckCodeCardId.trim().length() == 0
                || !nonCheckCodeCardId.matches("\\d+")) {
            //如果传的不是数据返回N
            return 'N';
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
    }

    private boolean checkBankId(String bankId) {
        Pattern pattern = Pattern.compile("^\\d{19}$ | ^\\d{16}$");
        Matcher m = pattern.matcher(bankId);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }

    private Message message;

    class GetBankInfoThread extends Thread {

        @Override
        public void run() {
            String strUrlPath;
            if (PreferencesUtils.getBoolean(OutofMoneyActivity.this, "isOtherLogin")) {
                strUrlPath = Constants.getBankUrl + PreferencesUtils.getString(OutofMoneyActivity.this, "otherToken");
            } else {
                strUrlPath = Constants.getBankUrl + PreferencesUtils.getString(OutofMoneyActivity.this, "token");
            }
            String strResult = HttpUtils.submitPost(strUrlPath);
            try {
                if (!strResult.isEmpty() && !strResult.equals("-1")) {
                    Map<String, Object> map = JsonUtils.getMapObj(strResult);
                    if (TextUtils.equals(map.get("status").toString(), "true")) {
                        dataList = JsonUtils.getListMap(map.get("data").toString());
                        message = new Message();
                        message.what = 0;
                        handler.sendMessage(message);
                    } else if (TextUtils.equals(map.get("status").toString(), "false") && TextUtils.equals(map.get("data").toString(), "invalid token")) {
                        strUrlPath = Constants.getBankUrl + TokenUtlils.getToken();
                        strResult = HttpUtils.submitPost(strUrlPath);
                        map = JsonUtils.getMapObj(strResult);
                        if (TextUtils.equals(map.get("status").toString(), "true")) {
                            dataList = JsonUtils.getListMap(map.get("data").toString());
                            message = new Message();
                            message.what = 0;
                            handler.sendMessage(message);
                        } else {
                            handler.sendEmptyMessage(6);
                            isFirstGoToLogin = true;
                        }
                    } else {
                    }
                } else if (strResult.equals("-1")) {
                    message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }

            } catch (Exception e) {
                e.printStackTrace();
                message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        }
    }

    class GetMoneyNumberLimitThread extends Thread {
        @Override
        public void run() {
            super.run();
            String strUrlPath;
            if (PreferencesUtils.getBoolean(OutofMoneyActivity.this, "isOtherLogin")) {
                strUrlPath = Constants.getOutOfMoneyLimitUrl + PreferencesUtils.getString(OutofMoneyActivity.this, "otherToken");
            } else {
                strUrlPath = Constants.getOutOfMoneyLimitUrl + PreferencesUtils.getString(OutofMoneyActivity.this, "token");
            }
            String strResult = HttpUtils.submitPost(strUrlPath);
            try {
                if (!strResult.isEmpty() && !strResult.equals("-1")) {
                    Map<String, Object> map = JsonUtils.getMapObj(strResult);
                    if (TextUtils.equals(map.get("status").toString(), "true") && !map.get("data").toString().equals("[]")) {
                        message = new Message();
                        message.what = 7;
                        bundle = new Bundle();
                        bundle.putString("data", map.get("data").toString());
                        message.setData(bundle);
                        handler.sendMessage(message);
                        handler.sendEmptyMessageAtTime(9, 150);
                    } else if (TextUtils.equals(map.get("status").toString(), "false") && TextUtils.equals(map.get("data").toString(), "invalid token")) {
                        strUrlPath = Constants.getOutOfMoneyLimitUrl + TokenUtlils.getToken();
                        strResult = HttpUtils.submitPost(strUrlPath);
                        map = JsonUtils.getMapObj(strResult);
                        if (TextUtils.equals(map.get("status").toString(), "true") && !map.get("data").toString().equals("[]")) {
                            message = new Message();
                            message.what = 7;
                            bundle = new Bundle();
                            bundle.putString("data", map.get("data").toString());
                            message.setData(bundle);
                            handler.sendMessage(message);
                            handler.sendEmptyMessageAtTime(9, 150);
                        } else {
                            handler.sendEmptyMessage(6);
                        }
                    } else {
                        handler.sendEmptyMessage(8);
                    }
                } else if (strResult.equals("-1")) {
                    handler.sendEmptyMessage(8);
                }

            } catch (Exception e) {
                e.printStackTrace();
                handler.sendEmptyMessage(8);
            }
        }
    }

    class SaveOutofMoneyThread extends Thread {
        @Override
        public void run() {
            params = new HashMap<>();
            params.put("total-fee", personPopWindowMoneyNumberET.getText().toString());
            params.put("bank-id", bankId);
            params.put("real-name", personPopWindowUserName.getText().toString());
            params.put("card", personPopWindowCardId.getText().toString());
            String strUrlPath;
            if (PreferencesUtils.getBoolean(OutofMoneyActivity.this, "isOtherLogin")) {
                strUrlPath = Constants.outMoneyPostUrl + PreferencesUtils.getString(OutofMoneyActivity.this, "otherToken");
            } else {
                strUrlPath = Constants.outMoneyPostUrl + PreferencesUtils.getString(OutofMoneyActivity.this, "token");
            }
            String strResult = HttpUtils.submitPostData(strUrlPath, params, "utf-8");
            try {
                Map<String, Object> map = JsonUtils.getMapObj(strResult);
                if (TextUtils.equals(map.get("status").toString(), "true")) {
                    Message message = new Message();
                    message.what = 2;
                    Bundle bundle = new Bundle();
                    bundle.putString("data", map.get("data").toString());
                    message.setData(bundle);
                    handler.sendMessage(message);
                } else if (TextUtils.equals(map.get("status").toString(), "false") && TextUtils.equals(map.get("data").toString(), "invalid token")) {
                    strUrlPath = Constants.outMoneyPostUrl + TokenUtlils.getToken();
                    strResult = HttpUtils.submitPostData(strUrlPath, params, "utf-8");
                    map = JsonUtils.getMapObj(strResult);
                    if (TextUtils.equals(map.get("status").toString(), "true")) {
                        Message message = new Message();
                        message.what = 2;
                        Bundle bundle = new Bundle();
                        bundle.putString("data", map.get("data").toString());
                        message.setData(bundle);
                        handler.sendMessage(message);
                    } else {
                        handler.sendEmptyMessage(6);
                        isFirstGoToLogin = true;
                    }
                } else {
                    Message message = new Message();
                    message.what = 3;
                    Bundle bundle = new Bundle();
                    bundle.putString("data", map.get("data").toString());
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            setResult(0);
        }
    }
}
