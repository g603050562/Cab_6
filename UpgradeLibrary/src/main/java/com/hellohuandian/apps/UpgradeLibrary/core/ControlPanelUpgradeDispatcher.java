package com.hellohuandian.apps.UpgradeLibrary.core;

import com.hellohuandian.apps.UpgradeLibrary.executers.battery.BatteryExecuter;
import com.hellohuandian.apps.UpgradeLibrary.executers.controlpanel.ControlPanelExecuter;
import com.hellohuandian.apps.UpgradeLibrary.messages.UpgradeMessage;
import com.hellohuandian.apps.UpgradeLibrary.messages.battery.BatteryUpgradeMessage;
import com.hellohuandian.apps.UpgradeLibrary.messages.controlPanel.ControlPanelUpgradeMessage;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Author:      Lee Yeung
 * Create Date: 2019-09-07
 * Description:
 */
public final class ControlPanelUpgradeDispatcher extends ConcurrentLinkedQueue<UpgradeMessage> {

    private static final ControlPanelUpgradeDispatcher UPGRADE_DISPATCHER = new ControlPanelUpgradeDispatcher();

    private final ControlPanelExecuter controlPanelExecuter = new ControlPanelExecuter();

    public static final ControlPanelUpgradeDispatcher getInstance() {
        return UPGRADE_DISPATCHER;
    }

    public void init(SerialPortRwAction serialPortRwAction) {
        controlPanelExecuter.init(serialPortRwAction);
    }

    public void init_12(SerialPortRwAction serialPortRwAction) {
        controlPanelExecuter.init_12(serialPortRwAction);
    }

    public void dispatch(UpgradeMessage upgradeMessage) {
        if (upgradeMessage instanceof ControlPanelUpgradeMessage) {
            controlPanelExecuter.upgrade((ControlPanelUpgradeMessage) upgradeMessage);
        }
    }
}