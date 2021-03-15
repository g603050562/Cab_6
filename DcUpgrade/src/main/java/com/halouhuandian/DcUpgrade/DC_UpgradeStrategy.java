package com.halouhuandian.DcUpgrade;

import com.halouhuandian.DcUpgrade.callback.DC_StatusCallBack;
import com.halouhuandian.DcUpgrade.canExtension.CanDeviceIoAction;

/**
 * Author:      Lee Yeung
 * Create Date: 2019-12-20
 * Description:
 */
public abstract class DC_UpgradeStrategy {
    protected byte address;
    protected final byte androidAddress = (byte) 0xE0;//android上位机地址

    private DC_StatusCallBack dc_statusCallBack;
    private String upgradeFilePath;

    public DC_UpgradeStrategy(byte address, String upgradeFilePath) {
        this.address = address;
        this.upgradeFilePath = upgradeFilePath;
    }

    void setDc_statusCallBack(DC_StatusCallBack dc_statusCallBack) {
        this.dc_statusCallBack = dc_statusCallBack;
    }

    protected final void onStatusCall(byte address, byte status, String info, long process, long total) {
        if (dc_statusCallBack != null) {
            dc_statusCallBack.onStatusCall(address, status, info, process, total);
        }
    }

    public String getUpgradeFilePath() {
        return upgradeFilePath;
    }

    protected final void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public abstract void execute_can(CanDeviceIoAction deviceIoAction, byte[] bytes);
}
