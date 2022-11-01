package client.halouhuandian.app15.view.customUi.activityDialog;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;

public class DialogMainAnimation {

    private Activity activity;
    private LinearLayout dialogPanel;
    private LinearLayout grayBackground;

    private int dialogPanelTime = 700;
    private IFDialogAnimationListener iFDialogAnimationListener;


    public interface IFDialogAnimationListener {
        void onStartDialogAnimationStart();
        void onStartDialogAnimationEnd();
        void onCloseDialogAnimationStart();
        void onCloseDialogAnimationEnd();
    }

    public DialogMainAnimation(Activity activity, LinearLayout dialogPanel, LinearLayout grayBackground, IFDialogAnimationListener iFDialogAnimationListener) {
        this.activity = activity;
        this.dialogPanel = dialogPanel;
        this.grayBackground = grayBackground;
        this.iFDialogAnimationListener = iFDialogAnimationListener;
    }

    public DialogMainAnimation(Activity activity, LinearLayout dialogPanel, LinearLayout grayBackground) {
        this.activity = activity;
        this.dialogPanel = dialogPanel;
        this.grayBackground = grayBackground;
    }

    public void startAnimation() {
        An_1();
    }

    public void closeAnimation() {
        An_2();
    }

    private void An_1() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(dialogPanel, "alpha", 0, 1);
        objectAnimator.setDuration(dialogPanelTime);
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if(iFDialogAnimationListener!=null){
                    iFDialogAnimationListener.onStartDialogAnimationStart();
                }
                grayBackground.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationEnd(Animator animator) {
                if(iFDialogAnimationListener!=null){
                    iFDialogAnimationListener.onStartDialogAnimationEnd();
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        objectAnimator.start();
    }

    private void An_2() {

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(dialogPanel, "alpha", 1, 0);
        objectAnimator.setDuration(dialogPanelTime);
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if(iFDialogAnimationListener!=null){
                    iFDialogAnimationListener.onCloseDialogAnimationStart();
                }
            }
            @Override
            public void onAnimationEnd(Animator animator) {
                grayBackground.setVisibility(View.GONE);
                if(iFDialogAnimationListener!=null){
                    iFDialogAnimationListener.onCloseDialogAnimationEnd();
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        objectAnimator.start();
    }
}