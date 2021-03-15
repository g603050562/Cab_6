package com.hellohuandian.apps.UpgradeLibrary.core;

import com.hellohuandian.apps.UpgradeLibrary.executers.battery.BatteryExecuter;
import com.hellohuandian.apps.UpgradeLibrary.messages.UpgradeMessage;
import com.hellohuandian.apps.UpgradeLibrary.messages.battery.BatteryUpgradeMessage;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Author:      Lee Yeung
 * Create Date: 2019-09-07
 * Description:
 */
public final class BatteryUpgradeDispatcher extends ConcurrentLinkedQueue<UpgradeMessage> {


    private static final BatteryUpgradeDispatcher UPGRADE_DISPATCHER = new BatteryUpgradeDispatcher();

    private final BatteryExecuter batteryExecuter = new BatteryExecuter();

    public static final BatteryUpgradeDispatcher getInstance() {
        return UPGRADE_DISPATCHER;
    }

    public void init(SerialPortRwAction serialPortRwAction) {
        batteryExecuter.init(serialPortRwAction);
    }

    public void dispatch(UpgradeMessage upgradeMessage) {
        if (upgradeMessage instanceof BatteryUpgradeMessage) {
            batteryExecuter.upgrade((BatteryUpgradeMessage) upgradeMessage);
        }
    }
}