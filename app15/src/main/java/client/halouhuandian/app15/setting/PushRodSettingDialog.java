package client.halouhuandian.app15.setting;

import android.app.Dialog;
import android.content.Context;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.util.Consumer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import client.halouhuandian.app15.R;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Author:      Lee Yeung
 * Create Date: 2020/9/17
 * Description:
 */
public final class PushRodSettingDialog extends Dialog implements View.OnClickListener {
    private EditText et_currentPushRodTime;

    private Consumer<Float> currentDetectionConsumer;
    private Consumer cancleAutoControlConsumerConsumer;

    public PushRodSettingDialog(@NonNull Context context) {
        super(context, R.style.Translucent_NoTitle);
        setContentView(R.layout.dialog_set_pushrod_setting);
        setUi();
    }

    private void setUi() {
        et_currentPushRodTime = findViewById(R.id.et_currentPushRodTime);
        findViewById(R.id.enter).setOnClickListener(this);
        findViewById(R.id.cancle).setOnClickListener(this);

        setCancelable(false);
        et_currentPushRodTime.setSelection(et_currentPushRodTime.getText().length());
        et_currentPushRodTime.addTextChangedListener(new TextWatcher() {
            //正则匹配：99.9(保留一位小数)
            private final String regex = "\\d*\\.?\\d?";
            private final Pattern pattern = Pattern.compile(regex);

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    et_currentPushRodTime.removeTextChangedListener(this);
                    et_currentPushRodTime.setText(match(s.toString()));
                    et_currentPushRodTime.setSelection(et_currentPushRodTime.getText().length());
                    et_currentPushRodTime.addTextChangedListener(this);
                }
            }

            private String match(String text) {
                if (!TextUtils.isEmpty(text)) {
                    Matcher matcher = pattern.matcher(text);
                    if (matcher.find()) {
                        return matcher.group(0);
                    }
                }

                return text;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.enter:
                final String et_currentPushRodTimeStr = et_currentPushRodTime.getText().toString();
                float pushTime = 3f;
                if (!TextUtils.isEmpty(et_currentPushRodTimeStr)) {
                    try {
                        pushTime = Float.parseFloat(et_currentPushRodTimeStr);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    if (!(pushTime >= 3f && pushTime <= 15)) {
                        Toast.makeText(getContext(), "时间必须是[3~15]", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                confirm(pushTime);
                break;
            case R.id.cancle:
                cancle();
                break;
        }
    }

    private void confirm(float pushTime) {
        if (currentDetectionConsumer != null) {
            currentDetectionConsumer.accept(pushTime);
        }
        dismiss();
    }

    private void cancle() {
        if (cancleAutoControlConsumerConsumer != null) {
            cancleAutoControlConsumerConsumer.accept("");
        }
        dismiss();
    }

    public void setCurrentPushTimeConsumer(Consumer<Float> currentDetectionConsumer) {
        this.currentDetectionConsumer = currentDetectionConsumer;
    }

    public void setCancleTimeConsumer(Consumer cancleAutoControlConsumerConsumer) {
        this.cancleAutoControlConsumerConsumer = cancleAutoControlConsumerConsumer;
    }

    /**
     * 利用android的事件分发机制，用户在点击除EditText...之外的控件隐藏软键盘输入窗口。
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isShouldHideInput(View v, MotionEvent ev) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], right = left + v.getWidth(), bottom = top + v.getHeight();
            if (ev.getX() > left && ev.getX() < right && ev.getY() > top && ev.getY() < bottom) {
                return false;// 忽略
            } else
                return true;
        }
        return false;
    }

    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onBackPressed() {
        if (isShowing()) {
            dismiss();
        }
    }
}
