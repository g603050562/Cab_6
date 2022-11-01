package client.halouhuandian.app15.service.logic.logicHttpConnection.webSocket;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import client.halouhuandian.app15.hardWareConncetion.daa.DaaController;
import client.halouhuandian.app15.hardWareConncetion.daa.DaaIntegration;
import client.halouhuandian.app15.hardWareConncetion.daa.DaaSend;
import client.halouhuandian.app15.hardWareConncetion.daa.mode.DaaDataFormat;
import client.halouhuandian.app15.hardWareConncetion.environmentPlate.EnvironmentController;
import client.halouhuandian.app15.hardWareConncetion.environmentPlate.EnvironmentDataFormat;
import client.halouhuandian.app15.hardWareConncetion.environmentPlate.EnvironmentReturnDataFormat;
import client.halouhuandian.app15.model.dao.fileSave.LocalLog;
import client.halouhuandian.app15.model.dao.sharedPreferences.CabInfoSp;
import client.halouhuandian.app15.model.dao.sharedPreferences.ForbiddenSp;
import client.halouhuandian.app15.pub.BaseDataDistribution;
import client.halouhuandian.app15.pub.util.UtilFilesDirectory;
import client.halouhuandian.app15.pub.RootCmd;
import client.halouhuandian.app15.pub.util.UtilMd5;
import client.halouhuandian.app15.pub.util.UtilUidDictionart;
import client.halouhuandian.app15.service.logic.logicHttpConnection.fileDownLoad.UpdateAndroidCore;
import client.halouhuandian.app15.service.logic.logicHttpConnection.fileDownLoad.UpdateAndroidDownloaderAndLauncher;
import client.halouhuandian.app15.service.logic.logicHttpConnection.fileDownLoad.UpdateHardWare;
import client.halouhuandian.app15.service.logic.logicHttpConnection.fileUpload.logs.HttpUploadLogs;
import client.halouhuandian.app15.service.logic.logicHttpConnection.fileUpload.logs.HttpUploadLogsPath;
import client.halouhuandian.app15.service.logic.logicHttpConnection.fileUpload.movies.HttpUploadMovies;
import client.halouhuandian.app15.service.logic.logicHttpConnection.fileUpload.movies.HttpUploadMoviesPath;
import client.halouhuandian.app15.service.logic.logicHttpConnection.http.BaseHttp;
import client.halouhuandian.app15.service.logic.logicHttpConnection.http.BaseHttpParameterFormat;
import client.halouhuandian.app15.service.logic.logicHttpConnection.http.HttpUrlMap;
import client.halouhuandian.app15.service.logic.logicHttpConnection.http.mode.WebSocketUpdateHardWareDataFormat;
import client.halouhuandian.app15.service.logic.logicHttpConnection.http.rentBattery.RentBatteryDataFormat;
import client.halouhuandian.app15.service.logic.logicHttpConnection.webSocket.mode.WebSocketReturnDataFormat;
import client.halouhuandian.app15.service.logic.logicHttpConnection.webSocket.mode.WebSocketReturnType;
import client.halouhuandian.app15.service.logic.logicHttpConnection.webSocket.mode.uploadCabInfo.UploadCabInfoDataFormat;
import client.halouhuandian.app15.service.logic.logicOpenDoor.LogicOpenDoor;
import client.halouhuandian.app15.service.logic.logicOpenDoor.LogicOpenDoorReturnDataFormat;
import client.halouhuandian.app15.service.logic.logicWriteUid.LogicWriteUid;

/**
 * 长链接 数据解析 处理以及回调
 * 如果不需要前台处理的话 直接在这个类 直接调用其他类处理了
 * 如果需要前台处理 比如显示或跳转操作
 * 需要再controller类注册 再通过接口分发 处理
 */

public class WebSocketReturnDataAnalysis {

    public interface WebSocketReturnDataAnalysisListener {
        void dataReturn(WebSocketReturnDataFormat webSocketReturnDataFormat);
    }

    private Context context;
    private WebSocketReturnDataAnalysisListener webSocketReturnDataAnalysisListener;

    private DaaController.DaaControllerListener daaControllerListener;
    private DaaDataFormat daaDataFormat;
    private EnvironmentDataFormat environmentDataFormat;
    private BaseDataDistribution.LogicListener environmentControllerListener;

