package client.halouhuandian.app15.service.logic.logicChangeBatteries;

public class ChangeBatteriesReturnDataFormat {

    public enum ChangeBatteriesReturnDataType{
        data,
        info,
        inchingTrigger
    }

    private ChangeBatteriesReturnDataType changeBatteriesReturnDataType;
    private Object object;

    public ChangeBatteriesReturnDataFormat(ChangeBatteriesReturnDataType changeBatteriesReturnDataType, Object object) {
        this.changeBatteriesReturnDataType = changeBatteriesReturnDataType;
        this.object = object;
    }

    public ChangeBatteriesReturnDataType getChangeBatteriesReturnDataType() {
        return changeBatteriesReturnDataType;
    }

    public Object getObject() {
        return object;
    }
}
