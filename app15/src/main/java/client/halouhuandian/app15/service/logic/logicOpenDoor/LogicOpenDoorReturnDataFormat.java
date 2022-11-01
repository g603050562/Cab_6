package client.halouhuandian.app15.service.logic.logicOpenDoor;

public class LogicOpenDoorReturnDataFormat {

    private boolean result;
    private String info;

    public LogicOpenDoorReturnDataFormat(boolean result, String info) {
        this.result = result;
        this.info = info;
    }

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
