package client.halouhuandian.app15.hardWareConncetion.daa;

import client.halouhuandian.app15.config.SystemConfig;
import client.halouhuandian.app15.hardWareConncetion.androidHard.SerialAndCanPortUtilsGeRui;
import client.halouhuandian.app15.pub.util.UtilPublic;

public class DaaSend {

    //dcdc下发推杆收回
    public static void putterPull(int door, int pushrodActSetTime) {
        byte[] a = new byte[]{0x02, (byte) pushrodActSetTime};
        String hexDoor = Integer.toHexString(door);
        String b = "98030" + hexDoor + "65";
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(b, a);
    }

    //dcdc下发推杆伸出
    public static void putterPush(int door, int pushrodActSetTime) {
        byte[] a = new byte[]{0x01, (byte) pushrodActSetTime};
        String hexDoor = Integer.toHexString(door);
        String b = "98030" + hexDoor + "65";
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(b, a);
    }

    //dcdc下发写入UID
    public static void writeUid(int door, String uid) {
        String hexDoor = Integer.toHexString(door);
        String a = "98050" + hexDoor + "65";
        byte[] data = new byte[8];
        for (int i = 0; i < 8; i++) {
            data[i] = (byte) uid.charAt(i);
        }
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(a, data);
    }

    //dcdc下发关机
    public static void closeDcdc(int door) {
        String hexDoor = Integer.toHexString(door);
        String a = "98000" + hexDoor + "65";
        byte[] data = new byte[]{(byte) 0xAA, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(a, data);
    }

    //dcdc下发开机
    public static void openDcdc(int door) {
        String hexDoor = Integer.toHexString(door);
        String a = "98000" + hexDoor + "65";
        byte[] data = new byte[]{(byte) 0x55, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(a, data);
    }

    //dcdc下发所有dc挂起
    public static void hangOnAll() {
        String a = "9813FF65";
        byte[] data = new byte[]{0x02, 0x02};
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(a, data);
        System.out.println("update - 发送 - 挂起所有dc - " + a + " - data - " + UtilPublic.ByteArrToHex(data));
    }

    //dcdc下发除了目标dc全部挂起
    public static void hangOnAllExceptIndex(int door) {
        for (int i = 0; i < SystemConfig.getMaxBattery(); i++) {
            if (door != i + 1) {
                int temDoor = i + 1;
                String hexDoor = Integer.toHexString(temDoor);
                String a = "98130" + hexDoor + "65";
                byte[] data = new byte[]{0x02, 0x02};
                SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(a, data);
                System.out.println("update - 发送 - 挂起目标"+(i + 1)+"挂起 - " + a + " - data - " + UtilPublic.ByteArrToHex(data));
            }
        }
    }


    //dcdc下发所有dc挂起
    public static void cancelHangOnAll() {
        String a = "9813FF65";
        byte[] data = new byte[]{0x02, 0x01};
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(a, data);
    }

    //dcdc下发 禁用dcdc
    public static void forbiddenDcdc(int door) {
        String hexDoor = Integer.toHexString(door);
        String a = "98140" + hexDoor + "65";
        byte[] b = new byte[]{0x01, 0x02, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00};
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(a, b);
    }

    //dcdc下发 启用dcdc
    public static void recoveryDcdc(int door) {
        String hexDoor = Integer.toHexString(door);
        String a = "98140" + hexDoor + "65";
        byte[] b = new byte[]{0x01, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(a, b);
    }

    //dcdc下发 电池过压恢复
    public static void overVoltageReset(int door) {
        String hexDoor = Integer.toHexString(door);
        String a = "98140" + hexDoor + "65";
        byte[] b = new byte[]{0x01, 0x03, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00};
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(a, b);
    }

    //dcdc下发 dcdc默认加热参数
    public static void setHeatingDefault() {
        for (int i = 0; i < SystemConfig.getMaxBattery(); i++) {
            String hexDoor = Integer.toHexString(i+1);
            String a = "98140" + hexDoor + "65";
            byte[] b = new byte[]{0x01, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
            SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(a, b);
        }
    }

    //dcdc下发 dcdc自动加热
    public static void setHeatingAuto() {
        for (int i = 0; i < SystemConfig.getMaxBattery(); i++) {
            String hexDoor = Integer.toHexString(i+1);
            String a = "98140" + hexDoor + "65";
            byte[] b = new byte[]{0x01, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
            SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(a, b);
        }
    }

    //dcdc下发 dcdc关闭自动加热
    public static void setHeatingClose() {
        for (int i = 0; i < SystemConfig.getMaxBattery(); i++) {
            String hexDoor = Integer.toHexString(i+1);
            String a = "98140" + hexDoor + "65";
            byte[] b = new byte[]{0x01, 0x04, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00};
            SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(a, b);
        }
    }


    //dcdc下发转跳到bootLoader
    public static void turnToBootLoader(int door) {
        String hex = Integer.toHexString(door);
        if (hex.length() == 1) {
            hex = "0" + hex;
        }
        String a = "9813" + hex + "65";
        byte[] data = new byte[]{0x01, 0x01};
        System.out.println("update - 发送 - android - " + a + " - data - " + UtilPublic.ByteArrToHex(data));
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(a, data);
    }

    //dcdc升级时上位机地址
    final static private int upAddress = 0xE0;

    //dcdc下发升级连接帧
    public static void lifeConnection(int door) {
        String a = creatDcUpdateOrder("9CA", 1, door, upAddress, 0);
        System.out.println("update - 发送 - android - " + a + " - data - ");
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(a, null);
    }

    //dcdc下发确认升级连接帧
    public static void confirmLifeConnection(int door, byte[] data) {
        String a = creatDcUpdateOrder("9CA", 1, door, upAddress, 0);
        data[0] = (byte) 0x80;
        data[1] = (byte) 0xf0;
        System.out.println("update - 发送 - android - " + a + " - data - " + UtilPublic.ByteArrToHex(data));
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(a, data);
    }

    //dcdc下发文件数据
    public static void sendUpdateData(int top, int door, byte[] data) {
        String a = creatDcUpdateOrder(Integer.toHexString(top), 1, door, 0xE0, 0);
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(a, data);
        System.out.println("update - 发送 - android - " + a + " - data - " + UtilPublic.ByteArrToHex(data));
    }

    /**
     * 生成升级地址数据
     *
     * @param type           协议号 9CA - 上位机升级连接帧    9C8 - 下位机请求首帧信息真    9C9 - 下位机请求非首帧信息帧    9D0~9EF - 上位机下发bin文件内容
     * @param ptp            点对点 - 1    群发 - 0
     * @param receiveAddress 接收设备地址
     * @param sendAddress    发送设备地址
     * @param cnt            数据包含下一帧 - 1    不包含 - 0
     */
    public static String creatDcUpdateOrder(String type, int ptp, int receiveAddress, int sendAddress, int cnt) {

        String binaryStr = "";
        binaryStr = binaryStr + UtilPublic.hexString2binaryString(type);
        binaryStr = binaryStr + ptp + "";
        binaryStr = binaryStr + UtilPublic.hexString2binaryString(Integer.toHexString(receiveAddress));
        binaryStr = binaryStr + UtilPublic.hexString2binaryString(Integer.toHexString(sendAddress));
        binaryStr = binaryStr + cnt + "";
        binaryStr = binaryStr + "1";
        binaryStr = binaryStr + "1";
        String hexStr = Long.toString(Long.parseLong(binaryStr, 2), 16);
        return hexStr;
    }
}