    public WebSocketReturnDataAnalysis(Context context, WebSocketReturnDataAnalysisListener webSocketReturnDataAnalysisListener) {
        this.context = context;
        this.webSocketReturnDataAnalysisListener = webSocketReturnDataAnalysisListener;

        daaDataFormat = DaaController.getInstance().getDaaDataFormat();
        DaaController.getInstance().addListener(daaControllerListener = new DaaController.DaaControllerListener() {
            @Override
            public void returnData(DaaDataFormat mDaaDataFormat, DaaIntegration.ReturnDataType returnDataType, int index) {
                daaDataFormat = mDaaDataFormat;
            }
        });

        environmentDataFormat = EnvironmentController.getInstance().getEnvironmentDataFormat();
        EnvironmentController.getInstance().addListener(environmentControllerListener = new BaseDataDistribution.LogicListener() {
            @Override
            public void returnData(Object object) {
                EnvironmentReturnDataFormat environmentReturnDataFormat = (EnvironmentReturnDataFormat) object;
                if (environmentReturnDataFormat.environmentReturnDataFormatType == EnvironmentReturnDataFormat.EnvironmentReturnDataFormatType.environmentData) {
                    environmentDataFormat = (EnvironmentDataFormat) environmentReturnDataFormat.getReturnData();
                }
            }
        });
    }



