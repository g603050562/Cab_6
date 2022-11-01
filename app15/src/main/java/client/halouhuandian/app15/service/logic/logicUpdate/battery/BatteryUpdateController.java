package client.halouhuandian.app15.service.logic.logicUpdate.battery;

import client.halouhuandian.app15.service.logic.logicUpdate.UpdateInfoFormat;
import client.halouhuandian.app15.service.logic.logicUpdate.UpdateInfoReturnListener;

/**
 * 电池升级控制类
 */
public final class BatteryUpdateController{

    //单例
    private static volatile BatteryUpdateController batteryUpdateController;
    private BatteryUpdateController(){};
    public static BatteryUpdateController getInstance(){
        if(batteryUpdateController == null){
            synchronized (BatteryUpdateController.class){
                if(batteryUpdateController == null){
                    batteryUpdateController = new BatteryUpdateController();
                }
            }
        }
        return batteryUpdateController;
    }

    private BatteryUpgradeProcess batteryUpgradeProcess;
    private UpdateInfoReturnListener updateInfoReturnListener;

    //开始
    public void init(UpdateInfoFormat updateInfoFormat , UpdateInfoReturnListener mUpdateInfoReturnListener) {
        this.updateInfoReturnListener = mUpdateInfoReturnListener;
        if(batteryUpgradeProcess == null){
            batteryUpgradeProcess = new BatteryUpgradeProcess(updateInfoFormat, new UpdateInfoReturnListener() {
                @Override
                public void returnInfo(int door, UpdateTypeInfo type, String info) {
                    updateInfoReturnListener.returnInfo(door,type,info);
                }

                @Override
                public void returnRate(long current, long total) {
                    updateInfoReturnListener.returnRate(current,total);
                }
            });
            if(updateInfoFormat.getType().equals("DengBo")){
                batteryUpgradeProcess.onStartByDengBo();
            }else{
                batteryUpgradeProcess.onStart();
            }

        }
        BatteryUpdateIntegration.getInstance().init(updateInfoFormat.getDoor());
    }

    public void onDestroy(){
        if(batteryUpgradeProcess!=null){
            batteryUpgradeProcess.onDestroy();
            batteryUpgradeProcess = null;
        }
        BatteryUpdateIntegration.getInstance().onDestroy();
    }
}