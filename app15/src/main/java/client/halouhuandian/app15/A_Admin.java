package client.halouhuandian.app15;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.util.Consumer;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hellohuandian.pubfunction.ProgressDialog.ProgressDialog;
import com.hellohuandian.pubfunction.ProgressDialog.ProgressDialog_3;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import client.halouhuandian.app15.devicesController.currentDetectionBoard.CurrentDetectionController;
import client.halouhuandian.app15.devicesController.currentDetectionBoard.CurrentDetectionModel;
import client.halouhuandian.app15.devicesController.rod.BatteryDataModel;
import client.halouhuandian.app15.devicesController.rod.DoorController;
import client.halouhuandian.app15.devicesController.sensor.SensorController;
import client.halouhuandian.app15.devicesController.sensor.SensorDataBean;
import client.halouhuandian.app15.devicesController.switcher.AirFanSpeedSwitcher;
import client.halouhuandian.app15.devicesController.switcher.DeviceSwitchController;
import client.halouhuandian.app15.devicesController.switcher.DeviceSwitcher;
import client.halouhuandian.app15.pub.dcList.AcdcList;
import client.halouhuandian.app15.pub.dcList.DcDownloadDialog;
import client.halouhuandian.app15.pub.dcList.DcList;
import client.halouhuandian.app15.pub.dcList.DcdcList;
import client.halouhuandian.app15.pub.dcList.HttpOptAcdcList;
import client.halouhuandian.app15.pub.dcList.HttpOptDcdcList;
import client.halouhuandian.app15.setting.CurrentDetectionSettingDialog;
import client.halouhuandian.app15.setting.PushRodSettingDialog;
import client.halouhuandian.app15.sp.CabInfoSp;


/**
 * Created by apple on 2017/12/2.
 * 舱门具体信息，控制，配置页面
 */
public class A_Admin extends Activity implements View.OnClickListener {
    private Handler updateHandler = new Handler() {
        private StringBuilder stringBuilder = new StringBuilder();

        @Override
        public void handleMessage(Message msg) {
            tv_envBoard.setText("环境板");
            stringBuilder.setLength(0);
            tv_envInfo.setText(stringBuilder.append(SensorController.getInstance().getSensorDataBean().getWaterLevel_String())
                    .append(System.lineSeparator())
                    .append(SensorController.getInstance().getSensorDataBean().getWaterLevel2_String())
                    .append(System.lineSeparator())
                    .append(SensorController.getInstance().getSensorDataBean().getSmoke_String())
                    .append(System.lineSeparator())
                    .append(SensorController.getInstance().getSensorDataBean().getTemperature1_String())
                    .append(System.lineSeparator())
                    .append(SensorController.getInstance().getSensorDataBean().getTemperature3_String())
                    .append(System.lineSeparator())
                    .append("SV:").append(SensorController.getInstance().getSensorDataBean().getSoftwareVersion()).append("  HV:").append(Integer.toHexString(SensorController.getInstance().getSensorDataBean().getHardwareVersion()))
                    .append(System.lineSeparator())
                    .append(((SensorController.getInstance().getSensorDataBean().getAirFan1Status() == 0 ? 1 : 0)
                            + (SensorController.getInstance().getSensorDataBean().getAirFan2Status() == 0 ? 1 : 0))));

            updateHandler.sendEmptyMessageDelayed(0, 2000);

            if (cabInfoSp!=null)
            {
                switch (cabInfoSp.optHeatMode()) {
                    case "1"://加热
                        tv_heatMode.setText("加热");
                        break;
                    case "2"://自动
                        tv_heatMode.setText("自动");
                        break;
                    case "-1"://停止
                        tv_heatMode.setText("不加热");
                        break;
                }
            }
        }
    };

    private Activity activity;
    private TextView deviceNumber, write_uid, retuen_page, finish;
    private TextView item_5_text_buttom, item_7_text, item_9_text, tv_pushrodActSetTime;
    private TextView tv_heatMode;
    private ImageView thread_protection;
    //退出参数
    private static Boolean isExit = false;

    //电柜舱门数
    private int CABINET_COUNT = 9;

    private GridView bar_info_girdview, cha_info_girdview;
    private List<Map<String, String>> listData = new ArrayList<>();
    private List<Map<String, String>> gridData_2 = new ArrayList<>();
    private MyAdapter_1 gridSimpleAdapter_1;
    private MyAdapter_2 gridSimpleAdapter_2;

    //线程保护参数
    private String thread_protection_type = "-1";
    //loading框
    private ProgressDialog progressDialog;
    private ProgressDialog_3 progressDialog_3;
    //返回handler
    public static Handler updataHandler, showprogressDialogHandler, disprogressDialogHandler;

    //电柜信息
    private CabInfoSp cabInfoSp;

