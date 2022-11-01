package client.halouhuandian.app15.service.logic.logicOpenDoor;

import org.json.JSONException;

import client.halouhuandian.app15.hardWareConncetion.daa.DaaController;
import client.halouhuandian.app15.hardWareConncetion.daa.DaaIntegration;
import client.halouhuandian.app15.hardWareConncetion.daa.DaaSend;
import client.halouhuandian.app15.hardWareConncetion.daa.mode.DaaDataFormat;
import client.halouhuandian.app15.hardWareConncetion.environmentPlate.EnvironmentController;
import client.halouhuandian.app15.hardWareConncetion.environmentPlate.EnvironmentDataFormat;
import client.halouhuandian.app15.hardWareConncetion.environmentPlate.EnvironmentReturnDataFormat;
import client.halouhuandian.app15.model.dao.fileSave.LocalLog;
import client.halouhuandian.app15.pub.BaseDataDistribution;

public class LogicOpenDoor {

    //舱门操作返回监听
    public interface LogicOpenDoorAsynchronousListener {
        void returnData(LogicOpenDoorReturnDataFormat logicOpenDoorReturnDataFormat) throws InterruptedException;
    }

    //舱门操作提示dialog返回
    public interface LogicOpenDoorDialogListener {
        void showDialog(String msg, int time, int type) throws JSONException;
    }

    //dcl单例
    private volatile static LogicOpenDoor logicOpenDoor = null;
    private LogicOpenDoor(){};
    public static LogicOpenDoor getInstance() {
        if (logicOpenDoor == null) {
            synchronized (LogicOpenDoor.class) {
                if (logicOpenDoor == null) {
                    logicOpenDoor = new LogicOpenDoor();
                }
            }
        }
        return logicOpenDoor;
    }

