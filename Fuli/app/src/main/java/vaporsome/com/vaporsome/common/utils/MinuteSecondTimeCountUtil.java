package vaporsome.com.vaporsome.common.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.CountDownTimer;
import android.widget.TextView;

public class MinuteSecondTimeCountUtil extends CountDownTimer {
    private Activity mActivity;
    private TextView textView;//按钮

    // 在这个构造方法里需要传入三个参数，一个是Activity，一个是总的时间millisInFuture，一个是countDownInterval，然后就是你在哪个按钮上做这个是，就把这个按钮传过来就可以了
    public MinuteSecondTimeCountUtil(Activity mActivity, long millisInFuture, long countDownInterval, TextView textView) {
        super(millisInFuture, countDownInterval);
        this.mActivity = mActivity;
        this.textView = textView;
    }


    @SuppressLint("NewApi")
    @Override
    public void onTick(long millisUntilFinished) {
//        textView.setClickable(false);//设置不能点击
        textView.setEnabled(false);
        if (!(millisUntilFinished == 0))
            textView.setText(millisUntilFinished / 1000 / 60 + ":" + (millisUntilFinished / 1000 % 60) + ":" +
                    String.valueOf(millisUntilFinished).substring(String.valueOf(millisUntilFinished).length() - 2, String.valueOf(millisUntilFinished).length()));//设置倒计时时间
        else {
            textView.setText("00:00:00");
        }

        //设置按钮为灰色，这时是不能点击的
//        btn.setBackgroundResource(R.drawable.login_button_shape_nomal);
//        Spannable span = new SpannableString(textView.getText().toString());//获取按钮的文字
//        span.setSpan(new ForegroundColorSpan(Color.parseColor("#D94E42")), 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//讲倒计时时间显示为红色
//        textView.setText(span);
    }

    @SuppressLint("NewApi")
    @Override
    public void onFinish() {
//        textView.setClickable(true);//重新获得点击
        textView.setEnabled(true);
//        btn.setBackgroundResource(R.drawable.login_button_shape_nomal);//还原背景色
    }

    private String getRandom() {
        return String.valueOf(Math.random() * 100).substring(1, 3);
    }

}
