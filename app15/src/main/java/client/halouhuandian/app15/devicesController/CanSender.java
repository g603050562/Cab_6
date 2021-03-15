package client.halouhuandian.app15.devicesController;

import client.halouhuandian.app15.MyApplication;
import client.halouhuandian.app15.serial_port.SerialAndCanPortUtils;

/**
 * Author:      Lee Yeung
 * Create Date: 2020/3/30
 * Description:
 */
public class CanSender {
    private final static CanSender CAN_SENDER = new CanSender();
    private final static SerialAndCanPortUtils SERIAL_AND_CAN_PORT_UTILS = MyApplication.serialAndCanPortUtils;

    private CanSender() {
    }

    public static CanSender getInstance() {
        return CAN_SENDER;
    }

    public void send(byte[] canBytes) {
        if (SERIAL_AND_CAN_PORT_UTILS != null && canBytes != null) {
            SERIAL_AND_CAN_PORT_UTILS.canSendOrder(canBytes);
        }
    }
}
