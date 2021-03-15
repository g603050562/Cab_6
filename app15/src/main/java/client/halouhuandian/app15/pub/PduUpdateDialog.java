package client.halouhuandian.app15.pub;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.hellohuandian.pubfunction.Root.RootCommand;

import java.text.SimpleDateFormat;
import java.util.Date;

import client.halouhuandian.app15.A_Main;
import client.halouhuandian.app15.R;

import static client.halouhuandian.app15.A_Main.showProgressDialogHandler;


public class PduUpdateDialog {

    private Activity activity;
    private int updateCount = 1;
    private AlertDialog mAlertDialog;
    private View view;
    private ProgressBar p_bar;
    private EditText logview;

    private Handler logHandler;


    public PduUpdateDialog(Activity activity) {
        this.activity = activity;
        LayoutInflater inflater = LayoutInflater.from(activity);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        mAlertDialog = builder.create();
        view = inflater.inflate(R.layout.admin_item_4_dialog, null);
        p_bar = (ProgressBar) view.findViewById(R.id.p_bar);
        p_bar.setProgress(updateCount);
        logview = (EditText) view.findViewById(R.id.log);
        logview.setText("");

        logHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                String log = msg.getData().getString("log");
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String sub_log = logview.getText().toString();
                if (sub_log.length() > 6000) {
                    int a = sub_log.length() - 6000;
                    sub_log = sub_log.substring(a, sub_log.length());
                }
                logview.setText(sub_log + "\n" + df.format(new Date()) + "    " + log);
                logview.setSelection(logview.getText().length());
            }
        };
    }

    public void show() {
        mAlertDialog.show();
        mAlertDialog.getWindow().setContentView(view);
        mAlertDialog.setCancelable(false);
        mAlertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    }

    private void dismiss() {
        mAlertDialog.dismiss();
    }

    public void setLog(String log) {
    }

}
