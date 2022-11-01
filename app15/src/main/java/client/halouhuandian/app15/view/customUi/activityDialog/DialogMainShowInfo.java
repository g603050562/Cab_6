package client.halouhuandian.app15.view.customUi.activityDialog;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import client.halouhuandian.app15.model.dao.fileSave.LocalLog;

public class DialogMainShowInfo {

    public interface SpeakDialogListener {
        void onSpeakDialogReturn(String msg);
    }

    //上下文
    private Activity activity;
    //背景蒙版
    private LinearLayout grayBackground;
    //dialog背景
    private LinearLayout dialogPanel;
    //显示信息
    private TextView time, info;
    //线程相关
    private int threadCode = 0;
    private Thread thread;
    private int countDownTime = -100;
    //dialog正在运行
    private boolean dialogIsActivity = false;
    private String showInfo = "";

    private DialogMainAnimation dialogMainAnimation;
    private SpeakDialogListener speakDialogListener;

    public DialogMainShowInfo(Activity mActivity, LinearLayout dialogPanel, TextView mTime, TextView mInfo, LinearLayout dialogBackground, SpeakDialogListener speakDialogListener) {
        this.activity = mActivity;
        this.grayBackground = dialogBackground;
        this.time = mTime;
        this.info = mInfo;
        this.dialogPanel = dialogPanel;
        this.speakDialogListener = speakDialogListener;
        init();
    }

    private void init() {
        //动画初始化
        if (dialogMainAnimation == null) {
            dialogMainAnimation = new DialogMainAnimation(activity, dialogPanel, grayBackground);
        }
        //计时线程
        if (thread == null) {
            thread = new Thread() {
                @Override
                public void run() {
                    super.run();
                    while (threadCode == 0) {
                        if (activity == null) {
                            break;
                        }
                        if (countDownTime > 0) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    time.setText(countDownTime + "");
                                    if (showInfo.indexOf("<$time>") != -1) {
                                        String tempStrng = showInfo;
                                        tempStrng = tempStrng.replace("<$time>", countDownTime + "");
                                        info.setText(tempStrng);
                                    }
                                }
                            });
                            countDownTime = countDownTime - 1;
                        } else if (countDownTime <= 0 && countDownTime != -100) {
                            if(dialogIsActivity == true){
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialogMainAnimation.closeAnimation();
                                        dialogIsActivity = false;
                                    }
                                });
                            }
                            countDownTime = -100;
                        }
                        try {
                            sleep(1000);
                        } catch (Exception e) {
                            LocalLog.getInstance().writeLog(e.toString(),DialogMainShowInfo.class);
                        }
                    }
                }
            };
            thread.start();
        }
    }


    public void show(String showInfo, int countDownTime, int speakType) {
        this.showInfo = showInfo;
        this.countDownTime = countDownTime;
        //上下文为空的话 直接返回
        if (activity == null) {
            return;
        }
        //如果dialog没有运行 开始运行 反之跳过
        if (dialogIsActivity == false) {
            dialogMainAnimation.startAnimation();
            dialogIsActivity = true;
        }
        //自减字数处理
        if (showInfo.indexOf("<$time>") != -1) {
            showInfo = showInfo.replace("<$time>", countDownTime + "");
        }
        //填写文字
        time.setText(countDownTime + "");
        info.setText(showInfo + "");
        //语音处理
        if (speakType == 1) {
            speakDialogListener.onSpeakDialogReturn(showInfo);
        }
    }

    public void onDestroy() {
        grayBackground.setVisibility(View.GONE);
        threadCode = 1;
    }
}
