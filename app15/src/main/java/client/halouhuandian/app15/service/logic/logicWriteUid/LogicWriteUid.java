package client.halouhuandian.app15.service.logic.logicWriteUid;

import org.json.JSONException;

import client.halouhuandian.app15.hardWareConncetion.daa.DaaController;
import client.halouhuandian.app15.hardWareConncetion.daa.DaaIntegration;
import client.halouhuandian.app15.hardWareConncetion.daa.DaaSend;
import client.halouhuandian.app15.hardWareConncetion.daa.mode.DaaDataFormat;
import client.halouhuandian.app15.model.dao.fileSave.LocalLog;

/**
 * 电池写入逻辑
 */
public class LogicWriteUid {

    public interface LogicWriteUidListener {
        void returnStatus(boolean status);

        void showDialog(String msg, int time, int type) throws JSONException;
    }

    //写入电池的目标舱门
    private int tarDoor = -1;
    //需要写入的UID
    private String uid = "";
    //写入UID状态
    private int exchangeFailCount = 0;
    //下发指令
    private DaaSend daaSend;
    //电柜数据
    private DaaDataFormat daaDataFormat;
    //返回的接口
    private LogicWriteUidListener logicWriteUidListener = null;
    //接口
    private DaaController.DaaControllerListener daaControllerListener;

    public LogicWriteUid(int tarDoor, String uid, LogicWriteUidListener logicWriteUidListener) {
        this.tarDoor = tarDoor;
        this.uid = uid;
        this.logicWriteUidListener = logicWriteUidListener;
        init();
        onStart();
    }

    public LogicWriteUid(int tarDoor, String uid) {
        this.tarDoor = tarDoor;
        this.uid = uid;
        init();
        onStart();
    }

    private void init() {
        if (daaSend == null) {
            daaSend = new DaaSend();
        }
        if (daaControllerListener == null) {
            daaDataFormat = DaaController.getInstance().getDaaDataFormat();
            DaaController.getInstance().addListener(daaControllerListener = new DaaController.DaaControllerListener() {
                @Override
                public void returnData(DaaDataFormat mDaaDataFormat, DaaIntegration.ReturnDataType returnDataType, int index) {
                    daaDataFormat = mDaaDataFormat;
                }
            });
        }
    }

    private void onStart() {

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    //做超时等待
                    int result_out = 0;
                    for (int i = 0; i < 150; i++) {
                        if (i == 0 || i == 50 || i == 100) {
                            DaaSend.writeUid(tarDoor, uid);
                            LocalLog.getInstance().writeLog("写入UID - 舱门 - " + tarDoor + " - UID - " + uid + " - barUid - " + daaDataFormat.getDcdcInfoByBaseFormat(tarDoor - 1).getUID());
                        }
                        sleep(100);
                        String sUID = daaDataFormat.getDcdcInfoByBaseFormat(tarDoor - 1).getUID();
                        if (sUID.equals(uid)) {
                            result_out = 1;
                            break;
                        }
                    }
                    if (result_out == 0) {
                        //没有写入成功
                        if (exchangeFailCount == 0) {
                            if (logicWriteUidListener != null) {
                                logicWriteUidListener.showDialog(tarDoor + "号舱门电池信息写入失败，正在尝试二次写入，请稍候！！", 10, 1);
                            }
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    try {
                                        sleep(10000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    onStart();
                                    exchangeFailCount = 1;
                                }
                            }.start();
                        } else {
                            exchangeFailCount = 0;
                            if (logicWriteUidListener != null) {
                                logicWriteUidListener.returnStatus(false);
                            }
                        }
                    } else {
                        exchangeFailCount = 0;
                        if (logicWriteUidListener != null) {
                            logicWriteUidListener.returnStatus(true);
                        }
                    }
                    onDestroy();
                } catch (Exception e) {
                    onDestroy();
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void onDestroy() {
        DaaController.getInstance().deleteListener(daaControllerListener);
        daaControllerListener = null;
    }
}
