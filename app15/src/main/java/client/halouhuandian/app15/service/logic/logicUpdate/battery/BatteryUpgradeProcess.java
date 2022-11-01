package client.halouhuandian.app15.service.logic.logicUpdate.battery;

import java.io.File;
import java.io.FileInputStream;

import client.halouhuandian.app15.hardWareConncetion.daa.DaaSend;
import client.halouhuandian.app15.pub.BaseDataDistribution;
import client.halouhuandian.app15.pub.util.UtilHexToBin;
import client.halouhuandian.app15.pub.util.UtilPublic;
import client.halouhuandian.app15.service.logic.logicUpdate.UpdateInfoFormat;
import client.halouhuandian.app15.service.logic.logicUpdate.UpdateInfoReturnListener;


/**
 * Author:      Lee Yeung
 * Create Date: 2019-08-30
 * Description:
 */
public class BatteryUpgradeProcess {

    //升级参数
    private UpdateInfoFormat updateInfoFormat;
    //返回接口
    private UpdateInfoReturnListener updateInfoReturnListener;
    //数据接口
    private BaseDataDistribution.LogicListener logicListener;
    //缓存数组
    private byte[] tempArrays = null;


    private static int dengBoBootLoaderIndex = 1024;

    //构造函数
    public BatteryUpgradeProcess(UpdateInfoFormat mUpdateInfoFormat , UpdateInfoReturnListener updateInfoReturnListener) {
        this.updateInfoFormat = mUpdateInfoFormat;
        this.updateInfoReturnListener = updateInfoReturnListener;
        if(logicListener == null){
            BatteryUpdateIntegration.getInstance().addListener(logicListener = new BaseDataDistribution.LogicListener() {
                @Override
                public void returnData(Object object) {
                    tempArrays = (byte[]) object;
                    System.out.println("batteryUpdate - receiver - " + UtilPublic.ByteArrToHex(tempArrays));
                }
            });
        }
    }

