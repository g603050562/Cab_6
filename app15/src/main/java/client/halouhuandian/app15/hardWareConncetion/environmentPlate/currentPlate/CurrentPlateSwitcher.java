package client.halouhuandian.app15.hardWareConncetion.environmentPlate.currentPlate;

import client.halouhuandian.app15.hardWareConncetion.androidHard.SerialAndCanPortUtilsGeRui;

public class CurrentPlateSwitcher {

    private String ADDREDD = "98C06865";


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
    public void setCurrentPlateParam(float currentThreshold, int currentLimitedTime, int relayRecoveryTime){
        final boolean isValRight = currentThreshold >= 1.5f && currentLimitedTime >= 100 && relayRecoveryTime >= 100;
        if(!isValRight){
            return;
        }
        byte[] data = new byte[]{0,0,0,0,0,0,0,0};
        data[0] = (byte) 0x00;
        data[1] = (byte) (currentThreshold * 100 / 256);
        data[2] = (byte) (currentThreshold * 100 % 256);
        data[3] = (byte) (currentLimitedTime / 256);
        data[4] = (byte) (currentLimitedTime % 256);
        data[5] = (byte) (relayRecoveryTime  / 256);
        data[6] = (byte) (relayRecoveryTime  % 256);
        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(ADDREDD,data);
    }
}
