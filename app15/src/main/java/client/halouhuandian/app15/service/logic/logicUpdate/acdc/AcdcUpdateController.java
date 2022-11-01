package client.halouhuandian.app15.service.logic.logicUpdate.acdc;

import client.halouhuandian.app15.service.logic.logicUpdate.UpdateInfoFormat;
import client.halouhuandian.app15.service.logic.logicUpdate.UpdateInfoReturnListener;

public class AcdcUpdateController {

    //单例
    private static volatile AcdcUpdateController acdcUpdateController;
    private AcdcUpdateController(){}
    public static AcdcUpdateController getInstance(){
        if(acdcUpdateController == null){
            synchronized (AcdcUpdateController.class){
                if(acdcUpdateController == null){
                    acdcUpdateController = new AcdcUpdateController();
                }
            }
        }
        return acdcUpdateController;
    }

    private AcdcUpdateProcess acdcUpdateProcess;

    public void init(UpdateInfoFormat updateInfoFormat , UpdateInfoReturnListener updateInfoReturnListener){
        //打开升级acdc数据监听
        AcdcUpdateIntegration.getInstance().init(updateInfoFormat);
        //打开升级acdc流程
        acdcUpdateProcess = new AcdcUpdateProcess(updateInfoFormat  , updateInfoReturnListener);
        acdcUpdateProcess.onStart();
    }

    public void onDestroy(){
        //结束升级acdc数据监听
        AcdcUpdateIntegration.getInstance().onDestroy();
        acdcUpdateProcess.onDestroy();
        acdcUpdateProcess = null;
    }
}
