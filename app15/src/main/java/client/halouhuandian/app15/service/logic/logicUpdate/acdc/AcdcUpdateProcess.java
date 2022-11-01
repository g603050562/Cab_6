package client.halouhuandian.app15.service.logic.logicUpdate.acdc;

import com.google.zxing.common.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;

import client.halouhuandian.app15.hardWareConncetion.daa.DaaController;
import client.halouhuandian.app15.hardWareConncetion.daa.DaaSend;
import client.halouhuandian.app15.hardWareConncetion.daa.mode.DaaDataFormat;
import client.halouhuandian.app15.model.dao.fileSave.LocalLog;
import client.halouhuandian.app15.pub.BaseDataDistribution;
import client.halouhuandian.app15.service.logic.logicUpdate.UpdateInfoFormat;
import client.halouhuandian.app15.service.logic.logicUpdate.UpdateInfoReturnListener;

public class AcdcUpdateProcess {

    private Thread thread;
    private int threadWhileCode = 500;
    private long sendCount = 0;
    private int sendIndex = 0;
    private int updateType = -1;
    private UpdateInfoFormat updateInfoFormat;
    private UpdateInfoReturnListener updateInfoReturnListener;
    private AcdcUpdateDataFormat acdcUpdateDataFormat = null;
    private BaseDataDistribution.LogicListener logicListener;

    public AcdcUpdateProcess(UpdateInfoFormat updateInfoFormat, UpdateInfoReturnListener updateInfoReturnListener) {
        this.updateInfoFormat = updateInfoFormat;
        this.updateInfoReturnListener = updateInfoReturnListener;
        AcdcUpdateIntegration.getInstance().addListener(logicListener = new BaseDataDistribution.LogicListener() {
            @Override
            public void returnData(Object object) {
                acdcUpdateDataFormat = (AcdcUpdateDataFormat) object;
            }
        });
    }

