package client.halouhuandian.app15.hardWareConncetion.environmentPlate;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;

import client.halouhuandian.app15.hardWareConncetion.androidHard.BaseDataRegister;
import client.halouhuandian.app15.hardWareConncetion.androidHard.BaseDataReturnListener;
import client.halouhuandian.app15.hardWareConncetion.androidHard.CanDataFormat;
import client.halouhuandian.app15.hardWareConncetion.androidHard.SerialAndCanPortUtilsGeRui;
import client.halouhuandian.app15.hardWareConncetion.environmentPlate.currentPlate.CurrentPlateController;
import client.halouhuandian.app15.hardWareConncetion.environmentPlate.currentPlate.CurrentPlateDataFormat;
import client.halouhuandian.app15.model.dao.fileSave.LocalLog;
import client.halouhuandian.app15.model.dao.sharedPreferences.CabInfoSp;
import client.halouhuandian.app15.pub.util.UtilPublic;

/**
 * 解析环境板数据
 *
 */
public class  EnvironmentIntegration {

    public interface EnvironmentIntegrationListener{
        void returnData(EnvironmentDataFormat environmentDataFormat);
        void buttonTrigger(int number , int type); // type - 1 - 按下    type - 0 - 回弹 
    }

    //环境板地址
    private long addressLong = 2561697126l; // 98b06566
    //控制板基础信息数据阵列 处理缓存以及拼装数据
    private byte[][] controlPlateBaseArrays = new byte[9][8];
    //返回接口
    private EnvironmentIntegrationListener environmentIntegrationListener;
    //记录按钮触发状态
    private int[] buttonTriggerTypes = new int[]{-1,-1};
    //数据缓存
    private EnvironmentDataFormat environmentDataFormat;
    //触发事件
    private long triggerActivityTime = 0;
    //挂起参数
    private int hangUpState = 0;

    private Context context;

    //老电流板数据兼容
    //老环境板初始化参数
    private int oldCurrentPlateState = 0;

    //数据注册
    private BaseDataRegister baseDataRegister;

    public EnvironmentIntegration(Context context , EnvironmentIntegrationListener environmentIntegrationListener){
        this.context = context;
        this.environmentIntegrationListener = environmentIntegrationListener;
        environmentDataFormat = new EnvironmentDataFormat();

        ArrayList<long[]> rangeList = new ArrayList<>();
        rangeList.add(new long[]{addressLong ,addressLong});
        baseDataRegister = new BaseDataRegister(rangeList, new BaseDataReturnListener() {
            @Override
            public void returnData(CanDataFormat canDataFormat) {
                onCanResultApp(canDataFormat);
            }
        });
        SerialAndCanPortUtilsGeRui.getInstance().addListener(baseDataRegister);

    }

    public void onDestroy(){
        SerialAndCanPortUtilsGeRui.getInstance().deleteListener(baseDataRegister);
    }

    public void onCanResultApp(CanDataFormat canDataFormat) {

        long dataAddressLong = canDataFormat.getAddressByLong();
        byte[] data = canDataFormat.getData();

        if(dataAddressLong == addressLong){

            int topData = data[0];
            if (topData == 16) {
                controlPlateBaseArrays[0] = data;
            } else if (topData == 32) {
                controlPlateBaseArrays[1] = data;
            } else if (topData == 33) {
                controlPlateBaseArrays[2] = data;
            } else if (topData == 34) {
                controlPlateBaseArrays[3] = data;
            } else if (topData == 35) {
                controlPlateBaseArrays[4] = data;
            } else if (topData == 36) {
                controlPlateBaseArrays[5] = data;
            } else if (topData == 37) {
                controlPlateBaseArrays[6] = data;
            } else if (topData == 38) {
                controlPlateBaseArrays[7] = data;
            } else if (topData == 39) {
                controlPlateBaseArrays[8] = data;
                //判断是否有空值
                int isNull = 0;
                for (int i = 0; i < controlPlateBaseArrays.length; i++) {
                    if (controlPlateBaseArrays[i][0] == 0) {
                        isNull = 1;
                    }
                }
                //没有空值 就拼接数据 有空值不作操作等着清空
                if (isNull == 0) {
                    byte[] returnOrder = new byte[63];
                    for (int i = 0; i < controlPlateBaseArrays.length; i++) {
                        for (int j = 0; j < controlPlateBaseArrays[i].length - 1; j++) {
                            returnOrder[(i * 7) + j] = controlPlateBaseArrays[i][j + 1];
                        }
                    }

                    //电池信息解析
                    if (returnOrder != null) {
                        //基础信息整合返回
                        environmentDataFormat.baseDataFormat(returnOrder);
                        environmentIntegrationListener.returnData(environmentDataFormat);
                        //按钮触发处理
                        int buttonTrigger_1 = environmentDataFormat.getButtonStatus_1();
                        int buttonTrigger_2 = environmentDataFormat.getButtonStatus_2();

                        if(System.currentTimeMillis() - triggerActivityTime > 3 * 1000 && hangUpState == 0){
                            if(buttonTriggerTypes[0] == 0 && buttonTrigger_1 == 1){
                                environmentIntegrationListener.buttonTrigger(1,1);
                                triggerActivityTime = System.currentTimeMillis();
                            } else if(buttonTriggerTypes[1] == 0 && buttonTrigger_2 == 1){
                                environmentIntegrationListener.buttonTrigger(2,1);
                                triggerActivityTime = System.currentTimeMillis();
                            } else if(buttonTriggerTypes[0] == 1 && buttonTrigger_1 == 0){
                                environmentIntegrationListener.buttonTrigger(1,2);
                                triggerActivityTime = System.currentTimeMillis();
                            } else if(buttonTriggerTypes[1] == 1 && buttonTrigger_2 == 0){
                                environmentIntegrationListener.buttonTrigger(2,2);
                                triggerActivityTime = System.currentTimeMillis();
                            }
                        }

                        buttonTriggerTypes[0] = buttonTrigger_1;
                        buttonTriggerTypes[1] = buttonTrigger_2;

                        //老电流板兼容 如果环境板是177版本的 那肯定是老电流板
                        if(environmentDataFormat.getAddress() == 177 && oldCurrentPlateState == 0){

                            oldCurrentPlateState = 1;
                            CurrentPlateController.getInstance().init(new CurrentPlateController.CurrentPlateControllerListener() {
                                @Override
                                public void returnData(CurrentPlateDataFormat currentPlateDataFormat) {
                                    environmentDataFormat.extendDataFormat(currentPlateDataFormat);
                                }
                            });
                        }
                    }
                }
                //数据化数组
                initArrays();
            }
        }
    }

    public void hangUpButtonTrigger(){
        hangUpState = 1;
    }

    public void hangUpButtonTriggerCancel(){
        hangUpState = 0;
    }

    //初始化数组
    private void initArrays() {
        byte[] byte_8 = new byte[8];
        Arrays.fill(byte_8, (byte) 0);
        Arrays.fill(controlPlateBaseArrays , byte_8);
    }
}
