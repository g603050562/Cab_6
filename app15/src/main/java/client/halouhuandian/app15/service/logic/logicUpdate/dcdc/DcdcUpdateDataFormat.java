package client.halouhuandian.app15.service.logic.logicUpdate.dcdc;

public class DcdcUpdateDataFormat {

    public enum DcdcUpdateType{
        liveConnection,
        requireHandData,
        requireBodyData,
    }
    private long address;
    private byte[] data;
    private DcdcUpdateType type;

    public DcdcUpdateDataFormat(long address, byte[] data, DcdcUpdateType type) {
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

    public DcdcUpdateType getType() {
        return type;
    }
}
