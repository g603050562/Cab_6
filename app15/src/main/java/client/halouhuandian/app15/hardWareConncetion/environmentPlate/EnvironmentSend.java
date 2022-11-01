package client.halouhuandian.app15.hardWareConncetion.environmentPlate;

import client.halouhuandian.app15.hardWareConncetion.androidHard.SerialAndCanPortUtilsGeRui;
import client.halouhuandian.app15.pub.util.UtilPublic;

public class EnvironmentSend {

    private static String address = "98B06665";

    /**
     * 设置电流检测板参数
     *
     * @param currentThreshold   电流阈值设置：数据分辨率:0.01A，0A偏移量 若不设置，默认3.5A
     *                           例如设置 3.00A，则相应发送数 据为300，即 2C 01
     * @param currentLimitedTime 电流板允许电流超限时间：
     *                           数据分辨率:1ms，0ms偏移量 若不设置，默认800ms
     *                           例如设置 1000ms，则相应发送数 据为1000，即 E8 03
     * @param relayRecoveryTime  继电器断开后恢复时间：
     *                           数据分辨率:1ms，0ms偏移量 若不设置，默认1S
     *                           例如设置 1000ms，则相应发送数 据为1000，即 E8 03
     * @return true:设置的参数符合限制要求，并不代表设置的参数会立即生效。
     */
    public static void setCurrentPlateParam(float currentThreshold, int currentLimitedTime, int relayRecoveryTime) {
        byte[] cmd = new byte[]{(byte) 0xB0, (byte) 0x05, (byte) 0x00, (byte) 0x0C, (byte) 0x00, (byte) 0x0A, (byte) (currentThreshold * 100 / 256), (byte) (currentThreshold * 100 % 256), (byte) currentLimitedTime, (byte) relayRecoveryTime};
        String crc = getCRC(cmd);
        String crcHigh = crc.substring(0, 2);
        String crcLow = crc.substring(2, 4);
        byte[] data_1 = new byte[]{(byte) 0x10, (byte) 0xB0, (byte) 0x05, (byte) 0x00, (byte) 0x0C, (byte) 0x00, (byte) 0x0A, (byte) (currentThreshold * 100 / 256)};
        byte[] data_2 = new byte[]{(byte) 0x20, (byte) (currentThreshold * 100 % 256), (byte) currentLimitedTime, (byte) relayRecoveryTime, (byte) Integer.parseInt(crcLow, 16), (byte) Integer.parseInt(crcHigh, 16)};

        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(address, data_1);
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(address, data_2);
    }

    //电流板重启
    public static void reboot() {
        byte[] data_1 = new byte[]{(byte) 0x10, (byte) 0xB0, (byte) 0x05, (byte) 0x00, (byte) 0x0A, (byte) 0x00, (byte) 0x07, (byte) 0x00};
        byte[] data_2 = new byte[]{(byte) 0x20, (byte) 0x01, (byte) 0xF6, (byte) 0x7F};

        System.out.println("update - address -  发送 - "+address+" - data - "+ UtilPublic.ByteArrToHex(data_1));
        System.out.println("update - address -  发送 - "+address+" - data - "+ UtilPublic.ByteArrToHex(data_2));
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(address, data_1);
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(address, data_2);
    }

    //电流板重启
    public static void rebootByBoot() {
        byte[] data_1 = new byte[]{(byte) 0x10, (byte) 0xB0, (byte) 0x05, (byte) 0x00, (byte) 0x0C, (byte) 0x00, (byte) 0x07, (byte) 0x00};
        byte[] data_2 = new byte[]{(byte) 0x20, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x31, (byte) 0x80};
        byte[] bytes = new byte[]{(byte) 0xB0, (byte) 0x05, (byte) 0x00, (byte) 0x0C, (byte) 0x00, (byte) 0x07, (byte) 0x00, (byte) 0x00,(byte) 0x00,(byte) 0x00};
        System.out.println("update - address - 发送 - "+address+" - data - "+ UtilPublic.ByteArrToHex(data_1));
        System.out.println("update - address - 发送 - "+address+" - data - "+ UtilPublic.ByteArrToHex(data_2));
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(address, data_1);
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(address, data_2);
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
