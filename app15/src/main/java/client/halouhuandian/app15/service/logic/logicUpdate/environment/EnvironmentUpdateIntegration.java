package client.halouhuandian.app15.service.logic.logicUpdate.environment;



import java.util.ArrayList;

import client.halouhuandian.app15.hardWareConncetion.androidHard.BaseDataRegister;
import client.halouhuandian.app15.hardWareConncetion.androidHard.BaseDataReturnListener;
import client.halouhuandian.app15.hardWareConncetion.androidHard.CanDataFormat;
import client.halouhuandian.app15.hardWareConncetion.androidHard.SerialAndCanPortUtilsGeRui;
import client.halouhuandian.app15.pub.BaseDataDistribution;
import client.halouhuandian.app15.pub.util.UtilPublic;
import client.halouhuandian.app15.service.logic.logicUpdate.UpdateInfoFormat;

public class EnvironmentUpdateIntegration extends BaseDataDistribution {

    //单例
    private static volatile EnvironmentUpdateIntegration environmentUpdateIntegration;
    private EnvironmentUpdateIntegration(){}
    public static EnvironmentUpdateIntegration getInstance(){
        if(environmentUpdateIntegration == null) {
            synchronized (EnvironmentUpdateIntegration.class){
                if(environmentUpdateIntegration == null){
                    environmentUpdateIntegration = new EnvironmentUpdateIntegration();
                }
            }
        }
        return environmentUpdateIntegration;
    }

    private String address = "98B06566";
    private UpdateInfoFormat updateInfoFormat;
    //数据注册
    private BaseDataRegister baseDataRegister;

    public void init(UpdateInfoFormat updateInfoFormat){
        this.updateInfoFormat = updateInfoFormat;
        onStart();
    }

    private void onStart(){

        if(baseDataRegister == null){
            ArrayList<long[]> rangeList = new ArrayList<>();
            rangeList.add(new long[]{Long.parseLong(address , 16) ,Long.parseLong(address , 16)});
            baseDataRegister = new BaseDataRegister(rangeList, new BaseDataReturnListener() {
                @Override
                public void returnData(CanDataFormat canDataFormat) {
                    onCanResultApp(canDataFormat);
                }
            });
            SerialAndCanPortUtilsGeRui.getInstance().addListener(baseDataRegister);

        }
    }

    public void onDestroy(){
        SerialAndCanPortUtilsGeRui.getInstance().deleteListener(baseDataRegister);
        baseDataRegister = null;
    }

    public void onCanResultApp(CanDataFormat canDataFormat) {
        //can地址
        long addressLong = canDataFormat.getAddressByLong();
        //can数据
        byte[] canData = canDataFormat.getData();

        System.out.println("update - address - 接收 - "+ address + " - data - "+ UtilPublic.ByteArrToHex(canData));
        if(addressLong == Long.parseLong(address,16)){
            if((canData[2] & 0xff) == 0x08 && (canData[5] & 0xff) == 0x06){
                System.out.println("update - address - 接收 - "+ address + " - data - "+ UtilPublic.ByteArrToHex(canData));
                System.out.println("update - 数据返回 - 积极回复");
                sendData(new EnvironmentUpdateDataFormat(addressLong , canData , EnvironmentUpdateDataFormat.EnvironmentUpdateType.requireData));
            }
            if((canData[2] & 0xff) == 0x08 && (canData[5] & 0xff) == 0x15){
                System.out.println("update - address - 接收 - "+ address + " - data - "+ UtilPublic.ByteArrToHex(canData));
                System.out.println("update - 数据返回 - 消极回复");
                sendData(new EnvironmentUpdateDataFormat(addressLong , canData , EnvironmentUpdateDataFormat.EnvironmentUpdateType.requireData));
            }
        }
    }

}
