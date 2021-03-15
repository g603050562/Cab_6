package com.hellohuandian.apps.UpgradeLibrary.messages.controlPanel;


import com.hellohuandian.apps.UpgradeLibrary.messages.UpgradeMessage;

/**
 * Author:      Guo
 * Create Date: 2019-09-07
 * Description:
 */
public class ControlPanelUpgradeMessage extends UpgradeMessage {

    private String filePath;

    private UpgradeCallBack upgradeCallBack;

    public ControlPanelUpgradeMessage(byte address) {
        super(address);
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public UpgradeCallBack getUpgradeCallBack() {
        return upgradeCallBack;
    }

    public void setUpgradeCallBack(UpgradeCallBack upgradeCallBack) {
        this.upgradeCallBack = upgradeCallBack;
    }

}
