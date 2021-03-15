package client.halouhuandian.app15.devicesController.switcher;

import android.support.v4.util.Consumer;

import client.halouhuandian.app15.devicesController.CanSender;

/**
 * Author:      Lee Yeung
 * Create Date: 2020/3/30
 * Description: 开关控制器
 */
public final class DeviceSwitchController implements Consumer<byte[]> {
    private static final DeviceSwitchController SWITCH_CONTROLLER = new DeviceSwitchController();
    private final DeviceSwitcher deviceSwitcher = new DeviceSwitcher();
    private final AirFanSpeedSwitcher airFanSpeedSwitcher = new AirFanSpeedSwitcher();

    private DeviceSwitchController() {
        deviceSwitcher.setCanDataConsumer(this);
        airFanSpeedSwitcher.setCanDataConsumer(this);
    }

    public static DeviceSwitchController getInstance() {
        return SWITCH_CONTROLLER;
    }

    /**
     * 出发控制开关
     *
     * @param cmd
     */
    public void control(@DeviceSwitcher.CMD int cmd) {
        deviceSwitcher.setCmdData(cmd);
    }

    /**
     * 控制风扇转速
     *
     * @param speedCmd
     */
    public void controlAirFanSpeed(@AirFanSpeedSwitcher.SPEED int speedCmd) {
        airFanSpeedSwitcher.setCmdData(speedCmd);
    }

    public void controlAirFan(@DeviceSwitcher.AIR_FAN int cmd) {
        deviceSwitcher.setCmd(cmd);
    }


    @Override
    public void accept(byte[] bytes) {
        CanSender.getInstance().send(bytes);
    }
}
