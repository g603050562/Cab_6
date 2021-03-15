package com.apps.ExtensionTaskUpgrade.categories.battery;

import com.apps.ExtensionTaskUpgrade.categories.UpgradeProgram;

/**
 * Author:      Lee Yeung
 * Create Date: 2020-02-19
 * Description:
 */
public class BatteryUpgradeProgram extends UpgradeProgram
{
    public final class UpgradeStatus
    {
        public static final byte WAITTING_485 = 2;//等待485重置
        public static final byte BATTERY_INFO = 3;//电池信息
        public static final byte BOOT_LOADER_MODE = 4;//进入BootLoader模式
        public static final byte INIT_FIRMWARE_DATA = 5;//初始化固件数据
        public static final byte WRITE_DATA = 6;//写入升级数据
        public static final byte ACTION_BMS = 7;//立即激活新BMS程序
        public static final byte FAILED = 8;//升级失败
        public static final byte SUCCESSED = 9;//升级成功
    }

    public interface OnBatteryUpgradeCallBack
    {
        void onUpgrading(byte address, byte statusFlag, String statusInfo, long currentPregress, long totalPregress);

        void onError(byte address, byte statusFlag, String statusInfo);
    }

    private OnBatteryUpgradeCallBack onBatteryUpgradeCallBack;

    protected byte address;
    protected final BatteryAttribute batteryAttribute;

    public BatteryUpgradeProgram(BatteryAttribute batteryAttribute)
    {
        this.batteryAttribute = batteryAttribute;
        if (batteryAttribute != null)
        {
            address = batteryAttribute.address;
        }
    }

    public void setOnBatteryUpgradeCallBack(OnBatteryUpgradeCallBack onBatteryUpgradeCallBack)
    {
        this.onBatteryUpgradeCallBack = onBatteryUpgradeCallBack;
    }

    protected void onUpgrading(byte address, byte statusFlag, String statusInfo, long currentPregress, long totalPregress)
    {
        if (onBatteryUpgradeCallBack != null)
        {
            if (statusFlag == UpgradeStatus.FAILED)
            {
                onBatteryUpgradeCallBack.onError(address, statusFlag, statusInfo);
            } else
            {
                onBatteryUpgradeCallBack.onUpgrading(address, statusFlag, statusInfo, currentPregress, totalPregress);
            }
        }
    }
}
