package client.halouhuandian.app15.view.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import client.halouhuandian.app15.R;
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
import client.halouhuandian.app15.pub.util.UtilPublic;
import client.halouhuandian.app15.pub.util.UtilUidDictionart;
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
import client.halouhuandian.app15.view.activity.BaseActivity;
import client.halouhuandian.app15.view.activity.admin.A_Admin_12;
import client.halouhuandian.app15.view.activity.admin.A_Admin_9;
import client.halouhuandian.app15.view.activity.update.A_UpdateAcdc;
import client.halouhuandian.app15.view.activity.update.A_UpdateBattery;
import client.halouhuandian.app15.view.activity.update.A_UpdateDcdc;
import client.halouhuandian.app15.view.activity.update.A_UpdateEnvironment;
import client.halouhuandian.app15.view.customUi.activityDialog.BlackExchangeDialog_12;
import client.halouhuandian.app15.view.customUi.activityDialog.BlackExchangeDialog_9;

import static java.lang.Thread.sleep;

public class A_Main_Mixiang_12 extends BaseActivity {

    //舱门view
    @BindViews({R.id.bar_r_text_1, R.id.bar_r_text_2, R.id.bar_r_text_3, R.id.bar_r_text_4, R.id.bar_r_text_5, R.id.bar_r_text_6, R.id.bar_r_text_7, R.id.bar_r_text_8, R.id.bar_r_text_9,R.id.bar_r_text_10,R.id.bar_r_text_11,R.id.bar_r_text_12})
    public List<TextView> bar_r_texts;
    @BindViews({R.id.bar_not_image_1, R.id.bar_not_image_2, R.id.bar_not_image_3, R.id.bar_not_image_4, R.id.bar_not_image_5, R.id.bar_not_image_6, R.id.bar_not_image_7, R.id.bar_not_image_8, R.id.bar_not_image_9,R.id.bar_not_image_10,R.id.bar_not_image_11,R.id.bar_not_image_12})
    public List<ImageView> bar_not_images;

    //动画相关view
    @BindView(R.id.animation_dialog)
    public BlackExchangeDialog_12 animationDialog;
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
    //400电话
    @BindView(R.id.tel_text_1)
    public TextView phoneView;
    //程序版本
    @BindView(R.id.version)
    public TextView versionView;
    //广告显示
    @BindView(R.id.adv_webview)
    public WebView advertisementWebView;
    @BindView(R.id.adv_default)
    public ImageView advDefault;

    //右下角图标显示
    @BindView(R.id.signal) //信号强弱图标
    public ImageView signalView;
    @BindView(R.id.t_5)
    public TextView eleMeterView; //电表走字显示

    //摄像头显示
    @BindView(R.id.camera_surface_view)
    public SurfaceView cameraSurfaceView;

    //电表和温度数据 485数据的返回
    public String eleMeter = "0";
    //dbm信号
    public int dbm = -1;

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

        setContentView(R.layout.activity_main_1080p_black_12);
        ButterKnife.bind(this);

        //删除本地webView缓存
        deleteDatabase("WebView.db");
        advertisementWebView.clearHistory();
        advertisementWebView.clearFormData();
        getCacheDir().delete();

