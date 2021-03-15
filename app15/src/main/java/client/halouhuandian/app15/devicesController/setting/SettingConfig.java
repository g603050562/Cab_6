package client.halouhuandian.app15.devicesController.setting;

import com.hellohuandian.pubfunction.Unit.LogUtil;

import java.util.Arrays;
import java.util.Calendar;

import client.halouhuandian.app15.MyApplication;
import client.halouhuandian.app15.StringFormatHelper;
import client.halouhuandian.app15.devicesController.CanSender;

/**
 * Author:      Lee Yeung
 * Create Date: 2020/7/9
 * Description:
 */
public final class SettingConfig implements MyApplication.IFResultAppLinstener {
    private static final SettingConfig SETTING_CONFIG = new SettingConfig();

    private byte PS = 0x00;
    private byte SA = 0x65;
    private byte PF = 0x14;
    private byte LEN = 0x08;

    private final byte[] _SETTING = {SA, PS, PF, (byte) 0x98,
            LEN,//数据长度
            0x00, 0x00, 0x00,//远程帧，错误帧，过载帧
            (byte) 0x00, 0x00, 0x00, 0x0A, 0x00, 0x00, 0x00, 0x00};

    private SettingConfig() {
        MyApplication.getInstance().addListener(this);
    }

    public static SettingConfig getInstance() {
        return SETTING_CONFIG;
    }

    public void setDefaultHeating() {
        setHeating(65, 6, -30);
    }

    public void autoHeating()
    {
        //        10,11,12,1,2启动加热其他停止
        switch (getMonth()) {
            case 10:
            case 11:
            case 12:
            case 1:
            case 2:
            case 3:
                SettingConfig.getInstance().setDefaultHeating();
                break;
            default:
                SettingConfig.getInstance().setHeatingUnable();
                break;
        }
    }

    public static int getMonth() {
        Calendar cd = Calendar.getInstance();
        return cd.get(Calendar.MONTH) + 1;
    }
    /**
     * @param hatchDoorTemperature         电池仓终止温度
     * @param batteryInnerEndTemperature   电芯内部加热终止温度
     * @param batteryInnerStartTemperature 电芯内部加热启动温度
     */
    public void setHeating(float hatchDoorTemperature, float batteryInnerEndTemperature, float batteryInnerStartTemperature) {
        setting();
        _SETTING[9] = 0x04;
        _SETTING[10] = 0x00;
        _SETTING[11] = 0x00;

//        int int_hatchDoorTemperature = (int) (hatchDoorTemperature * 10 + 50 * 10);
//        LogUtil.I("电池仓终止温度:" + hatchDoorTemperature);
//        int int_batteryInnerEndTemperature = (int) (batteryInnerEndTemperature * 10 + 50f * 10);
//        LogUtil.I("电芯内部加热终止温度:" + batteryInnerEndTemperature);
//        int int_batteryInnerStartTemperature = (int) (batteryInnerStartTemperature * 10 + 50f * 10);
//        LogUtil.I("电芯内部加热启动温度:" + batteryInnerStartTemperature);
//
//        _SETTING[10] = (byte) (int_hatchDoorTemperature & 0xFF);
//        _SETTING[11] = (byte) ((int_hatchDoorTemperature >> 8) & 0xFF);
//
//        _SETTING[12] = (byte) (int_batteryInnerEndTemperature & 0xFF);
//        _SETTING[13] = (byte) ((int_batteryInnerEndTemperature >> 8) & 0xFF);
//
//        _SETTING[14] = (byte) (int_batteryInnerStartTemperature & 0xFF);
//        _SETTING[15] = (byte) ((int_batteryInnerStartTemperature >> 8) & 0xFF);

        LogUtil.I("设置加热：" + StringFormatHelper.getInstance().toHexString(_SETTING));
        send9Address();
    }

    /**
     * 停用加热
     */
    public void setHeatingUnable() {
        setting();
        _SETTING[9] = 0x04;
        _SETTING[10] = 0x01;
        LogUtil.I("停用加热：" + StringFormatHelper.getInstance().toHexString(_SETTING));
        send9Address();
    }


    public void readHeating() {
        reading();
        _SETTING[9] = 0x01;
        Arrays.fill(_SETTING, 10, _SETTING.length, (byte) 0x00);
        send9Address();
//        _SETTING[1] = 1;
//        CanSender.getInstance().send(_SETTING);
    }