    //更新柜子后台数据
    private int type = 0;
    private Thread thread = new Thread() {
        @Override
        public void run() {
            super.run();
            while (type == 0) {
                try {
                    sleep(2000);
                    updataHandler.sendMessage(new Message());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private RadioGroup rg1To5AirFanControl;
    private RadioGroup rg6To10AirFanControl;

    private TextView tvDetectionBoardInfo, tv_envInfo, tv_envBoard;

    private DcDownloadDialog dcDownloadDialog;
    private float currentThreshold;
    private CurrentDetectionSettingDialog currentDetectionSettingDialog;
    private PushRodSettingDialog pushRodSettingDialog;

    public static final String[] stopCodeStr = new String[]{"无", "充电模块故障终止", "安卓版离线故障终止", "充电模块开启失败故障终止", "充电模块和BMS通讯失败故障终止", "整包电池电压过高告警终止", "整包电池电压过低告警终止", "电池反接保护终止", "电池仓NTC掉线加热故障终止",
            "充电模块ID重复故障终止", "充电继电器黏连告警终止", "充电继电器驱动失效告警终止", "BMS过压终止", "BMS欠压终止", "BMS充电过流终止", "BMS放电过流终止", "BMS短路终止", "BMS过高温终止", "BMS过低温终止", "BMS充电握手失败终止",
            "BMS MOS击穿终止", "BMS电池温度异常终止", "BMS电池反充终止", "BMS保险丝断开终止", "电池包故障终止", "安卓版下发关机终止", "无电池终止", "锁微动异常检测终止", "达到加热目标终止", "加热继电器黏连告警终止", "加热继电器驱动失效告警终止", "充电回路异常断开告警终止",
            "电池BMS数据异常告警终止", "侧微动异常检测告警", "达到SOC目标终止", "推仓门终止", "激活失败终止", "电池串数错误终止", "DCDC禁用终止", "ACDC全部离线终止", "拔电池终止", "加热电压异常终止", "输入继电器异常终止", "加热保护板异常终止", "模块连续开启失败终止", "加热禁用终止", "充电低温故障终止", "电池升级终止", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""
            , "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;
        setContentView(R.layout.admin_720p);
        cabInfoSp = new CabInfoSp(activity);
        currentThreshold = cabInfoSp.optCurrentThreshold();
        MyApplication.getInstance().addActivity(this);

        findById();
        handler();
        init();
    }

    private void findById() {
        rg1To5AirFanControl = findViewById(R.id.rg_1To5AirFanControl);
        rg6To10AirFanControl = findViewById(R.id.rg_6To10AirFanControl);

        //退出和返回
        retuen_page = (TextView) findViewById(R.id.retuen_page);
        retuen_page.setOnClickListener(this);
        finish = (TextView) findViewById(R.id.finish);
        finish.setOnClickListener(this);
        //左上的gridview
        bar_info_girdview = (GridView) this.findViewById(R.id.bar_info_girdview);
        cha_info_girdview = (GridView) this.findViewById(R.id.cha_info_girdview);
        //电柜参数
        item_7_text = (TextView) this.findViewById(R.id.item_7_text);
        //线程保护
        thread_protection = (ImageView) this.findViewById(R.id.thread_protection);
        thread_protection.setOnClickListener(this);
        //语音测试
        item_5_text_buttom = (TextView) this.findViewById(R.id.item_5_text_buttom);
        item_5_text_buttom.setOnClickListener(this);

        item_9_text = (TextView) this.findViewById(R.id.item_9_text);

        tv_pushrodActSetTime = (TextView) this.findViewById(R.id.tv_pushrodActSetTime);
        tv_pushrodActSetTime.setText((A_Main2.pushrodActSetTime / 10f) + "秒");

        tv_heatMode = findViewById(R.id.tv_heatMode);
        switch (cabInfoSp.optHeatMode()) {
            case "1"://加热
                tv_heatMode.setText("加热");
                break;
            case "2"://自动
                tv_heatMode.setText("自动");
                break;
            case "-1"://停止
                tv_heatMode.setText("不加热");
                break;
        }

        thread_protection_type = cabInfoSp.getTPTNumber();
        if (thread_protection_type.equals("1")) {
            thread_protection.setImageResource(R.drawable.image_6);
        } else if (thread_protection_type.equals("0")) {
            thread_protection.setImageResource(R.drawable.image_7);
        }

        write_uid = this.findViewById(R.id.write_uid);
        deviceNumber = this.findViewById(R.id.deviceNumber);
        write_uid.setOnClickListener(this);

        if (!TextUtils.isEmpty(A_Main2.cabid_title)) {
            deviceNumber.setText(A_Main2.cabid_title);
        }

        findViewById(R.id.tv_controlAndroid12vOpen).setOnClickListener(this);
        findViewById(R.id.tv_controlAndroid12vReboot).setOnClickListener(this);
        findViewById(R.id.tv_controlAirFan12vOpen).setOnClickListener(this);
        findViewById(R.id.tv_controlAirFan12vClose).setOnClickListener(this);
        findViewById(R.id.tv_control12v1Open).setOnClickListener(this);
        findViewById(R.id.tv_control12v1Close).setOnClickListener(this);
        findViewById(R.id.tv_control12v2Open).setOnClickListener(this);
        findViewById(R.id.tv_control12v2Close).setOnClickListener(this);
        findViewById(R.id.tv_clearAmmeterData).setOnClickListener(this);
        findViewById(R.id.btn_currentThreshold).setOnClickListener(this);

        findViewById(R.id.tv_airFan1Open).setOnClickListener(this);
        findViewById(R.id.tv_airFan1Close).setOnClickListener(this);
        findViewById(R.id.tv_airFan2Open).setOnClickListener(this);
        findViewById(R.id.tv_airFan2Close).setOnClickListener(this);
        findViewById(R.id.tv_upgradeDcdc).setOnClickListener(this);
        findViewById(R.id.tv_upgradeAcdc).setOnClickListener(this);
        findViewById(R.id.tv_openAllDoors).setOnClickListener(this);
        findViewById(R.id.tv_closeAllDoors).setOnClickListener(this);
        findViewById(R.id.btn_setPushRodTime).setOnClickListener(this);

        tv_envInfo = findViewById(R.id.tv_envInfo);
        tvDetectionBoardInfo = findViewById(R.id.tv_detectionBoardInfo);
        tv_envBoard = findViewById(R.id.tv_envBoard);
        tv_envBoard.setText("环境板：未检测到");

        updateHandler.sendEmptyMessage(0);

        if (BuildConfig.DEBUG) {
            // TODO: 2020/7/20 测试显示按钮，正式不处理，默认隐藏
            findViewById(R.id.controlLl).setVisibility(View.VISIBLE);
            findViewById(R.id.tv_clearAmmeterData).setVisibility(View.VISIBLE);
            write_uid.setVisibility(View.VISIBLE);
        }

        CurrentDetectionModel currentDetectionModel = CurrentDetectionController.getInstance().optCurrentDetectionModel();
        if (currentDetectionModel != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.setLength(0);
            tvDetectionBoardInfo.setText(stringBuilder.append(currentDetectionModel.status_String)
                    .append(System.lineSeparator())
                    .append(currentDetectionModel.outVoltage_String)
                    .append(System.lineSeparator())
                    .append(currentDetectionModel.outCurrent_String)
                    .append(System.lineSeparator())
                    .append(currentDetectionModel.outWarning_String)
                    .append(System.lineSeparator())
                    .append("SV:")
                    .append(currentDetectionModel.getSoftwareVersion())
                    .append("/")
                    .append("HV:")
                    .append(currentDetectionModel.getHardwareVersion())
                    .append(System.lineSeparator())
                    .append(currentDetectionModel.isExistDevice() ? currentThreshold + "A" : "").toString());
        }

        CurrentDetectionController.getInstance().setConsumer(new Consumer<CurrentDetectionModel>() {
            private StringBuilder stringBuilder = new StringBuilder();

            @Override
            public void accept(final CurrentDetectionModel currentDetectionModel) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        stringBuilder.setLength(0);
                        tvDetectionBoardInfo.setText(stringBuilder.append(currentDetectionModel.status_String)
                                .append(System.lineSeparator())
                                .append(currentDetectionModel.outVoltage_String)
                                .append(System.lineSeparator())
                                .append(currentDetectionModel.outCurrent_String)
                                .append(System.lineSeparator())
                                .append(currentDetectionModel.outWarning_String)
                                .append(System.lineSeparator())
                                .append("SV:")
                                .append(currentDetectionModel.getSoftwareVersion())
                                .append("/")
                                .append("HV:")
                                .append(currentDetectionModel.getHardwareVersion())
                                .append(System.lineSeparator())
                                .append(currentThreshold + "A")
                                .append(System.lineSeparator())
                                .append(cabInfoSp.optAutoSetCurrentDetectionStatus() ? "自动" : "手动").toString());
                    }
                });
            }
        });
    }

