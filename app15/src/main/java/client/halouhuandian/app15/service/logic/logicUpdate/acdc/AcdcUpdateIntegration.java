package client.halouhuandian.app15.service.logic.logicUpdate.acdc;

import java.util.ArrayList;

import client.halouhuandian.app15.hardWareConncetion.androidHard.BaseDataRegister;
import client.halouhuandian.app15.hardWareConncetion.androidHard.BaseDataReturnListener;
import client.halouhuandian.app15.hardWareConncetion.androidHard.CanDataFormat;
import client.halouhuandian.app15.hardWareConncetion.androidHard.SerialAndCanPortUtilsGeRui;
import client.halouhuandian.app15.hardWareConncetion.daa.DaaController;
import client.halouhuandian.app15.hardWareConncetion.daa.DaaSend;
import client.halouhuandian.app15.hardWareConncetion.daa.mode.DaaDataFormat;
import client.halouhuandian.app15.pub.BaseDataDistribution;
import client.halouhuandian.app15.pub.util.UtilPublic;
import client.halouhuandian.app15.service.logic.logicUpdate.UpdateInfoFormat;

public class AcdcUpdateIntegration extends BaseDataDistribution {

    //单例
    private static volatile AcdcUpdateIntegration acdcUpdateIntegration;
    private AcdcUpdateIntegration(){}
    public static AcdcUpdateIntegration getInstance(){
        if(acdcUpdateIntegration == null) {
            synchronized (AcdcUpdateIntegration.class){
                if(acdcUpdateIntegration == null){
                    acdcUpdateIntegration = new AcdcUpdateIntegration();
                }
            }
        }
        return acdcUpdateIntegration;
    }
    //连接帧 相应地址
    private String state_1 = "";
    //请求信息帧
    private String state_2 = "";
    //请求信息帧
    private String state_3 = "";
    //升级数据
    private UpdateInfoFormat updateInfoFormat;

    //数据注册
    private BaseDataRegister baseDataRegister;

    public void init(UpdateInfoFormat updateInfoFormat){
        this.updateInfoFormat = updateInfoFormat;
        onStart();
    }

    private void onStart(){

        int door = updateInfoFormat.getDoor();

        DaaDataFormat daaDataFormat = DaaController.getInstance().getDaaDataFormat();
        String tarVersionStr = daaDataFormat.getAcdcInfoByStateFormat(door - 1).getAcdcHardWareVersion();
        if(!tarVersionStr.equals("") && isNumeric(tarVersionStr)){
            int tarVersion = Integer.parseInt(tarVersionStr);
            if(tarVersion >= 50){
                state_1 = DaaSend.creatDcUpdateOrder("9CA" , 1 , 0xE0 , door + 80 , 0);
                state_2 = DaaSend.creatDcUpdateOrder("9C8" , 1 , 0xE0 , door + 80 , 0);
                state_3 = DaaSend.creatDcUpdateOrder("9C9" , 1 , 0xE0 , door + 80 , 0);
            }else{
                state_1 = DaaSend.creatDcUpdateOrder("9CA" , 1 , 0xE0 , updateInfoFormat.getDoor() , 0);
                state_2 = DaaSend.creatDcUpdateOrder("9C8" , 1 , 0xE0 , updateInfoFormat.getDoor() , 0);
                state_3 = DaaSend.creatDcUpdateOrder("9C9" , 1 , 0xE0 , updateInfoFormat.getDoor() , 0);
            }
        }else{
            state_1 = DaaSend.creatDcUpdateOrder("9CA" , 1 , 0xE0 , door + 80 , 0);
            state_2 = DaaSend.creatDcUpdateOrder("9C8" , 1 , 0xE0 , door + 80 , 0);
            state_3 = DaaSend.creatDcUpdateOrder("9C9" , 1 , 0xE0 , door + 80 , 0);
        }
        System.out.println("update - 返回接收类型 - " + tarVersionStr);
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

        if(canDataFormat.getAddressByStr().substring(0,2).equals("9c")){
            System.out.println("update - 接收 - address - " + canDataFormat.getAddressByStr() + " - data - "+ UtilPublic.ByteArrToHex(canData));
        }

        if(addressLong == Long.parseLong(state_1 , 16)){
            sendData(new AcdcUpdateDataFormat(addressLong , canData , AcdcUpdateDataFormat.AcdcUpdateType.liveConnection));
        }else if(addressLong == Long.parseLong(state_2 , 16)){
            sendData(new AcdcUpdateDataFormat(addressLong , canData , AcdcUpdateDataFormat.AcdcUpdateType.requireHandData));
        }else if(addressLong == Long.parseLong(state_3 , 16)){
            sendData(new AcdcUpdateDataFormat(addressLong , canData , AcdcUpdateDataFormat.AcdcUpdateType.requireBodyData));
        }
    }

    public static boolean isNumeric(String str){
        for (int i = str.length();--i>=0;){
            if (!Character.isDigit(str.charAt(i))){
                return false;
            }
        }
        return true;
    }

}
