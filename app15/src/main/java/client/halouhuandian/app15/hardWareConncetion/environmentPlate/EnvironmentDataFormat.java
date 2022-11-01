package client.halouhuandian.app15.hardWareConncetion.environmentPlate;

import client.halouhuandian.app15.hardWareConncetion.environmentPlate.currentPlate.CurrentPlateDataFormat;
import client.halouhuandian.app15.model.dao.sharedPreferences.CabInfoSp;
import client.halouhuandian.app15.pub.util.UtilPublic;

public class EnvironmentDataFormat {

    private String[] str_1 = new String[]{"初始化","待机","预警上传", "电源继电器断开"};

    //环境板地址
    private int address = -1;
    //功能识别码
    private int functionState = -1;
    //帧长
    private int length = -1;
    //版本
    private int version = -1;
    //水位1数据
    private double water_1 = -1;
    //温度3数据
    private double temperature_3 = -60;
    //温度2数据
    private double temperature_2 = -60;
    //烟感数据
    private double smoke = -1;

    //电表电压
    private double electricityMeterVoltage = -1;
    //电表电流
    private double electricityMeterElectric = -1;
    //电表有用功率
    private double usefulPower = -1;
    //电表用功总电能
    private double usefulTotalElectricEnergy = -1;
    //电表功率因数
    private double powerFactor = -1;

    //二氧化碳
    private double CO2 = -1;
    //电表温度
    private int electricityMeterTemperature = -1;
    //电表频率
    private double electricityMeterFrequency = -1;
    //硬件状态
    private int hardwareStatus = -1;

    //电流板电流
    private double currentPlateElectric = -1;
    //电流板电压
    private double currentPlateVoltage = -1;
    //电流板电流预警
    private double currentPlateElectricWarning = -1;
    //电流板预警信息
    private String currentPlateWarningInfo = "";
    //超限时间
    private int timeOutTime = -1;
    //恢复时间
    private int recoveryTime = -1;

    //运行时间
    private String runningTime = "";
    private int runningTimeState = 0;

    //电流板硬件版本
    private int currentPlateHardVersion = -1;
    //电流板软件版本
    private int currentPlateSoftVersion = -1;

    //温度1数据
    private double temperature_1 = -60;
    //水位2数据
    private double water_2 = -1;

    //数据时间
    private double dataTime = -1;


    //推杆电源接通情况
    private int putterPowerStatus = -1; // 0 - 接通   1 - 断开
    //按键1情况
    private int buttonStatus_1 = -1;// 0 - 接通   1 - 断开
    //按键2情况
    private int buttonStatus_2 = -1;// 0 - 接通   1 - 断开
    //备用电源1情况
    private int standbyPowerStatus_1  = -1;// 0 - 断开   1 - 接通
    //备用电源2情况
    private int standbyPowerStatus_2  = -1;// 0 - 断开   1 - 接通
    //风扇1情况
    private int fanStatus_1  = -1;// 0 - 接通   1 - 断开
    //风扇2情况
    private int fanStatus_2  = -1;// 0 - 接通   1 - 断开
    //安卓电源接通情况
    private int androidPower = -1;// 0 - 接通   1 - 断开


    public EnvironmentDataFormat(){

    }

