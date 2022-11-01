package client.halouhuandian.app15.service.logic.logicUpdate.battery;

import java.util.ArrayList;
import java.util.Arrays;

import client.halouhuandian.app15.hardWareConncetion.androidHard.BaseDataRegister;
import client.halouhuandian.app15.hardWareConncetion.androidHard.BaseDataReturnListener;
import client.halouhuandian.app15.hardWareConncetion.androidHard.CanDataFormat;
import client.halouhuandian.app15.hardWareConncetion.androidHard.SerialAndCanPortUtilsGeRui;
import client.halouhuandian.app15.pub.BaseDataDistribution;
import client.halouhuandian.app15.pub.util.UtilPublic;

public class BatteryUpdateIntegration extends BaseDataDistribution {

    //单例
    private static volatile BatteryUpdateIntegration batteryUpdateIntegration;
    private BatteryUpdateIntegration() { }
    public static BatteryUpdateIntegration getInstance() {
        if (batteryUpdateIntegration == null) {
            synchronized (BatteryUpdateIntegration.class) {
                if (batteryUpdateIntegration == null) {
                    batteryUpdateIntegration = new BatteryUpdateIntegration();
                }
            }
        }
        return batteryUpdateIntegration;
    }

    //电池升级地址 A0
    private final long DEFINE_BATTERY_UPDATE_A0 = 2560648448l; // 98A06500
    //电池升级地址 A1
    private final long DEFINE_BATTERY_UPDATE_A1 = 2560713984l; // 98A16500
    //电池升级地址 A2
    private final long DEFINE_BATTERY_UPDATE_A2 = 2560779520l; // 98A26500
    //电池升级地址 A3
    private final long DEFINE_BATTERY_UPDATE_A3 = 2560845056l; // 98A36500

    //电池升级地址 A0
    private long BATTERY_UPDATE_A0 = 0l; // 98A06500
    //电池升级地址 A1
    private long BATTERY_UPDATE_A1 = 0l; // 98A16500
    //电池升级地址 A2
    private long BATTERY_UPDATE_A2 = 0l; // 98A26500
    //电池升级地址 A3
    private long BATTERY_UPDATE_A3 = 0l; // 98A36500

    //电池信息组装数据
    private byte[] dateArrays = new byte[1];

    //数据注册
    private BaseDataRegister baseDataRegister;

    public void init(int door) {
        BATTERY_UPDATE_A0 = DEFINE_BATTERY_UPDATE_A0 + door;
        BATTERY_UPDATE_A1 = DEFINE_BATTERY_UPDATE_A1 + door;
        BATTERY_UPDATE_A2 = DEFINE_BATTERY_UPDATE_A2 + door;
        BATTERY_UPDATE_A3 = DEFINE_BATTERY_UPDATE_A3 + door;
        onStart();
    }

    private void onStart() {
        if(baseDataRegister == null){

            ArrayList<long[]> rangeList = new ArrayList<>();
            rangeList.add(new long[]{BATTERY_UPDATE_A0 ,BATTERY_UPDATE_A0});
            rangeList.add(new long[]{BATTERY_UPDATE_A1 ,BATTERY_UPDATE_A1});
            rangeList.add(new long[]{BATTERY_UPDATE_A2 ,BATTERY_UPDATE_A2});
            rangeList.add(new long[]{BATTERY_UPDATE_A3 ,BATTERY_UPDATE_A3});
            baseDataRegister = new BaseDataRegister(rangeList, new BaseDataReturnListener() {
                @Override
                public void returnData(CanDataFormat canDataFormat) {
                    onCanResultApp(canDataFormat);
                }
            });
            SerialAndCanPortUtilsGeRui.getInstance().addListener(baseDataRegister);

        }
    }

    public void onDestroy() {
        SerialAndCanPortUtilsGeRui.getInstance().deleteListener(baseDataRegister);
        baseDataRegister = null;
    }