        //初始化信息
        writeLog("电柜初始化");
        animationDialog.setActivity(activity);
        showDialogInfo("电柜正在初始化,请稍候", 5, 0);
        if(WebSocketController.getInstance().getHasReceiverCabId() == null){
            cabIdView.setText("D" + CabInfoSp.getInstance().getCabinetNumber_XXXXX());
        }else{
            cabIdView.setText(WebSocketController.getInstance().getHasReceiverCabId());
        }
        setTel(CabInfoSp.getInstance().getTelNumber());
        versionView.setText("Ver：" + CabInfoSp.getInstance().getVersion());
        setRentBatteryQCode();
        setDownloadQrCode();
        downloadQRcodeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(new Intent(activity, A_Admin_12.class));
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
//                                RootCmd.execRootCmd("reboot");
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
                    String mEleMeter = mEnvironmentDataFormat.getUsefulTotalElectricEnergy() + "";
                    eleMeter = mEleMeter + "KWH";
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
                            eleMeterView.setText(eleMeter);
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
        if (webSocketReturnDataFormat.getWebSocketReturnType() == WebSocketReturnType.openAdmin) {
            activity.startActivity(new Intent(activity, A_Admin_12.class));
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
                        advertisementWebView.loadUrl(HttpUrlMap.apc+"Cabinet/message.html?id=" + CabInfoSp.getInstance().getCabinetNumber_4600XXXX());

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
                                                    advDefault.setVisibility(View.GONE);
                                                    advertisementWebView.setVisibility(View.VISIBLE);
                                                    System.gc();
                                                }
                                            });
                                        } catch (Exception e) {
                                            LocalLog.getInstance().writeLog(e.toString(), A_Main_Mixiang_9.class);
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
        //升级单个dcdc
        else if (webSocketReturnDataFormat.getWebSocketReturnType() == WebSocketReturnType.updateSingleAcdc) {
            WebSocketUpdateHardWareDataFormat webSocketUpdateHardWareDataFormat = (WebSocketUpdateHardWareDataFormat) webSocketReturnDataFormat.getObject();
            Intent intent = new Intent(activity, A_UpdateAcdc.class);
            intent.putExtra("door", webSocketUpdateHardWareDataFormat.getDoor());
            intent.putExtra("path", webSocketUpdateHardWareDataFormat.getDataPath());
            intent.putExtra("type", webSocketUpdateHardWareDataFormat.getType());
            activity.startActivity(intent);
        }
        //升级所有dcdc
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

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(ForbiddenSp.getInstance().getTargetForbidden(fIndex) != 1){
                    if(!bar_r_texts.get(fIndex).getText().equals("禁用")){
                        bar_r_texts.get(fIndex).setText("禁用");
                        bar_not_images.get(fIndex).setVisibility(View.GONE);
                    }
                }else{
                    if (SOC == -1 && !bar_r_texts.get(fIndex).getText().equals("0%")) {
                        bar_r_texts.get(fIndex).setText("0%");
                        bar_not_images.get(fIndex).setVisibility(View.GONE);
                    } else if (SOC == 0 && !bar_r_texts.get(fIndex).getText().equals("")) {
                        bar_r_texts.get(fIndex).setText("");
                        bar_not_images.get(fIndex).setVisibility(View.VISIBLE);
                    } else if (SOC > 0 && SOC <= 100 && !bar_r_texts.get(fIndex).getText().equals(SOC + "%")) {
                        bar_r_texts.get(fIndex).setText(SOC + "%");
                        bar_not_images.get(fIndex).setVisibility(View.GONE);
                    }
                }
            }
        });
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
     * 设置租电池二维码
     */
    public void setRentBatteryQCode(){
        String timeString = System.currentTimeMillis()+"";
        int length = timeString.length();
        timeString = timeString.substring(0 , length - 3);
        String dataString = CabInfoSp.getInstance().getCabinetNumber_4600XXXX() + timeString;
        String dataString_62 = UtilUidDictionart.get_T10_To_S62(dataString);
        rentBarQRcodeView.setImageBitmap(UtilPublic.generateBitmap(dataString_62, 400, 400 , 0xff000000 , 0xffffffff));
    }

    /**
     * 设置二维码
     */
    public void setDownloadQrCode() {
        downloadQRcodeView.setImageBitmap(UtilPublic.generateBitmap(HttpUrlMap.DownLoadApkJump, 400, 400 , 0xff000000 , 0xffffffff));
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
            LocalLog.getInstance().writeLog(e.toString(), A_Main_Mixiang_9.class);
        }
    }

    /**
     * 显示对话框提示
     *
     * @param msg
     * @param time
     */

    private void showDialogInfo(final String msg, final int time, final int type) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                animationDialog.showDialog(msg, time, type, new BlackExchangeDialog_9.BlackExchangeDialog_9_listener() {
                    @Override
                    public void messageReturn(String msg) {
                        if(type == 1){
                            TextToSpeechController.getInstance().onSpeak(msg);
                        }
                    }
                });

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
        LocalLog.getInstance().writeLog(log , A_Main_Mixiang_12.class);
    }

    /**
     * 设置400电话
     *
     * @param phones_str
     */
    private void setTel(String phones_str) {
        if (phones_str.equals("")) {
            phoneView.setText("   ");
            phoneView.setVisibility(View.VISIBLE);
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
                if (UtilPublic.getIsExistExCard() == true &&  UtilPublic.hasCamera() == true) {
                    moviesController = new MoviesController(activity);
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

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                animationDialog.showExchangeInfo(in_electric,out_electric);
            }
        });

    }
}
