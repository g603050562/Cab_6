package client.halouhuandian.app15;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.util.Consumer;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hellohuandian.app.httpclient.HttpGetDownloaderAndLauncher;
import com.hellohuandian.app.httpclient.HttpGetQcode;
import com.hellohuandian.app.httpclient.HttpGetTel;
import com.hellohuandian.app.httpclient.HttpOutLineCheckOldBind;
import com.hellohuandian.app.httpclient.HttpOutLineCheckUserBalance;
import com.hellohuandian.app.httpclient.HttpOutLineDate;
import com.hellohuandian.app.httpclient.HttpOutLineRentBattery;
import com.hellohuandian.app.httpclient.HttpUploadBatteryInfo;
import com.hellohuandian.app.httpclient.HttpUploadBatteryInfoToRent;
import com.hellohuandian.app.httpclient.HttpUploadGMS;
import com.hellohuandian.app.httpclient.HttpWriteUidRet;
import com.hellohuandian.app.httpclient.IFHttpBandLongLinkLinstener;
import com.hellohuandian.app.httpclient.IFHttpGetQcodeLinstener;
import com.hellohuandian.app.httpclient.IFHttpGetTelLinstener;
import com.hellohuandian.app.httpclient.IFHttpOutLineCheckOldBindListener;
import com.hellohuandian.app.httpclient.IFHttpOutLineCheckUserBalanceListener;
import com.hellohuandian.app.httpclient.IFHttpOutLineDateListener;
import com.hellohuandian.app.httpclient.IFHttpOutLineRentBatteryUploadInfoConfirmListener;
import com.hellohuandian.app.httpclient.IFHttpUploadBatteryInfoListener;
import com.hellohuandian.app.httpclient.IFHttpUploadBatteryInfoToRentListener;
import com.hellohuandian.app.httpclient.longLink.OpenLongLink;
import com.hellohuandian.moviesupload.HttpUploadMovies;
import com.hellohuandian.moviesupload.HttpUploadMoviesPath;
import com.hellohuandian.moviesupload.MoviesCreateFile;
import com.hellohuandian.moviesupload.MoviesUnit;
import com.hellohuandian.pubfunction.BaseStation.GSMCellLocation;
import com.hellohuandian.pubfunction.DBM.CurrentNetDBM;
import com.hellohuandian.pubfunction.DBM.IFCurrentNetDBMLinstener;
import com.hellohuandian.pubfunction.DownLoad.DownLoadUpdateMain;
import com.hellohuandian.pubfunction.DownLoad.IFDownLoadUpdateMainLinstener;
import com.hellohuandian.pubfunction.ProgressDialog.ProgressDialog_4;
import com.hellohuandian.pubfunction.Root.RootCommand;
import com.hellohuandian.pubfunction.Unit.LogUtil;
import com.hellohuandian.pubfunction.Unit.PubFunction;
import com.hellohuandian.pubfunction.Unit.Unit;
import com.hellohuandian.pubfunction.animation.ExchangeAnimation;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import client.halouhuandian.app15.bean.RentBtyListModel;
import client.halouhuandian.app15.core.tools.BatteryUtil;
import client.halouhuandian.app15.core.tools.NetUtil;
import client.halouhuandian.app15.db.DBHelper;
import client.halouhuandian.app15.devicesController.currentDetectionBoard.CurrentDetectionController;
import client.halouhuandian.app15.devicesController.currentDetectionBoard.CurrentDetectionModel;
import client.halouhuandian.app15.devicesController.currentDetectionBoard.SetPushRodLitValModel;
import client.halouhuandian.app15.devicesController.fire.FireSwitchController;
import client.halouhuandian.app15.devicesController.rod.BatteryDataModel;
import client.halouhuandian.app15.devicesController.rod.DoorController;
import client.halouhuandian.app15.devicesController.sensor.SensorController;
import client.halouhuandian.app15.devicesController.sensor.SensorDataBean;
import client.halouhuandian.app15.devicesController.setting.SettingConfig;
import client.halouhuandian.app15.devicesController.switcher.DeviceSwitchController;
import client.halouhuandian.app15.devicesController.switcher.DeviceSwitcher;
import client.halouhuandian.app15.log.LocalLog;
import client.halouhuandian.app15.pub.CreateFile;
import client.halouhuandian.app15.pub.DownLoadApk;
import client.halouhuandian.app15.pub.UpdataBattery;
import client.halouhuandian.app15.pub.UpdataDcdc;
import client.halouhuandian.app15.pub.dcList.DcDataLink;
import client.halouhuandian.app15.serial_port.ChangeTool;
import client.halouhuandian.app15.sp.CabInfoSp;
import client.halouhuandian.app15.sp.ForbiddenSp;
import client.halouhuandian.app15.upgrade.battery.A_UpDataBattery;
import client.halouhuandian.app15.upgrade.dc.acdc.AcdcUpdateActivity;
import client.halouhuandian.app15.upgrade.dc.acdc.AcdcUpdateActivity2;
import client.halouhuandian.app15.upgrade.dc.dcdc.DcdcUpdateActivity;
import client.halouhuandian.app15.upgrade.dc.dcdc.DcdcUpdateActivity2;
import client.halouhuandian.app15.upgrade.environmentBoard.EvnBordUpdateActivity;

/**
 * 软件首页
 */
public class A_Main2 extends Activity implements OnClickListener, MyApplication.IFResultAppLinstener, OpenLongLink.IFHttpOpenLongLinkLinstener, IFHttpBandLongLinkLinstener,
        IFHttpGetQcodeLinstener, IFHttpGetTelLinstener, IFCurrentNetDBMLinstener, IFHttpUploadBatteryInfoListener, IFHttpOutLineDateListener {

    private static LocalLog localLog;
    private Activity activity;
    //默认仓门数
    private int DEFINE_BAR_COUNT = 9;
    //柜子获得的id
    private String cabinetID = "";
    //view
    private ImageView bar_r_image_1, bar_r_image_2, bar_r_image_3, bar_r_image_4, bar_r_image_5, bar_r_image_6, bar_r_image_7, bar_r_image_8, bar_r_image_9, bar_r_image_10, bar_r_image_11, bar_r_image_12;
    private ImageView[] bar_r_images;
    private TextView bar_r_text_1, bar_r_text_2, bar_r_text_3, bar_r_text_4, bar_r_text_5, bar_r_text_6, bar_r_text_7, bar_r_text_8, bar_r_text_9, bar_r_text_10, bar_r_text_11, bar_r_text_12;
    private TextView[] bar_r_texts;
    private ImageView bar_not_image_1, bar_not_image_2, bar_not_image_3, bar_not_image_4, bar_not_image_5, bar_not_image_6, bar_not_image_7, bar_not_image_8, bar_not_image_9;
    private ImageView[] bar_not_images;

    private ImageView black_1, black_2;
    private ImageView up_bar_1, up_bar_2;
    private ImageView rent_bar_qcode;
    private TextView up_bar_1_text, up_bar_2_text;
    private FrameLayout up_bar_2_panel, up_bar_1_panel;
    private TextView now_address, tel_text;
    private TextView cab_id, version, cost_bi;
    private LinearLayout info_panel, cost;
    private TextView now_time; //右上角时间
    private ImageView download_qcode; //二维码手机app下载

    //ttl语音朗读
    private TextToSpeech textToSpeech;

    //Handler
    private Handler setUiHandler, changeQcodeHandler, setTimeHandler, sensorCollectionHandler, rebootAndroidSystem, updataBatteryDownlondReturnHandler, uploadPbarHandler, setMoviesHandler, otherInfo_8_Handler;
    public static Handler chargeOnHandler, chargeOffHandler,
            speakHandler, showProgressDialogHandler, openADCDHandler, closeADCDHandler, adminElongationHandler, adminShrinkHandler;
    private Handler dcdcUpdradeHandler, acdcUpdradeHandler;
    private Handler envUpdradeHandler;
    private Handler updateHander = new Handler();

    /**
     * 电池信息
     */
    //电池拼接临时数据
    private byte[][][] bar_all_2 = new byte[9][18][8];
    //微动状态
    public static int[] SMALLS = new int[32];
    //门状态
    public static int[] DOORS = new int[32];
    //推杆状态（ 1 - 伸出，开门      0 - 停止      2 - 收缩，关门     3 - 故障      -1 - 初始化状态 ）
    public static int[] PUSHS = new int[32];
    //TEM_1温度传感器
    public static int[] TEM_1 = new int[32];
    //TEM_2温度传感器
    public static int[] TEM_2 = new int[32];
    //相对电池电量
    public static int[] PERCENtAGES = new int[32];
    //绝对容量百分比
    public static int[] JUEDUI_PERCENtAGES = new int[32];
    //Laft_cap(相对剩余)
    public static int[] LEFT_CAP = new int[32];
    //Full_cap(充满剩余)
    public static int[] FULL_CAP = new int[32];
    //电池id信息(例如：MBKKK.....)
    public static String[] BIDS = new String[32];
    //电池校验码变量
    public static String[] UIDS = new String[32];
    //温度
    //电压
    public static int[] DIANYA = new int[32];
    //电流
    public static int[] DIANLIU = new int[32];
    //SOH
    public static int[] SOH_2 = new int[32];
    //LOOPS
    public static int[] LOOPS = new int[32];
    //电池版本号
    public static String[] barVer = new String[32];
    //单体最大电压
    public static int[] item_max = new int[32];
    //单体最小电压
    public static int[] item_min = new int[32];
    //需求电压
    public static int[] demandPower = new int[32];

    //采样电压
    public static String[] samplingVs = new String[32];
    public static String[] realSocVs = new String[32];

    /**
     * DCDC信息
     */
    //DCDC_状态
    public static String[] DCDC_state = new String[32];
    //DCDC_电压
    public static int[] DCDC_dianya = new int[32];
    //DCDC_电流
    public static int[] DCDC_dianliu = new int[32];
    //DCDC_停止码
    public static int[] DCDC_stop = new int[32];
    //DCDC_软件版本
    public static String[] DCDC_SV = new String[32];
    //DCDC_硬件版本
    public static String[] DCDC_HV = new String[32];

    /**
     * 告警状态
     */
    //DCDC_状态
    public static String[] ERROR_state_01 = new String[32];
    public static String[] ERROR_state_02 = new String[32];
    public static String[] ERROR_state_03 = new String[32];
    //ACDC告警只有内部告警
    public static int[] ACDC_ERROR_state_01 = new int[2];
    /**
     * ACDC信息
     */
    //ACDC_输入电压
    public static int[] ACDC_dianya_in = new int[32];
    //ACDC_输出电压
    public static int[] ACDC_dianya_out = new int[32];
    //ACDC_输出电流
    public static int[] ACDC_dianliu_in = new int[32];
    //ACDC_功率
    public static int[] ACDC_gonglv = new int[32];
    //ACDC_软件版本
    public static String[] ACDC_SV = new String[32];
    //ACDC_硬件版本
    public static String[] ACDC_HV = new String[32];

    public static String[] whichACDC = new String[32];
    public static String[] remainingTotalPower = new String[32];
    public static short[] remainingTotalPower2 = new short[32];
    public static String[] sleepStatus = new String[32];
    public static int[] sleepStatusValues = new int[32];
    public static long[] lastDataDateAttrs = new long[32];

    //ACDC 返回时间
    public static long[] ACDC_RT = new long[32];
    //等待加热状态，没2次30秒循环判断是否加热的标机状态
    public static boolean[] waitheatingStatus = new boolean[32];
    private Gson gson = new Gson();
    //长链接
    private OpenLongLink longLink;
    //判断时候是在挂起的状态   1是挂起状态  0是正常状态
    public static int AN_IS_RUN = 0;
    //判断电池是否在升级
    public static int BAR_IS_RUN = 0;
    //左上角 图标显示
    private ImageView b_3;
    private TextView t_5, t_6, t_7, t_8, t_9;

    int onCreateThreadCode = 0;
    //新提示框
    private ProgressDialog_4 progressDialog_4;
    //数据库操作
    private DBHelper mHelper;
    private SQLiteDatabase mDatabase;
    private int exchangeFailCount = 0;

    //新界面
    LinearLayout dialog_panel;
    TextView dialog_time, dialog_info;
    //湿度和温度的显示值 电表和温度数据
    private String tem_str = "";
    private String hun_str = "";
    //电表功率
    private String ammeterPower;
    //电表千瓦时，用了多少度电
    private String ammeterTotalPower;
    private String evnSoftwareVersion;

    private boolean isInitFinish;

    private CurrentNetDBM currentNetDBM;
    private boolean isAutoSetCurrentDetection;
    private boolean isAllowCloseAcdcs = true;

    public static boolean isAutoControlAirfan = true;

    public static Set<String> upgrading = new HashSet<>();
    public volatile boolean upgradingStatus;
    private int temperature;
    static int emptyDoor;
    private Handler openDoorButtonHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            final int randomEmptyDoor = bundle.getInt("randomEmptyDoor");
            switch (msg.what) {
                case 1:
                    if (randomEmptyDoor > 0) {
                        if (SMALLS[randomEmptyDoor - 1] == 0) {
                            syncControl(new Consumer() {
                                @Override
                                public void accept(Object o) {
                                    showDialogInfo("准备关闭" + randomEmptyDoor + "号仓门，请注意安全", "3", "1");
                                    SystemClock.sleep(3000);
                                    LogUtil.I("按钮randomEmptyDoor:" + randomEmptyDoor);
                                    LogUtil.I("按钮SMALLS:" + SMALLS[randomEmptyDoor - 1]);
                                    if (SMALLS[randomEmptyDoor - 1] == 0) {
//                                        pull("", randomEmptyDoor + "");
//                                        testStop(randomEmptyDoor);

                                        BatteryDataModel batteryDataModel = batteryDataModels.get(randomEmptyDoor - 1);
                                        if (batteryDataModel != null) {
                                            DoorController.getInstance().closeDoor(batteryDataModel);
                                        }
                                    }
                                }
                            });
                        }
                    }
                    break;
                case 2:
                    int time = bundle.getInt("time");
                    if (time > 0) {
                        showDialogInfo("开仓按钮过于频繁，请稍后再试", time + "", "1");
                    }
                    break;

                case 3:
                    if (randomEmptyDoor > 0) {
                        syncControl(new Consumer() {
                            @Override
                            public void accept(Object o) {
                                final long endTime = System.currentTimeMillis() + 50 * 1000;
                                boolean isEmpty = false;
                                while (System.currentTimeMillis() <= endTime) {
                                    SystemClock.sleep(1000);
                                    if (BIDS[randomEmptyDoor - 1].equals("FFFFFFFFFFFFFFFF")
                                            || BIDS[randomEmptyDoor - 1].equals("0000000000000000")) {
                                        // TODO: 2020/11/23 如果电池不存在了就计时10S
                                        isEmpty = true;
                                        break;
                                    }
                                }

                                if (isEmpty) {
                                    openDoorButtonHandler.removeCallbacksAndMessages(null);
                                    showDialogInfo("10秒后关闭" + randomEmptyDoor + "号仓门，请注意安全", "10", "1");
                                    SystemClock.sleep(10000);
//                                    pull("", randomEmptyDoor + "");
//                                    testStop(randomEmptyDoor);
                                    BatteryDataModel batteryDataModel = batteryDataModels.get(randomEmptyDoor - 1);
                                    if (batteryDataModel != null) {
                                        DoorController.getInstance().closeDoor(batteryDataModel);
                                    }

                                }
                            }
                        });
                    }
                    break;
            }
        }
    };

    //网络接口返回
    IFHttpGetTelLinstener ifHttpGetTelLinstener;
    IFHttpUploadBatteryInfoListener ifHttpUploadBatteryInfoListener;
    IFHttpBandLongLinkLinstener ifHttpBandLongLinkLinstener;
    IFHttpOutLineDateListener ifHttpOutLineDateListener;
    IFHttpGetQcodeLinstener ifHttpGetQcodeLinstener;
    IFCurrentNetDBMLinstener ifCurrentNetDBMLinstener;
    IFHttpOutLineCheckUserBalanceListener ifHttpOutLineCheckUserBalanceListener = new IFHttpOutLineCheckUserBalanceListener() {
        @Override
        public void onHttpOutLineCheckUserBalanceResult(String uid, final String door, String code, String str, String data) {
//            code = "-1";
            writeLocalLog(data + "");
            if (code.equals("-1")) {  //开启离线换电
                outLineExchangeUID(Integer.parseInt(door), null);
            } else {
                try {

                    // TODO: 2020/6/4  "status":0,"msg":"换出电池操作太频繁，请1分钟后，再次尝试~(推出)
                    // TODO: 2020/6/4  2分钟(吞)

                    JSONObject jsonObject = new JSONObject(data);
                    if (code.equals("0")) {
                        String show = jsonObject.getString("show");
                        final String errno = jsonObject.getString("errno");
                        //未绑定的电池 E2001:回收
                        if (errno.equals("E2001")) {
                            isReplaceBattery = false;
                            if (show.equals("1")) {
                                showDialogInfo(str, "10", "1");
                            }
                            isReplaceBattery = false;
                        }
                        //弹出插入的电池(E1001:不符合换电规则需要弹出)
                        else if (errno.equals("E1001")) {
                            if (show.equals("1")) {
                                showDialogInfo(str, "10", "1");
                            }

                            syncControl(new Consumer() {
                                @NonNull
                                @Override
                                public String toString() {
                                    return door + "号" + errno;
                                }

                                @Override
                                public void accept(Object o) {
                                    //根据返回的错误码 未知原因 吐出原电池
                                    BatteryDataModel batteryDataModel = batteryDataModels.get(Integer.parseInt(door) - 1);
                                    if (batteryDataModel != null) {
                                        DoorController.getInstance().openDoor(batteryDataModel);
                                    }
//                            push("错误码弹出电池：E1001", door);
//                            SystemClock.sleep(6000);
                                    isReplaceBattery = false;

                                    Bundle bundle10s = new Bundle();
                                    Message message10s = new Message();
                                    message10s.what = 3;
                                    bundle10s.putInt("randomEmptyDoor", Integer.parseInt(door));
                                    message10s.setData(bundle10s);
                                    openDoorButtonHandler.sendMessage(message10s);

                                    Message message1 = new Message();
                                    message1.what = 1;
                                    Bundle bundle1 = new Bundle();
                                    bundle1.putInt("randomEmptyDoor", Integer.parseInt(door));
                                    message1.setData(bundle1);
                                    openDoorButtonHandler.sendMessageDelayed(message1, 60000);
                                }
                            });
                        }
                        //设置挂起状态
                        AN_IS_RUN = 0;
                    } else if (code.equals("1")) {
                        JSONObject returnData = jsonObject.getJSONObject("data");
                        final String utype = returnData.getString("utype");
                        final String uid32 = returnData.getString("uid32");
                        if (!TextUtils.isEmpty(utype)) {
                            cost_bi.post(new Runnable() {
                                @Override
                                public void run() {
                                    cost_bi.setText(utype);
                                }
                            });
                        }
                        outLineExchangeUID(Integer.parseInt(door), uid32);
                    }

                } catch (JSONException e) {
                    isReplaceBattery = false;
                    e.printStackTrace();
                    //设置挂起状态
                    AN_IS_RUN = 0;
                }

            }
        }
    };
    IFHttpOutLineCheckOldBindListener ifHttpOutLineCheckOldBindListener = new IFHttpOutLineCheckOldBindListener() {
        @Override
        public void onHttpOutLineCheckOldBindResult(String in_door, String code, String str, String data) {
            writeLocalLog(data + "");
            if (code.equals("-1")) {  //开启离线换电
                showDialogInfo("该电池未绑定，将被回收，如有问题请拨打客服电话！", "10", "1");
                //设置充电状态
                AN_IS_RUN = 0;
                isReplaceBattery = false;
            } else {

                try {
                    JSONObject jsonObject = new JSONObject(data);
                    if (code.equals("0")) {
                        isReplaceBattery = false;
                        String errno = jsonObject.getString("errno");
                        String msg = jsonObject.getString("msg");

                        if (errno.equals("E2001")) {
                            showDialogInfo(msg, "10", "1");
                        }
                    } else if (code.equals("1")) {
                        JSONObject returnData = jsonObject.getJSONObject("data");
                        String uid32 = returnData.getString("uid32");
                        if (!TextUtils.isEmpty(in_door)) {
                            firstSetUidAndExchange(Integer.parseInt(in_door), uid32);
                        }
                    } else if (code.equals("2")) {
                        JSONObject returnData = jsonObject.getJSONObject("data");
                        String uid32 = returnData.getString("uid32");
                        if (!TextUtils.isEmpty(in_door)) {
                            firstSetUidAndExchange(Integer.parseInt(in_door), uid32);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    //设置充电状态
                    AN_IS_RUN = 0;
                    isReplaceBattery = false;
                }

            }
        }

        /**
         * 首次设置Uid并且直接换电
         * @param currentDoor
         * @param uid
         */
        private void firstSetUidAndExchange(final int currentDoor, final String uid) {
            LogUtil.I("线程：" + Thread.currentThread().getName());
            if (isValidDoor(currentDoor)) {
                //写入UID
                int tryWriteCount = 3;
                final long intervalTime = 3000;
                try {
                    while (tryWriteCount > 0) {
                        writeBatteryCheckCode(uid, currentDoor);
                        Thread.sleep(intervalTime);
                        if (UIDS[currentDoor - 1].equals(uid)) {
                            break;
                        }
                        tryWriteCount--;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    isReplaceBattery = false;
                }

                //判断确认写入UID
                if (UIDS[currentDoor - 1].equals(uid)) {
                    LogUtil.I("判断确认写入UID");
                    new ExchangeBarThread(currentDoor).run();
                } else {
                    showDialogInfo("换电失败，正在弹出电池，请重试！，如有问题请拨打客服电话", "7", "1");

                    BatteryDataModel batteryDataModel = batteryDataModels.get(currentDoor - 1);
                    if (batteryDataModel != null) {
                        DoorController.getInstance().openDoor(batteryDataModel);
                    }
//                    push("写入要弹出的电池ID失败，弹出电池！", currentDoor + "");
//                    SystemClock.sleep(6000);
                    isReplaceBattery = false;
                }
            }
        }
    };

    OpenLongLink.IFHttpOpenLongLinkLinstener ifHttpOpenLongLinkLinstener;

    //右上角电柜编号
    public static String cabid_title = "";
    //本地还剩多少未上传换电次数
    private int localExchanges = 0;
    //4G信号强度
    private int dbm = 0;
    //sharedPreferences - 电柜保存参数
    private CabInfoSp cabInfoSp;
    //sharedPreferences - 禁用参数保存
    private ForbiddenSp forbiddenSp;


    //录制视频
    private MoviesUnit moviesUnit = null;
    private FrameLayout camera_preview;
    private LinearLayout camera_preview_panel;
    private TextView tv_cameraErrorInfo;
    private LinearLayout uploadmoviespanel;
    private ProgressBar uploadPbar;

    private WebView adv_webview;

    private final ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(@NonNull Network network) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    t_9.setText("在线");
                    if (!TextUtils.isEmpty(cabid_title)) {
                        cab_id.setText(cabid_title);
                        localLog = new LocalLog(A_Main2.this, cabid_title);
                    }
                }
            });
        }

        @Override
        public void onLost(Network network) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    b_3.setImageResource(R.drawable.b9);
                    t_9.setText("离线");
                    if (!TextUtils.isEmpty(cabid_title)) {
                        cab_id.setText("D" + cabid_title);
                    }
                }
            });
        }
    };
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        int times = 3;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_TIME_TICK)) {
                --times;
                if (times < 0) {
                    times = 3;
                    startNewTask(new Consumer() {
                        @Override
                        public void accept(Object o) {
                            //每3分钟清除一下UID
                            clearUid();
                        }
                    });
                }
            }
        }
    };

    private volatile boolean isReplaceBattery;

    private byte[][] uidBytes = new byte[9][8];
    public static byte pushrodActSetTime = (byte) 0x96;
    public static final List<BatteryDataModel> batteryDataModels = new ArrayList<>(9);

    private StringBuilder parserDcdcErrorCodeSb = new StringBuilder();

    //获得4g卡信息后的处理
    private final Handler onCreateHandler = new Handler() {
        private boolean isInitFinish;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isInitFinish) {
                return;
            }
