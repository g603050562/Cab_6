package client.halouhuandian.app15.hardWareConncetion.daa.mode.dcdc;

/**
 * dcdc上传的电池基本信息
 */
public class DcdcInfoByBaseFormat {

    /**
     * 第一帧数据
     */
    //舱门地址
    private int address = -1;
    //三个微动或者其他硬件信息
    private int inchingByInner = -1; //这个代表舱门内部的底部微动
    private int inchingByOuterOpen = -1; //这个代表舱门外部开门微动
    private int inchingByOuterClose = -1; //这个代表舱门外部关门微动
    //推杆状态
    private int putterState = 0;
    //电池壳温度
    private double temperatureSensorByInner = -1;
    //电池芯温度
    private double temperatureSensorByOuter = -1;
    //电池芯温度
    private double temperatureSensorByOuter2 = -1;
    //电池电量 - soc
    private int batteryRelativeSurplus = 0;
    /**
     * 第二帧数据
     */
    //电池电压
    private double batteryVoltage = -1;
    //电池内部电流
    private double batteryElectric = -1;
    //绝对剩余容量
    private int batteryAbsoluteSurplus = -1;
    //剩余容量
    private int batteryRemainingCapacity = -1;
    //满充容量
    private int batteryFullCapacity = -1;
    /**
     * 第三帧数据
     */
    //循环次数
    private int loops = -1;
    //电池健康百分比
    private int batteryHealthy = -1;
    //电池版本 格式："软件版本"+"硬件版本"
    private String batteryVersion = "0000";
    //需求功率
    private double requirePower = 0;
    /**
     *第四帧到第九帧数据
     */
    //单体电压最小 格式:  "串数下标"+_+"数值"
    private String itemMin = "0_0";
    //单体电压最大 格式:  "串数下标"+_+"数值"
    private String itemMax = "0_0";
    //压差
    private int pressureDifferential = -1;
    /**
     * 第十帧到第十二帧数据
     */
    //电池ID
    private String BID = "FFFFFFFFFFFFFFFF";
    /**
     * 第十三帧到第十五帧数据
     */
    private String manufacturer = "";
    /**
     * 第十六帧到第十七帧
     */
    //电池UID
    private String UID = "FFFFFFFF";
    /**
     * 第十八帧数据
     */
    //采样电压
    private double samplingVoltage = -1;
    //电池实际soc
    private int batteryRealRelativeSurplus = 0;

    //数据时间
    private long dataTime = 0;

    public DcdcInfoByBaseFormat() {
        this.dataTime = System.currentTimeMillis();
    }

