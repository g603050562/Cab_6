package client.halouhuandian.app15.view.activity.admin;

import android.app.ActivityManager;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import client.halouhuandian.app15.R;
import client.halouhuandian.app15.hardWareConncetion.daa.DaaController;
import client.halouhuandian.app15.hardWareConncetion.daa.DaaIntegration;
import client.halouhuandian.app15.hardWareConncetion.daa.mode.DaaDataFormat;
import client.halouhuandian.app15.hardWareConncetion.environmentPlate.EnvironmentController;
import client.halouhuandian.app15.hardWareConncetion.environmentPlate.EnvironmentDataFormat;
import client.halouhuandian.app15.hardWareConncetion.environmentPlate.EnvironmentReturnDataFormat;
import client.halouhuandian.app15.hardWareConncetion.environmentPlate.electricityMeter.ElectricityMeterController;
import client.halouhuandian.app15.model.adapter.AdminDcdcBottomAdapter_12;
import client.halouhuandian.app15.model.adapter.AdminDcdcBottomAdapter_9;
import client.halouhuandian.app15.model.adapter.AdminDcdcTopAdapter_12;
import client.halouhuandian.app15.model.dao.fileSave.LocalLog;
import client.halouhuandian.app15.model.dao.sharedPreferences.CabInfoSp;
import client.halouhuandian.app15.pub.BaseDataDistribution;
import client.halouhuandian.app15.service.logic.logicHttpConnection.webSocket.WebSocketController;
import client.halouhuandian.app15.service.logic.logicHttpConnection.webSocket.mode.WebSocketReturnDataFormat;
import client.halouhuandian.app15.service.logic.logicHttpConnection.webSocket.mode.WebSocketReturnType;
import client.halouhuandian.app15.service.logic.logicOpenDoor.LogicOpenDoor;
import client.halouhuandian.app15.view.activity.BaseActivity;
import client.halouhuandian.app15.view.customUi.activityDialog.DialogAdminCurrentPlateControl;
import client.halouhuandian.app15.view.customUi.activityDialog.DialogAdminFanControl;
import client.halouhuandian.app15.view.customUi.activityDialog.DialogAdminFireControl;

public class A_Admin_12 extends BaseActivity {

    //??????????????????
    private static Boolean isExit = false;
    //??????????????????
    @BindView(R.id.thread_protection)
    public ImageView thread_protection;
    //??????????????????
    @BindView(R.id.bar_info_girdview)
    public GridView bar_info_girdview;
    //?????????????????????
    @BindView(R.id.cha_info_girdview)
    public GridView cha_info_girdview;
    //??????????????????
    @BindView(R.id.electricReboot)
    public TextView electricReboot;
    //????????????
    @BindView(R.id.fanControl)
    public TextView fanControl;
    //??????????????????
    @BindView(R.id.currentPlateControl)
    public TextView currentPlateControl;
    //????????????
    @BindView(R.id.fireControl)
    public TextView fireControl;
    //????????????
    @BindView(R.id.cleanMeter)
    public TextView cleanMeter;

    //????????????
    private Thread refreshThread;
    //??????????????????
    private boolean refreshThreadState = true;
    //????????????
    private DaaDataFormat daaDataFormat;
    private EnvironmentDataFormat environmentDataFormat;
    //??????????????????
    private DaaController.DaaControllerListener daaControllerListener;
    private BaseDataDistribution.LogicListener environmentControllerListener;
    private BaseDataDistribution.LogicListener webSocketListener;

