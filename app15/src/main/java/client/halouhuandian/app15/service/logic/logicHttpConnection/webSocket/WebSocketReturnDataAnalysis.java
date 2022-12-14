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
 * ????????? ???????????? ??????????????????
 * ????????????????????????????????? ?????????????????? ??????????????????????????????
 * ???????????????????????? ???????????????????????????
 * ?????????controller????????? ????????????????????? ??????
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
            //???????????????  ??????android??????
            if (type.equals("restartAndrBoard")) {
                //??????root??????
                new RootCmd().execRootCmd("reboot");
                writeLog("?????????????????????android???");
            }
            //???????????????  ??????????????????
            else if (type.equals("cmdRemoteOpenAdmin")) {
                String action = jsonObject.getString("action");
                if (action.equals("1")) {
                    webSocketReturnDataAnalysisListener.dataReturn(new WebSocketReturnDataFormat(WebSocketReturnType.openAdmin, 1));
                    writeLog("?????????????????????????????????");
                } else if (action.equals("0")) {
                    webSocketReturnDataAnalysisListener.dataReturn(new WebSocketReturnDataFormat(WebSocketReturnType.closeAdmin, 0));
                    writeLog("?????????????????????????????????");
                }
            }
            //???????????????  ???????????????
            else if (type.equals("cmdAlertMsg")) {
                createDialogJsonAndShow(jsonObject.getString("msg"), jsonObject.getInt("time"), 1);
            }
            else if (type.equals("bindmxOK")){
                String cabid = jsonObject.getString("cabid");
                CabInfoSp.getInstance().setCabinetNumber_XXXXX(cabid);
                webSocketReturnDataAnalysisListener.dataReturn(new WebSocketReturnDataFormat(WebSocketReturnType.internetInfo, 0));
            }
            //???????????????  ??????????????????
            else if (type.equals("remoteSendCabStat")) {
                String cabid = jsonObject.getString("cabid");
                String name = jsonObject.getString("name");
                String number = jsonObject.getString("number");
                String isHeat = jsonObject.getString("isheat");//????????????
                //????????????????????????
                CabInfoSp.getInstance().setCabinetNumber_XXXXX(cabid);
                CabInfoSp.getInstance().setAddress(name);
                webSocketReturnDataAnalysisListener.dataReturn(new WebSocketReturnDataFormat(WebSocketReturnType.internetInfo, 0));
                //???????????????400??????
                JSONArray phones = jsonObject.getJSONArray("phones");
                JSONObject phoneJSONObject = phones.getJSONObject(0);
                CabInfoSp.getInstance().setTelNumber(phoneJSONObject.getString("phone"));
                //???????????????????????? ?????? dc????????????
                String data = jsonObject.getString("data");
                JSONTokener jsonTokener_1 = new JSONTokener(data);
                JSONArray jsonArray = (JSONArray) jsonTokener_1.nextValue();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject_1 = jsonArray.getJSONObject(i);
                    String door = jsonObject_1.getString("door");
                    //??????????????????
                    String outIn = jsonObject_1.getString("outIn");
                    int door_int = Integer.parseInt(door);
                    int outIn_int = Integer.parseInt(outIn);
                    ForbiddenSp.getInstance().setTargetForbidden(door_int - 1, outIn_int);
                    //dc????????????
                    String stopdc = jsonObject_1.getString("stopdc");
                    String currentDcState = daaDataFormat.getDcdcInfoByStateFormat(i).getDcdcState();
                    if(currentDcState.equals("?????????") && stopdc.equals("0")){
                        DaaSend.recoveryDcdc(i+1);
                    }else if(!currentDcState.equals("?????????") && !stopdc.equals("0")){
                        DaaSend.forbiddenDcdc(i+1);
                    }
                }
            }
            //???????????????  ??????????????????
            else if (type.equals("remoteOpenDoor")) {
                final int door = Integer.parseInt(jsonObject.getString("door"));
                createDialogJsonAndShow("????????????" + door + "????????????????????????", 10, 1);
                LogicOpenDoor.getInstance().putterPushAndReturnResult(door, CabInfoSp.getInstance().getPutterActivityTime(), "????????????????????????", new LogicOpenDoor.LogicOpenDoorAsynchronousListener() {
                    @Override
                    public void returnData(LogicOpenDoorReturnDataFormat logicOpenDoorReturnDataFormat) {
                        if (logicOpenDoorReturnDataFormat.getResult() == false) {
                            LogicOpenDoor.getInstance().putterPull(door, CabInfoSp.getInstance().getPutterActivityTime(), "?????????????????????????????? - ????????????");
                        }
                    }
                });
            }
            //??????????????? ??????????????????isResult
            else if (type.equals("remoteCloseDoor")) {
                final int door = Integer.parseInt(jsonObject.getString("door"));
                createDialogJsonAndShow("????????????" + door + "????????????????????????", 10, 1);
                LogicOpenDoor.getInstance().putterPullAndReturnResult(door, CabInfoSp.getInstance().getPutterActivityTime(), "????????????????????????", new LogicOpenDoor.LogicOpenDoorAsynchronousListener() {
                    @Override
                    public void returnData(LogicOpenDoorReturnDataFormat logicOpenDoorReturnDataFormat) {
                        if (logicOpenDoorReturnDataFormat.getResult() == false) {
                            LogicOpenDoor.getInstance().putterPush(door, CabInfoSp.getInstance().getPutterActivityTime(), "?????????????????????????????? - ????????????");
                        }
                    }
                });
            }
            //???????????????  ?????????????????????????????????????????????
            else if (type.equals("getBatteryInfo")) {
                String json = new UploadCabInfoDataFormat(context, daaDataFormat, environmentDataFormat).getJsonString();
                List<BaseHttpParameterFormat> baseHttpParameterFormats = new ArrayList<>();
                baseHttpParameterFormats.add(new BaseHttpParameterFormat("number", CabInfoSp.getInstance().getCabinetNumber_4600XXXX()));
                baseHttpParameterFormats.add(new BaseHttpParameterFormat("jsons", json));
                BaseHttp baseHttp = new BaseHttp(HttpUrlMap.UploadCabinetInfo, baseHttpParameterFormats);
                baseHttp.onStart();
            }

            //??????????????? ???????????? ???????????????????????????????????????
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
            //??????????????? ?????????????????????????????????????????????
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
                                createDialogJsonAndShow("??????????????????????????????????????????", 60, 1);
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
                                                    createDialogJsonAndShow("?????????????????????????????????" + tel + "??????????????????" + door + "??????????????? , ?????????????????? ????????????????????????????????????<$time>????????????????????????????????????????????????????????????????????????????????????", 60, 1);
                                                    LogicOpenDoor.getInstance().putterPushAndPullContainBattery(door, CabInfoSp.getInstance().getPutterActivityTime(), 60, "???????????????????????????????????????????????????????????????", new LogicOpenDoor.LogicOpenDoorDialogListener() {
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
                                                    createDialogJsonAndShow("????????????????????????????????????????????????????????????", 10, 1);
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
            //??????????????? ????????????UID
            else if (type.equals("writeBtyUid")) {
                final String fUid32 = jsonObject.getString("uid32");
                final String fDoor = jsonObject.getString("door");
                final String fOutType = jsonObject.getString("outType");
                new LogicWriteUid(Integer.parseInt(fDoor), fUid32, new LogicWriteUid.LogicWriteUidListener() {
                    @Override
                    public void returnStatus(boolean status) {
                        if (status == true) {
                            if (fOutType.equals("2")) {
                                LogicOpenDoor.getInstance().putterPushAndPullContainBattery(Integer.parseInt(fDoor), CabInfoSp.getInstance().getPutterActivityTime(), 30, "????????????????????????", new LogicOpenDoor.LogicOpenDoorDialogListener() {
                                    @Override
                                    public void showDialog(String msg, int time, int type) {
                                        createDialogJsonAndShow(msg, time, type);
                                    }
                                });
                                String tel = UtilUidDictionart.getI10EndPhoneNumber(fUid32);
                                createDialogJsonAndShow("???????????????????????????????????????" + tel + "??????????????????" + fDoor + "??????????????????????????????30?????????????????????????????????", 10, 1);
                            }
                        } else {
                            createDialogJsonAndShow("????????????????????????????????????????????????????????????", 10, 1);
                        }
                    }
                    @Override
                    public void showDialog(String msg, int time, int type) {
                        createDialogJsonAndShow(msg, time, type);
                    }
                });
            }
            //??????????????? ????????????  -  ???????????????
            else if (type.equals("activateBattery")) {
                //???????
                //???????????????
            }
            //????????? ????????????????????????
            else if (type.equals("disableDoorOut")) {
                final String door = jsonObject.getString("door");
                String outIn = jsonObject.getString("outIn");
                ForbiddenSp.getInstance().setTargetForbidden(Integer.parseInt(door) - 1, Integer.parseInt(outIn));
                if (outIn.equals("-2")) {
                    LogicOpenDoor.getInstance().putterPush(Integer.parseInt(door), CabInfoSp.getInstance().getPutterActivityTime(), "????????????????????????");
                    createDialogJsonAndShow("????????????"+door+"????????????", 10, 1);
                } else if (outIn.equals("-1")) {
                    LogicOpenDoor.getInstance().putterPull(Integer.parseInt(door), CabInfoSp.getInstance().getPutterActivityTime(), "????????????????????????");
                    createDialogJsonAndShow("????????????"+door+"????????????", 10, 1);
                } else if (outIn.equals("1")) {
                    createDialogJsonAndShow("????????????"+door+"????????????", 10, 1);
                }
            }
            //??????????????? ????????????????????????
            else if (type.equals("setThreadsProtectionStatus")) {
                String threadType = jsonObject.getString("setStatus");
                if (threadType.equals("0")) {
                    CabInfoSp.getInstance().setTPTNumber("0");
                    createDialogJsonAndShow("???????????????????????? ????????????", 10, 1);
                } else {
                    CabInfoSp.getInstance().setTPTNumber("1");
                    createDialogJsonAndShow("???????????????????????? ????????????", 10, 1);
                }
            }
            //??????????????? ????????????????????????
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
            //??????????????? ????????????????????????
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
                        /* TODO: ???????????????????????? */
                    }
                });
            }

            //??????????????? ??????????????????
            else if (type.equals("upLogFileList")) {
                String cabid = jsonObject.getString("cabid");
                String admid = jsonObject.getString("admid");
                String date = jsonObject.getString("date");
                String upUrl = jsonObject.getString("upUrl");
                HttpUploadLogsPath httpUploadLogsPath = new HttpUploadLogsPath(cabid, admid, upUrl, date);
                httpUploadLogsPath.start();
            }
            //??????????????? ??????????????????
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


            //??????????????? ?????????????????? ?????????????????? ????????????????????????????????? - ?????????????????????????????? - ?????????????????????????????? - ?????????
            else if (type.equals("defsetDcdc")) {
                //???????????
            }
            //??????????????? ??????dcdc????????????
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
            //??????????????? ????????????????????????
            else if (type.equals("resetDcdc")) {
                String door = jsonObject.getString("door");
                DaaSend.overVoltageReset(Integer.parseInt(door));
            }
            //??????????????? ????????????????????????
            else if (type.equals("remoteSprayWater")) {
                //todo:???????????????
                String door = jsonObject.getString("door");
                String opts = jsonObject.getString("opts");
                // ?????? - 1       -1 - ????????????????????????
                if (opts.equals("1")) {

                } else if (opts.equals("-1")) {

                }
            }
            //??????????????? ??????????????????
            else if (type.equals("pushrodActSetTime")) {
                String timeStr = jsonObject.optString("vals", "");
                float time = Float.parseFloat(timeStr);
                if (time >= 3 && time <= 15) {
                    time = (time * 10);
                    CabInfoSp.getInstance().setPutterActivityTime((int) time);
                    String name = jsonObject.optString("name", "");
                    createDialogJsonAndShow(name + time + "???", 5, 1);
                }
            }
            //??????????????? ???????????????????????????
            else if (type.equals("setPushRodLitVal")) {
                float litVal = (float) jsonObject.getDouble("litVal");
                int isAuto = jsonObject.getInt("isAuto");
                if (litVal >= 1.5f && litVal <= 10) {
                    CabInfoSp.getInstance().setCurrentPlateMode(isAuto);
                    if (isAuto == 1) {
                        createDialogJsonAndShow("???????????????????????????", 5, 1);
                    } else if (isAuto == 0) {
                        createDialogJsonAndShow("???????????????????????????", 5, 1);
                        CabInfoSp.getInstance().setCurrentThreshold(litVal);
                        EnvironmentController.getInstance().setCurrentPlateParam(litVal, 800, 1000);
                    }
                }
            }
            //??????????????? ????????????
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
            //????????????????????????????????????
            else if (type.equals("updateCabinetApp")) {
                String furl = jsonObject.getString("furl");
                createDialogJsonAndShow("?????????????????????????????????????????????", 10, 1);
                UpdateHardWare updateHardWare = new UpdateHardWare(context, "app11.apk", furl, new UpdateHardWare.DownloadUpdateFileListener() {
                    @Override
                    public void onDownloadUpdateFileReturn(UpdateHardWare content, String dataPath) {
                        content.installApk(dataPath, true);
                    }
                });
                updateHardWare.downloadAPK();
                writeLog("?????? - ??????????????????????????????");
            }
            //???????????????????????????
            else if (type.equals("updateOneBattery")) {
                final String url = jsonObject.getString("url");
                final String tarDoor = jsonObject.getString("door");
                final String fname = jsonObject.getString("fname");
                final String manu = jsonObject.getString("manu");
                createDialogJsonAndShow("????????????" + tarDoor + "?????????????????????????????????", 10, 1);
                new UpdateHardWare(context, fname, url, new UpdateHardWare.DownloadUpdateFileListener() {
                    @Override
                    public void onDownloadUpdateFileReturn(UpdateHardWare content, String dataPath) {
                        WebSocketUpdateHardWareDataFormat webSocketUpdateHardWareDataFormat = new WebSocketUpdateHardWareDataFormat(Integer.parseInt(tarDoor), dataPath, manu, CabInfoSp.getInstance().getTelNumber(), CabInfoSp.getInstance().getCabinetNumber_4600XXXX());
                        webSocketReturnDataAnalysisListener.dataReturn(new WebSocketReturnDataFormat(WebSocketReturnType.updateSingleBattery, webSocketUpdateHardWareDataFormat));
                    }
                });
            }
            //???????????????????????????dcdc
            else if (type.equals("upgradeDcdc")) {
                final String url = jsonObject.getString("url");
                final String tarDoor = jsonObject.getString("door");
                final String fName = jsonObject.getString("fname");
                final String manu = "single";
                createDialogJsonAndShow("????????????" + tarDoor + "???DCDC????????????????????????", 10, 1);
                new UpdateHardWare(context, fName, url, new UpdateHardWare.DownloadUpdateFileListener() {
                    @Override
                    public void onDownloadUpdateFileReturn(UpdateHardWare content, String dataPath) {
                        WebSocketUpdateHardWareDataFormat webSocketUpdateHardWareDataFormat = new WebSocketUpdateHardWareDataFormat(Integer.parseInt(tarDoor), dataPath, manu, CabInfoSp.getInstance().getTelNumber(), CabInfoSp.getInstance().getCabinetNumber_4600XXXX());
                        webSocketReturnDataAnalysisListener.dataReturn(new WebSocketReturnDataFormat(WebSocketReturnType.updateSingleDcdc, webSocketUpdateHardWareDataFormat));
                    }
                });
            }
            //???????????????????????????dcdc
            else if (type.equals("upgradeDcdcAll")) {
                final String url = jsonObject.getString("url");
                final String tarDoor = "0";
                final String fName = jsonObject.getString("fname");
                final String manu = "all";
                createDialogJsonAndShow("????????????DCDC????????????????????????", 10, 1);
                new UpdateHardWare(context, fName, url, new UpdateHardWare.DownloadUpdateFileListener() {
                    @Override
                    public void onDownloadUpdateFileReturn(UpdateHardWare content, String dataPath) {
                        WebSocketUpdateHardWareDataFormat webSocketUpdateHardWareDataFormat = new WebSocketUpdateHardWareDataFormat(Integer.parseInt(tarDoor), dataPath, manu, CabInfoSp.getInstance().getTelNumber(), CabInfoSp.getInstance().getCabinetNumber_4600XXXX());
                        webSocketReturnDataAnalysisListener.dataReturn(new WebSocketReturnDataFormat(WebSocketReturnType.updateAllDcdc, webSocketUpdateHardWareDataFormat));
                    }
                });
            }
            //???????????????????????????ACDC
            else if (type.equals("upgradeAcdc")) {
                final String url = jsonObject.getString("url");
                final String tarDoor = jsonObject.getString("acdcno");
                final String fName = jsonObject.getString("fname");
                final String manu = "single";
                createDialogJsonAndShow("????????????" + tarDoor + "???ACDC????????????????????????", 10, 1);
                new UpdateHardWare(context, fName, url, new UpdateHardWare.DownloadUpdateFileListener() {
                    @Override
                    public void onDownloadUpdateFileReturn(UpdateHardWare content, String dataPath) {
                        WebSocketUpdateHardWareDataFormat webSocketUpdateHardWareDataFormat = new WebSocketUpdateHardWareDataFormat(Integer.parseInt(tarDoor), dataPath, manu, CabInfoSp.getInstance().getTelNumber(), CabInfoSp.getInstance().getCabinetNumber_4600XXXX());
                        webSocketReturnDataAnalysisListener.dataReturn(new WebSocketReturnDataFormat(WebSocketReturnType.updateSingleAcdc, webSocketUpdateHardWareDataFormat));
                    }
                });
            }
            //???????????????????????????ACDC
            else if (type.equals("upgradeAcdcAll")) {
                final String url = jsonObject.getString("url");
                final String tarDoor = "0";
                final String fName = jsonObject.getString("fname");
                final String manu = "all";
                createDialogJsonAndShow("????????????ACDC????????????????????????", 10, 1);
                new UpdateHardWare(context, fName, url, new UpdateHardWare.DownloadUpdateFileListener() {
                    @Override
                    public void onDownloadUpdateFileReturn(UpdateHardWare content, String dataPath) {
                        WebSocketUpdateHardWareDataFormat webSocketUpdateHardWareDataFormat = new WebSocketUpdateHardWareDataFormat(Integer.parseInt(tarDoor), dataPath, manu, CabInfoSp.getInstance().getTelNumber(), CabInfoSp.getInstance().getCabinetNumber_4600XXXX());
                        webSocketReturnDataAnalysisListener.dataReturn(new WebSocketReturnDataFormat(WebSocketReturnType.updateAllAcdc, webSocketUpdateHardWareDataFormat));
                    }
                });
            }
            //??????????????????????????????
            else if (type.equals("envBoardUpgrade")) {
                final String url = jsonObject.getString("url");
                final String fName = jsonObject.getString("name");
                final String version = jsonObject.getString("ver");
                createDialogJsonAndShow("?????????????????????????????????????????????", 10, 1);
                new UpdateHardWare(context, fName, url, new UpdateHardWare.DownloadUpdateFileListener() {
                    @Override
                    public void onDownloadUpdateFileReturn(UpdateHardWare content, String dataPath) {
                        WebSocketUpdateHardWareDataFormat webSocketUpdateHardWareDataFormat = new WebSocketUpdateHardWareDataFormat(0, dataPath, "0", CabInfoSp.getInstance().getTelNumber(), CabInfoSp.getInstance().getCabinetNumber_4600XXXX());
                        webSocketReturnDataAnalysisListener.dataReturn(new WebSocketReturnDataFormat(WebSocketReturnType.updateEnvironment, webSocketUpdateHardWareDataFormat));
                    }
                });

            }
            //???????????????  ??????android??????
            else if (type.equals("upgradeCabinetCore")) {
                String apkUrl = jsonObject.getString("apkurl");
                String zipUrl = jsonObject.getString("zipurl");
                UpdateAndroidCore updateAndroidCore = new UpdateAndroidCore(context, apkUrl, zipUrl);
                updateAndroidCore.onStart();
            }
            //????????? ???????????????
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
     * ??????json??? main?????? ??????dialog
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
     * ??????????????????
     *
     * @param log
     */
    public void writeLog(String log) {
        LocalLog.getInstance().writeLog(log , this);
    }

}
