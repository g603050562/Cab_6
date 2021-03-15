package client.halouhuandian.app15.devicesController.fire;

import android.support.annotation.IntDef;
import android.support.v4.util.Consumer;

import com.hellohuandian.pubfunction.Unit.LogUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Author:      Lee Yeung
 * Create Date: 2020/3/30
 * Description:
 */
public final class FireControlSwitcher {
    private byte PS = 0x67;
    private byte SA = 0x65;
    private byte PF = (byte) 0xC0;
    private byte LEN = 0x08;

    private final byte[] _FIRE_CONTROL_10 = {SA, PS, PF, (byte) 0x98,
            LEN,//数据长度
            0x00, 0x00, 0x00,//远程帧，错误帧，过载帧
            0x10,
            (byte) 0xC0, 0x05, 0x00, 0x0A, 0x00, 0x09, 0x00};//15

    private final byte[] _FIRE_CONTROL_20 = {SA, PS, PF, (byte) 0x98,
            0x04,//数据长度
            0x00, 0x00, 0x00,//远程帧，错误帧，过载帧
            0x20,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

    private Consumer<byte[]> canDataConsumer;

    @IntDef({
            SWITCH._01, SWITCH._02, SWITCH._03, SWITCH._04, SWITCH._05,
            SWITCH._06, SWITCH._07, SWITCH._08, SWITCH._09, SWITCH._10})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SWITCH {
        int _01 = 1;
        int _02 = 1 << 1;
        int _03 = 1 << 2;
        int _04 = 1 << 3;
        int _05 = 1 << 4;
        int _06 = 1 << 5;
        int _07 = 1 << 6;
        int _08 = 1 << 7;
        int _09 = 1 << 8;
        int _10 = 1 << 9;
    }

    private short switchControlCmd = 0;

    final byte close = (byte) 0xAA;
    final byte[] closeAcdc = new byte[]{0x65, 0x00, 0x10, (byte) 0x98,
            0x01,
            0x00, 0x00, 0x00,
            close, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

    void setCmdData(int... switchCmds) {
        boolean isClose = false;
        if (switchCmds != null && switchCmds.length > 0) {
            for (int cmd : switchCmds) {
                if (cmd > 0 && cmd <= SWITCH._10) {
                    isClose = true;
                    switchControlCmd |= cmd;
                } else {
                    switchControlCmd &= cmd;
                }
            }

            if (isClose) {
                for (int addr = 0x51; addr <= 0x52; addr++) {
                    closeAcdc[1] = (byte) addr;
                    canDataConsumer.accept(closeAcdc);
                }
            }

            LogUtil.I("消防状态：" + Integer.toBinaryString(switchControlCmd) + isClose);

            _FIRE_CONTROL_10[15] = (byte) ((switchControlCmd >> 8) & 0xFF);
            _FIRE_CONTROL_20[9] = (byte) (switchControlCmd & 0xFF);
            byte[] dataBytes = {_FIRE_CONTROL_10[9], _FIRE_CONTROL_10[10], _FIRE_CONTROL_10[11], _FIRE_CONTROL_10[12]
                    , _FIRE_CONTROL_10[13], _FIRE_CONTROL_10[14], _FIRE_CONTROL_10[15], _FIRE_CONTROL_20[9]};

            if (canDataConsumer != null) {

                canDataConsumer.accept(_FIRE_CONTROL_10);

                final short crc = crc16(dataBytes, 0, 8);
                _FIRE_CONTROL_20[10] = (byte) (crc & 0xFF);
                _FIRE_CONTROL_20[11] = (byte) (crc >> 8 & 0xFF);
                canDataConsumer.accept(_FIRE_CONTROL_20);
            }
        }
    }

    public void setCanDataConsumer(Consumer<byte[]> canDataConsumer) {
        this.canDataConsumer = canDataConsumer;
    }

    /**
     * CRC-16/MODBUS
     *
     * @param data
     * @param offset
     * @param len
     * @return
     */
    private final short crc16(byte[] data, int offset, int len) {
        int crc = 0xFFFF;
        int j;
        for (int i = offset; i < len; i++) {
            crc = ((crc & 0xFF00) | (crc & 0x00FF) ^ (data[i] & 0xFF));
            for (j = 0; j < 8; j++, crc = ((crc & 0x0001) > 0) ? (crc >> 1) ^ 0xA001 : (crc >> 1)) ;
        }

        return (short) (crc & 0xFFFF);
    }
}
