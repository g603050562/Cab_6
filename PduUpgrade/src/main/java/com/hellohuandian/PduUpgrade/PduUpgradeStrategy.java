package com.hellohuandian.PduUpgrade;

import android.support.v4.util.Consumer;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Author:      Lee Yeung
 * Create Date: 2019-12-06
 * Description:
 */
public class PduUpgradeStrategy extends BasePduUpgradeStrategy {

    public PduUpgradeStrategy() {
        this((byte) 0x88);
    }

    PduUpgradeStrategy(byte address) {
        super(address);
    }

    class Lock {
        public byte sn;
        public boolean isContinue;
    }

    private final PduUpgradeProgress mPduUpgradeProgress = new PduUpgradeProgress() {
        @Override
        public void onProgress(long currentProgress, long totalProgress, byte statusCode, byte codeValue, String info) {
            if (pduUpgradeProgress != null) {
                pduUpgradeProgress.onProgress(currentProgress, totalProgress, statusCode, codeValue, info);
            }
        }
    };

    private CanReader canReader;
    private CanWriter canWriter;
    private PduUpgradeProgress pduUpgradeProgress;

    public void setPduUpgradeProgress(PduUpgradeProgress pduUpgradeProgress) {
        this.pduUpgradeProgress = pduUpgradeProgress;
    }

    public void setCanReader(CanReader canReader) {
        this.canReader = canReader;
    }

    public void setCanWriter(CanWriter canWriter) {
        this.canWriter = canWriter;
    }

