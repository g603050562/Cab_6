package client.halouhuandian.app15.service.logic.logicUpdate;

import client.halouhuandian.app15.service.logic.logicUpdate.acdc.AcdcUpdateController;
import client.halouhuandian.app15.service.logic.logicUpdate.battery.BatteryUpdateController;
import client.halouhuandian.app15.service.logic.logicUpdate.dcdc.DcdcUpdateController;
import client.halouhuandian.app15.service.logic.logicUpdate.environment.EnvironmentUpdateController;

/**
 * 升级控制器
 */
public class UpdateController {

    private static volatile UpdateController updateController;
    private UpdateController(){}
    public static UpdateController getInstance(){
        if(updateController == null){
            synchronized (UpdateController.class){
                if(updateController == null){
                    updateController = new UpdateController();
                }
            }
        }
        return updateController;
    }

    private UpdateInfoFormat updateInfoFormat;

    public void init(UpdateInfoFormat updateInfoFormat , UpdateInfoReturnListener updateInfoReturnListener){
        this.updateInfoFormat = updateInfoFormat;
        if(updateInfoFormat.getUpdateType() == UpdateInfoFormat.UpdateType.updateBattery){
            BatteryUpdateController.getInstance().init(updateInfoFormat , updateInfoReturnListener);
        } else if(updateInfoFormat.getUpdateType() == UpdateInfoFormat.UpdateType.updateDcdc){
            DcdcUpdateController.getInstance().init(updateInfoFormat , updateInfoReturnListener);
        } else if(updateInfoFormat.getUpdateType() == UpdateInfoFormat.UpdateType.updateAcdc){
            AcdcUpdateController.getInstance().init(updateInfoFormat , updateInfoReturnListener);
        } else if(updateInfoFormat.getUpdateType() == UpdateInfoFormat.UpdateType.updateEnvironment){
            EnvironmentUpdateController.getInstance().init(updateInfoFormat , updateInfoReturnListener);
        }
    }

    public void onDestroy(){
        if(updateInfoFormat.getUpdateType() == UpdateInfoFormat.UpdateType.updateBattery){
            BatteryUpdateController.getInstance().onDestroy();
        } else if(updateInfoFormat.getUpdateType() == UpdateInfoFormat.UpdateType.updateDcdc){
            DcdcUpdateController.getInstance().onDestroy();
        } else if(updateInfoFormat.getUpdateType() == UpdateInfoFormat.UpdateType.updateAcdc){
            AcdcUpdateController.getInstance().onDestroy();
        } else if(updateInfoFormat.getUpdateType() == UpdateInfoFormat.UpdateType.updateEnvironment){
            EnvironmentUpdateController.getInstance().onDestroy();
        }
    }
}