    public void onStart() {

        new Thread() {
            @Override
            public void run() {
                super.run();
                final int door = updateInfoFormat.getDoor();
                final String filePath = updateInfoFormat.getFilePath();
                try {
                    //1.准备工作
                    //1_1.检查文件
                    if (filePath == null) {
                        updateInfoReturnListener.returnInfo(door , UpdateInfoReturnListener.UpdateTypeInfo.error , "升级文件路径为空 升级失败！");
                        onDestroy();
                        return;
                    }
                    File file = new File(filePath);
                    if (file == null) {
                        updateInfoReturnListener.returnInfo(door , UpdateInfoReturnListener.UpdateTypeInfo.error , "升级文件初始化失败 升级失败！");
                        onDestroy();
                        return;
                    }
                    if (!file.exists()) {
                        updateInfoReturnListener.returnInfo(door , UpdateInfoReturnListener.UpdateTypeInfo.error , "文件不存在 升级失败！");
                        onDestroy();
                        return;
                    }
                    //1_2.dcdc进入挂起模式
                    DaaSend.hangOnAllExceptIndex(door);
                    sleep(2 * 1000);
                    //1_3.dcdc进入电池升级模式
                    updateInfoReturnListener.returnInfo(door , UpdateInfoReturnListener.UpdateTypeInfo.steps , "正在进入电池升级模式!");
                    BatteryUpdateIntegration.getInstance().sendStart(door);
                    sleep(2 * 1000);
                    //1_4.加载电池bin文件到内存
                    FileInputStream fileInputStream = new FileInputStream(filePath);
                    int len = fileInputStream.available();
                    byte[] binData = new byte[len];
                    fileInputStream.read(binData);
                    //1_5.计算文件bin文件
                    final int binDataCrc = crc32(binData);
                    byte[] result = null;



                    //2.进入bootloader
                    byte[] bootLoaderMode = new byte[]{(byte)0x3A, (byte)0x16, (byte) 0xF0, (byte)0x0D, (byte) 0xF1, (byte)0x4A, (byte)0x4D, (byte)0x4B,
                            (byte)0x2D, (byte)0x42, (byte)0x4D, (byte)0x53, (byte)0x2D, (byte)0x42, (byte)0x4C, (byte)0x30,
                            (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x0D, (byte)0x0A};
                    int sum = calculateSum(bootLoaderMode, 1, 16);
                    bootLoaderMode[17] = (byte) (sum & 0xFF);
                    bootLoaderMode[18] = (byte) (sum >> 8 & 0xFF);
                    final long startTime = System.currentTimeMillis();
                    while (System.currentTimeMillis() - startTime < 10 * 1000){
                        BatteryUpdateIntegration.getInstance().sendData(door , bootLoaderMode);
                        sleep(600);
                        result = tempArrays;
                        tempArrays = null;
                        if (result != null && result.length > 6 && result[4] == (byte) 0xF1 && result[5] == 0x00) {
                            break;
                        }
                    }
                    if (result != null && result.length > 6 && result[4] == (byte) 0xF1 && result[5] == 0x00) {
                        updateInfoReturnListener.returnInfo(door , UpdateInfoReturnListener.UpdateTypeInfo.steps , "进入BootLoader模式成功!");
                    } else {
                        updateInfoReturnListener.returnInfo(door , UpdateInfoReturnListener.UpdateTypeInfo.error , "进入BootLoader模式失败!");
                        onDestroy();
                        return;
                    }



                    //3.发送新固件信息(0xF6)
                    byte[] firmwareInfo = new byte[]{(byte)0x3A, (byte)0x16, (byte) 0xF0, (byte)0x11, (byte) 0xF6, (byte)0x00, (byte)0x00, (byte)0x00,
                            (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                            (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0D,
                            (byte)0x0A};
                    firmwareInfo[7] = (byte) (len & 0xFF);
                    firmwareInfo[8] = (byte) (len >> 8 & 0xFF);
                    firmwareInfo[9] = (byte) (len >> 16 & 0xFF);
                    int totalFrameSize = len / 128;
                    if (len % 128 > 0) {
                        totalFrameSize += 1;
                    }
                    firmwareInfo[10] = (byte) (totalFrameSize & 0xFF);
                    firmwareInfo[11] = (byte) (totalFrameSize >> 8 & 0xFF);
                    firmwareInfo[12] = (byte) (binDataCrc & 0xFF);
                    firmwareInfo[13] = (byte) (binDataCrc >> 8 & 0xFF);
                    firmwareInfo[14] = (byte) (binDataCrc >> 16 & 0xFF);
                    firmwareInfo[15] = (byte) (binDataCrc >> 24 & 0xFF);
                    sum = calculateSum(firmwareInfo, 1, 20);
                    firmwareInfo[21] = (byte) (sum & 0xFF);
                    firmwareInfo[22] = (byte) (sum >> 8 & 0xFF);
                    BatteryUpdateIntegration.getInstance().sendData(door , firmwareInfo);
                    sleep(2000);
                    result = tempArrays;
                    tempArrays = null;
                    if (result != null && result.length > 6 && result[4] == (byte) 0xF6 && result[5] == 0x00) {
                        updateInfoReturnListener.returnInfo(door , UpdateInfoReturnListener.UpdateTypeInfo.steps , "固件信息发送成功!");
                        returnRate(0, totalFrameSize);
                    } else {
                        updateInfoReturnListener.returnInfo(door , UpdateInfoReturnListener.UpdateTypeInfo.error , "固件信息发送失败!");
                        returnRate(0, totalFrameSize);
                        onDestroy();
                        return;
                    }
                    sleep(2000);



                    //4.开始循环写入数据帧 数据(0xF7)
                    byte[] firmwareData = new byte[]{0x3A, 0x16, (byte) 0xF0, (byte) 0x83, (byte) 0xF7, 0x00, 0x00,//1~2当前固件数据帧号。范围:0~(总帧数-1)。
                            //固件数据每帧最长数据为 128 字节。
                            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0D, 0x0A};
                    int loopCount = 1;
                    short sn = 0;
                    int offset = 0;
                    for (; loopCount < totalFrameSize; loopCount++, sn++, offset += 127) {
                        firmwareData[5] = (byte) (sn & 0xFF);
                        firmwareData[6] = (byte) (sn >> 8 & 0xFF);
                        System.arraycopy(binData, offset, firmwareData, 7, 128);
                        sum = calculateSum(firmwareData, 1, 134);
                        firmwareData[135] = (byte) (sum & 0xFF);
                        firmwareData[136] = (byte) (sum >> 8 & 0xFF);
                        BatteryUpdateIntegration.getInstance().sendData(door , firmwareData);
                        offset++;
                        for(int i = 0 ; i < 100 ; i++){
                            sleep(10);
                            if(tempArrays != null){
                                result = tempArrays;
                                tempArrays = null;
                                break;
                            }
                        }
                        if (result != null && result.length > 6 && result[4] == (byte) 0xF7 && result[5] == 0x00) {
                            updateInfoReturnListener.returnInfo(door , UpdateInfoReturnListener.UpdateTypeInfo.steps , "正在发送第" + (sn+1) + "条数据，总共"+totalFrameSize+"条数据");
                            returnRate(sn, totalFrameSize);
                        } else {
                            updateInfoReturnListener.returnInfo(door , UpdateInfoReturnListener.UpdateTypeInfo.error , "发送" + (sn+1) + "条失败");
                            returnRate(sn, totalFrameSize);
                            onDestroy();
                            return;
                        }
                    }



                    //5.处理尾帧数据 数据(0xF7)
                    byte[] activationBMS = new byte[]{0x3A, 0x16, (byte) 0xF0, 0x02, (byte) 0xF4, 0x00, 0x00, 0x00, 0x0D, 0x0A};
                    if (loopCount == totalFrameSize) {
                        firmwareData[5] = (byte) (sn & 0xFF);
                        firmwareData[6] = (byte) (sn >> 8 & 0xFF);
                        final int lastLen = 7 + len - offset + 4;
                        byte[] lastData = new byte[lastLen];
                        System.arraycopy(firmwareData, 0, lastData, 0, 7);
                        System.arraycopy(binData, offset, lastData, 7, len - offset);
                        lastData[3] = (byte) ((3 + len - offset) & 0xFF);
                        int end = lastLen - 4 - 1;
                        sum = calculateSum(lastData, 1, end);
                        lastData[++end] = (byte) (sum & 0xFF);
                        lastData[++end] = (byte) (sum >> 8 & 0xFF);
                        lastData[++end] = 0x0D;
                        lastData[++end] = 0x0A;
                        updateInfoReturnListener.returnInfo(door , UpdateInfoReturnListener.UpdateTypeInfo.steps , "开始激活电池...");
                        BatteryUpdateIntegration.getInstance().sendData(door , lastData);
                        sleep(5000);
                        result = tempArrays;
                        tempArrays = null;
                        if (result != null && result.length > 6 && result[4] == (byte) 0xF7 && result[5] == 0x00) {
                            updateInfoReturnListener.returnInfo(door , UpdateInfoReturnListener.UpdateTypeInfo.steps , "正在发送第" + (sn+1) + "条数据，总共"+totalFrameSize+"条数据");
                            returnRate(sn, totalFrameSize);
                        } else {
                            updateInfoReturnListener.returnInfo(door , UpdateInfoReturnListener.UpdateTypeInfo.error , "发送" + (sn+1) + "条失败");
                            returnRate(sn, totalFrameSize);
                            onDestroy();
                            return;
                        }
                        returnRate(totalFrameSize, totalFrameSize);
                    }
                    sum = calculateSum(activationBMS, 1, 5);
                    activationBMS[6] = (byte) (sum & 0xFF);
                    activationBMS[7] = (byte) (sum >> 8 & 0xFF);
                    BatteryUpdateIntegration.getInstance().sendData(door , activationBMS);
                    sleep(600);
                    result = tempArrays;
                    tempArrays = null;
                    if (result != null && result.length > 6 && result[4] == (byte) 0xF4 && result[5] == 0x00) {
                        sleep(10 * 1000);
                        updateInfoReturnListener.returnInfo(door , UpdateInfoReturnListener.UpdateTypeInfo.steps , "激活成功");
                        returnRate(totalFrameSize, totalFrameSize);
                    }

                    //电池升级成功
                    updateInfoReturnListener.returnInfo(door , UpdateInfoReturnListener.UpdateTypeInfo.steps , "电池升级成功");
                    returnRate(totalFrameSize, totalFrameSize);
                    updateInfoReturnListener.returnInfo(door , UpdateInfoReturnListener.UpdateTypeInfo.success , "正在退出电池升级模式!");
                    onDestroy();

                } catch (Exception e) {
                    updateInfoReturnListener.returnInfo(door , UpdateInfoReturnListener.UpdateTypeInfo.error , e.toString());
                    onDestroy();
                }
            }
        }.start();
    }

     public void onStartByDengBo(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                final int door = updateInfoFormat.getDoor();
                final String filePath = updateInfoFormat.getFilePath();
                try {
                    //1.准备工作
                    //1_1.检查文件
                    if (filePath == null) {
                        updateInfoReturnListener.returnInfo(door , UpdateInfoReturnListener.UpdateTypeInfo.error , "升级文件路径为空 升级失败！");
                        onDestroy();
                        return;
                    }
                    File file = new File(filePath);
                    if (file == null) {
                        updateInfoReturnListener.returnInfo(door , UpdateInfoReturnListener.UpdateTypeInfo.error , "升级文件初始化失败 升级失败！");
                        onDestroy();
                        return;
                    } else if (!file.exists()) {
                        System.out.println("error - " + filePath);
                        updateInfoReturnListener.returnInfo(door , UpdateInfoReturnListener.UpdateTypeInfo.error , "文件不存在 升级失败！");
                        onDestroy();
                        return;
                    } else {
                        updateInfoReturnListener.returnInfo(door , UpdateInfoReturnListener.UpdateTypeInfo.steps , "升级文件有效！");
                    }
                    //1_2.dcdc进入挂起模式
                    DaaSend.hangOnAllExceptIndex(door);
                    sleep(2 * 1000);
                    //1_3.dcdc进入电池升级模式
                    updateInfoReturnListener.returnInfo(door , UpdateInfoReturnListener.UpdateTypeInfo.steps , "正在进入电池升级模式!");
                    BatteryUpdateIntegration.getInstance().sendStart(door);
                    sleep(2 * 1000);

                    //1_4.加载电池bin文件到内存
                    byte[] bytes = new UtilHexToBin().onStart(filePath);

                    //2_1.获取电池数据
                    byte[] intoUpdate = new byte[]{(byte)0xAA};
                    BatteryUpdateIntegration.getInstance().sendData(door , intoUpdate);
                    updateInfoReturnListener.returnInfo(door , UpdateInfoReturnListener.UpdateTypeInfo.steps , "正在获取电池数据!");
                    sleep(5000);
                    byte[] getBatteryInfo = new byte[]{0x55,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
                    byte[] getBatteryInfoReturn = null;
                    boolean getBatteryInfoReturnType = false;
                    for(int i = 0 ; i < 100 ; i ++){
                        if(i % 20 == 0){
                            BatteryUpdateIntegration.getInstance().sendData(door , getBatteryInfo);
                        }
                        sleep(50);
                        getBatteryInfoReturn = tempArrays;
                        tempArrays = null;
                        if (getBatteryInfoReturn != null && getBatteryInfoReturn.length > 10 && getBatteryInfoReturn[0] == 0x55) {
                            getBatteryInfoReturnType = true;
                            break;
                        }
                    }
                    if(getBatteryInfoReturnType){
                        updateInfoReturnListener.returnInfo(door , UpdateInfoReturnListener.UpdateTypeInfo.steps , "电池数据有效!");
                    }else{
                        updateInfoReturnListener.returnInfo(door , UpdateInfoReturnListener.UpdateTypeInfo.error , "未收到电池信息反馈，电池升级结束!");
                        return;
                    }

                    //2_2.擦除数据
                    sleep(2000);
                    updateInfoReturnListener.returnInfo(door , UpdateInfoReturnListener.UpdateTypeInfo.steps , "正在擦除电池数据!");
                    byte[] clearBatteryInfo = new byte[]{0x55,0x03,(byte)0xE0,0x01,0x55,(byte)0xAA,0x00,0x04,0x00,0x00};
                    byte[] clearBatteryInfoReturn = null;
                    boolean clearBatteryInfoReturnType = false;
                    for(int i = 0 ; i < 100 ; i ++){
                        if(i % 20 == 0){
                            BatteryUpdateIntegration.getInstance().sendData(door , clearBatteryInfo);
                        }
                        sleep(50);
                        clearBatteryInfoReturn = tempArrays;
                        tempArrays = null;
                        if (clearBatteryInfoReturn != null && clearBatteryInfoReturn.length > 10 && clearBatteryInfoReturn[0] == 0x55) {
                            clearBatteryInfoReturnType = true;
                            break;
                        }
                    }
                    if(clearBatteryInfoReturnType){
                        updateInfoReturnListener.returnInfo(door , UpdateInfoReturnListener.UpdateTypeInfo.steps , "电池擦除数据成功!");
                    }else{
                        updateInfoReturnListener.returnInfo(door , UpdateInfoReturnListener.UpdateTypeInfo.error , "未收到电池信息反馈，电池升级结束!");
                        return;
                    }

                    //2_3.下发数据
                    sleep(1000);
                    int totalCount = bytes.length;
                    int totalCountIndex = totalCount / 64;
                    int totalCountLow = totalCount % 64;
                    if(totalCountLow != 0){
                        totalCountIndex = totalCountIndex + 1;
                    }
                    byte[] dataArrays = new byte[74];
                    for(int i = 0 ; i < totalCountIndex ; i++){

                        int index = dengBoBootLoaderIndex + (32 * i);
                        int lowIndex = index % 256;
                        int highIndex = index / 256;

                        dataArrays[0] = (byte)0x55;
                        dataArrays[1] = (byte)0x02;
                        dataArrays[2] = (byte)0x40;
                        dataArrays[3] = (byte)0x00;
                        dataArrays[4] = (byte)0x55;
                        dataArrays[5] = (byte)0xAA;
                        dataArrays[6] = (byte)lowIndex;
                        dataArrays[7] = (byte)highIndex;
                        dataArrays[8] = (byte)0x00;
                        dataArrays[9] = (byte)0x00;

                        for(int j = 0 ; j < dataArrays.length - 10 ; j++){
                            int itemIndex = i * 64 + j;
                            if(itemIndex < bytes.length){
                                dataArrays[j + 10] = bytes[itemIndex];
                            }else{

                            }
                        }
                        byte[] sendBatteryInfoReturn = null;
                        int sendBatteryInfoReturnType = -1;
                        BatteryUpdateIntegration.getInstance().sendData(door , dataArrays);
                        for(int j = 0 ; j < 100 ; j ++){
                            sleep(50);
                            sendBatteryInfoReturn = tempArrays;
                            tempArrays = null;
                            if (sendBatteryInfoReturn != null && sendBatteryInfoReturn.length > 10 && sendBatteryInfoReturn[0] == 0x55 && sendBatteryInfoReturn[10] == 0x01) {
                                sendBatteryInfoReturnType = 1;
                                break;
                            }
                            if (sendBatteryInfoReturn != null && sendBatteryInfoReturn.length > 10 && sendBatteryInfoReturn[0] == 0x55 && sendBatteryInfoReturn[10] == 0xfe) {
                                sendBatteryInfoReturnType = 2;
                                break;
                            }
                        }
                        if(sendBatteryInfoReturnType == 1){
                            updateInfoReturnListener.returnInfo(door , UpdateInfoReturnListener.UpdateTypeInfo.steps , "共"+totalCountIndex+"条数据,发送第"+i+"帧电池数据成功!");
                            returnRate(i, totalCountIndex);
                        }else if(sendBatteryInfoReturnType == -1){
                            updateInfoReturnListener.returnInfo(door , UpdateInfoReturnListener.UpdateTypeInfo.error , "未收到电池信息反馈，电池升级结束!");
                            return;
                        }else if(sendBatteryInfoReturnType == 2){
                            updateInfoReturnListener.returnInfo(door , UpdateInfoReturnListener.UpdateTypeInfo.error , "电池升级失败，升级结束!");
                            return;
                        }
                        sleep(10);
                    }

                    //2_4.CRC校验
                    sleep(1000);
                    updateInfoReturnListener.returnInfo(door , UpdateInfoReturnListener.UpdateTypeInfo.steps , "正在进行CRC校验!");
                    byte[] crcBatteryInfo = new byte[]{0x55,0x08,(byte)0x00,0x78,0x00,0x00,0x00,0x04,0x00,0x00};
                    byte[] crcBatteryInfoReturn = null;
                    boolean crcBatteryInfoReturnType = false;
                    for(int i = 0 ; i < 100 ; i ++){
                        if(i % 20 == 0){
                            BatteryUpdateIntegration.getInstance().sendData(door , crcBatteryInfo);
                        }
                        sleep(50);
                        crcBatteryInfoReturn = tempArrays;
                        tempArrays = null;
                        if (crcBatteryInfoReturn != null && crcBatteryInfoReturn.length > 10 && crcBatteryInfoReturn[0] == 0x55) {
                            crcBatteryInfoReturnType = true;
                            break;
                        }
                    }
                    if(crcBatteryInfoReturnType){
                        updateInfoReturnListener.returnInfo(door , UpdateInfoReturnListener.UpdateTypeInfo.steps , "电池CRC数据校验成功!");
                    }else{
                        updateInfoReturnListener.returnInfo(door , UpdateInfoReturnListener.UpdateTypeInfo.error , "未收到电池信息反馈，电池升级结束!");
                        return;
                    }

                    //2_5.设备重启
                    sleep(1000);
                    updateInfoReturnListener.returnInfo(door , UpdateInfoReturnListener.UpdateTypeInfo.steps , "正在进行设备重启!");
                    byte[] rebootBatteryInfo = new byte[]{0x55,0x09,(byte)0x00,0x00,0x55,(byte) 0xAA,0x00,0x00,0x00,0x00};
                    byte[] rebootBatteryInfoReturn = null;
                    boolean rebootBatteryInfoReturnType = false;
                    for(int i = 0 ; i < 100 ; i ++){
                        if(i % 20 == 0){
                            BatteryUpdateIntegration.getInstance().sendData(door , rebootBatteryInfo);
                        }
                        sleep(50);
                        rebootBatteryInfoReturn = tempArrays;
                        tempArrays = null;
                        if (rebootBatteryInfoReturn != null && rebootBatteryInfoReturn.length > 10 && rebootBatteryInfoReturn[0] == 0x55) {
                            rebootBatteryInfoReturnType = true;
                            break;
                        }
                    }
                    if(rebootBatteryInfoReturnType){
                        updateInfoReturnListener.returnInfo(door , UpdateInfoReturnListener.UpdateTypeInfo.success , "电池升级成功!");
                        return;
                    }else{
                        updateInfoReturnListener.returnInfo(door , UpdateInfoReturnListener.UpdateTypeInfo.error , "未收到电池信息反馈，电池升级结束!");
                        return;
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    updateInfoReturnListener.returnInfo(door , UpdateInfoReturnListener.UpdateTypeInfo.error , e.toString());
                    onDestroy();
                }

            }
        }.start();
     }


    //返回进度信息
    private void returnRate(long currentPregress, long totalPregress){
        updateInfoReturnListener.returnRate(currentPregress , totalPregress);
    }

    /**
     * 计算校验和
     *
     * @param data
     * @param start
     * @param end
     * @return
     */
    private final int calculateSum(byte[] data, int start, int end) {
        int sum = 0;
        if (data != null && start >= 0 && end >= start && end < data.length) {
            for (int s = start; s <= end; sum += data[s] & 0xFF, s++) ;
        }
        return sum;
    }

    /**
     * crc校验码计算
     *
     * @param data
     * @return
     */
    private final int crc32(byte[] data) {
        int crcVal = 0xFFFFFFFF;
        int k;
        for (byte item : data) {
            crcVal ^= item << 24;
            for (k = 0; k < 8; k++, crcVal = ((crcVal & 0x80000000) != 0) ? ((crcVal << 1) ^ 0x04C11DB7) : (crcVal << 1))
                ;
        }
        return crcVal;
    }

    public void onDestroy() {
        DaaSend.cancelHangOnAll();
        BatteryUpdateIntegration.getInstance().sendStop(updateInfoFormat.getDoor());
        BatteryUpdateIntegration.getInstance().deleteListener(logicListener);
        logicListener = null;
    }

}
