package client.halouhuandian.app15.service.logic.logicUpdate.dcdc;

import client.halouhuandian.app15.service.logic.logicUpdate.UpdateInfoFormat;
import client.halouhuandian.app15.service.logic.logicUpdate.UpdateInfoReturnListener;

public class DcdcUpdateController {

    //单例
    private static volatile DcdcUpdateController dcdcUpdateController;
    private DcdcUpdateController(){}
    public static DcdcUpdateController getInstance(){
        if(dcdcUpdateController == null){
            synchronized (DcdcUpdateController.class){
                if(dcdcUpdateController == null){
                    dcdcUpdateController = new DcdcUpdateController();
                }
            }
        }
        return dcdcUpdateController;
    }

    private DcdcUpdateProcess dcdcUpdateProcess;

    public void init(UpdateInfoFormat updateInfoFormat , UpdateInfoReturnListener updateInfoReturnListener){
        //打开升级dcdc数据监听
        DcdcUpdateIntegration.getInstance().init(updateInfoFormat);
        //打开升级dcdc流程
        dcdcUpdateProcess = new DcdcUpdateProcess(updateInfoFormat  , updateInfoReturnListener);
        dcdcUpdateProcess.onStart();
    }

    public void onDestroy(){
        //结束升级dcdc数据监听
        DcdcUpdateIntegration.getInstance().onDestroy();
        dcdcUpdateProcess.onDestroy();
        dcdcUpdateProcess = null;
    }
}
