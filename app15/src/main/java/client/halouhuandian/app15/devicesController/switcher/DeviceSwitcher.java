package client.halouhuandian.app15.devicesController.switcher;

import android.support.annotation.IntDef;
import android.support.v4.util.Consumer;

import com.hellohuandian.pubfunction.Unit.LogUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;

/**
 * Author:      Lee Yeung
 * Create Date: 2020/3/30
 * Description: 控制命令类
 */
public final class DeviceSwitcher {
    private byte PS = 0x66;
    private byte SA = 0x65;
    private byte PF = (byte) 0xB0;
    private byte LEN = 0x08;

    //控制Android12V继电 器开
    private final byte[] _ANDROID_12V_OPEN = {SA, PS, PF, (byte) 0x98,
            LEN,//数据长度
            0x00, 0x00, 0x00,//远程帧，错误帧，过载帧
            (byte) 0xB0, 0x05, 0x00, 0x0A, 0x00, 0x01, 0x00, 0x00};
    //控制Android12V继电器重启
    private final byte[] _ANDROID_12V_REBOOT = {SA, PS, PF, (byte) 0x98,
            LEN,//数据长度
            0x00, 0x00, 0x00,//远程帧，错误帧，过载帧
            (byte) 0xB0, 0x05, 0x00, 0x0A, 0x00, 0x01, 0x00, 0x01};
    //控制风扇12V继电器开
    private final byte[] _AIRFAN_12V_OPEN = {SA, PS, PF, (byte) 0x98,
            LEN,//数据长度
            0x00, 0x00, 0x00,//远程帧，错误帧，过载帧
            (byte) 0xB0, 0x05, 0x00, 0x0A, 0x00, 0x02, 0x00, 0x00};
    //控制风扇12V继电器关
    private final byte[] _AIRFAN_12V_CLOSE = {SA, PS, PF, (byte) 0x98,
            LEN,//数据长度
            0x00, 0x00, 0x00,//远程帧，错误帧，过载帧
            (byte) 0xB0, 0x05, 0x00, 0x0A, 0x00, 0x02, 0x00, 0x01};

    //备用12V继电器1开
    private final byte[] _12V_1_OPEN = {SA, PS, PF, (byte) 0x98,
            LEN,//数据长度
            0x00, 0x00, 0x00,//远程帧，错误帧，过载帧
            (byte) 0xB0, 0x05, 0x00, 0x0A, 0x00, 0x03, 0x00, 0x00};
    //备用12V继电器1关
    private final byte[] _12V_1_CLOSE = {SA, PS, PF, (byte) 0x98,
            LEN,//数据长度
            0x00, 0x00, 0x00,//远程帧，错误帧，过载帧
            (byte) 0xB0, 0x05, 0x00, 0x0A, 0x00, 0x03, 0x00, 0x01};
    //备用12V继电器2开
    private final byte[] _12V_2_OPEN = {SA, PS, PF, (byte) 0x98,
            LEN,//数据长度
            0x00, 0x00, 0x00,//远程帧，错误帧，过载帧
            (byte) 0xB0, 0x05, 0x00, 0x0A, 0x00, 0x04, 0x00, 0x00};
    //备用12V继电器2关
    private final byte[] _12V_2_CLOSE = {SA, PS, PF, (byte) 0x98,
            LEN,//数据长度
            0x00, 0x00, 0x00,//远程帧，错误帧，过载帧
            (byte) 0xB0, 0x05, 0x00, 0x0A, 0x00, 0x04, 0x00, 0x01};

    //清除电表数据
    private final byte[] _CLEAR_AMMETER_DATA = {SA, PS, PF, (byte) 0x98,
            LEN,//数据长度
            0x00, 0x00, 0x00,//远程帧，错误帧，过载帧
            (byte) 0xB0, 0x05, 0x00, 0x0A, 0x00, 0x05, 0x00, 0x00};

    private final byte[] tempBytes = new byte[16];

    @IntDef({
            CMD.ANDROID_12V_OPEN, CMD.ANDROID_12V_REBOOT,
            CMD.AIRFAN_12V_OPEN, CMD.AIRFAN_12V_CLOSE,
            CMD.V12_1_OPEN, CMD.V12_1_CLOSE,
            CMD.V12_2_OPEN, CMD.V12_2_CLOSE,
            CMD.CLEAR_AMMETER_DATA})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CMD {
        int ANDROID_12V_OPEN = 1;
        int ANDROID_12V_REBOOT = 2;
        int AIRFAN_12V_OPEN = 3;
        int AIRFAN_12V_CLOSE = 4;
        int V12_1_OPEN = 5;
        int V12_1_CLOSE = 6;
        int V12_2_OPEN = 7;
        int V12_2_CLOSE = 8;
        int CLEAR_AMMETER_DATA = 9;
    }

