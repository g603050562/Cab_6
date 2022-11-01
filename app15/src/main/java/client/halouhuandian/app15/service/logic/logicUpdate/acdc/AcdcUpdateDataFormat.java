package client.halouhuandian.app15.service.logic.logicUpdate.acdc;

public class AcdcUpdateDataFormat {

    public enum AcdcUpdateType{
        liveConnection,
        requireHandData,
        requireBodyData,
    }
    private long address;
    private byte[] data;
    private AcdcUpdateType type;

    public AcdcUpdateDataFormat(long address, byte[] data, AcdcUpdateType type) {
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

    public AcdcUpdateType getType() {
        return type;
    }
}
