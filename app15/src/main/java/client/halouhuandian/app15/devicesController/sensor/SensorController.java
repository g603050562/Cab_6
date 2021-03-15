package client.halouhuandian.app15.devicesController.sensor;

import android.support.v4.util.Consumer;

import client.halouhuandian.app15.MyApplication;
import client.halouhuandian.app15.StringFormatHelper;

/**
 * Author:      Lee Yeung
 * Create Date: 2020/3/27
 * Description: 传感器控制器，处理解析分发传感器数据
 * <p>
 * 关于环境板CAN通讯协议说明
 * 1、环境板的扩展帧ID为：18 B0 65 66；
 * 2、每帧8个数据字节中的第1个字节是是拼接数据时用到的计数器；
 * 其中第0个字节0xB0代表的是地址为0xB0的从机；
 * 其中第1个字节0x02代表的是此帧是自动上报功能；（详见附表1）
 * 其中第2个字节0x29代表的是有效数据是41个；
 * 其中第3个字节0x00代表环境板软件版本号;
 * 其中第4个字至第7个字节是水位数据（附表3会给出转换公式）;
 * 其中第8个字节至11个字节是温度1数据；（附表3会给出转换公式）；
 * 其中第12个字节至15个字节是温度2数据；（附表3会给出转换公式）；
 * 其中第16个字节至19个字节是烟感数据；（附表3会给出转换公式）；
 * 其中第20个字节至39个字节是电表数据；（附表3会给出转换公式）；
 * 其中第20个字节至21个字节是电表电压数据；（附表3会给出转换公式）；
 * 其中第22个字节至23个字节是电表电流数据；（附表3会给出转换公式）；
 * 其中第24个字节至25个字节是电表功率数据；（附表3会给出转换公式）；
 * 其中第26个字节至29个字节是电表有功总电能数据；（附表3会给出转换公式）；
 * 其中第30个字节至31个字节是电表功率因数数据；（附表3会给出转换公式）；
 * 其中第32个字节至35个字节是电表二氧化碳排量数据；（附表3会给出转换公式）；
 * 其中第36个字节至37个字节是电表温度数据；（协议上标明保留）；
 * 其中第38个字节至39个字节是电表频率数据；（附表3会给出转换公式）；
 */
public final class SensorController implements MyApplication.IFResultAppLinstener {
    private static final SensorController SENSOR_CONTROLLER = new SensorController();

    private byte[] data;
    private int len;
    private int position;
    private final int start = 9;
    private final SensorDataBean sensorDataBean = new SensorDataBean();
    private final SensorParser sensorParser = new SensorParser();
    private Consumer<SensorDataBean> consumer;
    private short orderOffset;

    private SensorController() {
    }

    public static SensorController getInstance() {
        return SENSOR_CONTROLLER;
    }

    @Override
    public void onCanResultApp(byte[] canData) {
        if (canData != null && canData.length == 16) {
            //匹配超越传感器采集板
            if ((canData[3] & 0xFF) == 0x98 && (canData[2] & 0xFF) == 0xB0 && (canData[1] & 0xFF) == 0x65 && (canData[0] & 0xFF) == 0x66) {
                final short sn = (short) (canData[8] & 0xFF);
                //判断首帧,进行初始化
                if (sn == 0x10) {
                    len = (canData[11] & 0xFF);
                    if (len > 0 && (data == null || data.length != len)) {
                        data = new byte[len];
                    }
                    orderOffset = sn;
                    position = 0;
                }

                //封装数据数组
                if ((sn - 1 == orderOffset || orderOffset == 0x10) && data != null && data.length == len) {
                    orderOffset = sn;
                    int copyLen = canData.length - start;
                    if (position + copyLen > data.length) {
                        //修正复制长度
                        copyLen = data.length % copyLen;
                    }

                    System.arraycopy(canData, start, data, position, copyLen);
                    position += copyLen;
                    //判断数据是否封装完毕
                    if (position >= data.length) {
                        position = 0;
                        parse();
                        update();
                    }
                } else {
                    position = 0;
                    orderOffset = 0;
                }
            }
        }
    }

    @Override
    public void onSerialResultApp(byte[] serData) {

    }

    private void parse() {
        sensorParser.parse(sensorDataBean, data);
    }

    private void update() {
        if (consumer != null) {
            consumer.accept(sensorDataBean);
        }
    }

    public SensorDataBean getSensorDataBean() {
        return sensorDataBean;
    }

    public void setUpdateConsumer(Consumer<SensorDataBean> consumer) {
        this.consumer = consumer;
    }
}
