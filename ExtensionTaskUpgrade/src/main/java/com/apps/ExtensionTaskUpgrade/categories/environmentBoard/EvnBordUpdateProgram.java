package com.apps.ExtensionTaskUpgrade.categories.environmentBoard;

import android.support.v4.util.Consumer;
import android.text.TextUtils;

import com.apps.ExtensionTaskUpgrade.categories.UpgradeProgram;
import com.apps.ExtensionTaskUpgrade.core.ioAction.CanDeviceIoAction;
import com.apps.ExtensionTaskUpgrade.help.StringFormatHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Author:      Lee Yeung
 * Create Date: 2020/4/23
 * Description: 环境板升级
 * ========================================
 * <p>
 * Xmodem、Ymodem和Zmodem协议是最常用的三种通信协议。
 * <p>
 * Xmodem协议是最早的，传输128字节信息块。
 * <p>
 * Ymodem是Xmodem的改进版协议，具有传输快速稳定的优点。它可以一次传输1024字节的信息块，同时还支持传输多个文件。
 * <p>
 * 平常所说的Ymodem协议是指的Ymodem-1K，除此还有Ymodem-g（没有CRC校验，不常用）。
 * <p>
 * YModem-1K用1024字节信息块传输取代标准的128字节传输，数据的发送回使用CRC校验，保证数据传输的正确性。它每传输一个信息块数据时，就会等待接收端回应ACK信号，接收到回应后，才会继续传输下一个信息块，保证数据已经全部接收。
 * ————————————————
 * 版权声明：本文为CSDN博主「LcmKing」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。
 * 原文链接：https://blog.csdn.net/lcmsir/java/article/details/80550821
 */
public class EvnBordUpdateProgram extends UpgradeProgram {
    //其中SOH=0x01，表示这个数据帧中包含着128个字节的数据（STX表示1024字节，初始帧只有128个）
    private final byte SOH = 0x01;
    private final byte STX = 0x02;//数据帧头标记
    private final byte SOH_END = 0x04;
    private final byte frameSn_D = 0x00;//00表示数据帧序号，初始是0，依次向下排
    private final byte frameSn_R = (byte) 0xFF;//FF是帧序号的取反
    private final byte end = 0x00;//最后一定要在文件名后加上00，表示文件名的结束

