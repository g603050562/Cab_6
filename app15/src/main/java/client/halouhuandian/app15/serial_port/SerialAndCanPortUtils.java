package client.halouhuandian.app15.serial_port;

import android.util.Log;

import com.hellohuandian.pubfunction.Unit.LogUtil;
import com.hellohuandian.pubfunction.Unit.PubFunction;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import client.halouhuandian.app15.A_RootCmd;
import client.halouhuandian.app15.CanAndSer;

/**
 * Created by WangChaowei on 2017/12/7.
 */

public class SerialAndCanPortUtils {


    private FileInputStream cmFileInputStream;
    private FileOutputStream cmFileOutputStream;
    private OutputStream coutputStream;
    private FileInputStream smFileInputStream;
    private FileOutputStream smFileOutputStream;
    private OutputStream soutputStream;


    private IFSerialPortResultListener serLinstener;
    private IFCanBusResultListener canLinstener;

    private int canReadState = 0;
    private int serReadState = 0;


    /**
     * 打开串口
     *
     * @return serialPort串口对象
     */
    public void openSerialPort(IFSerialPortResultListener serLinstener) {
        //set初始化
        this.serLinstener = serLinstener;
        FileDescriptor sFd = CanAndSer.openSer(new File("/dev/ttyS4").getAbsolutePath(), 9600);
        smFileInputStream = new FileInputStream(sFd);
        smFileOutputStream = new FileOutputStream(sFd);
        soutputStream = getSerOutputStream();
        new ReadSerThread().start(); //开始线程监控是否有数据要接收
    }

    public void setSerialPortListener(IFSerialPortResultListener serLinstener) {
        this.serLinstener = serLinstener;
    }


    /**
     * 打开Can
     */
    public void openCanPort(IFCanBusResultListener canLinstener) {
        //can初始化
        this.canLinstener = canLinstener;
        A_RootCmd.execRootCmd("echo 4096 > /sys/class/net/can0/tx_queue_len");
        A_RootCmd.execRootCmd("ip link set can0 down");
        A_RootCmd.execRootCmd("ip link set can0 type can loopback off triple-sampling on");
        A_RootCmd.execRootCmd("ip link set can0 type can bitrate 125000 loopback off triple-sampling on");
        A_RootCmd.execRootCmd("ip link set can0 up");
        FileDescriptor cFd = CanAndSer.openCan();
        cmFileInputStream = new FileInputStream(cFd);
        cmFileOutputStream = new FileOutputStream(cFd);
        coutputStream = getCanOutputStream();
        new ReadCanThread().start(); //开始线程监控是否有数据要接收
    }

    public void setCanPortListener(IFCanBusResultListener canLinstener) {
        this.canLinstener = canLinstener;
    }

    /**
     * 单开一线程，来读数据
     */

    private int send_time = 160;
    private byte[] str = new byte[]{};

    private class ReadSerThread extends Thread {
        @Override
        public void run() {
            super.run();
            //判断进程是否在运行，更安全的结束进程
            sendMessage.start();
            while (serReadState == 0) {
                //64   1024
                send_time = 160;
                byte[] buffer = new byte[64];
                try {
                    int size = smFileInputStream.read(buffer);
                    if (size > 0) {
                        byte[] temp = new byte[size];
                        System.arraycopy(buffer, 0, temp, 0, size);
                        str = arrayJoin(str, temp);
                    }
                } catch (IOException e) {
                    Log.e("TAR", "run: 数据读取异常：" + e.toString());
                }
            }
        }
    }

    ;

    private Thread sendMessage = new Thread() {

        @Override
        public void run() {
            while (serReadState == 0) {
                try {

                    sleep(20);

                    if (send_time <= 0) {
                        if (str.length > 0) {
                            if (serLinstener != null) {
                                serLinstener.onSerialPortResult(str);
                            }
                            send_time = -1;
                            str = new byte[]{};
                        }
                    } else if (send_time > 0) {
                        send_time = send_time - 20;
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };


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
                        if (canLinstener != null) {
                            canLinstener.onCanBusResult(buffer);
                        }
                    }
                } catch (IOException e) {
                    System.out.println(e.toString());
                }
            }

        }
    }

    public void serSendOrder(byte[] data) {
        System.out.println("SER 下发：" + PubFunction.ByteArrToHex(data));
        if (serLinstener == null) {
            System.out.println("SER 下发失败：该串口通信已被占用，请重新初始化串口类");
            return;
        }
        try {
            if (data.length > 0) {
                soutputStream.write(data);
                soutputStream.flush();
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    public void canSendOrder(byte[] sendData) {
        if (sendData != null && sendData.length == 16) {
            try {
                coutputStream.write(sendData);
                coutputStream.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void canSendOrder(String str, byte[] data) {
        if (canLinstener == null) {
            System.out.println(" 下发失败：该串口通信已被占用，请重新初始化串口类");
            return;
        }
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
            sendData[4] = (byte) data.length;
            sendData[5] = (byte) 0x00;
            sendData[6] = (byte) 0x00;
            sendData[7] = (byte) 0x00;

            for (int i = 0; i < data.length; i++) {
                sendData[8 + i] = data[i];
            }

            if (sendData.length > 0) {
                coutputStream.write(sendData);
                coutputStream.flush();
            }

        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    public void onDestroy() {
        canReadState = 1;
        serReadState = 1;
        cmFileInputStream = null;
        cmFileOutputStream = null;
        coutputStream = null;
        smFileInputStream = null;
        smFileOutputStream = null;
        soutputStream = null;
    }


    public byte[] arrayJoin(byte[] a, byte[] b) {
        byte[] arr = new byte[a.length + b.length];//开辟新数组长度为两数组之和
        for (int i = 0; i < a.length; i++) {//拷贝a数组到目标数组arr
            arr[i] = a[i];
        }
        for (int j = 0; j < b.length; j++) {//拷贝b数组到目标数组arr
            arr[a.length + j] = b[j];
        }
        return arr;
    }

    public OutputStream getCanOutputStream() {
        return cmFileOutputStream;
    }

    public OutputStream getSerOutputStream() {
        return smFileOutputStream;
    }

    public static String ByteArrToHex(byte[] inBytArr) {
        StringBuilder strBuilder = new StringBuilder();
        for (byte valueOf : inBytArr) {
            strBuilder.append(Byte2Hex(Byte.valueOf(valueOf)));
            strBuilder.append(" ");
        }
        return strBuilder.toString();
    }

    public static String Byte2Hex(Byte inByte) {
        return String.format("%02x", new Object[]{inByte}).toUpperCase();
    }

}
