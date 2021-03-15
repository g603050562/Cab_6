package client.halouhuandian.app15.devicesController.currentDetectionBoard;

/**
 * Author:      Lee Yeung
 * Create Date: 2020/4/14
 * Description:
 */
final class CurrentDetectionErrorCodeTable
{
    private static final String[] currentDetectionWarning = {
            "电流超过阈值:",
            "继电器异常",
            "Can通讯异常",
            "电流采样异常",
            "电压采样异常",
            "继电器状态",
    };

    static String matchErrorCodeInfo(int warningStatusIndex, byte value)
    {
        if (value == 0x01 && warningStatusIndex >= 0 && warningStatusIndex < currentDetectionWarning.length)
        {
            return currentDetectionWarning[warningStatusIndex];
        }
        return "";
    }
}