    //电柜数据
    private DaaDataFormat daaDataFormat;
    //分发接口
    private DaaController.DaaControllerListener daaControllerListener;
    //舱门开启或关闭失败 延时检测时间
    private int delayDetectionTime = 3 * 1000;
    //dcdc打开以及关闭舱门
    int[] interrupts = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}; //延时中断参数     0 - 初始化    1 - 被中断    2 - 正在进行

    public void init() {
        DaaController.getInstance().addListener(daaControllerListener = new DaaController.DaaControllerListener() {
            @Override
            public void returnData(DaaDataFormat mDaaDataFormat, DaaIntegration.ReturnDataType returnDataType, int index) {
                daaDataFormat = mDaaDataFormat;
            }
        });
    }



    //dcdc收回舱门
    public void putterPull(int door, int pushrodActSetTime, String info) {
        putterPull(door , pushrodActSetTime , info , 1);
    }
    //dcdc收回舱门 type - 0 - 不会中断 - 1 - 会中断
    private void putterPull(int door, int pushrodActSetTime, String info , int type) {
        DaaSend.putterPull(door, pushrodActSetTime);
//        if (interrupts[door - 1] == 2 && type == 1) {
//            interrupts[door - 1] = 1;
//        }
        if (type == 1) {
            interrupts[door - 1] = 1;
        }
        LocalLog.getInstance().writeLog("收回舱门 - " + door + " - 持续时间 - " + pushrodActSetTime + " - 信息 - " + info + " - 类型 - 不返回结果");
    }



    //dcdc收回舱门并异步返回结果
    public void putterPullAndReturnResult(final int door, final int pushrodActSetTime, final String info, final LogicOpenDoorAsynchronousListener logicOpenDoorAsynchronousListener) {
        putterPullAndReturnResult(door , pushrodActSetTime , info , logicOpenDoorAsynchronousListener , 1);
    }
    //dcdc收回舱门并异步返回结果 type - 0 - 不会中断 - 1 - 会中断
    private void putterPullAndReturnResult(final int door, final int pushrodActSetTime, final String info, final LogicOpenDoorAsynchronousListener logicOpenDoorAsynchronousListener , int type) {
        DaaSend.putterPull(door, pushrodActSetTime);
//        if (interrupts[door - 1] == 2 && type == 1) {
//            interrupts[door - 1] = 1;
//        }
        if (type == 1) {
            interrupts[door - 1] = 1;
        }
        int inchingBefore = daaDataFormat.getDcdcInfoByBaseFormat(door - 1).getInchingByOuterClose();
        LocalLog.getInstance().writeLog("收回舱门 - " + door + " - 持续时间 - " + pushrodActSetTime + " - 信息 - " + info + " - 类型 - 返回结果 - 当前微动状态 - " + inchingBefore);
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                long sleepTime = pushrodActSetTime * 100 + delayDetectionTime;
                try {
                    sleep(sleepTime);
                    int inchingAfter = daaDataFormat.getDcdcInfoByBaseFormat(door - 1).getInchingByOuterClose();
                    if (inchingAfter == 1) {
                        LocalLog.getInstance().writeLog("收回舱门 - " + door + " - 结果 - 成功 - 当前微动状态 - " + inchingAfter);
                        logicOpenDoorAsynchronousListener.returnData(new LogicOpenDoorReturnDataFormat(true, "关门成功"));
                    } else if (inchingAfter == 0) {
                        LocalLog.getInstance().writeLog("收回舱门 - " + door + " - 结果 - 失败 - 当前微动状态 - " + inchingAfter);
                        logicOpenDoorAsynchronousListener.returnData(new LogicOpenDoorReturnDataFormat(false, "关门失败"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }



    //dcdc打开舱门
    public void putterPush(int door, int pushrodActSetTime, String info) {
        putterPush(door , pushrodActSetTime ,info , 1);
    }
    //dcdc打开舱门 type - 0 - 不会中断 - 1 - 会中断
    private void putterPush(int door, int pushrodActSetTime, String info , int type) {
        DaaSend.putterPush(door, pushrodActSetTime);
//        if (interrupts[door - 1] == 2 && type == 1) {
//            interrupts[door - 1] = 1;
//        }
        if (type == 1) {
            interrupts[door - 1] = 1;
        }
        LocalLog.getInstance().writeLog("打开舱门 - " + door + " - 推杆持续时间 - " + pushrodActSetTime + " - 信息 - " + info + " - 类型 - 不返回结果");
    }




    //dcdc打开舱门并异步返回结果
    public void putterPushAndReturnResult(final int door, final int pushrodActSetTime, final String info, final LogicOpenDoorAsynchronousListener logicOpenDoorAsynchronousListener) {
        putterPushAndReturnResult(door , pushrodActSetTime , info , logicOpenDoorAsynchronousListener , 1);
    }
    //dcdc打开舱门并异步返回结果 type - 0 - 不会中断 - 1 - 会中断

    private void putterPushAndReturnResult(final int door, final int pushrodActSetTime, final String info, final LogicOpenDoorAsynchronousListener logicOpenDoorAsynchronousListener , int type) {
        DaaSend.putterPush(door, pushrodActSetTime);
//        if (interrupts[door - 1] == 2 && type == 1) {
//            interrupts[door - 1] = 1;
//        }
        if (type == 1) {
            interrupts[door - 1] = 1;
        }
        int inchingBefore = daaDataFormat.getDcdcInfoByBaseFormat(door - 1).getInchingByOuterClose();
        LocalLog.getInstance().writeLog("打开舱门 - " + door + " - 推杆持续时间 - " + pushrodActSetTime + " - 信息 - " + info + " - 类型 - 返回结果 - 当前微动状态 - " + inchingBefore);
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                long sleepTime = pushrodActSetTime * 100 + delayDetectionTime;
                try {
                    int inchingBefore = daaDataFormat.getDcdcInfoByBaseFormat(door - 1).getInchingByOuterOpen();
                    sleep(sleepTime);
                    if(inchingBefore == -1){
                        int inchingAfter = daaDataFormat.getDcdcInfoByBaseFormat(door - 1).getInchingByOuterClose();
                        if (inchingAfter == 0) {
                            LocalLog.getInstance().writeLog("打开舱门 - " + door + " - 结果 - 成功 - 当前微动状态 - " + inchingAfter);
                            logicOpenDoorAsynchronousListener.returnData(new LogicOpenDoorReturnDataFormat(true, "开门成功"));
                        } else if (inchingAfter == 1) {
                            LocalLog.getInstance().writeLog("打开舱门 - " + door + " - 结果 - 失败  - 当前微动状态 - " + inchingAfter);
                            logicOpenDoorAsynchronousListener.returnData(new LogicOpenDoorReturnDataFormat(false, "开门失败"));
                        }
                    }else{
                        int inchingAfter = daaDataFormat.getDcdcInfoByBaseFormat(door - 1).getInchingByOuterOpen();
                        if (inchingAfter == 1) {
                            LocalLog.getInstance().writeLog("打开舱门 - " + door + " - 结果 - 成功 - 当前微动状态 - " + inchingAfter);
                            logicOpenDoorAsynchronousListener.returnData(new LogicOpenDoorReturnDataFormat(true, "开门成功"));
                        } else if (inchingAfter == 0) {
                            LocalLog.getInstance().writeLog("打开舱门 - " + door + " - 结果 - 失败  - 当前微动状态 - " + inchingAfter);
                            logicOpenDoorAsynchronousListener.returnData(new LogicOpenDoorReturnDataFormat(false, "开门失败"));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }



    public void putterPushAndPullContainBattery(final int door, final int pushrodActSetTime, final int intervalTime, final String info, final LogicOpenDoorDialogListener logicOpenDoorCountDownListener) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
//                    //如果已经打开舱门了 就不需要二次进行后续了
//                    if (interrupts[door - 1] == 2) {
//                        return;
//                    } else {
//                        //确认已经打开舱门
//                        interrupts[door - 1] = 2;
//                    }
                    interrupts[door - 1] = 0;
                    //打开舱门并且附带返回结果
                    putterPushAndReturnResult(door, pushrodActSetTime, info, new LogicOpenDoorAsynchronousListener() {
                        @Override
                        public void returnData(LogicOpenDoorReturnDataFormat logicOpenDoorReturnDataFormat) throws InterruptedException {
                            if (!logicOpenDoorReturnDataFormat.getResult()) {
                                try {
                                    putterPush(door, pushrodActSetTime, info + " - 二次打开" , 0);
                                    logicOpenDoorCountDownListener.showDialog(door + "号舱门正在尝试再次打开", 5, 1);
                                } catch (Exception e) {
                                    LocalLog.getInstance().writeLog(e.toString(), LogicOpenDoor.class);
                                }
                            }
                        }
                    } , 0);


                    //在规定时间内循环等待关门触发
                    LocalLog.getInstance().writeLog("舱门将在"+intervalTime+"秒后关闭");
                    for (int i = 0; i < intervalTime; i++) {
                        //检测是否被其他开关门操作中断 中断的话就不用继续了
                        if (interrupts[door - 1] == 1) {
                            interrupts[door - 1] = 0;
                            LocalLog.getInstance().writeLog("包含电池开门延时线程被中断");
                            break;
                        }
                        //如果里侧微动检测到电池已经没了 5秒后关闭舱门
//                        int newInchingByInner = daaDataFormat.getDcdcInfoByBaseFormat(door - 1).getInchingByInner();
                        String bid = daaDataFormat.getDcdcInfoByBaseFormat(door - 1).getBID();
                        if (bid.equals("0000000000000000")) {
                            logicOpenDoorCountDownListener.showDialog(door + "号舱门将在<$time>秒后关闭，请注意安全！", 5, 1);
                            sleep(5000);
                            break;
                        }
                        //最后5秒触发一次警告检测
                        if (i == intervalTime - 5 && interrupts[door - 1] != 1) {
                            logicOpenDoorCountDownListener.showDialog(door + "号舱门将在<$time>秒后关闭，请注意安全！", 5, 1);
                        }
                        sleep(1000);
                    }


                    //检测是否被其他开关门操作中断 中断的话就不用继续了
                    if (interrupts[door - 1] == 1) {
                        interrupts[door - 1] = 0;
                    } else {
                        //关门操作
                        putterPullAndReturnResult(door, pushrodActSetTime, info, new LogicOpenDoorAsynchronousListener() {
                            @Override
                            public void returnData(LogicOpenDoorReturnDataFormat logicOpenDoorReturnDataFormat) throws InterruptedException {
                                //确认已经关闭舱门
                                if (!logicOpenDoorReturnDataFormat.getResult()) {
                                    try {
                                        putterPull(door, pushrodActSetTime, info + " - 二次关闭" , 0);
                                        logicOpenDoorCountDownListener.showDialog(door + "号舱门正在尝试再次关闭", 5, 1);
                                    } catch (Exception e) {
                                        LocalLog.getInstance().writeLog(e.toString(), LogicOpenDoor.class);
                                    }
                                }
                            }
                        } , 0);
                    }
                } catch (Exception e) {
                    LocalLog.getInstance().writeLog(e.toString(), LogicOpenDoor.class);
                }
            }
        };
        thread.start();
    }

    //dcdc打开以及关闭舱门并且附带返回结果 不包含电池打开
    public void putterPushAndPullExcludeBattery(final int door, final int pushrodActSetTime, final int intervalTime, final String info, final LogicOpenDoorDialogListener logicOpenDoorCountDownListener) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {

//                    //如果已经打开舱门了 就不需要二次进行后续了
//                    if (interrupts[door - 1] == 2) {
//                        return;
//                    } else {
//                        //确认已经打开舱门
//                        interrupts[door - 1] = 2;
//                    }
                    interrupts[door - 1] = 0;
                    //打开舱门并且附带返回结果
                    putterPushAndReturnResult(door, pushrodActSetTime, info, new LogicOpenDoorAsynchronousListener() {
                        @Override
                        public void returnData(LogicOpenDoorReturnDataFormat logicOpenDoorReturnDataFormat) throws InterruptedException {
                            if (!logicOpenDoorReturnDataFormat.getResult()) {
                                try {
                                    putterPush(door, pushrodActSetTime, info + " - 二次打开" , 0);
                                    logicOpenDoorCountDownListener.showDialog(door + "号舱门正在尝试再次打开", 5, 1);
                                } catch (Exception e) {
                                    LocalLog.getInstance().writeLog(e.toString(), LogicOpenDoor.class);
                                }
                            }
                        }
                    },0);

                    //在规定时间内循环等待关门触发
                    LocalLog.getInstance().writeLog("舱门将在"+intervalTime+"秒后关闭");
                    for (int i = 0; i < intervalTime; i++) {
                        //检测是否被其他开关门操作中断 中断的话就不用继续了
                        if (interrupts[door - 1] == 1) {
                            interrupts[door - 1] = 0;
                            LocalLog.getInstance().writeLog("不包含电池开门延时线程被中断");
                            break;
                        }
                        //最后5秒触发一次警告检测
                        if (i == intervalTime - 5 && interrupts[door - 1] != 1) {
                            logicOpenDoorCountDownListener.showDialog(door + "号舱门将在<$time>秒后关闭，请注意安全！", 5, 1);
                        }
                        sleep(1000);

                    }

                    //检测是否被其他开关门操作中断 中断的话就不用继续了
                    if (interrupts[door - 1] == 1) {
                        interrupts[door - 1] = 0;
                    } else {
                        //关门操作
                        putterPullAndReturnResult(door, pushrodActSetTime, info, new LogicOpenDoorAsynchronousListener() {
                            @Override
                            public void returnData(LogicOpenDoorReturnDataFormat logicOpenDoorReturnDataFormat) throws InterruptedException {
                                //确认已经关闭舱门
                                if (!logicOpenDoorReturnDataFormat.getResult()) {
                                    try {
                                        putterPull(door, pushrodActSetTime, info + " - 二次关闭" , 0);
                                        logicOpenDoorCountDownListener.showDialog(door + "号舱门正在尝试再次关闭", 5, 1);
                                    } catch (Exception e) {
                                        LocalLog.getInstance().writeLog(e.toString(), LogicOpenDoor.class);
                                    }
                                }
                            }
                        } , 0);
                    }
                } catch (Exception e) {
                    LocalLog.getInstance().writeLog(e.toString(), LogicOpenDoor.class);
                }
            }
        };
        thread.start();
    }

    public void onDestroy() {
        DaaController.getInstance().deleteListener(daaControllerListener);
    }

}