    public void baseDataFormat(byte[] bytes){

        if(bytes == null || bytes.length != 63){
            return;
        }
        int[] data = bytesToInts(bytes);
        address = data[0];
        functionState = data[1] ;
        length = data[2];
        version = data[3];
        water_1 = (double)(data[4]  * 256 * 256 * 256 + data[5]  * 256 * 256 + data[6]  * 256 + data[7] ) / 10000;
        temperature_3 = EnvironmentTemperatureTable.queryTemperature((data[8]  * 256 * 256 * 256 + data[9]  * 256 * 256 + data[10]  * 256 + data[11] ));
        temperature_2 = EnvironmentTemperatureTable.queryTemperature((data[12]  * 256 * 256 * 256 + data[13]  * 256 * 256 + data[14]  * 256 + data[15] ));
        smoke = (double)(data[16]  * 256 * 256 * 256 + data[17]  * 256 * 256 + data[18]  * 256 + data[19] ) / 10000;
        electricityMeterVoltage = (double)(data[20]  * 256 + data[21] ) / 100;
        electricityMeterElectric = (double)(data[22]  * 256 + data[23] ) / 100;

        if(electricityMeterVoltage > 100 && electricityMeterVoltage < 300){
            usefulPower = data[24]  * 256 + data[25] ;
            usefulTotalElectricEnergy = (double)(data[26]  * 256 * 256 * 256 + data[27]  * 256 * 256 + data[28]  * 256 + data[29] ) / 3200;
            powerFactor = (double)(data[30]  * 256 + data[31] ) / 1000;
            CO2 = (double)(data[32]  * 256 * 256 * 256 + data[33]  * 256 * 256 + data[34]  * 256 + data[35] ) / 1000;
            electricityMeterTemperature = data[36]  * 256 + data[37] ;
            electricityMeterFrequency = (double)(data[38]  * 256 + data[39] ) / 100;
        }else{
            int usefulPowerInt = data[32]  * 256 * 256 * 256 + data[33]  * 256 * 256 + data[34]  * 256 + data[35];
            usefulPower = Float.intBitsToFloat(usefulPowerInt);
            int usefulTotalElectricEnergyInt = data[26]  * 256 * 256 * 256 + data[27]  * 256 * 256 + data[28]  * 256 + data[29];
            usefulTotalElectricEnergy =  Float.intBitsToFloat(usefulTotalElectricEnergyInt);
            powerFactor = (double)(data[30]  * 256 + data[31] ) / 1000;
            CO2 = 0;
            electricityMeterTemperature = data[36]  * 256 + data[37] ;
            electricityMeterFrequency = (double)(data[38]  * 256 + data[39] ) / 100;
        }

        hardwareStatus = data[40] ;

        temperature_1 = EnvironmentTemperatureTable.queryTemperature((data[45]  * 256 * 256 * 256 + data[46]  * 256 * 256 + data[47]  * 256 + data[48] ));
        water_2 =  (double)(data[49]  * 256 * 256 * 256 + data[50]  * 256 * 256 + data[51]  * 256 + data[52] ) / 10000;

        if(runningTimeState == 0){
            runningTime = str_1[data[57]];
            currentPlateElectric = (double)(data[41]  * 256 + data[42] ) / 100;
            currentPlateVoltage = (double)(data[43]  * 256 + data[44] ) / 100;
            currentPlateElectricWarning = (double)(data[53]  * 256 + data[54] ) / 100;
            timeOutTime = data[55]  * 10;
            recoveryTime = data[56]  * 10;
        }

        dataTime = System.currentTimeMillis();

        String str = UtilPublic.int2Binary(hardwareStatus);
        putterPowerStatus = str.substring(0,1).equals("1") ? 1:0;
        buttonStatus_1 = str.substring(2,3).equals("1") ? 1:0;
        buttonStatus_2 = str.substring(1,2).equals("1") ? 1:0;
        standbyPowerStatus_1 = str.substring(4,5).equals("1") ? 1:0;
        standbyPowerStatus_2 = str.substring(3,4).equals("1") ? 1:0;
        fanStatus_1 = str.substring(6,7).equals("1") ? 1:0;
        fanStatus_2 = str.substring(5,6).equals("1") ? 1:0;
        putterPowerStatus = str.substring(7,8).equals("1") ? 1:0;
    }

    public void extendDataFormat(CurrentPlateDataFormat currentPlateDataFormat){
        runningTimeState = 1;
        currentPlateElectric = currentPlateDataFormat.getOutPutElectric();
        currentPlateVoltage = currentPlateDataFormat.getOutPutVoltage();
        currentPlateWarningInfo = currentPlateDataFormat.getOutPutWarning();
        currentPlateHardVersion = currentPlateDataFormat.getHardwareVersion();
        currentPlateSoftVersion = currentPlateDataFormat.getSoftwareVersion();
        runningTime = currentPlateDataFormat.getStatus();
        currentPlateElectricWarning = CabInfoSp.getInstance().getCurrentThreshold();
        timeOutTime = CabInfoSp.getInstance().getCurrentOutTime();
        recoveryTime = CabInfoSp.getInstance().getCurrentRecoveryTime();
    }


    public int getAddress() {
        return address;
    }

    public int getFunctionState() {
        return functionState;
    }

    public int getLength() {
        return length;
    }

    public int getVersion() {
        return version;
    }

    public double getWater_1() {
        return water_1;
    }

    public double getTemperature_3() {
        return temperature_3;
    }

    public double getTemperature_2() {
        return temperature_2;
    }

    public double getSmoke() {
        return smoke;
    }

    public double getElectricityMeterVoltage() {
        return electricityMeterVoltage;
    }

    public double getElectricityMeterElectric() {
        return electricityMeterElectric;
    }

    public double getUsefulPower() {
        return usefulPower;
    }

    public double getUsefulTotalElectricEnergy() {
        return usefulTotalElectricEnergy;
    }