//            isInitFinish = true;
            init();

            //设置400显示
            tel_text.setText(cabInfoSp.getTelNumber());
            LogUtil.I("初始化网络：   电话-" + cabInfoSp.getTelNumber());

            startNewTask(new Consumer() {
                @Override
                public void accept(Object o) {
                    try {
                        //获取电柜显示电话
                        HttpGetTel httpGetTel = new HttpGetTel(cabinetID, ifHttpGetTelLinstener);
                        httpGetTel.start();

                        HttpGetQcode httpGetQcode = new HttpGetQcode(cabinetID, ifHttpGetQcodeLinstener);
                        httpGetQcode.start();

                        deleteDatabase("WebView.db");
                        if (adv_webview != null) {
                            adv_webview.clearHistory();
                            adv_webview.clearFormData();
                        }

                        getCacheDir().delete();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;

        setContentView(R.layout.activity_main_1080p_new2);
        cabInfoSp = new CabInfoSp(activity);
        forbiddenSp = new ForbiddenSp(activity);
        pushrodActSetTime = cabInfoSp.getpushrodActSetTime();

        isAutoSetCurrentDetection = cabInfoSp.optAutoSetCurrentDetectionStatus();

        //获取root权限
        RootCommand rootCommand = new RootCommand();
        rootCommand.RootCommandStart("chmod 777 " + getPackageCodePath());

//        String packegName_11 = "pm uninstall -k client.NewElectric.app11";
//        String packegName_10 = "pm uninstall -k client.NewElectric.app10";
//        rootCommand.execRootCmd(packegName_11);
//        rootCommand.execRootCmd(packegName_10);

        if (MyApplication.getInstance().serialAndCanPortUtils == null) {
            MyApplication.getInstance().initSerialAndCanPortUtils();
            MyApplication.getInstance().addActivity(this);
        }

        //zhu
        MyApplication.getInstance().addListener(SensorController.getInstance());
        MyApplication.getInstance().addListener(this);
        MyApplication.getInstance().addListener(CurrentDetectionController.getInstance());
        DcDataLink.getInstance().setIFHttpOpenLongLinkLinstener(this);

        //设置电流板参数
        CurrentDetectionController.getInstance().enabledCurrentDetection(cabInfoSp.optCurrentThreshold(), 800, 1000);

        //守护进程的service
        Intent intent = new Intent(activity, MyService.class);
        PendingIntent sender = PendingIntent.getService(activity, 0, intent, 0);
        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000, sender);

        handler();
        findView();
        main();

        progressDialog_4 = new ProgressDialog_4(activity, dialog_panel, dialog_time, dialog_info, speakHandler, info_panel);
        progressDialog_4.show("电柜正在初始化，请稍候！", 30, 1);

        //开机延迟获取4g卡准备
        Thread onCreateThread = new Thread() {
            int count = 0;

            @SuppressLint("MissingPermission")
            @Override
            public void run() {
                super.run();

                //创建数据库 数据表
                mHelper = new DBHelper(activity);
                mDatabase = mHelper.getWritableDatabase();
                LogUtil.I("数据库创建：" + mDatabase);
                while (onCreateThreadCode == 0) {

                    try {
                        sleep(1000);

                        TelephonyManager mTelephonyMgr = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
                        String imsi = "";
                        imsi = mTelephonyMgr.getSubscriberId();
                        if (imsi == null || imsi.equals("") || imsi.equals("null")) {

                        } else {
                            cabInfoSp.setCabinetNumber(imsi);
                            cabinetID = cabInfoSp.getCabinetNumber();
                            onCreateThreadCode = 1;
                            showDialogInfo("电柜初始化成功！", "3", "2");
                            jumpAppProgram();
                            onCreateHandler.sendMessage(new Message());
                        }

                        count = count + 1;
                        if (count > 30) {
                            showDialogInfo("初始化失败，未检测到4G卡，正在重启系统", "10", "2");
                            sleep(10000);
                            onCreateThreadCode = 1;
                            DeviceSwitchController.getInstance().control(DeviceSwitcher.CMD.ANDROID_12V_REBOOT);
                            break;
                        } else {
                            LogUtil.I("网络：   正在检测4G卡 imsi - " + imsi);
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        onCreateThreadCode = 1;
                        DeviceSwitchController.getInstance().control(DeviceSwitcher.CMD.ANDROID_12V_REBOOT);
                    }

                    String isHeat = cabInfoSp.optHeatMode();
                    LogUtil.I("加热模式：" + isHeat);
                    if (!TextUtils.isEmpty(isHeat)) {
                        LogUtil.I("加热模式：" + isHeat);
                        cabInfoSp.saveHeatMode(isHeat);
                        switch (isHeat) {
                            case "1"://加热
                                LogUtil.I("加热模式：加热");
                                SettingConfig.getInstance().setDefaultHeating();
                                break;
                            case "2"://自动
                                LogUtil.I("加热模式：自动");
                                SettingConfig.getInstance().autoHeating();
                                break;
                            case "-1"://停止
                                LogUtil.I("加热模式：停止");
                                SettingConfig.getInstance().setHeatingUnable();
                                break;
                        }
                    }
                }
            }
        };
        onCreateThread.start();

        // TODO: 2020/3/29 更新传感器UI
        SensorController.getInstance().setUpdateConsumer(new Consumer<SensorDataBean>() {
            private byte openDoorButton;
            private volatile boolean isOpenProcess;

            private int ammeterPower1;

            @Override
            public void accept(SensorDataBean sensorDataBean) {

                // TODO: 2020/3/29 传感器数据->UI(注意此回调是在子线程)
                if (sensorDataBean != null) {
                    if (sensorDataBean.getWaterLevel() < 1.5f || sensorDataBean.getWaterLevel2() < 1.5f) {
                        FireSwitchController.getInstance().control(1);
                    }

                    evnSoftwareVersion = sensorDataBean.getSoftwareVersion() + "";
                    //湿度参数,由于传感器没有所有显示0
                    hun_str = sensorDataBean.getTemperature3_String();//环境
                    tem_str = sensorDataBean.getTemperature1_String();//柜内
                    ammeterTotalPower = sensorDataBean.getAmmeterTotalPower_String();
                    ammeterPower = sensorDataBean.getAmmeterPower_String();

                    int ammeterPower2 = (int) sensorDataBean.getAmmeterTotalPower();
                    if (ammeterPower2 > ammeterPower1 || ammeterPower2 < 0) {
                        ammeterPower1 = ammeterPower2;
                        writeLocalLog("电表耗电量：" + ammeterTotalPower);
                    }
                    if (isAutoSetCurrentDetection) {
                        int tem = 0;
                        float doorAndDoorTemperature = sensorDataBean.getTemperature2();//舱门之间的温度
                        float innerTemperature = sensorDataBean.getTemperature1();//柜内温度
                        float envTemperature = sensorDataBean.getTemperature3();//柜外温度
                        // TODO: 2021/1/1 三个温度取最低的有效温度
                        if (doorAndDoorTemperature >= -40) {
                            tem = (int) doorAndDoorTemperature;
                        }
                        if (innerTemperature >= -40) {
                            tem = (int) Math.min(tem, innerTemperature);
                        }
                        if (envTemperature >= -40) {
                            tem = (int) Math.min(tem, envTemperature);
                        }
                        if (temperature != tem) {
                            temperature = tem;
                            writeLocalLog("自动设置电流阈值温度：" + tem);
                            CurrentDetectionController.getInstance().setCurrentDetection(temperature);
                            cabInfoSp.setCurrentThreshold(CurrentDetectionController.getInstance().getCurrentThreshold());
                        }
                    }

                    if(isAutoControlAirfan)
                    {
                        int waitPower = ACDC_gonglv[0] * 10 + ACDC_gonglv[1] * 10 - remainingTotalPower2[0] * 10;
                        if (waitPower >= 500) {
                            //在线,大于-40度认为在线
                            if (sensorDataBean.getTemperature1() >= -40) {

                                if (sensorDataBean.getTemperature1() >= 40 && sensorDataBean.getTemperature1() <= 50) {
                                    if (sensorDataBean.getAirFan1Status() == 1) {
                                        DeviceSwitchController.getInstance().controlAirFan(DeviceSwitcher.AIR_FAN._1_OPEN);
                                    }
                                    if (sensorDataBean.getAirFan2Status() == 0) {
                                        DeviceSwitchController.getInstance().controlAirFan(DeviceSwitcher.AIR_FAN._2_CLOSE);
                                    }
                                } else if (sensorDataBean.getTemperature1() > 50) {
                                    if (sensorDataBean.getAirFan1Status() == 1) {
                                        DeviceSwitchController.getInstance().controlAirFan(DeviceSwitcher.AIR_FAN._1_OPEN);
                                    }
                                    if (sensorDataBean.getAirFan2Status() == 1) {
                                        DeviceSwitchController.getInstance().controlAirFan(DeviceSwitcher.AIR_FAN._2_OPEN);
                                    }
                                } else {
                                    if (sensorDataBean.getAirFan1Status() == 0) {
                                        DeviceSwitchController.getInstance().controlAirFan(DeviceSwitcher.AIR_FAN._1_CLOSE);
                                    }
                                    if (sensorDataBean.getAirFan2Status() == 0) {
                                        DeviceSwitchController.getInstance().controlAirFan(DeviceSwitcher.AIR_FAN._2_CLOSE);
                                    }
                                }
                            } else {
                                //不在线，就是温度传感器不在线
                                if (waitPower <= 3000) {
                                    //小于3000w开一个，否则开两个
                                    if (sensorDataBean.getAirFan1Status() == 1) {
                                        DeviceSwitchController.getInstance().controlAirFan(DeviceSwitcher.AIR_FAN._1_OPEN);
                                    }
                                    if (sensorDataBean.getAirFan2Status() == 0) {
                                        DeviceSwitchController.getInstance().controlAirFan(DeviceSwitcher.AIR_FAN._2_CLOSE);
                                    }
                                }
                                if (waitPower > 3000) {
                                    if (sensorDataBean.getAirFan1Status() == 1) {
                                        DeviceSwitchController.getInstance().controlAirFan(DeviceSwitcher.AIR_FAN._1_OPEN);
                                    }
                                    if (sensorDataBean.getAirFan2Status() == 1) {
                                        DeviceSwitchController.getInstance().controlAirFan(DeviceSwitcher.AIR_FAN._2_OPEN);
                                    }
                                }
                            }

                        } else {
                            // TODO: 2020/8/6小于500W都关闭
                            if (sensorDataBean.getAirFan1Status() == 0) {
                                DeviceSwitchController.getInstance().controlAirFan(DeviceSwitcher.AIR_FAN._1_CLOSE);
                            }
                            if (sensorDataBean.getAirFan2Status() == 0) {
                                DeviceSwitchController.getInstance().controlAirFan(DeviceSwitcher.AIR_FAN._2_CLOSE);
                            }
                        }
                    }

//                    if(sensorDataBean.getWaterLevel())

                    if (!isInitFinish) {
                        writeLocalLog("按钮开门日志-isInitFinish：" + isInitFinish);
                        return;
                    }
                    //没有升级任务才可以处理按钮开门
                    if (isReplaceBattery) {
                        writeLocalLog("按钮开门日志-isReplaceBattery：" + isReplaceBattery);
                        return;
                    }
                    if ((upgrading != null && !upgrading.isEmpty())) {
                        writeLocalLog("按钮开门日志-upgrading：" + upgrading);
                        return;
                    }

                    final byte currentButtonStatus = sensorDataBean.getButtonStatus();
                    if (openDoorButton == currentButtonStatus) {
                        if (isOpenProcess) {
//                            writeLocalLog("按钮开门日志-isOpenProcess：" + isOpenProcess);
                            return;
                        }
                        if (obtainEmptyDoor() == -1) {
                            if (!textToSpeech.isSpeaking()) {
                                writeLocalLog("没有可以打开的空仓门");
                                showDialogInfo("没有可以打开的空仓门", "10", "1");
                            }
                            isOpenProcess = false;
                        } else if (obtainOpenedEmptyDoor() != -1) {
                            if (!textToSpeech.isSpeaking()) {
                                writeLocalLog("当前已经有打开的空仓门");
                                showDialogInfo("当前已经有打开的空仓门", "10", "1");
                            }
                            isOpenProcess = false;
                        } else {
                            isOpenProcess = true;
                            syncControl(new Consumer() {
                                @Override
                                public void accept(Object o) {
                                    boolean isOpened = false;
                                    writeLocalLog("准备打开空仓门，请注意安全！");
                                    showDialogInfo("准备打开空仓门，请注意安全！", "10", "1");

                                    BatteryDataModel batteryDataModel = obtainRandomEmptyDoorModel();
                                    if (batteryDataModel != null) {
                                        DoorController.getInstance().openDoor(batteryDataModel);
                                        SystemClock.sleep(3000);
                                        writeLocalLog("打开" + batteryDataModel.doorNumber + "空仓门,侧微动：" + batteryDataModel.isSideMicroswitchPressed());
                                        if (batteryDataModel.isSideMicroswitchPressed()) {
                                            batteryDataModel = obtainRandomEmptyDoorModel();
                                            DoorController.getInstance().openDoor(batteryDataModel);
                                            SystemClock.sleep(3000);
                                            writeLocalLog("再次打开" + batteryDataModel.doorNumber + "空仓门,侧微动：" + batteryDataModel.isSideMicroswitchPressed());
                                            LogUtil.I("打开舱门" + batteryDataModel.isSideMicroswitchPressed());
                                            if (batteryDataModel.isSideMicroswitchPressed()) {
                                                writeLocalLog("打开" + batteryDataModel.doorNumber + "空仓门,侧微动：" + batteryDataModel.isSideMicroswitchPressed());
                                                showDialogInfo("打开仓门失败，请重试", "10", "1");
                                            } else {
                                                isOpened = true;
                                            }
                                        } else {
                                            writeLocalLog("打开" + batteryDataModel.doorNumber + "空仓门成功,侧微动：" + batteryDataModel.isSideMicroswitchPressed());
                                            isOpened = true;
                                        }
                                    }

                                    if (isOpened) {
                                        Bundle bundle = new Bundle();
                                        Message message1 = new Message();
                                        message1.what = 1;
                                        bundle.putInt("randomEmptyDoor", batteryDataModel.doorNumber);
                                        message1.setData(bundle);
                                        openDoorButtonHandler.sendMessageDelayed(message1, TimeUnit.MINUTES.toMillis(1));
                                    }
                                    isOpenProcess = false;
                                }
                            });
                        }
                    }
                }
            }
        });

        currentNetDBM = new CurrentNetDBM(getApplicationContext(), this);

        setRebootAppTime();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                final int[] bs = {FireControlSwitcher.SWITCH._01, FireControlSwitcher.SWITCH._02,
//                        FireControlSwitcher.SWITCH._03, FireControlSwitcher.SWITCH._04,
//                        FireControlSwitcher.SWITCH._05, FireControlSwitcher.SWITCH._06,
//                        FireControlSwitcher.SWITCH._07, FireControlSwitcher.SWITCH._08,
//                        FireControlSwitcher.SWITCH._09, FireControlSwitcher.SWITCH._10
//                };
//
//                SystemClock.sleep(20000);
//                while (true) {
//                    for (int i : bs) {
//                        LogUtil.I(i+ ":消防继电器");
//                        FireSwitchController.getInstance().control(i);
//                        SystemClock.sleep(5000);
//                    }
//                }
//            }
//        }).start();
        registerNetworkCallback();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(receiver, filter);

        initErrorHandler();

        final long rodActionTime = cabInfoSp.getpushrodActSetTime();
        batteryDataModels.clear();
        for (int i = 1; i <= 9; i++) {
            BatteryDataModel batteryDataModel = new BatteryDataModel(i);
            batteryDataModel.setRodActionTime(rodActionTime);
            batteryDataModels.add(batteryDataModel);
        }


    }


    private void registerNetworkCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE))
                    .registerDefaultNetworkCallback(networkCallback);
        } else {
            ((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE))
                    .requestNetwork(new NetworkRequest.Builder().build(), networkCallback);
        }
    }

    private void findView() {
        bar_r_image_1 = (ImageView) findViewById(R.id.bar_r_image_1);
        bar_r_image_2 = (ImageView) findViewById(R.id.bar_r_image_2);
        bar_r_image_3 = (ImageView) findViewById(R.id.bar_r_image_3);
        bar_r_image_4 = (ImageView) findViewById(R.id.bar_r_image_4);
        bar_r_image_5 = (ImageView) findViewById(R.id.bar_r_image_5);
        bar_r_image_6 = (ImageView) findViewById(R.id.bar_r_image_6);
        bar_r_image_7 = (ImageView) findViewById(R.id.bar_r_image_7);
        bar_r_image_8 = (ImageView) findViewById(R.id.bar_r_image_8);
        bar_r_image_9 = (ImageView) findViewById(R.id.bar_r_image_9);
        bar_r_image_10 = (ImageView) findViewById(R.id.bar_r_image_10);
        bar_r_image_11 = (ImageView) findViewById(R.id.bar_r_image_11);
        bar_r_image_12 = (ImageView) findViewById(R.id.bar_r_image_12);
        bar_r_images = new ImageView[]{bar_r_image_1, bar_r_image_2, bar_r_image_3, bar_r_image_4, bar_r_image_5, bar_r_image_6, bar_r_image_7, bar_r_image_8, bar_r_image_9, bar_r_image_10, bar_r_image_11, bar_r_image_12};
        bar_r_text_1 = (TextView) findViewById(R.id.bar_r_text_1);
        bar_r_text_2 = (TextView) findViewById(R.id.bar_r_text_2);
        bar_r_text_3 = (TextView) findViewById(R.id.bar_r_text_3);
        bar_r_text_4 = (TextView) findViewById(R.id.bar_r_text_4);
        bar_r_text_5 = (TextView) findViewById(R.id.bar_r_text_5);
        bar_r_text_6 = (TextView) findViewById(R.id.bar_r_text_6);
        bar_r_text_7 = (TextView) findViewById(R.id.bar_r_text_7);
        bar_r_text_8 = (TextView) findViewById(R.id.bar_r_text_8);
        bar_r_text_9 = (TextView) findViewById(R.id.bar_r_text_9);
        bar_r_text_10 = (TextView) findViewById(R.id.bar_r_text_10);
        bar_r_text_11 = (TextView) findViewById(R.id.bar_r_text_11);
        bar_r_text_12 = (TextView) findViewById(R.id.bar_r_text_12);
        bar_r_texts = new TextView[]{bar_r_text_1, bar_r_text_2, bar_r_text_3, bar_r_text_4, bar_r_text_5, bar_r_text_6, bar_r_text_7, bar_r_text_8, bar_r_text_9, bar_r_text_10, bar_r_text_11, bar_r_text_12};

        bar_not_image_1 = (ImageView) findViewById(R.id.bar_not_image_1);
        bar_not_image_2 = (ImageView) findViewById(R.id.bar_not_image_2);
        bar_not_image_3 = (ImageView) findViewById(R.id.bar_not_image_3);
        bar_not_image_4 = (ImageView) findViewById(R.id.bar_not_image_4);
        bar_not_image_5 = (ImageView) findViewById(R.id.bar_not_image_5);
        bar_not_image_6 = (ImageView) findViewById(R.id.bar_not_image_6);
        bar_not_image_7 = (ImageView) findViewById(R.id.bar_not_image_7);
        bar_not_image_8 = (ImageView) findViewById(R.id.bar_not_image_8);
        bar_not_image_9 = (ImageView) findViewById(R.id.bar_not_image_9);
        bar_not_images = new ImageView[]{bar_not_image_1, bar_not_image_2, bar_not_image_3, bar_not_image_4, bar_not_image_5, bar_not_image_6, bar_not_image_7, bar_not_image_8, bar_not_image_9};

        cab_id = (TextView) this.findViewById(R.id.cab_id);
        version = (TextView) this.findViewById(R.id.version);
        version.setText("版本：" + MyApplication.cab_version + "");
        cost_bi = (TextView) this.findViewById(R.id.cost_bi);
        info_panel = (LinearLayout) this.findViewById(R.id.dialog_panel);
        black_1 = (ImageView) this.findViewById(R.id.black_1);
        black_2 = (ImageView) this.findViewById(R.id.black_2);
        up_bar_1 = (ImageView) this.findViewById(R.id.up_bar_1);
        up_bar_2 = (ImageView) this.findViewById(R.id.up_bar_2);
        up_bar_1_text = (TextView) this.findViewById(R.id.up_bar_1_text);
        up_bar_2_text = (TextView) this.findViewById(R.id.up_bar_2_text);
        up_bar_2_panel = (FrameLayout) this.findViewById(R.id.up_bar_2_panel);
        up_bar_1_panel = (FrameLayout) this.findViewById(R.id.up_bar_1_panel);
        now_address = (TextView) this.findViewById(R.id.now_address);
        cost = (LinearLayout) this.findViewById(R.id.cost);
        rent_bar_qcode = (ImageView) this.findViewById(R.id.rent_bar_qcode);
        download_qcode = (ImageView) this.findViewById(R.id.download_qcode);
        download_qcode.setOnClickListener(this);
        startNewTask(new Consumer() {
            @Override
            public void accept(Object o) {
                try {
                    LogUtil.I("当前线程：" + Thread.currentThread().getName());
                    final Bitmap bitmap = Unit.generateBitmap("http://app.halouhuandian.com/App/jump", 400, 400);
                    if (bitmap != null && !bitmap.isRecycled()) {
                        updateHander.post(new Runnable() {
                            @Override
                            public void run() {
                                download_qcode.setImageBitmap(bitmap);
                            }
                        });
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        now_time = (TextView) this.findViewById(R.id.now_time);

        b_3 = (ImageView) this.findViewById(R.id.b_3);
        t_5 = this.findViewById(R.id.t_5);
        t_6 = this.findViewById(R.id.t_6);
        t_7 = this.findViewById(R.id.t_7);
        t_8 = this.findViewById(R.id.t_8);
        t_9 = this.findViewById(R.id.t_9);

        tel_text = this.findViewById(R.id.tel_text_1);
        camera_preview = findViewById(R.id.camera_preview);
        camera_preview_panel = this.findViewById(R.id.camera_preview_panel);
        tv_cameraErrorInfo = this.findViewById(R.id.camera_preview_panel_text);
        uploadmoviespanel = this.findViewById(R.id.uploadmoviespanel);
        uploadPbar = this.findViewById(R.id.p_bar);

        dialog_panel = this.findViewById(R.id.dialog_panel);
        dialog_info = this.findViewById(R.id.dialog_info);
        dialog_time = this.findViewById(R.id.dialog_time);
        adv_webview = this.findViewById(R.id.adv_webview);

        cabid_title = cabInfoSp.getLongLinkNumber();
        if (!TextUtils.isEmpty(cabid_title)) {
            localLog = new LocalLog(this, cabid_title);
            cab_id.setText("D" + cabid_title);
        }

        String address = cabInfoSp.getAddress();
        if (!TextUtils.isEmpty(cabid_title)) {
            now_address.setText(address);
        }
        //设置时间
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    setTimeHandler.sendMessage(new Message());
                    SystemClock.sleep(1000);
                }
            }
        }).start();
    }

    /**
     * @param canData 返回的can数据
     */
    //:todo  can数据返回（包含电池信息 错误码信息 ACDC信息 DCDC信息处理）
    @Override
    public void onCanResultApp(byte[] canData) {
        if (!A_Main2.upgrading.isEmpty()) {
            return;
        }
        byte[] return_byte = canData;

        String return_id = String.format("%02x", new Object[]{return_byte[3]}).toUpperCase() + "" + String.format("%02x", new Object[]{return_byte[2]}).toUpperCase() + "" + String.format("%02x", new Object[]{return_byte[1]}).toUpperCase() + "" + String.format("%02x", new Object[]{return_byte[0]}).toUpperCase();

        int door = Integer.parseInt(return_id.substring(6, 8), 16);
        if (door < 1) {
            return;
        }

        //返回的为拼装的item数值
        byte[] can_item_data = new byte[]{return_byte[8], return_byte[9], return_byte[10], return_byte[11], return_byte[12], return_byte[13], return_byte[14], return_byte[15]};
        //ACDC返回数据帧
        //:todo  电池信息处理
        if (return_id.substring(0, 6).equals("980265")) {
            int doorIndex = door - 1;
            //返回的8位数据的第1位
            int head_can_item_data = can_item_data[0];
            if (head_can_item_data == 1) {
                bar_all_2[door - 1][0] = can_item_data;
                SMALLS[doorIndex] = can_item_data[1] & 0x0F;
                if (doorIndex < batteryDataModels.size()) {
                    BatteryDataModel batteryDataModel = batteryDataModels.get(doorIndex);
                    if (batteryDataModel != null) {
                        batteryDataModel.setSideMicroswitch((byte) SMALLS[doorIndex]);
                    }
                }
            } else if (head_can_item_data == 2) {
                bar_all_2[door - 1][1] = can_item_data;
            } else if (head_can_item_data == 3) {
                bar_all_2[door - 1][2] = can_item_data;
            } else if (head_can_item_data == 4) {
                bar_all_2[door - 1][3] = can_item_data;
            } else if (head_can_item_data == 5) {
                bar_all_2[door - 1][4] = can_item_data;
            } else if (head_can_item_data == 6) {
                bar_all_2[door - 1][5] = can_item_data;
            } else if (head_can_item_data == 7) {
                bar_all_2[door - 1][6] = can_item_data;
            } else if (head_can_item_data == 8) {
                bar_all_2[door - 1][7] = can_item_data;
            } else if (head_can_item_data == 9) {
                bar_all_2[door - 1][8] = can_item_data;
            } else if (head_can_item_data == 10) {
                bar_all_2[door - 1][9] = can_item_data;
            } else if (head_can_item_data == 11) {
                bar_all_2[door - 1][10] = can_item_data;
            } else if (head_can_item_data == 12) {
                bar_all_2[door - 1][11] = can_item_data;
            } else if (head_can_item_data == 13) {
                bar_all_2[door - 1][12] = can_item_data;
            } else if (head_can_item_data == 14) {
                bar_all_2[door - 1][13] = can_item_data;
            } else if (head_can_item_data == 15) {
                bar_all_2[door - 1][14] = can_item_data;
            } else if (head_can_item_data == 16) {
                bar_all_2[door - 1][15] = can_item_data;
                System.arraycopy(can_item_data, 1, uidBytes[door - 1], 0, 7);
            } else if (head_can_item_data == 17) {
                bar_all_2[door - 1][16] = can_item_data;
                System.arraycopy(can_item_data, 1, uidBytes[door - 1], 7, 1);
            } else if (head_can_item_data == 18) {

                bar_all_2[door - 1][17] = can_item_data;

                int is_incloud_null = 0;
                for (int i = 0; i < bar_all_2[door - 1].length; i++) {
                    if (bar_all_2[door - 1][i][0] == 0) {
                        is_incloud_null = 1;
                    } else {
                    }
                }

                if (is_incloud_null == 0) {
                    byte[] return_order = new byte[126];
                    byte[][] arr_t = bar_all_2[door - 1];
                    for (int i = 0; i < arr_t.length; i++) {
                        for (int j = 0; j < arr_t[i].length - 1; j++) {
                            return_order[(i * 7) + j] = arr_t[i][j + 1];
                        }
                    }
                    if (doorIndex >= 0) {
                        lastDataDateAttrs[doorIndex] = System.currentTimeMillis();
                    }

                    //电池信息解析
                    new DataHandleBARThread(door, return_order).start();
                } else {
//                    LogUtil.I("CAN - 返回 - BAR ：  " + door + " - 丢帧");
                }
                byte[][] arr_clear = {{0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0}};
                bar_all_2[door - 1] = arr_clear;
            }
        }
        //DCDC返回数据帧
        if (return_id.substring(0, 6).equals("980465")) {
            //返回的第几个
            new DataHandleDCDCThread(door, can_item_data).start();
        }
        //错误码
        if (return_id.substring(0, 6).equals("980665")) {
            if (door == 0x51 || door == 0x52) {
                ACDC_ERROR_state_01[door - 0x51] = (can_item_data[1] & 0xFF) | ((can_item_data[2] & 0xFF) << 8) | ((can_item_data[2] & 0xFF) << 16) | ((can_item_data[3] & 0xFF) << 24);
            } else {
                //:todo 错误码处理
                try {
                    int top = can_item_data[0];
                    if (top == 1) {
                        if (door == 81) {
                            ERROR_state_01[9] = PubFunction.ByteArrToHex(can_item_data);
                        } else if (door == 82) {
                            ERROR_state_01[10] = PubFunction.ByteArrToHex(can_item_data);
                        } else if (door == 83) {
                            ERROR_state_01[11] = PubFunction.ByteArrToHex(can_item_data);
                        } else {
                            ERROR_state_01[door - 1] = PubFunction.ByteArrToHex(can_item_data);
                        }
                    } else if (top == 2) {
                        if (door == 81) {
                            ERROR_state_02[9] = PubFunction.ByteArrToHex(can_item_data);
                        } else if (door == 82) {
                            ERROR_state_02[10] = PubFunction.ByteArrToHex(can_item_data);
                        } else if (door == 83) {
                            ERROR_state_02[11] = PubFunction.ByteArrToHex(can_item_data);
                        } else {
                            ERROR_state_02[door - 1] = PubFunction.ByteArrToHex(can_item_data);
                        }
                    } else if (top == 3) {
                        if (door == 81) {
                            ERROR_state_03[9] = PubFunction.ByteArrToHex(can_item_data);
                        } else if (door == 82) {
                            ERROR_state_03[10] = PubFunction.ByteArrToHex(can_item_data);
                        } else if (door == 83) {
                            ERROR_state_03[11] = PubFunction.ByteArrToHex(can_item_data);
                        } else {
                            ERROR_state_03[door - 1] = PubFunction.ByteArrToHex(can_item_data);
                        }
                    }
//                LogUtil.I("CAN - 返回 - ERROR ： " + PubFunction.ByteArrToHex(can_item_data));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        //ACDC返回数据帧
        switch (return_id.substring(0, 8)) {
            case "98116551":
            case "98116552":
            case "98116553":
                new DataHandleACDCThread(door, can_item_data).start();
                break;
        }

        //ACDC返回数据帧
        if (return_id.substring(0, 6).equals("981265")) {
            int door_int = door - 81;
            ACDC_SV[door_int] = (can_item_data[0] & 0xFF) + "";
            ACDC_HV[door_int] = (can_item_data[1] & 0xFF) + "";

            byte v = (byte) (can_item_data[2] & 0xFF);
            String whichAc = v == 0 ? "辅机" : v == 1 ? "主机" : "";
            whichACDC[door_int] = whichAc;

            short v2 = (short) ((can_item_data[3] & 0xFF) | ((can_item_data[4] & 0xFF) << 8));
            remainingTotalPower2[door_int] = (short) (v2 * 10);
            remainingTotalPower[door_int] = ((float) v2 / 100) + "KW";

            switch (can_item_data[5] & 0xFF) {
                case 0x00:
                    sleepStatus[door_int] = "未休眠";
                    break;
                case 0x01:
                    sleepStatus[door_int] = "进入休眠";
                    break;
                case 0x02:
                    sleepStatus[door_int] = "休眠后已经唤醒";
                    break;
                default:
                    sleepStatus[door_int] = "无";
                    break;
            }
        }
    }


    //:todo  电池信息解析
    //怕单一线程时间内处理不了 多线程处理数据 每返回一帧数据 起一个线程
    private class DataHandleBARThread extends Thread {
        private byte[] data;
        private int i_address;

        public DataHandleBARThread(int address, byte[] data) {
            this.i_address = address;
            this.data = data;
        }

        @Override
        public void run() {
            super.run();
            if (i_address <= 0) {
                return;
            }

            //电池查询命令返回
            if (data.length == 126 && AN_IS_RUN == 0 && upgrading.isEmpty()) {
                //替换索引值
                Map<String, String> map = new A_M_Analysis().analysisData_BAR(data);
                final String door_state = map.get("door");
                // 检测到电池的提示
                boolean isCheckedBattery = (DOORS[i_address - 1] == 0 && door_state.equals("1"));

                //更新ui
                if (!bar_r_texts[i_address - 1].getText().equals(map.get("xiangduibaifenbi"))) {
                    if (setUiHandler != null) {
                        Message message_ui = setUiHandler.obtainMessage();
                        Bundle bundle_ui = new Bundle();
                        bundle_ui.putInt("address", i_address);
                        message_ui.setData(bundle_ui);
                        setUiHandler.sendMessage(message_ui);
                    }
                }
//                changeQcodeHandler.sendMessage(new Message());

                //仓门微动1
                SMALLS[i_address - 1] = Integer.parseInt(map.get("small"));
                //仓门微动2
                DOORS[i_address - 1] = Integer.parseInt(map.get("door"));
                //推杆
                PUSHS[i_address - 1] = Integer.parseInt(map.get("push"));
                //电池壳温度
                TEM_1[i_address - 1] = Integer.parseInt(map.get("tem_1"));
                //电池芯温度
                TEM_2[i_address - 1] = Integer.parseInt(map.get("tem_2"));
                //电池电量
                PERCENtAGES[i_address - 1] = Integer.parseInt(map.get("xiangduibaifenbi"));
                PERCENtAGES[i_address - 1] = PERCENtAGES[i_address - 1] > 100 ? PERCENtAGES[i_address - 1] - 100 : PERCENtAGES[i_address - 1];

                //电池电压
                DIANYA[i_address - 1] = Integer.parseInt(map.get("dianya"));
                //电池电流
                DIANLIU[i_address - 1] = Integer.parseInt(map.get("dianliu"));
                //剩余容量
                LEFT_CAP[i_address - 1] = Integer.parseInt(map.get("shengyurongliang"));
                //满充容量
                FULL_CAP[i_address - 1] = Integer.parseInt(map.get("manchongrongliang"));

                //循环次数
                LOOPS[i_address - 1] = Integer.parseInt(map.get("loops"));
                //soh
                SOH_2[i_address - 1] = Integer.parseInt(map.get("soh"));
                //电池版本
                barVer[i_address - 1] = map.get("berVer");
                //单体最大电压
                item_max[i_address - 1] = Integer.parseInt(map.get("item_max"));
                //单体最小电压
                item_min[i_address - 1] = Integer.parseInt(map.get("item_min"));
                //单体最小电压
                demandPower[i_address - 1] = Integer.parseInt(map.get("demandPower"));
                //采样电压
                samplingVs[i_address - 1] = map.get("samplingV");
                realSocVs[i_address - 1] = map.get("realSoc");

                //电池ID
                final String bd = map.get("BID");
                String bd1 = BIDS[i_address - 1];
                if (TextUtils.isEmpty(bd1)) {
                    //处理空指针
                    bd1 = "";
                }

                boolean isBdChange = !bd1.equals(bd) && !bd.equals("FFFFFFFFFFFFFFFF")
                        && !bd.equals("0000000000000000");
                BIDS[i_address - 1] = bd;
                //电池UID
                final String uid = map.get("UID");
                UIDS[i_address - 1] = uid;

                final int doorIndex = i_address - 1;

                // TODO: 2020/11/16 需要追加判断侧微动是开门状态
                if (i_address > 0 && isInitFinish && !isReplaceBattery && SMALLS[doorIndex] == 0) {
                    if (isCheckedBattery || isBdChange) {
                        isReplaceBattery = true;
                        // TODO: 2020/7/19 触发换电操作
                        LogUtil.I("触发换电操作");
                        try {
                            syncControl(new Consumer() {
                                @Override
                                public int hashCode() {
                                    return (i_address + "号仓准备换电").hashCode();
                                }

                                @Override
                                public boolean equals(Object obj) {
                                    return hashCode() == obj.hashCode();
                                }

                                @NonNull
                                @Override
                                public String toString() {
                                    return i_address + "号仓准备换电";
                                }

                                @Override
                                public void accept(Object o) {

                                    openDoorButtonHandler.removeCallbacksAndMessages(null);
                                    adminShrinkHandler.removeCallbacksAndMessages(null);
                                    BatteryDataModel batteryDataModel;

                                    if (PUSHS[i_address - 1] != 0x02) {
                                        showDialogInfo("5秒后关闭" + i_address + "号仓门，请注意安全", "5", "1");
                                        SystemClock.sleep(5000);

                                        LogUtil.I(i_address + "号准备换电");
                                        LogUtil.I("电池ID：" + BIDS[doorIndex]);
                                        LogUtil.I("锁微动：" + DOORS[i_address - 1]);
                                        LogUtil.I("测微动：" + SMALLS[i_address - 1]);
                                        writeLocalLog(i_address + "号准备换电 " + "电池ID：" + BIDS[doorIndex] + "-锁微动：" + DOORS[i_address - 1]);

                                        showDialogInfo("正在关闭" + i_address + "号仓门，请注意安全！", "6", "1");
                                        LogUtil.I("正在关闭" + i_address + "号仓门，请注意安全！");
                                        writeLocalLog("正在关闭" + i_address + "号仓门，请注意安全！");

                                        batteryDataModel = batteryDataModels.get(i_address - 1);
                                        if (batteryDataModel != null) {
                                            DoorController.getInstance().closeDoor(batteryDataModel);
                                        }
                                        showDialogInfo("正在检测电池请稍后", "60", "1");
                                    } else {
                                        showDialogInfo("正在检测电池请稍后", "60", "1");
                                        SystemClock.sleep(5 * 1000);
                                    }

                                    LogUtil.I("换电正在检测电池请稍后");
                                    String dcdcStatus = null;
                                    int readTimes = 15;
                                    while (readTimes-- > 0) {
                                        SystemClock.sleep(1000);
                                        if (!BIDS[doorIndex].equals("FFFFFFFFFFFFFFFF")
                                                && !BIDS[doorIndex].equals("0000000000000000")) {
                                            writeLocalLog(i_address + "号仓电池识别成功BID：" + BIDS[doorIndex] + " UID：" + UIDS[doorIndex]);
                                            break;
                                        } else {
                                            dcdcStatus = DCDC_state[i_address - 1];
                                            if (!TextUtils.isEmpty(dcdcStatus)) {
                                                if (dcdcStatus.equals("7")) {
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    LogUtil.I("换电readTimes：" + readTimes + " - dcdcStatus:" + dcdcStatus);

                                    boolean isActionStatus = false;
                                    if (!TextUtils.isEmpty(dcdcStatus)) {
                                        if (dcdcStatus.equals("7")) {
                                            isActionStatus = true;
                                            writeLocalLog("正在尝试激活电池,请稍后");
                                            showDialogInfo("正在尝试激活电池,请稍后", "40", "1");
                                            final long startTime = System.currentTimeMillis();
                                            while (System.currentTimeMillis() - startTime < 40 * 1000) {
                                                SystemClock.sleep(1000);
                                                dcdcStatus = DCDC_state[i_address - 1];
                                                if (!BIDS[doorIndex].equals("FFFFFFFFFFFFFFFF")
                                                        && !BIDS[doorIndex].equals("0000000000000000")) {
                                                    writeLocalLog("激活电池成功！");
                                                    writeLocalLog(i_address + "号仓电池识别成功BID：" + BIDS[doorIndex] + " UID：" + UIDS[doorIndex]);
                                                    break;
                                                }

                                                if (!TextUtils.isEmpty(dcdcStatus) && dcdcStatus.equals("8")) {
                                                    writeLocalLog("激活电池失败，请重试或拨打客服电话咨询！");
                                                    showDialogInfo("激活电池失败，请重试或拨打客服电话咨询！", "10", "1");
                                                    isReplaceBattery = false;

                                                    batteryDataModel = batteryDataModels.get(i_address - 1);
                                                    if (batteryDataModel != null) {
                                                        DoorController.getInstance().openDoor(batteryDataModel);
                                                    }
//                                                    push("", i_address + "");
//                                                    SystemClock.sleep(6000);
                                                    LogUtil.I("检测不到换电电池，请重试！");

                                                    Bundle bundle = new Bundle();
                                                    Message message1 = new Message();
                                                    message1.what = 1;
                                                    bundle.putInt("randomEmptyDoor", i_address);
                                                    message1.setData(bundle);
                                                    openDoorButtonHandler.sendMessageDelayed(message1, TimeUnit.MINUTES.toMillis(1));
                                                    writeLocalLog(i_address + "号仓门将在60秒后关闭");
                                                    return;
                                                }
                                            }
                                        }
                                    }

                                    writeLocalLog("判断确认微动" + i_address + "仓门：" + DOORS[i_address - 1]);
                                    LogUtil.I("判断微动" + i_address + "仓门：" + DOORS[i_address - 1]);
                                    if (!(BIDS[doorIndex].charAt(0) == 'M' || BIDS[doorIndex].charAt(0) == 'N' || BIDS[doorIndex].charAt(0) == 'R')) {
                                        writeLocalLog(i_address + "号仓门电池无法识别！" + BIDS[doorIndex]);
                                        showDialogInfo(i_address + "号仓门电池无法识别！", "10", "1");
                                        isReplaceBattery = false;

                                        batteryDataModel = batteryDataModels.get(i_address - 1);
                                        if (batteryDataModel != null) {
                                            DoorController.getInstance().openDoor(batteryDataModel);
                                        }

                                        if (!isActionStatus) {
                                            Bundle bundle10s = new Bundle();
                                            Message message10s = new Message();
                                            message10s.what = 3;
                                            bundle10s.putInt("randomEmptyDoor", i_address);
                                            message10s.setData(bundle10s);
                                            openDoorButtonHandler.sendMessage(message10s);
                                        }

                                        Bundle bundle = new Bundle();
                                        Message message1 = new Message();
                                        message1.what = 1;
                                        bundle.putInt("randomEmptyDoor", i_address);
                                        message1.setData(bundle);
                                        openDoorButtonHandler.sendMessageDelayed(message1, TimeUnit.MINUTES.toMillis(1));
                                        writeLocalLog(i_address + "号仓门将在60秒后关闭");
                                        return;
                                    }

                                    LogUtil.I("侧微动状态：" + SMALLS[i_address - 1]);
                                    if (SMALLS[i_address - 1] == 1) {
                                        if (!BIDS[doorIndex].equals("FFFFFFFFFFFFFFFF")
                                                && !BIDS[doorIndex].equals("0000000000000000")) {
                                            writeLocalLog("正在校验电池信息，请稍候！" + BIDS[doorIndex]);
                                            new ExchangeBarThread(i_address).run();
                                            return;
                                        } else {
                                            writeLocalLog("检测不到换电电池，请重试！");
                                            batteryDataModel = batteryDataModels.get(i_address - 1);
                                            if (batteryDataModel != null) {
                                                DoorController.getInstance().openDoor(batteryDataModel);
                                            }
//                                            push("", i_address + "");
//                                            SystemClock.sleep(6000);
                                            LogUtil.I("检测不到换电电池，请重试！");
                                            showDialogInfo("检测不到换电电池，请重试！", "6", "1");
                                            isReplaceBattery = false;

                                            Bundle bundle10s = new Bundle();
                                            Message message10s = new Message();
                                            message10s.what = 3;
                                            bundle10s.putInt("randomEmptyDoor", i_address);
                                            message10s.setData(bundle10s);
                                            openDoorButtonHandler.sendMessage(message10s);

                                            Bundle bundle = new Bundle();
                                            Message message1 = new Message();
                                            message1.what = 1;
                                            bundle.putInt("randomEmptyDoor", i_address);
                                            message1.setData(bundle);
                                            openDoorButtonHandler.sendMessageDelayed(message1, TimeUnit.MINUTES.toMillis(1));
                                            writeLocalLog(i_address + "号仓门将在60秒后关闭");
                                            return;
                                        }
                                    } else {
                                        writeLocalLog("号仓门关闭失败，请换其他仓门重试");
                                        LogUtil.I(i_address + "号仓门关闭失败，请换其他仓门重试");
                                        showDialogInfo(i_address + "号仓门关闭失败，请换其他仓门重试", "6", "1");
                                        isReplaceBattery = false;
                                        return;
                                    }
                                }
                            });
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            writeLocalLog("异常错误" + ex.getMessage());
                            isReplaceBattery = false;
                        }
                    }
                }
            }
        }
    }

    //:todo  DCDC信息解析
    //怕单一线程时间内处理不了 多线程处理数据 每返回一帧数据 起一个线程
    private class DataHandleDCDCThread extends Thread {
        private byte[] data;
        private int i_address;

        public DataHandleDCDCThread(int address, byte[] data) {
            this.i_address = address;
            this.data = data;
        }

        @Override
        public void run() {
            super.run();
            //替换索引值
            Map<String, String> map = new A_M_Analysis().analysisData_DCDC(data);
            DCDC_state[i_address - 1] = map.get("DCDC_state");
            DCDC_dianya[i_address - 1] = Integer.parseInt(map.get("DCDC_dianya"));
            DCDC_dianliu[i_address - 1] = Integer.parseInt(map.get("DCDC_dianliu"));
            DCDC_stop[i_address - 1] = Integer.parseInt(map.get("DCDC_stop"));
            DCDC_SV[i_address - 1] = map.get("DCDC_SV");
            DCDC_HV[i_address - 1] = map.get("DCDC_HV");

        }
    }

    //:todo  ACDC信息解析
    //怕单一线程时间内处理不了 多线程处理数据 每返回一帧数据 起一个线程
    private class DataHandleACDCThread extends Thread {
        private byte[] data;
        private int i_address;

        public DataHandleACDCThread(int address, byte[] data) {
            this.i_address = address - 80;
            this.data = data;
        }

        @Override
        public void run() {
            super.run();
            //替换索引值
            Map<String, String> map = new A_M_Analysis().analysisData_ACDC(data);
            ACDC_dianya_in[i_address - 1] = Integer.parseInt(map.get("ACDC_in_dianya"));
            ACDC_dianya_out[i_address - 1] = Integer.parseInt(map.get("ACDC_out_dianya"));
            ACDC_dianliu_in[i_address - 1] = Integer.parseInt(map.get("ACDC_in_dianliu"));
            ACDC_gonglv[i_address - 1] = Integer.parseInt(map.get("ACDC_gonglv"));
            ACDC_RT[i_address - 1] = System.currentTimeMillis();
        }
    }

    /**
     * @param serData 返回的485数据
     */
    //:todo  返回的485数据处理 目前是处理 温湿传感器 和 电表数据用的
    @Override
    public void onSerialResultApp(byte[] serData) {
        String str = new ChangeTool().ByteArrToHex(serData);
        str = str.replaceAll(" ", "");
//        LogUtil.I("SER 返回：" + str);
    }


    private void handler() {

        //:todo can推杆收缩（关门）    类型：handler   message：door - 门（从1开始）   info - 打开类型（这个参数主要是上传给服务器 告知后台是怎么打开仓门的）
        adminShrinkHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                int door_int = Integer.parseInt(msg.getData().getString("door"));

                byte[] a_1 = new byte[]{0x02, pushrodActSetTime};
                String b_1 = "98030" + door_int + "65";
                MyApplication.serialAndCanPortUtils.canSendOrder(b_1, a_1);
                LogUtil.I("CAN - 控制板 - 下发：    " + door_int + "号仓 收电池   " + b_1 + "    " + PubFunction.ByteArrToHex(a_1));
            }
        };

        chargeOnHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                // TODO: 2020/7/31 关闭DCDC电源

                int door_int = Integer.parseInt(msg.getData().getString("door"));
                FireSwitchController.getInstance().control(1 << (door_int - 1));
            }
        };

        chargeOffHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int door_int = Integer.parseInt(msg.getData().getString("door"));
                FireSwitchController.getInstance().control(~(1 << (door_int - 1)));
            }
        };

        //:todo can推杆伸长（开门）    类型：handler   message：door - 门（从1开始）   info - 打开类型（这个参数主要是上传给服务器 告知后台是怎么打开仓门的）
        adminElongationHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int door_int = Integer.parseInt(msg.getData().getString("door"));
                boolean isBreakForbidden = msg.getData().getBoolean("isBreakForbidden");
                if (!isBreakForbidden) {
                    if (forbiddenSp.getTargetForbidden(door_int - 1) != 1) {
                        showDialogInfo(door_int + "号仓门已被禁用，无法弹出，如有疑问请拔打客服电话", "10", "1");
                        return;
                    }
                }

                byte[] a = new byte[]{0x01, pushrodActSetTime};
                String b = "98030" + door_int + "65";
                MyApplication.serialAndCanPortUtils.canSendOrder(b, a);
                LogUtil.I("CAN - 控制板 - 下发：    " + door_int + "号仓 推电池   " + b + "    " + PubFunction.ByteArrToHex(a));

            }
        };

        //:todo 打开ADCD    类型：handler   参数：door - acdc编号（1，2，3）
        openADCDHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String door = msg.getData().getString("door");

                String id = "";
                if (door.equals("1")) {
                    id = "98105165";
                } else if (door.equals("2")) {
                    id = "98105265";
                } else if (door.equals("3")) {
                    id = "98105365";
                }

                byte[] data = new byte[]{0x55};
                MyApplication.serialAndCanPortUtils.canSendOrder(id, data);
            }
        };

        //:todo 关闭ADCD   类型：handler   参数：door - acdc编号（1，2，3）
        closeADCDHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String door = msg.getData().getString("door");

                String id = "";
                if (door.equals("1")) {
                    id = "98105165";
                } else if (door.equals("2")) {
                    id = "98105265";
                } else if (door.equals("3")) {
                    id = "98105365";
                }

                byte[] data = new byte[]{(byte) 0xAA};
                MyApplication.serialAndCanPortUtils.canSendOrder(id, data);
            }
        };


        //:todo 换电成功更新UI    类型：handler     未完成（新模式需要重写）
        setUiHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                int i = msg.getData().getInt("address") - 1;

                try {
                    if (PERCENtAGES[i] == -1) {
                        Picasso.with(activity).load(R.drawable.left_bar_null).into(bar_r_images[i]);
                        bar_r_texts[i].setText("0%");
                    } else {
                        if (forbiddenSp.getTargetForbidden(i) != 1) {
                            bar_r_texts[i].setText("停用");
                            bar_not_images[i].setVisibility(View.VISIBLE);
                            return;
                        } else {
                            bar_not_images[i].setVisibility(View.GONE);
                        }
                        if (!(BIDS[i].charAt(0) == 'M' || BIDS[i].charAt(0) == 'N' || BIDS[i].charAt(0) == 'R')) {
                            bar_r_texts[i].setText("空");
                            Picasso.with(activity).load(R.drawable.left_bar_null).into(bar_r_images[i]);
                            return;
                        }
                        int c = PERCENtAGES[i];
                        bar_r_texts[i].setText((PERCENtAGES[i] > 100 ? PERCENtAGES[i] - 100 : PERCENtAGES[i]) + "%");


                        if (c > 0 && c <= 100) {
                            //第七次上传参数
                            String BidStr = BIDS[i];
                            String TopBidStr = BidStr.substring(0, 1);
                            String EndBidStr = BidStr.substring(10, 12);
                            String volt = "";
                            if (TopBidStr.equals("M") && EndBidStr.equals("MM")) {
                                Picasso.with(activity).load(R.drawable.left_bar_has).into(bar_r_images[i]);
                            } else {
                                Picasso.with(activity).load(R.drawable.left_bar_out).into(bar_r_images[i]);
                            }
                        } else if (UIDS[i].equals("00000000") || UIDS[i].equals("FFFFFFFF")) {
                            Picasso.with(activity).load(R.drawable.left_bar_null).into(bar_r_images[i]);
                            bar_r_texts[i].setText("空");
                        }

                    }

                } catch (Exception e) {

                }

            }
        };

        //更新屏幕中间二维码用的
        //:todo 每次有电池变化的话 都会更新中间二维码    类型：handler    未完成（已经废弃 目前二维码写死也是可以的）
        changeQcodeHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                startNewTask(new Consumer() {
                    @Override
                    public void accept(Object o) {
                        try {
                            final Bitmap bitmap = Unit.generateBitmap(cabinetID + "/FFFFFFFFFFFFFFFF/0", 400, 400);
                            if (bitmap != null && !bitmap.isRecycled()) {
                                updateHander.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        rent_bar_qcode.setImageBitmap(bitmap);
                                    }
                                });
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            }
        };

        //:todo 打开提示框（包含语音）     类型：handler   参数：msg - 显示信息   time - 显示时间
        showProgressDialogHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String speakType = msg.getData().getString("type");
                String msg_str = msg.getData().getString("msg");
                String time_str = msg.getData().getString("time");
                progressDialog_4.show(msg_str, Integer.parseInt(time_str), Integer.parseInt(speakType));
            }
        };

        //:todo 讯飞语音插件    类型：handler    参数：msg - 朗读信息
        speakHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String str = msg.getData().getString("msg");
                if (textToSpeech != null) {
                    textToSpeech.speak(str, TextToSpeech.QUEUE_ADD, null);
                }
            }
        };

        //:todo 更新界面UI    类型：handler
        setTimeHandler = new Handler() {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss");
            Date curDate = new Date();

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                curDate.setTime(System.currentTimeMillis());
                String str = formatter.format(curDate);
                now_time.setText(str);
            }
        };

        sensorCollectionHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                t_5.setText(ammeterTotalPower);
                t_6.setText(hun_str);
                t_7.setText(tem_str);
                t_8.setText(ammeterPower);
            }
        };

        //重启android板
        rebootAndroidSystem = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //获取root权限
                DeviceSwitchController.getInstance().control(DeviceSwitcher.CMD.ANDROID_12V_REBOOT);
