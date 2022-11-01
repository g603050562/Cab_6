package client.halouhuandian.app15.service.logic.logicNetDBM;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import client.halouhuandian.app15.model.dao.fileSave.LocalLog;
import client.halouhuandian.app15.pub.BaseDataDistribution;

/**
 * 获取4g信号值
 */

public class DataDistributionCurrentNetDBM extends BaseDataDistribution {

    //单例
    private static volatile DataDistributionCurrentNetDBM logicCurrentNetDBM;
    private DataDistributionCurrentNetDBM(){};
    public static DataDistributionCurrentNetDBM getInstance (){
        if(logicCurrentNetDBM == null){
            synchronized (DataDistributionCurrentNetDBM.class){
                if(logicCurrentNetDBM == null){
                    logicCurrentNetDBM = new DataDistributionCurrentNetDBM();
                }
            }
        }
        return logicCurrentNetDBM;
    }
    //最终返回值
    private int dbm = 0;

    public void init(final Context context) {
        LocalLog.getInstance().writeLog("DBM模块儿初始化" , DataDistributionCurrentNetDBM.class);
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        PhoneStateListener mylistener = new PhoneStateListener() {
            //            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                super.onSignalStrengthsChanged(signalStrength);
                String signalInfo = signalStrength.toString();
                String[] params = signalInfo.split(" ");

                if (tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_LTE) {
                    //4G网络 最佳范围   >-90dBm 越大越好
                    int Itedbm = Integer.parseInt(params[9]);

                    dbm = Itedbm;

                } else if (tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSDPA ||
                        tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSPA ||
                        tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSUPA ||
                        tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS) {
                    //3G网络最佳范围  >-90dBm  越大越好  ps:中国移动3G获取不到  返回的无效dbm值是正数（85dbm）
                    //在这个范围的已经确定是3G，但不同运营商的3G有不同的获取方法，故在此需做判断 判断运营商与网络类型的工具类在最下方
                    String yys = getOperator(context);//获取当前运营商
                    if (yys == "中国移动") {
                    } else if (yys == "中国联通") {
                        int cdmaDbm = signalStrength.getCdmaDbm();
                        dbm = cdmaDbm;
                    } else if (yys == "中国电信") {
                        int evdoDbm = signalStrength.getEvdoDbm();
                        dbm = evdoDbm;
                    }

                } else {
                    //2G网络最佳范围>-90dBm 越大越好
                    int asu = signalStrength.getGsmSignalStrength();
                    int dbm_1 = -113 + 2 * asu;
                    dbm = dbm_1;
                }
                sendData(dbm);
            }
        };
        //开始监听
        tm.listen(mylistener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    public int getDbm() {
        return dbm;
    }

    @SuppressLint("MissingPermission")
    public static String getOperator(Context context) {
        String ProvidersName = "";
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String IMSI = telephonyManager.getSubscriberId();
        Log.i("qweqwes", "运营商代码" + IMSI);
        if (IMSI != null) {
            if (IMSI.startsWith("46000") || IMSI.startsWith("46002") || IMSI.startsWith("46007")) {
                ProvidersName = "中国移动";
            } else if (IMSI.startsWith("46001") || IMSI.startsWith("46006")) {
                ProvidersName = "中国联通";
            } else if (IMSI.startsWith("46003")) {
                ProvidersName = "中国电信";
            }
            return ProvidersName;
        } else {
            return "没有获取到sim卡信息";
        }
    }

}
