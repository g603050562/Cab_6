package client.halouhuandian.app15.service.logic.logicChangeBatteries;

/**
 * 换电信息 mode类
 */
public class ChangeBatteriesChangeDataFormat {

    private int inDoor = -1;
    private String inputBattery = "";
    private int inElectric = -1;

    private int outDoor = -1;
    private String outPutBattery = "";
    private int outElectric = -1;

    private String costType = "";
    private String costInfo = "";

    public ChangeBatteriesChangeDataFormat(int inDoor, String  inputBattery, int inElectric, int outDoor, String outPutBattery, int outElectric, String costType, String costInfo) {
        this.inDoor = inDoor;
        this.inputBattery = inputBattery;
        this.inElectric = inElectric;
        this.outDoor = outDoor;
        this.outPutBattery = outPutBattery;
        this.outElectric = outElectric;
        this.costType = costType;
        this.costInfo = costInfo;
    }

    public int getInDoor() {
        return inDoor;
    }

    public String getInputBattery() {
        return inputBattery;
    }

    public int getInElectric() {
        return inElectric;
    }

    public int getOutDoor() {
        return outDoor;
    }

    public String getOutPutBattery() {
        return outPutBattery;
    }

    public int getOutElectric() {
        return outElectric;
    }

    public String getCostType() {
        return costType;
    }

    public String getCostInfo() {
        return costInfo;
    }
}
