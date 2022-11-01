package client.halouhuandian.app15.hardWareConncetion.daa.mode.dcdc;

public class DcdcInfoByWarningFormat {

    //dcdc内部告警
    private String errorStateInSide = "";
    //dcdc外部告警
    private String errorStateOutSide = "";
    //BMS告警
    private String errorStateBMS = "";
    //数据时间
    private long dataTime = 0;

    public DcdcInfoByWarningFormat() {
        this.dataTime = System.currentTimeMillis();
    }

    public DcdcInfoByWarningFormat(String errorStateInSide, String errorStateOutSide, String errorStateBMS) {
        this.errorStateInSide = errorStateInSide;
        this.errorStateOutSide = errorStateOutSide;
        this.errorStateBMS = errorStateBMS;
        this.dataTime = System.currentTimeMillis();
    }

    public String getErrorStateInSide() {
        return errorStateInSide;
    }

    public void setErrorStateInSide(String errorStateInSide) {
        this.errorStateInSide = errorStateInSide;
    }

    public String getErrorStateOutSide() {
        return errorStateOutSide;
    }

    public void setErrorStateOutSide(String errorStateOutSide) {
        this.errorStateOutSide = errorStateOutSide;
    }

    public String getErrorStateBMS() {
        return errorStateBMS;
    }

    public void setErrorStateBMS(String errorStateBMS) {
        this.errorStateBMS = errorStateBMS;
    }

    public long getDataTime() {
        return dataTime;
    }

    public void setDataTime(long dataTime) {
        this.dataTime = dataTime;
    }

    @Override
    public String toString() {
        return "DcdcWarningBean{" +
                "errorStateInSide='" + errorStateInSide + '\'' +
                ", errorStateOutSide='" + errorStateOutSide + '\'' +
                ", errorStateBMS='" + errorStateBMS + '\'' +
                '}';
    }
}
