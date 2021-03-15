package com.apps.ExtensionTaskUpgrade.categories.dc.dcdc;

import android.os.SystemClock;
import android.support.v4.util.Consumer;

import com.apps.ExtensionTaskUpgrade.categories.dc.DC_UpgradeProgram;
import com.apps.ExtensionTaskUpgrade.core.ioAction.CanDeviceIoAction;
import com.apps.ExtensionTaskUpgrade.help.StringFormatHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Author:      Lee Yeung
 * Create Date: 2019-12-10
 * Description:
 */
public class DCDC_UpgradeProgram extends DC_UpgradeProgram {
    public DCDC_UpgradeProgram(byte address) {
        super(address);
    }

    class Lock {
        public volatile boolean isContinue;
        //传输地址
        public volatile int transmissionAddress;
        //传输长度
        public volatile short transmissionLen;

        public byte[] flagBytes = new byte[16];
    }

    @Override
    protected void execute_can(final CanDeviceIoAction deviceIoAction) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(upgradeFilePath);
                if (!(file != null && file.exists())) {
                    onUpgrading(address, DCUpgradeStatus.FAILED, "升级文件可能不存在！", -1, -1);
                    return;
                }

                InputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if (inputStream == null) {
                    return;
                }

                int len = 0;
                try {
                    len = inputStream.available();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (len <= 0) {
                    onUpgrading(address, DCUpgradeStatus.FAILED, "数据长度为0！", -1, -1);
                    return;
                }

                final byte[] DATA = new byte[len];
                try {
                    inputStream.read(DATA);
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    runPduUpgrade(DATA, deviceIoAction, new Lock());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void runPduUpgrade(final byte[] DATA, final CanDeviceIoAction deviceIoAction, final Lock lock) throws Exception {
        // TODO: 2020/6/19 下发DCDC关机
        final byte PF = 0x00;
        byte PS = -1;
        final byte SA = 0x65;//SA：Android的地址固定
        final byte turnOff = (byte) 0xAA;
        final byte[] DATA_TURE_OFF = new byte[]{SA, PS, PF, (byte) 0x98,
                0x08,//数据长度
                0x00, 0x00, 0x00,//远程帧，错误帧，过载帧
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        DATA_TURE_OFF[1] = address;
        DATA_TURE_OFF[8] = turnOff;
        deviceIoAction.write(DATA_TURE_OFF);
        sleep(1000);

        // TODO: 2019-12-11 ===========================1：发送命令让总线从正常模式切换至升级模式================================
        // TODO: 2019-12-11 (先选择设置模式，模式值为升级模式,注意：0xFF是广播地址为了让所有DCDC模块全部挂起并且让出总线负载)
        final byte[] selectUpgradeModeData = new byte[]{0x65, (byte) 0xFF, 0x13, (byte) 0x98,
                0x02,
                0x00, 0x00, 0x00,
                0x02, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        System.out.println("DCDC升级步骤1：" + StringFormatHelper.getInstance().toHexString(selectUpgradeModeData));
        deviceIoAction.write(selectUpgradeModeData);
        onUpgrading(address, DCUpgradeStatus.SET_UPGRADE_MODE, "升级模式", -1, -1);
        sleep(100);

        // TODO: 2019-12-11 ===========================2：上位机发送给下位机从APP切换至boot命令================================
        // TODO: 2019-12-11 (设置指定地址对应的DCDC模块进入升级模式之后，需要对其重启boot才能生效，注意：只操作重新选定的地址)
        final byte[] selectBootModeData = new byte[]{0x65, (byte) (address & 0xFF), 0x13, (byte) 0x98,
                0x02,
                0x00, 0x00, 0x00,
                0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        System.out.println("DCDC升级步骤2：" + StringFormatHelper.getInstance().toHexString(selectBootModeData));
        deviceIoAction.write(selectBootModeData);
        onUpgrading(address, DCUpgradeStatus.REBOOT_UPGRADE_MODE, "重启升级模式", -1, -1);
        sleep(100);

        // TODO: 2019-12-11 ===========================3：注册下位机是否收到连接帧回调================================
        // TODO: 2019-12-11 下位机立即回复上位机的连接帧请求,只是请求，还没有确认
        final int connectingRequestResultId = (0x9CA << 20) + (0x01 << 19) + ((androidAddress & 0xFF) << 11) + ((address & 0xFF) << 3) + (0x03 << 0);
        System.out.println("DCDC-connectingRequestResultId:" + connectingRequestResultId);
        deviceIoAction.registerTimeOut(connectingRequestResultId, SystemClock.elapsedRealtime() + 5 * 1000);
        deviceIoAction.register(connectingRequestResultId, new Consumer<byte[]>() {
            @Override
            public void accept(byte[] bytes) {
                if (bytes != null) {
                    System.out.println("DCDC升级步骤4下位机返回：" + StringFormatHelper.getInstance().toHexString(bytes));
                    lock.isContinue = (bytes[8] & 0xFF) == 0x00 && (bytes[9] & 0xFF) == 0xF0;
                    if (lock.isContinue) {
                        System.arraycopy(bytes, 0, lock.flagBytes, 0, lock.flagBytes.length);
                        deviceIoAction.unRegister(connectingRequestResultId);
                    }
                }
                synchronized (lock) {
                    lock.notify();
                }
            }
        });

        // TODO: 2019-12-11上位机开始50ms周期发送请求连接帧，是否超时达到200ms
        final int connectingId = (0x9CA << 20) + (0x01 << 19) + ((address & 0xFF) << 11) + ((androidAddress & 0xFF) << 3) + (0x03 << 0);
        final byte[] connectingFrameData = new byte[]{(byte) (connectingId & 0xFF), (byte) ((connectingId >> 8) & 0xFF), (byte) ((connectingId >> 16) & 0xFF), (byte) ((connectingId >> 24) & 0xFF),
                0x00,
                0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        onUpgrading(address, DCUpgradeStatus.CONNECTING, "请求连接帧", -1, -1);

        int count = 8;
        while (count-- > 0) {
            System.out.println("DCDC升级步骤3：" + StringFormatHelper.getInstance().toHexString(connectingFrameData));
            deviceIoAction.write(connectingFrameData);
            sleep(100);
            if (lock.isContinue) {
                break;
            }
        }
        if (!lock.isContinue) {
            synchronized (lock) {
                lock.wait(5 * 1000);
            }
        }
        if (!lock.isContinue) {
            // TODO: 2019-12-12 下位机立即回复上位机的连接帧请求超时
            System.out.println("下位机立即回复上位机的连接帧请求超时");
            // TODO: 2019-12-16 解除挂起DCDC
            selectUpgradeModeData[9] = 0x01;
            System.out.println("DCDC解除挂起");
            onUpgrading(address, DCUpgradeStatus.FAILED, "请求连接帧失败", -1, -1);
            deviceIoAction.write(selectUpgradeModeData);
            return;
        }

        // TODO: 2019-12-11 ========================先注册升级请求传输帧===================================
        // TODO: 2019-12-11  升级开始，模块请求升级信息0x1c8f000b(超时时间为15S，15S升级失败，返回)
        final int _1c8_ResultId = (0x9C8 << 20) + (0x01 << 19) + ((androidAddress & 0xFF) << 11) + ((address & 0xFF) << 3) + (0x03 << 0);
        deviceIoAction.register(_1c8_ResultId, new Consumer<byte[]>() {
            @Override
            public void accept(byte[] bytes) {
                System.out.println("DCDC升级头");
                synchronized (lock) {
                    /*
                        0x00:块回应 ack 响应
                        0x01:数据丢失
                        0x02:最后一块响应
                        0x03:非法的固件程序
                        0x04:设备写固件丢失
                    */
                    if (bytes != null && bytes.length == 16) {
                        lock.isContinue = (bytes[8] & 0xFF) == 0x00 && (bytes[9] & 0xFF) == 0x00;
                        lock.transmissionAddress = (bytes[10] & 0xFF) << 24 | (bytes[11] & 0xFF) << 16 | (bytes[12] & 0xFF) << 8 | (bytes[13] & 0xFF);
                        lock.transmissionLen = (short) ((bytes[14] & 0xFF) << 8 | (bytes[15] & 0xFF));
                    }
                    lock.notify();
                }
            }
        });
        final int _1c9_ResultId = (0x9C9 << 20) + (0x01 << 19) + ((androidAddress & 0xFF) << 11) + ((address & 0xFF) << 3) + (0x03 << 0);
        deviceIoAction.register(_1c9_ResultId, new Consumer<byte[]>() {
            @Override
            public void accept(byte[] bytes) {
                System.out.println("DCDC升级段");
                synchronized (lock) {
                    /*
                        0x00:块回应 ack 响应
                        0x01:数据丢失
                        0x02:最后一块响应
                        0x03:非法的固件程序
                        0x04:设备写固件丢失
                    */
                    if (bytes != null && bytes.length == 16) {
                        if ((bytes[8] & 0xFF) == 0x00 && (bytes[9] & 0xFF) == 0x02) {
                            deviceIoAction.unRegister(_1c9_ResultId);
                            deviceIoAction.unRegister(_1c8_ResultId);
                            System.out.println("升级成功");
                        } else {
                            lock.isContinue = (bytes[8] & 0xFF) == 0x00 && (bytes[9] & 0xFF) == 0x00;
                            lock.transmissionAddress = (bytes[10] & 0xFF) << 24 | (bytes[11] & 0xFF) << 16 | (bytes[12] & 0xFF) << 8 | (bytes[13] & 0xFF);
                            lock.transmissionLen = (short) ((bytes[14] & 0xFF) << 8 | (bytes[15] & 0xFF));
                        }
                    }
                    lock.notify();
                }
            }
        });

        // TODO: 2019-12-11 ========================下位机立即回复上位机的连接帧确认===================================
        // TODO: 2019-12-11上位机响应确认连接oK命令(注意是确认连接ok)
        lock.isContinue = false;
        int _connected_Frame_Id = (0x9CA << 20) + (0x01 << 19) + ((address & 0xFF) << 11) + ((androidAddress & 0xFF) << 3) + (0x03 << 0);
        final byte[] connectedFrameData = new byte[]{(byte) (_connected_Frame_Id & 0xFF), (byte) ((_connected_Frame_Id >> 8) & 0xFF), (byte) ((_connected_Frame_Id >> 16) & 0xFF), (byte) ((_connected_Frame_Id >> 24) & 0xFF),
                0x08,
                0x00, 0x00, 0x00,
                (byte) 0x80, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        synchronized (lock) {
            if (lock.flagBytes != null && lock.flagBytes.length == 16) {
                System.arraycopy(lock.flagBytes, 9, connectedFrameData, 9, 7);
            }

            System.out.println("DCDC升级步骤5：");
            deviceIoAction.write(connectedFrameData);
            onUpgrading(address, DCUpgradeStatus.CONNECTED, "请求连接帧成功", -1, -1);
            lock.wait(5 * 1000);
        }
        if (!lock.isContinue) {
            deviceIoAction.unRegister(_1c8_ResultId);
            deviceIoAction.unRegister(_1c9_ResultId);
            // TODO: 2019-12-16 解除挂起DCDC
            selectUpgradeModeData[9] = 0x01;
            System.out.println("DCDC解除挂起：");
            deviceIoAction.write(selectUpgradeModeData);
            onUpgrading(address, DCUpgradeStatus.FAILED, "请求连接帧确认失败", -1, -1);
            return;
        }

        // TODO: 2019-12-11上位机发送数据(0x1d0~0x1ef)
        int _transmission_Frame_Id = (0x9D0 << 20) + (0x01 << 19) + ((address & 0xFF) << 11) + ((androidAddress & 0xFF) << 3) + (0x03 << 0);
        final byte[] transmissionFrameData = new byte[]{(byte) (_transmission_Frame_Id & 0xFF), (byte) ((_transmission_Frame_Id >> 8) & 0xFF), (byte) ((_transmission_Frame_Id >> 16) & 0xFF), (byte) ((_transmission_Frame_Id >> 24) & 0xFF),
                0x00,
                0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        final long totalLen = DATA.length;
        long current = 0;
        while (true) {
            synchronized (lock) {
                short sn = 0;
                int len;
                while ((len = lock.transmissionLen >= 8 ? 8 : lock.transmissionLen % 8) > 0) {
                    current += len;
                    sn = sn % 256 == 0 ? 0 : sn;
                    transmissionFrameData[3] = (byte) (0x9D + (sn >> 4));
                    transmissionFrameData[2] = (byte) (0x08 + ((sn & 0x0F) << 4));

                    transmissionFrameData[4] = (byte) len;
                    System.arraycopy(DATA, lock.transmissionAddress, transmissionFrameData, 8, len);
                    System.out.println("DCDC数据:" + address + " sn：" + sn);
                    deviceIoAction.write(transmissionFrameData);

                    sn++;
                    lock.transmissionAddress += len;
                    lock.transmissionLen -= len;
                    sleep(20);
                }

                onUpgrading(address, DCUpgradeStatus.UPGRADING, "升级...", current, totalLen);

                lock.isContinue = false;
                System.out.println("DCDC数据:" + address + "等待-isContinue" + lock.isContinue);
                lock.wait();

                if (!lock.isContinue) {
                    System.out.println("DCDC数据:" + address + "跳出-isContinue" + lock.isContinue);
                    break;
                }
            }
        }

        deviceIoAction.unRegister(_1c8_ResultId);
        deviceIoAction.unRegister(_1c9_ResultId);

        System.out.println("DCDC升级成功" + address);
        onUpgrading(address, DCUpgradeStatus.SUCCESSED, "升级成功", totalLen, totalLen);

        // TODO: 2019-12-16 解除挂起DCDC
        selectUpgradeModeData[9] = 0x01;
        deviceIoAction.write(selectUpgradeModeData);
        System.out.println("DCDC解除挂起：");
        sleep(5000);
    }
}


