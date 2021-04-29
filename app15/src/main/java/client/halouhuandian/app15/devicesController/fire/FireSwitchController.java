package client.halouhuandian.app15.devicesController.fire;

import android.support.v4.util.Consumer;

import client.halouhuandian.app15.devicesController.CanSender;

/**
 * Author:      Lee Yeung
 * Create Date: 2020/3/30
 * Description: 开关控制器
 */
public final class FireSwitchController implements Consumer<byte[]> {
    private static final FireSwitchController FIRE_SWITCH_CONTROLLER = new FireSwitchController();
    private final FireControlSwitcher fireControlSwitcher = new FireControlSwitcher();

    private FireSwitchController() {
        fireControlSwitcher.setCanDataConsumer(this);
    }

    public static FireSwitchController getInstance() {
        return FIRE_SWITCH_CONTROLLER;
    }

    public void control(int... switchCmds) {
//        fireControlSwitcher.setCmdData(switchCmds);
    }

    @Override
    public void accept(byte[] bytes) {
        CanSender.getInstance().send(bytes);
    }
}
