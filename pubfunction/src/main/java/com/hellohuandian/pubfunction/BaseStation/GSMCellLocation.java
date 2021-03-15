package com.hellohuandian.pubfunction.BaseStation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class GSMCellLocation {

    private static final String TAG = "GSMCellLocationActivity";

    private Activity activity;

    public GSMCellLocation(Activity activity) {
        this.activity = activity;
    }

    public Map<String , String> getGSMCell() {

        try {

            Map<String , String> map = new HashMap<>();

            TelephonyManager mTelephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);

            // 中国移动和中国联通获取LAC、CID的方式
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                Log.i(TAG, "获取邻区基站信息: 没有权限");

                return null;
            }


            // 返回值MCC + MNC
            String operator = mTelephonyManager.getNetworkOperator();
            int mcc = Integer.parseInt(operator.substring(0, 3));
            int mnc = Integer.parseInt(operator.substring(3));

            map.put("mcc",mcc+"");
            map.put("mnc",mnc+"");

            GsmCellLocation location = (GsmCellLocation) mTelephonyManager.getCellLocation();
            int lac = location.getLac();
            int cellId = location.getCid();

            map.put("lac",lac+"");
            map.put("cellId",cellId+"");

            Log.i(TAG, " MCC = " + mcc + "\t MNC = " + mnc + "\t LAC = " + lac + "\t CID = " + cellId);

            return map;


        } catch (Exception e) {
            return null;
        }
    }
}