//                new RootCommand().RootCommandStart("reboot");
            }
        };

        updataBatteryDownlondReturnHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                String door = msg.getData().getString("door");
                String path = msg.getData().getString("path");
                String manu = msg.getData().getString("manu");

                Intent intent = new Intent(activity, A_UpDataBattery.class);
                intent.putExtra("door", door);
                intent.putExtra("path", path);
                intent.putExtra("manu", manu);
                intent.putExtra("tel", cabInfoSp.getTelNumber());
                intent.putExtra("cabid", cabid_title);
                activity.startActivity(intent);
            }
        };

        dcdcUpdradeHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                String door = msg.getData().getString("door");
                String path = msg.getData().getString("path");

                Intent intent;
                if (upgrading.contains("upgradeDcdcAll")) {
                    intent = new Intent(activity, DcdcUpdateActivity.class);
                } else {
                    intent = new Intent(activity, DcdcUpdateActivity2.class);
                }
                intent.putExtra("door", door);
                intent.putExtra("path", path);
                intent.putExtra("tel", cabInfoSp.getTelNumber());
                intent.putExtra("cabid", cabid_title);
                try {
                    intent.putExtra("address", Byte.parseByte(door));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (intent != null) {
                    activity.startActivity(intent);
                }
            }
        };

        acdcUpdradeHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                String door = msg.getData().getString("door");
                String path = msg.getData().getString("path");

                Intent intent;
                if (upgrading.contains("upgradeAcdcAll")) {
                    intent = new Intent(activity, AcdcUpdateActivity.class);
                } else {
                    intent = new Intent(activity, AcdcUpdateActivity2.class);
                }
                intent.putExtra("door", door);
                intent.putExtra("path", path);
                intent.putExtra("tel", cabInfoSp.getTelNumber());
                intent.putExtra("cabid", cabid_title);
                try {
                    intent.putExtra("address", Byte.parseByte(door));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (intent != null) {
                    activity.startActivity(intent);
                }
            }
        };
        envUpdradeHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                String path = msg.getData().getString("path");
                Intent intent = new Intent(activity, EvnBordUpdateActivity.class);
                intent.putExtra("path", path);
                intent.putExtra("tel", cabInfoSp.getTelNumber());
                intent.putExtra("cabid", cabid_title);
                activity.startActivity(intent);
            }
        };

        uploadPbarHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                long type = msg.getData().getLong("type");
                if (type == 1) {
                    long total = msg.getData().getLong("total");
                    long now = msg.getData().getLong("now");
                    float b = (float) now / total * 100;
                    LogUtil.I("movies：   " + "当前进度 - " + b);
                    uploadPbar.setProgress((int) b);
                    uploadmoviespanel.setVisibility(View.VISIBLE);
                    BAR_IS_RUN = 1;
                } else {
                    uploadmoviespanel.setVisibility(View.GONE);
                    BAR_IS_RUN = 0;
                }
            }
        };

        setMoviesHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                moviesUnit = new MoviesUnit(activity);


                LogUtil.I("卡数量：" + PubFunction.getExtSDCardPath().size());
                try {
                    StringBuilder stringBuilder = new StringBuilder();
                    if (moviesUnit.getmId() == -1 || !Unit.hasCamera()) {
                        //没有摄像头
                        camera_preview_panel.setVisibility(View.VISIBLE);
                        stringBuilder.append("#1");
                    }

                    if (!PubFunction.getExtSDCardPathList().contains("/mnt/external_sd")) {
                        stringBuilder.append("#2");
                    }
                    final String errorInfo = stringBuilder.toString();
                    if (!TextUtils.isEmpty(errorInfo)) {
                        tv_cameraErrorInfo.setText(errorInfo);
                    }

                    if (TextUtils.isEmpty(errorInfo)) {
                        boolean is_movies = moviesUnit.onResume(camera_preview);
                        if (is_movies == true) {
                            camera_preview_panel.setVisibility(View.GONE);
                            moviesUnit.moviesStart();
                        } else {
                            camera_preview_panel.setVisibility(View.VISIBLE);
                        }
                    }

                } catch (
                        Exception e) {
                    e.printStackTrace();
                }
            }
        }

        ;

        otherInfo_8_Handler = new

                Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        String t_8_str = msg.getData().getString("t_8");
                        t_8.setText(t_8_str + "W");
                    }
                }

        ;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (moviesUnit != null && camera_preview != null) {
            moviesUnit.onResume(camera_preview);
        }
        isAutoSetCurrentDetection = cabInfoSp.optAutoSetCurrentDetectionStatus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (moviesUnit != null && camera_preview != null) {
            moviesUnit.onPause();
        }
    }

    private void main() {
        //数据初始化
        Arrays.fill(BIDS, "FFFFFFFFFFFFFFFF");
        Arrays.fill(UIDS, "FFFFFFFF");
        //电量初始化
        Arrays.fill(PERCENtAGES, 0);
        //电量初始化
        Arrays.fill(JUEDUI_PERCENtAGES, 0);
        //微动初始化
        Arrays.fill(SMALLS, -1);
        //柜门初始化
        Arrays.fill(DOORS, -1);
        //电机初始化
        Arrays.fill(PUSHS, -1);
        //电压
        Arrays.fill(DIANYA, 0);
        //电流
        Arrays.fill(DIANLIU, 0);
        //SOH
        Arrays.fill(SOH_2, 0);
        //LOOPS
        Arrays.fill(LOOPS, 0);
        //item_max
        Arrays.fill(item_max, 0);
        //item_min
        Arrays.fill(item_min, 0);
        //demandPower
        Arrays.fill(demandPower, 0);
        //DCDC_state
        Arrays.fill(DCDC_state, "");
        //DCDC_dianya
        Arrays.fill(DCDC_dianya, 0);
        //DCDC_dianliu
        Arrays.fill(DCDC_dianliu, 0);
        //DCDC_stop
        Arrays.fill(DCDC_stop, 0);
        //DCDC_SV
        Arrays.fill(DCDC_SV, "");
        //DCDC_HV
        Arrays.fill(DCDC_HV, "");
        //DCDC_HV
        Arrays.fill(ERROR_state_01, "0000000000000000");
        //DCDC_HV
        Arrays.fill(ERROR_state_02, "0000000000000000");
        //DCDC_HV
        Arrays.fill(ERROR_state_03, "0000000000000000");
//        Arrays.fill(ACDC_ERROR_state_01, "0000000000000000");
        //ACDC_dianya
        Arrays.fill(ACDC_dianya_in, 0);
        //ACDC_dianya
        Arrays.fill(ACDC_dianya_out, 0);
        //ACDC_dianliu
        Arrays.fill(ACDC_dianliu_in, 0);
        //ACDC_gonglv
        Arrays.fill(ACDC_gonglv, 0);
        //ACDC_SV
        Arrays.fill(ACDC_SV, "");
        //ACDC_HV
        Arrays.fill(ACDC_HV, "");
        Arrays.fill(whichACDC, "");
        Arrays.fill(remainingTotalPower, "0KW");
        Arrays.fill(sleepStatus, "无");
        //ACDC_RT
        Arrays.fill(ACDC_RT, 0);
        //LEFT_CAP
        Arrays.fill(LEFT_CAP, 0);
        //FULL_CAP
        Arrays.fill(FULL_CAP, 0);
        //TEM_1
        Arrays.fill(TEM_1, 0);
        //TEM_2
        Arrays.fill(TEM_2, 0);
        //二次检测电池是否是弹出
        Arrays.fill(barVer, "0000");

        //填充电池临时数组数据
        byte[] byte_8 = new byte[8];
        Arrays.fill(byte_8, (byte) 0);
        byte[][] byte_17_8 = new byte[18][8];
        Arrays.fill(byte_17_8, byte_8);
        Arrays.fill(bar_all_2, byte_17_8);
    }

    private void init() {
        //初始化文件夹
        new CreateFile(activity);
        //语音初始化
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == textToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.CHINA);
                    if (result != TextToSpeech.LANG_COUNTRY_AVAILABLE
                            && result != TextToSpeech.LANG_AVAILABLE) {
                        LogUtil.I("TTS：TTS暂时不支持这种语音的朗读！");
                    }
                }
            }
        });

        //网络接口初始化
        ifHttpGetTelLinstener = this;
        ifHttpUploadBatteryInfoListener = this;
        ifHttpBandLongLinkLinstener = this;
        ifHttpOutLineDateListener = this;
        ifCurrentNetDBMLinstener = this;
        ifHttpGetQcodeLinstener = this;
        ifHttpOpenLongLinkLinstener = this;

        //获取root权限
        new RootCommand().RootCommandStart("chmod 777 " + getPackageCodePath());
        //更新屏幕二维码
