package client.halouhuandian.app15.view.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import client.halouhuandian.app15.R;
import client.halouhuandian.app15.config.SystemConfig;
import client.halouhuandian.app15.hardWareConncetion.daa.DaaController;
import client.halouhuandian.app15.hardWareConncetion.daa.DaaIntegration;
import client.halouhuandian.app15.hardWareConncetion.daa.mode.DaaDataFormat;
import client.halouhuandian.app15.hardWareConncetion.daa.mode.dcdc.DcdcInfoByBaseFormat;
import client.halouhuandian.app15.hardWareConncetion.environmentPlate.EnvironmentController;
import client.halouhuandian.app15.hardWareConncetion.environmentPlate.EnvironmentDataFormat;
import client.halouhuandian.app15.hardWareConncetion.environmentPlate.EnvironmentReturnDataFormat;
import client.halouhuandian.app15.model.dao.fileSave.LocalLog;
import client.halouhuandian.app15.model.dao.sharedPreferences.CabInfoSp;
import client.halouhuandian.app15.model.dao.sharedPreferences.ForbiddenSp;
import client.halouhuandian.app15.pub.BaseDataDistribution;
import client.halouhuandian.app15.pub.RootCmd;
import client.halouhuandian.app15.pub.util.UtilBattery;
import client.halouhuandian.app15.pub.util.UtilPublic;
import client.halouhuandian.app15.service.logic.logicChangeBatteries.ChangeBatteriesChangeDataFormat;
import client.halouhuandian.app15.service.logic.logicChangeBatteries.ChangeBatteriesController;
import client.halouhuandian.app15.service.logic.logicChangeBatteries.ChangeBatteriesReturnDataFormat;
import client.halouhuandian.app15.service.logic.logicFind4gCard.Find4gCard;
import client.halouhuandian.app15.service.logic.logicFind4gCard.Find4gCardReturnDataFormat;
import client.halouhuandian.app15.service.logic.logicHttpConnection.http.HttpUrlMap;
import client.halouhuandian.app15.service.logic.logicHttpConnection.http.mode.WebSocketUpdateHardWareDataFormat;
import client.halouhuandian.app15.service.logic.logicHttpConnection.webSocket.WebSocketController;
import client.halouhuandian.app15.service.logic.logicHttpConnection.webSocket.mode.WebSocketReturnDataFormat;
import client.halouhuandian.app15.service.logic.logicHttpConnection.webSocket.mode.WebSocketReturnType;
import client.halouhuandian.app15.service.logic.logicMovies.MoviesController;
import client.halouhuandian.app15.service.logic.logicNetDBM.DataDistributionCurrentNetDBM;
import client.halouhuandian.app15.service.logic.logicTTL.TextToSpeechController;
import client.halouhuandian.app15.service.logic.logicTimeThread.TimeThread;
import client.halouhuandian.app15.service.logic.logicTimeThread.TimeThreadDataFormat;
import client.halouhuandian.app15.service.logic.logicTimeThread.TimeThreadDataType;
import client.halouhuandian.app15.view.activity.admin.A_Admin_9;
import client.halouhuandian.app15.view.activity.BaseActivity;
import client.halouhuandian.app15.view.activity.update.A_UpdateAcdc;
import client.halouhuandian.app15.view.activity.update.A_UpdateBattery;
import client.halouhuandian.app15.view.activity.update.A_UpdateDcdc;
import client.halouhuandian.app15.view.activity.update.A_UpdateEnvironment;
import client.halouhuandian.app15.view.customUi.activityAnimation.ExchangeBarAnimation;
import client.halouhuandian.app15.view.customUi.activityDialog.DialogMainShowInfo;

import static java.lang.Thread.sleep;

/**
 * 主屏幕 - 前台显示
 */
public class A_Main_Hello_9 extends BaseActivity {

