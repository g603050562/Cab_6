package client.halouhuandian.app15.devicesController.dcdcStatusController;

import android.support.annotation.IntRange;

import client.halouhuandian.app15.devicesController.CanSender;

/**
 * Author:      Lee Yeung
 * Create Date: 2021/1/28
 * Description:
 */
public class DcdcStatusController {
    private static final DcdcStatusController DCDC_STATUS_CONTROLLER = new DcdcStatusController();
    private final byte open = (byte) 0x55;
    private final byte close = (byte) 0xAA;

    private DcdcStatusController() {
    }

    public static DcdcStatusController getInstance() {
        return DCDC_STATUS_CONTROLLER;
    }

    public void turnOfDcdc(@IntRange(from = 1, to = 9) int sn) {
        final byte[] closeDcdc = new byte[]{0x65, 0x00, 0x00, (byte) 0x98,
                0x08,
                0x00, 0x00, 0x00,
                open, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        closeDcdc[1] = (byte) sn;
        CanSender.getInstance().send(closeDcdc);
    }

    public void turnOffDcdc(@IntRange(from = 1, to = 9) int sn) {

        final byte[] openDcdc = new byte[]{0x65, 0x00, 0x00, (byte) 0x98,
                0x08,
                0x00, 0x00, 0x00,
                close, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        openDcdc[1] = (byte) sn;
        CanSender.getInstance().send(openDcdc);
    }
}
