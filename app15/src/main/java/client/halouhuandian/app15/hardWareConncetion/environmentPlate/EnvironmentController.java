package client.halouhuandian.app15.hardWareConncetion.environmentPlate;

import android.content.Context;

import java.util.ArrayList;

import client.halouhuandian.app15.hardWareConncetion.environmentPlate.currentPlate.CurrentPlateSwitcher;
import client.halouhuandian.app15.hardWareConncetion.environmentPlate.fan.FanController;
import client.halouhuandian.app15.hardWareConncetion.environmentPlate.fireControl.FireControlController;
import client.halouhuandian.app15.model.dao.sharedPreferences.CabInfoSp;
import client.halouhuandian.app15.pub.BaseDataDistribution;

/**
 * 环境板控制器
 */
public class EnvironmentController extends BaseDataDistribution {

    //单例
    private volatile static EnvironmentController environmentController = null;
    private EnvironmentController(){}
    public static EnvironmentController getInstance(){
        if (environmentController == null){
            synchronized (EnvironmentController.class){
                if(environmentController == null){
                    environmentController = new EnvironmentController();
                }
            }
        }
        return environmentController;
    }

    private Context context;
    private EnvironmentIntegration environmentIntegration;
    private EnvironmentLifeThread environmentLifeThread;
    private EnvironmentDataFormat environmentDataFormat;

    //初始化
    public void init(Context context){
        this.context = context;
        environmentDataFormat = new EnvironmentDataFormat();
        onStart();
        setCurrentPlateParamInit();
    }

    private void onStart(){
        //打开环境板之后 消防监听
        FireControlController.getInstance().init();
        //打开环境板之后 风扇监听
        FanController.getInstance().init(context);
        //环境板生命帧
        if(environmentLifeThread == null){
            environmentLifeThread = new EnvironmentLifeThread();
            environmentLifeThread.onStart();
        }
        //数据回调分发
        if(environmentIntegration == null){
            environmentIntegration = new EnvironmentIntegration(context,new EnvironmentIntegration.EnvironmentIntegrationListener() {
                @Override
                public void returnData(EnvironmentDataFormat mEnvironmentDataFormat) {
                    environmentDataFormat = mEnvironmentDataFormat;
                    sendData(new EnvironmentReturnDataFormat(EnvironmentReturnDataFormat.EnvironmentReturnDataFormatType.environmentData , environmentDataFormat));
                }
                @Override
                public void buttonTrigger(int number, int type) {
                    sendData(new EnvironmentReturnDataFormat(EnvironmentReturnDataFormat.EnvironmentReturnDataFormatType.buttonTrigger , new int[]{number , type}));
                }
            });
        }
    }

    public void onDestroy(){
        FanController.getInstance().onDestroy();
        FireControlController.getInstance().onDestroy();
        environmentLifeThread.onDestroy();
        environmentIntegration.onDestroy();
    }

    public void hangUpButtonTrigger(){
        environmentIntegration.hangUpButtonTrigger();
    }

    public void hangUpButtonTriggerCancel(){
        environmentIntegration.hangUpButtonTriggerCancel();
    }


    //设置电流板阈值 - 不同版本区分
    public void setCurrentPlateParam(float currentThreshold, int currentLimitedTime, int relayRecoveryTime){
        if(environmentDataFormat.getAddress() == 177){
            new CurrentPlateSwitcher().setCurrentPlateParam(currentThreshold,currentLimitedTime,relayRecoveryTime);
        }else if(environmentDataFormat.getAddress() == 178){
            new EnvironmentSend().setCurrentPlateParam(currentThreshold,currentLimitedTime,relayRecoveryTime);
        }
    }

    public void setCurrentPlateParamInit(){
        float currentThreshold = CabInfoSp.getInstance().getCurrentThreshold();
        int currentLimitedTime = 800;
        int relayRecoveryTime = 1000;
        new CurrentPlateSwitcher().setCurrentPlateParam(currentThreshold,currentLimitedTime,relayRecoveryTime);
        new EnvironmentSend().setCurrentPlateParam(currentThreshold,currentLimitedTime,relayRecoveryTime);
    }

    public EnvironmentDataFormat getEnvironmentDataFormat() {
        return environmentDataFormat;
    }
}
