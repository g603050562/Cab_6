package client.halouhuandian.app15.devicesController.rod;

import android.os.SystemClock;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import client.halouhuandian.app15.A_Main2;
import client.halouhuandian.app15.devicesController.currentDetectionBoard.CurrentDetectionController;
import client.halouhuandian.app15.devicesController.currentDetectionBoard.CurrentDetectionModel;
import client.halouhuandian.app15.devicesController.sensor.CurrentDetectionFunc;

/**
 * Author:      Lee Yeung
 * Create Date: 2020/11/17
 * Description: 操作门动作控制器
 */
public final class DoorController {
    private static final DoorController DOOR_CONTROLLER = new DoorController();

    private DoorController() {
    }

    public static DoorController getInstance() {
        return DOOR_CONTROLLER;
    }

    /**
     * 关门操作同时检测侧微动状态后，立即停止。
     * 调用测方法需要在子线程中执行。
     *
     * @param batteryDataModel
     */
    public void closeDoor(BatteryDataModel batteryDataModel) {
        closeDoor(batteryDataModel, 3);
    }

    public void closeDoor(BatteryDataModel batteryDataModel, int closeDoorTryTimes) {
        if (batteryDataModel != null) {
            final CurrentDetectionModel currentDetectionModel = CurrentDetectionController.getInstance().optCurrentDetectionModel();
            int tryTimes = closeDoorTryTimes;
            SystemClock.sleep(500);

            do {
                RodDataController.getInstance().closeDoor(batteryDataModel.doorNumber);
                long stopTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(15);
                while (System.currentTimeMillis() <= stopTime) {
                    if (batteryDataModel.isSideMicroswitchPressed()) {
                        // TODO: 2020/11/25  侧微动压住延迟25ms停止推杆
                        final int stopDelayTime = 25;
                        SystemClock.sleep(stopDelayTime);
                        RodDataController.getInstance().stop(batteryDataModel.doorNumber);
                        A_Main2.writeLocalLog("关门操作-" + batteryDataModel.doorNumber + "号仓侧微动触发：发送停止推杆");
                        break;
                    }

                    if (currentDetectionModel != null && currentDetectionModel.isExistDevice() && currentDetectionModel.isCurrentLimited()) {
                        RodDataController.getInstance().stop(batteryDataModel.doorNumber);
                        if (currentDetectionModel.isCurrentLimited()) {
                            while (currentDetectionModel.isCurrentLimited()) {
                                continue;
                            }
                        } else {
                            SystemClock.sleep(500);
                        }

                        // TODO: 2020/11/24 反推
                        RodDataController.getInstance().openDoor(batteryDataModel.doorNumber, (byte) 5);
                        if (tryTimes > 0) {
                            // TODO: 2020/11/27 500ms开门的形成也需要处理电流板阈值报警停止推杆
                            final long stopTime_500ms = System.currentTimeMillis() + 500;
                            while (System.currentTimeMillis() < stopTime_500ms) {
                                if (currentDetectionModel.isCurrentLimited()) {
                                    RodDataController.getInstance().stop(batteryDataModel.doorNumber);
                                    A_Main2.writeLocalLog("关门操作-" + batteryDataModel.doorNumber + "号仓电流板阈：" + currentDetectionModel.outCurrent_String
                                            + "-电流板状态：" + currentDetectionModel.status_String
                                            + "-值触发触发：发送停止推杆");
                                    break;
                                }
                            }
                            SystemClock.sleep(TimeUnit.SECONDS.toMillis(5));
                        }
                        break;
                    }
                }
            }
            while (tryTimes-- > 1 && !batteryDataModel.isSideMicroswitchPressed());
        }
    }

    /**
     * 开门操作：必须在子线程执行
     *
     * @param batteryDataModel
     */
    public void openDoor(BatteryDataModel batteryDataModel) {
        openDoor(batteryDataModel, 1);
    }

