package client.halouhuandian.app15.hardWareConncetion.androidHard;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import client.halouhuandian.app15.CanAndSer;
import client.halouhuandian.app15.model.dao.fileSave.LocalLog;
import client.halouhuandian.app15.pub.RootCmd;
import client.halouhuandian.app15.pub.util.UtilPublic;

/**
 * 格瑞斯特 小安卓版 can和485收发实现类 目前市场就这个一个 先这么来
 * 以后为了适配更多android板 需搞成多态式
 */

public class SerialAndCanPortUtilsGeRui{

    //单例
    private static volatile SerialAndCanPortUtilsGeRui instance;
    private SerialAndCanPortUtilsGeRui(){};
    public static SerialAndCanPortUtilsGeRui getInstance(){
        if(instance == null){
            synchronized (SerialAndCanPortUtilsGeRui.class){
                if(instance == null){
                    instance = new SerialAndCanPortUtilsGeRui();
                }
            }
        }
        return instance;
    }

    //can从linux获取数据
    private FileInputStream cmFileInputStream;
    private FileOutputStream cmFileOutputStream;
    private OutputStream coutputStream;
    private int canReadState = 0;


    //数据分发
    public interface LogicListener{
        void returnData(Object object);
    }

    //需要分发的数据 注册列表
    private ArrayList<BaseDataRegister> baseDataRegisters = new ArrayList<>();

    public void addListener(BaseDataRegister baseDataRegister){
        if(baseDataRegisters.size() > 0){
            if(!baseDataRegisters.contains(baseDataRegister)){
                baseDataRegisters.add(baseDataRegister);
            }
        }else{
            baseDataRegisters.add(baseDataRegister);
        }
    }

    public void deleteListener(BaseDataRegister baseDataRegister){
        if(baseDataRegisters.size() > 0){
            if(baseDataRegisters.contains(baseDataRegister)){
                baseDataRegisters.remove(baseDataRegister);
            }
        }
    }

    //数据分发
    protected void sendData(byte[] rawData){
        //解析数据 返回的也是这个数据 省的二次解析了
        CanDataFormat canDataFormat = new CanDataFormat(rawData);
        long dataAddressLong = canDataFormat.getAddressByLong();
        //遍历注册列表 符合的数据就下放
        for(int i = 0 ; i < baseDataRegisters.size() ; i++){
            //获得接口返回
            BaseDataReturnListener baseDataReturnListener = baseDataRegisters.get(i).getBaseDataReturnListener();
            //如果注册的值是在某个范围内的数据
            ArrayList<long[]> rangeList = baseDataRegisters.get(i).getRangeList();
            if(rangeList!= null){
                int rangListSize = rangeList.size();
                if(rangListSize > 0){
                    for(int j = 0 ; j < rangeList.size() ; j++){
                        long[] tempArray = rangeList.get(j);
                        //传了一个空 或者 长度不匹配 的长度域 一律无视掉
                        if(tempArray == null || tempArray.length < 2){
                            return;
                        }
                        if(dataAddressLong >= tempArray[0] && dataAddressLong <= tempArray[1]){
                            baseDataReturnListener.returnData(canDataFormat);
                        }
                    }
                }
            }
        }
    }

    /**
     * 打开串口
     *
     * @return serialPort串口对象
     */
    public void init(){
        //can初始化
        RootCmd.execRootCmd("ip link set can0 down");
        RootCmd.execRootCmd("ip link set can0 type can loopback off triple-sampling on");
        RootCmd.execRootCmd("ip link set can0 type can bitrate 125000 loopback off triple-sampling on");
        RootCmd.execRootCmd("ip link set can0 up");
        RootCmd.execRootCmd("echo  4096 > /sys/class/net/can0/tx_queue_len");



        FileDescriptor cFd = CanAndSer.openCan();
        cmFileInputStream = new FileInputStream(cFd);
        cmFileOutputStream = new FileOutputStream(cFd);
        coutputStream = cmFileOutputStream;
        new ReadCanThread().start(); //开始线程监控是否有数据要接收
    }



    /**
     * 单开一线程，来读数据
     */
    private class ReadCanThread extends Thread {
        @Override
        public void run() {
            super.run();
            //判断进程是否在运行，更安全的结束进程
            while (canReadState == 0) {
                byte[] buffer = new byte[16];
                try {
                    int size = cmFileInputStream.read(buffer);
                    if (size > 0) {
                        sendData(buffer);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LocalLog.getInstance().writeLog(e.toString() , SerialAndCanPortUtilsGeRui.class);
                }
            }

        }
    }


    /**
     * 写入CAN数据
     */
    public void canSendOrder(String str, byte[] data) {
        try {

            int a_int = Integer.parseInt(str.substring(6, 8), 16);
            int b_int = Integer.parseInt(str.substring(4, 6), 16);
            int c_int = Integer.parseInt(str.substring(2, 4), 16);
            int d_int = Integer.parseInt(str.substring(0, 2), 16);

            byte[] sendData = new byte[16];
            Arrays.fill(sendData, (byte) 0);
            sendData[0] = (byte) a_int;
            sendData[1] = (byte) b_int;
            sendData[2] = (byte) c_int;
            sendData[3] = (byte) d_int;
            if(data == null){
                sendData[4] = 0;
            }else{
                sendData[4] = (byte) data.length;
            }
            sendData[5] = (byte) 0x00;
            sendData[6] = (byte) 0x00;
            sendData[7] = (byte) 0x00;
            if(data == null){
                for (int i = 0; i < 8; i++) {
                    sendData[8 + i] = 0;
                }
            }else{
                for (int i = 0; i < data.length; i++) {
                    sendData[8 + i] = data[i];
                }
            }
            if (sendData.length > 0) {
                coutputStream.write(sendData);
                coutputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            LocalLog.getInstance().writeLog(e.toString() , SerialAndCanPortUtilsGeRui.class);
        }
    }

    /**
     * 写入CAN数据
     */
    public void canSendOrder(CanDataFormat canDataFormat){
        canSendOrder(canDataFormat.getAddressByStr(), canDataFormat.getData());
    }

    public void onDestroy() {
        cmFileInputStream = null;
        cmFileOutputStream = null;
        coutputStream = null;
        canReadState = 1;
    }
}
