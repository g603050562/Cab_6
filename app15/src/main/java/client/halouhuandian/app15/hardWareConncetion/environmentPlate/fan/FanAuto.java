package client.halouhuandian.app15.hardWareConncetion.environmentPlate.fan;

import android.content.Context;

import client.halouhuandian.app15.hardWareConncetion.daa.DaaController;
import client.halouhuandian.app15.hardWareConncetion.daa.DaaIntegration;
import client.halouhuandian.app15.hardWareConncetion.daa.mode.DaaDataFormat;
import client.halouhuandian.app15.hardWareConncetion.environmentPlate.EnvironmentController;
import client.halouhuandian.app15.hardWareConncetion.environmentPlate.EnvironmentDataFormat;
import client.halouhuandian.app15.hardWareConncetion.environmentPlate.EnvironmentReturnDataFormat;
import client.halouhuandian.app15.model.dao.sharedPreferences.CabInfoSp;
import client.halouhuandian.app15.pub.BaseDataDistribution;

/**
 * 风扇逻辑
 * 分为 自动 和 手动模式    CabInfoSp.getInstance().getFanActivityMode() == 1 为自动    CabInfoSp.getInstance().getFanActivityMode() == ？？为手动
 * 自动的默认稳定梯度为30和40度
 * 网络长链接下发电柜状态可以更改
 */

public class FanAuto {

    private FanSwitcher fanSwitcher;

    private DaaController.DaaControllerListener daaControllerListener;
    private BaseDataDistribution.LogicListener environmentControllerListener;

    //acdc剩余总功率
    private double acdcSurplusPower;
    //活动的风扇个数
    private int activityFanCount = 0;
    //设置最小间隔时间
    private long diffTime = 10 * 1000;
    //发送时间
    private long sendTime = 0;
    public void init(Context context){
        fanSwitcher = new FanSwitcher();
        onStart();
    }

    private void onStart(){

        EnvironmentController.getInstance().addListener( environmentControllerListener = new BaseDataDistribution.LogicListener() {
            @Override
            public void returnData(Object object) {
                EnvironmentReturnDataFormat environmentReturnDataFormat = (EnvironmentReturnDataFormat)object;
                if(environmentReturnDataFormat.environmentReturnDataFormatType == EnvironmentReturnDataFormat.EnvironmentReturnDataFormatType.environmentData){
                    EnvironmentDataFormat environmentDataFormat = (EnvironmentDataFormat)environmentReturnDataFormat.getReturnData();
                    int fanState = CabInfoSp.getInstance().getFanActivityMode();

                    if(System.currentTimeMillis() > sendTime + diffTime){
                        sendTime = System.currentTimeMillis();
                    }else {
                        return;
                    }

                    if(fanState == 1){
                        double tem = environmentDataFormat.getTemperature_1();
                        if(tem >= -40){
                            int fanThreshold_1 = CabInfoSp.getInstance().getFanThreshold_1();
                            int fanThreshold_2 = CabInfoSp.getInstance().getFanThreshold_2();
                            fanGradientOpen(tem ,fanThreshold_1,fanThreshold_2 );
                        }else{
                            if(acdcSurplusPower <= 4 && acdcSurplusPower >= 2){
                                fanSwitcher.openFan_1();
                                activityFanCount = 1;
                            }else if(acdcSurplusPower < 2){
                                fanSwitcher.openFan_1();
                                fanSwitcher.openFan_2();
                                activityFanCount = 2;
                            }else {
                                activityFanCount = 0;
                            }
                        }
                    }else if(fanState == -1){
                        fanSwitcher.openFan_1();
                        fanSwitcher.closeFan_2();
                        activityFanCount = 1;
                    }else if(fanState == -2){
                        fanSwitcher.closeFan_1();
                        fanSwitcher.openFan_2();
                        activityFanCount = 1;
                    }else if(fanState == -3){
                        fanSwitcher.openFan_1();
                        fanSwitcher.openFan_2();
                        activityFanCount = 2;
                    }else if(fanState == -4){
                        fanSwitcher.closeFan_1();
                        fanSwitcher.closeFan_2();
                        activityFanCount = 0;
                    }
                }
            }
        });

        DaaController.getInstance().addListener(daaControllerListener = new DaaController.DaaControllerListener() {
            @Override
            public void returnData(DaaDataFormat daaDataFormat, DaaIntegration.ReturnDataType returnDataType, int index) {
                if(returnDataType.equals(DaaIntegration.ReturnDataType.acdcInfoByState)){
                    acdcSurplusPower = daaDataFormat.getAcdcInfoByStateFormats()[0].getAcdcSurplusPower();
                }
            }
        });
    }

    public int getActivityFanCount(){
        return activityFanCount;
    }

    public void onDestroy(){
        EnvironmentController.getInstance().deleteListener(environmentControllerListener);
        DaaController.getInstance().deleteListener(daaControllerListener);
    }

    private void fanGradientOpen(double tem , int fanThreshold_1 , int fanThreshold_2){
        if(tem >= fanThreshold_1){
            fanSwitcher.openFan_1();
        }else{
            fanSwitcher.closeFan_1();
        }
        if(tem >= fanThreshold_2){
            fanSwitcher.openFan_2();
        }else{
            fanSwitcher.closeFan_2();
        }
    }
}
