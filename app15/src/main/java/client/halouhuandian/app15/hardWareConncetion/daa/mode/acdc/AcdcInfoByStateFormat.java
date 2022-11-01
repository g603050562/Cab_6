package client.halouhuandian.app15.hardWareConncetion.daa.mode.acdc;

public class AcdcInfoByStateFormat {


    //acdc输出功率
    private double acdcOutputPower = 0;
    //acdc输入电压
    private double acdcInputVoltage = 0;
    //acdc输出电压
    private double acdcOutPutVoltage = 0;
    //acdc输出电流
    private double acdcOutPutElectric= 0;
    //acdc软件版本
    private String acdcSoftWareVersion = "";
    //acdc软件版本
    private String acdcHardWareVersion = "";
    //主从关系
    private String acdcMasterOrSlave = "";
    //剩余功率 - 单位KWH
    private double acdcSurplusPower = 0;
    //睡眠状态
    private String acdcIsSleep = "正常";
    //acdc数据时间
    private long dataTime = 0;

    public AcdcInfoByStateFormat() {
        this.dataTime = System.currentTimeMillis();
    }

    public double getAcdcOutputPower() {
        return acdcOutputPower;
    }

    public void setAcdcOutputPower(double acdcOutputPower) {
        this.acdcOutputPower = acdcOutputPower;
    }

    public double getAcdcInputVoltage() {
        return acdcInputVoltage;
    }

    public void setAcdcInputVoltage(double acdcInputVoltage) {
        this.acdcInputVoltage = acdcInputVoltage;
    }

    public double getAcdcOutPutVoltage() {
        return acdcOutPutVoltage;
    }

    public void setAcdcOutPutVoltage(double acdcOutPutVoltage) {
        this.acdcOutPutVoltage = acdcOutPutVoltage;
    }

    public double getAcdcOutPutElectric() {
        return acdcOutPutElectric;
    }

    public void setAcdcOutPutElectric(double acdcOutPutElectric) {
        this.acdcOutPutElectric = acdcOutPutElectric;
    }

    public String getAcdcMasterOrSlave() {
        return acdcMasterOrSlave;
    }

    public void setAcdcMasterOrSlave(String acdcMasterOrSlave) {
        this.acdcMasterOrSlave = acdcMasterOrSlave;
    }

    public double getAcdcSurplusPower() {
        return acdcSurplusPower;
    }

    public void setAcdcSurplusPower(double acdcSurplusPower) {
        this.acdcSurplusPower = acdcSurplusPower;
    }

    public String getAcdcIsSleep() {
        return acdcIsSleep;
    }

    public void setAcdcIsSleep(String acdcIsSleep) {
        this.acdcIsSleep = acdcIsSleep;
    }

    public long getDataTime() {
        return dataTime;
    }

    public void setDataTime(long dataTime) {
        this.dataTime = dataTime;
    }

    public String getAcdcSoftWareVersion() {
        return acdcSoftWareVersion;
    }

    public void setAcdcSoftWareVersion(String acdcSoftWareVersion) {
        this.acdcSoftWareVersion = acdcSoftWareVersion;
    }

    public String getAcdcHardWareVersion() {
        return acdcHardWareVersion;
    }

    public void setAcdcHardWareVersion(String acdcHardWareVersion) {
        this.acdcHardWareVersion = acdcHardWareVersion;
    }

    @Override
    public String toString() {
        return "AcdcStateBean{" +
                "acdcOutputPower=" + acdcOutputPower +
                ", acdcInputVoltage=" + acdcInputVoltage +
                ", acdcOutPutVoltage=" + acdcOutPutVoltage +
                ", acdcOutPutElectric=" + acdcOutPutElectric +
                ", acdcSV='" + acdcSoftWareVersion + '\'' +
                ", acdcHV='" + acdcHardWareVersion + '\'' +
                ", acdcMasterOrSlave='" + acdcMasterOrSlave + '\'' +
                ", acdcSurplusPower=" + acdcSurplusPower +
                ", acdcIsSleep=" + acdcIsSleep +
                '}';
    }
}