    private void handler() {

        updataHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                listData.clear();
                for (int i = 0; i < CABINET_COUNT; i++) {

                    Map<String, String> map = new HashMap<String, String>();
                    map.put("title", (i + 1) + "");
                    map.put("dianliang", A_Main2.PERCENtAGES[i] + "");
                    map.put("dianya", A_Main2.DIANYA[i] + "");
                    map.put("dianliu", A_Main2.DIANLIU[i] + "");
                    map.put("wendu", A_Main2.TEM_2[i] + "C");
                    map.put("xunhuancichu", A_Main2.LOOPS[i] + "次");
                    map.put("bid", A_Main2.BIDS[i]);
                    map.put("barVer", A_Main2.barVer[i] + "");
                    map.put("tem_1", A_Main2.TEM_1[i] + "");
                    map.put("tem_2", A_Main2.TEM_2[i] + "");
                    map.put("loops", A_Main2.LOOPS[i] + "");
                    map.put("uid", A_Main2.UIDS[i] + "");

                    map.put("DCDC_state", A_Main2.DCDC_state[i] + "");
                    map.put("DCDC_dianya", A_Main2.DCDC_dianya[i] + "");
                    map.put("DCDC_dianliu", A_Main2.DCDC_dianliu[i] + "");
                    map.put("DCDC_stop", A_Main2.DCDC_stop[i] + "");
                    map.put("DCDC_SV", A_Main2.DCDC_SV[i] + "");
                    map.put("DCDC_HV", A_Main2.DCDC_HV[i] + "");

                    map.put("ERROR_state_01", A_Main2.ERROR_state_01[i] + "");
                    map.put("ERROR_state_02", A_Main2.ERROR_state_02[i] + "");
                    map.put("ERROR_state_03", A_Main2.ERROR_state_03[i] + "");

                    map.put("item_max", A_Main2.item_max[i] + "");
                    map.put("item_min", A_Main2.item_min[i] + "");
                    map.put("demandPower", A_Main2.demandPower[i] + "");
                    map.put("samplingV", A_Main2.samplingVs[i]);
                    map.put("realSoc", A_Main2.realSocVs[i] + "");

                    String door_state = A_Main2.DOORS[i] + "";
                    if (door_state != null) {
                        if (door_state.equals("1")) {
                            door_state = "有电池";
                        } else if (door_state.equals("0")) {
                            door_state = "没电池";
                        }
                    }
                    String push_state = A_Main2.PUSHS[i] + "";
                    if (push_state != null) {
                        if (push_state.equals("1")) {
                            push_state = "仓打开";
                        } else if (push_state.equals("0")) {
                            push_state = "停止";
                        } else if (push_state.equals("2")) {
                            push_state = "仓关闭";
                        } else if (push_state.equals("3")) {
                            push_state = "故障";
                        } else if (push_state.equals("-1")) {
                            push_state = "未检测";
                        }
                    }

                    String small_state = A_Main2.SMALLS[i] + "";
                    if (small_state != null) {
                        if (small_state.equals("1")) {
                            small_state = "仓关闭";
                        } else if (small_state.equals("0")) {
                            small_state = "仓打开";
                        }
                    }
                    map.put("door", door_state);
                    map.put("small", small_state);
                    map.put("push", push_state);

                    listData.add(map);
                }
                gridSimpleAdapter_1.notifyDataSetChanged();

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
                Date date = new Date(System.currentTimeMillis());
                item_9_text.setText(simpleDateFormat.format(date));

                gridData_2.clear();
                for (int i = 0; i < 2; i++) {
                    Map<String, String> map = new HashMap<>();
                    map.put("state", "开");
                    map.put("conn", "正常");
                    map.put("max_charge_A", "16");
                    map.put("max_charge_V", "54.6/65.5");
                    map.put("ERROR_state_01", A_Main2.ERROR_state_01[i + 9] + "");
                    map.put("ACDC_dianya_in", A_Main2.ACDC_dianya_in[i] + "");
                    map.put("ACDC_dianya_out", A_Main2.ACDC_dianya_out[i] + "");
                    map.put("ACDC_dianliu_in", A_Main2.ACDC_dianliu_in[i] + "");
                    map.put("ACDC_gonglv", A_Main2.ACDC_gonglv[i] + "");
                    map.put("ACDC_SV", A_Main2.ACDC_SV[i] + "");
                    map.put("ACDC_HV", A_Main2.ACDC_HV[i] + "");
                    map.put("whichACDC", A_Main2.whichACDC[i] + "");
                    map.put("remainingTotalPower", A_Main2.remainingTotalPower[i] + "");
                    map.put("sleepStatus", A_Main2.sleepStatus[i] + "");
                    gridData_2.add(map);
                }
                gridSimpleAdapter_2 = new MyAdapter_2(activity, gridData_2, R.layout.admin_720p_grid_item_2);
                cha_info_girdview.setAdapter(gridSimpleAdapter_2);
            }
        };

        showprogressDialogHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                progressDialog.show();

                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (activity != null) {
                            disprogressDialogHandler.sendMessage(new Message());
                        }
                    }
                };
                thread.start();

            }
        };

        disprogressDialogHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                progressDialog.dismiss();
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CurrentDetectionController.getInstance().setConsumer(null);
        progressDialog.dismiss();