    public void dataAnalysis(String orderType) {
        try {
            JSONTokener jsonTokener = new JSONTokener(orderType);
            JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
            String type = jsonObject.getString("type");
            //长链接下发  重启android板子
            if (type.equals("restartAndrBoard")) {
                //获取root权限
                new RootCmd().execRootCmd("reboot");
                writeLog("长链接下发重启android板");
            }
            //长链接下发  打开关闭后台
            else if (type.equals("cmdRemoteOpenAdmin")) {
                String action = jsonObject.getString("action");
                if (action.equals("1")) {
                    webSocketReturnDataAnalysisListener.dataReturn(new WebSocketReturnDataFormat(WebSocketReturnType.openAdmin, 1));
                    writeLog("长链接下发开启电柜后台");
                } else if (action.equals("0")) {
                    webSocketReturnDataAnalysisListener.dataReturn(new WebSocketReturnDataFormat(WebSocketReturnType.closeAdmin, 0));
                    writeLog("长链接下发关闭电柜后台");
                }
            }
            //长链接下发  打开提示框
            else if (type.equals("cmdAlertMsg")) {
                createDialogJsonAndShow(jsonObject.getString("msg"), jsonObject.getInt("time"), 1);
            }
            else if (type.equals("bindmxOK")){
                String cabid = jsonObject.getString("cabid");
                CabInfoSp.getInstance().setCabinetNumber_XXXXX(cabid);
                webSocketReturnDataAnalysisListener.dataReturn(new WebSocketReturnDataFormat(WebSocketReturnType.internetInfo, 0));
            }
            //长链接下发  显示仓门状态
            else if (type.equals("remoteSendCabStat")) {
                String cabid = jsonObject.getString("cabid");
                String name = jsonObject.getString("name");
                String number = jsonObject.getString("number");
                String isHeat = jsonObject.getString("isheat");//是否加热
                //设置电柜基本信息
                CabInfoSp.getInstance().setCabinetNumber_XXXXX(cabid);
                CabInfoSp.getInstance().setAddress(name);
                webSocketReturnDataAnalysisListener.dataReturn(new WebSocketReturnDataFormat(WebSocketReturnType.internetInfo, 0));
                //设置显示的400电话
                JSONArray phones = jsonObject.getJSONArray("phones");
                JSONObject phoneJSONObject = phones.getJSONObject(0);
                CabInfoSp.getInstance().setTelNumber(phoneJSONObject.getString("phone"));
                //设置舱门禁用状态 以及 dc禁用状态
                String data = jsonObject.getString("data");
                JSONTokener jsonTokener_1 = new JSONTokener(data);
                JSONArray jsonArray = (JSONArray) jsonTokener_1.nextValue();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject_1 = jsonArray.getJSONObject(i);
                    String door = jsonObject_1.getString("door");
                    //舱门禁用状态
                    String outIn = jsonObject_1.getString("outIn");
                    int door_int = Integer.parseInt(door);
                    int outIn_int = Integer.parseInt(outIn);
                    ForbiddenSp.getInstance().setTargetForbidden(door_int - 1, outIn_int);
                    //dc禁用状态
                    String stopdc = jsonObject_1.getString("stopdc");
                    String currentDcState = daaDataFormat.getDcdcInfoByStateFormat(i).getDcdcState();
                    if(currentDcState.equals("禁用中") && stopdc.equals("0")){
                        DaaSend.recoveryDcdc(i+1);
                    }else if(!currentDcState.equals("禁用中") && !stopdc.equals("0")){
                        DaaSend.forbiddenDcdc(i+1);
                    }
                }
            }
            //长链接下发  网络后台开门
            else if (type.equals("remoteOpenDoor")) {
                final int door = Integer.parseInt(jsonObject.getString("door"));
                createDialogJsonAndShow("远程打开" + door + "号仓门请注意安全", 10, 1);
                LogicOpenDoor.getInstance().putterPushAndReturnResult(door, CabInfoSp.getInstance().getPutterActivityTime(), "网络下发打开舱门", new LogicOpenDoor.LogicOpenDoorAsynchronousListener() {
                    @Override
                    public void returnData(LogicOpenDoorReturnDataFormat logicOpenDoorReturnDataFormat) {
                        if (logicOpenDoorReturnDataFormat.getResult() == false) {
                            LogicOpenDoor.getInstance().putterPull(door, CabInfoSp.getInstance().getPutterActivityTime(), "网络下发打开舱门失败 - 二次尝试");
                        }
                    }
                });
            }
            //长链接下发 网络关闭舱门isResult
            else if (type.equals("remoteCloseDoor")) {
                final int door = Integer.parseInt(jsonObject.getString("door"));
                createDialogJsonAndShow("远程关闭" + door + "号仓门请注意安全", 10, 1);
                LogicOpenDoor.getInstance().putterPullAndReturnResult(door, CabInfoSp.getInstance().getPutterActivityTime(), "网络下发关闭舱门", new LogicOpenDoor.LogicOpenDoorAsynchronousListener() {
                    @Override
                    public void returnData(LogicOpenDoorReturnDataFormat logicOpenDoorReturnDataFormat) {
                        if (logicOpenDoorReturnDataFormat.getResult() == false) {
                            LogicOpenDoor.getInstance().putterPush(door, CabInfoSp.getInstance().getPutterActivityTime(), "网络下发关闭舱门失败 - 二次尝试");
                        }
                    }
                });
            }
            //长链接下发  给服务器上传电柜里面的电池参数
            else if (type.equals("getBatteryInfo")) {
                String json = new UploadCabInfoDataFormat(context, daaDataFormat, environmentDataFormat).getJsonString();
                List<BaseHttpParameterFormat> baseHttpParameterFormats = new ArrayList<>();
                baseHttpParameterFormats.add(new BaseHttpParameterFormat("number", CabInfoSp.getInstance().getCabinetNumber_4600XXXX()));
                baseHttpParameterFormats.add(new BaseHttpParameterFormat("jsons", json));
                BaseHttp baseHttp = new BaseHttp(HttpUrlMap.UploadCabinetInfo, baseHttpParameterFormats);
                baseHttp.onStart();
            }

            //长链接下发 绑定成功 检测本地下载器和启动器版本
            else if (type.equals("bindSuccess")) {
                String cabid = jsonObject.getString("cabid");
                webSocketReturnDataAnalysisListener.dataReturn(new WebSocketReturnDataFormat(WebSocketReturnType.bindSuccess, cabid));
                List<BaseHttpParameterFormat> baseHttpParameterFormats = new ArrayList<>();
                baseHttpParameterFormats.add(new BaseHttpParameterFormat("token", UtilMd5.getMd5Token("1")));
                baseHttpParameterFormats.add(new BaseHttpParameterFormat("data", "1"));
                BaseHttp baseHttp = new BaseHttp(HttpUrlMap.GetDownloaderAndLauncher, baseHttpParameterFormats, new BaseHttp.BaseHttpListener() {
                    @Override
                    public void dataReturn(int code, String message, String data) {
                        if(data != null && !data.equals("")){
                            try {
                                JSONObject jsonObjectVersion = new JSONObject(data);
                                String downloaderVersion = jsonObjectVersion.getString("DownloaderVersion");
                                String launcherVersion = jsonObjectVersion.getString("LauncherVersion");
                                String downloaderUrl = jsonObjectVersion.getString("DownloaderUrl");
                                String launcherUrl = jsonObjectVersion.getString("LauncherUrl");
                                UpdateAndroidDownloaderAndLauncher updateAndroidDownloaderAndLauncher = new UpdateAndroidDownloaderAndLauncher(context, downloaderVersion, launcherVersion, downloaderUrl, launcherUrl, new UpdateAndroidDownloaderAndLauncher.UpdataAndroidDownloaderAndLauncherListener() {
                                    @Override
                                    public void showDialog(String message, int time) {
                                        createDialogJsonAndShow(message, time, 1);
                                    }
                                    @Override
                                    public void writeLog(String message) {
                                        writeLog(message);
                                    }
                                });
                                updateAndroidDownloaderAndLauncher.onStart();
                            } catch (Exception e) {
                                writeLog("longLink - error - " + e.toString());
                            }
                        }
                    }
                });
                baseHttp.onStart();

            }
            //长链接下发 租电池时上传电柜里面的电池信息
            else if (type.equals("rentBtyList")) {
                final String uid = jsonObject.getString("uid");
                final String order_num = jsonObject.getString("order_num");
                final String did = jsonObject.getString("did");
                List<BaseHttpParameterFormat> baseHttpParameterFormats = new ArrayList<>();
                baseHttpParameterFormats.add(new BaseHttpParameterFormat("uid", uid));
                baseHttpParameterFormats.add(new BaseHttpParameterFormat("btyjson", new RentBatteryDataFormat().getJson(context, did, uid, order_num, daaDataFormat).toString()));
                BaseHttp baseHttp = new BaseHttp(HttpUrlMap.RentBattery, baseHttpParameterFormats, new BaseHttp.BaseHttpListener() {
                    @Override
                    public void dataReturn(int code, String message, String data) {
                        try {
                            if (code == 0) {
                                createDialogJsonAndShow(message, 60, 1);
                            } else if(code == -1 ){
                                createDialogJsonAndShow("网络请求超时，请稍候重试！！", 60, 1);
                            } else {
                                JSONObject jsonObject = new JSONObject(data);
                                final  String uid32 = jsonObject.getString("uid32");
                                final int door = Integer.parseInt(jsonObject.getString("door"));
                                final int index = door - 1;
                                new LogicWriteUid(door, uid32, new LogicWriteUid.LogicWriteUidListener() {
                                    @Override
                                    public void returnStatus(boolean status) {
                                        if(status){
                                            List<BaseHttpParameterFormat> baseHttpParameterFormats = new ArrayList<>();
                                            baseHttpParameterFormats.add(new BaseHttpParameterFormat("did", did));
                                            baseHttpParameterFormats.add(new BaseHttpParameterFormat("number", CabInfoSp.getInstance().getCabinetNumber_4600XXXX()));
                                            baseHttpParameterFormats.add(new BaseHttpParameterFormat("door", door + ""));
                                            baseHttpParameterFormats.add(new BaseHttpParameterFormat("rstatus", "1"));
                                            baseHttpParameterFormats.add(new BaseHttpParameterFormat("battery", daaDataFormat.getDcdcInfoByBaseFormat(index).getBID()));
                                            baseHttpParameterFormats.add(new BaseHttpParameterFormat("electric", daaDataFormat.getDcdcInfoByBaseFormat(index).getBatteryRelativeSurplus() + ""));
                                            BaseHttp baseHttp = new BaseHttp(HttpUrlMap.RentBatteryFinish, baseHttpParameterFormats, new BaseHttp.BaseHttpListener() {
                                                @Override
                                                public void dataReturn(int code, String message, String data) {
                                                    String tel = UtilUidDictionart.getI10EndPhoneNumber(uid32);
                                                    createDialogJsonAndShow("租赁成功！！请手机尾号" + tel + "的用户拿走第" + door + "号舱门电池 , 舱门正在打开 ，请您注意安全，舱门将在<$time>秒后关闭，请尽快取出您的电池，如有问题请拨打电话客服咨询", 60, 1);
                                                    LogicOpenDoor.getInstance().putterPushAndPullContainBattery(door, CabInfoSp.getInstance().getPutterActivityTime(), 60, "您的电池电量高于当前电柜最大值，无需换电！", new LogicOpenDoor.LogicOpenDoorDialogListener() {
                                                        @Override
                                                        public void showDialog(String msg, int time, int type) throws JSONException {
                                                            createDialogJsonAndShow(msg,time,type);
                                                        }
                                                    });
                                                }
                                            });
                                            baseHttp.onStart();
                                        }else{
                                            List<BaseHttpParameterFormat> baseHttpParameterFormats = new ArrayList<>();
                                            baseHttpParameterFormats.add(new BaseHttpParameterFormat("did", did));
                                            baseHttpParameterFormats.add(new BaseHttpParameterFormat("number", CabInfoSp.getInstance().getCabinetNumber_4600XXXX()));
                                            baseHttpParameterFormats.add(new BaseHttpParameterFormat("door", door + ""));
                                            baseHttpParameterFormats.add(new BaseHttpParameterFormat("rstatus", "-1"));
                                            baseHttpParameterFormats.add(new BaseHttpParameterFormat("battery", daaDataFormat.getDcdcInfoByBaseFormat(index).getBID()));
                                            baseHttpParameterFormats.add(new BaseHttpParameterFormat("electric", daaDataFormat.getDcdcInfoByBaseFormat(index).getBatteryRelativeSurplus() + ""));
                                            BaseHttp baseHttp = new BaseHttp(HttpUrlMap.RentBatteryFinish, baseHttpParameterFormats, new BaseHttp.BaseHttpListener() {
                                                @Override
                                                public void dataReturn(int code, String message, String data){
                                                    createDialogJsonAndShow("电池租赁失败，如有问题请联系电话客服！！", 10, 1);
                                                }
                                            });
                                            baseHttp.onStart();
                                        }
                                    }
                                    @Override
                                    public void showDialog(String msg, int time, int type){
                                        createDialogJsonAndShow(msg, 10, 1);
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            writeLog("longLink - error - " + e.toString());
                            System.out.println("longLink - error - " + e.toString());
                        }
                    }
                });
                baseHttp.onStart();
            }
            //长链接下发 电池写入UID
            else if (type.equals("writeBtyUid")) {
                final String fUid32 = jsonObject.getString("uid32");
                final String fDoor = jsonObject.getString("door");
                final String fOutType = jsonObject.getString("outType");
                new LogicWriteUid(Integer.parseInt(fDoor), fUid32, new LogicWriteUid.LogicWriteUidListener() {
                    @Override
                    public void returnStatus(boolean status) {
                        if (status == true) {
                            if (fOutType.equals("2")) {
                                LogicOpenDoor.getInstance().putterPushAndPullContainBattery(Integer.parseInt(fDoor), CabInfoSp.getInstance().getPutterActivityTime(), 30, "网络后台写入电池", new LogicOpenDoor.LogicOpenDoorDialogListener() {
                                    @Override
                                    public void showDialog(String msg, int time, int type) {
                                        createDialogJsonAndShow(msg, time, type);
                                    }
                                });
                                String tel = UtilUidDictionart.getI10EndPhoneNumber(fUid32);
                                createDialogJsonAndShow("电池写入成功！！请手机尾号" + tel + "的用户拿走第" + fDoor + "号舱门电池，舱门将在30秒后关闭，请注意安全！", 10, 1);
                            }
                        } else {
                            createDialogJsonAndShow("电池写入失败！！如有问题请联系电话客服！", 10, 1);
                        }
                    }
                    @Override
                    public void showDialog(String msg, int time, int type) {
                        createDialogJsonAndShow(msg, time, type);
                    }
                });
            }
            //长链接下发 激活电池  -  目前还没有
            else if (type.equals("activateBattery")) {
                //???????
                //没找到功能
            }
            //长链接 下发禁用舱门操作
            else if (type.equals("disableDoorOut")) {
                final String door = jsonObject.getString("door");
                String outIn = jsonObject.getString("outIn");
                ForbiddenSp.getInstance().setTargetForbidden(Integer.parseInt(door) - 1, Integer.parseInt(outIn));
                if (outIn.equals("-2")) {
                    LogicOpenDoor.getInstance().putterPush(Integer.parseInt(door), CabInfoSp.getInstance().getPutterActivityTime(), "网络后台禁用舱门");
                    createDialogJsonAndShow("正在禁用"+door+"号舱门！", 10, 1);
                } else if (outIn.equals("-1")) {
                    LogicOpenDoor.getInstance().putterPull(Integer.parseInt(door), CabInfoSp.getInstance().getPutterActivityTime(), "网络后台禁用舱门");
                    createDialogJsonAndShow("正在禁用"+door+"号舱门！", 10, 1);
                } else if (outIn.equals("1")) {
                    createDialogJsonAndShow("正在启用"+door+"号舱门！", 10, 1);
                }
            }
            //长链接下发 更改保护进程设置
            else if (type.equals("setThreadsProtectionStatus")) {
                String threadType = jsonObject.getString("setStatus");
                if (threadType.equals("0")) {
                    CabInfoSp.getInstance().setTPTNumber("0");
                    createDialogJsonAndShow("正在关闭线程保护 请稍后！", 10, 1);
                } else {
                    CabInfoSp.getInstance().setTPTNumber("1");
                    createDialogJsonAndShow("正在打开线程保护 请稍后！", 10, 1);
                }
            }
            //长链接下发 会传电柜目录信息
            else if (type.equals("upVideoFileList")) {
                String cabid = jsonObject.getString("cabid");
                String admid = jsonObject.getString("admid");
                String upUrl = jsonObject.getString("upUrl");
                String logintk = jsonObject.getString("_logintk_");
                String remark = jsonObject.getString("remark");
                String date = jsonObject.getString("date");
                String hour = jsonObject.getString("hour");
                String level = jsonObject.getString("level");
                HttpUploadMoviesPath httpUploadMoviesPath = new HttpUploadMoviesPath(context, cabid, admid, upUrl, logintk, date, hour, level);
                httpUploadMoviesPath.start();
            }
            //长链接下发 会传电柜视频信息
            else if (type.equals("upVideoFile")) {
                String cabid = jsonObject.getString("cabid");
                String vname = jsonObject.getString("vname");
                String admid = jsonObject.getString("admid");
                String upUrl = jsonObject.getString("upUrl");
                String upField = jsonObject.getString("upField");
                String token = jsonObject.getString("_token");
                String hour = jsonObject.getString("hour");
                String day = jsonObject.getString("day");
                HttpUploadMovies httpUploadMovies = new HttpUploadMovies();
                httpUploadMovies.httpPost(upUrl, UtilFilesDirectory.EXTERNAL_MOVIES_DIR + File.separator + day + File.separator + hour + File.separator + vname, vname, cabid, admid, token, new HttpUploadMovies.HttpUploadMoviesListener() {
                    @Override
                    public void onHttpUploadMoviesReturn(long total, long now, long type, String title) {
                        /* TODO: 下载界面更新界面 */
                    }
                });
            }

            //长链接下发 上传日志路径
            else if (type.equals("upLogFileList")) {
                String cabid = jsonObject.getString("cabid");
                String admid = jsonObject.getString("admid");
                String date = jsonObject.getString("date");
                String upUrl = jsonObject.getString("upUrl");
                HttpUploadLogsPath httpUploadLogsPath = new HttpUploadLogsPath(cabid, admid, upUrl, date);
                httpUploadLogsPath.start();
            }
            //长链接下发 上传日志文件
            else if (type.equals("upLogFileToServ")) {
                String cabid = jsonObject.getString("cabid");
                String vname = jsonObject.getString("vname");
                String admid = jsonObject.getString("admid");
                String day = jsonObject.getString("day");
                String upField = jsonObject.getString("upField");
                String upUrl = jsonObject.getString("upUrl");
                String remark = jsonObject.getString("remark");
                HttpUploadLogs httpUploadLogs = new HttpUploadLogs();
                httpUploadLogs.httpPost(upUrl, day, vname, cabid, admid);
            }


            //长链接下发 根据电芯温度 设置电池加热 （设置电芯开启加热温度 - 设置电芯停止加热温度 - 电池壳停止加热温度） - 废弃？
            else if (type.equals("defsetDcdc")) {
                //???????????
            }
            //长链接下发 设置dcdc禁用状态
            else if (type.equals("setStopDcdc")) {
                String door = jsonObject.getString("door");
                String setdc = jsonObject.getString("setdc");
                int doorNumber = Integer.parseInt(door);
                int dcStatus = Integer.parseInt(setdc);
                if (dcStatus == 1) {
                    DaaSend.forbiddenDcdc(doorNumber);
                } else if (dcStatus == 0) {
                    DaaSend.recoveryDcdc(doorNumber);
                }
            }
            //长链接下发 设置过压复位命令
            else if (type.equals("resetDcdc")) {
                String door = jsonObject.getString("door");
                DaaSend.overVoltageReset(Integer.parseInt(door));
            }
            //长链接下发 下发打开消防喷水
            else if (type.equals("remoteSprayWater")) {
                //todo:消防没有加
                String door = jsonObject.getString("door");
                String opts = jsonObject.getString("opts");
                // 正常 - 1       -1 - 关闭电源并且洒水
                if (opts.equals("1")) {

                } else if (opts.equals("-1")) {

                }
            }
            //长链接下发 推杆持续时间
            else if (type.equals("pushrodActSetTime")) {
                String timeStr = jsonObject.optString("vals", "");
                float time = Float.parseFloat(timeStr);
                if (time >= 3 && time <= 15) {
                    time = (time * 10);
                    CabInfoSp.getInstance().setPutterActivityTime((int) time);
                    String name = jsonObject.optString("name", "");
                    createDialogJsonAndShow(name + time + "秒", 5, 1);
                }
            }
            //长链接下发 设置推杆电流板阈值
            else if (type.equals("setPushRodLitVal")) {
                float litVal = (float) jsonObject.getDouble("litVal");
                int isAuto = jsonObject.getInt("isAuto");
                if (litVal >= 1.5f && litVal <= 10) {
                    CabInfoSp.getInstance().setCurrentPlateMode(isAuto);
                    if (isAuto == 1) {
                        createDialogJsonAndShow("设置电流板自动模式", 5, 1);
                    } else if (isAuto == 0) {
                        createDialogJsonAndShow("设置电流板手动模式", 5, 1);
                        CabInfoSp.getInstance().setCurrentThreshold(litVal);
                        EnvironmentController.getInstance().setCurrentPlateParam(litVal, 800, 1000);
                    }
                }
            }
            //长链接下发 加热模式
            else if (type.equals("removeSetHeatMode")) {
                String isHeat = jsonObject.optInt("isheat", 2) + "";
                CabInfoSp.getInstance().setHeatMode(isHeat);
                if (isHeat.equals("1")) {
                    DaaSend.setHeatingDefault();
                } else if (isHeat.equals("2")) {
                    DaaSend.setHeatingAuto();
                } else if (isHeat.equals("-1")) {
                    DaaSend.setHeatingClose();
                }
            }
            //长链接下发升级主换电程序
            else if (type.equals("updateCabinetApp")) {
                String furl = jsonObject.getString("furl");
                createDialogJsonAndShow("准备更新主程序，请勿进行操作！", 10, 1);
                UpdateHardWare updateHardWare = new UpdateHardWare(context, "app11.apk", furl, new UpdateHardWare.DownloadUpdateFileListener() {
                    @Override
                    public void onDownloadUpdateFileReturn(UpdateHardWare content, String dataPath) {
                        content.installApk(dataPath, true);
                    }
                });
                updateHardWare.downloadAPK();
                writeLog("下载 - 正在下载安装换电程序");
            }
            //长链接下发电池升级
            else if (type.equals("updateOneBattery")) {
                final String url = jsonObject.getString("url");
                final String tarDoor = jsonObject.getString("door");
                final String fname = jsonObject.getString("fname");
                final String manu = jsonObject.getString("manu");
                createDialogJsonAndShow("准备升级" + tarDoor + "号电池，请勿进行操作！", 10, 1);
                new UpdateHardWare(context, fname, url, new UpdateHardWare.DownloadUpdateFileListener() {
                    @Override
                    public void onDownloadUpdateFileReturn(UpdateHardWare content, String dataPath) {
                        WebSocketUpdateHardWareDataFormat webSocketUpdateHardWareDataFormat = new WebSocketUpdateHardWareDataFormat(Integer.parseInt(tarDoor), dataPath, manu, CabInfoSp.getInstance().getTelNumber(), CabInfoSp.getInstance().getCabinetNumber_4600XXXX());
                        webSocketReturnDataAnalysisListener.dataReturn(new WebSocketReturnDataFormat(WebSocketReturnType.updateSingleBattery, webSocketUpdateHardWareDataFormat));
                    }
                });
            }
            //长链接下发升级单个dcdc
            else if (type.equals("upgradeDcdc")) {
                final String url = jsonObject.getString("url");
                final String tarDoor = jsonObject.getString("door");
                final String fName = jsonObject.getString("fname");
                final String manu = "single";
                createDialogJsonAndShow("准备升级" + tarDoor + "号DCDC，请勿进行操作！", 10, 1);
                new UpdateHardWare(context, fName, url, new UpdateHardWare.DownloadUpdateFileListener() {
                    @Override
                    public void onDownloadUpdateFileReturn(UpdateHardWare content, String dataPath) {
                        WebSocketUpdateHardWareDataFormat webSocketUpdateHardWareDataFormat = new WebSocketUpdateHardWareDataFormat(Integer.parseInt(tarDoor), dataPath, manu, CabInfoSp.getInstance().getTelNumber(), CabInfoSp.getInstance().getCabinetNumber_4600XXXX());
                        webSocketReturnDataAnalysisListener.dataReturn(new WebSocketReturnDataFormat(WebSocketReturnType.updateSingleDcdc, webSocketUpdateHardWareDataFormat));
                    }
                });
            }
            //长链接下发升级所有dcdc
            else if (type.equals("upgradeDcdcAll")) {
                final String url = jsonObject.getString("url");
                final String tarDoor = "0";
                final String fName = jsonObject.getString("fname");
                final String manu = "all";
                createDialogJsonAndShow("准备升级DCDC，请勿进行操作！", 10, 1);
                new UpdateHardWare(context, fName, url, new UpdateHardWare.DownloadUpdateFileListener() {
                    @Override
                    public void onDownloadUpdateFileReturn(UpdateHardWare content, String dataPath) {
                        WebSocketUpdateHardWareDataFormat webSocketUpdateHardWareDataFormat = new WebSocketUpdateHardWareDataFormat(Integer.parseInt(tarDoor), dataPath, manu, CabInfoSp.getInstance().getTelNumber(), CabInfoSp.getInstance().getCabinetNumber_4600XXXX());
                        webSocketReturnDataAnalysisListener.dataReturn(new WebSocketReturnDataFormat(WebSocketReturnType.updateAllDcdc, webSocketUpdateHardWareDataFormat));
                    }
                });
            }
            //长链接下发升级单个ACDC
            else if (type.equals("upgradeAcdc")) {
                final String url = jsonObject.getString("url");
                final String tarDoor = jsonObject.getString("acdcno");
                final String fName = jsonObject.getString("fname");
                final String manu = "single";
                createDialogJsonAndShow("准备升级" + tarDoor + "号ACDC，请勿进行操作！", 10, 1);
                new UpdateHardWare(context, fName, url, new UpdateHardWare.DownloadUpdateFileListener() {
                    @Override
                    public void onDownloadUpdateFileReturn(UpdateHardWare content, String dataPath) {
                        WebSocketUpdateHardWareDataFormat webSocketUpdateHardWareDataFormat = new WebSocketUpdateHardWareDataFormat(Integer.parseInt(tarDoor), dataPath, manu, CabInfoSp.getInstance().getTelNumber(), CabInfoSp.getInstance().getCabinetNumber_4600XXXX());
                        webSocketReturnDataAnalysisListener.dataReturn(new WebSocketReturnDataFormat(WebSocketReturnType.updateSingleAcdc, webSocketUpdateHardWareDataFormat));
                    }
                });
            }
            //长链接下发升级所有ACDC
            else if (type.equals("upgradeAcdcAll")) {
                final String url = jsonObject.getString("url");
                final String tarDoor = "0";
                final String fName = jsonObject.getString("fname");
                final String manu = "all";
                createDialogJsonAndShow("准备升级ACDC，请勿进行操作！", 10, 1);
                new UpdateHardWare(context, fName, url, new UpdateHardWare.DownloadUpdateFileListener() {
                    @Override
                    public void onDownloadUpdateFileReturn(UpdateHardWare content, String dataPath) {
                        WebSocketUpdateHardWareDataFormat webSocketUpdateHardWareDataFormat = new WebSocketUpdateHardWareDataFormat(Integer.parseInt(tarDoor), dataPath, manu, CabInfoSp.getInstance().getTelNumber(), CabInfoSp.getInstance().getCabinetNumber_4600XXXX());
                        webSocketReturnDataAnalysisListener.dataReturn(new WebSocketReturnDataFormat(WebSocketReturnType.updateAllAcdc, webSocketUpdateHardWareDataFormat));
                    }
                });
            }
            //长链接下发升级环境板
            else if (type.equals("envBoardUpgrade")) {
                final String url = jsonObject.getString("url");
                final String fName = jsonObject.getString("name");
                final String version = jsonObject.getString("ver");
                createDialogJsonAndShow("准备升级环境板，请勿进行操作！", 10, 1);
                new UpdateHardWare(context, fName, url, new UpdateHardWare.DownloadUpdateFileListener() {
                    @Override
                    public void onDownloadUpdateFileReturn(UpdateHardWare content, String dataPath) {
                        WebSocketUpdateHardWareDataFormat webSocketUpdateHardWareDataFormat = new WebSocketUpdateHardWareDataFormat(0, dataPath, "0", CabInfoSp.getInstance().getTelNumber(), CabInfoSp.getInstance().getCabinetNumber_4600XXXX());
                        webSocketReturnDataAnalysisListener.dataReturn(new WebSocketReturnDataFormat(WebSocketReturnType.updateEnvironment, webSocketUpdateHardWareDataFormat));
                    }
                });

            }
            //长链接下发  更新android内核
            else if (type.equals("upgradeCabinetCore")) {
                String apkUrl = jsonObject.getString("apkurl");
                String zipUrl = jsonObject.getString("zipurl");
                UpdateAndroidCore updateAndroidCore = new UpdateAndroidCore(context, apkUrl, zipUrl);
                updateAndroidCore.onStart();
            }
            //长链接 切换服务器
            else if (type.equals("setGlobalDomain")) {
                String globalDomain = jsonObject.getString("mjson");
                JSONObject jsonObject1 = new JSONObject(globalDomain);
                CabInfoSp.getInstance().setServer(jsonObject1.toString());
                new RootCmd().execRootCmd("reboot");
            }

        } catch (Exception e) {
            writeLog("longLink - error - " + e.toString());
            System.out.println("longLink - error - " + e.toString());
        }
    }

    public void onDestroy() {
        DaaController.getInstance().deleteListener(daaControllerListener);
        EnvironmentController.getInstance().deleteListener(environmentControllerListener);
    }

    /**
     * 传入json给 main界面 显示dialog
     *
     * @param msg
     * @param time
     * @param type
     * @return
     */
    private void createDialogJsonAndShow(String msg, int time, int type){
        JSONObject dialogJson = new JSONObject();
        try {
            dialogJson.put("msg", msg);
            dialogJson.put("time", time);
            dialogJson.put("type", type);
        }catch (Exception e){
            writeLog("longLink - error - " + e.toString());
            System.out.println("longLink - error - " + e.toString());
        }
        webSocketReturnDataAnalysisListener.dataReturn(new WebSocketReturnDataFormat(WebSocketReturnType.showDialog, dialogJson.toString()));
    }

    /**
     * 写入本地日志
     *
     * @param log
     */
    public void writeLog(String log) {
        LocalLog.getInstance().writeLog(log , this);
    }

}
