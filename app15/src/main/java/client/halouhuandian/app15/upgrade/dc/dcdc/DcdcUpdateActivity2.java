package client.halouhuandian.app15.upgrade.dc.dcdc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.apps.ExtensionTaskUpgrade.categories.dc.DC_UpgradeProgram;
import com.apps.ExtensionTaskUpgrade.categories.dc.dcdc.DCDC_UpgradeProgram;
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
 * Description: DCDC单发升级
 */
public class DcdcUpdateActivity2 extends Activity {
    private TextView cabid;
    private TextView tel;
    private TextView time;
    private ProgressBar p_bar;
    private EditText log;

    private String filePath;
    private byte address;

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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.I("已经在升级了");
    }

    private void initViews() {
        TextView info = findViewById(R.id.info);
        info.setText("正在进行DCDC升级");
        cabid = findViewById(R.id.cabid);
        tel = findViewById(R.id.tel);
        time = findViewById(R.id.time);
        p_bar = findViewById(R.id.p_bar);
        log = findViewById(R.id.log);

        filePath = getIntent().getStringExtra("path");
        address = getIntent().getByteExtra("address", (byte) 0);
        cabid.setText(getIntent().getStringExtra("cabid"));
        tel.setText(getIntent().getStringExtra("tel"));

        LogUtil.I("升级路径" + filePath);
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
        A_Main2.upgrading.clear();
        A_Main2.BAR_IS_RUN = 0;
        handler.removeCallbacksAndMessages(null);
        MyApplication.getInstance().deletListener(canSerialDataReceiver);
    }

    private void update() {
        LogUtil.I("address:" + address);
        if (address <= 0) {
            return;
        }
        DC_UpgradeProgram dc_upgradeProgram = new DCDC_UpgradeProgram(address);
        dc_upgradeProgram.setUpgradeFilePath(filePath);
        dc_upgradeProgram.setOnDCUpgradeCallBack(new DC_UpgradeProgram.OnDCUpgradeCallBack() {
            @Override
            public void onUpgrading(byte address, byte status, final String statusInfo, final long process, final long total) {
                runOnUiThread(new Runnable() {
                    String proce = "";

                    @Override
                    public void run() {
                        if (process > 0 && total > 0) {
                            proce = "%" + ((int) (process / (float) total * 100));
                        }
                        log.setText(log.getText() + "\n" + statusInfo + proce);
                        log.setSelection(log.getText().toString().length());
                    }
                });

                switch (status) {
                    case DC_UpgradeProgram.DCUpgradeStatus.UPGRADING:
                        if (process > 0 && total > 0) {
                            p_bar.setProgress((int) (process / (float) total * 100));
                        }
                        break;
                    case DC_UpgradeProgram.DCUpgradeStatus
                            .SUCCESSED:
                    case DC_UpgradeProgram.DCUpgradeStatus
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

            @Override
            public void onError(final byte address, final byte statusFlag, final String statusInfo) {
                LogUtil.I("UpDataBattery：   错误 - " + statusInfo);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showError(address, statusFlag, statusInfo);
                    }
                });
            }
        });
        TaskDispatcher.getInstance().dispatch(DeviceIoType.CANBUS, dc_upgradeProgram);
    }


    private void showError(byte address, byte statusFlag, String statusInfo) {
        log.setText(log.getText() + "\n" + statusInfo);
        log.setSelection(log.getText().toString().length());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 5000);
    }
}