//        FireSwitchController.getInstance().control(0);
        updateHandler.removeCallbacksAndMessages(null);
    }

    private void init() {

        //电柜参数
        TelephonyManager mTelephonyMgr = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String imsi = mTelephonyMgr.getSubscriberId();
        @SuppressLint("MissingPermission") String phone_code = mTelephonyMgr.getLine1Number();
        item_7_text.setText(imsi);

        //loading框
        progressDialog = new ProgressDialog(activity);
        progressDialog_3 = new ProgressDialog_3(activity);

        setData_top();
        setData_bottom();

        if (!thread.isAlive()) {
            thread.start();
        }

        rg1To5AirFanControl.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = group.findViewById(checkedId);
                if (radioButton != null && radioButton.isChecked()) {
                    if (rg6To10AirFanControl.getCheckedRadioButtonId() != -1) {
                        rg6To10AirFanControl.clearCheck();
                    }
                    controlAirFanSpeed(checkedId);
                }
            }
        });
        rg6To10AirFanControl.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = group.findViewById(checkedId);
                if (radioButton != null && radioButton.isChecked()) {
                    if (rg1To5AirFanControl.getCheckedRadioButtonId() != -1) {
                        rg1To5AirFanControl.clearCheck();
                    }
                    controlAirFanSpeed(checkedId);
                }
            }
        });
    }


    private void setData_top() {
        for (int i = 0; i < CABINET_COUNT; i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("title", (i + 1) + "");
            map.put("dianliang", A_Main2.PERCENtAGES[i] + "");
            map.put("dianya", A_Main2.DIANYA[i] + "");
            map.put("dianliu", A_Main2.DIANLIU[i] + "");
            map.put("wendu", A_Main2.TEM_2[i] + "C");
            map.put("xunhuancichu", A_Main2.LOOPS[i] + "次");
            map.put("bid", A_Main2.BIDS[i]);
            map.put("barVer", A_Main2.barVer[i] + "");
            map.put("tem_1", A_Main2.TEM_1[i] + "");
            map.put("tem_2", A_Main2.TEM_2[i] + "");
            map.put("loops", A_Main2.LOOPS[i] + "");
            map.put("uid", A_Main2.UIDS[i] + "");

            map.put("DCDC_state", A_Main2.DCDC_state[i] + "");
            map.put("DCDC_dianya", A_Main2.DCDC_dianya[i] + "");
            map.put("DCDC_dianliu", A_Main2.DCDC_dianliu[i] + "");
            map.put("DCDC_stop", A_Main2.DCDC_stop[i] + "");
            map.put("DCDC_SV", A_Main2.DCDC_SV[i] + "");
            map.put("DCDC_HV", A_Main2.DCDC_HV[i] + "");

            map.put("ERROR_state_01", A_Main2.ERROR_state_01[i] + "");
            map.put("ERROR_state_02", A_Main2.ERROR_state_02[i] + "");
            map.put("ERROR_state_03", A_Main2.ERROR_state_03[i] + "");

            map.put("item_max", A_Main2.item_max[i] + "");
            map.put("item_min", A_Main2.item_min[i] + "");
            map.put("demandPower", A_Main2.demandPower[i] + "");
            map.put("realSoc", A_Main2.realSocVs[i] + "");

            String door_state = A_Main2.DOORS[i] + "";
            if (door_state != null) {
                if (door_state.equals("1")) {
                    door_state = "有电池";
                } else if (door_state.equals("0")) {
                    door_state = "没电池";
                }
            }

            String push_state = A_Main2.PUSHS[i] + "";
            if (push_state != null) {
                if (push_state.equals("1")) {
                    push_state = "收缩";
                } else if (push_state.equals("0")) {
                    push_state = "停止";
                } else if (push_state.equals("2")) {
                    push_state = "伸直";
                } else if (push_state.equals("3")) {
                    push_state = "故障";
                } else if (push_state.equals("-1")) {
                    push_state = "未检测";
                }
            }

            String small_state = A_Main2.SMALLS[i] + "";
            if (small_state != null) {
                if (small_state.equals("1")) {
                    small_state = "仓关闭";
                } else if (small_state.equals("0")) {
                    small_state = "仓打开";
                }
            }
            map.put("door", door_state);
            map.put("small", small_state);
            map.put("push", push_state);
            map.put("samplingV", A_Main2.samplingVs[i]);
            map.put("realSoc", A_Main2.realSocVs[i] + "");
            listData.add(map);
        }

        gridSimpleAdapter_1 = new MyAdapter_1(activity, listData, R.layout.admin_720p_grid_item_1);
        bar_info_girdview.setAdapter(gridSimpleAdapter_1);

    }


    private void setData_bottom() {
        gridData_2.clear();
        for (int i = 0; i < 3; i++) {
            Map<String, String> map = new HashMap<>();
            map.put("state", "开");
            map.put("conn", "正常");
            map.put("max_charge_A", "16");
            map.put("max_charge_V", "54.6");
            map.put("ERROR_state_01", A_Main2.ERROR_state_01[i + 9] + "");
            map.put("ACDC_dianya_in", A_Main2.ACDC_dianya_in[i] + "");
            map.put("ACDC_dianya_out", A_Main2.ACDC_dianya_out[i] + "");
            map.put("ACDC_dianliu_in", A_Main2.ACDC_dianliu_in[i] + "");
            map.put("ACDC_gonglv", A_Main2.ACDC_gonglv[i] + "");
            map.put("ACDC_SV", A_Main2.ACDC_SV[i] + "");
            map.put("ACDC_HV", A_Main2.ACDC_HV[i] + "");
            map.put("whichACDC", A_Main2.whichACDC[i] + "");
            map.put("remainingTotalPower", A_Main2.remainingTotalPower[i] + "");
            map.put("sleepStatus", A_Main2.sleepStatus[i] + "");
            gridData_2.add(map);
        }
        gridSimpleAdapter_2 = new MyAdapter_2(activity, gridData_2, R.layout.admin_720p_grid_item_2);
        cha_info_girdview.setAdapter(gridSimpleAdapter_2);
    }


    @Override
    public void onClick(View view) {

        if (R.id.tv_airFan1Open == view.getId()) {
            DeviceSwitchController.getInstance().controlAirFan(DeviceSwitcher.AIR_FAN._1_OPEN);
            SensorController.getInstance().getSensorDataBean().stopContinueControl();
        } else if (R.id.tv_airFan1Close == view.getId()) {
            DeviceSwitchController.getInstance().controlAirFan(DeviceSwitcher.AIR_FAN._1_CLOSE);
            SensorController.getInstance().getSensorDataBean().stopContinueControl();
        } else if (R.id.tv_airFan2Open == view.getId()) {
            DeviceSwitchController.getInstance().controlAirFan(DeviceSwitcher.AIR_FAN._2_OPEN);
            SensorController.getInstance().getSensorDataBean().stopContinueControl();
        } else if (R.id.tv_airFan2Close == view.getId()) {
            DeviceSwitchController.getInstance().controlAirFan(DeviceSwitcher.AIR_FAN._2_CLOSE);
            SensorController.getInstance().getSensorDataBean().stopContinueControl();
        } else if (finish.getId() == view.getId()) {
            Timer tExit = null;
            if (isExit == false) {
                isExit = true; // 准备退出
                Toast.makeText(activity, "再按一次退出应用！", Toast.LENGTH_LONG).show();
                tExit = new Timer();
                tExit.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        isExit = false;// 取消退出
                    }
                }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
            } else {
                MyApplication.getInstance().exit();
            }
        } else if (view.getId() == retuen_page.getId()) {
            this.finish();
            listData.clear();
            System.gc();
        } else if (thread_protection.getId() == view.getId()) {
            thread_protection_type = cabInfoSp.getTPTNumber();
            if (thread_protection_type.equals("1")) {
                thread_protection.setImageResource(R.drawable.image_7);
                cabInfoSp.setTPTNumber("0");
            } else if (thread_protection_type.equals("0")) {
                thread_protection.setImageResource(R.drawable.image_6);
                cabInfoSp.setTPTNumber("1");
            }
        } else if (view.getId() == item_5_text_buttom.getId()) {

            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("msg", "正在进行语音测试！");
            message.setData(bundle);
            A_Main2.speakHandler.sendMessage(message);
        } else if (view.getId() == write_uid.getId()) {

            LayoutInflater inflater = LayoutInflater.from(activity);
            final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            final AlertDialog mAlertDialog = builder.create();
            View view_1 = inflater.inflate(R.layout.admin_item_5_dialog, null);

            final EditText door = (EditText) view_1.findViewById(R.id.door);
            final EditText uid = (EditText) view_1.findViewById(R.id.uid);
            TextView enter = (TextView) view_1.findViewById(R.id.enter);
            enter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String door_str = door.getText().toString();
                    String uid_str = uid.getText().toString();
                    try {

                        int door_int = Integer.parseInt(door_str);
                        if (door_int < 10 && door_int > 0) {
                            if (uid_str.length() == 8) {
                                A_Main2.writeBatteryCheckCode(uid_str, door_int);

                                mAlertDialog.dismiss();

                            } else {
                                progressDialog_3.show("输入格式不对，请重新输入", 5);
                                mAlertDialog.dismiss();
                            }
                        } else {
                            progressDialog_3.show("输入格式不对，请重新输入", 5);
                            mAlertDialog.dismiss();
                        }

                    } catch (Exception e) {
                        progressDialog_3.show("输入格式不对，请重新输入", 5);
                        mAlertDialog.dismiss();
                    }
                }
            });
            mAlertDialog.show();
            mAlertDialog.getWindow().setContentView(view_1);
            mAlertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        } else if (view.getId() == R.id.btn_currentThreshold) {
            showCurrentDetectionSettingDialog();
        } else {
            View v = findViewById(view.getId());
            if (v instanceof TextView) {
                Toast.makeText(this, ((TextView) v).getText(), Toast.LENGTH_SHORT).show();
            }
            switch (view.getId()) {
                case R.id.tv_controlAndroid12vOpen:
                    control(DeviceSwitcher.CMD.ANDROID_12V_OPEN);
                    break;
                case R.id.tv_controlAndroid12vReboot:
                    control(DeviceSwitcher.CMD.ANDROID_12V_REBOOT);
                    break;
                case R.id.tv_controlAirFan12vOpen:
                    control(DeviceSwitcher.CMD.AIRFAN_12V_OPEN);
                    break;
                case R.id.tv_controlAirFan12vClose:
                    control(DeviceSwitcher.CMD.AIRFAN_12V_CLOSE);
                    break;
                case R.id.tv_control12v1Open:
                    control(DeviceSwitcher.CMD.V12_1_OPEN);
                    break;
                case R.id.tv_control12v1Close:
                    control(DeviceSwitcher.CMD.V12_1_CLOSE);
                    break;
                case R.id.tv_control12v2Open:
                    control(DeviceSwitcher.CMD.V12_2_OPEN);
                    break;
                case R.id.tv_control12v2Close:
                    control(DeviceSwitcher.CMD.V12_2_CLOSE);
                    break;
                case R.id.tv_clearAmmeterData:
                    control(DeviceSwitcher.CMD.CLEAR_AMMETER_DATA);
                    break;
                case R.id.tv_upgradeDcdc:
                    upgradeDcdc();
                    break;
                case R.id.tv_upgradeAcdc:
                    upgradeAcdc();
                    break;
                case R.id.tv_openAllDoors:
                    Message message = A_Main2.speakHandler.obtainMessage(0);
                    Bundle bundle = new Bundle();
                    message.setData(bundle);
                    bundle.putString("msg", "准备打开所有仓门请注意安全");
                    A_Main2.speakHandler.sendMessage(message);

                    break;
                case R.id.tv_closeAllDoors:
                    break;
                case R.id.btn_setPushRodTime:
                    //设置推杆时间
                    showPushRodSettingDialog();
                    break;

            }
        }
    }

    /*
     * 环境板开关控制
     *
     * @param cmd
     */
    private void control(@DeviceSwitcher.CMD int cmd) {
        DeviceSwitchController.getInstance().control(cmd);
    }

    /**
     * 控制风扇转速
     *
     * @param cmd
     */
    private void controlAirFanSpeed(@AirFanSpeedSwitcher.SPEED int cmd) {
        DeviceSwitchController.getInstance().controlAirFanSpeed(cmd);
    }

    private boolean isUpgrade;

    private void upgradeDcdc() {
        if (!isUpgrade) {
            isUpgrade = true;
            new HttpOptDcdcList(A_Main2.cabid_title, new Consumer<String>() {
                private Gson gson = new Gson();

                @Override
                public void accept(String data) {
                    if (!TextUtils.isEmpty(data)) {
                        DcdcList dcdcList = gson.fromJson(data, DcdcList.class);
                        if (dcdcList != null && dcdcList.getStatus() == 1) {
                            ArrayList<DcdcList.DataBean> dcListList = dcdcList.getData();
                            if (dcListList != null && !dcListList.isEmpty()) {
                                final ArrayList<DcList> dcLists = new ArrayList<>();
                                dcLists.addAll(dcListList);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showDcDownloadDialog(dcLists);
                                        isUpgrade = false;
                                    }
                                });
                                return;
                            }
                        }
                    }
                    isUpgrade = false;
                }
            }).start();
        }
    }

    private void upgradeAcdc() {
        if (!isUpgrade) {
            isUpgrade = true;
            new HttpOptAcdcList(A_Main2.cabid_title, new Consumer<String>() {
                private Gson gson = new Gson();

                @Override
                public void accept(String data) {
                    AcdcList acdcList = gson.fromJson(data, AcdcList.class);
                    if (acdcList != null && acdcList.getStatus() == 1) {
                        ArrayList<AcdcList.DataBean> dataBeans = acdcList.getData();
                        if (dataBeans != null && !dataBeans.isEmpty()) {
                            final ArrayList<DcList> dcLists = new ArrayList<>();
                            dcLists.addAll(dataBeans);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showDcDownloadDialog(dcLists);
                                    isUpgrade = false;
                                }
                            });
                            return;
                        }
                    }
                    isUpgrade = false;
                }
            }).start();
        }
    }

    private void showDcDownloadDialog(List<DcList> dcListList) {
        if (!isFinishing() && dcListList != null && !dcListList.isEmpty()) {
            if (dcDownloadDialog == null) {
                dcDownloadDialog = new DcDownloadDialog(this);
            }
            dcDownloadDialog.update(dcListList);
            if (!dcDownloadDialog.isShowing()) {
                dcDownloadDialog.show();
            }
        }
    }

    private void showCurrentDetectionSettingDialog() {
        if (currentDetectionSettingDialog == null) {
            currentDetectionSettingDialog = new CurrentDetectionSettingDialog(this);
            currentDetectionSettingDialog.setCurrentDetectionConsumer(new Consumer<Float>() {
                @Override
                public void accept(Float aFloat) {
                    if (cabInfoSp != null) {
                        currentThreshold = aFloat;
                        cabInfoSp.setCurrentThreshold(aFloat);
                        CurrentDetectionController.getInstance().enabledCurrentDetection(aFloat, 800, 1000);
                        cabInfoSp.setAutoSetCurrentDetectionStatus(false);
                    }
                }
            });

            currentDetectionSettingDialog.setCancleAutoControlConsumer(new Consumer() {

                @Override
                public void accept(Object o) {
                    if (cabInfoSp != null) {
                        SensorDataBean sensorDataBean = SensorController.getInstance().getSensorDataBean();
                        if (sensorDataBean != null) {
                            int temperature = 0;
                            float innerTemperature = sensorDataBean.getTemperature1();
                            if (innerTemperature >= -40) {
                                temperature = (int) innerTemperature;
                                CurrentDetectionController.getInstance().setCurrentDetection(temperature);
                            } else {
                                float envTemperature = sensorDataBean.getTemperature3();
                                if (envTemperature >= -40) {
                                    temperature = (int) envTemperature;
                                    CurrentDetectionController.getInstance().setCurrentDetection(temperature);
                                }
                            }
                            currentThreshold = CurrentDetectionController.getInstance().getCurrentThreshold();
                            cabInfoSp.setCurrentThreshold(currentThreshold);
                            cabInfoSp.setAutoSetCurrentDetectionStatus(true);
                        }
                    }
                }
            });
        }
        if (!currentDetectionSettingDialog.isShowing()) {
            currentDetectionSettingDialog.show();
        }
    }

    private void showPushRodSettingDialog() {
        if (pushRodSettingDialog == null) {
            pushRodSettingDialog = new PushRodSettingDialog(this);
            pushRodSettingDialog.setCurrentPushTimeConsumer(new Consumer<Float>() {
                @Override
                public void accept(Float aFloat) {
                    if (cabInfoSp != null) {
                        A_Main2.pushrodActSetTime = (byte) (aFloat * 10);
                        cabInfoSp.setpushrodActSetTime((byte) (aFloat * 10));
                        tv_pushrodActSetTime.setText((A_Main2.pushrodActSetTime / 10f) + "秒");
                    }
                }
            });

            pushRodSettingDialog.setCancleTimeConsumer(new Consumer() {

                @Override
                public void accept(Object o) {
                    pushRodSettingDialog.dismiss();
                }
            });
        }
        if (!pushRodSettingDialog.isShowing()) {
            pushRodSettingDialog.show();
        }
    }
}

