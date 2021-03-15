package client.halouhuandian.app15.devicesController.sensor;

import android.os.SystemClock;
import android.support.annotation.NonNull;

/**
 * Author:      Lee Yeung
 * Create Date: 2020/3/27
 * Description: 传感器数据对象
 */
public class SensorDataBean {
    private short hardwareVersion;
    private short softwareVersion;
    //水位：单位V
    private float waterLevel;
    private float waterLevel2;
    //温度1
    private float temperature1;
    //温度2
    private float temperature2;
    //温度3
    private float temperature3;
    //烟感
    private float smoke;
    //电表电压
    private float ammeterVoltage;
    //电表电流
    private float ammeterElectric;
    //电表功率
    private float ammeterPower;
    //电表总功率,kwh-千瓦时
    private float ammeterTotalPower;
    //电表功率因数
    private float ammeterPwerCoefficient;
    //电表二氧化碳排量
    private float ammeterCarbonEmission;
    //电表温度
    private float ammeterTemperature;
    //电表频率
    private float ammeterFrequency;

    private byte buttonStatus;
    private byte airFan1Status;
    private byte airFan2Status;

    //电表总功率,kwh-千瓦时
    private float ammeterTotalPower_485;

    public void setHardwareVersion(short hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    void setSoftwareVersion(short softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    void setWaterLevel(float waterLevel) {
        this.waterLevel = waterLevel;
    }

    public void setWaterLevel2(float waterLevel2) {
        this.waterLevel2 = waterLevel2;
    }

    void setTemperature1(float temperature1) {
        this.temperature1 = temperature1;
    }

    void setTemperature2(float temperature2) {
        this.temperature2 = temperature2;
    }

    void setTemperature3(float temperature3) {
        this.temperature3 = temperature3;
    }

    void setSmoke(float smoke) {
        this.smoke = smoke;
    }

    void setAmmeterVoltage(float ammeterVoltage) {
        this.ammeterVoltage = ammeterVoltage;
    }

    void setAmmeterElectric(float ammeterElectric) {
        this.ammeterElectric = ammeterElectric;
    }

    void setAmmeterPower(float ammeterPower) {
        this.ammeterPower = ammeterPower;
    }

    void setAmmeterTotalPower(float ammeterTotalPower) {
        this.ammeterTotalPower = ammeterTotalPower;
    }

    void setAmmeterTotalPower_485(float ammeterTotalPower_485) {
        this.ammeterTotalPower_485 = ammeterTotalPower_485;
    }

    void setAmmeterPwerCoefficient(float ammeterPwerCoefficient) {
        this.ammeterPwerCoefficient = ammeterPwerCoefficient;
    }

    void setAmmeterCarbonEmission(float ammeterCarbonEmission) {
        this.ammeterCarbonEmission = ammeterCarbonEmission;
    }

    void setAmmeterTemperature(float ammeterTemperature) {
        this.ammeterTemperature = ammeterTemperature;
    }

    void setAmmeterFrequency(float ammeterFrequency) {
        this.ammeterFrequency = ammeterFrequency;
    }

    public byte getButtonStatus() {
        return buttonStatus;
    }

    public void setButtonStatus(byte buttonStatus) {
        this.buttonStatus = buttonStatus;
    }

    public short getHardwareVersion() {
        return hardwareVersion;
    }

    public short getSoftwareVersion() {
        return softwareVersion;
    }

    public float getWaterLevel() {
        return waterLevel;
    }

    public float getWaterLevel2() {
        return waterLevel2;
    }

    public float getTemperature3() {
        return temperature3;
    }

    public float getTemperature1() {
        return temperature1;
    }

    public float getTemperature2() {
        return temperature2;
    }

    public float getSmoke() {
        return smoke;
    }

    public float getAmmeterVoltage() {
        return ammeterVoltage;
    }

    public float getAmmeterElectric() {
        return ammeterElectric;
    }

    public float getAmmeterPower() {
        return ammeterPower;
    }

    public float getAmmeterTotalPower() {
        return ammeterTotalPower;
    }

    public float getAmmeterPwerCoefficient() {
        return ammeterPwerCoefficient;
    }

    public float getAmmeterCarbonEmission() {
        return ammeterCarbonEmission;
    }

    public float getAmmeterTemperature() {
        return ammeterTemperature;
    }

    public float getAmmeterFrequency() {
        return ammeterFrequency;
    }

    //------------------添加单位--------------------
    public String getSoftwareVersion_String() {
        return "V " + softwareVersion;
    }

    public String getWaterLevel_String() {
        return waterLevel + "V";
    }

    public String getWaterLevel2_String() {
        return waterLevel2 + "V";
    }

    public String getTemperature1_String() {
        return temperature1 + "°C";
    }

    public String getTemperature2_String() {
        return temperature2 + "°C";
    }

    public String getTemperature3_String() {
        return temperature3 + "°C";
    }

    public String getSmoke_String() {
        return smoke + "V";
    }

    public String getAmmeterVoltage_String() {
        return ammeterVoltage + "V";
    }

    public String getAmmeterElectric_String() {
        return ammeterElectric + "A";
    }

    public String getAmmeterPower_String() {
        return ammeterPower + "w";
//        return ammeterPower != 0 ? String.format("%.1f", ammeterPower) : "0";
    }

    public String getAmmeterTotalPower_String() {
        return ammeterTotalPower + "kwh";
//        return ammeterTotalPower != 0 ? String.format("%.1f", ammeterTotalPower) : "0";
    }

    public String getAmmeterTotalPower_485_String() {
        return ammeterTotalPower_485 + "kwh";
//        return ammeterTotalPower != 0 ? String.format("%.1f", ammeterTotalPower) : "0";
    }

    public String getAmmeterCarbonEmission_String() {
        return ammeterCarbonEmission + "Kg";
    }

    public String getAmmeterTemperature_String() {
        return ammeterTemperature + "°C";
    }

    public String getAmmeterFrequency_String() {
        return ammeterFrequency + "Hz";
    }

    public byte getAirFan1Status() {
        return airFan1Status;
    }

    public byte getAirFan2Status() {
        return airFan2Status;
    }

    public void setAirFan1Status(byte airFan1Status) {
        this.airFan1Status = airFan1Status;
    }

    public void setAirFan2Status(byte airFan2Status) {
        this.airFan2Status = airFan2Status;
    }

    @NonNull
    @Override
    public String toString() {

        return
                "硬件版本：" + getHardwareVersion() + "\t"
                        + "软件版本：" + getSoftwareVersion_String() + "\t"
                        + "水位：" + getWaterLevel_String() + "\t"
                        + "水位2：" + getWaterLevel2_String() + "\t"
                        + "温度1：" + getTemperature1_String() + "\t"
                        + "温度2：" + getTemperature2_String() + "\t"
                        + "温度3：" + getTemperature3_String() + "\t"
                        + "烟感：" + getSmoke_String() + "\t"
                        + "电表电压：" + getAmmeterVoltage_String() + "\t"
                        + "电表电流：" + getAmmeterElectric_String() + "\t"
                        + "电表功率：" + getAmmeterPower_String() + "\t"
                        + "电表总功率：" + getAmmeterTotalPower_String() + "\t"
                        + "功率因子：" + getAmmeterPwerCoefficient() + "\t"
                        + "电表二氧化碳：" + getAmmeterCarbonEmission_String() + "\t"
                        + "电表温度：" + getAmmeterTemperature_String() + "\t"
                        + "电表频率：" + getAmmeterFrequency_String() + "\t"
                        + "485电表：" + getAmmeterTotalPower_485_String() + "\t"
                        + "按键：" + getButtonStatus() + "\t"
                        + "风扇1：" + airFan1Status + "\t"
                        + "风扇2：" + airFan2Status;
    }

    private long stopTime;

    public void stopContinueControl() {
        stopTime = SystemClock.elapsedRealtime() + 60 * 1000;
    }

    public boolean isContinueControl() {

        return SystemClock.elapsedRealtime() > stopTime;
    }
}
