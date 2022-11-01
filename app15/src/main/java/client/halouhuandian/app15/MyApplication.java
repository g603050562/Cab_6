package client.halouhuandian.app15;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;


import com.tencent.bugly.crashreport.CrashReport;

import client.halouhuandian.app15.hardWareConncetion.androidHard.SerialAndCanPortUtilsGeRui;
import client.halouhuandian.app15.hardWareConncetion.daa.DaaController;
import client.halouhuandian.app15.hardWareConncetion.environmentPlate.EnvironmentController;
import client.halouhuandian.app15.model.dao.fileSave.LocalLog;
import client.halouhuandian.app15.model.dao.sharedPreferences.CabInfoSp;
import client.halouhuandian.app15.model.dao.sharedPreferences.ForbiddenSp;
import client.halouhuandian.app15.pub.BaseDataDistribution;
import client.halouhuandian.app15.pub.util.UtilFilesDirectory;
import client.halouhuandian.app15.service.logic.logicChangeBatteries.ChangeBatteriesController;
import client.halouhuandian.app15.service.logic.logicFind4gCard.Find4gCard;
import client.halouhuandian.app15.service.logic.logicFind4gCard.Find4gCardReturnDataFormat;
import client.halouhuandian.app15.service.logic.logicHttpConnection.http.HttpUrlMap;
import client.halouhuandian.app15.service.logic.logicHttpConnection.webSocket.WebSocketController;
import client.halouhuandian.app15.service.logic.logicNetDBM.DataDistributionCurrentNetDBM;
import client.halouhuandian.app15.service.logic.logicOpenDoor.LogicOpenDoor;
import client.halouhuandian.app15.service.logic.logicTTL.TextToSpeechController;
import client.halouhuandian.app15.service.logic.logicTimeThread.TimeThread;


/**
 * Created by hasee on 2017/3/23.
 * 主要功能就是 完全退出APP
 */

public class MyApplication extends Application {

    //单例
    private static MyApplication instance = new MyApplication();
    //电柜版本信息
    private static String CAB_VERSION = "2.0.004.25";

    public MyApplication() {}

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //获取设备信息
        CabInfoSp.getInstance().init(getApplicationContext());
        ForbiddenSp.getInstance().init(getApplicationContext());
        CabInfoSp.getInstance().setAndroidDeviceModel(Build.MODEL);
        CabInfoSp.getInstance().setAndroidVersionRelease(Build.VERSION.RELEASE);
        CabInfoSp.getInstance().setVersion(CAB_VERSION+"");
        //初始化文件夹
        UtilFilesDirectory.getInstance().init(getApplicationContext());
        //下层数据通信
        initHandCommSerialAndCanPortUtils();
        //上层数据通信
        initSoftCommSerialAndCanPortUtils();
        //bugly初始化
//        CrashReport.setAppPackage(getApplicationContext(), CabInfoSp.getInstance().getCabinetNumber_4600XXXX());
//        CrashReport.initCrashReport(getApplicationContext(), "7c6109f88e", false);
        System.out.println("teat");
    }

    /**
     * 底层通信
     * 单例 统一从这里下发数据
     * 485串口 和 canbus 初始化 （不同android板的抽象工厂实现）
     * 原始数据分发（主要用于数据解析 或者 升级）
     */

    // 添加Listener到容器中
    public void initHandCommSerialAndCanPortUtils(){
        SerialAndCanPortUtilsGeRui.getInstance().init();
    }

    /**
     * 上层通信
     * 485串口 和 can 初始化
     * 经过解析数据触发条件分发（主要用于更新数据）
     */

    private void initSoftCommSerialAndCanPortUtils(){
        //守护进程的service
        Intent intent = new Intent(getApplicationContext(), MyService.class);
        PendingIntent sender = PendingIntent.getService(getApplicationContext(), 0, intent, 0);
        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000, sender);
        //日志初始化
        LocalLog.getInstance().init(CabInfoSp.getInstance().getCabinetNumber_4600XXXX());
        //开启获取DBM服务
        DataDistributionCurrentNetDBM.getInstance().init(getApplicationContext());
        //获取4g卡线程初始化
        Find4gCard.getInstance().init(getApplicationContext());
        Find4gCard.getInstance().addListener(new BaseDataDistribution.LogicListener() {
            @Override
            public void returnData(Object object) {
                Find4gCardReturnDataFormat find4gCardReturnDataFormat = (Find4gCardReturnDataFormat)object;
                if(find4gCardReturnDataFormat.getFind4gCardReturnDataType() == Find4gCardReturnDataFormat.Find4gCardReturnDataType.IMSI){
                    //获得4g卡后开启长链接服务
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                sleep(3000);
                                WebSocketController.getInstance().init(getApplicationContext());
                            } catch (Exception e) {
                                LocalLog.getInstance().writeLog(e.toString(),MyApplication.class);
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            }
        });
        //dcdc数据解析服务初始化
        DaaController.getInstance().init();
        //时间线程
        TimeThread.getInstance().init(getApplicationContext(), new TimeThread.TimeThreadReturn() {
            @Override
            public void initFinish() {
                //换电线程
                ChangeBatteriesController.getInstance().init(getApplicationContext());
            }
        });
        //环境板数据解析服务
        EnvironmentController.getInstance().init(getApplicationContext());
        //打开舱门初始化
        LogicOpenDoor.getInstance().init();
        //ttl初始化
        TextToSpeechController.getInstance().init(getApplicationContext());
        //初始化服务器
        HttpUrlMap.setServer(CabInfoSp.getInstance().getServer());
    }
}

