package client.halouhuandian.app15.view.customUi.activityDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import client.halouhuandian.app15.R;
import client.halouhuandian.app15.pub.util.UtilPublic;


public class DialogUpdateShowInfo {

    private Activity activity;
    private int mtime = -1;
    private AlertDialog dialog;
    private TextView t_1, t_2;
    private Handler handler;
    private View view;
    private int threadStatus = 0;

    private Thread thread;

    public DialogUpdateShowInfo(Activity mActivity) {

        this.activity = mActivity;

        if (activity == null) {
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(activity);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        dialog = builder.create();
        view = inflater.inflate(R.layout.alertdialog_update_1080p, null);

        dialog.getWindow().setDimAmount(0);

        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();

        lp.y = UtilPublic.dip2px(activity, -250); // 新位置Y坐标
        lp.width = UtilPublic.dip2px(activity, 500); // 宽度
        lp.height = UtilPublic.dip2px(activity, 180); // 高度

        dialogWindow.setAttributes(lp);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                t_1.setText(mtime + "");
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
                    try {
                        if (mtime > 0) {
                            handler.sendMessage(new Message());
                            mtime = mtime - 1;
                        } else if (mtime <= 0) {
                            dialog.dismiss();
                            mtime = -1;
                        }
                    } catch (Exception e) {
                        System.out.println("dialog：" + e.toString());
                    }
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        };
        thread.start();

    }

    public void show(String msg, int time) {

        if (activity == null) {
            return;
        }
        try {
            Window dialogWindow = dialog.getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();

            lp.y = UtilPublic.dip2px(activity, 0); // 新位置Y坐标
            lp.width = UtilPublic.dip2px(activity, 700); // 宽度
            lp.height = UtilPublic.dip2px(activity, 180); // 高度

            dialogWindow.setAttributes(lp);

            mtime = time;

            t_1 = (TextView) view.findViewById(R.id.t_1);
            t_2 = (TextView) view.findViewById(R.id.t_2);
            t_2.setText(msg + "");

            dialog.show();
            dialog.getWindow().setContentView(view);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void onDestory() {
        threadStatus = 1;
        dialog.dismiss();
    }
}
