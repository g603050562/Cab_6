package com.hellohuandian.apps.UpgradeLibrary.messages.battery;

import com.hellohuandian.apps.UpgradeLibrary.messages.UpgradeMessage;

/**
 * Author:      Lee Yeung
 * Create Date: 2019-09-07
 * Description:
 */
public class BatteryUpgradeMessage extends UpgradeMessage {
    private String filePath;
    private String manufacturer;

    private UpgradeCallBack upgradeCallBack;

    public BatteryUpgradeMessage(byte address) {
        super(address);
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public UpgradeCallBack getUpgradeCallBack() {
        return upgradeCallBack;
    }

    public void setUpgradeCallBack(UpgradeCallBack upgradeCallBack) {
        this.upgradeCallBack = upgradeCallBack;
    }
}
