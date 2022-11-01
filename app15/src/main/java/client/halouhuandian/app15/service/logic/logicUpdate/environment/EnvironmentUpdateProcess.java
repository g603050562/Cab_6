package client.halouhuandian.app15.service.logic.logicUpdate.environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import client.halouhuandian.app15.hardWareConncetion.androidHard.SerialAndCanPortUtilsGeRui;
import client.halouhuandian.app15.hardWareConncetion.environmentPlate.EnvironmentSend;
import client.halouhuandian.app15.pub.BaseDataDistribution;
import client.halouhuandian.app15.pub.util.UtilPublic;
import client.halouhuandian.app15.service.logic.logicUpdate.UpdateInfoFormat;
import client.halouhuandian.app15.service.logic.logicUpdate.UpdateInfoReturnListener;

public class EnvironmentUpdateProcess {

    private String address = "98B06665";
    private int threadWhileCode = 500;
    private Thread thread;
    private UpdateInfoFormat updateInfoFormat;
    private UpdateInfoReturnListener updateInfoReturnListener;
    private EnvironmentUpdateDataFormat environmentUpdateDataFormat = null;
    private BaseDataDistribution.LogicListener logicListener;

    private int indexCount = 0;
    private int isFalse = 0;

    public EnvironmentUpdateProcess(UpdateInfoFormat updateInfoFormat, UpdateInfoReturnListener updateInfoReturnListener) {
        this.updateInfoFormat = updateInfoFormat;
        this.updateInfoReturnListener = updateInfoReturnListener;
        EnvironmentUpdateIntegration.getInstance().addListener(logicListener = new BaseDataDistribution.LogicListener() {
            @Override
            public void returnData(Object object) {
                environmentUpdateDataFormat = (EnvironmentUpdateDataFormat) object;
            }
        });
    }

