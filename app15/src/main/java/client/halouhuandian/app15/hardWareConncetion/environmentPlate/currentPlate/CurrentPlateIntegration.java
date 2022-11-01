package client.halouhuandian.app15.hardWareConncetion.environmentPlate.currentPlate;


import java.util.ArrayList;

import client.halouhuandian.app15.hardWareConncetion.androidHard.BaseDataRegister;
import client.halouhuandian.app15.hardWareConncetion.androidHard.BaseDataReturnListener;
import client.halouhuandian.app15.hardWareConncetion.androidHard.CanDataFormat;
import client.halouhuandian.app15.hardWareConncetion.androidHard.SerialAndCanPortUtilsGeRui;

/**
 * 环境板数据信息解析整合
 */
public class CurrentPlateIntegration{

    public interface CurrentPlateIntegrationListener{
        void returnData(CurrentPlateDataFormat currentPlateDataFormat);
    }

    //地址
    private long addressLong = 2562785128l; // 98c0ff68
    private CurrentPlateIntegrationListener currentPlateIntegrationListener;
    //数据注册
    private BaseDataRegister baseDataRegister;

    public CurrentPlateIntegration(CurrentPlateIntegrationListener currentPlateIntegrationListener){

        this.currentPlateIntegrationListener = currentPlateIntegrationListener;

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
            CurrentPlateDataFormat currentPlateDataFormat = new CurrentPlateDataFormat(data);
            currentPlateIntegrationListener.returnData(currentPlateDataFormat);
        }
    }

}