    public void onStart() {

        final int door = updateInfoFormat.getDoor();
        final String filePath = updateInfoFormat.getFilePath();
        final String type = updateInfoFormat.getType();

        DaaDataFormat daaDataFormat = DaaController.getInstance().getDaaDataFormat();
        String tarVersionStr = daaDataFormat.getAcdcInfoByStateFormat(door - 1).getAcdcHardWareVersion();
        if(!tarVersionStr.equals("") && isNumeric(tarVersionStr)){
            int tarVersion = Integer.parseInt(tarVersionStr);
            if(tarVersion >= 50){
                updateType = 1;
            }else{
                updateType = 2;
            }
        }else{
            updateType = -1;
        }
        String cabType = "";
        if(updateType == 1){
            cabType = "7代电柜ac升级";
        }else if(updateType == 2){
            cabType = "6代电柜ac升级";
        }else{
            cabType = "未知电柜ac升级";
        }
        updateInfoReturnListener.returnInfo(door, UpdateInfoReturnListener.UpdateTypeInfo.steps, "升级类型 - " + cabType +" - 硬件版本 - " + tarVersionStr);
        updateInfoReturnListener.returnInfo(door, UpdateInfoReturnListener.UpdateTypeInfo.steps, "开始进行"+door+"号舱acdc升级");

        if (thread == null) {
            thread = new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        updateInfoReturnListener.returnInfo(door, UpdateInfoReturnListener.UpdateTypeInfo.steps, "开始进行"+door+"号舱acdc升级");
                        //1.验证升级文件完整性
                        File file = new File(filePath);
                        if (!(file != null && file.exists())) {
                            updateInfoReturnListener.returnInfo(door, UpdateInfoReturnListener.UpdateTypeInfo.error, "升级文件不存在");
                            onDestroy();
                            System.out.println("update - 升级文件不存在 - " + filePath);
                            return;
                        }
                        InputStream inputStream = new FileInputStream(file);
                        int len = inputStream.available();
                        if (len <= 0) {
                            updateInfoReturnListener.returnInfo(door, UpdateInfoReturnListener.UpdateTypeInfo.error, "数据长度为0!");
                            onDestroy();
                            System.out.println("update - 数据长度为0");
                            return;
                        }
                        //2.升级文件序列化
                        final byte[] DATA = new byte[len];
                        inputStream.read(DATA);
                        inputStream.close();
                        updateInfoReturnListener.returnInfo(door, UpdateInfoReturnListener.UpdateTypeInfo.steps, "正在文件序列化！");

                        sleep(2 * 1000);

                        //3.下发acdc关机
            //                        DaaSend.closeAcdc(door + 80);
            //                        sleep(20);
            //                        DaaSend.closeAcdc(door + 81);
            //                        sleep(20);
            //                        DaaSend.closeAcdc(door + 82);
                        sleep(1000);
                        updateInfoReturnListener.returnInfo(door, UpdateInfoReturnListener.UpdateTypeInfo.steps, "正在下发目标dc关闭！");
                        //4.让所有acdc挂起 释放总线资源
                        DaaSend.hangOnAll();
                        updateInfoReturnListener.returnInfo(door, UpdateInfoReturnListener.UpdateTypeInfo.steps, "正在下发所有dc挂起！");
                        //5.下发转跳到boot命令
                        DaaSend.turnToBootLoader(door + 80);
                        updateInfoReturnListener.returnInfo(door, UpdateInfoReturnListener.UpdateTypeInfo.steps, "正在下发dc从App转跳到BootLoader！");
                        //7_1.开始检测有没有返回命令
                        updateInfoReturnListener.returnInfo(door, UpdateInfoReturnListener.UpdateTypeInfo.steps, "正在等待目标dc返回值！");
                        //6.下发连接帧 周期50ms 超过200ms时超时 转跳到App层
                        byte[] sendData = null;
                        int state_1 = -1;
                        for (int i = 0; i < 10 * 15; i++) {
                            updateInfoReturnListener.returnInfo(door, UpdateInfoReturnListener.UpdateTypeInfo.steps, "正在下发周期连接帧！");
                            if(updateType == 1){
                                DaaSend.lifeConnection(door + 80);
                            }else{
                                DaaSend.lifeConnection(door);
                            }
                            sleep(100);
                            if (acdcUpdateDataFormat!= null && acdcUpdateDataFormat.getType() == AcdcUpdateDataFormat.AcdcUpdateType.liveConnection) {
                                state_1 = 1;
                                sendData = acdcUpdateDataFormat.getData();
                                acdcUpdateDataFormat = null;
                                break;
                            }
                        }
                        if (state_1 == -1) {
                            //7_2.开始检测有没有返回命令 - 没有接收的话直接转跳到app层
                            DaaSend.cancelHangOnAll();
                            updateInfoReturnListener.returnInfo(door, UpdateInfoReturnListener.UpdateTypeInfo.error, "没有建立升级连接，升级结束！");
                            onDestroy();
                            return;
                        }
                        //7_3.开始检测有没有返回命令 - 有就证明进入bootloader成功了
                        updateInfoReturnListener.returnInfo(door, UpdateInfoReturnListener.UpdateTypeInfo.steps, "转跳到BootLoader成功！");
                        //8_1.开始下发 确定建立连接
                        sleep(100);
                        if(updateType == 1){
                            DaaSend.confirmLifeConnection(door + 80,sendData);
                        }else{
                            DaaSend.confirmLifeConnection(door,sendData);
                        }

                        updateInfoReturnListener.returnInfo(door, UpdateInfoReturnListener.UpdateTypeInfo.steps, "正在下发dc确认建立连接！");
                        //9.开始下发数据
                        while (threadWhileCode > 0) {
                            if (acdcUpdateDataFormat == null) {
                                threadWhileCode --;
                                sleep(20);
                            } else {
                                threadWhileCode = 500;
                                byte[] canData = acdcUpdateDataFormat.getData();
                                int state = (0xff & canData[0]) * 256 + 0xff & canData[1];
                                if(state == 2){
                                    threadWhileCode = -100;
                                    break;
                                }else if(state == 1){
                                    updateInfoReturnListener.returnInfo(door, UpdateInfoReturnListener.UpdateTypeInfo.steps, "数据丢失！");
                                }else if(state == 3){
                                    updateInfoReturnListener.returnInfo(door, UpdateInfoReturnListener.UpdateTypeInfo.steps, "非法的固件程序！");
                                    threadWhileCode = 0;
                                    break;
                                }else if(state == 4){
                                    updateInfoReturnListener.returnInfo(door, UpdateInfoReturnListener.UpdateTypeInfo.steps, "设备写固件丢失！");
                                    threadWhileCode = 0;
                                    break;
                                }
                                int startAddress = ((0xff & canData[2]) * 256 * 256 * 256) + ((0xff & canData[3]) * 256 * 256) + ((0xff & canData[4]) * 256) + (0xff & canData[5]);
                                int length = ((0xff & canData[6]) * 256) + (0xff & canData[7]);
                                int orderCountRemainder = length % 8;
                                int orderCount = length / 8;
                                if (orderCountRemainder != 0) {
                                    orderCount++;
                                }
                                if (acdcUpdateDataFormat.getType() == AcdcUpdateDataFormat.AcdcUpdateType.requireHandData || acdcUpdateDataFormat.getType() == AcdcUpdateDataFormat.AcdcUpdateType.requireBodyData) {
                                    for (int i = 0; i < orderCount; i++) {
                                        sleep(5);
                                        byte[] bytes = null;
                                        int top = 2512 + i;
                                        if (i == orderCount - 1) {
                                            sendCount = sendCount + orderCountRemainder;
                                            if(orderCountRemainder == 0){
                                                orderCountRemainder = 8;
                                            }
                                            bytes = Arrays.copyOfRange(DATA, startAddress + i * 8, startAddress + i * 8 + orderCountRemainder);
                                        } else {
                                            sendCount = sendCount + 8;
                                            bytes = Arrays.copyOfRange(DATA, startAddress + i * 8, startAddress + i * 8 + 8);
                                        }
                                        sendIndex = sendIndex + 1;
                                        updateInfoReturnListener.returnInfo(door, UpdateInfoReturnListener.UpdateTypeInfo.steps, "正在发送第"+sendIndex+"帧数据！");
                                        updateInfoReturnListener.returnRate(sendCount,DATA.length);
                                        threadWhileCode = 500;

                                        if(updateType == 1){
                                            DaaSend.sendUpdateData(top , door + 80 , bytes);
                                        }else{
                                            DaaSend.sendUpdateData(top , door , bytes);
                                        }

                                    }
                                    acdcUpdateDataFormat = null;
                                }

                            }
                        }
                        //10.后续处理
                        updateInfoReturnListener.returnInfo(door, UpdateInfoReturnListener.UpdateTypeInfo.steps, "正在解除acdc挂起！");
                        if(threadWhileCode <= 0 && threadWhileCode > -100){
                            updateInfoReturnListener.returnInfo(door, UpdateInfoReturnListener.UpdateTypeInfo.error, door+"号acdc升级失败！");
                            onDestroy();
                        }else if(threadWhileCode == -100){
                            updateInfoReturnListener.returnInfo(door, UpdateInfoReturnListener.UpdateTypeInfo.success, door+"号acdc升级成功！");
                            onDestroy();
                        }
                        acdcUpdateDataFormat = null;

                    } catch (Exception e) {
                        LocalLog.getInstance().writeLog("update - error - " + e.toString() , AcdcUpdateProcess.class);
                        System.out.println("update - error - " + e.toString());
                    }
                }
            };
            thread.start();
        }

    }

    public void onDestroy() {
        sendCount = 0;
        sendIndex = 0;
        AcdcUpdateIntegration.getInstance().deleteListener(logicListener);
    }

    public static boolean isNumeric(String str){
        for (int i = str.length();--i>=0;){
            if (!Character.isDigit(str.charAt(i))){
                return false;
            }
        }
        return true;
    }

}
