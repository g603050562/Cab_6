package com.hellohuandian.pubfunction.Card;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

public class CardUnit {

    private Activity activity;

    public CardUnit(Activity activity) {
        this.activity = activity;
    }

    public String getcabID() {

        String cabId = "";
        SharedPreferences sharedPreferences = activity.getSharedPreferences("CabInfo", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        cabId = sharedPreferences.getString("cabinetNumber", "");
        String imsi = "";
        TelephonyManager mTelephonyMgr = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        imsi = mTelephonyMgr.getSubscriberId();

        if (imsi.equals("") || imsi.equals("null")) {
            if (cabId.equals("")) {
                editor.putString("cabinetNumber", "987654321"); //出场的默认id
                editor.commit();
                cabId = "987654321";
            }
        } else {
            editor.putString("cabinetNumber", imsi); //出场的默认id
            editor.commit();
            cabId = imsi;
        }

        return cabId;
    }


    public void doSendSMSTo(String phoneNumber, String message) {
        if (PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)) {
            SmsManager.getDefault().sendTextMessage(phoneNumber, null, message, null, null);
        }
    }


}
