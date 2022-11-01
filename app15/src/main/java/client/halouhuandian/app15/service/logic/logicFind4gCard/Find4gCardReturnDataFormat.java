package client.halouhuandian.app15.service.logic.logicFind4gCard;

public class Find4gCardReturnDataFormat {

    public enum Find4gCardReturnDataType{
        IMSI,
        error,
    }

    private Find4gCardReturnDataType find4gCardReturnDataType;
    private String info = "";

    public Find4gCardReturnDataFormat(Find4gCardReturnDataType find4gCardReturnDataType, String info) {
        this.find4gCardReturnDataType = find4gCardReturnDataType;
        this.info = info;
    }

    public Find4gCardReturnDataType getFind4gCardReturnDataType() {
        return find4gCardReturnDataType;
    }

    public String getInfo() {
        return info;
    }
}