    /**
     * 设置DC禁用状态
     *
     * @param usedEnable 0x00:不禁止 0x01:禁止
     */
    public void setForbiddenUseEnable(int doorNumber, byte usedEnable) {
        setting();
        _SETTING[9] = 0x02;
        _SETTING[10] = usedEnable;
        Arrays.fill(_SETTING, 11, _SETTING.length, (byte) 0x00);

        _SETTING[1] = (byte) doorNumber;
        LogUtil.I("禁用完毕:" + StringFormatHelper.getInstance().toHexString(_SETTING));
        CanSender.getInstance().send(_SETTING);
    }

    public void readForbiddenUseEnable() {
        reading();
        _SETTING[9] = 0x02;
        Arrays.fill(_SETTING, 10, _SETTING.length, (byte) 0x00);
        CanSender.getInstance().send(_SETTING);
    }

    /**
     * 设置复位命令
     */
    public void setOvervoltageReset(int doorNumber) {
        setting();
        _SETTING[9] = 0x03;
        _SETTING[10] = 0x01;
        Arrays.fill(_SETTING, 11, _SETTING.length, (byte) 0x00);
        _SETTING[1] = (byte) doorNumber;
        CanSender.getInstance().send(_SETTING);
        LogUtil.I("设置复位" + doorNumber + "号DCDC");
    }

    public void readOvervoltageReset() {
        reading();
        _SETTING[9] = 0x03;
        _SETTING[10] = 0x01;
        Arrays.fill(_SETTING, 11, _SETTING.length, (byte) 0x00);
        CanSender.getInstance().send(_SETTING);
    }

    private void setting() {
        _SETTING[8] = 0x01;
    }

    private void reading() {
        _SETTING[8] = 0x02;
    }

    private void send9Address() {
        for (int address = 1, len = 9; address <= len; address++) {
            _SETTING[1] = (byte) address;
            LogUtil.I("读取加热：" + StringFormatHelper.getInstance().toHexString(_SETTING));
            CanSender.getInstance().send(_SETTING);
        }
    }

    @Override
    public void onCanResultApp(byte[] canData) {
        if (canData != null && canData.length == 16) {
            if ((canData[3] & 0xFF) == 0x98 && (canData[2] & 0xFF) == PF && (canData[1] & 0xFF) == 0x65 && ((canData[0] & 0xFF) >= 0x01 && (canData[0] & 0xFF) <= 0x09)) {

                if ((canData[8] & 0xFF) == 0x01) {
                    LogUtil.I("回复信息：" + StringFormatHelper.getInstance().toHexString(canData));
                } else
                    //判断是否0x02(读取指令回复)
                    if ((canData[8] & 0xFF) == 0x02) {
                        switch ((canData[9] & 0xFF)) {
                            case 1:
                                LogUtil.I("温度信息：" + StringFormatHelper.getInstance().toHexString(canData));
                                parseTemperature(canData);
                                break;
                            case 2:
                                LogUtil.I("禁用信息：" + StringFormatHelper.getInstance().toHexString(canData));
                                parseForbiddenUseEnable(canData);
                                break;
                            case 3:
                                LogUtil.I("复位信息：" + StringFormatHelper.getInstance().toHexString(canData));
                                parseReset(canData);
                                break;
                        }
                    }
            }
        }
    }

    private void parseTemperature(byte[] canData) {
        float hatchDoorTemperature = ((canData[10] & 0xFF | ((canData[11] & 0xFF) << 8)) - 500) / 10;
        float batteryInnerEndTemperature = ((canData[12] & 0xFF | ((canData[13] & 0xFF) << 8)) - 500) / 10;
        float batteryInnerStartTemperature = ((canData[14] & 0xFF | ((canData[15] & 0xFF) << 8)) - 500) / 10;

        LogUtil.I("解析电池仓终止温度:" + hatchDoorTemperature);
        LogUtil.I("解析电芯内部加热终止温度:" + batteryInnerEndTemperature);
        LogUtil.I("解析电芯内部加热启动温度:" + batteryInnerStartTemperature);
    }

    private void parseForbiddenUseEnable(byte[] canData) {
        byte forbiddenUseEnable = (byte) (canData[10] & 0xFF);
        LogUtil.I("禁用状态:" + forbiddenUseEnable);
    }

    private void parseReset(byte[] canData) {
        byte resetStatus = (byte) (canData[10] & 0xFF);
        LogUtil.I("复位状态:" + resetStatus);
    }


    @Override
    public void onSerialResultApp(byte[] serData) {

    }
}
