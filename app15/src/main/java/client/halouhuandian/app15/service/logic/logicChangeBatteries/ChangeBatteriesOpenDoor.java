package client.halouhuandian.app15.service.logic.logicChangeBatteries;

import android.content.Context;
import client.halouhuandian.app15.hardWareConncetion.daa.DaaController;
import client.halouhuandian.app15.hardWareConncetion.daa.mode.DaaDataFormat;
import client.halouhuandian.app15.model.dao.fileSave.LocalLog;
import client.halouhuandian.app15.model.dao.sharedPreferences.CabInfoSp;
import client.halouhuandian.app15.model.dao.sharedPreferences.ForbiddenSp;
import client.halouhuandian.app15.pub.util.UtilBattery;
import client.halouhuandian.app15.service.logic.logicOpenDoor.LogicOpenDoor;
import client.halouhuandian.app15.service.logic.logicTimeThread.TimeThread;

/**
 * 电柜按钮触发打开随机舱门逻辑
 * 类外部调用onStart触发
 */

public class ChangeBatteriesOpenDoor {

    public interface ChangeBatteriesOpenDoorListener{
        void showDialog(String info , int time , int type);
    }

    private ChangeBatteriesOpenDoorListener changeBatteriesOpenDoorListener;

    //舱门打开时间
    private long openTime = 0;
    //挂起参数
    private boolean hangUp = false;


    public ChangeBatteriesOpenDoor(Context context , ChangeBatteriesOpenDoorListener changeBatteriesOpenDoorListener){
        this.changeBatteriesOpenDoorListener = changeBatteriesOpenDoorListener;

    }

    public void onStart(){

        DaaDataFormat daaDataFormat = DaaController.getInstance().getDaaDataFormat();
        boolean isInitFinish = TimeThread.getInstance().getIsInitFinish();

        float remainingTime = (65 - ((System.currentTimeMillis() - openTime) / 1000));
        if(remainingTime > 65 || remainingTime < 0){
            remainingTime = 10;
        }
        if (isInitFinish == false) {
            changeBatteriesOpenDoorListener.showDialog("正在等待初始化，请稍候！", 10, 1);
        } else if (UtilBattery.getEmptyDoor(ForbiddenSp.getInstance() , daaDataFormat) == -1) {
            changeBatteriesOpenDoorListener.showDialog("没有可以打开的空舱门！", 10, 1);
        } else if (UtilBattery.obtainOpenedEmptyDoor(ForbiddenSp.getInstance(),daaDataFormat) != -1) {
            changeBatteriesOpenDoorListener.showDialog("当前已经有打开的空舱门！", (int)remainingTime, 1);
        } else if (hangUp == true) {
            changeBatteriesOpenDoorListener.showDialog("正在进行硬件升级，请勿进行操作！", 10, 1);
        } else  if (System.currentTimeMillis() - openTime < 1000 * 60) {
            changeBatteriesOpenDoorListener.showDialog("正在检测舱门数据，请稍候再进行操作！", (int)remainingTime, 1);
        } else {
            final int intervalTime = 60;
            openTime = System.currentTimeMillis();
            int tarDoor = UtilBattery.getEmptyDoor(ForbiddenSp.getInstance(), daaDataFormat);
            changeBatteriesOpenDoorListener.showDialog("正在打开"+tarDoor+"号舱门，请插入您的电池，舱门将在<$time>秒后关闭，请注意安全！", intervalTime, 1);
            LogicOpenDoor.getInstance().putterPushAndPullExcludeBattery(tarDoor, CabInfoSp.getInstance().getPutterActivityTime(), 60, "按钮触发打开随机舱门", new LogicOpenDoor.LogicOpenDoorDialogListener() {
                @Override
                public void showDialog(String msg, int time, int type) {
                    changeBatteriesOpenDoorListener.showDialog(msg,time,type);
                }
            });
        }
    }

    public void onFinish(){
        openTime = 0;
    }


    public void hangUp(){
        hangUp = true;
    }

    public void hangUpCancel(){
        hangUp = false;
    }

    public void onDestroy(){
    }

    /**
     * 写入本地日志
     * @param log
     */
    public void writeLog(String log) {
        LocalLog.getInstance().writeLog(log , ChangeBatteriesOpenDoor.class);
    }

}
