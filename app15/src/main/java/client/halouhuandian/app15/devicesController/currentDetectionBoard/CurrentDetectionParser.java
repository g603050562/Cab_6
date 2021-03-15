package client.halouhuandian.app15.devicesController.currentDetectionBoard;

/**
 * Author:      Lee Yeung
 * Create Date: 2020/11/17
 * Description:
 */
final class CurrentDetectionParser
{
    private CurrentDetectionData currentDetectionData;

    void parse(CurrentDetectionData currentDetectionData, byte[] bytes)
    {
        if (currentDetectionData != null && bytes != null && bytes.length >= 8)
        {
            this.currentDetectionData = currentDetectionData;
            parse_status(bytes);
            parse_outVoltage(bytes);
            parse_outCurrent(bytes);
            parse_outWarning(bytes);
            parse_softwareVersion(bytes);
            parse_hardwareVersion(bytes);
        }
    }

    private void parse_status(byte[] bytes)
    {
        currentDetectionData.status = (byte) (bytes[0] & 0xFF);
    }

    private void parse_outVoltage(byte[] bytes)
    {
        final short value = (short) ((bytes[1] & 0xFF) | ((bytes[2] & 0xFF) << 8));
        currentDetectionData.outVoltage = value / 100f;
    }

    private void parse_outCurrent(byte[] bytes)
    {
        final short value = (short) ((bytes[3] & 0xFF) | ((bytes[4] & 0xFF) << 8));
        currentDetectionData.outCurrent = value / 100f;
    }

    private void parse_outWarning(byte[] bytes)
    {
        currentDetectionData.outWarning = (byte) (bytes[5] & 0xFF);
    }

    private void parse_softwareVersion(byte[] bytes)
    {
        currentDetectionData.softwareVersion = (byte) (bytes[6] & 0xFF);
    }

    private void parse_hardwareVersion(byte[] bytes)
    {
        currentDetectionData.hardwareVersion = (byte) (bytes[7] & 0xFF);
    }
}
