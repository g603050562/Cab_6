package client.halouhuandian.app15.hardWareConncetion.environmentPlate.fan;

import client.halouhuandian.app15.hardWareConncetion.androidHard.SerialAndCanPortUtilsGeRui;

/**
 * 风扇控制 下发帧
 */

public class FanSwitcher {

    private String ADDRESS = "98B06665";

    public void openFan_1(){
        byte[] cmd_1 = new byte[]{(byte)0x10,(byte) 0xB0,(byte)0x05,(byte)0x00,(byte)0x0A,(byte)0x00,(byte)0x02,(byte)0x00};
        byte[] cmd_2 = new byte[]{(byte)0x20,(byte) 0x00,(byte)0x27,(byte)0xBE};
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(ADDRESS,cmd_1);
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(ADDRESS,cmd_2);
        System.out.println("fan - 1 - open");
    }

    public void closeFan_1(){
        byte[] cmd_1 = new byte[]{(byte)0x10,(byte) 0xB0,(byte)0x05,(byte)0x00,(byte)0x0A,(byte)0x00,(byte)0x02,(byte)0x00};
        byte[] cmd_2 = new byte[]{(byte)0x20,(byte) 0x01,(byte)0xE6,(byte)0x7E};
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(ADDRESS,cmd_1);
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(ADDRESS,cmd_2);
        System.out.println("fan - 1 - close");
    }

    public void openFan_2(){
        byte[] cmd_1 = new byte[]{(byte)0x10,(byte) 0xB0,(byte)0x05,(byte)0x00,(byte)0x0A,(byte)0x00,(byte)0x06,(byte)0x00};
        byte[] cmd_2 = new byte[]{(byte)0x20,(byte) 0x00,(byte)0x66,(byte)0x7F};
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(ADDRESS,cmd_1);
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(ADDRESS,cmd_2);
        System.out.println("fan - 2 - open");
    }

    public void closeFan_2(){
        byte[] cmd_1 = new byte[]{(byte)0x10,(byte) 0xB0,(byte)0x05,(byte)0x00,(byte)0x0A,(byte)0x00,(byte)0x06,(byte)0x00};
        byte[] cmd_2 = new byte[]{(byte)0x20,(byte) 0x01,(byte)0xA7,(byte)0xBF};
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(ADDRESS,cmd_1);
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(ADDRESS,cmd_2);
        System.out.println("fan - 2 - close");
    }
}
