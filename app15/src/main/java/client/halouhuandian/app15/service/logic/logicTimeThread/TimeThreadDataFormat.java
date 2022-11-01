package client.halouhuandian.app15.service.logic.logicTimeThread;

public class TimeThreadDataFormat {
    private TimeThreadDataType timeThreadDataType;
    private String info;

    public TimeThreadDataFormat(TimeThreadDataType timeThreadDataType, String info) {
        this.timeThreadDataType = timeThreadDataType;
        this.info = info;
    }

    public TimeThreadDataType getTimeThreadDataType() {
        return timeThreadDataType;
    }

    public void setTimeThreadDataType(TimeThreadDataType timeThreadDataType) {
        this.timeThreadDataType = timeThreadDataType;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
