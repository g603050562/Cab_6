package client.halouhuandian.app15.devicesController.currentDetectionBoard;

/**
 * Author:      Lee Yeung
 * Create Date: 2020/11/17
 * Description:
 */
class CurrentDetectionData {
    /**
     * 0x00:待机
     * 0x01:故障
     * 0x02:告警
     * 0x03:禁止
     */
    byte status = -1;

    /**
     * 输出电压数据
     * 数据分辨率:0.01V，0V偏移量
     */
    float outVoltage = -1;

    /**
     * 输出电流数据
     * 数据分辨率:0.01A，0A偏移量
     */
    float outCurrent = -1;

    /**
     * 故障告警位
     * <p>
     * Bit1:电流超过阈值
     * Bit2:继电器异常
     * Bit3:Can通讯异常
     * Bit4:电流采样异常
     * Bit5:电压采样异常
     * Bit6:继电器状态
     * Bit7~Bit16:预留
     */
    byte outWarning = -1;

    byte hardwareVersion;
    byte softwareVersion;

    public float getOutCurrent() {
        return outCurrent;
    }

    public byte getHardwareVersion() {
        return hardwareVersion;
    }

    public byte getSoftwareVersion() {
        return softwareVersion;
    }
}
