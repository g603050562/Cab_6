package client.halouhuandian.app15.service.logic.logicUpdate.environment;

import client.halouhuandian.app15.service.logic.logicUpdate.UpdateInfoFormat;
import client.halouhuandian.app15.service.logic.logicUpdate.UpdateInfoReturnListener;

public class EnvironmentUpdateController {

    //单例
    private static volatile EnvironmentUpdateController environmentUpdateController;
    private EnvironmentUpdateController(){}
    public static EnvironmentUpdateController getInstance(){
        if(environmentUpdateController == null){
            synchronized (EnvironmentUpdateController.class){
                if(environmentUpdateController == null){
                    environmentUpdateController = new EnvironmentUpdateController();
                }
            }
        }
        return environmentUpdateController;
    }

    private EnvironmentUpdateProcess environmentUpdateProcess;

    public void init(UpdateInfoFormat updateInfoFormat , UpdateInfoReturnListener updateInfoReturnListener){
        EnvironmentUpdateIntegration.getInstance().init(updateInfoFormat);
        environmentUpdateProcess = new EnvironmentUpdateProcess(updateInfoFormat  , updateInfoReturnListener);
        environmentUpdateProcess.onStart();
    }

    public void onDestroy(){
        EnvironmentUpdateIntegration.getInstance().onDestroy();
        environmentUpdateProcess.onDestroy();
        environmentUpdateProcess = null;
    }


}