    public void onStart() {
        final int door = updateInfoFormat.getDoor();
        final String filePath = updateInfoFormat.getFilePath();
        if (thread == null) {
            thread = new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        updateInfoReturnListener.returnInfo(door, UpdateInfoReturnListener.UpdateTypeInfo.steps, "开始进行升级环境板");
                        //1.验证升级文件完整性
                        File file = new File(filePath);
                        if (!(file != null && file.exists())) {
                            updateInfoReturnListener.returnInfo(door, UpdateInfoReturnListener.UpdateTypeInfo.error, "升级文件不存在");
                            onDestroy();
                            return;
                        }
                        InputStream inputStream = new FileInputStream(file);
                        int len = inputStream.available();
                        if (len <= 0) {
                            updateInfoReturnListener.returnInfo(door, UpdateInfoReturnListener.UpdateTypeInfo.error, "数据长度为0!");
                            onDestroy();
                            return;
                        }
                        //2.正在重启环境板
                        updateInfoReturnListener.returnInfo(0, UpdateInfoReturnListener.UpdateTypeInfo.steps, "正在重启环境板");
                        EnvironmentSend.rebootByBoot();
                        sleep(3000);
                        //3.开始升级

                        //4.下发升级头文件
                        updateInfoReturnListener.returnInfo(0, UpdateInfoReturnListener.UpdateTypeInfo.steps, "正在下发头文件");
                        byte[][] topData = sendYModemData(createTopData(file));
                        for (int i = 0; i < topData.length; i++) {
                            SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(address, topData[i]);
                            System.out.println("update - address - 发送 - "+ address + " - data - "+ UtilPublic.ByteArrToHex(topData[i]));
                            sleep(20);
                        }
                        int state_1 = -1;
                        for (int i = 0; i < 10 * 5; i++) {
                            sleep(100);
                            if (environmentUpdateDataFormat != null && environmentUpdateDataFormat.getType() == EnvironmentUpdateDataFormat.EnvironmentUpdateType.requireData) {
                                state_1 = 1;
                                break;
                            }
                        }
                        if (state_1 == -1) {
                            updateInfoReturnListener.returnInfo(door, UpdateInfoReturnListener.UpdateTypeInfo.error, "没有建立升级连接，升级结束！");
                            onDestroy();
                            return;
                        }

                        //5.下发升级本体文件
                        long orderCount = file.length() / 1024;
                        long orderRemainder = file.length() % 1024;
                        if (orderRemainder != 0) {
                            orderCount = orderCount + 1;
                        }
                        long orderIndex = 1;
                        while (threadWhileCode > 0) {
                            if (environmentUpdateDataFormat == null) {
                                threadWhileCode--;
                                sleep(20);
                            } else {
                                sleep(50);
                                threadWhileCode = 500;
                                environmentUpdateDataFormat = null;
                                updateInfoReturnListener.returnRate(orderIndex, orderCount);
                                orderIndex = orderIndex + 1;
                                byte[][] bodyData = sendYModemData(createBodyData(inputStream));
                                for (int i = 0; i < bodyData.length; i++) {
                                    indexCount = indexCount + 1;

                                    updateInfoReturnListener.returnInfo(door, UpdateInfoReturnListener.UpdateTypeInfo.steps, "正在发送第" + indexCount + "帧数据！");

                                    SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(address, bodyData[i]);

                                    System.out.println("update - address - "+ address + " - data - "+ UtilPublic.ByteArrToHex(bodyData[i]));
                                    sleep(10);
                                }
                                if (inputStream.available() == 0) {
                                    threadWhileCode = -100;
                                }
                            }
                        }
                        if (threadWhileCode <= 0 && threadWhileCode > -100) {
                            if(isFalse < 2){
                                updateInfoReturnListener.returnInfo(door, UpdateInfoReturnListener.UpdateTypeInfo.steps, "环境板升级出现错误，正在重新开始升级！");
                                byte[][] endData = sendYModemData(createEndData());
                                for (int i = 0; i < topData.length; i++) {
                                    SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(address, endData[i]);
                                    System.out.println("update - address - "+ address + " - data - "+ UtilPublic.ByteArrToHex(endData[i]));
                                    sleep(20);
                                }
                                threadWhileCode = 500;
                                indexCount = 0;
                                isFalse = isFalse + 1;
                                run();
                            }else{
                                updateInfoReturnListener.returnInfo(door, UpdateInfoReturnListener.UpdateTypeInfo.error, "传输文件出现错误！");
                                onDestroy();
                                return;
                            }
                        } else if (threadWhileCode == -100) {
                            updateInfoReturnListener.returnInfo(door, UpdateInfoReturnListener.UpdateTypeInfo.steps, "传输主文件成功！");
                            //6.下发升级尾文件
                            updateInfoReturnListener.returnInfo(0, UpdateInfoReturnListener.UpdateTypeInfo.steps, "正在传输尾文件");
                            int state_2 = -1;
                            for (int i = 0; i < 10 * 5; i++) {
                                sleep(100);
                                if (environmentUpdateDataFormat != null && environmentUpdateDataFormat.getType() == EnvironmentUpdateDataFormat.EnvironmentUpdateType.requireData) {
                                    state_2 = 1;
                                    environmentUpdateDataFormat = null;
                                    break;
                                }
                            }
                            if (state_2 == -1) {
                                updateInfoReturnListener.returnInfo(door, UpdateInfoReturnListener.UpdateTypeInfo.error, "传输文件出现错误，升级结束！");
                                onDestroy();
                                return;
                            } else {
                                byte[][] endData = sendYModemData(createEndData());
                                for (int i = 0; i < topData.length; i++) {
                                    SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(address, endData[i]);
                                    System.out.println("update - address - "+ address + " - data - "+ UtilPublic.ByteArrToHex(endData[i]));
                                    sleep(20);
                                }
                                int state_3 = -1;
                                for (int i = 0; i < 10 * 5; i++) {
                                    sleep(100);
                                    if (environmentUpdateDataFormat != null && environmentUpdateDataFormat.getType() == EnvironmentUpdateDataFormat.EnvironmentUpdateType.requireData) {
                                        state_3 = 1;
                                        environmentUpdateDataFormat = null;
                                        break;
                                    }
                                }
                                if (state_3 == -1) {
                                    updateInfoReturnListener.returnInfo(door, UpdateInfoReturnListener.UpdateTypeInfo.error, "传输尾文件失败，升级结束！");
                                    onDestroy();
                                    return;
                                } else {
                                    updateInfoReturnListener.returnInfo(door, UpdateInfoReturnListener.UpdateTypeInfo.success, "传输成功，升级结束！");
                                    onDestroy();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }
    }


    //3. 起始帧的数据格式:SOH 00 FF filename filezise NUL CRCH CRCL
    private byte[] createTopData(File file) {

        System.out.println("update - address - 发送 - 文件名 - " + file.getName());

        final String fileName = file.getName();
        final char[] fileNameChars = fileName.toCharArray();
        long fileSizeLen = file.length();
        final char[] fileSizeLenChars = (fileSizeLen + "").toCharArray();

        byte[] startData = new byte[139];
        int offset = 0;
        startData[offset] = (byte) 0xB0;
        startData[++offset] = (byte) 0x07;
        startData[++offset] = (byte) 0x00;
        startData[++offset] = (byte) 0x8B;

        startData[++offset] = (byte) 0x01;
        startData[++offset] = (byte) 0x00;
        startData[++offset] = (byte) 0xFF;

        final int crcStart = offset + 1;

        for (char c : fileNameChars) {
            startData[++offset] = Byte.parseByte((int) c + "");
        }
        startData[++offset] = 0x00;

        for (char c : fileSizeLenChars) {
            startData[++offset] = Byte.parseByte((int) c + "");
        }
        startData[++offset] = 0x00;

        final short crc = crc16(startData, crcStart, 135);
        startData[135] = (byte) (crc & 0xFF);
        startData[136] = (byte) (crc >> 8);

        final short crcCan = crc16(startData, 0, 137);
        startData[137] = (byte) (crcCan & 0xFF);
        startData[138] = (byte) (crcCan >> 8 & 0xFF);

        return startData;
    }

    private int sn = 0x01;
    private int snR = (byte) 0xFE;
    private byte[] createBodyData(InputStream inputStream) throws IOException {

        byte[] dataBytes = new byte[1024];
        byte[] transferDataBytes = new byte[1 + 1 + 1 + 4 + dataBytes.length + 1 + 1 + 1 + 1];

        int offset = 0;

        transferDataBytes[offset] = (byte) 0xB0;
        transferDataBytes[++offset] = 0x07;
        transferDataBytes[++offset] = 0x04;
        transferDataBytes[++offset] = 0x0B;

        transferDataBytes[++offset] = 0x02;
        sn = (sn == 0x00 ? 0x01 : sn);
        transferDataBytes[++offset] = (byte) sn++;
        snR = (snR == 0xFF ? 0xFE : snR);
        transferDataBytes[++offset] = (byte) snR--;

        if (inputStream.available() / 1024 > 0) {
            inputStream.read(dataBytes, 0, dataBytes.length);
        } else {
            final int available = inputStream.available();
            if (available % 1024 > 0) {
                Arrays.fill(dataBytes, (byte) 0xff);
                inputStream.read(dataBytes, 0, available);
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
        startData[++offset] = (byte) 0x07;
        startData[++offset] = (byte) 0x00;
        startData[++offset] = (byte) 0x8B;

        startData[++offset] = (byte) 0x04;
        startData[++offset] = (byte) 0x00;
        startData[++offset] = (byte) 0xFF;
        final int crcStart = offset + 1;

        final short crc = crc16(startData, crcStart, 135);
        startData[135] = (byte) (crc & 0xFF);
        startData[136] = (byte) (crc >> 8);

        final short crcCan = crc16(startData, 0, 137);
        startData[137] = (byte) (crcCan & 0xFF);
        startData[138] = (byte) (crcCan >> 8 & 0xFF);
        return startData;
    }


    private byte[][] sendYModemData(byte[] data) {
        byte[][] returnData = null;
        int onderCount = data.length / 7;
        int orderRemainder = data.length % 7;
        if (orderRemainder != 0) {
            onderCount = onderCount + 1;
        }
        returnData = new byte[onderCount][8];
        for (int i = 0; i < onderCount; i++) {
            int dataTop = 0;
            if (i == 0) {
                dataTop = 16;
                returnData[i] = new byte[]{(byte) dataTop, data[i * 7], data[i * 7 + 1], data[i * 7 + 2], data[i * 7 + 3], data[i * 7 + 4], data[i * 7 + 5], data[i * 7 + 6]};
            } else if (i == onderCount - 1) {
                dataTop = 31 + i;
                returnData[i] = new byte[]{(byte) dataTop, 0, 0, 0, 0, 0, 0, 0};
                for (int j = 0; j < orderRemainder; j++) {
                    returnData[i][j + 1] = data[i * 7 + j];
                }
            } else {
                dataTop = 31 + i;
                returnData[i] = new byte[]{(byte) dataTop, data[i * 7], data[i * 7 + 1], data[i * 7 + 2], data[i * 7 + 3], data[i * 7 + 4], data[i * 7 + 5], data[i * 7 + 6]};
            }

        }
        return returnData;
    }

    private short crc16(byte[] data, int offset, int len) {
        int crc = 0xFFFF;
        int j;
        for (int i = offset; i < len; i++) {
            crc = ((crc & 0xFF00) | (crc & 0x00FF) ^ (data[i] & 0xFF));
            for (j = 0; j < 8; j++, crc = ((crc & 0x0001) > 0) ? (crc >> 1) ^ 0xA001 : (crc >> 1)) ;
        }
        return (short) (crc & 0xFFFF);
    }


    public void onDestroy() {
        EnvironmentSend.reboot();
        EnvironmentUpdateIntegration.getInstance().deleteListener(logicListener);
    }

}
