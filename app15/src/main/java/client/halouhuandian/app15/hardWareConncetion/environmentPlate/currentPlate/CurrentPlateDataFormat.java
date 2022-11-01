package client.halouhuandian.app15.hardWareConncetion.environmentPlate.currentPlate;

import java.nio.DoubleBuffer;
import java.text.DecimalFormat;

import client.halouhuandian.app15.pub.util.UtilPublic;

public class CurrentPlateDataFormat {

    private String[] strings_1 = new String[]{"待机", "故障", "告警", "禁止"};
    private String[] strings_2 = new String[]{"电流超过阈值", "继电器异常", "can通讯异常", "电流采样异常", "电压采样异常", "继电器开", "", ""};

    //电流板状态
    private String status = "";
    //输出电压
    private double outPutVoltage = -1;
    //输出电流
    private double outPutElectric = -1;
    //输出告警位
    private String outPutWarning = "";
    //软件版本号
    private int softwareVersion = -1;
    //硬件版本号
    private int hardwareVersion = -1;
    //时间
    private long dataTime = -1;

    public CurrentPlateDataFormat() {
    }

    public CurrentPlateDataFormat(byte[] data) {

        for (int i = 0; i < strings_1.length; i++) {
            if (i == data[0]) {
                status = strings_1[i];
            }
        }

        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        outPutVoltage = Double.valueOf(decimalFormat.format((double) (data[1] & 0xff + (data[2] & 0xff) * 256) / 100));
        outPutElectric = Double.valueOf(decimalFormat.format((double) (data[3] & 0xff + (data[4] & 0xff) * 256) / 100));

        String str = UtilPublic.int2Binary(data[5]);
        for (int i = 0; i < str.length(); i++) {
            String item = str.substring(i, i + 1);
            if (item.equals("1")) {
                outPutWarning = outPutWarning + strings_2[i] + "/";
            }
        }

        softwareVersion = data[6] & 0xff;
        hardwareVersion = data[7] & 0xff;

        dataTime = System.currentTimeMillis();
    }

    public String getStatus() {
        return status;
    }

    public double getOutPutVoltage() {
        return outPutVoltage;
    }

    public double getOutPutElectric() {
        return outPutElectric;
    }

    public String getOutPutWarning() {
        return outPutWarning;
    }

    public int getSoftwareVersion() {
        return softwareVersion;
    }

    public int getHardwareVersion() {
        return hardwareVersion;
    }

    public long getDataTime() {
        return dataTime;
    }


    public void setStrings_1(String[] strings_1) {
        this.strings_1 = strings_1;
    }

    public void setStrings_2(String[] strings_2) {
        this.strings_2 = strings_2;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setOutPutVoltage(int outPutVoltage) {
        this.outPutVoltage = outPutVoltage;
    }

    public void setOutPutElectric(int outPutElectric) {
        this.outPutElectric = outPutElectric;
    }

    public void setOutPutWarning(String outPutWarning) {
        this.outPutWarning = outPutWarning;
    }

    public void setSoftwareVersion(int softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public void setHardwareVersion(int hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    public void setDataTime(long dataTime) {
        this.dataTime = dataTime;
    }


    @Override
    public String toString() {
        return "CurrentPlateDataFormat{" +
                "status='" + status + '\'' +
                ", outPutVoltage=" + outPutVoltage +
                ", outPutElectric=" + outPutElectric +
                ", outPutWarning='" + outPutWarning + '\'' +
                ", softwareVersion=" + softwareVersion +
                ", hardwareVersion=" + hardwareVersion +
                ", data=" + dataTime +
                '}';
    }
}