    private Consumer<byte[]> canDataConsumer;

    private byte[] obtainCmdData(@CMD int cmd) {
        switch (cmd) {
            case CMD.ANDROID_12V_OPEN:
                return _ANDROID_12V_OPEN;
            case CMD.ANDROID_12V_REBOOT:
                return _ANDROID_12V_REBOOT;
            case CMD.AIRFAN_12V_OPEN:
                return _AIRFAN_12V_OPEN;
            case CMD.AIRFAN_12V_CLOSE:
                return _AIRFAN_12V_CLOSE;
            case CMD.V12_1_OPEN:
                return _12V_1_OPEN;
            case CMD.V12_1_CLOSE:
                return _12V_1_CLOSE;
            case CMD.V12_2_OPEN:
                return _12V_2_OPEN;
            case CMD.V12_2_CLOSE:
                return _12V_2_CLOSE;
            case CMD.CLEAR_AMMETER_DATA:
                return _CLEAR_AMMETER_DATA;
        }
        return null;
    }

    void setCmdData(@CMD int cmd) {
        byte[] dataBytes = obtainCmdData(cmd);
        if (dataBytes != null && canDataConsumer != null) {
            System.arraycopy(dataBytes, 0, tempBytes, 0, 16);
            final short crc = crc16(tempBytes, 8, 16);

            final byte _2FrameData = tempBytes[15];
            for (int i = 15; i > 8; i--) {
                tempBytes[i] = tempBytes[i - 1];
            }
            tempBytes[8] = 0x10;
            canDataConsumer.accept(tempBytes);

            tempBytes[4] = 4;
            tempBytes[8] = 0x20;
            tempBytes[9] = _2FrameData;
            tempBytes[10] = (byte) (crc & 0xFF);
            tempBytes[11] = (byte) (crc >> 8 & 0xFF);
            Arrays.fill(tempBytes, 12, 16, (byte) 0x00);
            canDataConsumer.accept(tempBytes);
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

    @IntDef({
            AIR_FAN._1_OPEN, AIR_FAN._1_CLOSE,
            AIR_FAN._2_OPEN, AIR_FAN._2_CLOSE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AIR_FAN {
        int _1_OPEN = 1;
        int _1_CLOSE = 2;
        int _2_OPEN = 3;
        int _2_CLOSE = 4;
    }

    private final byte[] _AIR_FAN_DATA_10 = {SA, PS, PF, (byte) 0x98,
            LEN,//数据长度
            0x00, 0x00, 0x00,//远程帧，错误帧，过载帧
            0x10, (byte) 0xB0, 0x05, 0x00, 0x0A, 0x00, 0x00, 0x00};

    private final byte[] _AIR_FAN_DATA_20 = {SA, PS, PF, (byte) 0x98,
            4,//数据长度
            0x00, 0x00, 0x00,//远程帧，错误帧，过载帧
            0x20, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

    void setCmd(@AIR_FAN int cmd) {
        switch (cmd) {
            case AIR_FAN._1_OPEN:
                _AIR_FAN_DATA_10[14] = 0x02;
                canDataConsumer.accept(_AIR_FAN_DATA_10);
                _AIR_FAN_DATA_20[9] = 0x00;
                _AIR_FAN_DATA_20[10] = 0x27;
                _AIR_FAN_DATA_20[11] = (byte) 0xBE;
                canDataConsumer.accept(_AIR_FAN_DATA_20);
                break;
            case AIR_FAN._1_CLOSE:
                _AIR_FAN_DATA_10[14] = 0x02;
                canDataConsumer.accept(_AIR_FAN_DATA_10);
                _AIR_FAN_DATA_20[9] = 0x01;
                _AIR_FAN_DATA_20[10] = (byte) 0xE6;
                _AIR_FAN_DATA_20[11] = (byte) 0x7E;
                canDataConsumer.accept(_AIR_FAN_DATA_20);
                break;
            case AIR_FAN._2_OPEN:
                _AIR_FAN_DATA_10[14] = 0x06;
                canDataConsumer.accept(_AIR_FAN_DATA_10);
                _AIR_FAN_DATA_20[9] = 0x00;
                _AIR_FAN_DATA_20[10] = 0x66;
                _AIR_FAN_DATA_20[11] = (byte) 0x7F;
                canDataConsumer.accept(_AIR_FAN_DATA_20);
                break;
            case AIR_FAN._2_CLOSE:
                _AIR_FAN_DATA_10[14] = 0x06;
                canDataConsumer.accept(_AIR_FAN_DATA_10);
                _AIR_FAN_DATA_20[9] = 0x01;
                _AIR_FAN_DATA_20[10] = (byte) 0xA7;
                _AIR_FAN_DATA_20[11] = (byte) 0xBF;
                canDataConsumer.accept(_AIR_FAN_DATA_20);
                break;
        }
    }
}
