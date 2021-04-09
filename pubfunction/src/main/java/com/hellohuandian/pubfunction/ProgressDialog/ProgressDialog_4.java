package com.hellohuandian.pubfunction.ProgressDialog;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hellohuandian.pubfunction.animation.DialogAnimation;


public class ProgressDialog_4 {


    private Activity activity;
    private LinearLayout panel, mInfoPanel;
    private TextView time, info;

    //线程相关
    private int threadStatus = 0;
    private Thread thread;
    private int mtime;
    private Handler setTimeHandler, dismissPanelHandler, speakHandler;

    private DialogAnimation dialogAnimation;

    int open_an = 0;

    public ProgressDialog_4(Activity mActivity, LinearLayout mPanel, final TextView mTime, TextView mInfo, Handler mSpeakHandler, LinearLayout mInfoPanel) {
        this.activity = mActivity;
        this.panel = mPanel;
        this.time = mTime;
        this.info = mInfo;
        this.mInfoPanel = mInfoPanel;
        this.speakHandler = mSpeakHandler;

        dialogAnimation = new DialogAnimation(activity, mInfoPanel, panel, new DialogAnimation.IFDialogAnimationListener() {
            @Override
            public void onStartDialogAnimationStart() {

            }

            @Override
            public void onStartDialogAnimationEnd() {

            }

            @Override
            public void onCloseDialogAnimationStart() {

            }

            @Override
            public void onCloseDialogAnimationEnd() {

            }
        });

        setTimeHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                time.setText(mtime + "");
            }
        };

        dismissPanelHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                dialogAnimation.closeAnimation();
                open_an = 0;
            }
        };

        thread = new Thread() {
            @Override
            public void run() {
                super.run();
                while (threadStatus == 0) {
                    if (activity == null) {
                        break;
                    }

                    if (mtime > 0) {
                        setTimeHandler.sendMessage(setTimeHandler.obtainMessage());
                        mtime = mtime - 1;
                        SystemClock.sleep(1000);
                    }
                    if (mtime == 0 && open_an == 1) {
                        mtime = -1;
                        open_an = 0;
                        dismissPanelHandler.sendMessage(new Message());
                    }
                }

            }
        };
        thread.start();
    }

    public void show(String msg_str, int time_int, int speakType) {

        if (activity == null) {
            return;
        }
        try {
            time.setText(time_int + "");
            info.setText(msg_str + "");
            mtime = time_int;

            if (open_an == 0) {
                dialogAnimation.startAnimation();
                open_an = 1;
            }

            if (speakHandler != null && speakType == 1) {
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("msg", msg_str);
                message.setData(bundle);
                speakHandler.sendMessage(message);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onDestory() {
        panel.setVisibility(View.GONE);
        threadStatus = 1;
    }
}