    //舱门view
    @BindViews({R.id.bar_r_image_1, R.id.bar_r_image_2, R.id.bar_r_image_3, R.id.bar_r_image_4, R.id.bar_r_image_5, R.id.bar_r_image_6, R.id.bar_r_image_7, R.id.bar_r_image_8, R.id.bar_r_image_9})
    public List<ImageView> bar_r_images;
    @BindViews({R.id.bar_r_text_1, R.id.bar_r_text_2, R.id.bar_r_text_3, R.id.bar_r_text_4, R.id.bar_r_text_5, R.id.bar_r_text_6, R.id.bar_r_text_7, R.id.bar_r_text_8, R.id.bar_r_text_9})
    public List<TextView> bar_r_texts;
    @BindViews({R.id.bar_not_image_1, R.id.bar_not_image_2, R.id.bar_not_image_3, R.id.bar_not_image_4, R.id.bar_not_image_5, R.id.bar_not_image_6, R.id.bar_not_image_7, R.id.bar_not_image_8, R.id.bar_not_image_9,})
    public List<ImageView> bar_not_images;
    //动画相关view
    @BindView(R.id.black_1)
    public ImageView black_1;
    @BindView(R.id.black_2)
    public ImageView black_2;
    @BindView(R.id.up_bar_1)
    public ImageView up_bar_1;
    @BindView(R.id.up_bar_2)
    public ImageView up_bar_2;
    @BindView(R.id.up_bar_1_text)
    public TextView up_bar_1_text;
    @BindView(R.id.up_bar_2_text)
    public TextView up_bar_2_text;
    @BindView(R.id.up_bar_1_panel)
    public FrameLayout up_bar_1_panel;
    @BindView(R.id.up_bar_2_panel)
    public FrameLayout up_bar_2_panel;
    @BindView(R.id.cost_bi) //扣币类型显示
    public TextView coinDeductionView;
    @BindView(R.id.cost) //扣币类型显示
    public LinearLayout costView;

    //租电池二维码
    @BindView(R.id.rent_bar_qcode)
    public ImageView rentBarQRcodeView;
    //扫码下载app二维码
    @BindView(R.id.download_qcode)
    public ImageView downloadQRcodeView;
    //站点编号
    @BindView(R.id.cab_id)
    public TextView cabIdView;
    //站点名称
    @BindView(R.id.now_address)
    public TextView addressView;
    //当前时间
    @BindView(R.id.now_time)
    public TextView nowTimeView;
    //400电话
    @BindView(R.id.tel_text_1)
    public TextView phoneView;
    //程序版本
    @BindView(R.id.version)
    public TextView versionView;
    //右下角图标显示
    @BindView(R.id.b_3) //信号强弱图标
    public ImageView signalView;
    @BindView(R.id.t_5)
    public TextView eleMeterView; //电表走字显示
    @BindView(R.id.t_6)
    public TextView innerTem; //柜内温度
    @BindView(R.id.t_7)
    public TextView outerTem; //柜外温度
    @BindView(R.id.t_8)
    public TextView powerView; //当前功率
    @BindView(R.id.t_9)
    public TextView isOnlineView; //温度显示
    //提示框
    @BindView(R.id.dialog_panel)
    public LinearLayout dialogPanelView;
    @BindView(R.id.dialog_time)
    public TextView dialogTimeView;
    @BindView(R.id.dialog_info)
    public TextView dialogInfoView;
    @BindView(R.id.dialog_background) // 提示框
    public LinearLayout dialogBackground;
    //摄像头显示
    @BindView(R.id.camera_surface_view)
    public SurfaceView cameraSurfaceView;
    @BindView(R.id.camera_preview_panel)
    public LinearLayout cameraPrePanelView;
    @BindView(R.id.camera_preview_panel_text)
    public TextView cameraPrePanelTitleView;
    //广告显示
    @BindView(R.id.adv_webview)
    public WebView advertisementWebView;
    //广告默认显示
    @BindView(R.id.adv_defult)
    public ImageView adv_defult;
    //移动提示框
    private DialogMainShowInfo AMainSpeakDialog;

    //dbm
    private int dbm = -1;
    //显示的环境板信息
    private String fCount = "";
    private String fInnerTemStr = "";
    private String fOuterTemStr = "";
    private String fUsefulPowerSer = "";

