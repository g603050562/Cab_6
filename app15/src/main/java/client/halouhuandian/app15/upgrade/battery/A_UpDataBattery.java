package client.halouhuandian.app15.upgrade.battery;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.apps.ExtensionTaskUpgrade.categories.battery.BatteryAttribute;
import com.apps.ExtensionTaskUpgrade.categories.battery.BatteryUpgradeProgram;
import com.apps.ExtensionTaskUpgrade.categories.battery.NuoWan.NuoWanBatteryUpgradeProgram;
import com.apps.ExtensionTaskUpgrade.core.dispatchers.TaskDispatcher;
import com.apps.ExtensionTaskUpgrade.core.ioAction.DeviceIoAction;
import com.apps.ExtensionTaskUpgrade.core.ioAction.DeviceIoType;
import com.hellohuandian.pubfunction.ProgressDialog.ProgressDialog_3;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import client.halouhuandian.app15.A_Main2;
import client.halouhuandian.app15.MyApplication;
import client.halouhuandian.app15.R;
import client.halouhuandian.app15.upgrade.CanSerialDataReceiver;


public class A_UpDataBattery extends Activity {
    private ProgressBar p_bar;
    private TextView title, cabid, tel, time;
    private EditText logView;

    //新提示框
    private ProgressDialog_3 progressDialog_2;

    private Handler barHandler, showProgressDialogHandler, logHandler, setTimeUIHndler;

    private Activity activity;

    private int tar_door;
    private String path = "";
    private String type = "";
    private String cabid_str = "";
    private String tel_str = "";

