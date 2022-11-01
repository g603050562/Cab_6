package client.halouhuandian.app15.service.logic.logicTimeThread;


import android.content.Context;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import client.halouhuandian.app15.config.SystemConfig;
import client.halouhuandian.app15.hardWareConncetion.daa.DaaController;
import client.halouhuandian.app15.hardWareConncetion.daa.DaaIntegration;
import client.halouhuandian.app15.hardWareConncetion.daa.DaaSend;
import client.halouhuandian.app15.hardWareConncetion.daa.mode.DaaDataFormat;
import client.halouhuandian.app15.hardWareConncetion.environmentPlate.electricityMeter.ElectricityMeterController;
import client.halouhuandian.app15.model.dao.fileSave.LocalLog;
import client.halouhuandian.app15.model.dao.sharedPreferences.CabInfoSp;
import client.halouhuandian.app15.model.dao.sharedPreferences.ForbiddenSp;
import client.halouhuandian.app15.model.dao.sqlLite.ExchangeInfoDB;
import client.halouhuandian.app15.model.dao.sqlLite.OutLineExchangeSaveInfo;
import client.halouhuandian.app15.pub.BaseDataDistribution;
import client.halouhuandian.app15.service.logic.logicHttpConnection.http.BaseHttp;
import client.halouhuandian.app15.service.logic.logicHttpConnection.http.BaseHttpParameterFormat;
import client.halouhuandian.app15.service.logic.logicHttpConnection.http.HttpUrlMap;
import client.halouhuandian.app15.service.logic.logicNetDBM.DataDistributionCurrentNetDBM;
import client.halouhuandian.app15.service.logic.logicOpenDoor.LogicOpenDoor;
import client.halouhuandian.app15.service.logic.logicWriteUid.LogicWriteUid;

/**
 * 时间线程
 */
public class TimeThread extends BaseDataDistribution {

    private static volatile TimeThread timeThread;
    private TimeThread(){};
    public static TimeThread getInstance(){
        if(timeThread == null){
            synchronized (TimeThread.class){
                if(timeThread == null){
                    timeThread = new TimeThread();
                }
            }
        }
        return timeThread;
    }

    public interface TimeThreadReturn{
        void initFinish();
    }


    private Context context;
    //线程
    private Thread thread;
    //线程code
    private int threadCode = 0;
    //初始化完成参数
    private boolean isInitFinish = false;
    //daa数据接口监听
    private DaaController.DaaControllerListener daaControllerListener;
    //daa数据
    private DaaDataFormat daaDataFormat;
    //接口返回
    private TimeThreadReturn timeThreadReturn;

    public void init(final Context context) {
        init(context , null);
    }

    public void init(final Context context , TimeThreadReturn timeThreadReturn){
        this.context = context;
        this.timeThreadReturn = timeThreadReturn;

        DaaController.getInstance().addListener(daaControllerListener = new DaaController.DaaControllerListener() {
            @Override
            public void returnData(DaaDataFormat mDaaDataFormat, DaaIntegration.ReturnDataType returnDataType, int index) {
                daaDataFormat = mDaaDataFormat;
            }
        });

        onStart();
    }

