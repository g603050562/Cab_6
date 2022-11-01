package client.halouhuandian.app15.service.logic.logicFind4gCard;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import client.halouhuandian.app15.model.dao.fileSave.LocalLog;
import client.halouhuandian.app15.model.dao.sharedPreferences.CabInfoSp;
import client.halouhuandian.app15.pub.BaseDataDistribution;

import static java.lang.Thread.sleep;

/**
 * 获取4g卡 4600编号
 */

public class Find4gCard extends BaseDataDistribution {

    //单例
    private static volatile Find4gCard find4gCard = null;
    private Find4gCard() {};
    public static Find4gCard getInstance() {
        if (find4gCard == null) {
            synchronized (Find4gCard.class) {
                if (find4gCard == null) {
                    find4gCard = new Find4gCard();
                }
            }
        }
        return find4gCard;
    }

    private String returnIMSI = "";

    public void init(final Context context) {
        LocalLog.getInstance().writeLog("4G模块儿初始化", Find4gCard.class);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int state = -1;
                    for (int i = 0; i < 120; i++) {
                        sleep(500);

                        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }

                        returnIMSI = mTelephonyMgr.getSubscriberId();

                        if (returnIMSI == null || returnIMSI.equals("") || returnIMSI.equals("null")) {
                            LocalLog.getInstance().writeLog("正在寻找4g模块儿", Find4gCard.class);
                        } else {
                            state = 1;
                            break;
                        }
                    }

                    if (state == 1) {
                        LocalLog.getInstance().writeLog("获取到4g卡号 - " + returnIMSI, Find4gCard.class);
                        CabInfoSp.getInstance().setCabinetNumber_4600XXXX(returnIMSI);
                        sendData(new Find4gCardReturnDataFormat(Find4gCardReturnDataFormat.Find4gCardReturnDataType.IMSI , returnIMSI));
                    } else if (state == -1) {
                        LocalLog.getInstance().writeLog("未获取到4g卡号，电柜将要重启", Find4gCard.class);
                        sendData(new Find4gCardReturnDataFormat(Find4gCardReturnDataFormat.Find4gCardReturnDataType.error , "未获取到4g卡号，电柜将要重启"));
                    }

                    onDestroy();
                } catch (Exception e) {
                    LocalLog.getInstance().writeLog(e.toString(), Find4gCard.class);
                }
            }
        }).start();
    }

    @Override
    public void addListener(LogicListener listener) {
        super.addListener(listener);
        if (returnIMSI != null && !returnIMSI.equals("")) {
            listener.returnData(new Find4gCardReturnDataFormat(Find4gCardReturnDataFormat.Find4gCardReturnDataType.IMSI , returnIMSI));
        }
    }

    private void onDestroy() {
    }

}