class MyAdapter_1 extends BaseAdapter {

    private final StringBuilder stringBuilder = new StringBuilder();
    private Context context;
    private List<? extends Map<String, ?>> mData;
    private int resources;

    private TextView statusView;
    private TextView statusView2;

    public MyAdapter_1(Context context, List<? extends Map<String, ?>> data, int resources) {
        this.context = context;
        this.resources = resources;
        this.mData = data;
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {


        final ViewHolder viewHolder;
        if (null == convertView) {

            convertView = View.inflate(context, resources, null);
            viewHolder = new ViewHolder();

            viewHolder.a_1 = (TextView) convertView.findViewById(R.id.a_1);
            viewHolder.t_1 = (TextView) convertView.findViewById(R.id.bid);
            viewHolder.t_2 = (TextView) convertView.findViewById(R.id.dianya);
            viewHolder.t_3 = (TextView) convertView.findViewById(R.id.dianliang);
            viewHolder.t_4 = (TextView) convertView.findViewById(R.id.dianliu);
            viewHolder.t_5 = (TextView) convertView.findViewById(R.id.wendu);
            viewHolder.t_6 = (TextView) convertView.findViewById(R.id.inner_lock);
            viewHolder.t_8 = (TextView) convertView.findViewById(R.id.t_8);
            viewHolder.t_9 = (TextView) convertView.findViewById(R.id.t_9);
            viewHolder.t_10 = (TextView) convertView.findViewById(R.id.t_10);
            viewHolder.t_11 = (TextView) convertView.findViewById(R.id.t_11);
            viewHolder.t_12 = (TextView) convertView.findViewById(R.id.side_lock);
            viewHolder.t_13 = (TextView) convertView.findViewById(R.id.t_13);
            viewHolder.t_14 = (TextView) convertView.findViewById(R.id.t_14);
            viewHolder.t_16 = (TextView) convertView.findViewById(R.id.t_16);
            viewHolder.t_17 = (TextView) convertView.findViewById(R.id.t_17);

            viewHolder.t_20 = (TextView) convertView.findViewById(R.id.t_20);
            viewHolder.t_21 = (TextView) convertView.findViewById(R.id.t_21);
            viewHolder.t_22 = (TextView) convertView.findViewById(R.id.t_22);
            viewHolder.t_23 = (TextView) convertView.findViewById(R.id.t_23);
            viewHolder.t_24 = (TextView) convertView.findViewById(R.id.t_24);
            viewHolder.t_25 = (TextView) convertView.findViewById(R.id.t_25);
            viewHolder.tv_samplingV = (TextView) convertView.findViewById(R.id.tv_samplingV);
            viewHolder.tv_RealSoc = (TextView) convertView.findViewById(R.id.realSoc);

            viewHolder.uid = (TextView) convertView.findViewById(R.id.uid);

            viewHolder.open_door = (TextView) convertView.findViewById(R.id.open_door);
            viewHolder.close_door = (TextView) convertView.findViewById(R.id.close_door);
            viewHolder.state = (TextView) convertView.findViewById(R.id.state);
            viewHolder.chargeOn = (TextView) convertView.findViewById(R.id.charge_on);
            viewHolder.chargeOff = (TextView) convertView.findViewById(R.id.charge_off);

            if (BuildConfig.DEBUG) {
                viewHolder.open_door.setVisibility(View.VISIBLE);
                viewHolder.close_door.setVisibility(View.VISIBLE);

                // TODO: 2020/7/31 消防临时添加
                viewHolder.chargeOn.setText("喷水");
                viewHolder.chargeOff.setText("停水");
//                viewHolder.chargeOn.setVisibility(View.VISIBLE);
//                viewHolder.chargeOff.setVisibility(View.VISIBLE);
//                viewHolder.heating.setVisibility(View.VISIBLE);
            }

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.open_door.setTag(i);
        viewHolder.open_door.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final int doorNum = Integer.parseInt(view.getTag().toString()) + 1;
//                A_Main2.adminElongationHandler.sendMessage(message);

                A_Main2.syncControl(new Consumer() {
                    @Override
                    public int hashCode() {
                        return ("后台打开" + doorNum + "号舱门").hashCode();
                    }

                    @Override
                    public boolean equals(Object obj) {
                        return hashCode() == obj.hashCode();
                    }

                    @NonNull
                    @Override
                    public String toString() {
                        return "后台打开" + doorNum + "号舱门";
                    }

                    @Override
                    public void accept(Object o) {
                        A_Admin.showprogressDialogHandler.sendMessage(new Message());
                        BatteryDataModel batteryDataModel = A_Main2.batteryDataModels.get(doorNum - 1);
                        if (batteryDataModel != null) {
                            DoorController.getInstance().openDoor(batteryDataModel);
                        }
                    }
                });
            }
        });