    public DcdcInfoByBaseFormat(int address, int inchingByInner, int inchingByOuterOpen, int inchingByOuterClose, int putterState, double temperatureSensorByInner, double temperatureSensorByOuter, int batteryRelativeSurplus, int batteryVoltage, int batteryElectric, int batteryAbsoluteSurplus, int batteryRemainingCapacity, int batteryFullCapacity, int loops, int batteryHealthy, String batteryVersion, double requirePower, String itemMin, String itemMax, int pressureDifferential, String BID, String UID, int samplingVoltage , int batteryRealRelativeSurplus, String manufacturer) {
        this.address = address;
        this.inchingByInner = inchingByInner;
        this.inchingByOuterOpen = inchingByOuterOpen;
        this.inchingByOuterClose = inchingByOuterClose;
        this.putterState = putterState;
        this.temperatureSensorByInner = temperatureSensorByInner;
        this.temperatureSensorByOuter = temperatureSensorByOuter;
        this.temperatureSensorByOuter2 = temperatureSensorByOuter;
        this.batteryRelativeSurplus = batteryRelativeSurplus;
        this.batteryVoltage = batteryVoltage;
        this.batteryElectric = batteryElectric;
        this.batteryAbsoluteSurplus = batteryAbsoluteSurplus;
        this.batteryRemainingCapacity = batteryRemainingCapacity;
        this.batteryFullCapacity = batteryFullCapacity;
        this.loops = loops;
        this.batteryHealthy = batteryHealthy;
        this.batteryVersion = batteryVersion;
        this.requirePower = requirePower;
        this.itemMin = itemMin;
        this.itemMax = itemMax;
        this.pressureDifferential = pressureDifferential;
        this.BID = BID;
        this.UID = UID;
        this.samplingVoltage = samplingVoltage;
        this.batteryRealRelativeSurplus = batteryRealRelativeSurplus;
        this.manufacturer = manufacturer;
        this.dataTime = System.currentTimeMillis();
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getInchingByInner() {
        return inchingByInner;
    }

    public void setInchingByInner(int inchingByInner) {
        this.inchingByInner = inchingByInner;
    }

    public int getInchingByOuterOpen() {
        return inchingByOuterOpen;
    }

    public void setInchingByOuterOpen(int inchingByOuterOpen) {
        this.inchingByOuterOpen = inchingByOuterOpen;
    }

    public int getInchingByOuterClose() {
        return inchingByOuterClose;
    }

    public void setInchingByOuterClose(int inchingByOuterClose) {
        this.inchingByOuterClose = inchingByOuterClose;
    }

    public int getPutter() {
        return putterState;
    }

    public void setPutter(int putterState) {
        this.putterState = putterState;
    }

    public double getTemperatureSensorByInner() {
        return temperatureSensorByInner;
    }

    public void setTemperatureSensorByInner(double temperatureSensorByInner) {
        this.temperatureSensorByInner = temperatureSensorByInner;
    }

    public double getTemperatureSensorByOuter() {
        return temperatureSensorByOuter;
    }

    public void setTemperatureSensorByOuter(double temperatureSensorByOuter) {
        this.temperatureSensorByOuter = temperatureSensorByOuter;
    }

    public double getTemperatureSensorByOuter2() {
        return temperatureSensorByOuter2;
    }

    public void setTemperatureSensorByOuter2(double temperatureSensorByOuter2) {
        this.temperatureSensorByOuter2 = temperatureSensorByOuter2;
    }

    public int getBatteryRelativeSurplus() {
        return batteryRelativeSurplus;
    }

    public void setBatteryRelativeSurplus(int batteryRelativeSurplus) {
        this.batteryRelativeSurplus = batteryRelativeSurplus;
    }

    public double getBatteryVoltage() {
        return batteryVoltage;
    }

    public void setBatteryVoltage(double batteryVoltage) {
        this.batteryVoltage = batteryVoltage;
    }

    public double getBatteryElectric() {
        return batteryElectric;
    }

    public void setBatteryElectric(double batteryElectric) {
        this.batteryElectric = batteryElectric;
    }

    public int getBatteryAbsoluteSurplus() {
        return batteryAbsoluteSurplus;
    }

    public void setBatteryAbsoluteSurplus(int batteryAbsoluteSurplus) {
        this.batteryAbsoluteSurplus = batteryAbsoluteSurplus;
    }

    public int getBatteryRemainingCapacity() {
        return batteryRemainingCapacity;
    }

    public void setBatteryRemainingCapacity(int batteryRemainingCapacity) {
        this.batteryRemainingCapacity = batteryRemainingCapacity;
    }

    public int getBatteryFullCapacity() {
        return batteryFullCapacity;
    }

    public void setBatteryFullCapacity(int batteryFullCapacity) {
        this.batteryFullCapacity = batteryFullCapacity;
    }

    public int getLoops() {
        return loops;
    }

    public void setLoops(int loops) {
        this.loops = loops;
    }

    public int getBatteryHealthy() {
        return batteryHealthy;
    }

    public void setBatteryHealthy(int batteryHealthy) {
        this.batteryHealthy = batteryHealthy;
    }

    public String getBatteryVersion() {
        return batteryVersion;
    }

    public void setBatteryVersion(String batteryVersion) {
        this.batteryVersion = batteryVersion;
    }

    public double getRequirePower() {
        return requirePower;
    }

    public void setRequirePower(double requirePower) {
        this.requirePower = requirePower;
    }

    public String getItemMin() {
        return itemMin;
    }

    public void setItemMin(String itemMin) {
        this.itemMin = itemMin;
    }

    public String getItemMax() {
        return itemMax;
    }

    public void setItemMax(String itemMax) {
        this.itemMax = itemMax;
    }

    public int getPressureDifferential() {
        return pressureDifferential;
    }

    public void setPressureDifferential(int pressureDifferential) {
        this.pressureDifferential = pressureDifferential;
    }

    public String getBID() {
        return BID;
    }

    public void setBID(String BID) {
        this.BID = BID;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public double getSamplingVoltage() {
        return samplingVoltage;
    }

    public void setSamplingVoltage(double samplingVoltage) {
        this.samplingVoltage = samplingVoltage;
    }

    public int getBatteryRealRelativeSurplus() {
        return batteryRealRelativeSurplus;
    }

    public void setBatteryRealRelativeSurplus(int batteryRealRelativeSurplus) {
        this.batteryRealRelativeSurplus = batteryRealRelativeSurplus;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public long getDataTime() {
        return dataTime;
    }

    public void setDataTime(long dataTime) {
        this.dataTime = dataTime;
    }

    @Override
    public String toString() {
        return "DcdcBaseBean{" +
                "address=" + address +
                ", inchingByInner=" + inchingByInner +
                ", inchingByOuterOpen=" + inchingByOuterOpen +
                ", inchingByOuterClose=" + inchingByOuterClose +
                ", putterState=" + putterState +
                ", temperatureSensorByInner=" + temperatureSensorByInner +
                ", temperatureSensorByOuter=" + temperatureSensorByOuter +
                ", batteryRelativeSurplus=" + batteryRelativeSurplus +
                ", batteryVoltage=" + batteryVoltage +
                ", batteryElectric=" + batteryElectric +
                ", batteryAbsoluteSurplus=" + batteryAbsoluteSurplus +
                ", batteryRemainingCapacity=" + batteryRemainingCapacity +
                ", batteryFullCapacity=" + batteryFullCapacity +
                ", loops=" + loops +
                ", batteryHealthy=" + batteryHealthy +
                ", batteryVersion='" + batteryVersion + '\'' +
                ", requirePower=" + requirePower +
                ", itemMin='" + itemMin + '\'' +
                ", itemMax='" + itemMax + '\'' +
                ", pressureDifferential=" + pressureDifferential +
                ", BID='" + BID + '\'' +
                ", UID='" + UID + '\'' +
                ", samplingVoltage=" + samplingVoltage +
                '}';
    }
}
