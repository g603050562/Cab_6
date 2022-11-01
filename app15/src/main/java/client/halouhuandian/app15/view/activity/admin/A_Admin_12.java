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

    //程序退出参数
    private static Boolean isExit = false;
    //线程保护按钮
    @BindView(R.id.thread_protection)
    public ImageView thread_protection;
    //电池显示列表
    @BindView(R.id.bar_info_girdview)
    public GridView bar_info_girdview;
    //充电器显示列表
    @BindView(R.id.cha_info_girdview)
    public GridView cha_info_girdview;
    //电柜断电重启
    @BindView(R.id.electricReboot)
    public TextView electricReboot;
    //风扇控制
    @BindView(R.id.fanControl)
    public TextView fanControl;
    //电流板控制器
    @BindView(R.id.currentPlateControl)
    public TextView currentPlateControl;
    //消防控制
    @BindView(R.id.fireControl)
    public TextView fireControl;
    //电表清零
    @BindView(R.id.cleanMeter)
    public TextView cleanMeter;

    //刷新线程
    private Thread refreshThread;
    //刷新线程状态
    private boolean refreshThreadState = true;
    //缓存数据
    private DaaDataFormat daaDataFormat;
    private EnvironmentDataFormat environmentDataFormat;
    //数据接口监听
    private DaaController.DaaControllerListener daaControllerListener;
    private BaseDataDistribution.LogicListener environmentControllerListener;
    private BaseDataDistribution.LogicListener webSocketListener;

    //适配器
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

        //线程保护
        String threadProtectionType = CabInfoSp.getInstance().getTPTNumber();
        if (threadProtectionType.equals("1")) {
            thread_protection.setImageResource(R.drawable.image_6);
        } else if (threadProtectionType.equals("0")) {
            thread_protection.setImageResource(R.drawable.image_7);
        }

        //dcdc数据监听
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

        //环境板数据监听
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

        //长连接监听
        WebSocketController.getInstance().addListener(webSocketListener = new BaseDataDistribution.LogicListener() {
            @Override
            public void returnData(Object object) {
                updateWebSocket((WebSocketReturnDataFormat)object);
            }
        });

        //每1秒更新数据
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

    //更新数据
    protected void refreshData(){
        if(daaDataFormat != null && environmentDataFormat != null){
            if(adminDcdcTopAdapter_12 == null){
                adminDcdcTopAdapter_12 = new AdminDcdcTopAdapter_12(activity, daaDataFormat.getDcdcInfoByBaseFormats(), daaDataFormat.getDcdcInfoByStateFormats(), daaDataFormat.getDcdcInfoByWarningFormats(), new AdminDcdcTopAdapter_12.AdminTopAdapterListener() {
                    @Override
                    public void openDoor(int door) {
                        LogicOpenDoor.getInstance().putterPush(door,CabInfoSp.getInstance().getPutterActivityTime(),"电柜后台开门");
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
            // 1. 通过Context获取ActivityManager
            ActivityManager activityManager = (ActivityManager) activity.getApplicationContext().getSystemService(activity.ACTIVITY_SERVICE);
            // 2. 通过ActivityManager获取任务栈
            List<ActivityManager.AppTask> appTaskList = activityManager.getAppTasks();
            // 3. 逐个关闭Activity
            for (ActivityManager.AppTask appTask : appTaskList) {
                appTask.finishAndRemoveTask();
            }
            // 4. 结束进程
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
            isExit = true; // 准备退出
            Toast.makeText(activity, "再按一次断电重启！", Toast.LENGTH_LONG).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;// 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
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
