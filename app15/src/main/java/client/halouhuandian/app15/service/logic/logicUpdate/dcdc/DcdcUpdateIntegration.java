package client.halouhuandian.app15.service.logic.logicUpdate.dcdc;

import java.util.ArrayList;

import client.halouhuandian.app15.hardWareConncetion.androidHard.BaseDataRegister;
import client.halouhuandian.app15.hardWareConncetion.androidHard.BaseDataReturnListener;
import client.halouhuandian.app15.hardWareConncetion.androidHard.CanDataFormat;
import client.halouhuandian.app15.hardWareConncetion.androidHard.SerialAndCanPortUtilsGeRui;
import client.halouhuandian.app15.hardWareConncetion.daa.DaaSend;
import client.halouhuandian.app15.pub.BaseDataDistribution;
import client.halouhuandian.app15.service.logic.logicUpdate.UpdateInfoFormat;

public class DcdcUpdateIntegration extends BaseDataDistribution {

    //单例
    private static volatile DcdcUpdateIntegration dcdcUpdateIntegration;
    private DcdcUpdateIntegration(){}
    public static DcdcUpdateIntegration getInstance(){
        if(dcdcUpdateIntegration == null) {
            synchronized (DcdcUpdateIntegration.class){
                if(dcdcUpdateIntegration == null){
                    dcdcUpdateIntegration = new DcdcUpdateIntegration();
                }
            }
        }
        return dcdcUpdateIntegration;
    }
    //连接帧 相应地址
    private String state_1 = "";
    //请求信息帧
    private String state_2 = "";
    //请求信息帧
    private String state_3 = "";

    private UpdateInfoFormat updateInfoFormat;

    //数据注册
    private BaseDataRegister baseDataRegister;

    public void init(UpdateInfoFormat updateInfoFormat){
        this.updateInfoFormat = updateInfoFormat;
        onStart();
    }

    private void onStart(){
        state_1 = DaaSend.creatDcUpdateOrder("9CA" , 1 , 0xE0 , updateInfoFormat.getDoor() , 0);
        state_2 = DaaSend.creatDcUpdateOrder("9C8" , 1 , 0xE0 , updateInfoFormat.getDoor() , 0);
        state_3 = DaaSend.creatDcUpdateOrder("9C9" , 1 , 0xE0 , updateInfoFormat.getDoor() , 0);

        if(baseDataRegister == null){
            ArrayList<long[]> rangeList = new ArrayList<>();
            rangeList.add(new long[]{Long.parseLong(state_1 , 16) ,Long.parseLong(state_1 , 16)});
            rangeList.add(new long[]{Long.parseLong(state_2 , 16) ,Long.parseLong(state_2 , 16)});
            rangeList.add(new long[]{Long.parseLong(state_3 , 16) ,Long.parseLong(state_3 , 16)});
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

        if(addressLong == Long.parseLong(state_1 , 16)){
            sendData(new DcdcUpdateDataFormat(addressLong , canData , DcdcUpdateDataFormat.DcdcUpdateType.liveConnection));
        }else if(addressLong == Long.parseLong(state_2 , 16)){
            sendData(new DcdcUpdateDataFormat(addressLong , canData , DcdcUpdateDataFormat.DcdcUpdateType.requireHandData));
        }else if(addressLong == Long.parseLong(state_3 , 16)){
            sendData(new DcdcUpdateDataFormat(addressLong , canData , DcdcUpdateDataFormat.DcdcUpdateType.requireBodyData));
        }
    }

}