//        changeQcodeHandler.sendMessage(new Message());
        startNewTask(new Consumer() {
            @Override
            public void accept(Object o) {
                try {
                    final Bitmap bitmap = Unit.generateBitmap(cabinetID + "/FFFFFFFFFFFFFFFF/0", 400, 400);
                    if (bitmap != null && !bitmap.isRecycled()) {
                        updateHander.post(new Runnable() {
                            @Override
                            public void run() {
                                rent_bar_qcode.setImageBitmap(bitmap);
                            }
                        });
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                //长链接初始化
                longLink = new OpenLongLink(ifHttpOpenLongLinkLinstener);
                LogUtil.I("longlink :    调用LinkHandler");
            }
        }).start();

        //can发送线程初始化
//        if (!NetUtil.isNetworkConnected(getApplicationContext())) {
        if (!thread_send_can.isAlive()) {
            thread_send_can.start();
        }
//        }
    }


    //:todo 换电线程   类型：线程    参数：i_address - 插入的电池仓门    换电逻辑需要按照流程图重写
    private class ExchangeBarThread extends Thread {

        private int i_address = -1;

        public ExchangeBarThread(int i_address) {
            this.i_address = i_address;
        }

        @Override
        public void run() {
            super.run();
            LogUtil.I("线程：" + Thread.currentThread().getName());

            final int doorIndex = i_address - 1;
            final String uid = UIDS[doorIndex];
            // TODO: 2019-12-26  网络Y/N ，N：无网继续换电。Y：有网，根据服务器结果而定
            if (NetUtil.isNetworkConnected(getApplicationContext())) {
                // TODO: 2019-12-26 有网的操作
                final int inputBar = i_address;
                if (BatteryUtil.is8A(uid)) {
                    //8个A的 请求 checkoldband接口
                    HttpOutLineCheckOldBind httpOutLineCheckOldBind = new HttpOutLineCheckOldBind(cabinetID, inputBar + "", BIDS[inputBar - 1], dbm, ifHttpOutLineCheckOldBindListener);
                    httpOutLineCheckOldBind.run();
                } else {
                    HttpOutLineCheckUserBalance httpOutLineCheckUserBalance = new HttpOutLineCheckUserBalance(cabinetID, UIDS[inputBar - 1], BIDS[inputBar - 1], PERCENtAGES[inputBar - 1] > 100 ? "1" : PERCENtAGES[inputBar - 1] + "", (inputBar) + "", dbm, ifHttpOutLineCheckUserBalanceListener);
                    httpOutLineCheckUserBalance.run();
                }
            } else {
                // TODO: 2019-12-26 1:先查找60或者48V
                // TODO: 2019-12-26 2：找不到符合换电的电池推出原电池
                // TODO: 2019-12-26 3：校验码写入失败处理，考虑重试2~3次
                // TODO: 2019-12-26 4:推出符合要求的电池，发送消息提示拿走
                outLineExchangeUID(i_address, null);
            }
        }

    }

    public void outLineExchangeUID(final int door, String uid32) {
        writeLocalLog("door" + door);
        final int doorIndex = door - 1;

        if (doorIndex >= 0) {
            final String uid = TextUtils.isEmpty(uid32) ? UIDS[doorIndex] : uid32;
            //排查选中的电是不是没有绑定的电池
            if (BatteryUtil.is8A(uid)) {//电池未绑定，回收电池
                writeLocalLog(door + "号仓电池未绑定，将被回收，如有问题请拨打客服电话咨询！");
                showDialogInfo(door + "号仓电池未绑定，将被回收，如有问题请拨打客服电话咨询！", "7", "1");
                isReplaceBattery = false;
                return;
            }

            if (PERCENtAGES[doorIndex] == 100) {
                //插入电池电量最高，无需换电
                writeLocalLog("当前电量为100%，无需换电~");
                showDialogInfo("当前电量为100%，无需换电~", "7", "1");
//                push("电池高于电柜电池，换电结束", "" + door);
//                SystemClock.sleep(6000);
                BatteryDataModel batteryDataModel = batteryDataModels.get(door - 1);
                if (batteryDataModel != null) {
                    DoorController.getInstance().openDoor(batteryDataModel);
                }

                Message message1 = new Message();
                message1.what = 1;
                Bundle bundle1 = new Bundle();
                bundle1.putInt("randomEmptyDoor", door);
                message1.setData(bundle1);
                openDoorButtonHandler.sendMessageDelayed(message1, 60000);
                isReplaceBattery = false;
                return;
            }

            //最大下标
            int vIndex = -1;
            //获取id下标0位，得到M/N，区分出60V和48V
            final String bid = (BIDS != null && BIDS.length > doorIndex && doorIndex >= 0) ? BIDS[doorIndex] : "";
            final char bid_pos_0 = bid.charAt(0);
            //最大下表的最大电量
            int vMaxCapacity = 0;
            //循环查找
            for (int i = 0, len = BIDS.length; i < len; i++) {
                final String tBid = BIDS[i];
                int is_stop = forbiddenSp.getTargetForbidden(i);
                if (!TextUtils.isEmpty(tBid) && tBid.charAt(0) == bid_pos_0 && is_stop == 1
                        && SMALLS[i] == 1 && UIDS[i].equals("AAAAAAAA")) {
                    if (PERCENtAGES[i] > vMaxCapacity) {
                        vMaxCapacity = PERCENtAGES[i];
                        vIndex = i;
                    }
                }
            }

            if (vIndex == -1) {
                writeLocalLog("当前电柜没有可选电池~");
                showDialogInfo("当前电柜没有可选电池~", "7", "1");
//                push("电池高于电柜电池，换电结束", "" + door);
//                SystemClock.sleep(6000);
                BatteryDataModel batteryDataModel = batteryDataModels.get(door - 1);
                if (batteryDataModel != null) {
                    DoorController.getInstance().openDoor(batteryDataModel);
                }

                Bundle bundle10s = new Bundle();
                Message message10s = new Message();
                message10s.what = 3;
                bundle10s.putInt("randomEmptyDoor", door);
                message10s.setData(bundle10s);
                openDoorButtonHandler.sendMessage(message10s);

                Message message1 = new Message();
                message1.what = 1;
                Bundle bundle1 = new Bundle();
                bundle1.putInt("randomEmptyDoor", door);
                message1.setData(bundle1);
                openDoorButtonHandler.sendMessageDelayed(message1, 60000);
                isReplaceBattery = false;
                return;
            }

            writeLocalLog("找到最大电量电池ID：" + BIDS[vIndex]);

            if (PERCENtAGES[doorIndex] >= PERCENtAGES[vIndex]) {
                //插入电池电量最高，无需换电
                writeLocalLog("当前电量为最高电量，无需换电~");
                showDialogInfo("当前电量为最高电量，无需换电~", "7", "1");
//                push("电池高于电柜电池，换电结束", "" + door);
//                SystemClock.sleep(6000);
                BatteryDataModel batteryDataModel = batteryDataModels.get(door - 1);
                if (batteryDataModel != null) {
                    DoorController.getInstance().openDoor(batteryDataModel);
                }

                Message message1 = new Message();
                message1.what = 1;
                Bundle bundle1 = new Bundle();
                bundle1.putInt("randomEmptyDoor", door);
                message1.setData(bundle1);
                openDoorButtonHandler.sendMessageDelayed(message1, 60000);
                isReplaceBattery = false;
                return;
            }

            int out_door = (vIndex + 1);
            LogUtil.I(door + "号电池换" + out_door);
            writeLocalLog(door + "号电池换" + out_door);

            //给数据库记录数据
            final int outPercent = PERCENtAGES[vIndex];
            final String out_battery = BIDS[vIndex] + "";

            final int in_door = door;
            final int inPercent = PERCENtAGES[doorIndex];
            final String in_battery = BIDS[doorIndex] + "";

            final String inDoorBarUID = uid;

            //做超时等待
            int result_out = 0;
            //下发写入弹出电池ID
            //尝试写3次校验码(UID)间隔2S读取一次是否写入成功
            int tryWriteCount = 10;
            final long intervalTime = 1000;
            writeLocalLog("准备给" + out_door + "号仓电池：" + out_battery + " 写入UID：" + inDoorBarUID);
            writeBatteryCheckCode(inDoorBarUID, out_door);
            while (tryWriteCount > 0) {
                //下发写入弹出电池ID
                SystemClock.sleep(intervalTime);
                if (new String(uidBytes[out_door - 1]).equals(inDoorBarUID)) {
                    result_out = 1;
                    writeLocalLog(out_door + "号仓电池：" + out_battery + " 写入UID成功");
                    break;
                }

                if (tryWriteCount == 5) {
                    writeBatteryCheckCode(inDoorBarUID, out_door);
                }
                tryWriteCount--;
            }

            if (result_out == 0) {
                //没有写入成功
                if (exchangeFailCount == 0) {

                    writeLocalLog("电池校验失败，正在尝试再次换电，请稍后！");
                    showDialogInfo("电池校验失败，正在尝试再次换电，请稍后！", "5", "1");
                    SystemClock.sleep(5000);
                    outLineExchangeUID(doorIndex + 1, null);
                    exchangeFailCount = 1;
                } else {
                    writeLocalLog("写入要弹出的电池ID失败，弹出电池！");
                    showDialogInfo("换电失败，正在弹出电池，请重试！，如有问题请拨打客服电话", "6", "1");
//                    push("写入要弹出的电池ID失败，弹出电池！", in_door + "");
//                    SystemClock.sleep(6000);

                    BatteryDataModel batteryDataModel = batteryDataModels.get(in_door - 1);
                    if (batteryDataModel != null) {
                        DoorController.getInstance().openDoor(batteryDataModel);
                    }

                    exchangeFailCount = 0;

                    Message message1 = new Message();
                    message1.what = 1;
                    Bundle bundle1 = new Bundle();
                    bundle1.putInt("randomEmptyDoor", emptyDoor);
                    message1.setData(bundle1);
                    openDoorButtonHandler.sendMessageDelayed(message1, 60000);
                    isReplaceBattery = false;
                }
            } else {
                writeLocalLog("准备清除UID为8A");
                writeBatteryCheckCode("AAAAAAAA", in_door);
                int tryTime = 10;
                while (tryTime > 0) {
                    if (new String(uidBytes[in_door - 1]).equals("AAAAAAAA")) {
                        writeLocalLog(in_door + "号仓电池设置8A成功");
                        break;
                    }
                    SystemClock.sleep(1000);
                    tryTime--;

                    if (tryTime == 5) {
                        writeBatteryCheckCode("AAAAAAAA", in_door);
                    }
                }

                if (!new String(uidBytes[in_door - 1]).equals("AAAAAAAA")) {
                    //擦除失败标记-3
                    forbiddenSp.setTargetForbidden(in_door - 1, -3);
                    writeLocalLog(in_door + "号仓电池写入8A失败标记禁用");
                }

                //UID计算电话号码
                String phoneInfo = UidDictionart.getI10EndPhoneNumber(uid);
                if (!TextUtils.isEmpty(phoneInfo)) {
                    writeLocalLog("换电成功，请手机尾号" + phoneInfo + "的用户取走" + out_door + "号仓门电池");
                    showDialogInfo("换电成功，请手机尾号" + phoneInfo + "的用户取走" + out_door + "号仓门电池", "15", "1");
                }

                //开启动画
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putInt("inPercent", inPercent);
                bundle.putInt("outPercent", outPercent);
                bundle.putInt("type", 0);
                message.setData(bundle);
                setExchangeHandler.sendMessage(message);

                writeLocalLog("准备打开" + out_door + "号仓门，当前侧微动状态：" + SMALLS[out_door - 1]);
//                push("正常换电", "" + (out_door));
                BatteryDataModel batteryDataModel = batteryDataModels.get(out_door - 1);
                if (batteryDataModel != null) {
                    DoorController.getInstance().openDoor(batteryDataModel);
                    LogUtil.I("开门后侧微动：" + SMALLS[out_door - 1]);
                    writeLocalLog("开门后侧微动：" + SMALLS[out_door - 1]);
                }
                if (mDatabase != null) {
                    //插入数据库
                    try {
                        LogUtil.I("插入数据库：" + cabinetID + "、" + uid + "、" + in_battery + "、" + in_door + "、" + inPercent + "、" + out_battery + "、" + out_door + "、" + outPercent);
                        ContentValues values = new ContentValues();
                        values.put(DBHelper.NUMBER, cabinetID);
                        values.put(DBHelper.UID, uid);
                        values.put(DBHelper.EXTIME, System.currentTimeMillis());
                        values.put(DBHelper.IN_BATTERY, in_battery);
                        values.put(DBHelper.IN_DOOR, in_door);
                        values.put(DBHelper.IN_ELECTRIC, inPercent);
                        values.put(DBHelper.OUT_BATTERY, out_battery);
                        values.put(DBHelper.OUT_DOOR, out_door);
                        values.put(DBHelper.OUT_ELECTRIC, outPercent);
                        mDatabase.insert(DBHelper.TABLE_NAME, null, values);
                        writeLocalLog("换电记录插入数据库完成");
                    } catch (Exception ex) {
                        writeLocalLog("换电记录插入数据库失败：" + ex.toString());
                    }
                }

                //10秒关门逻辑
                Bundle bundle10s = new Bundle();
                Message message10s = new Message();
                message10s.what = 3;
                bundle10s.putInt("randomEmptyDoor", out_door);
                message10s.setData(bundle10s);
                openDoorButtonHandler.sendMessage(message10s);

                //60秒超时关门逻辑
                Message message1 = new Message();
                message1.what = 1;
                Bundle bundle1 = new Bundle();
                bundle1.putInt("randomEmptyDoor", out_door);
                message1.setData(bundle1);
                openDoorButtonHandler.sendMessageDelayed(message1, 60000);

                exchangeFailCount = 0;
                isReplaceBattery = false;
            }
        } else {
            showDialogInfo("非法操作，换电结束！", "7", "1");
            isReplaceBattery = false;
            return;
        }
        //设置充电状态
        AN_IS_RUN = 0;
    }

    /**
     * 写入校验码
     */
    public static void writeBatteryCheckCode(String writeUid, int door_id) {
        byte[] CHECK_CODE_BYTES = new byte[]{0x65, 0x00, 0x05, (byte) 0x98
                , 0x08
                , 0x00, 0x00, 0x00
                , 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
        };

        if (!TextUtils.isEmpty(writeUid) && writeUid.trim().length() == 8) {
            writeLocalLog("写入电池UID:" + writeUid);
            CHECK_CODE_BYTES[1] = (byte) door_id;
            String upperCaseCheckCode = writeUid.trim().toUpperCase();
            for (int i = 8, j = 0, len = 16; i < len; i++, j++) {
                CHECK_CODE_BYTES[i] = (byte) upperCaseCheckCode.charAt(j);
            }
            MyApplication.serialAndCanPortUtils.canSendOrder(CHECK_CODE_BYTES);
        }
    }

    /**
     * 推出电池
     *
     * @param info
     * @param door
     */
    private void push(final String info, final String door) {
        Message message_2 = adminElongationHandler.obtainMessage();
        Bundle bundle_2 = new Bundle();
        bundle_2.putString("info", info);
        bundle_2.putString("door", door);
        message_2.setData(bundle_2);
        adminElongationHandler.sendMessage(message_2);
    }

    /**
     * 初始化关闭仓门使用
     *
     * @param info
     * @param door
     */
    private void initPull(final String info, final String door) {
        Message message_2 = adminShrinkHandler.obtainMessage();
        Bundle bundle_2 = new Bundle();
        bundle_2.putString("info", info);
        bundle_2.putString("door", door);
        message_2.setData(bundle_2);
        adminShrinkHandler.sendMessage(message_2);
    }

    /**
     * ∂
     * 收回电池
     *
     * @param info
     * @param door
     */
    public static void pull(final String info, final String door) {
//        Message message_2 = adminShrinkHandler.obtainMessage();
//        Bundle bundle_2 = new Bundle();
//        bundle_2.putString("info", info);
//        bundle_2.putString("door", door);
//        message_2.setData(bundle_2);
//        adminShrinkHandler.sendMessage(message_2);

        if (!TextUtils.isEmpty(door)) {
            try {
                final int doorInt = Integer.parseInt(door);
                BatteryDataModel batteryDataModel = batteryDataModels.get(doorInt - 1);
                if (batteryDataModel != null) {
                    DoorController.getInstance().closeDoor(batteryDataModel);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 收回电池
     *
     * @param info
     * @param door
     */
    private void pushAndPull(final String info, final String door, final int time) {
        syncControl(new Consumer() {
            @Override
            public void accept(Object o) {
                if (time > 0) {
//                    push(info, door);
                    final BatteryDataModel batteryDataModel = batteryDataModels.get(Integer.parseInt(door) - 1);
                    if (batteryDataModel != null) {
                        DoorController.getInstance().openDoor(batteryDataModel);
                    }
                    showDialogInfo("请取走您的电池，仓门将在" + time + "秒后关闭！", "20", "1");
                    adminShrinkHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                            pull(info, door);
                            if (batteryDataModel != null) {
                                DoorController.getInstance().closeDoor(batteryDataModel);
                            }
                        }
                    }, time * 1000);
                }
            }
        });
    }

    /**
     * 显示对话框提示
     *
     * @param msg
     * @param time
     */
    private void showDialogInfo(String msg, String time, String type) {
        Message message_3 = showProgressDialogHandler.obtainMessage();
        Bundle bundle_3 = new Bundle();
        bundle_3.putString("msg", msg);
        bundle_3.putString("time", time);
        bundle_3.putString("type", type);
        message_3.setData(bundle_3);
        showProgressDialogHandler.sendMessage(message_3);
    }

    /**
     * @param uid       用户Uid
     * @param door      插入的仓门
     * @param status    网络返回状态  -1：本地异常    0：服务器返回状态异常    1：正常换电
     * @param str       提示信息
     * @param errorCode 错误码
     * @param is_show   错误码是否展示
     * <p>
     * 网络返回处理
     * setExchangeHandler 换电动画参数设置
     * anStartHandler 开启动画
     */
    private Handler setExchangeHandler = new Handler() {
        private final int[] batteryPercentImages = {R.drawable.image_b_0,
                R.drawable.image_b_10, R.drawable.image_b_20, R.drawable.image_b_30,
                R.drawable.image_b_40, R.drawable.image_b_50, R.drawable.image_b_60,
                R.drawable.image_b_70, R.drawable.image_b_80, R.drawable.image_b_90,
                R.drawable.image_b_100};

        @Override
        public void handleMessage(Message msg) {
            int inPercent = msg.getData().getInt("inPercent");
            int outPercent = msg.getData().getInt("outPercent");
            int type = msg.getData().getInt("type");

            up_bar_1_text.setText(inPercent + "%");
            up_bar_2_text.setText(outPercent + "%");
            if (!NetUtil.isNetworkConnected(getApplicationContext())) {
                cost_bi.setText("离线换电");
            }
            final int percentIndex_in = inPercent / 10;
            if (percentIndex_in >= 0 && percentIndex_in < batteryPercentImages.length) {
                Picasso.with(activity).load(batteryPercentImages[percentIndex_in]).into(up_bar_1);
            }

            final int percentIndex_out = outPercent / 10;
            if (percentIndex_out >= 0 && percentIndex_out < batteryPercentImages.length) {
                Picasso.with(activity).load(batteryPercentImages[percentIndex_out]).into(up_bar_2);
            }

            ExchangeAnimation exchangeAnimation = new ExchangeAnimation(info_panel, cost, black_1, black_2, up_bar_2_panel, up_bar_1_panel, dialog_panel, new ExchangeAnimation.IFExchangeAnimationListener() {
                @Override
                public void onExchangeAnimationStart() {
                    AN_IS_RUN = 1;
                }

                @Override
                public void onExchangeAnimationEnd() {
                    AN_IS_RUN = 0;
                }
            });
            exchangeAnimation.startAnimation();
        }
    };


    //:todo 时间线程   类型：线程    开机启动 - 更新数据
    //线程code
    int send_can_type_code = 0;
    //发送自增的生命帧
    int send_live_count = 0;
    private Thread thread_send_can = new Thread() {
        @Override
        public void run() {
            super.run();

            try {
                sleep(1000 * 10);
                //开启启动 打开一个空的仓门
                for (int i = 0; i < 9; i++) {
                    int is_stop = forbiddenSp.getTargetForbidden(i);
                    if (SMALLS[i] == 0 && is_stop == 1) {
//                        showDialogInfo("正在关闭" + (i + 1) + "仓门", "5", "1");
                        showDialogInfo("即将关闭" + (i + 1) + "仓门" + "请注意安全", "5", "1");
                        initPull("电柜开机关闭仓门", (i + 1) + "");
                        Thread.sleep(6000);
                    }
                }

                isInitFinish = true;
                LogUtil.I("初始化完成");
                //摄像头初始化
                setMoviesHandler.sendMessage(new Message());

            } catch (Exception e) {
                e.printStackTrace();
            }

            while (send_can_type_code == 0) {

                //每秒钟下发生命帧
                byte alive[] = new byte[]{(byte) send_live_count};
                String alive_b = "9807ff65";
                MyApplication.serialAndCanPortUtils.canSendOrder(alive_b + "", alive);
                send_live_count = send_live_count > 254 ? 0 : send_live_count + 1;

                try {
                    sleep(1000);

                    Calendar calendar = Calendar.getInstance();
                    int minute = calendar.get(Calendar.MINUTE);
                    int second = calendar.get(Calendar.SECOND);
                    int hour = calendar.get(Calendar.HOUR);

                    //每十分钟请求一次400电话
                    if (minute % 10 == 0 && second == 2) {
                        //获取电柜显示电话
                        HttpGetTel httpGetTel = new HttpGetTel(cabinetID, ifHttpGetTelLinstener);
                        httpGetTel.start();
                        //请求二维码
                        HttpGetQcode httpGetQcode = new HttpGetQcode(cabinetID, ifHttpGetQcodeLinstener);
                        httpGetQcode.start();
                        //上传GMS
                        Map<String, String> map = new GSMCellLocation(activity).getGSMCell();
                        if (map != null) {
                            HttpUploadGMS httpUploadGMS = new HttpUploadGMS(cabinetID, map.get("mcc"), map.get("mnc"), map.get("lac"), map.get("cellId"));
                            httpUploadGMS.start();
                        }
                    }

                    //每10秒
                    //传感器显示的值
                    sensorCollectionHandler.sendMessage(new Message());

                    if (AN_IS_RUN == 0) {
                        if (second % 30 == 0 && mDatabase != null) {
                            //每分钟判断数据库里面有没有换电数据 如果有的话 上传给服务器 如果成功的话 数据库里面删除这条数据
                            Cursor cursor = mDatabase.query(DBHelper.TABLE_NAME,
                                    new String[]{DBHelper.NUMBER, DBHelper.UID, DBHelper.EXTIME, DBHelper.IN_BATTERY, DBHelper.IN_DOOR, DBHelper.IN_ELECTRIC, DBHelper.OUT_BATTERY, DBHelper.OUT_DOOR, DBHelper.OUT_ELECTRIC},
                                    null,
                                    null,
                                    null,
                                    null,
                                    null);
                            if (cursor != null) {
                                int numberIndex = cursor.getColumnIndex(DBHelper.NUMBER);
                                int uidIndex = cursor.getColumnIndex(DBHelper.UID);
                                int extimeIndex = cursor.getColumnIndex(DBHelper.EXTIME);
                                int inBatteryIndex = cursor.getColumnIndex(DBHelper.IN_BATTERY);
                                int inDoorIndex = cursor.getColumnIndex(DBHelper.IN_DOOR);
                                int inElectricIndex = cursor.getColumnIndex(DBHelper.IN_ELECTRIC);
                                int outBatteryIndex = cursor.getColumnIndex(DBHelper.OUT_BATTERY);
                                int outDoorIndex = cursor.getColumnIndex(DBHelper.OUT_DOOR);
                                int outElectricIndex = cursor.getColumnIndex(DBHelper.OUT_ELECTRIC);
                                if (cursor.moveToFirst()) {
                                    String number = cursor.getString(numberIndex);
                                    String uid = cursor.getString(uidIndex);
                                    String extime = cursor.getString(extimeIndex);
                                    String in_eldctric = cursor.getString(inElectricIndex);
                                    String in_door = cursor.getString(inDoorIndex);
                                    String in_battery = cursor.getString(inBatteryIndex);
                                    String out_battery = cursor.getString(outBatteryIndex);
                                    String out_door = cursor.getString(outDoorIndex);
                                    String outPercent = cursor.getString(outElectricIndex);

                                    HttpOutLineDate httpOutLineDate = new HttpOutLineDate(number, uid, extime, in_battery, in_door, in_eldctric, out_battery, out_door, outPercent, dbm, ifHttpOutLineDateListener);
                                    httpOutLineDate.start();
//                                LogUtil.I("网络：   正在上传数据库数据   " + extime);
                                }
                                localExchanges = cursor.getCount();
                                LogUtil.I("网络：   数据库换电条数 - " + localExchanges);
                            }
                        }
                    }

                    if (longLink != null) {
                        onLinePanelHandler.removeCallbacksAndMessages(null);
                        onLinePanelHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                t_9.setText(longLink.isKeepConnected() ? "在线" : "离线");
                            }
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    //:todo onDestroy（）
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //开机初始化线程
        onCreateThreadCode = 1;
        //can发送接口
        send_can_type_code = 1;
        //删除串口的调用
        MyApplication.getInstance().deletListener(this);
        //清空长连接
        if (longLink != null) {
            longLink.onDestory();
            longLink = null;
        }
//        MyApplication.serialAndCanPortUtils.onDestroy();

        ((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE))
                .unregisterNetworkCallback(networkCallback);
        upgrading.clear();
    }

    @Override
    public void onClick(View v) {
        if (download_qcode.getId() == v.getId()) {
            activity.startActivity(new Intent(activity, A_Admin.class));
        }
    }


    /**
     * @param dbm 信号强弱值
     *            <p>
     *            网络信号
     */
    @Override
    public void IFCurrentNetDBMResult(int signalLevel, int dbm) {
        this.dbm = dbm;

        if (signalLevel > 2) {
            b_3.setImageResource(R.drawable.b3);
        } else {
            switch (signalLevel) {
                case 2:
                    b_3.setImageResource(R.drawable.b10);
                    break;
                default:
                    b_3.setImageResource(R.drawable.b9);
                    break;
            }
        }
    }


    /**
     * @param code 状态码
     * @param str  返回信息
     * @param data 返回值
     * <p>
     * 上传换电信息
     * <p>
     * outLineDataSuccess 删除本地数据
     */
    Handler outLineDataSuccess = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            String extime = msg.getData().getString("extime");
            int count = mDatabase.delete(DBHelper.TABLE_NAME, DBHelper.EXTIME + " = ?", new String[]{extime});
            LogUtil.I("网络：删除本地数据库   " + count);

        }
    };

    @Override
    public void onHttpOutLineDateResult(String code, String str, String data, String extime) {
        if (code.equals("-1")) {
            LogUtil.I("网络 ：" + str);
        } else if (code.equals("1")) {
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("extime", extime);
            message.setData(bundle);
            outLineDataSuccess.sendMessage(message);

        } else if (code.equals("0")) {
        }
    }


    /**
     * @param code 状态码
     * @param str  返回信息
     * @param data 返回值
     *             <p>
     *             上传电池信息目前不需要任何处理
     */
    @Override
    public void onHttpUploadBatteryInfoResult(String code, String str, String data) {

    }


    /**
     * @param code 状态码
     * @param str  返回信息
     * @param data 返回值
     * <p>
     * 获取右上角二维码
     * <p>
     */

    private Handler QcodeHadnler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final String url = msg.getData().getString("url");
            if (!TextUtils.isEmpty(url)) {
                startNewTask(new Consumer() {
                    @Override
                    public void accept(Object o) {
                        LogUtil.I("当前线程：" + Thread.currentThread().getName());
                        try {
                            final Bitmap bitmap = Unit.generateBitmap(url, 400, 400);
                            if (bitmap != null && !bitmap.isRecycled()) {
                                updateHander.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        download_qcode.setImageBitmap(bitmap);
                                    }
                                });
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            }
        }
    };

    @Override
    public void onHttpGetQcodeResult(String code, String str, String data) {
        if (code.equals("-1")) {
            LogUtil.I("网络：   二维码 - " + str);
        } else if (code.equals("1")) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("url", jsonObject.getString("url"));
                message.setData(bundle);
                QcodeHadnler.sendMessage(message);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (code.equals("0")) {
        }
    }


    /**
     * @param code 状态码
     * @param str  返回信息
     * @param data 返回值
     * <p>
     * 电话400返回
     * setTelHandler 设置400显示UI
     */
    private Handler setTelHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String tel = msg.getData().getString("tel");
            tel_text.setText(!TextUtils.isEmpty(tel) ? tel : "");
        }
    };

    @Override
    public void onHttpGetTelResult(String code, String str, String data) {

        if (code.equals("-1")) {

        } else if (code.equals("1")) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                String tel = jsonObject.getString("contact");
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("tel", tel);
                message.setData(bundle);
                setTelHandler.sendMessage(message);
                cabInfoSp.setTelNumber(tel);
                LogUtil.I("网络：   电话 - " + jsonObject.getString("contact"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (code.equals("0")) {

        }
    }


    /**
     * 回传连接号，绑定长连接
     *
     * @param code 长连接号
     */
    @Override
    public void onHttpReTurnIDResult(String code) {
        new com.hellohuandian.app.httpclient.HttpBandLongLink(code, cabinetID, cabInfoSp.getLongLinkNumber(), ifHttpBandLongLinkLinstener)
                .start();
    }

    /**
     * @param code 状态码
     * @param str  返回信息
     * @param data 返回值
     *             <p>
     *             绑定长连接
     */
    @Override
    public void onHttpBandLongLinkResult(String code, String str, String data) {
        if (code.equals("-1")) {
            LogUtil.I("网络 ：   绑定失败");
        } else if (code.equals("1")) {
            LogUtil.I("网络 ：   绑定成功");
        } else if (code.equals("0")) {
            LogUtil.I("网络 ：   绑定失败");
        }
    }

    /**
     * 返回的错误码，做二次处理
     *
     * @param data
     */
    Handler onLinePanelHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            int msg_str = msg.getData().getInt("msg");
//            t_9.setText(msg_str == 1 ? "在线" : "离线");
        }
    };

    @Override
    public void onHttpReturnErrorResult(int data) {
//        Message message = new Message();
//        Bundle bundle = new Bundle();
//        bundle.putInt("msg", data);
//        message.setData(bundle);
//        onLinePanelHandler.sendMessage(message);
    }

    private Handler showAdvWebViewHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            adv_webview.setVisibility(View.VISIBLE);
        }
    };
    /**
     * 回传数据，电柜处理行为
     *
     * @param data 返回的json数据
     */
    private Handler setNumberHadnler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            String address = msg.getData().getString("address");
            String number = msg.getData().getString("number");
            cab_id.setText(number);
            now_address.setText(address);

            LogUtil.I("开始加载广告");
            WebSettings webSettings = adv_webview.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
            webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
            webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
            webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
