package client.halouhuandian.app15.hardWareConncetion.daa.mode.dcdc;

public class DcdcInfoByStateFormat {

    //dcdc状态
    private String dcdcState = "初始化";
    //dcdc输出电流
    private double dcdcElectric = 0;
    //dcdc输出电压
    private double dcdcVoltage = 0;
    //dcdc停止原因
    private String dcdcStopInfo = "";
    //dcdc软件版本
    private String dcdcSoftwareVersion = "";
    //dcdc硬件版本
    private String dcdcHardWareVersion = "";
    //dcdc状态信息返回时间
    private long dataTime = 0;

    public DcdcInfoByStateFormat(){
        this.dataTime = System.currentTimeMillis();
    }


    public String getDcdcState() {
        return dcdcState;
    }

    public void setDcdcState(String dcdcState) {
        this.dcdcState = dcdcState;
    }

    public double getDcdcElectric() {
        return dcdcElectric;
    }

    public void setDcdcElectric(double dcdcElectric) {
        this.dcdcElectric = dcdcElectric;
    }

    public double getDcdcVoltage() {
        return dcdcVoltage;
    }

    public void setDcdcVoltage(double dcdcVoltage) {
        this.dcdcVoltage = dcdcVoltage;
    }

    public String getDcdcStopInfo() {
        return dcdcStopInfo;
    }

    public void setDcdcStopInfo(String dcdcStopInfo) {
        this.dcdcStopInfo = dcdcStopInfo;
    }

    public String getDcdcSoftwareVersion() {
        return dcdcSoftwareVersion;
    }

    public void setDcdcSoftwareVersion(String dcdcSoftwareVersion) {
        this.dcdcSoftwareVersion = dcdcSoftwareVersion;
    }

    public String getDcdcHardWareVersion() {
        return dcdcHardWareVersion;
    }

    public void setDcdcHardWareVersion(String dcdcHardWareVersion) {
        this.dcdcHardWareVersion = dcdcHardWareVersion;
    }

    public long getDataTime() {
        return dataTime;
    }

    public void setDataTime(long dataTime) {
        this.dataTime = dataTime;
    }

    @Override
    public String toString() {
        return "DaaInfoByDcdcFormat{" +
                "dcdcState='" + dcdcState + '\'' +
                ", dcdcElectric=" + dcdcElectric +
                ", dcdcVoltage=" + dcdcVoltage +
                ", dcdcStopInfo='" + dcdcStopInfo + '\'' +
                ", dcdcSoftwareVersion=" + dcdcSoftwareVersion +
                ", dcdcHardWareVersion=" + dcdcHardWareVersion +
                '}';
    }
}
