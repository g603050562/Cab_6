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
 * ????????? - ????????????
 */
public class A_Main_Hello_9 extends BaseActivity {

    //??????view
    @BindViews({R.id.bar_r_image_1, R.id.bar_r_image_2, R.id.bar_r_image_3, R.id.bar_r_image_4, R.id.bar_r_image_5, R.id.bar_r_image_6, R.id.bar_r_image_7, R.id.bar_r_image_8, R.id.bar_r_image_9})
    public List<ImageView> bar_r_images;
    @BindViews({R.id.bar_r_text_1, R.id.bar_r_text_2, R.id.bar_r_text_3, R.id.bar_r_text_4, R.id.bar_r_text_5, R.id.bar_r_text_6, R.id.bar_r_text_7, R.id.bar_r_text_8, R.id.bar_r_text_9})
    public List<TextView> bar_r_texts;
    @BindViews({R.id.bar_not_image_1, R.id.bar_not_image_2, R.id.bar_not_image_3, R.id.bar_not_image_4, R.id.bar_not_image_5, R.id.bar_not_image_6, R.id.bar_not_image_7, R.id.bar_not_image_8, R.id.bar_not_image_9,})
    public List<ImageView> bar_not_images;
    //????????????view
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
    @BindView(R.id.cost_bi) //??????????????????
    public TextView coinDeductionView;
    @BindView(R.id.cost) //??????????????????
    public LinearLayout costView;

    //??????????????????
    @BindView(R.id.rent_bar_qcode)
    public ImageView rentBarQRcodeView;
    //????????????app?????????
    @BindView(R.id.download_qcode)
    public ImageView downloadQRcodeView;
    //????????????
    @BindView(R.id.cab_id)
    public TextView cabIdView;
    //????????????
    @BindView(R.id.now_address)
    public TextView addressView;
    //????????????
    @BindView(R.id.now_time)
    public TextView nowTimeView;
    //400??????
    @BindView(R.id.tel_text_1)
    public TextView phoneView;
    //????????????
    @BindView(R.id.version)
    public TextView versionView;
    //?????????????????????
    @BindView(R.id.b_3) //??????????????????
    public ImageView signalView;
    @BindView(R.id.t_5)
    public TextView eleMeterView; //??????????????????
    @BindView(R.id.t_6)
    public TextView innerTem; //????????????
    @BindView(R.id.t_7)
    public TextView outerTem; //????????????
    @BindView(R.id.t_8)
    public TextView powerView; //????????????
    @BindView(R.id.t_9)
    public TextView isOnlineView; //????????????
    //?????????
    @BindView(R.id.dialog_panel)
    public LinearLayout dialogPanelView;
    @BindView(R.id.dialog_time)
    public TextView dialogTimeView;
    @BindView(R.id.dialog_info)
    public TextView dialogInfoView;
    @BindView(R.id.dialog_background) // ?????????
    public LinearLayout dialogBackground;
    //???????????????
    @BindView(R.id.camera_surface_view)
    public SurfaceView cameraSurfaceView;
    @BindView(R.id.camera_preview_panel)
    public LinearLayout cameraPrePanelView;
    @BindView(R.id.camera_preview_panel_text)
    public TextView cameraPrePanelTitleView;
    //????????????
    @BindView(R.id.adv_webview)
    public WebView advertisementWebView;
    //??????????????????
    @BindView(R.id.adv_defult)
    public ImageView adv_defult;
    //???????????????
    private DialogMainShowInfo AMainSpeakDialog;

    //dbm
    private int dbm = -1;
    //????????????????????????
    private String fCount = "";
    private String fInnerTemStr = "";
    private String fOuterTemStr = "";
    private String fUsefulPowerSer = "";

