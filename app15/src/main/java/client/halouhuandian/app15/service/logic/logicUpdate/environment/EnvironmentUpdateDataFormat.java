package client.halouhuandian.app15.service.logic.logicUpdate.environment;

public class EnvironmentUpdateDataFormat {

    public enum EnvironmentUpdateType{
        requireData,
    }
    private long address;
    private byte[] data;
    private EnvironmentUpdateType type;

    public EnvironmentUpdateDataFormat(long address, byte[] data, EnvironmentUpdateType type) {
        this.address = address;
        this.data = data;
        this.type = type;
    }

    public long getAddress() {
        return address;
    }

    public byte[] getData() {
        return data;
    }

    public EnvironmentUpdateType getType() {
        return type;
    }
}