    public void upgrade() {
        if (TextUtils.isEmpty(filePath)) {
            mPduUpgradeProgress.onProgress(0, 0, PduUpgradeStatusCode.PDU_UPGRADE_RUN, (byte) 0x00, "pdu升级文件路径为空！");
            return;
        }

        File file = new File(filePath);
        if (!(file != null && file.exists())) {
            mPduUpgradeProgress.onProgress(0, 0, PduUpgradeStatusCode.PDU_UPGRADE_RUN, (byte) 0x00, "pdu升级文件可能不存在！");
            return;
        }

        if (canReader == null || canWriter == null) {
            mPduUpgradeProgress.onProgress(0, 0, PduUpgradeStatusCode.PDU_UPGRADE_RUN, (byte) 0x00, "读和写操作可能为空！");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                runPduUpgrade();
            }
        }).start();
    }

    private void runPduUpgrade() {
        final Lock lock = new Lock();
        // TODO: 2019-12-02 1:请求pdu升级模式
        canReader.setConsumer(new Consumer<byte[]>() {
            final int updradeModeResultId = 0x88 << 24 | 0x0F << 16 | 0x3D << 8 | 0xAB;

            @Override
            public void accept(byte[] bytes) {
                if (bytes != null && bytes.length == 16) {
                    final int frameId = (bytes[3] & 0xFF) << 24 | (bytes[2] & 0xFF) << 16 | (bytes[1] & 0xFF) << 8 | (bytes[0] & 0xFF);
                    if (frameId == updradeModeResultId) {
                        lock.isContinue = (bytes[11] & 0xFF) == 0x31;
                        mPduUpgradeProgress.onProgress(0, 0, PduUpgradeStatusCode.PDU_UPGRADE_MODE, bytes[11],
                                lock.isContinue ? "PDU进入升级模式成功！" : "PDU进入升级模式失败！");
                        synchronized (lock) {
                            lock.notify();
                        }
                    }
                }
            }
        });
        // 请求升级模式指令
        final byte[] updradeModeData = new byte[]{0x3F, (byte) 0xAF, 0x0D, (byte) 0x88,
                0x08,
                0x00, 0x00, 0x00,
                0x01, (byte) 0xF0, 0x00, 0x30, 0x55, 0x00, 0x00, 0x00};
        synchronized (lock) {
            try {
                canWriter.write(updradeModeData);
                lock.wait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!lock.isContinue) {
            return;
        }

        // TODO: 2019-12-02 2: 进入升级模式后，上位机把整个 bin 文件拆分开，每整包数据是 2k 字节数据，2k 的数据 也是拆成每帧报文，携带 7 个字节发送。
        File file = new File(filePath);
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
            return;
        }

        // TODO: 2019-12-05 计算总2K包数量
        final int packetCount = len / 2048 + (len % 2048 > 0 ? 1 : 0);

        // TODO: 2019-12-05 建立链接指令
        final byte[] upgradeConnectData = new byte[]{0x3F, (byte) 0xAF, 0x1D, (byte) 0x88,
                0x08,
                0x00, 0x00, 0x00,
                0x10,//命令标志16(10H)请求发送
                (byte) (packetCount & 0xFF), (byte) (packetCount >> 8 & 0xFF), (byte) (packetCount >> 16 & 0xFF), (byte) (packetCount >> 24 & 0xFF),//本包程序字节数
                (byte) (packetCount & 0xFF), (byte) (packetCount >> 8 & 0xFF),//本包帧数
                (byte) 0xFF//预留
        };

        // TODO: 2019-12-05 数据单帧指令
        final byte[] upgradeFrameData = new byte[]{0x3F, (byte) 0xAF, 0x2D, (byte) 0x88,
                0x08,
                0x00, 0x00, 0x00,
                0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        // TODO: 2019-12-05 单包结束指令
        final byte[] framePacketFinish = new byte[]{0x3F, (byte) 0xAF, 0x1D, (byte) 0x88,
                0x08,
                0x00, 0x00, 0x00,
                0x13,//报文发送结束
                0x00, 0x00, 0x00, 0x00,//本包程序字节数
                0x00, 0x00,//CRC-16
                0x00};


        try {
            final int availableLen = inputStream.available();
            if (availableLen <= 0) {
                return;
            }

            //创建升级包文件数组
            final byte[] frameDataByes = new byte[availableLen];
            inputStream.read(frameDataByes);
            inputStream.close();

            int elementCount = availableLen;
            int currentReadLen;
            int packetLen = 0;
            int start = 0;
            int packetSn = 0;

            while (elementCount > 0) {
                packetSn++;
                final int psn = packetSn;
                lock.isContinue = false;
                // TODO: 2019-12-05 ===========================================================
                // TODO: 2019-12-03  3.每次都要重新建立链接
                canReader.setConsumer(new Consumer<byte[]>() {
                    final int updradeConnectResultId = 0x88 << 24 | 0x1F << 16 | 0x3D << 8 | 0xAB;

                    @Override
                    public void accept(byte[] bytes) {
                        if (bytes != null && bytes.length == 16) {
                            final int frameId = (bytes[3] & 0xFF) << 24 | (bytes[2] & 0xFF) << 16 | (bytes[1] & 0xFF) << 8 | (bytes[0] & 0xFF);
                            if (frameId == updradeConnectResultId) {
                                synchronized (lock) {
                                    lock.isContinue = (bytes[8] & 0xFF) == 0x11;
                                    if (lock.isContinue) {
                                        lock.sn = bytes[11];//下一个要发送的数据包编号
                                    } else {
                                        mPduUpgradeProgress.onProgress(psn, packetCount, PduUpgradeStatusCode.PDU_UPGRADE_CONNECTING, bytes[8],
                                                lock.isContinue ? "PDU建立连接成功！" : "PDU建立连接失败！");
                                    }
                                    lock.notify();
                                }
                            }
                        }
                    }
                });
                synchronized (lock) {
                    try {
                        canWriter.write(upgradeConnectData);
                        lock.wait();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (!lock.isContinue) {
                    return;
                }

                byte sn = lock.sn;
                //当前可读取长度
                currentReadLen = elementCount >= 2048 ? 2048 : elementCount;
                elementCount -= currentReadLen;
                // TODO: 2019-12-05 ===========================================================
                // TODO: 2019-12-05 循环处理单包整除7的数据
                for (int i = 0, count = currentReadLen / 7; i < count; i++, start += 7) {
                    upgradeFrameData[4] = 0x08;
                    sn = sn % 256 == 0 ? 0 : sn;
                    upgradeFrameData[8] = sn++;
                    System.arraycopy(frameDataByes, start, upgradeFrameData, 9, 7);
                    Thread.sleep(10);
                    canWriter.write(upgradeFrameData);
                }
                // TODO: 2019-12-05 ===========================================================
                // TODO: 2019-12-05 处理单包不能被7整除，剩余长度的数据
                final int lastLen = currentReadLen % 7;
                if (lastLen > 0) {
                    upgradeFrameData[4] = (byte) (1 + lastLen);
                    upgradeFrameData[8] = sn;

                    System.arraycopy(frameDataByes, start, upgradeFrameData, 9, lastLen);
                    canWriter.write(upgradeFrameData);
                    start += lastLen;
                }

                // TODO: 2019-12-05 ===========================================================
                // TODO: 2019-12-04 5.发送单包数据帧结束指令
                framePacketFinish[9] = (byte) (currentReadLen & 0xFF);
                framePacketFinish[10] = (byte) (currentReadLen >> 8 & 0xFF);
                framePacketFinish[11] = (byte) (currentReadLen >> 16 & 0xFF);
                framePacketFinish[12] = (byte) (currentReadLen >> 24 & 0xFF);

                //计算单包CRC值
                packetLen += currentReadLen;
                short crc16 = crc16(frameDataByes, packetLen - currentReadLen, packetLen);
                framePacketFinish[13] = (byte) (crc16 & 0xFF);
                framePacketFinish[14] = (byte) (crc16 >> 8 & 0xFF);

                canReader.setConsumer(new Consumer<byte[]>() {
                    final int framePacketFinishResultId = 0x88 << 24 | 0x3F << 16 | 0x3D << 8 | 0xAB;

                    @Override
                    public void accept(byte[] bytes) {
                        if (bytes != null && bytes.length == 16) {
                            final int frameId = (bytes[3] & 0xFF) << 24 | (bytes[2] & 0xFF) << 16 | (bytes[1] & 0xFF) << 8 | (bytes[0] & 0xFF);
                            if (frameId == framePacketFinishResultId) {
                                synchronized (lock) {
                                    lock.isContinue = (bytes[8] & 0xFF) == 0xAA;
                                    String info = "";
                                    if (lock.isContinue) {
                                        switch ((bytes[8] & 0xFF)) {
                                            case 0xAA:
                                                info = "单包成功！";
                                                break;
                                            case 0x00:
                                                info = "单包失败！";
                                                break;
                                            case 0x08:
                                                info = "程序长度错误！";
                                                break;
                                            case 0x09:
                                                info = "程序校验错误！";
                                                break;
                                            case 0x0B:
                                                info = "写入FLASH失败！";
                                                break;

                                        }
                                    }
                                    mPduUpgradeProgress.onProgress(psn, packetCount, PduUpgradeStatusCode.PDU_UPGRADE_PACKET_FINISH, bytes[8], info);
                                    lock.notify();
                                }
                            }
                        }
                    }
                });
                synchronized (lock) {
                    canWriter.write(framePacketFinish);
                    lock.wait();
                }

                if (!lock.isContinue) {
                    return;
                }
            }

            // TODO: 2019-12-05 ===========================================================
            // TODO: 2019-12-04 6.发送整包结束报文指令
            canReader.setConsumer(new Consumer<byte[]>() {
                final int framePacketFinishResultId = 0x88 << 24 | 0x5F << 16 | 0x3D << 8 | 0xAB;

                @Override
                public void accept(byte[] bytes) {
                    if (bytes != null && bytes.length == 16) {
                        final int frameId = (bytes[3] & 0xFF) << 24 | (bytes[2] & 0xFF) << 16 | (bytes[1] & 0xFF) << 8 | (bytes[0] & 0xFF);
                        if (frameId == framePacketFinishResultId) {
                            synchronized (lock) {
                                lock.isContinue = (bytes[8] & 0xFF) == 0x0E;
                                String info = "";
                                if (lock.isContinue) {
                                    switch ((bytes[8] & 0xFF)) {
                                        case 0xAA:
                                            info = "升级成功！";
                                            break;
                                        case 0x00:
                                            info = "升级程序接收失败！";
                                            break;
                                        case 0x03:
                                            info = "CRC校验失败！";
                                            break;
                                        case 0x04:
                                            info = "复制程序失败(BOOT)！";
                                            break;
                                        case 0x05:
                                            info = "传输错误超过3次！";
                                            break;
                                        case 0x06:
                                            info = "连续20次接收非升级指令！";
                                            break;
                                        case 0x07:
                                            info = "CAN接收超时！";
                                            break;
                                        case 0x08:
                                            info = "CRC校验失败(BOOT)！";
                                            break;
                                        case 0x09:
                                            info = "栈顶地址不合法(BOOT)！";
                                            break;
                                        case 0x0A:
                                            info = "FLG区校验失败！";
                                            break;
                                        case 0x0C:
                                            info = "BOOT区CRC校验失败！";
                                            break;
                                        case 0x0D:
                                            info = "BOOT区栈顶地址不合法！";
                                            break;
                                        case 0x0E:
                                            info = "应用程序区程序接收完成！";
                                            break;
                                        case 0x0B:
                                            info = "写入FLASH失败！";
                                            break;
                                    }
                                }
                                mPduUpgradeProgress.onProgress(packetCount, packetCount, PduUpgradeStatusCode.PDU_UPGRADE_ALL_FINISH, bytes[8], info);
                                lock.notify();
                            }
                        }
                    }
                }
            });
            final short crc16 = crc16(frameDataByes, 0, availableLen);
            final byte[] allPacketFinish = new byte[]{0x3F, (byte) 0xAF, 0x4D, (byte) 0x88,
                    0x08,
                    0x00, 0x00, 0x00,
                    (byte) (availableLen & 0xFF), (byte) (availableLen >> 8 & 0xFF), (byte) (availableLen >> 16 & 0xFF), (byte) (availableLen >> 24 & 0xFF),//本包程序字节数
                    (byte) (crc16 & 0xFF), (byte) (crc16 >> 8 & 0xFF),//CRC-16
                    (byte) 0xFF, (byte) 0xFF};

            synchronized (lock) {
                canWriter.write(allPacketFinish);
                lock.wait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
