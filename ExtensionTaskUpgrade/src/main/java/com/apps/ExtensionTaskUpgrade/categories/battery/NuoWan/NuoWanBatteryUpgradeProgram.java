package com.apps.ExtensionTaskUpgrade.categories.battery.NuoWan;

import android.support.v4.util.Consumer;
import android.text.TextUtils;

import com.apps.ExtensionTaskUpgrade.categories.battery.BatteryAttribute;
import com.apps.ExtensionTaskUpgrade.categories.battery.BatteryUpgradeProgram;
import com.apps.ExtensionTaskUpgrade.categories.battery.UpgradeLock;
import com.apps.ExtensionTaskUpgrade.core.ioAction.CanDeviceIoAction;
import com.apps.ExtensionTaskUpgrade.core.ioAction.DeviceIoAction;
import com.apps.ExtensionTaskUpgrade.help.StringFormatHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Author:      Lee Yeung
 * Create Date: 2020-02-20
 * Description: 诺万电池升级程序
 */
public class NuoWanBatteryUpgradeProgram extends BatteryUpgradeProgram {
    //需要进入485转发，下标0是对应的控制板地址，需要设置电池对应的控制板地址

    private byte[] _485 = new byte[]{address, 0x05, 0x00, 0x0B, 0x00, 0x01, 0x00, 0x00};

    //电池ID码
    static final byte[] BATTERY_ID_CODE = {0x3A, 0x16, (byte) 0x7E, 0x01, 0x00, 0x00, 0x00, 0x0D, 0x0A};

    //BMS版本信息
    static final byte[] VERSION = {0x3A, 0x16, 0x7F, 0x01, 0x00, 0x00, 0x00, 0x0D, 0x0A};

    //进入bootLoader模式
    private byte[] bootLoaderMode = new byte[]{0x3A, 0x16, (byte) 0xF0, 0x0D,//长度信息是子命令和数据内容的长度
            (byte) 0xF1, 0x4A, 0x4D, 0x4B, 0x2D, 0x42, 0x4D, 0x53, 0x2D, 0x42, 0x4C, 0x30, 0x30, 0x00, 0x00, 0x0D, 0x0A};

    //发送新固件信息(0xF6)
    private byte[] firmwareInfo = new byte[]{0x3A, 0x16, (byte) 0xF0, 0x11, (byte) 0xF6, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
            , 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0D, 0x0A};

    //发送新固件数据(0xF7)
    private byte[] firmwareData = new byte[]{0x3A, 0x16, (byte) 0xF0, (byte) 0x83, (byte) 0xF7, 0x00, 0x00,//1~2当前固件数据帧号。范围:0~(总帧数-1)。
            //固件数据每帧最长数据为 128 字节。
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0D, 0x0A};

    //立即激活新程序，不激活的话，在所有数据帧写完之10S后电池自动激活
    private byte[] activationBMS = new byte[]{0x3A, 0x16, (byte) 0xF0, 0x02, (byte) 0xF4, 0x00, 0x00, 0x00, 0x0D, 0x0A};


    public NuoWanBatteryUpgradeProgram(BatteryAttribute batteryAttribute) {
        super(batteryAttribute);
    }

