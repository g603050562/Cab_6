package client.halouhuandian.app15.devicesController.currentDetectionBoard;

import android.support.v4.util.Consumer;

import com.hellohuandian.pubfunction.Unit.LogUtil;

import client.halouhuandian.app15.MyApplication;
import client.halouhuandian.app15.devicesController.CanSender;

/**
 * Author:      Lee Yeung
 * Create Date: 2020/11/17
 * Description: 电流检测板控制器
 */
public final class CurrentDetectionController implements MyApplication.IFResultAppLinstener {
    private static final CurrentDetectionController CURRENT_DETECTION_CONTROLLER = new CurrentDetectionController();

    private final byte[] parserData = new byte[8];
    private final CurrentDetectionModel currentDetectionModel = new CurrentDetectionModel();
    private final CurrentDetectionParser currentDetectionParser = new CurrentDetectionParser();
    private Consumer<CurrentDetectionModel> consumer;
    private float currentThreshold;

    private CurrentDetectionController() {
        currentDetectionModel.parseFinish();
    }

    public static CurrentDetectionController getInstance() {
        return CURRENT_DETECTION_CONTROLLER;
    }

    protected boolean isMatched(byte[] data) {
        return (data != null && data.length == 16)
                && ((data[3] & 0xFF) == 0x98 && (data[2] & 0xFF) == 0xC0 && (data[1] & 0xFF) == 0xFF && (data[0] & 0xFF) == 0x68);
    }

    protected void onReceive(byte[] data) {
        System.arraycopy(data, 8, parserData, 0, 8);
        currentDetectionParser.parse(currentDetectionModel, parserData);
        currentDetectionModel.parseFinish();
        update(currentDetectionModel);
    }

    public CurrentDetectionModel optCurrentDetectionModel() {
        return currentDetectionModel;
    }

    /**
     * 设置电流检测板参数
     *
     * @param currentThreshold   电流阈值设置：数据分辨率:0.01A，0A偏移量 若不设置，默认3.5A
     *                           例如设置 3.00A，则相应发送数 据为300，即 2C 01
     * @param currentLimitedTime 电流板允许电流超限时间：
     *                           数据分辨率:1ms，0ms偏移量 若不设置，默认800ms
     *                           例如设置 1000ms，则相应发送数 据为1000，即 E8 03
     * @param relayRecoveryTime  继电器断开后恢复时间：
     *                           数据分辨率:1ms，0ms偏移量 若不设置，默认1S
     *                           例如设置 1000ms，则相应发送数 据为1000，即 E8 03
     * @return true:设置的参数符合限制要求，并不代表设置的参数会立即生效。
     */
    public boolean enabledCurrentDetection(float currentThreshold, int currentLimitedTime, int relayRecoveryTime) {
        final boolean isValRight = currentThreshold >= 1.5f && currentLimitedTime >= 100 && relayRecoveryTime >= 100;

        if (isValRight) {
            final byte enable = 0x00;
            final byte[] setCurrentDetection_CMD = new byte[]{(byte) 0x65, 0x68, (byte) 0xC0, (byte) 0x98
                    , 0x08
                    , 0x00, 0x00, 0x00
                    , enable, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
            };

            final short currentThresholdVal = (short) (currentThreshold * 100);
            setCurrentDetection_CMD[9] = (byte) (currentThresholdVal & 0xFF);
            setCurrentDetection_CMD[10] = (byte) ((currentThresholdVal >> 8) & 0xFF);

            setCurrentDetection_CMD[11] = (byte) (currentLimitedTime & 0xFF);
            setCurrentDetection_CMD[12] = (byte) ((currentLimitedTime >> 8) & 0xFF);

            setCurrentDetection_CMD[13] = (byte) (relayRecoveryTime & 0xFF);
            setCurrentDetection_CMD[14] = (byte) ((relayRecoveryTime >> 8) & 0xFF);

            CanSender.getInstance().send(setCurrentDetection_CMD);
            this.currentThreshold = currentThreshold;
        }

        return isValRight;
    }

    /**
     * 禁用电流检测板，会停止限制电流到达阈值报警
     */
    public void disabledCurrentDetection() {
        final byte enable = 0x01;
        final byte[] setCurrentDetection_CMD = new byte[]{(byte) 0x65, 0x68, (byte) 0xC0, (byte) 0x98
                , 0x08
                , 0x00, 0x00, 0x00
                , enable, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
        };

        CanSender.getInstance().send(setCurrentDetection_CMD);
    }

    @Override
    public void onCanResultApp(byte[] canData) {
        if (isMatched(canData)) {
            onReceive(canData);
        }
    }

    @Override
    public void onSerialResultApp(byte[] serData) {

    }

    private void update(CurrentDetectionModel currentDetectionModel) {
        if (consumer != null) {
            consumer.accept(currentDetectionModel);
        }
    }

    public void setConsumer(Consumer<CurrentDetectionModel> consumer) {
        this.consumer = consumer;
    }

    public void setCurrentDetection(float temperature) {
        float litVal = 3;
        if (temperature >= 25) {
            litVal = 3;
        } else if (temperature >= 10) {
            litVal = 4;
            litVal += 1;
        } else if (temperature >= -5) {
            litVal = 5;
            litVal += 1;
        } else if (temperature >= -10) {
            litVal = 6;
            litVal += 1;
        } else if (temperature >= -25) {
            litVal = 7.5f;
            litVal += 1;
        } else if (temperature >= -40) {
            litVal = 10;
            litVal += 1;
        }

        if (currentThreshold != litVal) {
            enabledCurrentDetection(litVal, 800, 1000);
            LogUtil.I("电流板设置阈值：" + litVal);
        }
    }

    public float getCurrentThreshold() {
        return currentThreshold;
    }
}

