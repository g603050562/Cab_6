package client.halouhuandian.app15.hardWareConncetion.environmentPlate.fireControl;

import java.util.Arrays;

import client.halouhuandian.app15.config.SystemConfig;
import client.halouhuandian.app15.hardWareConncetion.androidHard.SerialAndCanPortUtilsGeRui;
import client.halouhuandian.app15.pub.util.UtilPublic;

/**
 * 消防执行类
 */
public class FireControlSwitcher {

    private String envAddress = "98C06765";
    private String acdcAddress_1 = "98105165";
    private String acdcAddress_2 = "98105265";
    int[] openType = new int[]{0,0,0,0,0,0,0,0,0,0,0,0};

    public void sendOpenCmd(int door){
        if(door < 1 || door > SystemConfig.getMaxBattery())
            return;
        openType[door-1] = 1;
        openType[9] = 1;
        makeOrder();
        closeAcdc();
    }

    public void sendOpenAll(){
        Arrays.fill(openType,1);
        makeOrder();
    }

    public void sendCloseCmd(){
        Arrays.fill(openType,0);
        makeOrder();
        openAcdc();
    }

    public void sendCloseCmd(int door){
        if(door < 1 || door > SystemConfig.getMaxBattery())
            return;
        openType[door-1] = 0;
        openType[9] = 1;
        makeOrder();
        closeAcdc();
    }

    public void makeOrder(){
        int total = 0;
        int param = 1;
        for(int i : openType){
            total = total + i * param;
            param = param * 2;
        }
        int paramHigh = total / 256;
        int paramLow = total % 256;
        byte[] cmd = new byte[]{(byte) 0xC0, (byte)0x05, (byte)0x00, (byte)0x0A, (byte)0x00, (byte)0x09, (byte)paramHigh , (byte)paramLow};
        String crc = getCRC(cmd);
        String crcHigh = crc.substring(0,2);
        String crcLow = crc.substring(2,4);
        byte[] cmd_1 = new byte[]{(byte) 0x10,(byte) 0xC0, (byte)0x05, (byte)0x00, (byte)0x0A, (byte)0x00, (byte)0x09, (byte)paramHigh};
        byte[] cmd_2 = new byte[]{(byte) 0x20, (byte)paramLow, (byte) Integer.parseInt(crcLow, 16),(byte) Integer.parseInt(crcHigh, 16)};
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(envAddress,cmd_1);
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(envAddress,cmd_2);
        System.out.println("fire:"+ UtilPublic.ByteArrToHex(cmd_1));
        System.out.println("fire:"+ UtilPublic.ByteArrToHex(cmd_2));
    }

    public void closeAcdc(){
        byte[] cmd = new byte[]{(byte)0xAA,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(acdcAddress_1,cmd);
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(acdcAddress_2,cmd);
    }
    public void openAcdc(){
        byte[] cmd = new byte[]{(byte)0x55,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(acdcAddress_1,cmd);
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(acdcAddress_2,cmd);
    }

    //CRC验证
    public static String getCRC(byte[] bytes) {
        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;
        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        return Integer.toHexString(CRC);
    }
}