    @Override
    protected void execute_sp(DeviceIoAction deviceIoAction) {
        if (deviceIoAction == null) {
            return;
        }

        final byte address = batteryAttribute.address;

        int sum;

        short crc = crc16(_485, 0, 6);
        _485[_485.length - 2] = (byte) (crc & 0xFF);
        _485[_485.length - 1] = (byte) (crc >> 8 & 0xFF);

        try {
            byte[] result = null;
            sleep(5000);
            deviceIoAction.read();
            // TODO: 2019-09-05 激活485转发
            deviceIoAction.write(_485);
            // TODO: 2019-09-05 读取一次串口数据
            sleep(500);
            result = deviceIoAction.read();
            if (result != null && result.length > 0) {
                onUpgrading(address, UpgradeStatus.WAITTING_485, "激活485转发成功.", 0, 0);
            } else {
                onUpgrading(address, UpgradeStatus.FAILED, "激活485转发失败." + StringFormatHelper.getInstance().toHexString(result), 0, 0);
                sleep(10 * 1000);
                return;
            }

            if (TextUtils.isEmpty(batteryAttribute.filePath)) {
                onUpgrading(address, UpgradeStatus.FAILED, "升级文件路径为空.", 0, 0);
                sleep(10 * 1000);
                return;
            }
            File file = new File(batteryAttribute.filePath);
            if (!(file != null && file.exists())) {
                onUpgrading(address, UpgradeStatus.FAILED, "升级文件不存在." + file.getAbsolutePath(), 0, 0);
                sleep(10 * 1000);
                return;
            }

            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                onUpgrading(address, UpgradeStatus.FAILED, "升级文件没有找到." + e.getLocalizedMessage(), 0, 0);
                sleep(10 * 1000);
                return;
            }

            if (inputStream == null) {
                onUpgrading(address, UpgradeStatus.FAILED, "升级文件流为null.", 0, 0);
                sleep(10 * 1000);
                return;
            }

            final String idCode = batteryAttribute.idCode;
            if (TextUtils.isEmpty(idCode) || idCode.length() < 2) {
                onUpgrading(address, UpgradeStatus.FAILED, "传入的ID码为空或者长度小于2.", 0, 0);
                sleep(10 * 1000);
                return;
            }

            sum = calculateSum(BATTERY_ID_CODE, 1, 4);
            BATTERY_ID_CODE[5] = (byte) (sum & 0xFF);
            BATTERY_ID_CODE[6] = (byte) (sum >> 8 & 0xFF);
            System.out.println("请求ID码：" + StringFormatHelper.getInstance().toHexString(BATTERY_ID_CODE));
            deviceIoAction.write(BATTERY_ID_CODE);
            sleep(200);
            result = deviceIoAction.read();
            if (result != null && result.length >= 20) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 4, len = result.length - 4; i < len; stringBuilder.append((char) (result[i] & 0xFF)), i++)
                    ;
                final String resultIdCode = stringBuilder.toString();
                System.out.println("ID码：" + resultIdCode);
                System.out.println("ID码十六进制：" + StringFormatHelper.getInstance().toHexString(result));
                if (!(!TextUtils.isEmpty(resultIdCode) && resultIdCode.startsWith(idCode))) {
                    onUpgrading(address, UpgradeStatus.FAILED, "电池类型和升级包不匹配.", 0, 0);
                    sleep(10 * 1000);
                    return;
                }
            } else {
                onUpgrading(address, UpgradeStatus.FAILED, "电池ID码错误.", 0, 0);
                sleep(10 * 1000);
                return;
            }

            // TODO: 2019-09-08 比较CRC校验码
            if (TextUtils.isEmpty(batteryAttribute.crcValue)) {
                onUpgrading(address, UpgradeStatus.FAILED, "文件包CRC为空.", 0, 0);
                sleep(10 * 1000);
                return;
            }

            // TODO: 2019-09-05 进入BootLoader模式
            sum = calculateSum(bootLoaderMode, 1, 16);
            bootLoaderMode[17] = (byte) (sum & 0xFF);
            bootLoaderMode[18] = (byte) (sum >> 8 & 0xFF);
            final long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < 30 * 1000)//超过30S失败
            {
                deviceIoAction.write(bootLoaderMode);

                sleep(500);
                result = deviceIoAction.read();
                if (result != null && result.length > 6 && result[4] == (byte) 0xF1 && result[5] == 0x00) {
                    break;
                }
            }
            if (result != null && result.length > 6 && result[4] == (byte) 0xF1 && result[5] == 0x00) {
                onUpgrading(address, UpgradeStatus.BOOT_LOADER_MODE, "进入BootLoader模式成功.", 0, 0);
            } else {
                onUpgrading(address, UpgradeStatus.FAILED, "进入BootLoader模式失败." + StringFormatHelper.getInstance().toHexString(result), 0, 0);
                sleep(10 * 1000);
                return;
            }

            // TODO: 2019-09-05 初始化固件数据
            int len = 0;
            try {
                len = inputStream.available();
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] binData = null;
            if (len > 0) {
                binData = new byte[len];
                try {
                    inputStream.read(binData);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                firmwareInfo[7] = (byte) (len & 0xFF);
                firmwareInfo[8] = (byte) (len >> 8 & 0xFF);
                firmwareInfo[9] = (byte) (len >> 16 & 0xFF);
                // TODO:总帧数,低字节在前，高字节在后。
                int totalFrameSize = len / 128;
                if (len % 128 > 0) {
                    totalFrameSize += 1;
                }

                firmwareInfo[10] = (byte) (totalFrameSize & 0xFF);
                firmwareInfo[11] = (byte) (totalFrameSize >> 8 & 0xFF);

                final int binDataCrc = crc32(binData);
                System.out.println("计算后的CRC：" + binDataCrc);
                if (hexToInt(batteryAttribute.crcValue) != binDataCrc) {
                    onUpgrading(address, UpgradeStatus.FAILED, "CRC不匹配!" + StringFormatHelper.getInstance().toHexString(result), 0, 0);
                    sleep(10 * 1000);
                    return;
                }

                // TODO: 2019-09-02 CRC32 校验码,低字节在前，高字节在后。
                firmwareInfo[12] = (byte) (binDataCrc & 0xFF);
                firmwareInfo[13] = (byte) (binDataCrc >> 8 & 0xFF);
                firmwareInfo[14] = (byte) (binDataCrc >> 16 & 0xFF);
                firmwareInfo[15] = (byte) (binDataCrc >> 24 & 0xFF);

                sum = calculateSum(firmwareInfo, 1, 20);
                firmwareInfo[21] = (byte) (sum & 0xFF);
                firmwareInfo[22] = (byte) (sum >> 8 & 0xFF);
                System.out.println("新固件信息写入：" + StringFormatHelper.getInstance().toHexString(firmwareInfo));
                deviceIoAction.write(firmwareInfo);
                sleep(2000);// TODO: 2019-10-14 诺万厂商建议2S
                result = deviceIoAction.read();
                if (result != null && result.length > 6 && result[4] == (byte) 0xF6 && result[5] == 0x00) {
                    onUpgrading(address, UpgradeStatus.INIT_FIRMWARE_DATA, "新固件信息发送成功.", 0, 0);
                } else {
                    onUpgrading(address, UpgradeStatus.FAILED, "新固件信息发送失败:" + StringFormatHelper.getInstance().toHexString(result), 0, 0);
                    sleep(10 * 1000);
                    return;
                }

                // TODO: 2019-09-02 开始循环写入数据帧
                int loopCount = 1;
                short sn = 0;
                int offset = 0;
                for (; loopCount < totalFrameSize; loopCount++, sn++, offset += 127) {
                    // TODO: 2019-09-02 帧号
                    firmwareData[5] = (byte) (sn & 0xFF);
                    firmwareData[6] = (byte) (sn >> 8 & 0xFF);
                    // TODO: 2019-09-02 数据帧
                    System.arraycopy(binData, offset, firmwareData, 7, 128);
                    sum = calculateSum(firmwareData, 1, 134);
                    firmwareData[135] = (byte) (sum & 0xFF);
                    firmwareData[136] = (byte) (sum >> 8 & 0xFF);
                    deviceIoAction.write(firmwareData);
                    offset++;
                    sleep(200);
                    result = deviceIoAction.read();
                    if (result != null && result.length > 6 && result[4] == (byte) 0xF7 && result[5] == 0x00) {
                        onUpgrading(address, UpgradeStatus.WRITE_DATA, "发送" + sn + "条成功", sn, totalFrameSize);

                    } else {
                        onUpgrading(address, UpgradeStatus.FAILED, "发送" + sn + "条失败! 错误数据：" + StringFormatHelper.getInstance().toHexString(result), sn, totalFrameSize);
                        sleep(10 * 1000);
                        return;
                    }
                }

                if (loopCount == totalFrameSize) {
                    // TODO: 2019-09-02 处理最后一帧数据
                    // TODO: 2019-09-02 帧号
                    firmwareData[5] = (byte) (sn & 0xFF);
                    firmwareData[6] = (byte) (sn >> 8 & 0xFF);

                    // TODO: 2019-09-03 最后一帧数据长度
                    final int lastLen = 7 + len - offset + 4;
                    byte[] lastData = new byte[lastLen];
                    System.arraycopy(firmwareData, 0, lastData, 0, 7);
                    System.arraycopy(binData, offset, lastData, 7, len - offset);
                    // TODO: 2019-09-03 填充长度
                    lastData[3] = (byte) ((3 + len - offset) & 0xFF);

                    int end = lastLen - 4 - 1;
                    sum = calculateSum(lastData, 1, end);
                    lastData[++end] = (byte) (sum & 0xFF);
                    lastData[++end] = (byte) (sum >> 8 & 0xFF);
                    lastData[++end] = 0x0D;
                    lastData[++end] = 0x0A;

                    deviceIoAction.write(lastData);
                    sleep(3000);
                    result = deviceIoAction.read();
                    if (result != null && result.length > 6 && result[4] == (byte) 0xF7 && result[5] == 0x00) {
                        onUpgrading(address, UpgradeStatus.WRITE_DATA, "发送" + sn + "条成功", sn, totalFrameSize);

                    } else {
                        onUpgrading(address, UpgradeStatus.FAILED, "发送" + sn + "条失败! 错误数据：" + StringFormatHelper.getInstance().toHexString(result), sn, totalFrameSize);
                        sleep(10 * 1000);
                        return;
                    }
                }

                sum = calculateSum(activationBMS, 1, 5);
                activationBMS[6] = (byte) (sum & 0xFF);
                activationBMS[7] = (byte) (sum >> 8 & 0xFF);

                deviceIoAction.write(activationBMS);
                sleep(100);
                result = deviceIoAction.read();
                onUpgrading(address, UpgradeStatus.ACTION_BMS, "正在激活...", sn, totalFrameSize);

                sleep(500);
                if (result != null && result.length > 6 && result[4] == (byte) 0xF4 && result[5] == 0x00) {
                    onUpgrading(address, UpgradeStatus.ACTION_BMS, "激活成功!", sn, totalFrameSize);
                } else {
                    onUpgrading(address, UpgradeStatus.FAILED, "激活失败!", sn, totalFrameSize);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            onUpgrading(address, UpgradeStatus.FAILED, "升级IO异常\n" + e.getLocalizedMessage(), -1, -1);
        }

        sleep(10 * 1000);
        // TODO: 2019-09-20 超时10S电池自动使用新程序,同时推出485转发模式
        onUpgrading(address, UpgradeStatus.SUCCESSED, "升级成功!", -1, -1);
    }

    @Override
    protected void execute_can(final CanDeviceIoAction canDeviceIoAction) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                execute_can(canDeviceIoAction, new UpgradeLock());
            }
        }).start();
    }

    private void execute_can(CanDeviceIoAction canDeviceIoAction, final UpgradeLock upgradeLock) {

        // TODO: 2020-01-15 结束升级，发送通讯命令
        //        final byte[] fi3nishUpgrade = new byte[]{0x65, address, (byte) 0xA3, (byte) 0x98,
        //                0x01,
        //                0x00, 0x00, 0x00,
        //                (byte) 0xAA, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        //        try
        //        {
        //            deviceIoAction.write(fi3nishUpgrade);
        //        }
        //        catch (IOException e)
        //        {
        //            e.printStackTrace();
        //        }
        //        System.out.println("~~结束升级");
        //        if(true)return;

        int sum;
        int totalFrameSize = 0;

        try {
            byte[] result = null;
            if (TextUtils.isEmpty(batteryAttribute.filePath)) {
                onUpgrading(address, UpgradeStatus.FAILED, "升级文件路径为空.", 0, 0);
                sleep(10 * 1000);
                return;
            }
            File file = new File(batteryAttribute.filePath);
            if (!(file != null && file.exists())) {
                onUpgrading(address, UpgradeStatus.FAILED, "升级文件不存在." + file.getAbsolutePath(), 0, 0);
                sleep(10 * 1000);
                return;
            }

            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                onUpgrading(address, UpgradeStatus.FAILED, "升级文件没有找到." + e.getLocalizedMessage(), 0, 0);
                sleep(10 * 1000);
                return;
            }

            if (inputStream == null) {
                onUpgrading(address, UpgradeStatus.FAILED, "升级文件流为null.", 0, 0);
                sleep(10 * 1000);
                return;
            }

            // TODO: 2020-01-13 电池升级命令帧0xA3
            final byte[] updradeModeData = new byte[]{0x65, address, (byte) 0xA3, (byte) 0x98,
                    0x01,
                    0x00, 0x00, 0x00,
                    0x55, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
            canDeviceIoAction.write(updradeModeData);

            // TODO: 2020-01-12 定义头帧数据PF:0xA0
            final byte[] startData = new byte[]{0x65, address, (byte) 0xA0, (byte) 0x98,
                    0x04,
                    0x00, 0x00, 0x00,
                    (byte) 0xAA, 0x55, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
            // TODO: 2020-01-12 定义数据帧PF:0xA1
            final byte[] data = new byte[]{0x65, address, (byte) 0xA1, (byte) 0x98,
                    0x08,
                    0x00, 0x00, 0x00,
                    0x55, (byte) 0xAA, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
            // TODO: 2020-01-12 定义尾帧数据PF:0xA2
            final byte[] lastData = new byte[]{0x65, address, (byte) 0xA2, (byte) 0x98,
                    0x02,
                    0x00, 0x00, 0x00,
                    0x55, (byte) 0xAA, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};


            // TODO: 2020-01-13 注册回调对象
            final int startDataResultId = (startData[3] & 0xFF) << 24 | (startData[2] & 0xFF) << 16 | (startData[0] & 0xFF) << 8 | startData[1] & 0xFF;
            canDeviceIoAction.register(startDataResultId, new Consumer<byte[]>() {
                @Override
                public void accept(byte[] bytes) {
                    System.out.println("~~电池升级头：" + bytes[12]);
                    if (bytes != null && bytes.length == 16) {
                        if (upgradeLock.cmdFlagMap != null && !upgradeLock.cmdFlagMap.containsKey(bytes[12])) {
                            upgradeLock.cmdFlag = bytes[12];
                            upgradeLock.dataSize = 0;
                            upgradeLock.dataLen = ((bytes[11] & 0xFF) << 8) + (bytes[10] & 0xFF);
                            if (upgradeLock.dataLen > 0) {
                                upgradeLock.flagBytes = new byte[upgradeLock.dataLen];
                            }
//                            System.out.println("~~电池升级头帧：" + StringFormatHelper.getInstance().toHexString(bytes));
                        }
                    }
                }
            });

            final int dataResultId = (data[3] & 0xFF) << 24 | (data[2] & 0xFF) << 16 | (data[0] & 0xFF) << 8 | data[1];
            canDeviceIoAction.register(dataResultId, new Consumer<byte[]>() {
                @Override
                public void accept(byte[] bytes) {
                    if (bytes != null && bytes.length == 16) {
                        if (upgradeLock.cmdFlagMap != null && !upgradeLock.cmdFlagMap.containsKey(upgradeLock.cmdFlag)) {
//                            System.out.println("~~电池升级数据帧：" + StringFormatHelper.getInstance().toHexString(bytes));
                            if (upgradeLock.flagBytes != null) {
                                final int len = (bytes[4] & 0xFF) - 1;
                                System.arraycopy(bytes, 9, upgradeLock.flagBytes, upgradeLock.dataSize, len);
                                upgradeLock.dataSize += len;
                            }
                        }
                    }
                }
            });

            final int lastDataResultId = (lastData[3] & 0xFF) << 24 | (lastData[2] & 0xFF) << 16 | (lastData[0] & 0xFF) << 8 | lastData[1] & 0xFF;
            canDeviceIoAction.register(lastDataResultId, new Consumer<byte[]>() {
                @Override
                public void accept(byte[] bytes) {
                    if (bytes != null && bytes.length == 16) {
                        if ((bytes[8] & 0xFF) == 0x55 && (bytes[9] & 0xFF) == 0xAA) {
                            if (upgradeLock.cmdFlag == bytes[10] && upgradeLock.cmdFlagMap != null && !upgradeLock.cmdFlagMap.containsKey(upgradeLock.cmdFlag)) {
                                if (upgradeLock.cmdFlag != (byte) 0xF7) {
                                    upgradeLock.cmdFlagMap.put(upgradeLock.cmdFlag, true);
                                }
//                                System.out.println("~~电池升级尾帧：" + StringFormatHelper.getInstance().toHexString(bytes));
                                upgradeLock.isContinue = true;
                                if (upgradeLock.flagBytes != null) {
//                                    System.out.println("~~电池结果总帧：" + StringFormatHelper.getInstance().toHexString(upgradeLock.flagBytes));
                                }

                                synchronized (upgradeLock) {
                                    upgradeLock.notify();
                                }
                            }
                        }
                    }
                }
            });

            //设置长度信息
            //            startData[10] = (byte) (bootLoaderMode.length & 0xFF);
            //            startData[11] = (byte) ((bootLoaderMode.length >> 8) & 0xFF);

            // TODO: 2019-09-05 进入BootLoader模式
            sum = calculateSum(bootLoaderMode, 1, 16);
            bootLoaderMode[17] = (byte) (sum & 0xFF);
            bootLoaderMode[18] = (byte) (sum >> 8 & 0xFF);
            System.out.println("~~请求BootLoader模式：" + StringFormatHelper.getInstance().toHexString(bootLoaderMode));
            final long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < 30 * 1000)//超过30S失败
            {
                convertData(startData, data, lastData, bootLoaderMode, canDeviceIoAction);
                sleep(500);
                if (upgradeLock.flagBytes != null && upgradeLock.flagBytes.length > 6 && upgradeLock.flagBytes[4] == (byte) 0xF1 && upgradeLock.flagBytes[5] == 0x00) {
                    System.out.println("~~成功break");
                    break;
                } else {
                    if (upgradeLock.cmdFlagMap != null) {
                        upgradeLock.cmdFlagMap.remove(upgradeLock.cmdFlag);
                    }
                    System.out.println("~~没有成功清除");
                }
            }
            if (upgradeLock.flagBytes != null && upgradeLock.flagBytes.length > 6 && upgradeLock.flagBytes[4] == (byte) 0xF1 && upgradeLock.flagBytes[5] == 0x00) {
                onUpgrading(address, UpgradeStatus.BOOT_LOADER_MODE, "进入BootLoader模式成功.", 0, 0);
            } else {
                onUpgrading(address, UpgradeStatus.FAILED, "进入BootLoader模式失败." + StringFormatHelper.getInstance().toHexString(result), 0, 0);
                sleep(10 * 1000);
                return;
            }

            // TODO: 2019-09-05 初始化固件数据
            int len = 0;
            try {
                len = inputStream.available();
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] binData = null;

            if (len > 0) {
                binData = new byte[len];
                try {
                    inputStream.read(binData);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                firmwareInfo[7] = (byte) (len & 0xFF);
                firmwareInfo[8] = (byte) (len >> 8 & 0xFF);
                firmwareInfo[9] = (byte) (len >> 16 & 0xFF);
                // TODO:总帧数,低字节在前，高字节在后。
                totalFrameSize = len / 128;
                if (len % 128 > 0) {
                    totalFrameSize += 1;
                }

                firmwareInfo[10] = (byte) (totalFrameSize & 0xFF);
                firmwareInfo[11] = (byte) (totalFrameSize >> 8 & 0xFF);

                final int binDataCrc = crc32(binData);
                System.out.println("~~计算后的CRC：" + binDataCrc);
                if (hexToInt(batteryAttribute.crcValue) != binDataCrc) {
                    onUpgrading(address, UpgradeStatus.FAILED, "CRC不匹配!" + StringFormatHelper.getInstance().toHexString(result), 0, 0);
                    sleep(10 * 1000);
                    return;
                }

                // TODO: 2019-09-02 CRC32 校验码,低字节在前，高字节在后。
                firmwareInfo[12] = (byte) (binDataCrc & 0xFF);
                firmwareInfo[13] = (byte) (binDataCrc >> 8 & 0xFF);
                firmwareInfo[14] = (byte) (binDataCrc >> 16 & 0xFF);
                firmwareInfo[15] = (byte) (binDataCrc >> 24 & 0xFF);

                sum = calculateSum(firmwareInfo, 1, 20);
                firmwareInfo[21] = (byte) (sum & 0xFF);
                firmwareInfo[22] = (byte) (sum >> 8 & 0xFF);
                System.out.println("~~新固件信息写入：" + StringFormatHelper.getInstance().toHexString(firmwareInfo));

                convertData(startData, data, lastData, firmwareInfo, canDeviceIoAction);
                synchronized (upgradeLock) {
                    try {
                        upgradeLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (upgradeLock.flagBytes != null && upgradeLock.flagBytes.length > 6 && upgradeLock.flagBytes[4] == (byte) 0xF6 && upgradeLock.flagBytes[5] == 0x00) {
                    onUpgrading(address, UpgradeStatus.INIT_FIRMWARE_DATA, "新固件信息发送成功.", 0, 0);
                } else {
                    onUpgrading(address, UpgradeStatus.FAILED, "新固件信息发送失败:" + StringFormatHelper.getInstance().toHexString(result), 0, 0);
                    sleep(10 * 1000);
                    return;
                }

                System.out.println("~~新固件信息发送成功");

                // TODO: 2019-09-02 开始循环写入数据帧
                int loopCount = 1;
                short sn = 0;
                int offset = 0;
                for (; loopCount < totalFrameSize; loopCount++, sn++, offset += 127) {
                    // TODO: 2019-09-02 帧号
                    firmwareData[5] = (byte) (sn & 0xFF);
                    firmwareData[6] = (byte) (sn >> 8 & 0xFF);
                    // TODO: 2019-09-02 数据帧
                    System.arraycopy(binData, offset, firmwareData, 7, 128);
                    sum = calculateSum(firmwareData, 1, 134);
                    firmwareData[135] = (byte) (sum & 0xFF);
                    firmwareData[136] = (byte) (sum >> 8 & 0xFF);

//                    System.out.println("~~第" + sn + "包：" + StringFormatHelper.getInstance().toHexString(firmwareData));
                    convertData(startData, data, lastData, firmwareData, canDeviceIoAction);
                    offset++;

                    synchronized (upgradeLock) {
                        try {
                            long startT = System.currentTimeMillis();
                            upgradeLock.wait(5000);
                            long endT = System.currentTimeMillis();
                            if (endT - startT >= 5000) {
                                onUpgrading(address, UpgradeStatus.FAILED, "发送" + sn + "条失败，数据帧超时！", sn, totalFrameSize);
                                sleep(10 * 1000);
                                return;
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

//                    sleep(50);

                    if (upgradeLock.flagBytes != null && upgradeLock.flagBytes.length > 6 && upgradeLock.flagBytes[4] == (byte) 0xF7 && upgradeLock.flagBytes[5] == 0x00) {
                        onUpgrading(address, UpgradeStatus.WRITE_DATA, "发送" + sn + "条成功", sn, totalFrameSize);
                    } else {
                        onUpgrading(address, UpgradeStatus.FAILED, "发送" + sn + "条失败! 错误数据：" + StringFormatHelper.getInstance().toHexString(result), sn, totalFrameSize);
                        sleep(10 * 1000);
                        return;
                    }
                }

                if (loopCount == totalFrameSize) {
                    // TODO: 2019-09-02 处理最后一帧数据
                    // TODO: 2019-09-02 帧号
                    firmwareData[5] = (byte) (sn & 0xFF);
                    firmwareData[6] = (byte) (sn >> 8 & 0xFF);

                    // TODO: 2019-09-03 最后一帧数据长度
                    final int lastLen = 7 + len - offset + 4;
                    byte[] lastFrameData = new byte[lastLen];
                    System.arraycopy(firmwareData, 0, lastFrameData, 0, 7);
                    System.arraycopy(binData, offset, lastFrameData, 7, len - offset);
                    // TODO: 2019-09-03 填充长度
                    lastFrameData[3] = (byte) ((3 + len - offset) & 0xFF);

                    int end = lastLen - 4 - 1;
                    sum = calculateSum(lastFrameData, 1, end);
                    lastFrameData[++end] = (byte) (sum & 0xFF);
                    lastFrameData[++end] = (byte) (sum >> 8 & 0xFF);
                    lastFrameData[++end] = 0x0D;
                    lastFrameData[++end] = 0x0A;

//                    System.out.println("~~第" + sn + "包：" + StringFormatHelper.getInstance().toHexString(firmwareData));
                    convertData(startData, data, lastData, lastFrameData, canDeviceIoAction);
                    synchronized (upgradeLock) {
                        try {
                            upgradeLock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    if (upgradeLock.flagBytes != null && upgradeLock.flagBytes.length > 6 && upgradeLock.flagBytes[4] == (byte) 0xF7 && upgradeLock.flagBytes[5] == 0x00) {
                        onUpgrading(address, UpgradeStatus.WRITE_DATA, "发送" + sn + "条成功", sn, totalFrameSize);
                    } else {
                        onUpgrading(address, UpgradeStatus.FAILED, "发送" + sn + "条失败! 错误数据：" + StringFormatHelper.getInstance().toHexString(result), sn, totalFrameSize);
                        sleep(10 * 1000);
                        return;
                    }
                }

                sleep(5000);

                sum = calculateSum(activationBMS, 1, 5);
                activationBMS[6] = (byte) (sum & 0xFF);
                activationBMS[7] = (byte) (sum >> 8 & 0xFF);

                convertData(startData, data, lastData, activationBMS, canDeviceIoAction);
                synchronized (upgradeLock) {
                    try {
                        upgradeLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                onUpgrading(address, UpgradeStatus.ACTION_BMS, "正在激活...", sn, totalFrameSize);
//                System.out.println("~~激活读：" + StringFormatHelper.getInstance().toHexString(result));

                if (upgradeLock.flagBytes != null && upgradeLock.flagBytes.length > 6 && upgradeLock.flagBytes[4] == (byte) 0xF4 && upgradeLock.flagBytes[5] == 0x00) {
                    onUpgrading(address, UpgradeStatus.ACTION_BMS, "激活成功!", sn, totalFrameSize);
                } else {
                    onUpgrading(address, UpgradeStatus.FAILED, "激活失败!", sn, totalFrameSize);
                }
            }

            // TODO: 2020-01-15 结束升级，发送通讯命令
            final byte[] finishUpgrade = new byte[]{0x65, address, (byte) 0xA3, (byte) 0x98,
                    0x01,
                    0x00, 0x00, 0x00,
                    (byte) 0xAA, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
            canDeviceIoAction.write(finishUpgrade);
        } catch (IOException e) {
            onUpgrading(address, UpgradeStatus.FAILED, "升级IO异常\n" + e.getLocalizedMessage(), -1, -1);
        }

        System.out.println("~~激活执行完成");

        // TODO: 2019-09-20 超时10S电池自动使用新程序,同时推出485转发模式
        onUpgrading(address, UpgradeStatus.SUCCESSED, "升级成功!", totalFrameSize, totalFrameSize);
    }

    private void convertData(byte[] startData, byte[] data, byte[] lastData, byte[] srcData, CanDeviceIoAction deviceIoAction) throws IOException {
        startData[10] = (byte) (srcData.length & 0xFF);
        startData[11] = (byte) ((srcData.length >> 8) & 0xFF);

        deviceIoAction.write(startData);
//        System.out.println("~~开始：" + StringFormatHelper.getInstance().toHexString(startData));
        sleep(20);

        int count = srcData.length / 7;
        int frameSn = 1;
        for (int i = 0, pos = 0; i < count; i++, frameSn = frameSn++ % 255 > 0 ? frameSn : 1, pos += 7) {
            data[8] = (byte) frameSn;
            System.arraycopy(srcData, pos, data, 9, 7);
            deviceIoAction.write(data);
//            System.out.println("~~数据：" + StringFormatHelper.getInstance().toHexString(data));
            sleep(20);
        }

        if (srcData.length % 7 > 0) {
            int pos = srcData.length - srcData.length % 7;
            int len_ = srcData.length % 7;
            data[8] = (byte) frameSn;
            System.arraycopy(srcData, pos, data, 9, len_);
            deviceIoAction.write(data);
//            System.out.println("~~数据：" + StringFormatHelper.getInstance().toHexString(data));
            sleep(20);
        }
        deviceIoAction.write(lastData);
//        System.out.println("~~结束：" + StringFormatHelper.getInstance().toHexString(lastData));
    }
}
