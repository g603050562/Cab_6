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
public final class CurrentDetectionSettingDialog extends Dialog implements View.OnClickListener {
    private EditText etCurrentThreshold;

    private Consumer<Float> currentDetectionConsumer;
    private Consumer cancleAutoControlConsumerConsumer;

    public CurrentDetectionSettingDialog(@NonNull Context context) {
        super(context, R.style.Translucent_NoTitle);
        setContentView(R.layout.dialog_current_detection_setting);
        setUi();
    }

    private void setUi() {
        etCurrentThreshold = findViewById(R.id.et_currentThreshold);
        findViewById(R.id.enter).setOnClickListener(this);
        findViewById(R.id.cancle).setOnClickListener(this);

        setCancelable(false);
        etCurrentThreshold.setSelection(etCurrentThreshold.getText().length());
        etCurrentThreshold.addTextChangedListener(new TextWatcher() {
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
                    etCurrentThreshold.removeTextChangedListener(this);
                    etCurrentThreshold.setText(match(s.toString()));
                    etCurrentThreshold.setSelection(etCurrentThreshold.getText().length());
                    etCurrentThreshold.addTextChangedListener(this);
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
                final String currentThresholdStr = etCurrentThreshold.getText().toString();
                float currentThreshold = 3f;
                if (!TextUtils.isEmpty(currentThresholdStr)) {
                    try {
                        currentThreshold = Float.parseFloat(currentThresholdStr);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    if (!(currentThreshold >= 1.5f && currentThreshold <= 10)) {
                        Toast.makeText(getContext(), "电流必须是[1.5A~10A]", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                confirm(currentThreshold);
                break;
            case R.id.cancle:
                cancle();
                break;
        }
    }

    private void confirm(float currentThreshold) {
        if (currentDetectionConsumer != null) {
            currentDetectionConsumer.accept(currentThreshold);
        }
        dismiss();
    }

    private void cancle() {
        if (cancleAutoControlConsumerConsumer != null) {
            cancleAutoControlConsumerConsumer.accept("");
        }
        dismiss();
    }

    public void setCurrentDetectionConsumer(Consumer<Float> currentDetectionConsumer) {
        this.currentDetectionConsumer = currentDetectionConsumer;
    }

    public void setCancleAutoControlConsumer(Consumer cancleAutoControlConsumerConsumer) {
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
