package client.halouhuandian.app15.view.customUi.activityDialog;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import client.halouhuandian.app15.R;
import client.halouhuandian.app15.pub.util.UtilPublic;

public class BlackExchangeDialog_12 extends FrameLayout {

    private Context context;
    private Activity activity;

    private LinearLayout panel;
    private TextView t_1;
    private TextView t_2;
    private LinearLayout exchange_panel;
    private LinearLayout input_panel;
    private LinearLayout image_panel;
    private LinearLayout output_panel;
    private TextView inputPerView;
    private TextView outputPerView;
    private Thread showDialogThread;

    private boolean showDialogThreadCode = true;
    private int showDialogThreadCount = -1;
    private boolean isShow = false;

    private String fInfo = "";


    public BlackExchangeDialog_12(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public BlackExchangeDialog_12(Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public BlackExchangeDialog_12(Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    public void setActivity(Activity activity){
        this.activity = activity;
    }


    private void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_main_1080p_black_12_dialog, null);
        t_1 = view.findViewById(R.id.t_1);
        t_2 = view.findViewById(R.id.t_2);
        panel = view.findViewById(R.id.panel);
        exchange_panel = view.findViewById(R.id.exchange_panel);
        input_panel = view.findViewById(R.id.input_panel);
        image_panel = view.findViewById(R.id.image_panel);
        output_panel = view.findViewById(R.id.output_panel);
        inputPerView = view.findViewById(R.id.input);
        outputPerView = view.findViewById(R.id.output);
        addView(view);
    }

    public void showDialog(String info , int time , int type , BlackExchangeDialog_9.BlackExchangeDialog_9_listener blackExchangeDialog_9_listener ){

        showDialogThreadCount = time;
        fInfo = info;

        //自减字数处理
        String showInfo = "";
        if (info.indexOf("<$time>") != -1) {
            showInfo = info.replace("<$time>", time + "");
            blackExchangeDialog_9_listener.messageReturn(showInfo);
        }else{
            showInfo = info;
        }
        t_1.setText(showInfo);

        if(showDialogThread == null){
            showDialogThread = new Thread(){
                @Override
                public void run() {
                    super.run();
                    while (showDialogThreadCode == true){
                        try {
                            sleep(1000);
                            if(showDialogThreadCount > 0){
                                showDialogThreadCount = showDialogThreadCount - 1;
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        show();

                                        if(showDialogThreadCount <= 0){
                                            t_2.setText(0+"");
                                        }else{
                                            t_2.setText(showDialogThreadCount+"");
                                        }

                                        if (fInfo.indexOf("<$time>") != -1) {
                                            t_1.setText(fInfo.replace("<$time>", showDialogThreadCount + ""));
                                        }else{
                                            t_1.setText(fInfo);
                                        }
                                    }
                                });
                            }
                            if(showDialogThreadCount == 0){
                                showDialogThreadCount = -1;
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        hide();
                                    }
                                });
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            };
            showDialogThread.start();
        }
    }

    public void showExchangeInfo(int inputPer , int outputPer){
        inputPerView.setText(inputPer + "%");
        outputPerView.setText(outputPer + "%");

        finish_2();

        new Thread(){
            @Override
            public void run() {
                super.run();
                for(int i = 0 ; i < 75 ; i++){
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            exchange_panel.setVisibility(VISIBLE);
                            LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) exchange_panel.getLayoutParams(); //取控件textView当前的布局参数 linearParams.height = 20;// 控件的高强制设成20
                            linearParams.height = linearParams.height + UtilPublic.dip2px(activity,2);// 控件的宽强制设成30
                            exchange_panel.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
                        }
                    });
                    try {
                        sleep(7);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showExchangeInfo_1();
                    }
                });
            }
        }.start();
    }

    private void showExchangeInfo_1(){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(input_panel, "alpha", 0, 1);
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                input_panel.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                showExchangeInfo_2();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        objectAnimator.setDuration(1000);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.start();
    }
    private void showExchangeInfo_2(){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(image_panel, "alpha", 0, 1);
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                image_panel.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                showExchangeInfo_3();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        objectAnimator.setDuration(1000);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.start();
    }
    private void showExchangeInfo_3(){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(output_panel, "alpha", 0, 1);
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                output_panel.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        objectAnimator.setDuration(1000);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.start();
    }

    private void show(){
        if(!isShow){
            isShow = true;
            start_1();
        }
    }
    private void hide(){
        if(isShow){
            isShow = false;
            finish_1();
        }
    }

    private void start_1(){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(panel, "alpha", 0, 1);
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                panel.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        objectAnimator.setDuration(300);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.start();
    }

    private void finish_1(){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(panel, "alpha", 1, 0);
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                panel.setVisibility(GONE);
                finish_2();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        objectAnimator.setDuration(300);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.start();
    }

    private void finish_2(){
        input_panel.setVisibility(INVISIBLE);
        image_panel.setVisibility(INVISIBLE);
        output_panel.setVisibility(INVISIBLE);
        exchange_panel.setVisibility(GONE);
        LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) exchange_panel.getLayoutParams(); //取控件textView当前的布局参数 linearParams.height = 20;// 控件的高强制设成20
        linearParams.height = UtilPublic.dip2px(activity,1);// 控件的宽强制设成30
        exchange_panel.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
    }

    public void onDestroy(){

    }
}