//            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); //关闭webview中缓存
            webSettings.setAllowFileAccess(true); //设置可以访问文件
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
            webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
            webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
            adv_webview.loadUrl("http://apc.halouhuandian.com/Cabinet/message.html?id=" + cabInfoSp.getCabinetNumber());
            adv_webview.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    showAdvWebViewHandler.sendMessage(new Message());
                }
            });
        }
    };

    @Override
    public void onHttpReturnDataResult(final String orderString) {
        try {
            JSONTokener jsonTokener = new JSONTokener(orderString);
            JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
            final String type = jsonObject.getString("type");
            if (BAR_IS_RUN == 1) {
                return;
            }
            //长链接下发  重启android板子
            if (type.equals("restartAndrBoard")) {
                //获取root权限
                new RootCommand().RootCommandStart("reboot");
            }
            //长链接下发  打开关闭后台
            else if (type.equals("cmdRemoteOpenAdmin")) {
                String action = jsonObject.getString("action");
                if (action.equals("1")) {
                    activity.startActivity(new Intent(activity, A_Admin.class)
                            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
                }
                if (action.equals("0")) {
                    if (MyApplication.activitys != null && !MyApplication.activitys.isEmpty()) {
                        for (Activity act : MyApplication.activitys) {
                            if (act != null && A_Admin.class.getName().equals(act.getClass().getName())) {
                                if (!act.isFinishing()) {
                                    act.finish();
                                    MyApplication.activitys.remove(act);
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            //长链接下发  打开提示框
            else if (type.equals("cmdAlertMsg")) {
                String msg_str = jsonObject.getString("msg");
                String time = jsonObject.getString("time");

                int time_int = Integer.parseInt(time);
                showDialogInfo(msg_str, time_int + "", "1");
            }

            //长链接下发  显示仓门状态
            else if (type.equals("remoteSendCabStat")) {

                String data = jsonObject.getString("data");
                String name = jsonObject.getString("name");
                String number = jsonObject.getString("number");
                String cabid = jsonObject.getString("cabid");
                String isHeat = jsonObject.getString("isheat");//是否加热

                cabid_title = cabid;
                cabInfoSp.setLongLinkNumber(cabid);
                cabInfoSp.setAddress(name);
                cabInfoSp.setAddress_1(name);
                cabInfoSp.setVersion(MyApplication.getInstance().cab_version+"");

                if (number.equals(cabinetID)) {

                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("number", cabid);
                    bundle.putString("address", name);
                    message.setData(bundle);
                    setNumberHadnler.sendMessage(message);

                    try {
                        JSONTokener jsonTokener_1 = new JSONTokener(data);
                        JSONArray jsonArray = (JSONArray) jsonTokener_1.nextValue();

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject_1 = jsonArray.getJSONObject(i);
                            String door = jsonObject_1.getString("door");
                            String outIn = jsonObject_1.getString("outIn");

                            int door_int = Integer.parseInt(door);
                            int outIn_int = Integer.parseInt(outIn);

                            forbiddenSp.setTargetForbidden(door_int - 1, outIn_int);

                        }

                        for (int i = 0; i < jsonArray.length(); i++) {
                            Message message_ui = new Message();
                            Bundle bundle_ui = new Bundle();
                            bundle_ui.putInt("address", i + 1);
                            message_ui.setData(bundle_ui);
                            setUiHandler.sendMessage(message_ui);
                        }
                    } catch (Exception e) {
                    }
                }

                if (!isInitFinish) {
                    if (!thread_send_can.isAlive()) {
                        thread_send_can.start();
                    }
                }
            }

            //长链接下发  更新柜子android软件
            else if (type.equals("updateCabinetApp")) {
                try {
                    String furl = jsonObject.getString("furl");
                    showDialogInfo("准备更新主程序，请勿进行操作！", "60", 1 + "");
                    DownLoadApk downLoadApk = new DownLoadApk(activity, furl);
                    downLoadApk.start();
                } catch (Exception ex) {
                    writeLocalLog(ex.getMessage());
                    upgrading.clear();
                    ex.printStackTrace();
                }
            }
            //长链接下发  更新柜子DCDC
            else if (type.equals("upgradeDcdc") && upgrading.isEmpty()) {
                try {
                    upgrading.add("upgradeDcdc");
                    String furl = jsonObject.getString("url");
                    String door = jsonObject.getString("door");
                    String fname = jsonObject.getString("fname");
                    String name = jsonObject.getString("name");
                    showDialogInfo("准备升级" + door + "号DCDC，请勿进行操作！", "10", 1 + "");
                    LogUtil.I("DCDC地址：" + furl);
                    UpdataDcdc updataDcdc = new UpdataDcdc(activity, furl, fname, door, name, dcdcUpdradeHandler);
                    updataDcdc.downloadAPK();
                } catch (Exception ex) {
                    writeLocalLog(ex.getMessage());
                    upgrading.clear();
                    ex.printStackTrace();
                }
            } else if (type.equals("upgradeDcdcAll") && upgrading.isEmpty()) {
                try {
                    LogUtil.I(jsonObject.toString());
                    writeLocalLog(jsonObject.toString());
                    upgrading.add("upgradeDcdcAll");
                    String furl = jsonObject.getString("url");
                    String fname = jsonObject.getString("fname");
                    String name = jsonObject.getString("name");
                    showDialogInfo("准备升级" + "DCDC，请勿进行操作！", "10", 1 + "");
                    LogUtil.I("DCDC地址：" + furl);
                    UpdataDcdc updataDcdc = new UpdataDcdc(activity, furl, fname, "", name, dcdcUpdradeHandler);
                    updataDcdc.downloadAPK();
                } catch (Exception ex) {
                    writeLocalLog(ex.getMessage());
                    upgrading.clear();
                    ex.printStackTrace();
                }
            }
            //长链接下发  更新柜子ACDC
            else if (type.equals("upgradeAcdc") && upgrading.isEmpty()) {
                try {
                    writeLocalLog(jsonObject.toString());
                    upgrading.add("upgradeAcdc");
                    String furl = jsonObject.getString("url");
                    String acdcno = jsonObject.getString("acdcno");
                    String fname = jsonObject.getString("fname");
                    String name = jsonObject.getString("name");
                    showDialogInfo("准备升级" + acdcno + "号ACDC，请勿进行操作！", "10", 1 + "");
                    LogUtil.I("ACDC地址：" + furl);
                    UpdataDcdc updataDcdc = new UpdataDcdc(activity, furl, fname, acdcno, name, acdcUpdradeHandler);
                    updataDcdc.downloadAPK();
                } catch (Exception ex) {
                    writeLocalLog(ex.getMessage());
                    upgrading.clear();
                    ex.printStackTrace();
                }
            } else if (type.equals("upgradeAcdcAll") && upgrading.isEmpty()) {
                try {
                    LogUtil.I(jsonObject.toString());
                    upgrading.add("upgradeAcdcAll");
                    String furl = jsonObject.getString("url");
                    String fname = jsonObject.getString("fname");
                    String name = jsonObject.getString("name");
                    showDialogInfo("准备升级" + "ACDC，请勿进行操作！", "10", 1 + "");
                    LogUtil.I("ACDC地址：" + furl);
                    UpdataDcdc updataDcdc = new UpdataDcdc(activity, furl, fname, "0", name, acdcUpdradeHandler);
                    updataDcdc.downloadAPK();
                } catch (Exception ex) {
                    writeLocalLog(ex.getMessage());
                    upgrading.clear();
                    ex.printStackTrace();
                }
            } else if (type.equals("envBoardUpgrade") && upgrading.isEmpty()) {
                try {
                    String furl = jsonObject.getString("url");
                    if (!TextUtils.isEmpty(furl)) {
                        upgrading.add("envBoardUpgrade");
                        String name = furl.substring(furl.lastIndexOf("/") + 1, furl.length());
                        showDialogInfo("准备升级环境板，请勿进行操作！", "10", 1 + "");
                        LogUtil.I("环境板地址：" + furl);
                        UpdataDcdc updataDcdc = new UpdataDcdc(activity, furl, name, "0", name, envUpdradeHandler);
                        updataDcdc.downloadAPK();
                    }
                } catch (Exception ex) {
                    writeLocalLog(ex.getMessage());
                    upgrading.clear();
                    ex.printStackTrace();
                }
            }

            //长链接下发  网络后台开门
            else if (type.equals("remoteOpenDoor")) {
                try {
                    final int door = Integer.parseInt(jsonObject.getString("door"));
                    LogUtil.I((door + "号门状态：" + SMALLS[door - 1]));

                    syncControl(new Consumer() {
                        @Override
                        public int hashCode() {
                            return ("远程打开" + door + "号仓门").hashCode();
                        }

                        @Override
                        public boolean equals(Object obj) {
                            return hashCode() == obj.hashCode();
                        }

                        @NonNull
                        @Override
                        public String toString() {
                            return "远程打开" + door + "号仓门";
                        }

                        @Override
                        public void accept(Object o) {
                            showDialogInfo("远程打开" + door + "号仓门请注意安全", "10", "1");
//                            push("正在打开" + door + "号仓门", door + "");
//                            SystemClock.sleep(6000);
                            BatteryDataModel batteryDataModel = batteryDataModels.get(door - 1);
                            if (batteryDataModel != null) {
                                DoorController.getInstance().openDoor(batteryDataModel);
                            }
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (type.equals("remoteCloseDoor")) {
                String num = jsonObject.getString("number");
                LogUtil.I("num:" + num);
                if (!TextUtils.isEmpty(num)) {
                    try {
                        final int door = Integer.parseInt(jsonObject.getString("door"));
                        LogUtil.I((door + "号门状态：" + SMALLS[door - 1]));
                        syncControl(new Consumer() {
                            @Override
                            public int hashCode() {
                                return ("远程关闭" + door + "号仓门请注意安全").hashCode();
                            }

                            @Override
                            public boolean equals(Object obj) {
                                return hashCode() == obj.hashCode();
                            }

                            @NonNull
                            @Override
                            public String toString() {
                                return "远程关闭" + door + "号仓门";
                            }

                            @Override
                            public void accept(Object o) {
                                showDialogInfo("远程关闭" + door + "号仓门", "10", "1");
                                BatteryDataModel batteryDataModel = batteryDataModels.get(door - 1);
                                if (batteryDataModel != null) {
                                    DoorController.getInstance().closeDoor(batteryDataModel);
                                }
                            }
                        });
                    } catch (Exception ex) {
                        LogUtil.I("异常:");
                        ex.printStackTrace();
                    }
                }
            }

            //长链接下发  给服务器上传电柜里面的电池参数
            else if (type.equals("getBatteryInfo")) {
                JSONArray send_jsonarray = new JSONArray();
                for (int i = 0; i < DEFINE_BAR_COUNT; i++) {
                    //上传数据
                    JSONObject jsonObject_1 = new JSONObject();
                    try {
                        //上传参数
                        jsonObject_1.put("door", i + 1 + ""); //仓门id 从1开始
                        jsonObject_1.put("battery", BIDS[i] + "");   //电池id
                        jsonObject_1.put("bty_rate", PERCENtAGES[i] > 100 ? PERCENtAGES[i] - 100 : PERCENtAGES[i]);  //电池电量

                        //第二次上传参数
                        jsonObject_1.put("soh", "100");  //soh
                        jsonObject_1.put("soh_2", SOH_2[i]);  //soh

                        if (TextUtils.isEmpty(realSocVs[i])) {
                            jsonObject_1.put("soc", realSocVs[i] + "");
                        } else {
                            jsonObject_1.put("soc", Integer.parseInt(realSocVs[i]) > 100 ? 1 : realSocVs[i]);
                        }

                        jsonObject_1.put("volt_min", item_min[i]);
                        jsonObject_1.put("volt_max", item_max[i]);
                        //第三次上传参数
                        jsonObject_1.put("vdif", item_max[i] - item_min[i]);  //电池里面各个串数电池 最高和最低的差值    压差
                        jsonObject_1.put("uses", LOOPS[i]);  //循环次数
                        //第四次上传参数
                        jsonObject_1.put("wendu", TEM_2[i] / 10f); //电池温度
                        jsonObject_1.put("dianya", DIANYA[i] * 100); //电池 电压
                        jsonObject_1.put("dianliu", DIANLIU[i] * 100); //电池 电流
                        //第五次上传参数
                        jsonObject_1.put("inching", DOORS[i]); // 微动数据上传
                        jsonObject_1.put("side_inching", SMALLS[i]); // 微动数据上传

                        //第六次上传参数
                        jsonObject_1.put("cabid", cabInfoSp.getLongLinkNumber()); // 长链接下发的id
                        jsonObject_1.put("full_cap", FULL_CAP[i] * 1000); // 充满电池容量
                        jsonObject_1.put("left_cap", LEFT_CAP[i] * 1000); // 剩余电池容量

                        jsonObject_1.put("TEM_2", TEM_1[i] / 10f);
                        jsonObject_1.put("uid32", UIDS[i]); // 剩余电池容量
                        jsonObject_1.put("outIn", forbiddenSp.getTargetForbidden(i));
                        jsonObject_1.put("lastDataDate", lastDataDateAttrs[i]);

                        //第七次上传参数
                        if (!TextUtils.isEmpty(BIDS[i])) {
                            final String volt = BIDS[i].startsWith("M") ? "60V" : "48V";
                            jsonObject_1.put("volt", volt); //上传电池的类型是什么类型的 现在又 48V和60V的
                        }

                        if (!TextUtils.isEmpty(barVer[i])) {
                            try {
                                String barBer = barVer[i];
                                String a = barBer.substring(0, 2);
                                String b = barBer.substring(2, 4);
                                int a_i = Integer.parseInt(a, 16);
                                int b_i = Integer.parseInt(b, 16);
                                jsonObject_1.put("bsv", b_i);
                                jsonObject_1.put("bhv", a_i);
                            } catch (Exception e) {
                            }
                        }

                        if (!TextUtils.isEmpty(DCDC_SV[i])) {
                            jsonObject_1.put("dcbsv", DCDC_SV[i]);
                        }
                        if (!TextUtils.isEmpty(DCDC_HV[i])) {
                            jsonObject_1.put("dcbhv", DCDC_HV[i]);
                        }
                        if (!TextUtils.isEmpty(samplingVs[i])) {
                            jsonObject_1.put("collvolt", samplingVs[i]);
                        }

                        try {
                            String end_ERROR_state_str_01 = ERROR_state_01[i].substring(2, 10);
                            parserDcdcErrorCodeSb.setLength(0);
                            if (end_ERROR_state_str_01.length() == 8) {
                                for (int in = end_ERROR_state_str_01.length() - 1; in >= 0; in -= 2) {
                                    parserDcdcErrorCodeSb.append(end_ERROR_state_str_01.charAt(in - 1));
                                    parserDcdcErrorCodeSb.append(end_ERROR_state_str_01.charAt(in));
                                }
                            }
                            jsonObject_1.put("inwarn", parserDcdcErrorCodeSb.toString());

                            String end_ERROR_state_str_02 = ERROR_state_02[i].substring(2, 10);
                            parserDcdcErrorCodeSb.setLength(0);
                            if (end_ERROR_state_str_02.length() == 8) {
                                for (int in = end_ERROR_state_str_02.length() - 1; in >= 0; in -= 2) {
                                    parserDcdcErrorCodeSb.append(end_ERROR_state_str_02.charAt(in - 1));
                                    parserDcdcErrorCodeSb.append(end_ERROR_state_str_02.charAt(in));
                                }
                            }
                            jsonObject_1.put("outwarn", parserDcdcErrorCodeSb.toString());

                            String end_ERROR_state_str_03 = ERROR_state_03[i].substring(2, 10);
                            parserDcdcErrorCodeSb.setLength(0);
                            if (end_ERROR_state_str_03.length() == 8) {
                                for (int in = end_ERROR_state_str_03.length() - 1; in >= 0; in -= 2) {
                                    parserDcdcErrorCodeSb.append(end_ERROR_state_str_03.charAt(in - 1));
                                    parserDcdcErrorCodeSb.append(end_ERROR_state_str_03.charAt(in));
                                }
                            }
                            jsonObject_1.put("bmswarn", parserDcdcErrorCodeSb.toString());
                        } catch (Exception e) {

                        }

                        jsonObject_1.put("modvolt", DCDC_dianya[i] / 10f);
                        jsonObject_1.put("modele", DCDC_dianliu[i] / 10f);

                        String DCDC_state = A_Main2.DCDC_state[i];
                        String state_str = "";
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
                                default:
                                    state_str = "无";
                                    break;
                            }
                            jsonObject_1.put("dcdcStatus", state_str);
                        }

                        int stop_code = A_Main2.DCDC_stop[i];
                        if (stop_code < A_Admin.stopCodeStr.length) {
                            jsonObject_1.put("stopReson", A_Admin.stopCodeStr[stop_code]);
                        }

                        send_jsonarray.put(jsonObject_1);

                        if (send_jsonarray.length() == DEFINE_BAR_COUNT) { // 所有数据都得上去 包括没有的仓门

                            JSONObject dataJsonObject1 = new JSONObject();
                            dataJsonObject1.put("number", cabinetID);  //柜子id
                            dataJsonObject1.put("dbm", dbm);  //sim卡信号值
                            dataJsonObject1.put("cabtype", "6-can-48_60v混合");  //柜子类型
                            dataJsonObject1.put("version", MyApplication.cab_version + "");  //柜子当前app版本
                            dataJsonObject1.put("doors", send_jsonarray);//柜子舱门数量

                            //判断是什么样的柜子   1(默认) - 网络换电    2 - 离线换电
                            dataJsonObject1.put("isline", "-1");
                            //上传android设备名称，以后好区分android板
                            dataJsonObject1.put("androidSoft", MyApplication.device_model);
                            //上传本地数据库还有多少换电数据
                            dataJsonObject1.put("localExchanges", localExchanges);

                            dataJsonObject1.put("threadProtectionType", cabInfoSp.getTPTNumber()); //线程保护
                            //是否存在拓展卡
                            dataJsonObject1.put("isExCard", PubFunction.getIsExistExCard());


                            dataJsonObject1.put("acdc1", whichACDC[0]);
                            dataJsonObject1.put("acdc2", whichACDC[1]);
                            dataJsonObject1.put("ac1bsv", ACDC_SV[0]);
                            dataJsonObject1.put("ac2bsv", ACDC_SV[1]);
                            dataJsonObject1.put("ac1bhv", ACDC_HV[0]);
                            dataJsonObject1.put("ac2bhv", ACDC_HV[1]);
                            dataJsonObject1.put("ac1involt", ACDC_dianya_in[0] * 0.1);
                            dataJsonObject1.put("ac2involt", ACDC_dianya_in[1] * 0.1);
                            dataJsonObject1.put("ac1outvolt", ACDC_dianya_out[0] * 0.1);
                            dataJsonObject1.put("ac2outvolt", ACDC_dianya_out[1] * 0.1);
                            dataJsonObject1.put("ac1maxkw", ACDC_gonglv[0]);
                            dataJsonObject1.put("ac2maxkw", ACDC_gonglv[1]);
                            dataJsonObject1.put("ac1outele", ACDC_dianliu_in[0] * 0.1);
                            dataJsonObject1.put("ac2outele", ACDC_dianliu_in[1] * 0.1);
                            dataJsonObject1.put("maxpp", remainingTotalPower[0]);
                            //ACDC剩余总功率
                            dataJsonObject1.put("ac1spkw", remainingTotalPower2[0]);
                            dataJsonObject1.put("ac2spkw", remainingTotalPower2[1]);
                            //当前休眠状态
                            dataJsonObject1.put("ac1restate", sleepStatus[0]);
                            dataJsonObject1.put("ac2restate", sleepStatus[1]);

                            final char[] warningCodeChars = new char[8];
                            final StringBuilder stringBuilder = new StringBuilder(warningCodeChars.length);
                            Arrays.fill(warningCodeChars, '0');
                            char[] warningStatusChars = Integer.toHexString(ACDC_ERROR_state_01[0]).toCharArray();
                            System.arraycopy(warningStatusChars, 0, warningCodeChars, warningCodeChars.length - warningStatusChars.length, warningStatusChars.length);
                            dataJsonObject1.put("ac1inwarn", stringBuilder.append(warningCodeChars).toString());
                            stringBuilder.setLength(0);
                            warningStatusChars = Integer.toHexString(ACDC_ERROR_state_01[1]).toCharArray();
                            System.arraycopy(warningStatusChars, 0, warningCodeChars, warningCodeChars.length - warningStatusChars.length, warningStatusChars.length);
                            dataJsonObject1.put("ac2inwarn", stringBuilder.append(warningCodeChars).toString());

                            SensorDataBean sensorDataBean = SensorController.getInstance().getSensorDataBean();
                            if (sensorDataBean != null) {
                                dataJsonObject1.put("envtem1", sensorDataBean.getTemperature1()); //内部温度
                                dataJsonObject1.put("envtem2", sensorDataBean.getTemperature2()); //舱体和舱体附近的温度
                                dataJsonObject1.put("envtem3", sensorDataBean.getTemperature3()); //环境温度
                                dataJsonObject1.put("cab_ele", sensorDataBean.getAmmeterTotalPower()); //电表的用电量

                                dataJsonObject1.put("water1", sensorDataBean.getWaterLevel());
                                dataJsonObject1.put("water2", sensorDataBean.getWaterLevel2());
                                dataJsonObject1.put("envbsv", sensorDataBean.getSoftwareVersion());
                                dataJsonObject1.put("envbhv", Integer.toHexString(sensorDataBean.getHardwareVersion()).toUpperCase());
                                dataJsonObject1.put("smoke", sensorDataBean.getSmoke());

                                CurrentDetectionModel currentDetectionModel = CurrentDetectionController.getInstance().optCurrentDetectionModel();
                                if (currentDetectionModel != null && currentDetectionModel.isExistDevice()) {
                                    dataJsonObject1.put("curBoardStatus", currentDetectionModel.status_String);//电流板状态
                                    dataJsonObject1.put("curBoardVout", currentDetectionModel.outVoltage_String);//电流板输出电压
                                    dataJsonObject1.put("curBoardAout", currentDetectionModel.outCurrent_String);//电流板输出电流
                                    dataJsonObject1.put("curBoardErr", currentDetectionModel.outWarning_String);//电流板故障告警
                                    dataJsonObject1.put("curBoardVer", "SV:" + currentDetectionModel.getSoftwareVersion() + "/HV:" + currentDetectionModel.getHardwareVersion());//电流板版本信息
                                    dataJsonObject1.put("curBoardLitVal", cabInfoSp.optCurrentThreshold() + "A");//电流板阈值
                                    dataJsonObject1.put("curBoardMode：“", isAutoSetCurrentDetection ? "自动" : "手动");//电流板阈值
                                } else {
                                }
                                dataJsonObject1.put("pushRodTime", cabInfoSp.getpushrodActSetTime() / 10);//推杆时间
                            }

                            if (BAR_IS_RUN == 0) {
                                String str = dataJsonObject1.toString();
                                LogUtil.I("json:" + str);

                                HttpUploadBatteryInfo httpUploadBatteryInfo = new HttpUploadBatteryInfo(cabinetID, dataJsonObject1.toString(), ifHttpUploadBatteryInfoListener);
                                httpUploadBatteryInfo.start();
                            }

                            send_jsonarray = null;
                            send_jsonarray = new JSONArray();

                            break;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            //长链接下发  给服务器上传电柜里面的电池参数
            else if (type.equals("rentBtyList")) {
                syncControl(new Consumer() {
                    @NonNull
                    @Override
                    public String toString() {
                        return type;
                    }

                    @Override
                    public void accept(Object o) {
                        try {

                            final RentBtyListModel rentBtyListModel = gson.fromJson(orderString, RentBtyListModel.class);
                            final String uid32 = rentBtyListModel.getUid32();
                            writeLocalLog(orderString);
                            if (rentBtyListModel == null) {
                                return;
                            }

                            JSONObject jsonObject_1 = new JSONObject();
                            try {
                                jsonObject_1.put("did", rentBtyListModel.getDid());
                                jsonObject_1.put("uid", rentBtyListModel.getUid());
                                jsonObject_1.put("cid", cabinetID);
                                jsonObject_1.put("order_num", rentBtyListModel.getOrder_num());
                                jsonObject_1.put("cabid", cabInfoSp.getLongLinkNumber());

                                JSONArray jsonArray = new JSONArray();
                                for (int i = 0; i < DEFINE_BAR_COUNT; i++) {
                                    JSONObject jsonObject1 = new JSONObject();
                                    jsonObject1.put("bid", BIDS[i]);
                                    jsonObject1.put("per", PERCENtAGES[i]);
                                    jsonObject1.put("door", i + 1);
                                    jsonObject1.put("uid32", UIDS[i]);

                                    //第七次上传参数
                                    jsonObject_1.put("volt", bar_60_or_48(i + 1)); //上传电池的类型是什么类型的 现在又 48V和60V的
                                    jsonArray.put(jsonObject1);
                                }

                                jsonObject_1.put("data", jsonArray);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                writeLocalLog(e.toString());
                            }
                            writeLocalLog("json数据封装结束：" + jsonObject_1.toString());

                            HttpUploadBatteryInfoToRent httpUploadBatteryInfoToRent = new HttpUploadBatteryInfoToRent(jsonObject_1.toString(), rentBtyListModel.getUid() + "", new IFHttpUploadBatteryInfoToRentListener() {
                                @Override
                                public void onHttpUploadBatteryInfoToRentResult(String code, String str, String data) {
                                    try {
                                        if ("1".equals(code)) {
                                            showDialogInfo("正在绑定电池请稍后", "30", "1");

                                            JSONTokener jsonTokener = new JSONTokener(data);
                                            JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();

                                            String door_str = jsonObject.getString("door");
                                            String battery = jsonObject.getString("battery");
                                            final int door_int = Integer.parseInt(door_str);

                                            int tryWriteCount = 5;
                                            final long intervalTime = 3000;
                                            boolean isWriteSuccessed = false;
                                            writeLocalLog("准备给" + door_int + "号仓电池写入UID：" + uid32);
                                            while (tryWriteCount > 0) {
                                                //下发写入弹出电池ID
                                                writeBatteryCheckCode(uid32, door_int);
                                                SystemClock.sleep(intervalTime);
                                                if (UIDS[door_int - 1].equals(uid32)) {
                                                    isWriteSuccessed = true;
                                                    break;
                                                }
                                                tryWriteCount--;
                                            }
                                            HttpOutLineRentBattery httpOutLineRentBattery;
                                            if (isWriteSuccessed) {
                                                httpOutLineRentBattery = new HttpOutLineRentBattery(rentBtyListModel.getDid() + "", cabInfoSp.getCabinetNumber(), battery, door_str, "1");
                                            } else {
                                                httpOutLineRentBattery = new HttpOutLineRentBattery(rentBtyListModel.getDid() + "", cabInfoSp.getCabinetNumber(), battery, door_str, "0");
                                                writeLocalLog("给" + door_int + "号仓电池写入UID：" + uid32 + " 失败");
                                                showDialogInfo("租赁电池失败，请重试！", "10", "1");
                                            }

                                            httpOutLineRentBattery.setIfHttpOutLineRentBatteryUploadInfoConfirmListener(new IFHttpOutLineRentBatteryUploadInfoConfirmListener() {
                                                @Override
                                                public void onHttpOutLineRentBatteryUploadInfoConfirmResult(String code, String str, String data) {
                                                    writeLocalLog(str);
                                                    writeLocalLog(data);
                                                    if (!TextUtils.isEmpty(code) && "0".equals(code)) {
                                                        if (!TextUtils.isEmpty(str)) {
                                                            showDialogInfo(str, "10", "1");
                                                        }
                                                        return;
                                                    }
                                                    if (!TextUtils.isEmpty(code) && "1".equals(code)) {

                                                        showDialogInfo("租赁成功，请拿走" + door_int + " 舱门的电池！", "10", "1");
                                                        writeLocalLog("租赁成功，请拿走" + door_int + " 舱门的电池！");

//                                                        push("租赁电池", door_int + "");
//                                                        SystemClock.sleep(6000);
                                                        BatteryDataModel batteryDataModel = batteryDataModels.get(door_int - 1);
                                                        if (batteryDataModel != null) {
                                                            DoorController.getInstance().openDoor(batteryDataModel);
                                                        }

                                                        Bundle bundle10s = new Bundle();
                                                        Message message10s = new Message();
                                                        message10s.what = 3;
                                                        bundle10s.putInt("randomEmptyDoor", door_int);
                                                        message10s.setData(bundle10s);
                                                        openDoorButtonHandler.sendMessage(message10s);

                                                        Bundle bundle = new Bundle();
                                                        Message message1 = new Message();
                                                        message1.what = 1;
                                                        bundle.putInt("randomEmptyDoor", door_int);
                                                        message1.setData(bundle);
                                                        openDoorButtonHandler.sendMessageDelayed(message1, TimeUnit.MINUTES.toMillis(1));
                                                        writeLocalLog(door_int + "号仓门将在60秒后关闭");
                                                    }
                                                }
                                            });
                                            httpOutLineRentBattery.start();

                                        } else {
                                            writeLocalLog(str);
                                            if (!TextUtils.isEmpty(str)) {
                                                showDialogInfo(str, "10", "1");
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        writeLocalLog(e.toString());
                                    }
                                }
                            });
                            httpUploadBatteryInfoToRent.start();
                            writeLocalLog("租电池线程已经start");
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            writeLocalLog(ex.toString());
                        }
                    }
                });
            }


            //长链接下发 绑定长链接成功
            else if (type.equals("bindSuccess")) {
                String cabid = jsonObject.getString("cabid");
                cabInfoSp.setLongLinkNumber(cabid);

                //*********
                HttpGetDownloaderAndLauncher httpGetDownloaderAndLauncher = new HttpGetDownloaderAndLauncher(new HttpGetDownloaderAndLauncher.HttpGetDownloaderAndLauncherListener() {
                    @Override
                    public void returnMessage(String code, String msg, String data) {

                        try {

                            //网络版本
                            JSONObject jsonObjectVersion = new JSONObject(data);
                            String DownloaderVersion = jsonObjectVersion.getString("DownloaderVersion");
                            String LauncherVersion = jsonObjectVersion.getString("LauncherVersion");
                            String DownloaderUrl = jsonObjectVersion.getString("DownloaderUrl");
                            String LauncherUrl = jsonObjectVersion.getString("LauncherUrl");

                            //本地版本
                            PackageManager packageManager = getPackageManager();
                            PackageInfo packageInfo = packageManager.getPackageInfo("com.NewElectric.app4", 0);
                            int nowDownloaderVersion = packageInfo.versionCode;
                            PackageInfo packageInfoLauncher = packageManager.getPackageInfo("com.NewElectric.app5", 0);
                            int nowLauncherVersion = packageInfoLauncher.versionCode;

                            if(Integer.parseInt(DownloaderVersion) > nowDownloaderVersion){
                                DownLoadUpdateMain downLoadApk = new DownLoadUpdateMain(activity, DownloaderUrl, "app4.apk", new IFDownLoadUpdateMainLinstener() {
                                    @Override
                                    public void onDownLoadUpdateMainResult(int[] data) {
                                    }
                                });
                                downLoadApk.downloadAPK();
                                if (localLog != null) {
                                    localLog.writeLog("下载 - 正在下载安装下载器");
                                }
                                showDialogInfo("正在下载下载器！", "3", "2");
                            }

                            if(Integer.parseInt(LauncherVersion) > nowLauncherVersion){
                                DownLoadUpdateMain downLoadApk = new DownLoadUpdateMain(activity, LauncherUrl, "app5.apk" , 1 , new IFDownLoadUpdateMainLinstener() {
                                    @Override
                                    public void onDownLoadUpdateMainResult(int[] data) {
                                    }
                                });
                                downLoadApk.downloadAPK();
                                if (localLog != null) {
                                    localLog.writeLog("下载 - 正在下载安装启动器");
                                }
                                showDialogInfo("正在下载启动器！", "3", "2");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                httpGetDownloaderAndLauncher.start();
            }

            //长链接下发伸长推杆 和收回
            else if (type.equals("disableDoorOut")) {
                final String door = jsonObject.getString("door");
                String outIn = jsonObject.getString("outIn");
                forbiddenSp.setTargetForbidden(Integer.parseInt(door) - 1, Integer.parseInt(outIn));

                if (outIn.equals("-2")) {
                    syncControl(new Consumer() {
                        @Override
                        public void accept(Object o) {
                            //伸出
                            Message message = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("door", (door) + "");
                            bundle.putBoolean("isBreakForbidden", true);
                            message.setData(bundle);
                            adminElongationHandler.sendMessage(message);
                            SystemClock.sleep(6000);
                        }
                    });
                } else if (outIn.equals("-1")) {
                    syncControl(new Consumer() {
                        @Override
                        public void accept(Object o) {
                            //收回
                            Message message = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("door", (door) + "");
                            message.setData(bundle);
                            adminShrinkHandler.sendMessage(message);
                            SystemClock.sleep(6000);
                        }
                    });
                } else if (outIn.equals("1")) {
                    // TODO: 2020/7/2 1是启动仓门，不做任何处理
                }

                Message message_ui = new Message();
                Bundle bundle_ui = new Bundle();
                bundle_ui.putInt("address", Integer.parseInt(door));
                message_ui.setData(bundle_ui);
                setUiHandler.sendMessage(message_ui);

            }  //长链接下发 更新电表信息
            else if (type.equals("setThreadsProtectionStatus")) {

                String threadType = jsonObject.getString("setStatus");
                if (threadType.equals("0")) {
                    cabInfoSp.setTPTNumber("0");
                    showDialogInfo("正在关闭线程保护 请稍后！", 10 + "", 1 + "");
                } else {
                    cabInfoSp.setTPTNumber("1");
                    showDialogInfo("正在打开线程保护 请稍后！", 10 + "", 1 + "");
                }
            }//电池升级
            else if (type.equals("updateOneBattery") && upgrading.isEmpty()) {
                try {
                    upgrading.add("updateOneBattery");
                    String url = jsonObject.getString("url");
                    String door = jsonObject.getString("door");
                    String name = jsonObject.getString("name");
                    String ver = jsonObject.getString("ver");
                    String battery = jsonObject.getString("battery");
                    String fname = jsonObject.getString("fname");
                    String manu = jsonObject.getString("manu");
                    showDialogInfo("准备升级" + door + "号电池，请勿进行操作！", "10", 1 + "");

                    UpdataBattery updataBattery = new UpdataBattery(activity, url, fname, door, manu, updataBatteryDownlondReturnHandler);
                    updataBattery.downloadAPK();
                } catch (Exception ex) {
                    writeLocalLog(ex.getMessage());
                    upgrading.clear();
                    ex.printStackTrace();
                }

            }//长链接下发 回传目录信息
            else if (type.equals("upVideoFileList")) {

                LogUtil.I("movies：" + jsonObject.toString());

                String cabid = jsonObject.getString("cabid");
                String admid = jsonObject.getString("admid");
                String upUrl = jsonObject.getString("upUrl");
                String logintk = jsonObject.getString("_logintk_");
                String remark = jsonObject.getString("remark");
                String date = jsonObject.getString("date");
                String hour = jsonObject.getString("hour");
                String level = jsonObject.getString("level");

                HttpUploadMoviesPath httpUploadMoviesPath = new HttpUploadMoviesPath(activity, cabid, admid, upUrl, logintk, date, hour, level);
                httpUploadMoviesPath.start();

            }

            //长链接下发 回传视频信息
            else if (type.equals("upVideoFile")) {

                LogUtil.I("movies：" + jsonObject.toString());
                String cabid = jsonObject.getString("cabid");
                String vname = jsonObject.getString("vname");
                String admid = jsonObject.getString("admid");
                String upUrl = jsonObject.getString("upUrl");
                String upField = jsonObject.getString("upField");
                String token = jsonObject.getString("_token");
                String hour = jsonObject.getString("hour");
                String day = jsonObject.getString("day");

                HttpUploadMovies httpUploadMovies = new HttpUploadMovies(activity);
                httpUploadMovies.httpPost(upUrl,
                        MoviesCreateFile.OutSideSd + "/MyCameraApp" + File.separator + day + File.separator + hour + File.separator + vname,
                        vname, cabid, admid, token, upField, uploadPbarHandler);
            } else if (type.equals("writeBtyUid")) {

                String uid32 = jsonObject.getString("uid32");
                String door = jsonObject.getString("door");
                String battery = jsonObject.getString("battery");
                String outType = jsonObject.getString("outType");
                writeLocalLog("长连接下发设置UID：" + jsonObject);

                final int fOutDoorBarIndex = Integer.parseInt(door) - 1;
                final String fUid32 = uid32;
                final String fUid10 = Long.parseLong(uid32, 32) + "";
                final String fOutType = outType;
                final String fBattery = battery;
                final String fDoor = door;
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        //做超时等待
                        int result_out = 0;
                        for (int i = 0; i < 150; i++) {
                            if (i == 0 || i == 50 || i == 100) {
                                //下发写入弹出电池ID
                                writeBatteryCheckCode(fUid32, fOutDoorBarIndex + 1);
                            }
                            try {
                                sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (UIDS[fOutDoorBarIndex].equals(fUid32)) {
                                result_out = 1;
                                break;
                            }
                        }

                        if (result_out == 0) {
                            //没有写入成功
                            if (exchangeFailCount == 0) {
                                showDialogInfo("电池写入失败，正在尝试二次写入，请稍候！！", "10", "1");
                                Thread thread1 = new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        try {
                                            sleep(10000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }

                                        onHttpReturnDataResult(orderString.toString());
                                        exchangeFailCount = 1;
                                    }
                                };
                                thread1.start();
                            } else {
                                showDialogInfo("电池写入失败，请拨打客服电话", "7", "1");
                                exchangeFailCount = 0;
                            }
                        } else {
                            exchangeFailCount = 0;
                            if (fOutType.equals("2")) {
                                pushAndPull("写入电池UID，并弹出电池", (fOutDoorBarIndex + 1) + "", 10);
                                showDialogInfo("电池写入成功！！请手机尾号" + UidDictionart.getI10EndPhoneNumber(fUid10) + "的用户拿走第" + (fOutDoorBarIndex + 1) + "号仓门电池", "20", "1");
                            }
                            HttpWriteUidRet httpWriteUidRet = new HttpWriteUidRet(cabinetID, fDoor, fBattery, fUid32);
                            httpWriteUidRet.start();
                        }
                    }
                };
                thread.start();
            } else if (type.equals("defsetDcdc")) {
                LogUtil.I("defsetDcdc:" + jsonObject.toString());
                float hatchDoorTemperature = 0;
                float batteryInnerEndTemperature = 0;
                float batteryInnerStartTemperature = 0;
                SettingConfig.getInstance().setHeating(hatchDoorTemperature, batteryInnerEndTemperature, batteryInnerStartTemperature);
            } else if (type.equals("setStopDcdc")) {
                LogUtil.I("setStopDcdc:" + jsonObject.toString());
                try {
                    String door = jsonObject.getString("door");
                    String setdc = jsonObject.getString("setdc");
                    int doorNumber = Integer.parseInt(door);
                    int dcStatus = Integer.parseInt(setdc);
                    SettingConfig.getInstance().setForbiddenUseEnable(doorNumber, (byte) dcStatus);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (type.equals("resetDcdc")) {
                LogUtil.I("resetDcdc:" + jsonObject.toString());
                String door = jsonObject.getString("door");
                try {
                    if (!TextUtils.isEmpty(door)) {
                        SettingConfig.getInstance().setOvervoltageReset(Integer.parseInt(door));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (type.equals("remoteSprayWater")) {

                String door = jsonObject.getString("door");
                String opts = jsonObject.getString("opts");
                if (!TextUtils.isEmpty(door) && !TextUtils.isEmpty(opts)) {
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    switch (opts) {
                        case "1":
                            bundle.putString("door", door);
                            message.setData(bundle);
                            A_Main2.chargeOnHandler.sendMessage(message);
                            break;
                        case "-1":
                            bundle.putString("door", door);
                            message.setData(bundle);
                            A_Main2.chargeOffHandler.sendMessage(message);
                            break;
                    }
                }
                FireSwitchController.getInstance().control();
            } else if (type.equals("pushrodActSetTime")) {
                String timeStr = jsonObject.optString("vals", "");
                float time = 0;
                if (!TextUtils.isEmpty(timeStr)) {
                    try {
                        time = Float.parseFloat(timeStr);
                        if (time >= 3 && time <= 15) {
                            pushrodActSetTime = (byte) (time * 10);
                            if (batteryDataModels != null && !batteryDataModels.isEmpty()) {
                                for (BatteryDataModel batteryDataModel : batteryDataModels) {
                                    batteryDataModel.setRodActionTime(pushrodActSetTime);
                                }
                            }
                            cabInfoSp.setpushrodActSetTime(pushrodActSetTime);
                            String name = jsonObject.optString("name", "");
                            if (!TextUtils.isEmpty(name)) {
                                showDialogInfo(name + time + "秒", "5", "1");
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            } else if (type.equals("setPushRodLitVal")) {//设置推杆阈值
                try {
                    SetPushRodLitValModel setPushRodLitValModel = gson.fromJson(orderString, SetPushRodLitValModel.class);
                    if (setPushRodLitValModel.getLitVal() >= 1.5f && setPushRodLitValModel.getLitVal() <= 10) {
                        isAutoSetCurrentDetection = setPushRodLitValModel.getIsAuto() == 1;
                        cabInfoSp.setAutoSetCurrentDetectionStatus(setPushRodLitValModel.getIsAuto() == 1);
                        if (setPushRodLitValModel.getIsAuto() == -1) {
                            cabInfoSp.setCurrentThreshold(setPushRodLitValModel.getLitVal());
                            CurrentDetectionController.getInstance().setCurrentDetection(setPushRodLitValModel.getLitVal());
                        } else {
                            temperature = -100;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (type.equals("removeSetHeatMode")) {
                LogUtil.I("加热模式：");
                String isHeat = jsonObject.optInt("isheat", 2) + "";
                LogUtil.I("加热模式：" + isHeat);
                if (!TextUtils.isEmpty(isHeat)) {
                    LogUtil.I("加热模式：" + isHeat);
                    cabInfoSp.saveHeatMode(isHeat);
                    switch (isHeat) {
                        case "1"://加热
                            LogUtil.I("加热模式：加热");
                            SettingConfig.getInstance().setDefaultHeating();
                            break;
                        case "2"://自动
                            LogUtil.I("加热模式：自动");
                            SettingConfig.getInstance().autoHeating();
                            break;
                        case "-1"://停止
                            LogUtil.I("加热模式：停止");
                            SettingConfig.getInstance().setHeatingUnable();
                            break;
                    }
                }
            } else {
                LogUtil.I("长连接：" + type);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取一个空仓门
     *
     * @return
     */
    private int obtainEmptyDoor() {
        int emptyDoor = -1;
        for (int i = 0, len = BIDS.length; i < len; i++) {
            int is_stop = forbiddenSp.getTargetForbidden(i);

            // TODO: 2020/7/15 开空仓门条件：锁微动=0，侧微动=1，BID=0，不是禁用
            if (DOORS[i] == 0 && SMALLS[i] == 0
                    && BIDS[i].equals("0000000000000000")
                    && is_stop == 1) {
                return -2;
            }

            if (DOORS[i] == 0 && SMALLS[i] == 1
                    && BIDS[i].equals("0000000000000000")
                    && is_stop == 1) {
                emptyDoor = i + 1;
                break;
            }
        }
        return emptyDoor;
    }

    private int obtainNextEmptyDoor() {
        int emptyDoor = -1;
        for (int i = 0, len = BIDS.length; i < len; i++) {
            int is_stop = forbiddenSp.getTargetForbidden(i);

            if (DOORS[i] == 0 && SMALLS[i] == 1
                    && BIDS[i].equals("0000000000000000")
                    && is_stop == 1) {
                emptyDoor = i + 1;
                break;
            }
        }
        return emptyDoor;
    }

    private void openEmptyDoor() {
        boolean isFind = false;
        int i = 0;
        int len = BIDS.length;
        for (; i < len; i++) {
            int is_stop = forbiddenSp.getTargetForbidden(i);

            // TODO: 2020/7/15 开空仓门条件：锁微动=0，侧微动=1，BID=0，不是禁用
            if (DOORS[i] == 0 && SMALLS[i] == 0
                    && BIDS[i].equals("0000000000000000")
                    && is_stop == 1) {
                isFind = true;
                i++;
                break;
            }
        }

        if (isFind) {
            for (; i < len; i++) {
                int is_stop = forbiddenSp.getTargetForbidden(i);

                // TODO: 2020/7/15 开空仓门条件：锁微动=0，侧微动=1，BID=0，不是禁用
                if (DOORS[i] == 0 && SMALLS[i] == 0
                        && BIDS[i].equals("0000000000000000")
                        && is_stop == 1) {
                    final int doornum = i + 1;
                    syncControl(new Consumer() {
                        @Override
                        public void accept(Object o) {
                            showDialogInfo("准备关闭" + doornum + "号空仓门，请注意安全", "10", "1");
//                            pull("", doornum + "");
//                            testStop(doornum);
                            BatteryDataModel batteryDataModel = batteryDataModels.get(doornum - 1);
                            if (batteryDataModel != null) {
                                DoorController.getInstance().closeDoor(batteryDataModel);
                            }
                        }
                    });
                }
            }
        } else {
            final int emptyDoor = obtainEmptyDoor();
            if (!isValidDoor(emptyDoor)) {
                showDialogInfo("没有可用仓门，如有问题请拨打客服电话咨询！", "7", "1");
            } else {
                syncControl(new Consumer() {
                    @Override
                    public void accept(Object o) {
//                        push("", emptyDoor + "");
//                        SystemClock.sleep(6000);
                        BatteryDataModel batteryDataModel = batteryDataModels.get(emptyDoor - 1);
                        DoorController.getInstance().openDoor(batteryDataModel);
                    }
                });
            }
        }
    }

    /**
     * 验证是否是有效仓门
     *
     * @param door
     * @return
     */
    private boolean isValidDoor(int door) {
        return door >= 1 && door <= 9;
    }

    private void startNewTask(final Consumer consumer) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (consumer != null) {
                    consumer.accept(Thread.currentThread().getName());
                }
            }
        }).start();
    }

    private LinkedBlockingQueue<Consumer> pushRodLinkedBlockingQueue;

    private void setRebootAppTime() {
        //每天凌晨2：30重启Android环境板
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 2);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.DAY_OF_MONTH, 1);

        final long atTime = calendar.getTimeInMillis();
        new Handler().postAtTime(new Runnable() {
            @Override
            public void run() {
                DeviceSwitchController.getInstance().control(DeviceSwitcher.CMD.ANDROID_12V_REBOOT);
                setRebootAppTime();
            }
        }, atTime);
    }

    private static volatile boolean isExeTask;
    private static LinkedBlockingQueue<Consumer> replaceLinkedBlockingQueue;

    /**
     * @param consumer
     */
    public static void syncControl(Consumer consumer) {
        if (consumer != null) {

            if (replaceLinkedBlockingQueue == null) {
                replaceLinkedBlockingQueue = new LinkedBlockingQueue<>();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            Consumer c = null;
                            try {
                                c = replaceLinkedBlockingQueue.take();
                                isExeTask = true;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (c != null) {
                                LogUtil.I("换电任务开始：" + c.toString());
                                c.accept(null);
                                LogUtil.I("换电任务结束：" + c.toString());
                                Consumer c2 = replaceLinkedBlockingQueue.peek();
                                if (c2 != null && c2.equals(c)) {
                                    replaceLinkedBlockingQueue.poll();
                                }
                            }
                            isExeTask = false;
                        }
                    }
                }, "同步线程").start();
            }

            if (!replaceLinkedBlockingQueue.contains(consumer)) {
                replaceLinkedBlockingQueue.offer(consumer);
            }
        }
    }

    final byte[] frameData = new byte[]{0x65, 0x66, (byte) 0xB0, (byte) 0x98,
            0x08,
            0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

    private void jumpAppProgram() {
        final byte[] srcData = {(byte) 0xB0, 0x05, 0x00, 0x0A, 0x00, 0x08, 0x00, 0x01, (byte) 0xC6, 0x7C};
        try {
            convertData(frameData, srcData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void convertData(byte[] data, byte[] srcData) throws IOException {

        final int count = srcData.length / 7;
        int frameSn = 0x10;
        data[4] = 0x08;
        for (int i = 0, pos = 0; i < count; i++, pos += 7) {
            data[8] = (byte) frameSn;
            System.arraycopy(srcData, pos, data, 9, 7);
            MyApplication.serialAndCanPortUtils.canSendOrder(data);
            SystemClock.sleep(20);
            frameSn = frameSn == 0x10 ? 0x20 : ++frameSn;
            if (frameSn > 0xFF) {
                frameSn = 0x10;
            }
        }
        if (srcData.length % 7 > 0) {
            int pos = srcData.length - srcData.length % 7;
            int len_ = srcData.length % 7;
            data[4] = (byte) (len_ + 1);
            data[8] = (byte) frameSn;
            System.arraycopy(srcData, pos, data, 9, len_);
            MyApplication.serialAndCanPortUtils.canSendOrder(data);
        }
    }

    private static void writeLocalLog(String log) {
        LogUtil.I("日志：" + log);
        if (localLog != null) {
            localLog.writeLog(log);
        }
    }

    private String bar_60_or_48(int door) {
        String BidStr = BIDS[door - 1];
        String TopBidStr = BidStr.substring(0, 1);
        String EndBidStr = BidStr.substring(10, 12);
        String volt = "";
        if (TopBidStr.equals("M") && EndBidStr.equals("MM")) {
            volt = "60";
        } else {
            volt = "48";
        }
        return volt;
    }

    /**
     * 获取一个已经打开的空仓门
     *
     * @return
     */
    private int obtainOpenedEmptyDoor() {
        int emptyDoor = -1;
        for (int i = 0, len = BIDS.length; i < len; i++) {
            int is_stop = forbiddenSp.getTargetForbidden(i);

            // TODO: 2020/7/15 开空仓门条件侧微动=1，BID=0，不是禁用
            if (SMALLS[i] == 0
                    && is_stop == 1) {
                emptyDoor = i + 1;
                break;
            }
        }
        return emptyDoor;
    }


    /**
     * 获取随机可用空仓门
     *
     * @return
     */
    private int obtainRandomEmptyDoor() {
        int emptyDoor = -1;
        final List<Integer> emptyDoors = new ArrayList<>();

        for (int i = 0, len = BIDS.length; i < len; i++) {
            int is_stop = forbiddenSp.getTargetForbidden(i);
            if (DOORS[i] == 0 && SMALLS[i] == 1
                    && BIDS[i].equals("0000000000000000")
                    && is_stop == 1) {
                emptyDoors.add(i + 1);
            }
        }
        if (!emptyDoors.isEmpty()) {
            long ctm = System.currentTimeMillis();
            int number = (int) (ctm % 10);
            return emptyDoors.get(number % emptyDoors.size());
        }

        return emptyDoor;
    }

    private BatteryDataModel obtainRandomEmptyDoorModel() {
        return batteryDataModels.get(obtainRandomEmptyDoor() - 1);
    }


    /**
     * 子线程访问
     *
     * @param doorNumber
     */
    public static void testStop(int doorNumber) {
        final long stopTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(6);
        while (System.currentTimeMillis() < stopTime) {
            SystemClock.sleep(10);
            if (SMALLS[doorNumber - 1] == 1) {
                // TODO: 2020/10/15 发送停止推杆
                byte[] a_1 = new byte[]{0x00, pushrodActSetTime};
                String b_1 = "98030" + doorNumber + "65";
                MyApplication.serialAndCanPortUtils.canSendOrder(b_1, a_1);
                break;
            }
        }

        long addtime = stopTime - System.currentTimeMillis();
        if (addtime > 0) {
            SystemClock.sleep(addtime);
        }
    }

    private void clearUid() {
        LogUtil.I("检查擦除UID");
        if (!isExeTask) {
            for (int i = 0; i < UIDS.length && !isExeTask; i++) {
                int is_stop = forbiddenSp.getTargetForbidden(i);
                if (!TextUtils.isEmpty(UIDS[i]) && !"AAAAAAAA".equals(UIDS[i]) && !BIDS[i].equals("FFFFFFFFFFFFFFFF")
                        && !BIDS[i].equals("0000000000000000") && SMALLS[i] == 1 && DOORS[i] == 1 && is_stop == -3) {
                    writeBatteryCheckCode("AAAAAAAA", i + 1);
                    forbiddenSp.setTargetForbidden(i, -1);
                }
            }
        }
    }

    private void initErrorHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                LogUtil.I(t.toString() + "-" + e.toString());
                writeLocalLog(t.toString() + "-" + e.toString());
            }
        });

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                SystemClock.sleep(30 * 1000);
//                LogUtil.I("执行异常");
//                String s = "";
//                int a = Integer.parseInt(s);
//            }
//        }).start();
    }

    private int[] overTimes = new int[9];
    private int[] turnOn = new int[9];
    private int[] turnOff = new int[9];
    int aa = 30;

}