    private void onStart(){
        if (thread == null) {
            thread = new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        //先睡几秒
                        sleep(1000 * 5);
                        //开启启动 打开一个空的仓门
                        for (int i = 0; i < SystemConfig.getMaxBattery(); i++) {
                            int is_stop = ForbiddenSp.getInstance().getTargetForbidden(i);
                            if (daaDataFormat.getDcdcInfoByBaseFormat(i).getInchingByOuterClose() == 0 && is_stop == 1) {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("msg","即将关闭" + (i + 1) + "号舱门" + "请注意安全！");
                                jsonObject.put("time",10+"");
                                jsonObject.put("type",1);
                                sendData(new TimeThreadDataFormat(TimeThreadDataType.showDialog ,jsonObject.toString() ));
                                LogicOpenDoor.getInstance().putterPull(i+1,CabInfoSp.getInstance().getPutterActivityTime() , "开机关闭已经打开的舱门");
                                Thread.sleep(3000);
                            }
                        }
                        //返回初始化触发
                        isInitFinish = true;
                        if(timeThreadReturn !=null){
                            timeThreadReturn.initFinish();
                        }
                        //循环输出
                        while (threadCode == 0) {
                            sleep(1000);
                            //每秒触发
                            sendData(new TimeThreadDataFormat(TimeThreadDataType.dateReturn , System.currentTimeMillis() + ""));
                            Calendar calendar = Calendar.getInstance();
                            int minute = calendar.get(Calendar.MINUTE);
                            int second = calendar.get(Calendar.SECOND);

                            if (second % 30 == 0) {

                                //删除数据库操作
                                final ExchangeInfoDB exchangeInfoDB = ExchangeInfoDB.getInstance(context);
                                final OutLineExchangeSaveInfo outLineExchangeSaveInfo = exchangeInfoDB.getLastInfo();
                                if (outLineExchangeSaveInfo != null) {
                                    List<BaseHttpParameterFormat> baseHttpParameterFormats = new ArrayList<>();
                                    baseHttpParameterFormats.add(new BaseHttpParameterFormat("number", outLineExchangeSaveInfo.getNumber()));
                                    baseHttpParameterFormats.add(new BaseHttpParameterFormat("uid32", outLineExchangeSaveInfo.getUid()));
                                    baseHttpParameterFormats.add(new BaseHttpParameterFormat("extime", outLineExchangeSaveInfo.getExtime()));
                                    baseHttpParameterFormats.add(new BaseHttpParameterFormat("in_battery", outLineExchangeSaveInfo.getInBattery()));
                                    baseHttpParameterFormats.add(new BaseHttpParameterFormat("in_door", outLineExchangeSaveInfo.getInDoor()));
                                    baseHttpParameterFormats.add(new BaseHttpParameterFormat("in_electric", outLineExchangeSaveInfo.getInElectric()));
                                    baseHttpParameterFormats.add(new BaseHttpParameterFormat("out_battery", outLineExchangeSaveInfo.getOutBattery()));
                                    baseHttpParameterFormats.add(new BaseHttpParameterFormat("out_door", outLineExchangeSaveInfo.getOutDoor()));
                                    baseHttpParameterFormats.add(new BaseHttpParameterFormat("out_electric", outLineExchangeSaveInfo.getOutElectric()));
                                    BaseHttp baseHttp = new BaseHttp(HttpUrlMap.UploadExchangeLog, baseHttpParameterFormats, new BaseHttp.BaseHttpListener() {
                                        @Override
                                        public void dataReturn(int code, String message, String data) {
                                            if (code == 1) {
                                                final String fExtime = outLineExchangeSaveInfo.getExtime();
                                                exchangeInfoDB.deleteData(fExtime);
                                            }
                                        }
                                    });
                                    baseHttp.onStart();
                                    System.out.println("网络：   正在上传数据库数据   " + outLineExchangeSaveInfo.getExtime());
                                }


                                //设置加热模式
                                String isHeat = CabInfoSp.getInstance().getHeatMode();
                                if(isHeat.equals("1")){
                                    DaaSend.setHeatingDefault();
                                }else if(isHeat.equals("2")){
                                    DaaSend.setHeatingAuto();
                                }else if(isHeat.equals("-1")){
                                    DaaSend.setHeatingClose();
                                }
                            }
                            //每三分钟判断一下 电柜里面 擦写失败的电池 重写写成AAAAAAAA
                            if (minute % 3 == 0 && second == 0) {
                                for (int i = 0; i < SystemConfig.getMaxBattery(); i++) {
                                    if (ForbiddenSp.getInstance().getTargetForbidden(i) == -3) {
                                        String UIDS = daaDataFormat.getDcdcInfoByBaseFormat(i).getUID();
                                        if (UIDS.equals("AAAAAAAA") || UIDS.equals("00000000")) {
                                            ForbiddenSp.getInstance().setTargetForbidden(i, 1);
                                        } else {
                                            new LogicWriteUid(i+1, "AAAAAAAA");
                                        }
                                    }
                                }
                            }
                            //每天晚上两点半 准时重启电柜
                            SimpleDateFormat sf = new SimpleDateFormat("HH");
                            String time = sf.format(new Date());
                            if(time.equals("02") && minute == 30 && (second == 0 || second == 30)){
                                ElectricityMeterController.getInstance().rebootAndroid();
                            }
                            //本地心跳日志
                            if(second == 0){
                                LocalLog.getInstance().writeLog("心跳日志 - dbm信号 - (" + DataDistributionCurrentNetDBM.getInstance().getDbm() + ") - 本地未上传换电日志数 - " + ExchangeInfoDB.getInstance(context).getCount());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }
    }

    public boolean getIsInitFinish(){
        return isInitFinish;
    }


    public void onDestroy() {
        threadCode = 1;
        DaaController.getInstance().deleteListener(daaControllerListener);
    }
}