    private int timeState = 1;
    private int timeCount = 900;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.updata_battery);

        tar_door = Integer.parseInt(getIntent().getStringExtra("door"));
        path = getIntent().getStringExtra("path");
        type = getIntent().getStringExtra("manu");

        cabid_str = getIntent().getStringExtra("cabid");
        tel_str = getIntent().getStringExtra("tel");

        findView();
        handler();
        init();

        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();

                while (timeState == 1) {

                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (timeCount > 0) {

                        Message message = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putInt("time", timeCount);
                        message.setData(bundle);
                        setTimeUIHndler.sendMessage(message);
                        timeCount = timeCount - 1;

                    } else {

                        A_Main2.AN_IS_RUN = 0;
                        A_Main2.BAR_IS_RUN = 0;
                        activity.finish();
                    }
                }

            }
        };
        thread.start();

    }

    private void initListener() {
        MyApplication.getInstance().addActivity(this);
        MyApplication.getInstance().addListener(canSerialDataReceiver);
    }

    private void init() {
        activity = this;

        A_Main2.AN_IS_RUN = 1;
        A_Main2.BAR_IS_RUN = 1;
        progressDialog_2 = new ProgressDialog_3(activity);

        title.setText("正在升级第" + tar_door + "号舱门电池，请稍后！");
        cabid.setText(cabid_str);
        tel.setText(tel_str);

        System.out.println("UpDataBattery：   开始");
        Message messagelog = new Message();
        Bundle bundlelog = new Bundle();
        bundlelog.putString("log", "开始升级");
        messagelog.setData(bundlelog);
        logHandler.sendMessage(messagelog);

        final byte address = (byte) tar_door;
        final String idCode = type;
        String crcValue = null;
        final String filePath = path;
        String manufactuer = null;

        if (!TextUtils.isEmpty(path)) {
            int startPos = path.lastIndexOf("/");
            int lastPos = path.lastIndexOf(".");
            if (startPos != -1 && lastPos != -1) {
                path = path.substring(startPos + 1, lastPos);
            }
            String[] paths = path.split("_");
            if (paths != null && paths.length == 4) {
                crcValue = paths[3];
                manufactuer = paths[0];
            }
        }

        BatteryUpgradeProgram batteryUpgradeProgram = null;
        if (!TextUtils.isEmpty(manufactuer) && manufactuer.length() >= 2) {
            batteryUpgradeProgram = new NuoWanBatteryUpgradeProgram(new BatteryAttribute(address, idCode, crcValue, filePath));
        }

        //没有匹配到对应的电池厂商
        if (batteryUpgradeProgram == null) {
            showError(address, (byte) -1, "没有匹配到对应的电池厂商");
            return;
        }

        initListener();

        batteryUpgradeProgram.setOnBatteryUpgradeCallBack(new BatteryUpgradeProgram.OnBatteryUpgradeCallBack() {
            @Override
            public void onUpgrading(byte address, byte statusFlag, String statusInfo, long currentPregress, long totalPregress) {
                if (statusFlag != BatteryUpgradeProgram.UpgradeStatus.SUCCESSED) {
                    System.out.println("UpDataBattery：   升级中 - " + address + "   " + currentPregress + "   " + "    " + totalPregress);

                    Message messagelog = new Message();
                    Bundle bundlelog = new Bundle();
                    bundlelog.putString("log", "升级中 ： 发送帧 - " + currentPregress + "   总帧 - " + totalPregress);
                    messagelog.setData(bundlelog);
                    logHandler.sendMessage(messagelog);

                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putInt("process", (int) currentPregress);
                    bundle.putInt("total", (int) totalPregress);
                    message.setData(bundle);
                    barHandler.sendMessage(message);

                    A_Main2.AN_IS_RUN = 1;
                } else {
                    showSuccessed(address, statusFlag, statusInfo);
                }
            }

            @Override
            public void onError(byte address, byte statusFlag, String statusInfo) {
                System.out.println("UpDataBattery：   错误 - " + statusInfo);
                showError(address, statusFlag, statusInfo);
            }
        });
        TaskDispatcher.getInstance().dispatch(DeviceIoType.CANBUS, batteryUpgradeProgram);
    }

    private void findView() {
        p_bar = this.findViewById(R.id.p_bar);
        title = this.findViewById(R.id.title);
        logView = this.findViewById(R.id.log);
        cabid = this.findViewById(R.id.cabid);
        tel = this.findViewById(R.id.tel);
        time = this.findViewById(R.id.time);
    }

    private void handler() {

        barHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                int process = msg.getData().getInt("process");
                int total = msg.getData().getInt("total");

                float b = (float) process / total * 100;

                p_bar.setProgress((int) b);
            }
        };

        //显示提示框
        showProgressDialogHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                String msg_str = msg.getData().getString("msg");
                String time_str = msg.getData().getString("time");


                if (activity != null) {
                    progressDialog_2.show(msg_str, Integer.parseInt(time_str));
                }

            }
        };

        logHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);


                String log = msg.getData().getString("log");
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                String sub_log = logView.getText().toString();
                if (sub_log.length() > 6000) {
                    int a = sub_log.length() - 6000;
                    sub_log = sub_log.substring(a, sub_log.length());
                }

                logView.setText(sub_log + "\n" + df.format(new Date()) + "    " + log);
                logView.setSelection(logView.getText().length());
            }
        };

        setTimeUIHndler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                int timeCount = msg.getData().getInt("time");
                time.setText(timeCount + " S");
            }
        };

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getInstance().deletListener(canSerialDataReceiver);
        timeState = 0;

        if (progressDialog_2 != null) {
            progressDialog_2.onDestory();
        }
        A_Main2.upgrading.clear();
    }

    private void showError(byte address, byte statusFlag, String statusInfo) {
        Message messagelog = new Message();
        Bundle bundlelog = new Bundle();
        bundlelog.putString("log", "升级失败 - " + statusInfo);
        messagelog.setData(bundlelog);
        logHandler.sendMessage(messagelog);


        Message message_1 = new Message();
        Bundle bundle_1 = new Bundle();
        bundle_1.putString("msg", "电池升级出错，程序将在10秒后返回！");
        bundle_1.putString("time", 10 + "");
        message_1.setData(bundle_1);
        showProgressDialogHandler.sendMessage(message_1);

        Thread thread1 = new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    sleep(15000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                A_Main2.AN_IS_RUN = 0;
                A_Main2.BAR_IS_RUN = 0;
                activity.finish();
            }
        };
        thread1.start();
    }

    private void showSuccessed(byte address, byte statusFlag, String statusInfo) {
        Message messagelog = new Message();
        Bundle bundlelog = new Bundle();
        bundlelog.putString("log", "升级成功");
        messagelog.setData(bundlelog);
        logHandler.sendMessage(messagelog);

        Message message_1 = new Message();
        Bundle bundle_1 = new Bundle();
        bundle_1.putString("msg", "电池升级成功，程序将在10秒后返回！");
        bundle_1.putString("time", 10 + "");
        message_1.setData(bundle_1);
        showProgressDialogHandler.sendMessage(message_1);


        Thread thread1 = new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    sleep(15000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                A_Main2.AN_IS_RUN = 0;
                A_Main2.BAR_IS_RUN = 0;
                activity.finish();

            }
        };
        thread1.start();
    }
}
