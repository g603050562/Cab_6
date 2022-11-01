package client.halouhuandian.app15.hardWareConncetion.environmentPlate;

public class EnvironmentReturnDataFormat {

    public enum EnvironmentReturnDataFormatType{
        environmentData,
        buttonTrigger,
    }

    public EnvironmentReturnDataFormatType environmentReturnDataFormatType;
    public Object returnData;

    public EnvironmentReturnDataFormat(EnvironmentReturnDataFormatType environmentReturnDataFormatType, Object returnData) {
        this.environmentReturnDataFormatType = environmentReturnDataFormatType;
        this.returnData = returnData;
    }

    public EnvironmentReturnDataFormatType getEnvironmentReturnDataFormatType() {
        return environmentReturnDataFormatType;
    }

    public Object getReturnData() {
        return returnData;
    }
}
