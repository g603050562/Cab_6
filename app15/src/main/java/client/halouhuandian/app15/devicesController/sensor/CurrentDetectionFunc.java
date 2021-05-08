package client.halouhuandian.app15.devicesController.sensor;

import client.halouhuandian.app15.devicesController.currentDetectionBoard.CurrentDetectionController;
import client.halouhuandian.app15.devicesController.currentDetectionBoard.CurrentDetectionModel;

/**
 * Author:      Lee Yeung
 * Create Date: 2021/5/8
 * Description:
 */
public class CurrentDetectionFunc {
    private static final CurrentDetectionFunc CURRENT_DETECTION_FUNC = new CurrentDetectionFunc();

    private CurrentDetectionModel currentDetectionModel;
    private SensorDataBean envDataModel;

    private CurrentDetectionFunc() {
    }

    public static CurrentDetectionFunc getInstance() {
        return CURRENT_DETECTION_FUNC;
    }

    public boolean isExistDevice() {
        return getEnvDataModel() != null && getEnvDataModel().isExistCurrentBoardDevice() ? true : getCurrentDetectionModel() != null && getCurrentDetectionModel().isExistDevice();
    }

    public boolean isCurrentThresholdLimited() {
        return getEnvDataModel() != null & getEnvDataModel().isExistCurrentBoardDevice() ? getEnvDataModel().isCurrentThresholdLimited() : getCurrentDetectionModel() != null && getCurrentDetectionModel().isCurrentLimited();
    }

    private SensorDataBean getEnvDataModel() {
        if (envDataModel == null) {
            envDataModel = SensorController.getInstance().getSensorDataBean();
        }
        return envDataModel;
    }

    private CurrentDetectionModel getCurrentDetectionModel() {
        if (currentDetectionModel == null) {
            currentDetectionModel = CurrentDetectionController.getInstance().optCurrentDetectionModel();
        }
        return currentDetectionModel;
    }
}
