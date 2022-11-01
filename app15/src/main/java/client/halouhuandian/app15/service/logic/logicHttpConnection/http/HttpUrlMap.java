package client.halouhuandian.app15.service.logic.logicHttpConnection.http;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpUrlMap {

//    public static String apc = "http://apc.halouhuandian.com/";
//    private static String api = "http://api.halouhuandian.com/";
//    private static String app = "http://app.halouhuandian.com/";
//    private static String log = "http://logs.halouhuandian.com:52100/";
//    public static String longlink = "http://long.halouhuandian.com:5858";
//    public static String exchange = "http://apc.halouhuandian.com:1081/Exchange/exchangev5.html";
//    public static String BindLongLink = "http://apc.halouhuandian.com:1081/Connection/bind.html";

    public static String apc = "http://apc.mixiangx.com/";
    private static String api = "http://api.mixiangx.com/";
    private static String app = "http://app.mixiangx.com/";
    private static String log = "http://logs.mixiangx.com:52100/";
    public static String longlink = "http://long.mixiangx.com:2216";
    public static String exchange = "http://apc.mixiangx.com:1081/Exchange/exchangev5.html";
    public static String BindLongLink = "http://apc.mixiangx.com:1081/Connection/bind.html";


    //电柜日志
    public static String UploadCabinetInfo = log + "Log/logs.html";
    //写入UID上传服务器接口
    public static String UploadWriteUID = apc + "Cabinet/hisWriteUid32.html";
    //租电池接口
    public static String RentBattery =  api + "Rent/receiveBindv3.html";
    //租电池二次上传服务器接口
    public static String RentBatteryFinish = api + "Rent/confirmBindv3.html";
    //检测本地舱门电池是否存在绑定电池
    public static String BatteriesIsBinding = apc + "Check/startCheck.html";
    //获取服务器和下载器版本
    public static String GetDownloaderAndLauncher = "http://47.110.240.148/Log/getVersion";
    //上传服务器本地弹出电池
    public static String UploadOpenDoorLog = apc + "CabLog/addCabEjectLog";
    //上传服务器本地日志
    public static String UploadExchangeLog = apc + "Exchange/exchangev8.html";
    //电池换电UID转换接口
    public static String GetUID = apc + "Check/checkOldBind.html";
    //电池换电获取用户信息
    public static String GetUserInfo = apc + "Check/checkUserBalance.html";
    //电池写入UID失败上传日志
    public static String UploadWriteUidFail = apc + "Errors/cabinet.html";
    //获取控制板版本
    public static String GetControlPlateVersionBin =  apc + "ApsHard/soft.html";
    //http心跳
    public static String HttpBeat = apc + "Connection/heartbeat.html";
    //QrCode 二维码转挑
    public static String DownLoadApkJump = app + "App/jump";
    //上传服务器错误信息
    public static String UploadErrorInfo = apc + "Errors/cabinet.html";

    public static void setServer(String type){

        if(type.equals("")){
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(type);
            apc = jsonObject.getString("apc");
            api = jsonObject.getString("api");
            app = jsonObject.getString("app");
            log = jsonObject.getString("log");
            longlink = jsonObject.getString("longlink");
            BindLongLink = jsonObject.getString("BindLongLink") + "Connection/bind.html";
            init();
            System.out.println("longLink - 当前服务器 - " + type.toString());
        } catch (Exception e) {
            System.out.println("longLink - 当前服务器 - error " + e.toString());
        }
    }

    private static void init(){
        UploadCabinetInfo = log + "Log/logs.html";
        UploadWriteUID = apc + "Cabinet/hisWriteUid32.html";
        RentBattery =  api + "Rent/receiveBindv3.html";
        RentBatteryFinish = api + "Rent/confirmBindv3.html";
        BatteriesIsBinding = apc + "Check/startCheck.html";
        GetDownloaderAndLauncher = "http://47.110.240.148/Log/getVersion";
        UploadOpenDoorLog = apc + "CabLog/addCabEjectLog";
        UploadExchangeLog = apc + "Exchange/exchangev8.html";
        GetUID = apc + "Check/checkOldBind.html";
        GetUserInfo = apc + "Check/checkUserBalance.html";
        UploadWriteUidFail = apc + "Errors/cabinet.html";
        GetControlPlateVersionBin =  apc + "ApsHard/soft.html";
        HttpBeat = apc + "Connection/heartbeat.html";
        DownLoadApkJump = app + "App/jump";
        UploadErrorInfo = apc + "Errors/cabinet.html";
    }

}