        viewHolder.close_door.setTag(i);
        viewHolder.close_door.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final int doorNum = Integer.parseInt(view.getTag().toString()) + 1;
                A_Main2.syncControl(new Consumer() {
                    @Override
                    public int hashCode() {
                        return ("后台关闭" + doorNum + "号舱门").hashCode();
                    }

                    @Override
                    public boolean equals(Object obj) {
                        return hashCode() == obj.hashCode();
                    }

                    @NonNull
                    @Override
                    public String toString() {
                        return "后台关闭" + doorNum + "号舱门";
                    }

                    @Override
                    public void accept(Object o) {
//                        A_Main2.pull("", doorNum + "");
//                        A_Main2.testStop(doorNum);
                        A_Admin.showprogressDialogHandler.sendMessage(new Message());
                        BatteryDataModel batteryDataModel = A_Main2.batteryDataModels.get(doorNum - 1);
                        if (batteryDataModel != null) {
                            DoorController.getInstance().closeDoor(batteryDataModel);
                        }
                    }
                });
            }
        });
        viewHolder.chargeOn.setTag(i);
        viewHolder.chargeOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (statusView != null && statusView2 != null) {
                    return;
                }
                viewHolder.chargeOn.setEnabled(false);
                viewHolder.chargeOn.setText("喷水中");
                if (statusView == null) {
                    statusView = viewHolder.chargeOn;
                } else if (statusView2 == null) {
                    statusView2 = viewHolder.chargeOn;
                }
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("door", Integer.toString(Integer.parseInt(view.getTag().toString()) + 1));
                message.setData(bundle);
                A_Main2.chargeOnHandler.sendMessage(message);
            }
        });

        viewHolder.chargeOff.setTag(i);
        viewHolder.chargeOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (statusView != null && viewHolder.chargeOff.getParent() == statusView.getParent()) {
                    statusView.setEnabled(true);
                    statusView.setText("喷水");
                    statusView = null;
                } else if (statusView2 != null && viewHolder.chargeOff.getParent() == statusView2.getParent()) {

                    statusView2.setEnabled(true);
                    statusView2.setText("喷水");
                    statusView2 = null;
                } else {
                    return;
                }

                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("door", Integer.toString(Integer.parseInt(view.getTag().toString()) + 1));
                message.setData(bundle);
                A_Main2.chargeOffHandler.sendMessage(message);
            }
        });

        //格式化小数
        DecimalFormat df = new DecimalFormat("0.0");
        //标号
        if (i + 1 < 10) {
            viewHolder.a_1.setText("0" + (i + 1));
        } else {
            viewHolder.a_1.setText((i + 1) + "");
        }

        //电池编号
        viewHolder.t_1.setText(mData.get(i).get("bid").toString());
        //UID编号
        viewHolder.uid.setText(mData.get(i).get("uid").toString());

        //电压
        double dianya_double = Double.parseDouble(mData.get(i).get("dianya").toString());
        dianya_double = dianya_double / 10;
        String dinaya = df.format(dianya_double);
        viewHolder.t_2.setText(dinaya + "V");

        //电量
        String dianliang = mData.get(i).get("dianliang").toString();
        if (!TextUtils.isEmpty(dianliang) && !dianliang.equals("null")) {
            int dianliangint = Integer.parseInt(dianliang);
            viewHolder.t_3.setText((dianliangint > 100 ? dianliangint - 100 : dianliangint) + "%");
        }

        //电流
        double dianliu_double = Double.parseDouble(mData.get(i).get("dianliu").toString());
        dianliu_double = dianliu_double / 10;
        String dianliu = df.format(dianliu_double);
        viewHolder.t_4.setText(dianliu + "A");

        //温度显示
        double tem_1_double = Double.parseDouble(mData.get(i).get("tem_1").toString());
        tem_1_double = tem_1_double / 10;
        String tem_1 = df.format(tem_1_double);

        double tem_2_double = Double.parseDouble(mData.get(i).get("tem_2").toString());
        tem_2_double = tem_2_double / 10;
        String tem_2 = df.format(tem_2_double);

        if (mData.get(i).get("samplingV") != null) {
            viewHolder.tv_samplingV.setText(mData.get(i).get("samplingV").toString());
        } else {
            viewHolder.tv_samplingV.setText("0.0V");
        }

        String soc = mData.get(i).get("realSoc").toString();
        if (!TextUtils.isEmpty(soc) && !soc.equals("null")) {
            final int socInt = Integer.parseInt(soc);
            viewHolder.tv_RealSoc.setText("" + (socInt > 100 ? socInt - 100 : socInt));
        }

        //bms版本号
        String a = mData.get(i).get("barVer").toString().substring(0, 2);
        String b = mData.get(i).get("barVer").toString().substring(2, 4);
        int a_i = Integer.parseInt(a, 16);
        int b_i = Integer.parseInt(b, 16);
        viewHolder.t_13.setText("BH:" + a_i + "  " + "BS:" + b_i + "");

        //循环次数
        String loop_str = mData.get(i).get("loops").toString();
        viewHolder.t_14.setText(loop_str + "次");

        //温度显示
        viewHolder.t_16.setText(tem_1 + "C");
        viewHolder.t_5.setText(tem_2 + "C");

        viewHolder.t_6.setText(mData.get(i).get("door").toString());
        if (viewHolder.t_6.getText().equals("有电池")) {
            viewHolder.t_6.setTextColor(0xff008000);
        } else if (viewHolder.t_6.getText().equals("-1")) {
            viewHolder.t_6.setText("操作中");
            viewHolder.t_6.setTextColor(0xffcccccc);
        } else {
            viewHolder.t_6.setTextColor(0xfff06b00);
        }
        viewHolder.t_12.setText(mData.get(i).get("small").toString());
        if (viewHolder.t_12.getText().equals("有电池")) {
            viewHolder.t_12.setTextColor(0xff008000);
        } else if (viewHolder.t_12.getText().equals("-1")) {
            viewHolder.t_12.setText("操作中");
            viewHolder.t_12.setTextColor(0xffcccccc);
        } else {
            viewHolder.t_12.setTextColor(0xfff06b00);
        }

        viewHolder.t_17.setText(mData.get(i).get("push").toString());
        if (viewHolder.t_17.getText().equals("未检测")) {
            viewHolder.t_17.setTextColor(0xffcccccc);
        } else if (viewHolder.t_17.getText().equals("故障")) {
            viewHolder.t_17.setTextColor(0xff008000);
        } else {
            viewHolder.t_17.setTextColor(0xfff06b00);
        }

        //温度显示
        viewHolder.t_24.setText(mData.get(i).get("item_max").toString() + "mv");
        viewHolder.t_23.setText(mData.get(i).get("item_min").toString() + "mv");

        //电流
        double demandPower_double = Double.parseDouble(mData.get(i).get("demandPower").toString());
        demandPower_double = demandPower_double / 10;
        String demandPower = df.format(demandPower_double);
        viewHolder.t_25.setText(demandPower + "W");

        //DCDC状态
        String state_str = "";
        String DCDC_state = mData.get(i).get("DCDC_state").toString();
        if (!TextUtils.isEmpty(DCDC_state)) {
            switch (DCDC_state) {
                case "0":
                    state_str = "待机";
                    break;
                case "1":
                    state_str = "充电中";
                    break;
                case "2":
                    state_str = "故障中";
                    break;
                case "3":
                    state_str = "启动中";
                    break;
                case "4":
                    state_str = "排队中";
                    break;
                case "5":
                    state_str = "加热中";
                    break;
                case "6":
                    state_str = "告警中";
                    break;
                case "7":
                    state_str = "激活中";
                    break;
                case "8":
                    state_str = "激活失败";
                    break;
                case "9":
                    state_str = "SOC校准中";
                    break;
                case "10":
                    state_str = "禁用中";
                    break;
            }
        }

        viewHolder.state.setText(state_str);
        //模块输出电压
        double DCDC_dianya_double = Double.parseDouble(mData.get(i).get("DCDC_dianya").toString());
        DCDC_dianya_double = DCDC_dianya_double / 10;
        String DCDC_dinaya = df.format(DCDC_dianya_double);
        viewHolder.t_8.setText(DCDC_dinaya + "V");
        //电流
        double DCDC_dianliu_double = Double.parseDouble(mData.get(i).get("DCDC_dianliu").toString());
        DCDC_dianliu_double = DCDC_dianliu_double / 10;
        String DCDC_dianliu = df.format(DCDC_dianliu_double);
        viewHolder.t_9.setText(DCDC_dianliu + "A");
        //停止原因
        int stop_code = Integer.parseInt(mData.get(i).get("DCDC_stop").toString());
        if (stop_code < 50) {
            viewHolder.t_10.setText(A_Admin.stopCodeStr[stop_code]);
        }
        //版本号
        viewHolder.t_11.setText("S_VER - " + mData.get(i).get("DCDC_SV").toString() + "     H_VER - " + mData.get(i).get("DCDC_HV").toString());
        //故障原因
        String ERROR_state_str_01 = mData.get(i).get("ERROR_state_01").toString();
        String end_ERROR_state_str_01 = ERROR_state_str_01.substring(2, 10);
        stringBuilder.setLength(0);
        if (end_ERROR_state_str_01.length() == 8) {
            for (int in = end_ERROR_state_str_01.length() - 1; in >= 0; in -= 2) {
                stringBuilder.append(end_ERROR_state_str_01.charAt(in - 1));
                stringBuilder.append(end_ERROR_state_str_01.charAt(in));
            }
        }
        viewHolder.t_21.setText(stringBuilder.toString());

        //故障原因
        String ERROR_state_str_02 = mData.get(i).get("ERROR_state_02").toString();
        String end_ERROR_state_str_02 = ERROR_state_str_02.substring(2, 10);
        stringBuilder.setLength(0);
        if (end_ERROR_state_str_02.length() == 8) {
            for (int in = end_ERROR_state_str_02.length() - 1; in >= 0; in -= 2) {
                stringBuilder.append(end_ERROR_state_str_02.charAt(in - 1));
                stringBuilder.append(end_ERROR_state_str_02.charAt(in));
            }
        }
        viewHolder.t_20.setText(stringBuilder.toString());

        //故障原因
        String ERROR_state_str_03 = mData.get(i).get("ERROR_state_03").toString();
        String end_ERROR_state_str_03 = ERROR_state_str_03.substring(2, 10);
        stringBuilder.setLength(0);
        if (end_ERROR_state_str_03.length() == 8) {
            for (int in = end_ERROR_state_str_03.length() - 1; in >= 0; in -= 2) {
                stringBuilder.append(end_ERROR_state_str_03.charAt(in - 1));
                stringBuilder.append(end_ERROR_state_str_03.charAt(in));
            }
        }
        viewHolder.t_22.setText(stringBuilder.toString());

        return convertView;

    }

    private class ViewHolder {
        TextView a_1;
        TextView t_1;
        TextView t_2;
        TextView t_3;
        TextView t_4;
        TextView t_5;
        TextView t_6;
        TextView t_8;
        TextView t_9;
        TextView t_10;
        TextView t_11;
        TextView t_12;
        TextView t_13;
        TextView t_14;
        TextView t_16;
        TextView t_17;
        TextView t_20;
        TextView t_21;
        TextView t_22;
        TextView t_23;
        TextView t_24;
        TextView t_25;
        TextView tv_samplingV;
        TextView tv_RealSoc;

        TextView uid;
        TextView open_door;
        TextView close_door;
        TextView state;
        TextView chargeOn;
        TextView chargeOff;
    }

}