    public double getPowerFactor() {
        return powerFactor;
    }

    public double getCO2() {
        return CO2;
    }

    public int getElectricityMeterTemperature() {
        return electricityMeterTemperature;
    }

    public double getElectricityMeterFrequency() {
        return electricityMeterFrequency;
    }

    public double getHardwareStatus() {
        return hardwareStatus;
    }

    public double getCurrentPlateElectric() {
        return currentPlateElectric;
    }

    public double getCurrentPlateVoltage() {
        return currentPlateVoltage;
    }

    public double getCurrentPlateElectricWarning() {
        return currentPlateElectricWarning;
    }

    public int getTimeOutTime() {
        return timeOutTime;
    }

    public int getRecoveryTime() {
        return recoveryTime;
    }

    public String getRunningTime() {
        return runningTime;
    }

    public double getTemperature_1() {
        return temperature_1;
    }

    public double getWater_2() {
        return water_2;
    }

    public double getDataTime() {
        return dataTime;
    }

    public int getPutterPowerStatus() {
        return putterPowerStatus;
    }

    public int getButtonStatus_1() {
        return buttonStatus_1;
    }

    public int getButtonStatus_2() {
        return buttonStatus_2;
    }

    public int getStandbyPowerStatus_1() {
        return standbyPowerStatus_1;
    }

    public int getStandbyPowerStatus_2() {
        return standbyPowerStatus_2;
    }

    public int getFanStatus_1() {
        return fanStatus_1;
    }

    public int getFanStatus_2() {
        return fanStatus_2;
    }

    public int getAndroidPower() {
        return androidPower;
    }

    public int getCurrentPlateHardVersion() {
        return currentPlateHardVersion;
    }

    public void setCurrentPlateHardVersion(int currentPlateHardVersion) {
        this.currentPlateHardVersion = currentPlateHardVersion;
    }

    public int getCurrentPlateSoftVersion() {
        return currentPlateSoftVersion;
    }

    public void setCurrentPlateSoftVersion(int currentPlateSoftVersion) {
        this.currentPlateSoftVersion = currentPlateSoftVersion;
    }

    public String getCurrentPlateWarningInfo() {
        return currentPlateWarningInfo;
    }

    public void setCurrentPlateWarningInfo(String currentPlateWarningInfo) {
        this.currentPlateWarningInfo = currentPlateWarningInfo;
    }

    @Override
    public String toString() {
        return "EnvironmentDataFormat{" +
                "address=" + address +
                ", functionState=" + functionState +
                ", length=" + length +
                ", version=" + version +
                ", water_1=" + water_1 +
                ", temperature_3=" + temperature_3 +
                ", temperature_2=" + temperature_2 +
                ", smoke=" + smoke +
                ", electricityMeterVoltage=" + electricityMeterVoltage +
                ", electricityMeterElectric=" + electricityMeterElectric +
                ", usefulPower=" + usefulPower +
                ", usefulTotalElectricEnergy=" + usefulTotalElectricEnergy +
                ", powerFactor=" + powerFactor +
                ", CO2=" + CO2 +
                ", electricityMeterTemperature=" + electricityMeterTemperature +
                ", electricityMeterFrequency=" + electricityMeterFrequency +
                ", hardwareStatus=" + hardwareStatus +
                ", currentPlateElectric=" + currentPlateElectric +
                ", currentPlateVoltage=" + currentPlateVoltage +
                ", currentPlateElectricWarning=" + currentPlateElectricWarning +
                ", timeOutTime=" + timeOutTime +
                ", recoveryTime=" + recoveryTime +
                ", runningTime=" + runningTime +
                ", temperature_1=" + temperature_1 +
                ", water_2=" + water_2 +
                ", dataTime=" + dataTime +
                ", putterPowerStatus=" + putterPowerStatus +
                ", buttonStatus_1=" + buttonStatus_1 +
                ", buttonStatus_2=" + buttonStatus_2 +
                ", standbyPowerStatus_1=" + standbyPowerStatus_1 +
                ", standbyPowerStatus_2=" + standbyPowerStatus_2 +
                ", fanStatus_1=" + fanStatus_1 +
                ", fanStatus_2=" + fanStatus_2 +
                ", androidPower=" + androidPower +
                '}';
    }

    //负数处理
    private int[] bytesToInts(byte[] bytes){
        //负数处理
        int[] data = new int[bytes.length];
        for (int i = 0; i < data.length; i++) {
            data[i] = bytes[i] & 0xff;
        }
        return data;
    }
}
