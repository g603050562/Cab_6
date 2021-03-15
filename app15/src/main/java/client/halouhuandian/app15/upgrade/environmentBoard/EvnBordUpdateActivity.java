package client.halouhuandian.app15.upgrade.environmentBoard;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.apps.ExtensionTaskUpgrade.categories.environmentBoard.EvnBordUpdateProgram;
import com.apps.ExtensionTaskUpgrade.categories.environmentBoard.EvnCallBack;
import com.apps.ExtensionTaskUpgrade.categories.environmentBoard.EvnUpdateStatus;
import com.apps.ExtensionTaskUpgrade.core.dispatchers.TaskDispatcher;
import com.apps.ExtensionTaskUpgrade.core.ioAction.DeviceIoAction;
import com.apps.ExtensionTaskUpgrade.core.ioAction.DeviceIoType;
import com.hellohuandian.pubfunction.Unit.LogUtil;

import java.io.IOException;

import client.halouhuandian.app15.A_Main2;
import client.halouhuandian.app15.MyApplication;
import client.halouhuandian.app15.R;
import client.halouhuandian.app15.upgrade.CanSerialDataReceiver;

/**
 * Author:      Lee Yeung
 * Create Date: 2020/4/23
 * Description: 环境板升级
 */
public class EvnBordUpdateActivity extends Activity {
    private TextView cabid;
    private TextView tel;
    private TextView time;
    private ProgressBar p_bar;
    private EditText log;

    private boolean isReboot;
    private String filePath;

    private CanSerialDataReceiver canSerialDataReceiver = new CanSerialDataReceiver() {
        private byte[] canData;

        @Override
        public void init() {
            TaskDispatcher.getInstance().addDeviceIoAction(DeviceIoType.CANBUS, new DeviceIoAction() {
                @Override
                public void write(byte[] data) throws IOException {
                    MyApplication.serialAndCanPortUtils.canSendOrder(data);
                }

                @Override
                public byte[] read() throws IOException {
                    return canData;
                }
            });
        }

        @Override
        public void onCanResultApp(byte[] canData) {
            this.canData = canData;
            //通知解析分发
            TaskDispatcher.getInstance().notifyRead(DeviceIoType.CANBUS);
        }
    };

    private volatile boolean isFinishing;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            time.setText(msg.what + "s");
            if (msg.what == 0) {
                finish();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updata_evn);
        A_Main2.AN_IS_RUN = 1;
        A_Main2.BAR_IS_RUN = 1;
        initViews();
        initListener();
        startCountdown();
        update();
    }

    private void initViews() {
        cabid = findViewById(R.id.cabid);
        tel = findViewById(R.id.tel);
        time = findViewById(R.id.time);
        p_bar = findViewById(R.id.p_bar);
        log = findViewById(R.id.log);

        filePath = getIntent().getStringExtra("path");

        cabid.setText(getIntent().getStringExtra("cabid"));
        tel.setText(getIntent().getStringExtra("tel"));
        isReboot = getIntent().getBooleanExtra("isReboot", true);
    }

    private void initListener() {
        MyApplication.getInstance().addListener(canSerialDataReceiver);
    }

    private void startCountdown() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int limit = 600;
                while (limit >= 0 && !isFinishing) {
                    handler.sendEmptyMessage(limit);
                    limit--;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isFinishing = true;
        A_Main2.AN_IS_RUN = 0;
        A_Main2.BAR_IS_RUN = 0;
        A_Main2.upgrading.clear();
        handler.removeCallbacksAndMessages(null);
        MyApplication.getInstance().deletListener(canSerialDataReceiver);
    }

    private void update() {

        LogUtil.I("filePath:" + filePath);

        final Handler handler = new Handler();
        EvnBordUpdateProgram evnBordUpdateProgram = new EvnBordUpdateProgram(isReboot);
        evnBordUpdateProgram.setFilePath(filePath);
        evnBordUpdateProgram.setEvnCallBack(new EvnCallBack() {
            @Override
            public void update(long process, long total, final String info, final int status) {
                if (process > 0 && total > 0) {
                    p_bar.setProgress((int) (process / (float) total * 100));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            log.setText(log.getText() + "\n" + info);
                            log.setSelection(log.getText().toString().length());
                        }
                    });

                    switch (status) {
                        case EvnUpdateStatus
                                .SUCCESSED:
                        case EvnUpdateStatus
                                .FAILED:
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            }, 5000);
                            break;
                    }
                }
            }
        });
        TaskDispatcher.getInstance().dispatch(DeviceIoType.CANBUS, evnBordUpdateProgram);
    }
}