    //daa??????
    private DaaController.DaaControllerListener daaControllerListener;
    //dbm??????
    private BaseDataDistribution.LogicListener dbmLogicListener;
    //4g?????????
    private BaseDataDistribution.LogicListener _4gLogicListener;
    //???????????????
    private BaseDataDistribution.LogicListener environmentControllerListener;
    //???????????????
    private BaseDataDistribution.LogicListener webSocketListener;
    //??????????????????
    private BaseDataDistribution.LogicListener timeListener;
    //????????????
    private BaseDataDistribution.LogicListener exchangeBatteriesListener;
    //dcdc????????????
    private DaaDataFormat daaDataFormat = null;
    //????????????
    private MoviesController moviesController = null;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_1080p_org);
        ButterKnife.bind(this);

        //????????????webView??????
        deleteDatabase("WebView.db");
        advertisementWebView.clearHistory();
        advertisementWebView.clearFormData();
        getCacheDir().delete();

        //???????????????
        writeLog("???????????????");
        showDialogInfo("????????????????????????????????????", 30, 0);
        if(WebSocketController.getInstance().getHasReceiverCabId() == null){
            cabIdView.setText("D" + CabInfoSp.getInstance().getCabinetNumber_XXXXX());
        }else{
            cabIdView.setText(WebSocketController.getInstance().getHasReceiverCabId());
        }
        setTel(CabInfoSp.getInstance().getTelNumber());
        versionView.setText("Ver???" + CabInfoSp.getInstance().getVersion());
        rentBarQRcodeView.setImageBitmap(UtilPublic.generateBitmap(CabInfoSp.getInstance().getCabinetNumber_4600XXXX() + "/" + "0000000000000000" + "/" + "0", 400, 400));
        rentBarQRcodeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogInfo("????????????????????????", 20, 1);
                ChangeBatteriesChangeDataFormat changeBatteriesChangeDataFormat = new ChangeBatteriesChangeDataFormat(1, "111", 10, 2, "222", 20, "??????", "??????");
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

        //??????????????????
        setMovies();
        //???????????????
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
        //????????????
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
        //dcdc????????????
        DaaController.getInstance().addListener(daaControllerListener = new DaaController.DaaControllerListener() {
            @Override
            public void returnData(DaaDataFormat mDaaDataFormat, DaaIntegration.ReturnDataType returnDataType, int index) {
                daaDataFormat = mDaaDataFormat;
                if (returnDataType.equals(DaaIntegration.ReturnDataType.dcdcInfoByBase)) {
                    updateBattery(index + 1);
                }
            }
        });
        //dbm??????
        DataDistributionCurrentNetDBM.getInstance().addListener(dbmLogicListener = new BaseDataDistribution.LogicListener() {
            @Override
            public void returnData(Object object) {
                dbm = (int) object;
            }
        });
        //4g?????????
        Find4gCard.getInstance().addListener(_4gLogicListener = new BaseDataDistribution.LogicListener() {
            @Override
            public void returnData(Object object) {
                Find4gCardReturnDataFormat find4gCardReturnDataFormat = (Find4gCardReturnDataFormat) object;
                if (find4gCardReturnDataFormat.getFind4gCardReturnDataType() == Find4gCardReturnDataFormat.Find4gCardReturnDataType.IMSI) {
                    showDialogInfo("?????????????????????", 10, 0);
                } else if (find4gCardReturnDataFormat.getFind4gCardReturnDataType() == Find4gCardReturnDataFormat.Find4gCardReturnDataType.error) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                showDialogInfo("????????????4g????????????????????????20????????????", 20, 1);
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
        //?????????????????????
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
        //?????????????????????
        EnvironmentController.getInstance().addListener(environmentControllerListener = new BaseDataDistribution.LogicListener() {
            @Override
            public void returnData(Object object) {

                EnvironmentReturnDataFormat environmentReturnDataFormat = (EnvironmentReturnDataFormat) object;
                if (environmentReturnDataFormat.environmentReturnDataFormatType == EnvironmentReturnDataFormat.EnvironmentReturnDataFormatType.environmentData) {
                    EnvironmentDataFormat mEnvironmentDataFormat = (EnvironmentDataFormat) environmentReturnDataFormat.getReturnData();
                    String eleMeter = mEnvironmentDataFormat.getUsefulTotalElectricEnergy() + "";
                    String innerTemStr = mEnvironmentDataFormat.getTemperature_1() + ""; // ????????????
                    String outerTemStr = mEnvironmentDataFormat.getTemperature_3() + ""; // ????????????
                    String usefulPowerSer = mEnvironmentDataFormat.getUsefulPower() + ""; //????????????

                    fCount = eleMeter + "KWH";
                    fInnerTemStr = innerTemStr + "C";
                    fOuterTemStr = outerTemStr + "C";
                    fUsefulPowerSer = usefulPowerSer + "KW";

                } else if (environmentReturnDataFormat.environmentReturnDataFormatType == EnvironmentReturnDataFormat.EnvironmentReturnDataFormatType.buttonTrigger) {
                    int[] trigger = (int[]) environmentReturnDataFormat.getReturnData();
                    writeLog("?????? - " + trigger[0] + " - ?????? - " + trigger[1]);
                    ChangeBatteriesController.getInstance().buttonTriggerOpenDoor();
                }
            }
        });
        //???????????????
        WebSocketController.getInstance().addListener(webSocketListener = new BaseDataDistribution.LogicListener() {
            @Override
            public void returnData(Object object) {
                updateWebSocket((WebSocketReturnDataFormat) object);
            }
        });
        //??????????????????
        TimeThread.getInstance().addListener(timeListener = new BaseDataDistribution.LogicListener() {
            @Override
            public void returnData(Object object) {
                TimeThreadDataFormat timeThreadDataFormat = (TimeThreadDataFormat) object;
                if (timeThreadDataFormat.getTimeThreadDataType() == TimeThreadDataType.showDialog) {
                    showDialogInfoByJson(timeThreadDataFormat.getInfo());
                }
                if (timeThreadDataFormat.getTimeThreadDataType() == TimeThreadDataType.dateReturn) {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//??????????????????
                    final String time = df.format(new Date());
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //????????????????????????
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
     * ?????????????????????
     *
     * @param webSocketReturnDataFormat
     */
    private void updateWebSocket(WebSocketReturnDataFormat webSocketReturnDataFormat) {
        //??????????????????
        if (webSocketReturnDataFormat.getWebSocketReturnType() == WebSocketReturnType.onlineType) {
            final int onLineType = (int) webSocketReturnDataFormat.getObject();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (onLineType == 1) {
                        isOnlineView.setText("??????");
                    } else {
                        isOnlineView.setText("??????");
                    }
                }
            });
        }
        //??????????????????
        else if (webSocketReturnDataFormat.getWebSocketReturnType() == WebSocketReturnType.openAdmin) {
            activity.startActivity(new Intent(activity, A_Admin_9.class));
        }
        //???????????????
        else if (webSocketReturnDataFormat.getWebSocketReturnType() == WebSocketReturnType.showDialog) {
            String showDialogStr = (String) webSocketReturnDataFormat.getObject();
            showDialogInfoByJson(showDialogStr);
        }
        //??????????????????
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
                        webSettings.setUseWideViewPort(true); //????????????????????????webview?????????
                        webSettings.setLoadWithOverviewMode(true); // ????????????????????????
                        webSettings.setBuiltInZoomControls(true); //????????????????????????????????????false?????????WebView????????????
                        webSettings.setDisplayZoomControls(false); //???????????????????????????
                        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); //??????webview?????????
                        webSettings.setAllowFileAccess(true); //????????????????????????
                        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //????????????JS???????????????
                        webSettings.setLoadsImagesAutomatically(true); //????????????????????????
                        webSettings.setDefaultTextEncodingName("utf-8");//??????????????????
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
        //????????????
        else if (webSocketReturnDataFormat.getWebSocketReturnType() == WebSocketReturnType.updateSingleBattery) {
            WebSocketUpdateHardWareDataFormat webSocketUpdateHardWareDataFormat = (WebSocketUpdateHardWareDataFormat) webSocketReturnDataFormat.getObject();
            Intent intent = new Intent(activity, A_UpdateBattery.class);
            intent.putExtra("door", webSocketUpdateHardWareDataFormat.getDoor());
            intent.putExtra("path", webSocketUpdateHardWareDataFormat.getDataPath());
            intent.putExtra("type", webSocketUpdateHardWareDataFormat.getType());
            activity.startActivity(intent);
        }
        //????????????dcdc
        else if (webSocketReturnDataFormat.getWebSocketReturnType() == WebSocketReturnType.updateSingleDcdc) {
            WebSocketUpdateHardWareDataFormat webSocketUpdateHardWareDataFormat = (WebSocketUpdateHardWareDataFormat) webSocketReturnDataFormat.getObject();
            Intent intent = new Intent(activity, A_UpdateDcdc.class);
            intent.putExtra("door", webSocketUpdateHardWareDataFormat.getDoor());
            intent.putExtra("path", webSocketUpdateHardWareDataFormat.getDataPath());
            intent.putExtra("type", webSocketUpdateHardWareDataFormat.getType());
            activity.startActivity(intent);
        }
        //????????????dcdc
        else if (webSocketReturnDataFormat.getWebSocketReturnType() == WebSocketReturnType.updateAllDcdc) {
            WebSocketUpdateHardWareDataFormat webSocketUpdateHardWareDataFormat = (WebSocketUpdateHardWareDataFormat) webSocketReturnDataFormat.getObject();
            Intent intent = new Intent(activity, A_UpdateDcdc.class);
            intent.putExtra("door", webSocketUpdateHardWareDataFormat.getDoor());
            intent.putExtra("path", webSocketUpdateHardWareDataFormat.getDataPath());
            intent.putExtra("type", webSocketUpdateHardWareDataFormat.getType());
            activity.startActivity(intent);
        }
        //????????????acdc
        else if (webSocketReturnDataFormat.getWebSocketReturnType() == WebSocketReturnType.updateSingleAcdc) {
            WebSocketUpdateHardWareDataFormat webSocketUpdateHardWareDataFormat = (WebSocketUpdateHardWareDataFormat) webSocketReturnDataFormat.getObject();
            Intent intent = new Intent(activity, A_UpdateAcdc.class);
            intent.putExtra("door", webSocketUpdateHardWareDataFormat.getDoor());
            intent.putExtra("path", webSocketUpdateHardWareDataFormat.getDataPath());
            intent.putExtra("type", webSocketUpdateHardWareDataFormat.getType());
            activity.startActivity(intent);
        }
        //????????????acdc
        else if (webSocketReturnDataFormat.getWebSocketReturnType() == WebSocketReturnType.updateAllAcdc) {
            WebSocketUpdateHardWareDataFormat webSocketUpdateHardWareDataFormat = (WebSocketUpdateHardWareDataFormat) webSocketReturnDataFormat.getObject();
            Intent intent = new Intent(activity, A_UpdateAcdc.class);
            intent.putExtra("door", webSocketUpdateHardWareDataFormat.getDoor());
            intent.putExtra("path", webSocketUpdateHardWareDataFormat.getDataPath());
            intent.putExtra("type", webSocketUpdateHardWareDataFormat.getType());
            activity.startActivity(intent);
        }
        //???????????????
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
     * ??????????????????
     *
     * @param door
     */
    private void updateBattery(int door) {
        final int fIndex = door - 1;
        DcdcInfoByBaseFormat dcdcInfoByBaseFormat = daaDataFormat.getDcdcInfoByBaseFormat(fIndex);
        final int SOC = dcdcInfoByBaseFormat.getBatteryRelativeSurplus();
        final String BID = dcdcInfoByBaseFormat.getBID();
        if (ForbiddenSp.getInstance().getTargetForbidden(fIndex) != 1) {
            //??????????????????
            if (!bar_r_texts.get(fIndex).getText().equals("??????")) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bar_r_texts.get(fIndex).setText("??????");
                        bar_not_images.get(fIndex).setVisibility(View.VISIBLE);
                    }
                });
            }
        } else {
            bar_not_images.get(fIndex).setVisibility(View.GONE);
            if (SOC == -1) {
                //??????????????????
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
                //??????????????????
                if (!bar_r_texts.get(fIndex).getText().equals("???")) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bar_r_images.get(fIndex).setImageResource(R.drawable.left_bar_null);
                            bar_r_texts.get(fIndex).setText("???");
                        }
                    });
                }
            } else if (SOC > 0 && SOC <= 100) {
                //??????????????????
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
     * dbm????????????????????????
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
     * ?????????????????????
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
     * ?????????????????????
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
        writeLog("???????????? - " + msg);
    }

    /**
     * ??????????????????
     *
     * @param log
     */
    public void writeLog(String log) {
        LocalLog.getInstance().writeLog(log , A_Main_Hello_9.class);
    }

    /**
     * ??????400??????
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
     * ??????????????????UI
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
                    cameraPrePanelTitleView.setText("???????????????");
                    if (UtilPublic.hasCamera() == false) {
                        cameraPrePanelTitleView.setText(cameraPrePanelTitleView.getText() + "#1");
                    }
                    if (UtilPublic.getIsExistExCard() == false) {
                        cameraPrePanelTitleView.setText(cameraPrePanelTitleView.getText() + "#2");
                    }
                }
                System.out.println("movies???   ????????????????????? - " + UtilPublic.hasCamera() + "   SD?????????????????? - " + UtilPublic.getIsExistExCard());
            }
        });
    }

    /**
     * ????????????
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
     * ???????????? - ????????????????????? ????????????
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