    final byte[] frameData = new byte[]{0x65, 0x66, (byte) 0xB0, (byte) 0x98,
            0x08,
            0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

    private final Object lock = new Object();
    private volatile boolean isOutTime;
    private volatile boolean isOK;

    private long fileSizeLen;
    private boolean isReboot;
    private EvnCallBack evnCallBack;

    private String filePath;

    public EvnBordUpdateProgram() {
    }

    public EvnBordUpdateProgram(boolean isReboot) {
        this.isReboot = isReboot;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setEvnCallBack(EvnCallBack evnCallBack) {
        this.evnCallBack = evnCallBack;
    }

    @Override
    protected void execute_can(final CanDeviceIoAction canDeviceIoAction) {

        if (TextUtils.isEmpty(filePath)) {
            update(0, 0, "升级路径为空", EvnUpdateStatus.FAILED);
            return;
        }
        final File file = new File(filePath);
        if (file != null && file.exists()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        update(file, canDeviceIoAction);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            update(0, 0, "升级路径不存在", EvnUpdateStatus.FAILED);

        }
    }

    private void update(File file, final CanDeviceIoAction canDeviceIoAction) throws Exception {
        if (isReboot) {
            update(0, 0, "重启环境板", EvnUpdateStatus.START);
            reboot(canDeviceIoAction);
            sleep(500);
        }
//        jumpAppProgram(canDeviceIoAction);
//        if(true)return;

        byte[] createStartData = createStartData(file);
        if (createStartData != null && createStartData.length > 0) {
            final long createDataIdH = ((0xB0 & 0xFF) << 40) | ((0x08 & 0xFF) << 32);
            final int createDataId = ((0x98 & 0xFF) << 24) | ((0xB0 & 0xFF) << 16) | ((0X65 & 0xFF) << 8) | 0x66;
//            canDeviceIoAction.registerTimeOut(createDataIdH | createDataId, SystemClock.elapsedRealtime() + 30 * 1000);
            canDeviceIoAction.register(createDataIdH | createDataId, new Consumer<byte[]>() {
                @Override
                public void accept(byte[] bytes) {
                    if (bytes == null) {
                        isOutTime = true;
                        synchronized (lock) {
                            lock.notify();
                        }
                    } else if ((bytes[9] & 0xFF) == 0xB0 && (bytes[10] & 0xFF) == 0x08) {
                        System.out.println("~~帧回复：" + StringFormatHelper.getInstance().toHexString(bytes));
                        isOK = (bytes[13] & 0xFF) == 0x06;
                        if (!isOK) {
                            canDeviceIoAction.unRegister(createDataIdH | createDataId);
                            update(-1, fileSizeLen, "升级失败", EvnUpdateStatus.FAILED);
                        }
                        synchronized (lock) {
                            lock.notify();
                        }
                    }
                }
            });
            convertData(frameData, createStartData, canDeviceIoAction);
            synchronized (lock) {
                lock.wait();
            }
            if (isOutTime || !isOK) {
                canDeviceIoAction.unRegister(createDataIdH | createDataId);
                System.out.println("~~升级失败");
                return;
            }
            System.out.println("~~开始帧发送成功");

            final InputStream inputStream = new FileInputStream(file);
            long available;
            while ((available = inputStream.available()) > 0) {
                System.out.println("~~剩余长度：" + inputStream.available());
                Thread.sleep(50);
                convertData(frameData, createTransferData(inputStream), canDeviceIoAction);
                System.out.println("~~数据帧：发完一帧");

                update(fileSizeLen - available, fileSizeLen, "正在升级..." + (fileSizeLen - available), EvnUpdateStatus.PROCESS);

                synchronized (lock) {
                    lock.wait();
                }
                if (isOutTime || !isOK) {
                    canDeviceIoAction.unRegister(createDataIdH | createDataId);
                    System.out.println("~~升级失败：" + isOutTime + "-" + isOK);
                    return;
                }
            }

            System.out.println("~~数据帧发送完成");

            Thread.sleep(50);

            convertData(frameData, createEndData(), canDeviceIoAction);
            synchronized (lock) {
                lock.wait();
            }
            if (isOutTime || !isOK) {
                canDeviceIoAction.unRegister(createDataIdH | createDataId);
                System.out.println("~~升级失败");
                return;
            }
            Thread.sleep(1000);
            System.out.println("~~结束帧发送成功");
            jumpAppProgram(canDeviceIoAction);
            update(fileSizeLen, fileSizeLen, "升级成功" + fileSizeLen, EvnUpdateStatus.SUCCESSED);
        }
    }

    private byte[] createStartData(File file) {
        //1. 起始帧的数据格式:SOH 00 FF filename filezise NUL CRCH CRCL

        final String fileName = file.getName();
        final char[] fileNameChars = fileName.toCharArray();

        fileSizeLen = file.length();
        update(0, fileSizeLen, "开始升级", EvnUpdateStatus.START);
        final char[] fileSizeLenChars = (fileSizeLen + "").toCharArray();

        byte[] startData = new byte[139];
        int offset = 0;
        // B0   07   00   8B 固定填充
        startData[offset] = (byte) 0xB0;
        startData[++offset] = 0x07;
        startData[++offset] = 0x00;
        startData[++offset] = (byte) 0x8B;

        startData[++offset] = SOH;
        startData[++offset] = frameSn_D;
        startData[++offset] = frameSn_R;
        final int crcStart = offset + 1;

        for (char c : fileNameChars) {
            startData[++offset] = Byte.parseByte((int) c + "");
        }
        startData[++offset] = end;

        for (char c : fileSizeLenChars) {
            startData[++offset] = Byte.parseByte((int) c + "");
        }
        startData[++offset] = end;

        final short crc = crc16(startData, crcStart, 135);
        startData[135] = (byte) (crc & 0xFF);
        startData[136] = (byte) (crc >> 8);

        final short crcCan = crc16(startData, 0, 137);
        startData[137] = (byte) (crcCan & 0xFF);
        startData[138] = (byte) (crcCan >> 8 & 0xFF);
//        System.out.println("~~开始帧：" + StringFormatHelper.getInstance().toHexString(startData));

        return startData;
    }

    private final byte[] dataBytes = new byte[1024];
    private final byte[] transferDataBytes = new byte[1 + 1 + 1 + 4 + dataBytes.length + 1 + 1 + 1 + 1];
    private int sn = 0x01;
    private int snR = (byte) 0xFE;

    private byte[] createTransferData(InputStream inputStream) throws IOException {
        int offset = 0;

        transferDataBytes[offset] = (byte) 0xB0;
        transferDataBytes[++offset] = 0x07;
        transferDataBytes[++offset] = 0x04;
        transferDataBytes[++offset] = 0x0B;

        transferDataBytes[++offset] = STX;
        sn = (sn == 0x00 ? 0x01 : sn);
        transferDataBytes[++offset] = (byte) sn++;
        snR = (snR == 0xFF ? 0xFE : snR);
        transferDataBytes[++offset] = (byte) snR--;

        if (inputStream.available() / 1024 > 0) {
            inputStream.read(dataBytes, 0, dataBytes.length);
        } else {
            final int available = inputStream.available();
            if (available % 1024 > 0) {
                full(dataBytes);
                inputStream.read(dataBytes, 0, available);
                //填充剩余不满的数据
//                Arrays.fill(transferDataBytes, available + 7, transferDataBytes.length, (byte) 0xFF);
            }
        }
        System.arraycopy(dataBytes, 0, transferDataBytes, 7, dataBytes.length);

        final short crc = crc16(dataBytes, 0, dataBytes.length);
        transferDataBytes[transferDataBytes.length - 4] = (byte) (crc & 0xFF);
        transferDataBytes[transferDataBytes.length - 3] = (byte) (crc >> 8);

        final short crcCan = crc16(transferDataBytes, 0, transferDataBytes.length - 2);
        transferDataBytes[transferDataBytes.length - 2] = (byte) (crcCan & 0xFF);
        transferDataBytes[transferDataBytes.length - 1] = (byte) (crcCan >> 8 & 0xFF);

        return transferDataBytes;
    }

    private byte[] createEndData() {
        byte[] startData = new byte[139];
        int offset = 0;
        // B0   07   00   8B 固定填充
        startData[offset] = (byte) 0xB0;
        startData[++offset] = 0x07;
        startData[++offset] = 0x00;
        startData[++offset] = (byte) 0x8B;

        startData[++offset] = SOH_END;
        startData[++offset] = frameSn_D;
        startData[++offset] = frameSn_R;
        final int crcStart = offset + 1;

        final short crc = crc16(startData, crcStart, 135);
        startData[135] = (byte) (crc & 0xFF);
        startData[136] = (byte) (crc >> 8);

        final short crcCan = crc16(startData, 0, 137);
        startData[137] = (byte) (crcCan & 0xFF);
        startData[138] = (byte) (crcCan >> 8 & 0xFF);
        System.out.println("~~结束帧：" + StringFormatHelper.getInstance().toHexString(startData));
        return startData;
    }

    private void convertData(byte[] data, byte[] srcData, CanDeviceIoAction deviceIoAction) throws IOException {

        final int count = srcData.length / 7;
        int frameSn = 0x10;
        data[4] = 0x08;
        for (int i = 0, pos = 0; i < count; i++, pos += 7) {
            data[8] = (byte) frameSn;
            System.arraycopy(srcData, pos, data, 9, 7);
            deviceIoAction.write(data);
            System.out.println("~~帧数据：" + StringFormatHelper.getInstance().toHexString(data));
            sleep(20);
            frameSn = frameSn == 0x10 ? 0x20 : ++frameSn;
            if (frameSn > 0xFF) {
                frameSn = 0x10;
            }
        }
        if (srcData.length % 7 > 0) {
            int pos = srcData.length - srcData.length % 7;
            int len_ = srcData.length % 7;
            data[4] = (byte) (len_ + 1);
            data[8] = (byte) frameSn;
            System.arraycopy(srcData, pos, data, 9, len_);
            deviceIoAction.write(data);
            System.out.println("~~帧数据：" + StringFormatHelper.getInstance().toHexString(data));
        }
    }

    private void reboot(final CanDeviceIoAction canDeviceIoAction) {
        final byte[] srcData = {(byte) 0xB0, 0x05, 0x00, 0x0A, 0x00, 0x07, 0x00, 0x01, (byte) 0xF6, 0x7F};
        try {
            convertData(frameData, srcData, canDeviceIoAction);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void jumpAppProgram(final CanDeviceIoAction canDeviceIoAction) {
        final byte[] srcData = {(byte) 0xB0, 0x05, 0x00, 0x0A, 0x00, 0x08, 0x00, 0x01, (byte) 0xC6, 0x7C};
        try {
            convertData(frameData, srcData, canDeviceIoAction);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void update(long process, long total, String info, @EvnUpdateStatus int status) {
        if (evnCallBack != null) {
            evnCallBack.update(process, total, info, status);
        }
    }

    private void full(byte[] transferDataBytes) {
        if (transferDataBytes != null) {
            Arrays.fill(transferDataBytes, (byte) 0xFF);
        }
    }
}
