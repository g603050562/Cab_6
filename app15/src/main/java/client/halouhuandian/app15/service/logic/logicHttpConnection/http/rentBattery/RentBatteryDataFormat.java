package client.halouhuandian.app15.service.logic.logicHttpConnection.http.rentBattery;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import client.halouhuandian.app15.config.SystemConfig;
import client.halouhuandian.app15.hardWareConncetion.daa.mode.DaaDataFormat;
import client.halouhuandian.app15.hardWareConncetion.daa.mode.dcdc.DcdcInfoByBaseFormat;
import client.halouhuandian.app15.model.dao.sharedPreferences.CabInfoSp;
import client.halouhuandian.app15.pub.util.UtilBattery;

public class RentBatteryDataFormat {

    public JSONObject getJson(Context context , String did , String uid , String order_num , DaaDataFormat daaDataFormat) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("did",did);
        jsonObject.put("uid",uid);
        jsonObject.put("cid",CabInfoSp.getInstance().getCabinetNumber_4600XXXX());
        jsonObject.put("order_num",order_num);
        jsonObject.put("cabid", CabInfoSp.getInstance().getCabinetNumber_XXXXX());
        jsonObject.put("data" , getItemJson(daaDataFormat));
        return jsonObject;
    }

    public JSONArray getItemJson(DaaDataFormat daaDataFormat) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for(int i = 0; i < SystemConfig.getMaxBattery(); i++){
            JSONObject jsonObject = new JSONObject();
            DcdcInfoByBaseFormat dcdcInfoByBaseFormat = daaDataFormat.getDcdcInfoByBaseFormat(i);
            jsonObject.put("door",i+1);
            jsonObject.put("bid",dcdcInfoByBaseFormat.getBID());
            jsonObject.put("per",dcdcInfoByBaseFormat.getBatteryRelativeSurplus());
            jsonObject.put("uid32",dcdcInfoByBaseFormat.getUID());
            jsonObject.put("volt", UtilBattery.isType(dcdcInfoByBaseFormat.getBID()));
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

}
