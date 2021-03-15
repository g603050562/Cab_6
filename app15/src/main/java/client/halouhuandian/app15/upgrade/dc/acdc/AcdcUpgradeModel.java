package client.halouhuandian.app15.upgrade.dc.acdc;

/**
 * Author:      Lee Yeung
 * Create Date: 2020/7/27
 * Description:
 */
public class AcdcUpgradeModel {
    private byte address;
    private byte status;
    private String statusInfo;
    private long process;
    private long total;

    public AcdcUpgradeModel(byte address) {
        this.address = address;
    }

    public byte getAddress() {
        return address;
    }

    public void setAddress(byte address) {
        this.address = address;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public String getStatusInfo() {
        return statusInfo;
    }

    public void setStatusInfo(String statusInfo) {
        this.statusInfo = statusInfo;
    }

    public long getProcess() {
        return process;
    }

    public void setProcess(long process) {
        this.process = process;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
