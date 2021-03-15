package client.halouhuandian.app15.devicesController.switcher;

import android.support.annotation.IntDef;
import android.support.v4.util.Consumer;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;

/**
 * Author:      Lee Yeung
 * Create Date: 2020/3/30
 * Description: 风扇转速开关
 */
public final class AirFanSpeedSwitcher {
    private byte PS = 0x66;
    private byte SA = 0x65;
    private byte PF = (byte) 0xB0;
    private byte LEN = 0x08;

    //风扇转速等级
    private final byte[] _AIR_FAN_SPEED_LEVEL = {SA, PS, PF, (byte) 0x98,
            LEN,//数据长度
            0x00, 0x00, 0x00,//远程帧，错误帧，过载帧
            (byte) 0xB0, 0x05, 0x00, 0x0A, 0x00, 0x06, 0x00, 0x00};
    private final byte[] _AIR_FAN_SPEED_LEVEL_0 = {SA, PS, PF, (byte) 0x98,
            LEN,//数据长度
            0x00, 0x00, 0x00,//远程帧，错误帧，过载帧
            (byte) 0xB0, 0x05, 0x00, 0x0A, 0x00, 0x06, 0x00, 0x00};

    private Consumer<byte[]> canDataConsumer;

    @IntDef({
            SPEED._1, SPEED._2, SPEED._3, SPEED._4, SPEED._5,
            SPEED._6, SPEED._7, SPEED._8, SPEED._9, SPEED._10})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SPEED {
        int _1 = 1;
        int _2 = 2;
        int _3 = 3;
        int _4 = 4;
        int _5 = 5;
        int _6 = 6;
        int _7 = 7;
        int _8 = 8;
        int _9 = 9;
        int _10 = 10;
    }

    private byte[] obtainSpeedData(@SPEED int speedCmd) {
        final int speed = speedCmd * 100;

        _AIR_FAN_SPEED_LEVEL_0[14] = (byte) ((speed >> 8) & 0xFF);
        _AIR_FAN_SPEED_LEVEL_0[15] = (byte) (speed & 0xFF);
//        final short crc = crc16(_AIR_FAN_SPEED_LEVEL, 8, 14);
//        _AIR_FAN_SPEED_LEVEL[_AIR_FAN_SPEED_LEVEL.length - 2] = (byte) (crc & 0xFF);
//        _AIR_FAN_SPEED_LEVEL[_AIR_FAN_SPEED_LEVEL.length - 1] = (byte) (crc >> 8 & 0xFF);
        System.arraycopy(_AIR_FAN_SPEED_LEVEL_0, 0, _AIR_FAN_SPEED_LEVEL, 0, 16);
        return _AIR_FAN_SPEED_LEVEL;
    }

    void setCmdData(@DeviceSwitcher.CMD int cmd) {
        byte[] dataBytes = obtainSpeedData(cmd);
        if (dataBytes != null && canDataConsumer != null) {
            final short crc = crc16(dataBytes, 8, 16);

            final byte _2FrameData = dataBytes[15];
            for (int i = 15; i > 8; i--) {
                dataBytes[i] = dataBytes[i - 1];
            }
            dataBytes[8] = 0x10;
            canDataConsumer.accept(dataBytes);

            dataBytes[4] = 4;
            dataBytes[8] = 0x20;
            dataBytes[9] = _2FrameData;
            dataBytes[10] = (byte) (crc & 0xFF);
            dataBytes[11] = (byte) (crc >> 8 & 0xFF);

            Arrays.fill(dataBytes, 12, 16, (byte) 0x00);
            canDataConsumer.accept(dataBytes);
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
