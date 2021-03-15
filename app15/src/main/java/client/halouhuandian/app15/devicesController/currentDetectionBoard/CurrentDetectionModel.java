package client.halouhuandian.app15.devicesController.currentDetectionBoard;

/**
 * Author:      Lee Yeung
 * Create Date: 2020/11/17
 * Description:
 */
public final class CurrentDetectionModel extends CurrentDetectionData {
    public String status_String = "-";
    public String outVoltage_String = "-";
    public String outCurrent_String = "-";
    public String outWarning_String = "-";
    private boolean isExistDevice;

    private final StringBuilder stringBuilder = new StringBuilder();

    void parseFinish() {
        isExistDevice = true;
        switch (status) {
            case 0x00:
                status_String = "待机";
                break;
            case 0x01:
                status_String = "故障";
                break;
            case 0x02:
                status_String = "告警";
                break;
            case 0x03:
                status_String = "禁止";
                break;
            default:
                status_String = "检测失败";
                isExistDevice = false;
                break;
        }

        if (outVoltage != -1) {
            stringBuilder.setLength(0);
            outVoltage_String = stringBuilder.append(outVoltage).append("V").toString();
        } else {
            outVoltage_String = "";
        }

        if (outCurrent != -1) {
            stringBuilder.setLength(0);
            outCurrent_String = stringBuilder.append(outCurrent).append("A").toString();
        } else {
            outCurrent_String = "";
        }
        if (outWarning != -1) {
            stringBuilder.setLength(0);
            for (int i = 0; i < Short.SIZE; i++) {
                stringBuilder.append(CurrentDetectionErrorCodeTable.matchErrorCodeInfo(i, (byte) ((outWarning >> i) & 0x01)));
            }
            outWarning_String = stringBuilder.toString();
        } else {
            outWarning_String = "无";
        }
    }

    /**
     * 是否电流上限
     *
     * @return
     */
    public boolean isCurrentLimited() {
        return outWarning != -1 && (outWarning & 0x01) == 0x01;
    }

    /**
     * 是否存在电流板
     *
     * @return
     */
    public boolean isExistDevice() {
        return isExistDevice;
    }

    @Override
    public String toString() {
        return "状态:" + status_String + "\t"
                + "输出电压:" + outVoltage_String + "\t"
                + "输出电流:" + outCurrent_String + "\t"
                + "故障告警:" + outWarning_String + "\t"
                + "软件版本:" + softwareVersion + "\t"
                + "硬件版本:" + hardwareVersion;
    }
}
