package client.halouhuandian.app15.hardWareConncetion.environmentPlate.electricityMeter;

import client.halouhuandian.app15.hardWareConncetion.androidHard.SerialAndCanPortUtilsGeRui;

/**
 * 电表操作
 */

public class ElectricityMeterController {

    private static volatile ElectricityMeterController electricityMeterController;
    private ElectricityMeterController(){}
    public static ElectricityMeterController getInstance(){
        if(electricityMeterController == null){
            synchronized (ElectricityMeterController.class){
                if(electricityMeterController == null){
                    electricityMeterController = new ElectricityMeterController();
                }
            }
        }
        return electricityMeterController;
    }

    public void rebootAndroid(){
        String ADDRESS = "98B06665";
        byte[] cmd_1 = new byte[]{(byte)0x10,(byte) 0xB0,(byte)0x05,(byte)0x00,(byte)0x0A,(byte)0x00,(byte)0x01,(byte)0x00};
        byte[] cmd_2 = new byte[]{(byte)0x20,(byte) 0x01,(byte)0x16,(byte)0x7E};
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(ADDRESS,cmd_1);
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(ADDRESS,cmd_2);
    }

    public void cleanMeter(){
        String ADDRESS = "98B06665";
        byte[] cmd_1 = new byte[]{(byte)0x10,(byte) 0xB0,(byte)0x05,(byte)0x00,(byte)0x0A,(byte)0x00,(byte)0x05,(byte)0x00};
        byte[] cmd_2 = new byte[]{(byte)0x20,(byte) 0x00,(byte)0x96,(byte)0x7F};
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(ADDRESS,cmd_1);
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(ADDRESS,cmd_2);
    }

}
