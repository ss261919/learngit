package vaporsome.com.vaporsome.common.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.Button;
import android.widget.TextView;

import vaporsome.com.vaporsome.R;

public class TimeCountUtil extends CountDownTimer {
    private Activity mActivity;
    private TextView textView;//按钮

    // 在这个构造方法里需要传入三个参数，一个是Activity，一个是总的时间millisInFuture，一个是countDownInterval，然后就是你在哪个按钮上做这个是，就把这个按钮传过来就可以了
    public TimeCountUtil(Activity mActivity, long millisInFuture, long countDownInterval, TextView textView) {
        super(millisInFuture, countDownInterval);
        this.mActivity = mActivity;
        this.textView = textView;
    }


    @SuppressLint("NewApi")
    @Override
    public void onTick(long millisUntilFinished) {
//        textView.setClickable(false);//设置不能点击
        textView.setEnabled(false);
        textView.setText(millisUntilFinished / 1000 + "秒后可重新发送");//设置倒计时时间

        //设置按钮为灰色，这时是不能点击的
//        btn.setBackgroundResource(R.drawable.login_button_shape_nomal);
//        Spannable span = new SpannableString(textView.getText().toString());//获取按钮的文字
//        span.setSpan(new ForegroundColorSpan(Color.parseColor("#D94E42")), 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//讲倒计时时间显示为红色
//        textView.setText(span);
    }

    @SuppressLint("NewApi")
    @Override
    public void onFinish() {
        textView.setText("重新获取验证码");
//        textView.setClickable(true);//重新获得点击
        textView.setEnabled(true);
//        btn.setBackgroundResource(R.drawable.login_button_shape_nomal);//还原背景色
    }
}
