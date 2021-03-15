package com.apps.ExtensionTaskUpgrade.categories.dc;

import com.apps.ExtensionTaskUpgrade.categories.UpgradeProgram;

/**
 * Author:      Lee Yeung
 * Create Date: 2019-12-20
 * Description:
 */
public abstract class DC_UpgradeProgram extends UpgradeProgram
{
    protected final byte androidAddress = (byte) 0xE0;//android上位机地址
    protected byte address;

    public final class DCUpgradeStatus
    {
        public static final byte SET_UPGRADE_MODE = 1;//设置升级模式
        public static final byte REBOOT_UPGRADE_MODE = 2;//重启升级模式
        public static final byte CONNECTING = 3;//请求连接帧
        public static final byte CONNECTED = 4;//请求连接帧成功
        public static final byte UPGRADING = 5;//升级
        public static final byte FAILED = 8;//升级失败
        public static final byte SUCCESSED = 9;//升级成功

        public static final byte SHUTDOWN = 10;//升级成功
        public static final byte START = 11;//升级成功
    }

    public interface OnDCUpgradeCallBack
    {
        void onUpgrading(byte address, byte statusFlag, String statusInfo, long currentPregress, long totalPregress);

        void onError(byte address, byte statusFlag, String statusInfo);
    }

    private OnDCUpgradeCallBack onDCUpgradeCallBack;

    public DC_UpgradeProgram(byte address)
    {
        this.address = address;
    }

    protected String upgradeFilePath;

    public void setUpgradeFilePath(String upgradeFilePath)
    {
        this.upgradeFilePath = upgradeFilePath;
    }

    public void setOnDCUpgradeCallBack(OnDCUpgradeCallBack onDCUpgradeCallBack)
    {
        this.onDCUpgradeCallBack = onDCUpgradeCallBack;
    }

    protected void onUpgrading(byte address, byte statusFlag, String statusInfo, long currentPregress, long totalPregress)
    {
        if (onDCUpgradeCallBack != null)
        {
            if (statusFlag == DCUpgradeStatus.FAILED)
            {
                onDCUpgradeCallBack.onError(address, statusFlag, statusInfo);
            } else
            {
                onDCUpgradeCallBack.onUpgrading(address, statusFlag, statusInfo, currentPregress, totalPregress);
            }
        }
    }
}
