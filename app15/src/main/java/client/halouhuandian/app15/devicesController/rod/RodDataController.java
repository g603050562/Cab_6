package client.halouhuandian.app15.devicesController.rod;

import client.halouhuandian.app15.devicesController.CanSender;

/**
 * Author:      Lee Yeung
 * Create Date: 2020/3/23
 * Description: 推杆直接控制器
 */
public final class RodDataController {
    private static final RodDataController ROD_DATA_CONTROLLER = new RodDataController();

    private final byte PF = 0x03;
    private byte PS = -1;
    private final byte SA = 0x65;//SA：Android的地址固定

    private final byte stop = 0x00;
    private final byte close = 0x02;
    private final byte open = 0x01;

    private byte controlDuration = (byte) 150;

    // TODO: 2019-08-23 长度必须是16协议规则
    private final byte[] DATA = new byte[]{SA, PS, PF, (byte) 0x98,
            0x02,//数据长度
            0x00, 0x00, 0x00,//远程帧，错误帧，过载帧
            0x00, controlDuration, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

    private RodDataController() {
    }

    public static RodDataController getInstance() {
        return ROD_DATA_CONTROLLER;
    }

    public void setControlDuration(byte controlDuration) {
        this.controlDuration = controlDuration;
    }

    public void stop(int doorNumber) {
        DATA[1] = (byte) doorNumber;
        DATA[8] = stop;
        DATA[9] = (byte) (controlDuration & 0xFF);
        CanSender.getInstance().send(DATA);
    }

    public void closeDoor(int doorNumber) {
        closeDoor(doorNumber, controlDuration);
    }

    public void closeDoor(int doorNumber, byte closeDuration) {
        DATA[1] = (byte) doorNumber;
        DATA[8] = close;
        DATA[9] = (byte) (closeDuration & 0xFF);
        CanSender.getInstance().send(DATA);
    }

    public void openDoor(int doorNumber) {
        openDoor(doorNumber, controlDuration);
    }

    public void openDoor(int doorNumber, byte openDuration) {
        DATA[1] = (byte) doorNumber;
        DATA[8] = open;
        DATA[9] = (byte) (openDuration & 0xFF);
        CanSender.getInstance().send(DATA);
    }
}