    //daa监听
    private DaaController.DaaControllerListener daaControllerListener;
    //dbm监听
    private BaseDataDistribution.LogicListener dbmLogicListener;
    //4g卡监听
    private BaseDataDistribution.LogicListener _4gLogicListener;
    //环境板监听
    private BaseDataDistribution.LogicListener environmentControllerListener;
    //长连接监听
    private BaseDataDistribution.LogicListener webSocketListener;
    //时间线程监听
    private BaseDataDistribution.LogicListener timeListener;
    //换电监听
    private BaseDataDistribution.LogicListener exchangeBatteriesListener;
    //dcdc数据缓存
    private DaaDataFormat daaDataFormat = null;
    //录制视频
    private MoviesController moviesController = null;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_1080p_org);
        ButterKnife.bind(this);

        //删除本地webView缓存
        deleteDatabase("WebView.db");
        advertisementWebView.clearHistory();
        advertisementWebView.clearFormData();
        getCacheDir().delete();

        //初始化信息
        writeLog("电柜初始化");
        showDialogInfo("电柜正在初始化，请稍候！", 30, 0);
        if(WebSocketController.getInstance().getHasReceiverCabId() == null){
            cabIdView.setText("D" + CabInfoSp.getInstance().getCabinetNumber_XXXXX());
        }else{
            cabIdView.setText(WebSocketController.getInstance().getHasReceiverCabId());
        }
        setTel(CabInfoSp.getInstance().getTelNumber());
        versionView.setText("Ver：" + CabInfoSp.getInstance().getVersion());
        rentBarQRcodeView.setImageBitmap(UtilPublic.generateBitmap(CabInfoSp.getInstance().getCabinetNumber_4600XXXX() + "/" + "0000000000000000" + "/" + "0", 400, 400));
        rentBarQRcodeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogInfo("正在进行内存测试", 20, 1);
                ChangeBatteriesChangeDataFormat changeBatteriesChangeDataFormat = new ChangeBatteriesChangeDataFormat(1, "111", 10, 2, "222", 20, "测试", "测试");
                startAnimation(changeBatteriesChangeDataFormat);
            }
        });
        downloadQRcodeView.setImageBitmap(UtilPublic.generateBitmap(HttpUrlMap.DownLoadApkJump, 400, 400));
        downloadQRcodeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(new Intent(activity, A_Admin_9.class));
            }
        });

        //摄像头初始化
        setMovies();
        //监听初始化
        initDataService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (moviesController != null) {
            moviesController.onResume(cameraSurfaceView);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (moviesController != null) {
            moviesController.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解除注册
        AMainSpeakDialog.onDestroy();
        DaaController.getInstance().deleteListener(daaControllerListener);
        DataDistributionCurrentNetDBM.getInstance().deleteListener(dbmLogicListener);
        Find4gCard.getInstance().deleteListener(_4gLogicListener);
        EnvironmentController.getInstance().deleteListener(environmentControllerListener);
        TimeThread.getInstance().deleteListener(timeListener);
        WebSocketController.getInstance().deleteListener(webSocketListener);
        WebSocketController.getInstance().onDestroy();
        ChangeBatteriesController.getInstance().deleteListener(exchangeBatteriesListener);
    }

    private void initDataService() {
        //dcdc数据缓存
        DaaController.getInstance().addListener(daaControllerListener = new DaaController.DaaControllerListener() {
            @Override
            public void returnData(DaaDataFormat mDaaDataFormat, DaaIntegration.ReturnDataType returnDataType, int index) {
                daaDataFormat = mDaaDataFormat;
                if (returnDataType.equals(DaaIntegration.ReturnDataType.dcdcInfoByBase)) {
                    updateBattery(index + 1);
                }
            }
        });
        //dbm数据
        DataDistributionCurrentNetDBM.getInstance().addListener(dbmLogicListener = new BaseDataDistribution.LogicListener() {
            @Override
            public void returnData(Object object) {
                dbm = (int) object;
            }
        });
        //4g卡准备
        Find4gCard.getInstance().addListener(_4gLogicListener = new BaseDataDistribution.LogicListener() {
            @Override
            public void returnData(Object object) {
                Find4gCardReturnDataFormat find4gCardReturnDataFormat = (Find4gCardReturnDataFormat) object;
                if (find4gCardReturnDataFormat.getFind4gCardReturnDataType() == Find4gCardReturnDataFormat.Find4gCardReturnDataType.IMSI) {
                    showDialogInfo("电柜初始化成功", 10, 0);
                } else if (find4gCardReturnDataFormat.getFind4gCardReturnDataType() == Find4gCardReturnDataFormat.Find4gCardReturnDataType.error) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                showDialogInfo("未检测到4g卡信息，电柜将在20秒后重启", 20, 1);
                                sleep(1000 * 20);
                                RootCmd.execRootCmd("reboot");
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });
        //换电线程初始化
        ChangeBatteriesController.getInstance().addListener(exchangeBatteriesListener = new BaseDataDistribution.LogicListener() {
            @Override
            public void returnData(Object object) {
                ChangeBatteriesReturnDataFormat changeBatteriesReturnDataFormat = (ChangeBatteriesReturnDataFormat) object;
                if (changeBatteriesReturnDataFormat.getChangeBatteriesReturnDataType() == ChangeBatteriesReturnDataFormat.ChangeBatteriesReturnDataType.info) {
                    String dialogInfo = (String) changeBatteriesReturnDataFormat.getObject();
                    showDialogInfoByJson(dialogInfo);
                } else if (changeBatteriesReturnDataFormat.getChangeBatteriesReturnDataType() == ChangeBatteriesReturnDataFormat.ChangeBatteriesReturnDataType.data) {
                    ChangeBatteriesChangeDataFormat changeBatteriesChangeDataFormat = (ChangeBatteriesChangeDataFormat) changeBatteriesReturnDataFormat.getObject();
                    startAnimation(changeBatteriesChangeDataFormat);
                }
            }
        });
        //环境板数据监听
        EnvironmentController.getInstance().addListener(environmentControllerListener = new BaseDataDistribution.LogicListener() {
            @Override
            public void returnData(Object object) {

                EnvironmentReturnDataFormat environmentReturnDataFormat = (EnvironmentReturnDataFormat) object;
                if (environmentReturnDataFormat.environmentReturnDataFormatType == EnvironmentReturnDataFormat.EnvironmentReturnDataFormatType.environmentData) {
                    EnvironmentDataFormat mEnvironmentDataFormat = (EnvironmentDataFormat) environmentReturnDataFormat.getReturnData();
                    String eleMeter = mEnvironmentDataFormat.getUsefulTotalElectricEnergy() + "";
                    String innerTemStr = mEnvironmentDataFormat.getTemperature_1() + ""; // 柜内温度
                    String outerTemStr = mEnvironmentDataFormat.getTemperature_3() + ""; // 环境温度
                    String usefulPowerSer = mEnvironmentDataFormat.getUsefulPower() + ""; //实时功率

                    fCount = eleMeter + "KWH";
                    fInnerTemStr = innerTemStr + "C";
                    fOuterTemStr = outerTemStr + "C";
                    fUsefulPowerSer = usefulPowerSer + "KW";

                } else if (environmentReturnDataFormat.environmentReturnDataFormatType == EnvironmentReturnDataFormat.EnvironmentReturnDataFormatType.buttonTrigger) {
                    int[] trigger = (int[]) environmentReturnDataFormat.getReturnData();
                    writeLog("按钮 - " + trigger[0] + " - 动作 - " + trigger[1]);
                    ChangeBatteriesController.getInstance().buttonTriggerOpenDoor();
                }
            }
        });
        //长连接监听
        WebSocketController.getInstance().addListener(webSocketListener = new BaseDataDistribution.LogicListener() {
            @Override
            public void returnData(Object object) {
                updateWebSocket((WebSocketReturnDataFormat) object);
            }
        });
        //时间线程监听
        TimeThread.getInstance().addListener(timeListener = new BaseDataDistribution.LogicListener() {
            @Override
            public void returnData(Object object) {
                TimeThreadDataFormat timeThreadDataFormat = (TimeThreadDataFormat) object;
                if (timeThreadDataFormat.getTimeThreadDataType() == TimeThreadDataType.showDialog) {
                    showDialogInfoByJson(timeThreadDataFormat.getInfo());
                }
                if (timeThreadDataFormat.getTimeThreadDataType() == TimeThreadDataType.dateReturn) {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                    final String time = df.format(new Date());
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //每秒更新界面数据
                            nowTimeView.setText(time);
                            eleMeterView.setText(fCount);
                            innerTem.setText(fInnerTemStr);
                            outerTem.setText(fOuterTemStr);
                            powerView.setText(fUsefulPowerSer);
                            dbmReturn(dbm);
                        }
                    });
                }
            }
        });
    }

    /**
     * 长连接信息处理
     *
     * @param webSocketReturnDataFormat
     */
    private void updateWebSocket(WebSocketReturnDataFormat webSocketReturnDataFormat) {
        //在线状态显示
        if (webSocketReturnDataFormat.getWebSocketReturnType() == WebSocketReturnType.onlineType) {
            final int onLineType = (int) webSocketReturnDataFormat.getObject();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (onLineType == 1) {
                        isOnlineView.setText("在线");
                    } else {
                        isOnlineView.setText("离线");
                    }
                }
            });
        }
        //网络开关后台
        else if (webSocketReturnDataFormat.getWebSocketReturnType() == WebSocketReturnType.openAdmin) {
            activity.startActivity(new Intent(activity, A_Admin_9.class));
        }
        //显示提示框
        else if (webSocketReturnDataFormat.getWebSocketReturnType() == WebSocketReturnType.showDialog) {
            String showDialogStr = (String) webSocketReturnDataFormat.getObject();
            showDialogInfoByJson(showDialogStr);
        }
        //电柜信息返回
        else if (webSocketReturnDataFormat.getWebSocketReturnType() == WebSocketReturnType.internetInfo) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    cabIdView.setText(CabInfoSp.getInstance().getCabinetNumber_XXXXX());
                    addressView.setText(CabInfoSp.getInstance().getAddress());
                    setTel(CabInfoSp.getInstance().getTelNumber());
                    if (CabInfoSp.getInstance().getAndroidDeviceModel().equals("rk3288_box")) {
                        WebSettings webSettings = advertisementWebView.getSettings();
                        webSettings.setJavaScriptEnabled(true);
                        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
                        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
                        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
                        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
                        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); //关闭webview中缓存
                        webSettings.setAllowFileAccess(true); //设置可以访问文件
                        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
                        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
                        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
                        advertisementWebView.loadUrl("http://apc.halouhuandian.com/Cabinet/message.html?id=" + CabInfoSp.getInstance().getCabinetNumber_4600XXXX());

                        advertisementWebView.setWebViewClient(new WebViewClient() {
                            @Override
                            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                view.loadUrl(url);
                                return true;
                            }

                            @Override
                            public void onPageFinished(WebView view, String url) {
                                super.onPageFinished(view, url);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            sleep(1000 * 20);
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    advertisementWebView.setVisibility(View.VISIBLE);
                                                    adv_defult.setBackground(null);
                                                    System.gc();
                                                }
                                            });
                                        } catch (Exception e) {
                                            LocalLog.getInstance().writeLog(e.toString(), A_Main_Hello_9.class);
                                        }
                                    }
                                }).start();
                            }
                        });
                    }
                }
            });
        }
        //升级电池
        else if (webSocketReturnDataFormat.getWebSocketReturnType() == WebSocketReturnType.updateSingleBattery) {
            WebSocketUpdateHardWareDataFormat webSocketUpdateHardWareDataFormat = (WebSocketUpdateHardWareDataFormat) webSocketReturnDataFormat.getObject();
            Intent intent = new Intent(activity, A_UpdateBattery.class);
            intent.putExtra("door", webSocketUpdateHardWareDataFormat.getDoor());
            intent.putExtra("path", webSocketUpdateHardWareDataFormat.getDataPath());
            intent.putExtra("type", webSocketUpdateHardWareDataFormat.getType());
            activity.startActivity(intent);
        }
        //升级单个dcdc
        else if (webSocketReturnDataFormat.getWebSocketReturnType() == WebSocketReturnType.updateSingleDcdc) {
            WebSocketUpdateHardWareDataFormat webSocketUpdateHardWareDataFormat = (WebSocketUpdateHardWareDataFormat) webSocketReturnDataFormat.getObject();
            Intent intent = new Intent(activity, A_UpdateDcdc.class);
            intent.putExtra("door", webSocketUpdateHardWareDataFormat.getDoor());
            intent.putExtra("path", webSocketUpdateHardWareDataFormat.getDataPath());
            intent.putExtra("type", webSocketUpdateHardWareDataFormat.getType());
            activity.startActivity(intent);
        }
        //升级所有dcdc
        else if (webSocketReturnDataFormat.getWebSocketReturnType() == WebSocketReturnType.updateAllDcdc) {
            WebSocketUpdateHardWareDataFormat webSocketUpdateHardWareDataFormat = (WebSocketUpdateHardWareDataFormat) webSocketReturnDataFormat.getObject();
            Intent intent = new Intent(activity, A_UpdateDcdc.class);
            intent.putExtra("door", webSocketUpdateHardWareDataFormat.getDoor());
            intent.putExtra("path", webSocketUpdateHardWareDataFormat.getDataPath());
            intent.putExtra("type", webSocketUpdateHardWareDataFormat.getType());
            activity.startActivity(intent);
        }
        //升级单个acdc
        else if (webSocketReturnDataFormat.getWebSocketReturnType() == WebSocketReturnType.updateSingleAcdc) {
            WebSocketUpdateHardWareDataFormat webSocketUpdateHardWareDataFormat = (WebSocketUpdateHardWareDataFormat) webSocketReturnDataFormat.getObject();
            Intent intent = new Intent(activity, A_UpdateAcdc.class);
            intent.putExtra("door", webSocketUpdateHardWareDataFormat.getDoor());
            intent.putExtra("path", webSocketUpdateHardWareDataFormat.getDataPath());
            intent.putExtra("type", webSocketUpdateHardWareDataFormat.getType());
            activity.startActivity(intent);
        }
        //升级所有acdc
        else if (webSocketReturnDataFormat.getWebSocketReturnType() == WebSocketReturnType.updateAllAcdc) {
            WebSocketUpdateHardWareDataFormat webSocketUpdateHardWareDataFormat = (WebSocketUpdateHardWareDataFormat) webSocketReturnDataFormat.getObject();
            Intent intent = new Intent(activity, A_UpdateAcdc.class);
            intent.putExtra("door", webSocketUpdateHardWareDataFormat.getDoor());
            intent.putExtra("path", webSocketUpdateHardWareDataFormat.getDataPath());
            intent.putExtra("type", webSocketUpdateHardWareDataFormat.getType());
            activity.startActivity(intent);
        }
        //升级环境板
        else if (webSocketReturnDataFormat.getWebSocketReturnType() == WebSocketReturnType.updateEnvironment) {
            WebSocketUpdateHardWareDataFormat webSocketUpdateHardWareDataFormat = (WebSocketUpdateHardWareDataFormat) webSocketReturnDataFormat.getObject();
            Intent intent = new Intent(activity, A_UpdateEnvironment.class);
            intent.putExtra("door", webSocketUpdateHardWareDataFormat.getDoor());
            intent.putExtra("path", webSocketUpdateHardWareDataFormat.getDataPath());
            intent.putExtra("type", webSocketUpdateHardWareDataFormat.getType());
            activity.startActivity(intent);
        }
    }

    /**
     * 更新界面信息
     *
     * @param door
     */
    private void updateBattery(int door) {
        final int fIndex = door - 1;
        DcdcInfoByBaseFormat dcdcInfoByBaseFormat = daaDataFormat.getDcdcInfoByBaseFormat(fIndex);
        final int SOC = dcdcInfoByBaseFormat.getBatteryRelativeSurplus();
        final String BID = dcdcInfoByBaseFormat.getBID();
        if (ForbiddenSp.getInstance().getTargetForbidden(fIndex) != 1) {
            //防止重复刷新
            if (!bar_r_texts.get(fIndex).getText().equals("禁用")) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bar_r_texts.get(fIndex).setText("禁用");
                        bar_not_images.get(fIndex).setVisibility(View.VISIBLE);
                    }
                });
            }
        } else {
            bar_not_images.get(fIndex).setVisibility(View.GONE);
            if (SOC == -1) {
                //防止重复刷新
                if (!bar_r_texts.get(fIndex).getText().equals("0%")) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bar_r_images.get(fIndex).setImageResource(R.drawable.left_bar_null);
                            bar_r_texts.get(fIndex).setText("0%");
                        }
                    });
                }
            } else if (SOC == 0) {
                //防止重复刷新
                if (!bar_r_texts.get(fIndex).getText().equals("空")) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bar_r_images.get(fIndex).setImageResource(R.drawable.left_bar_null);
                            bar_r_texts.get(fIndex).setText("空");
                        }
                    });
                }
            } else if (SOC > 0 && SOC <= 100) {
                //防止重复刷新
                if (!bar_r_texts.get(fIndex).getText().equals(SOC + "%")) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bar_r_texts.get(fIndex).setText(SOC + "%");
                            if (SOC > 0 && SOC <= 100) {
                                String isType = UtilBattery.isType(BID);
                                if (isType.equals("48")) {
                                    bar_r_images.get(fIndex).setImageResource(R.drawable.left_bar_out);
                                } else {
                                    bar_r_images.get(fIndex).setImageResource(R.drawable.left_bar_has);
                                }
                            }
                        }
                    });
                }
            }
        }
    }

    /**
     * dbm返回信号数据更新
     *
     * @param dbm
     */
    public void dbmReturn(int dbm) {
        if (CabInfoSp.getInstance().getAndroidDeviceModel().equals("rk3288_box")) {
            if (dbm >= -95 && dbm < 0) {
                signalView.setImageResource(R.drawable.b3);
            } else if (dbm < -95 && dbm > -115) {
                signalView.setImageResource(R.drawable.b10);
            } else if (dbm <= -115) {
                signalView.setImageResource(R.drawable.b9);
            } else {
                signalView.setImageResource(R.drawable.b9);
            }
        } else {
            if (dbm >= -115 && dbm < 0) {
                signalView.setImageResource(R.drawable.b3);
            } else if (dbm < -115 && dbm > -130) {
                signalView.setImageResource(R.drawable.b10);
            } else if (dbm <= -130) {
                signalView.setImageResource(R.drawable.b9);
            } else {
                signalView.setImageResource(R.drawable.b9);
            }
        }
    }

    /**
     * 显示对话框提示
     *
     * @param json
     */
    private void showDialogInfoByJson(String json) {
        try {
            final JSONObject jsonObject = new JSONObject(json);
            showDialogInfo(jsonObject.getString("msg"), Integer.parseInt(jsonObject.getString("time")), Integer.parseInt(jsonObject.getString("type")));
        } catch (Exception e) {
            LocalLog.getInstance().writeLog(e.toString(), A_Main_Hello_9.class);
        }
    }

    /**
     * 显示对话框提示
     *
     * @param msg
     * @param time
     */

    private void showDialogInfo(String msg, int time, int type) {
        final String fMsg = msg;
        final int fTime = time;
        final int fType = type;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (AMainSpeakDialog == null) {
                    AMainSpeakDialog = new DialogMainShowInfo(activity, dialogPanelView, dialogTimeView, dialogInfoView, dialogBackground, new DialogMainShowInfo.SpeakDialogListener() {
                        @Override
                        public void onSpeakDialogReturn(String msg) {
                            TextToSpeechController.getInstance().onSpeak(msg);
                        }
                    });
                }
                AMainSpeakDialog.show(fMsg, fTime, fType);
            }
        });
        writeLog("电柜提示 - " + msg);
    }

    /**
     * 写入本地日志
     *
     * @param log
     */
    public void writeLog(String log) {
        LocalLog.getInstance().writeLog(log , A_Main_Hello_9.class);
    }

    /**
     * 设置400电话
     *
     * @param phones_str
     */
    private void setTel(String phones_str) {
        if (phones_str.equals("")) {
            phoneView.setVisibility(View.GONE);
        } else {
            phoneView.setText(phones_str);
            phoneView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 初始化摄像头UI
     */
    private void setMovies() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (UtilPublic.getIsExistExCard() == true && UtilPublic.hasCamera() == true) {
                    cameraPrePanelView.setVisibility(View.GONE);
                    moviesController = new MoviesController(activity);
                } else {
                    cameraPrePanelView.setVisibility(View.VISIBLE);
                    cameraPrePanelTitleView.setText("视频监控中");
                    if (UtilPublic.hasCamera() == false) {
                        cameraPrePanelTitleView.setText(cameraPrePanelTitleView.getText() + "#1");
                    }
                    if (UtilPublic.getIsExistExCard() == false) {
                        cameraPrePanelTitleView.setText(cameraPrePanelTitleView.getText() + "#2");
                    }
                }
                System.out.println("movies：   是否存在摄像头 - " + UtilPublic.hasCamera() + "   SD卡数是否存在 - " + UtilPublic.getIsExistExCard());
            }
        });
    }

    /**
     * 换电动画
     */
    private void startAnimation(ChangeBatteriesChangeDataFormat changeBatteriesChangeDataFormat) {

        final int in_electric = changeBatteriesChangeDataFormat.getInElectric();
        final int out_electric = changeBatteriesChangeDataFormat.getOutElectric();
        final String type = changeBatteriesChangeDataFormat.getCostType();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                int j = in_electric;
                int g = out_electric;

                up_bar_1_text.setText(in_electric + "%");
                up_bar_2_text.setText(out_electric + "%");
                coinDeductionView.setText(type);

                if (j > 0 && j <= 10) {
                    up_bar_1.setImageResource(R.drawable.image_b_0);
                } else if (j > 10 && j <= 20) {
                    up_bar_1.setImageResource(R.drawable.image_b_10);
                } else if (j > 20 && j <= 30) {
                    up_bar_1.setImageResource(R.drawable.image_b_20);
                } else if (j > 30 && j <= 40) {
                    up_bar_1.setImageResource(R.drawable.image_b_30);
                } else if (j > 40 && j <= 50) {
                    up_bar_1.setImageResource(R.drawable.image_b_40);
                } else if (j > 50 && j <= 60) {
                    up_bar_1.setImageResource(R.drawable.image_b_50);
                } else if (j > 60 && j <= 70) {
                    up_bar_1.setImageResource(R.drawable.image_b_60);
                } else if (j > 70 && j <= 80) {
                    up_bar_1.setImageResource(R.drawable.image_b_70);
                } else if (j > 80 && j <= 90) {
                    up_bar_1.setImageResource(R.drawable.image_b_80);
                } else if (j > 90 && j <= 99) {
                    up_bar_1.setImageResource(R.drawable.image_b_90);
                } else if (j == 100) {
                    up_bar_1.setImageResource(R.drawable.image_b_100);
                }

                if (g > 0 && g <= 10) {
                    up_bar_2.setImageResource(R.drawable.image_b_0);
                } else if (g > 10 && g <= 20) {
                    up_bar_2.setImageResource(R.drawable.image_b_10);
                } else if (g > 20 && g <= 30) {
                    up_bar_2.setImageResource(R.drawable.image_b_20);
                } else if (g > 30 && g <= 40) {
                    up_bar_2.setImageResource(R.drawable.image_b_30);
                } else if (g > 40 && g <= 50) {
                    up_bar_2.setImageResource(R.drawable.image_b_40);
                } else if (g > 50 && g <= 60) {
                    up_bar_2.setImageResource(R.drawable.image_b_50);
                } else if (g > 60 && g <= 70) {
                    up_bar_2.setImageResource(R.drawable.image_b_60);
                } else if (g > 70 && g <= 80) {
                    up_bar_2.setImageResource(R.drawable.image_b_70);
                } else if (g > 80 && g <= 90) {
                    up_bar_2.setImageResource(R.drawable.image_b_80);
                } else if (g > 90 && g <= 99) {
                    up_bar_2.setImageResource(R.drawable.image_b_90);
                } else if (g == 100) {
                    up_bar_2.setImageResource(R.drawable.image_b_100);
                }

                ExchangeBarAnimation exchangeAnimation = new ExchangeBarAnimation(CabInfoSp.getInstance().getAndroidDeviceModel(), dialogPanelView, costView, black_1, black_2, up_bar_2_panel, up_bar_1_panel, new ExchangeBarAnimation.IFExchangeAnimationListener() {
                    @Override
                    public void onExchangeAnimationStart() {
                    }

                    @Override
                    public void onExchangeAnimationEnd() {
                    }
                });
                exchangeAnimation.startAnimation();
            }
        });
    }

    /**
     * 图标闪烁 - 舱门弹出的时候 图标闪烁
     *
     * @param door
     */
    private void putterIconFlashing(int door) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < 10; i++) {
                        if (i % 2 == 0) {

                        } else if (i % 2 == 1) {

                        }
                    }
                } catch (Exception e) {
                    LocalLog.getInstance().writeLog(e.toString());
                }
            }
        }).start();
    }

}