    public void onCanResultApp(CanDataFormat canDataFormat) {
        //can地址
        long addressLong = canDataFormat.getAddressByLong();
        //can数据
        byte[] canData = canDataFormat.getData();

        if(addressLong == BATTERY_UPDATE_A0 || addressLong == BATTERY_UPDATE_A1 || addressLong == BATTERY_UPDATE_A2 || addressLong == BATTERY_UPDATE_A3){
            System.out.println("batteryUpdate - receive - address - " + canDataFormat.getAddressByStr() + " - data - " + canDataFormat.getDataByStr());
        }

        if (addressLong == BATTERY_UPDATE_A0) {
            int dataLength = canData[2] + canData[3] * 256;
            dateArrays = new byte[dataLength];
            Arrays.fill(dateArrays, (byte) 0x00);
        } else if (addressLong == BATTERY_UPDATE_A1) {
            if (dateArrays.length == 1) {
                return;
            }
            int currentPage = canData[0];
            int length = dateArrays.length;
            int totalPage = length / 7;
            int remainder = length % 7;
            if(remainder != 0){
                totalPage = totalPage + 1;
            }
            if(currentPage == totalPage){
                if(remainder == 0){
                    for (int i = 0; i < 7; i++) {
                        dateArrays[(currentPage - 1) * 7 + i] = canData[1 + i];
                    }
                }else{
                    for (int i = 0; i < remainder; i++) {
                        dateArrays[(currentPage - 1) * 7 + i] = canData[1 + i];
                    }
                }
            }else {
                if(currentPage < totalPage){
                    for (int i = 0; i < 7; i++) {
                        dateArrays[(currentPage - 1) * 7 + i] = canData[1 + i];
                    }
                }
            }
        } else if (addressLong == BATTERY_UPDATE_A2) {
            sendData(dateArrays);
//            System.out.println("batteryUpdate - receive - finalData - " + UtilPublic.ByteArrToHex(dateArrays));
            //有时候 dc返回来的数据 少一帧 而且奇葩的是 我要是不处理的话 还能跑。。。。牛逼
            //估计是给我传回来的数据丢帧 我得自动补齐 。。。
            //下面这个 内存都不能初始化 最好就是少一帧的情况直接用上次的返回值 。。。
//            dateArrays = new byte[0];
        }
    }

    public void sendData(int door, byte[] data) {

//        System.out.println("batteryUpdate - send - rawData - " + UtilPublic.ByteArrToHex(data));

        String addressA0 = "98A00" + door + "65";
        String addressA1 = "98A10" + door + "65";
        String addressA2 = "98A20" + door + "65";

        int length = data.length;
        int length_L = length % 256;
        int length_H = length / 256;

        //发送A0
        byte[] dataA0 = new byte[]{(byte) 0xAA, (byte) 0x55, (byte) length_L, (byte) length_H};
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(addressA0, dataA0);

        System.out.println("batteryUpdate - send - handData - address - " + addressA0 + " - data - " + UtilPublic.ByteArrToHex(dataA0));

        //发送A1
        int index = length / 7;
        int tem = length % 7;
        if (tem != 0) {
            index = index + 1;
        }
        for (int i = 0; i < index; i++) {
            byte[] dataA1 = null;
            if (i == index - 1) {
                if(tem == 0){
                    dataA1 = new byte[]{(byte) (i + 1), data[(i * 7) + 0], data[(i * 7) + 1], data[(i * 7) + 2], data[(i * 7) + 3], data[(i * 7) + 4], data[(i * 7) + 5], data[(i * 7) + 6]};
                }else{
                    dataA1 = new byte[tem + 1];
                    Arrays.fill(dataA1, (byte) 0x00);
                    dataA1[0] = (byte) (i + 1);
                    for (int j = 0; j < tem; j++) {
                        dataA1[j + 1] = data[(i * 7) + j];
                    }
                }
            } else {
                dataA1 = new byte[]{(byte) (i + 1), data[(i * 7) + 0], data[(i * 7) + 1], data[(i * 7) + 2], data[(i * 7) + 3], data[(i * 7) + 4], data[(i * 7) + 5], data[(i * 7) + 6]};
            }
            SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(addressA1, dataA1);
            System.out.println("batteryUpdate - send - bodyData - address - " + addressA1 + " - data - " + UtilPublic.ByteArrToHex(dataA1));
        }

        //发送A2
        byte[] dataA2 = new byte[]{(byte) 0x55, (byte) 0xAA, (byte) length_L, (byte) length_H};
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(addressA2, dataA2);
        System.out.println("batteryUpdate - send - footData - address - " + addressA2 + " - data - " + UtilPublic.ByteArrToHex(dataA2));

    }

    public void sendStart(int door) {
        String addressA3 = "98A30" + door + "65";
        byte[] dataA3 = new byte[]{(byte) 0x55};
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(addressA3, dataA3);
//        System.out.println("batteryUpdate - send - startUpdate - address - " + addressA3 + " - data - " + UtilPublic.ByteArrToHex(dataA3));
    }

    public void sendStop(int door) {
        String addressA3 = "98A30" + door + "65";
        byte[] dataA3 = new byte[]{(byte) 0xAA};
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(addressA3, dataA3);
//        System.out.println("batteryUpdate - send - endUpdate - address - " + addressA3 + " - data - " + UtilPublic.ByteArrToHex(dataA3));
    }



}