    public void openDoor(BatteryDataModel batteryDataModel, int openDoorTryTimes) {

        // TODO: 2021/2/3 直接先开2.5S 
//        RodDataController.getInstance().openDoor(batteryDataModel.doorNumber, (byte) 25);
//        SystemClock.sleep(2600);

//        final CurrentDetectionModel currentDetectionModel = CurrentDetectionController.getInstance().optCurrentDetectionModel();
//        if (currentDetectionModel != null && openDoorTryTimes > 0) {
//
//            int tryTimes = openDoorTryTimes;
//            while (tryTimes-- > 0) {
//                if (!currentDetectionModel.isExistDevice()) {
//                    RodDataController.getInstance().openDoor(batteryDataModel.doorNumber, (byte) batteryDataModel.getRodActionTime());
//                    SystemClock.sleep(batteryDataModel.getRodActionTime() * 100);
//                    return;
//                }
//                RodDataController.getInstance().openDoor(batteryDataModel.doorNumber);
//                final long stopTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(15);
//                final long outTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(3);
//                while (System.currentTimeMillis() <= stopTime) {
//                    if (currentDetectionModel.isExistDevice() && (currentDetectionModel.isCurrentLimited() || System.currentTimeMillis() >= outTime && currentDetectionModel.getOutCurrent() <= 0.3f)) {
//                        RodDataController.getInstance().stop(batteryDataModel.doorNumber);
//                        A_Main2.writeLocalLog("开门操作-" + batteryDataModel.doorNumber + "号仓电流板阈：" + currentDetectionModel.outCurrent_String
//                                + "-电流板状态：" + currentDetectionModel.status_String
//                                + "-值触发触发：发送停止推杆");
//                        if (currentDetectionModel.isCurrentLimited()) {
//                            while (currentDetectionModel.isCurrentLimited()) {
//                                continue;
//                            }
//                        } else {
//                            SystemClock.sleep(500);
//                        }
//
//                        if (openDoorTryTimes == 1) {
//                            RodDataController.getInstance().closeDoor(batteryDataModel.doorNumber, (byte) 1);
//                        } else {
//                            if (tryTimes > 0) {
//                                RodDataController.getInstance().closeDoor(batteryDataModel.doorNumber, (byte) 1);
//                                SystemClock.sleep(TimeUnit.SECONDS.toMillis(3));
//                            }
//                        }
//                        break;
//                    }
//                }
//            }
//        }













        SystemClock.sleep(500);

        if (CurrentDetectionFunc.getInstance().isExistDevice())
        {
            do
            {
                RodDataController.getInstance().openDoor(batteryDataModel.doorNumber);
                long stopTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10);
                while (System.currentTimeMillis() <= stopTime)
                {
                    if (batteryDataModel.isOpenMicroswitchNormal() && batteryDataModel.isOpenMicroswitchPressed())
                    {
                        final int stopDelayTime = 25;
                        SystemClock.sleep(stopDelayTime);
                        RodDataController.getInstance().stop(batteryDataModel.doorNumber);
                        break;
                    }

                    if (CurrentDetectionFunc.getInstance().isCurrentThresholdLimited())
                    {
                        RodDataController.getInstance().stop(batteryDataModel.doorNumber);
                        if (CurrentDetectionFunc.getInstance().isCurrentThresholdLimited())
                        {
                            while (CurrentDetectionFunc.getInstance().isCurrentThresholdLimited())
                            {
                                continue;
                            }
                        } else
                        {
                            SystemClock.sleep(500);
                        }

                        if (openDoorTryTimes <= 1)
                        {
                            RodDataController.getInstance().closeDoor(batteryDataModel.doorNumber, (byte) 1);
                        } else
                        {
                            RodDataController.getInstance().closeDoor(batteryDataModel.doorNumber, (byte) 5);
                            SystemClock.sleep(TimeUnit.SECONDS.toMillis(3));
                        }
                        break;
                    }
                }
            }
            while (openDoorTryTimes-- > 1 && !batteryDataModel.isOpenMicroswitchPressed());
        } else//没有电流控制板使用推杆时间控制
        {
//            float pushrodTime = AppSpSaver.getInstance().optPushRodTime();
//            final byte openDuration = (byte) (pushrodTime * 10);
//            RodDataController.getInstance().openDoor(batteryDataModel.doorNumber, openDuration);
//            boolean isV4_ = false;
//            ArrayList<DcDcDataModel> dcDcDataModels = DcDcDataController.getInstance().getDcDcDataModels();
//            if (dcDcDataModels != null && batteryDataModel.sn >= 0 && batteryDataModel.sn < dcDcDataModels.size())
//            {
//                isV4_ = dcDcDataModels.get(batteryDataModel.sn).getHardwareVersion() >= 4;
//            }
//
//            if (isV4_)
//            {
//                long stopTime = System.currentTimeMillis() + (int) (pushrodTime * 1000);
//                while (System.currentTimeMillis() <= stopTime)
//                {
//                    if (batteryDataModel.isOpenMicroswitchPressed())
//                    {
//                        final int stopDelayTime = 25;
//                        SystemClock.sleep(stopDelayTime);
//                        RodDataController.getInstance().stop(batteryDataModel.doorNumber);
//                        break;
//                    }
//                }
//            } else
//            {
//                SystemClock.sleep((long) (pushrodTime * 1000));
//            }
        }
    }
}