    //?????????
    private AdminDcdcTopAdapter_12 adminDcdcTopAdapter_12;
    private AdminDcdcBottomAdapter_12 adminDcdcBottomAdapter_12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_1080p_12);
        ButterKnife.bind(this);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        refreshThreadState = false;
        WebSocketController.getInstance().deleteListener(webSocketListener);
        DaaController.getInstance().deleteListener(daaControllerListener);
        EnvironmentController.getInstance().deleteListener(environmentControllerListener);
    }

    protected void init() {

        //????????????
        String threadProtectionType = CabInfoSp.getInstance().getTPTNumber();
        if (threadProtectionType.equals("1")) {
            thread_protection.setImageResource(R.drawable.image_6);
        } else if (threadProtectionType.equals("0")) {
            thread_protection.setImageResource(R.drawable.image_7);
        }

        //dcdc????????????
        daaDataFormat = DaaController.getInstance().getDaaDataFormat();

//        if(daaDataFormat == null){
//            daaDataFormat = new DaaDataFormat();
//        }

        DaaController.getInstance().addListener(daaControllerListener =  new DaaController.DaaControllerListener() {
            @Override
            public void returnData(DaaDataFormat mDaaDataFormat, DaaIntegration.ReturnDataType returnDataType, int index) {
                daaDataFormat = mDaaDataFormat;
            }
        });

        //?????????????????????
        environmentDataFormat = EnvironmentController.getInstance().getEnvironmentDataFormat();
        EnvironmentController.getInstance().addListener(environmentControllerListener =  new BaseDataDistribution.LogicListener() {
            @Override
            public void returnData(Object object) {
                EnvironmentReturnDataFormat environmentReturnDataFormat = (EnvironmentReturnDataFormat)object;
                if(environmentReturnDataFormat.environmentReturnDataFormatType == EnvironmentReturnDataFormat.EnvironmentReturnDataFormatType.environmentData){
                    environmentDataFormat = (EnvironmentDataFormat)environmentReturnDataFormat.getReturnData();
                    if(adminDcdcBottomAdapter_12 !=null){
                        adminDcdcBottomAdapter_12.setEnvironmentDataFormat(environmentDataFormat);
                    }
                }
            }
        });

        //???????????????
        WebSocketController.getInstance().addListener(webSocketListener = new BaseDataDistribution.LogicListener() {
            @Override
            public void returnData(Object object) {
                updateWebSocket((WebSocketReturnDataFormat)object);
            }
        });

        //???1???????????????
        if(refreshThread == null){
            refreshThread = new Thread(){
                @Override
                public void run() {
                    super.run();
                    while (refreshThreadState == true){
                        try {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    refreshData();
//                                    System.out.println("test - " + daaDataFormat.getDcdcInfoByStateFormat(0).getDcdcState());
                                }
                            });
                            sleep(1000);
                        } catch (Exception e) {
                            LocalLog.getInstance().writeLog(e.toString(), A_Admin_9.class);
                        }
                    }
                }
            };
            refreshThread.start();
        }
    }

    //????????????
    protected void refreshData(){
        if(daaDataFormat != null && environmentDataFormat != null){
            if(adminDcdcTopAdapter_12 == null){
                adminDcdcTopAdapter_12 = new AdminDcdcTopAdapter_12(activity, daaDataFormat.getDcdcInfoByBaseFormats(), daaDataFormat.getDcdcInfoByStateFormats(), daaDataFormat.getDcdcInfoByWarningFormats(), new AdminDcdcTopAdapter_12.AdminTopAdapterListener() {
                    @Override
                    public void openDoor(int door) {
                        LogicOpenDoor.getInstance().putterPush(door,CabInfoSp.getInstance().getPutterActivityTime(),"??????????????????");
                    }
                });
                bar_info_girdview.setAdapter(adminDcdcTopAdapter_12);
            }else{
                adminDcdcTopAdapter_12.setData(daaDataFormat.getDcdcInfoByBaseFormats(), daaDataFormat.getDcdcInfoByStateFormats(), daaDataFormat.getDcdcInfoByWarningFormats());
                adminDcdcTopAdapter_12.notifyDataSetChanged();
            }
            if(adminDcdcBottomAdapter_12 == null){
                adminDcdcBottomAdapter_12 = new AdminDcdcBottomAdapter_12(activity,daaDataFormat.getAcdcInfoByStateFormats(),daaDataFormat.getAcdcInfoByWarningFormats(),environmentDataFormat);
                cha_info_girdview.setAdapter(adminDcdcBottomAdapter_12);
            }else{
                adminDcdcBottomAdapter_12.setData(daaDataFormat.getAcdcInfoByStateFormats() , daaDataFormat.getAcdcInfoByWarningFormats());
                adminDcdcBottomAdapter_12.notifyDataSetChanged();
            }
        }
    }

    private void updateWebSocket(WebSocketReturnDataFormat webSocketReturnDataFormat){
        if(webSocketReturnDataFormat.getWebSocketReturnType() == WebSocketReturnType.closeAdmin){
            activity.finish();
        }
    }


    @OnClick(R.id.finish)
    public void onViewClick_finish() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // ????????????
            Toast.makeText(activity, "???????????????????????????", Toast.LENGTH_LONG).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;// ????????????
                }
            }, 2000); // ??????2?????????????????????????????????????????????????????????????????????????????????
        } else {
            // 1. ??????Context??????ActivityManager
            ActivityManager activityManager = (ActivityManager) activity.getApplicationContext().getSystemService(activity.ACTIVITY_SERVICE);
            // 2. ??????ActivityManager???????????????
            List<ActivityManager.AppTask> appTaskList = activityManager.getAppTasks();
            // 3. ????????????Activity
            for (ActivityManager.AppTask appTask : appTaskList) {
                appTask.finishAndRemoveTask();
            }
            // 4. ????????????
            System.exit(0);
        }
    }

    @OnClick(R.id.retuen_page)
    public void onViewClick_return() {
        this.finish();
    }

    @OnClick(R.id.thread_protection)
    public void onViewClickThreadProtection() {
        String threadProtectionType = CabInfoSp.getInstance().getTPTNumber();
        if (threadProtectionType.equals("1")) {
            thread_protection.setImageResource(R.drawable.image_7);
            CabInfoSp.getInstance().setTPTNumber("0");
        } else if (threadProtectionType.equals("0")) {
            thread_protection.setImageResource(R.drawable.image_6);
            CabInfoSp.getInstance().setTPTNumber("1");
        }
    }

    @OnClick(R.id.electricReboot)
    public void onViewClickElectricReboot(){
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // ????????????
            Toast.makeText(activity, "???????????????????????????", Toast.LENGTH_LONG).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;// ????????????
                }
            }, 2000); // ??????2?????????????????????????????????????????????????????????????????????????????????
        } else {
            ElectricityMeterController.getInstance().rebootAndroid();
        }
    }

    @OnClick(R.id.fanControl)
    public void onViewClickFanControl(){
        DialogAdminFanControl dialogAdminFanControl = new DialogAdminFanControl(activity);
        dialogAdminFanControl.show();
    }

    @OnClick(R.id.currentPlateControl)
    public void onViewClickCurrentPlateControl(){
        DialogAdminCurrentPlateControl currentPlateControl = new DialogAdminCurrentPlateControl(activity);
        currentPlateControl.show();
    }

    @OnClick(R.id.fireControl)
    public void onViewClickFireControl(){
        DialogAdminFireControl dialogAdminFireControl = new DialogAdminFireControl(activity);
        dialogAdminFireControl.show();
    }

    @OnClick(R.id.cleanMeter)
    public void onViewClickCleanMeter(){
        ElectricityMeterController.getInstance().cleanMeter();
    }


}
