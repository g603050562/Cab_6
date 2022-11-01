package client.halouhuandian.app15.hardWareConncetion.environmentPlate.fireControl;

import java.util.Observable;
import java.util.Observer;

import client.halouhuandian.app15.hardWareConncetion.environmentPlate.EnvironmentController;
import client.halouhuandian.app15.hardWareConncetion.environmentPlate.EnvironmentDataFormat;
import client.halouhuandian.app15.hardWareConncetion.environmentPlate.EnvironmentReturnDataFormat;
import client.halouhuandian.app15.pub.BaseDataDistribution;

public class FireControlAuto {

    public interface FireControlObserverListener{
        void fireControlTrigger(int door);
    }

    private FireControlObserverListener fireControlObserverListener;
    private BaseDataDistribution.LogicListener environmentControllerListener;

    //初始化
    public void init(FireControlObserverListener fireControlObserverListener){
        this.fireControlObserverListener = fireControlObserverListener;
        onStart();
    }

    private void onStart() {
        EnvironmentController.getInstance().addListener(environmentControllerListener =  new BaseDataDistribution.LogicListener() {
            @Override
            public void returnData(Object object) {
                EnvironmentReturnDataFormat environmentReturnDataFormat = (EnvironmentReturnDataFormat)object;
                if(environmentReturnDataFormat.environmentReturnDataFormatType == EnvironmentReturnDataFormat.EnvironmentReturnDataFormatType.environmentData){
                    //todo::消防触发
                    EnvironmentDataFormat environmentDataFormat = (EnvironmentDataFormat)environmentReturnDataFormat.getReturnData();
                    if(environmentDataFormat.getSmoke() > 999999){
                        fireControlObserverListener.fireControlTrigger(0);
                    }
                }
            }
        });
    }

    public void onDestroy(){
        EnvironmentController.getInstance().deleteListener(environmentControllerListener);
    }
}