class MyAdapter_2 extends BaseAdapter {

    final char[] warningCodeChars = new char[8];
    final StringBuilder stringBuilder = new StringBuilder(warningCodeChars.length);

    private Context context;
    private List<? extends Map<String, ?>> mData;
    private int resources;


    public MyAdapter_2(Context context, List<? extends Map<String, ?>> data, int resources) {
        this.context = context;
        this.resources = resources;
        this.mData = data;
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        ViewHolder viewHolder;
        if (null == convertView) {
            convertView = View.inflate(context, resources, null);
            viewHolder = new ViewHolder();

            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.t_1 = (TextView) convertView.findViewById(R.id.t_1);
            viewHolder.t_2 = (TextView) convertView.findViewById(R.id.t_2);
            viewHolder.t_3 = (TextView) convertView.findViewById(R.id.t_3);
            viewHolder.t_4 = (TextView) convertView.findViewById(R.id.t_4);
            viewHolder.t_5 = (TextView) convertView.findViewById(R.id.t_5);
            viewHolder.t_6 = (TextView) convertView.findViewById(R.id.t_6);
            viewHolder.t_7 = (TextView) convertView.findViewById(R.id.t_7);
            viewHolder.open = (TextView) convertView.findViewById(R.id.open);
            viewHolder.close = (TextView) convertView.findViewById(R.id.close);
            viewHolder.whichACDC = (TextView) convertView.findViewById(R.id.whichACDC);
            viewHolder.remainingTotalPower = (TextView) convertView.findViewById(R.id.remainingTotalPower);
            viewHolder.sleepStatus = (TextView) convertView.findViewById(R.id.sleepStatus);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        //title
        viewHolder.title.setText("ACDC：0" + (i + 1));

        if (i < 2) {
            Arrays.fill(warningCodeChars, '0');
            char[] warningStatusChars = Integer.toHexString(A_Main2.ACDC_ERROR_state_01[i]).toCharArray();
            System.arraycopy(warningStatusChars, 0, warningCodeChars, warningCodeChars.length - warningStatusChars.length, warningStatusChars.length);
            stringBuilder.setLength(0);
            viewHolder.t_2.setText(stringBuilder.append(warningCodeChars).toString());
        }

        if (viewHolder.t_2.getText().toString().contains("1")) {
            viewHolder.t_1.setText("内部告警");
        } else {
            viewHolder.t_1.setText("无");
        }


        //格式化小数
        DecimalFormat df_1 = new DecimalFormat("0.00");
        //格式化小数
        DecimalFormat df_2 = new DecimalFormat("0.0");
        //模块输入电压
        double ACDC_dianya_in_double = Double.parseDouble(mData.get(i).get("ACDC_dianya_in").toString());
        ACDC_dianya_in_double = ACDC_dianya_in_double / 10;
        String ACDC_dinaya_in = df_2.format(ACDC_dianya_in_double);
        viewHolder.t_3.setText(ACDC_dinaya_in + "V");

        //模块输出功率
        double ACDC_gonglv_double = Double.parseDouble(mData.get(i).get("ACDC_gonglv").toString());
        ACDC_gonglv_double = ACDC_gonglv_double / 1000f;
        String ACDC_gonglv = df_1.format(ACDC_gonglv_double);
        viewHolder.t_4.setText(ACDC_gonglv + "KW");
        //模块输出电压
        double ACDC_dianya_out_double = Double.parseDouble(mData.get(i).get("ACDC_dianya_out").toString());
        ACDC_dianya_out_double = ACDC_dianya_out_double / 10;
        String ACDC_doutaya_out = df_2.format(ACDC_dianya_out_double);
        viewHolder.t_5.setText(ACDC_doutaya_out + "V");
        //模块输入电压
        double ACDC_dianliu_in_double = Double.parseDouble(mData.get(i).get("ACDC_dianliu_in").toString());
        ACDC_dianliu_in_double = ACDC_dianliu_in_double / 10;
        String ACDC_dianliu_in = df_2.format(ACDC_dianliu_in_double);
        viewHolder.t_6.setText(ACDC_dianliu_in + "A");
        //版本号
        viewHolder.t_7.setText("S_VER - " + mData.get(i).get("ACDC_SV").toString() + "     H_VER - " + mData.get(i).get("ACDC_HV").toString());

        viewHolder.whichACDC.setText(mData.get(i).get("whichACDC").toString());
        viewHolder.remainingTotalPower.setText(mData.get(i).get("remainingTotalPower").toString());
        viewHolder.sleepStatus.setText(mData.get(i).get("sleepStatus").toString());

        viewHolder.open.setTag(i);
        viewHolder.open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("door", Integer.toString(Integer.parseInt(view.getTag().toString()) + 1));
                message.setData(bundle);
                A_Main2.openADCDHandler.sendMessage(message);
            }
        });

        viewHolder.close.setTag(i);
        viewHolder.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("door", Integer.toString(Integer.parseInt(view.getTag().toString()) + 1));
                message.setData(bundle);
                A_Main2.closeADCDHandler.sendMessage(message);
            }
        });


        return convertView;

    }

    private class ViewHolder {
        TextView title;
        TextView t_1;
        TextView t_2;
        TextView t_3;
        TextView t_4;
        TextView t_5;
        TextView t_6;
        TextView t_7;
        TextView open;
        TextView close;
        TextView whichACDC;
        TextView remainingTotalPower;
        TextView sleepStatus;
    }
}


