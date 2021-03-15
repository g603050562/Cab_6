package client.halouhuandian.app15;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.lang.reflect.Method;

/**
 * Author:      Lee Yeung
 * Create Date: 2020/6/15
 * Description:
 */
public final class CabinetUtil {
    /**
     * 获取柜子号码(android设备唯一的表示ID)
     *
     * @param context
     * @return
     */
    @SuppressLint("MissingPermission")
    public static String optCabinetNumber(Context context) {
        return optUniqueId(context);
    }

    private static String optUniqueId(Context context) {
        String uniqueId = null;
        uniqueId = optId(context);
        if (TextUtils.isEmpty(uniqueId)) {
            uniqueId = optSimSerialNumber(context);
        }
        if (TextUtils.isEmpty(uniqueId) || uniqueId.equals("unknown")) {
            uniqueId = optMacAddress(context);
        }
        return uniqueId;
    }

    @SuppressLint("MissingPermission")
    public static String optId(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        try {
            final String SubscriberId = mTelephonyMgr.getSubscriberId();
            return !TextUtils.isEmpty(SubscriberId) ? SubscriberId : mTelephonyMgr.getDeviceId();
        } catch (Exception ex) {
            ex.printStackTrace();
            return "null";
        }
    }

    @SuppressLint("MissingPermission")
    public static String optSimSerialNumber(Context context) {
        String simSerialNumber = null;

        try {
            simSerialNumber = ((TelephonyManager) context.getApplicationContext().getSystemService(
                    Context.TELEPHONY_SERVICE)).getSimSerialNumber();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (TextUtils.isEmpty(simSerialNumber)) {
            try {
                Class<?> c = Class.forName("android.os.SystemProperties");
                Method get = c.getMethod("get", String.class);
                simSerialNumber = (String) get.invoke(c, "ro.serialno");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return !TextUtils.isEmpty(simSerialNumber) ? simSerialNumber : android.os.Build.SERIAL;
    }

    public static String optMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        try {
            WifiInfo info = wifi.getConnectionInfo();
            return info.getMacAddress();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
