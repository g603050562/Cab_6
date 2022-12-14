package client.halouhuandian.app15.service.logic.logicChangeBatteries;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import client.halouhuandian.app15.config.SystemConfig;
import client.halouhuandian.app15.hardWareConncetion.daa.DaaController;
import client.halouhuandian.app15.hardWareConncetion.daa.DaaIntegration;
import client.halouhuandian.app15.hardWareConncetion.daa.mode.DaaDataFormat;
import client.halouhuandian.app15.hardWareConncetion.daa.mode.dcdc.DcdcInfoByBaseFormat;
import client.halouhuandian.app15.hardWareConncetion.environmentPlate.EnvironmentController;
import client.halouhuandian.app15.model.dao.fileSave.LocalLog;
import client.halouhuandian.app15.model.dao.sharedPreferences.CabInfoSp;
import client.halouhuandian.app15.model.dao.sharedPreferences.ForbiddenSp;
import client.halouhuandian.app15.model.dao.sqlLite.ExchangeInfoDB;
import client.halouhuandian.app15.model.dao.sqlLite.OutLineExchangeSaveInfo;
import client.halouhuandian.app15.pub.util.UtilBattery;
import client.halouhuandian.app15.pub.util.UtilUidDictionart;
import client.halouhuandian.app15.service.logic.logicHttpConnection.http.BaseHttp;
import client.halouhuandian.app15.service.logic.logicHttpConnection.http.BaseHttpParameterFormat;
import client.halouhuandian.app15.service.logic.logicHttpConnection.http.HttpUrlMap;
import client.halouhuandian.app15.service.logic.logicHttpConnection.http.exchangeBattery.HttpOutLineCheckOldBind;
import client.halouhuandian.app15.service.logic.logicHttpConnection.http.exchangeBattery.HttpOutLineCheckUserBalance;
import client.halouhuandian.app15.service.logic.logicHttpConnection.http.exchangeBattery.IFHttpOutLineCheckOldBindListener;
import client.halouhuandian.app15.service.logic.logicHttpConnection.http.exchangeBattery.IFHttpOutLineCheckUserBalanceListener;
import client.halouhuandian.app15.service.logic.logicNetDBM.DataDistributionCurrentNetDBM;
import client.halouhuandian.app15.service.logic.logicOpenDoor.LogicOpenDoor;
import client.halouhuandian.app15.service.logic.logicOpenDoor.LogicOpenDoorReturnDataFormat;
import client.halouhuandian.app15.service.logic.logicWriteUid.LogicWriteUid;

/**
 * ???????????????
 * ???????????????????????????????????????
 */

//todo::?????? - ??????????????????????????????????????????????????? ?????????????????????????????? ????????????????????????????????? ??????????????? ???????????????????????????????????? ???????????????????????????????????? ??????????????????????????????(???????????????)

public class ChangeBatteriesProcess {

    public interface ChangeBatteriesControllerListener {
        void returnInchingTrigger(int door);
        void returnResult(ChangeBatteriesChangeDataFormat changeBatteriesDataFormat);
        void showDialog(String msg , int time , int type);
    }

    private Context context;
    private ChangeBatteriesControllerListener changeBatteriesControllerListener;

    private DaaDataFormat daaDataFormat;
    private DaaController.DaaControllerListener daaControllerListener;

    private String costType = "";
    private String costInfo = "";

    //???????????????
    private int maxBattery = 12;
    //???????????????????????????daa?????????????????? ??????????????? ????????????????????????
    private int[] innerInching = new int[]{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
    private String[] bids = new String[]{"FFFFFFFF","FFFFFFFF","FFFFFFFF","FFFFFFFF","FFFFFFFF","FFFFFFFF","FFFFFFFF","FFFFFFFF","FFFFFFFF","FFFFFFFF","FFFFFFFF","FFFFFFFF"};
    //?????????????????? ??????UID?????????????????????????????? ???????????????????????????????????? ??????????????????????????? ?????????pass??????
    private int[] exchangeState = new int[]{0,0,0,0,0,0,0,0,0,0,0,0};
    //????????????????????????
    private long[] innerInchingTriggerTime = new long[]{0,0,0,0,0,0,0,0,0,0,0,0};

    public ChangeBatteriesProcess(Context context , ChangeBatteriesControllerListener changeBatteriesControllerListener){
        this.context = context;
        this.changeBatteriesControllerListener = changeBatteriesControllerListener;

        DaaController.getInstance().addListener( daaControllerListener = new DaaController.DaaControllerListener() {
            @Override
            public void returnData(DaaDataFormat mDaaDataFormat, DaaIntegration.ReturnDataType returnDataType, int index) {
                daaDataFormat = mDaaDataFormat;
                if(returnDataType == DaaIntegration.ReturnDataType.dcdcInfoByBase){
                    onStart(index);
                    innerInching[index] = mDaaDataFormat.getDcdcInfoByBaseFormat(index).getInchingByInner();
                    bids[index] = mDaaDataFormat.getDcdcInfoByBaseFormat(index).getBID();
                }
            }
        });
    }

    private void onStart(final int index){
        final int inIndex = index;
        final int inDoor = index + 1;
        final int dbm = DataDistributionCurrentNetDBM.getInstance().getDbm();
        DcdcInfoByBaseFormat newDcdcInfoByBaseFormat = daaDataFormat.getDcdcInfoByBaseFormat(index);
        int newInchingByInner = newDcdcInfoByBaseFormat.getInchingByInner();
        int newInchingByOuterClose = newDcdcInfoByBaseFormat.getInchingByOuterClose();
        String newBid = newDcdcInfoByBaseFormat.getBID();
        String oldBid = bids[index];
        int oldInchingByInner = innerInching[index];
        boolean inchingState = (oldInchingByInner == 0 && newInchingByInner == 1);
        boolean bidState = (UtilBattery.is160(oldBid) && !UtilBattery.is160(newBid) && !UtilBattery.is16F(newBid));
        //0.??????????????????
        if(inchingState == true){
            innerInchingTriggerTime[index] = System.currentTimeMillis();
        }

        //1.???????????????0???1 ?????? ??????UID?????????????????????   ?????????????????????  ??????????????????
        if((inchingState || bidState) && exchangeState[index] == 0){
            //1_1.?????????BID????????? ???????????????????????????????????? ?????????????????????????????????????????? ???????????????????????????????????? ????????????
            if(inchingState == false && bidState == true && System.currentTimeMillis() - innerInchingTriggerTime[index] > 120 * 1000 && newInchingByOuterClose == 1){
                LocalLog.getInstance().writeLog("exchange - ??????BID???????????? ??????120s??????????????????????????? ???????????? - ???????????????????????? - " + innerInchingTriggerTime[index] + " - ???????????????????????? - " + System.currentTimeMillis());
                return;
            }
            exchangeState[index] = 1;
            LocalLog.getInstance().writeLog("exchange - ???????????? - inchingState - "+ inchingState + " - bidState - "+ bidState + " - ????????????");
            EnvironmentController.getInstance().hangUpButtonTrigger();
            changeBatteriesControllerListener.showDialog("?????????????????????????????????<$time>??????????????????????????????",5 , 1);
            changeBatteriesControllerListener.returnInchingTrigger(index + 1);
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {
                        //2.???????????? ????????????????????????
                        sleep(5000);
                        int isClose = daaDataFormat.getDcdcInfoByBaseFormat(inIndex).getInchingByOuterClose();
                        if(isClose == 0){
                            LogicOpenDoor.getInstance().putterPullAndReturnResult(inDoor, CabInfoSp.getInstance().getPutterActivityTime(), "???????????????????????????????????????????????????", new LogicOpenDoor.LogicOpenDoorAsynchronousListener() {
                                @Override
                                public void returnData(LogicOpenDoorReturnDataFormat logicOpenDoorReturnDataFormat) throws InterruptedException {
//                                    if(!logicOpenDoorReturnDataFormat.getResult()){
//                                        LogicOpenDoor.getInstance().putterPull(inDoor, CabInfoSp.getInstance().getPutterActivityTime(), "???????????????????????????????????????????????????");
//                                    }
                                }
                            });
                        }
                        //2_1.??????????????????????????????
                        int innerState = -1;
                        for(int i = 0 ; i < 10 ; i++){
                            int newInchingByOuterClose = daaDataFormat.getDcdcInfoByBaseFormat(inIndex).getInchingByOuterClose();
                            if(newInchingByOuterClose == 1){
                                innerState = 1;
                                break;
                            }
                            sleep(1000);
                        }
                        //2_2.?????????????????? ???????????????????????????????????????
                        if(innerState == -1){
                            changeBatteriesControllerListener.showDialog("???????????????????????????<$time>???????????????????????????????????????!",60 , 1);
                            LogicOpenDoor.getInstance().putterPushAndPullContainBattery(inDoor, CabInfoSp.getInstance().getPutterActivityTime(), 60, "??????????????????????????????????????????", new LogicOpenDoor.LogicOpenDoorDialogListener() {
                                @Override
                                public void showDialog(String msg, int time, int type) {
                                    changeBatteriesControllerListener.showDialog(msg,time,type);
                                }
                            });
                            onStop(index);
                            return;
                        }
                        //3.?????????????????? ????????????????????????
                        changeBatteriesControllerListener.showDialog("????????????"+inDoor+"??????????????????????????????",10 , 1);
                        int newInchingByInner = daaDataFormat.getDcdcInfoByBaseFormat(inIndex).getInchingByInner();
                        String bid = daaDataFormat.getDcdcInfoByBaseFormat(inIndex).getBID();
                        //3_1.?????????????????? ????????????BID?????? ?????????????????? ????????????
                        if(newInchingByInner == 0 && UtilBattery.is160(bid)){
                            changeBatteriesControllerListener.showDialog("??????????????????????????????????????????",10 , 1);
                            onStop(index);
                            return;
                        }
                        //3_2.????????????????????? ?????????????????????????????? ??????????????????
                        else if(newInchingByInner == 1 && UtilBattery.is160(bid)){
                            exchangeState[index] = 0;
                            changeBatteriesControllerListener.showDialog("???????????????????????????????????????",40 , 1);
                            int bisState = -1;
                            for(int i = 0 ; i < 40 ; i ++){
                                String mBid = daaDataFormat.getDcdcInfoByBaseFormat(inIndex).getBID();
                                if(!UtilBattery.is160(mBid) && !UtilBattery.is16F(mBid)){
                                    bisState = 1;
                                    break;
                                }
                                sleep(1000);
                            }
                            if(bisState == -1){
                                changeBatteriesControllerListener.showDialog("??????????????????????????????????????????????????????????????????????????????",30 , 1);
                                onStop(index);
                            }
                            return;
                        }
                        //4.????????????
                        changeBatteriesControllerListener.showDialog("????????????"+inDoor+"??????????????????????????????",10 , 1);
                        String inUid = daaDataFormat.getDcdcInfoByBaseFormat(inIndex).getUID();
                        final String inBid = daaDataFormat.getDcdcInfoByBaseFormat(inIndex).getBID();
                        final int inElectric = daaDataFormat.getDcdcInfoByBaseFormat(inIndex).getBatteryRelativeSurplus();
                        //4_1.8???A????????????????????????????????? ??????????????????
                        if(UtilBattery.is8A(inUid)){
                            HttpOutLineCheckOldBind httpOutLineCheckOldBind = new HttpOutLineCheckOldBind(CabInfoSp.getInstance().getCabinetNumber_4600XXXX(),inDoor+"", inBid, dbm, new IFHttpOutLineCheckOldBindListener() {
                                @Override
                                public void onHttpOutLineCheckOldBindResult(final String in_door, String code, String str, String data) {
                                    //4_1_1.???uid ????????? ???????????? ????????????
                                    if (code.equals("-1")) {
                                        changeBatteriesControllerListener.showDialog(inDoor+"??????????????????????????????????????????????????????????????????????????????", 10, 1);
                                        onStop(index);
                                        return;
                                    } else {
                                        //4_1_2.?????????????????????????????? ??? ????????????
                                        try {
                                            final JSONObject jsonObject = new JSONObject(data);
                                            //4_1_2_1.?????????
                                            if (code.equals("0")) {
                                                changeBatteriesControllerListener.showDialog(inDoor+"????????????????????????????????????????????????????????????????????????", 10, 1);
                                                onStop(index);
                                                return;
                                            }
                                            //4_1_2_2.????????????Uid
                                            else if (code.equals("1") || code.equals("2")) {
                                                JSONObject returnData = jsonObject.getJSONObject("data");
                                                final String uid32 = returnData.getString("uid32");
                                                new LogicWriteUid(inDoor, uid32, new LogicWriteUid.LogicWriteUidListener() {
                                                    @Override
                                                    public void returnStatus(boolean status) {
                                                        //4_1_2_2_1.?????????????????? ????????????????????????
                                                        if(status == true){
                                                            HttpOutLineCheckUserBalance httpOutLineCheckUserBalance = new HttpOutLineCheckUserBalance(CabInfoSp.getInstance().getCabinetNumber_4600XXXX(),uid32, inBid, inElectric+"", inDoor+"", dbm, ifHttpOutLineCheckUserBalanceListener);
                                                            httpOutLineCheckUserBalance.start();
                                                        }
                                                        //4_1_2_2_2.????????????????????? ???????????????
                                                        else{
                                                            changeBatteriesControllerListener.showDialog("??????????????????,???????????????????????????????????????????????????????????????????????????<$time>?????????????????????????????????????????????????????????????????????????????????", 10, 1);
                                                            LogicOpenDoor.getInstance().putterPushAndPullContainBattery(inDoor, CabInfoSp.getInstance().getPutterActivityTime(), 60, "??????UID?????????????????????", new LogicOpenDoor.LogicOpenDoorDialogListener() {
                                                                @Override
                                                                public void showDialog(String msg, int time, int type) {
                                                                    changeBatteriesControllerListener.showDialog(msg,time,type);
                                                                }
                                                            });
                                                            onStop(index);
                                                            return;
                                                        }
                                                    }

                                                    @Override
                                                    public void showDialog(String msg, int time, int type) {
                                                        changeBatteriesControllerListener.showDialog(msg,time,type);
                                                    }
                                                });
                                            }
                                            //4_1_2_3.??????????????????
                                            else {
                                                changeBatteriesControllerListener.showDialog("?????????????????????????????????????????????????????????", 10, 1);
                                                onStop(index);
                                                return;
                                            }
                                        } catch (Exception e) {
                                            LocalLog.getInstance().writeLog("exchange - error - " + e.toString());
                                            System.out.println("exchange - error - " + e.toString());
                                            changeBatteriesControllerListener.showDialog("?????????????????????????????????????????????????????????", 10, 1);
                                            onStop(index);
                                            return;
                                        }
                                    }
                                }
                            });
                            httpOutLineCheckOldBind.start();
                        }
                        //4_2.??????UID????????????????????? ??????????????????????????????
                        else{
                            HttpOutLineCheckUserBalance httpOutLineCheckUserBalance = new HttpOutLineCheckUserBalance(CabInfoSp.getInstance().getCabinetNumber_4600XXXX(), inUid, inBid, inElectric+"", inDoor+"", dbm, ifHttpOutLineCheckUserBalanceListener);
                            httpOutLineCheckUserBalance.start();
                        }

                    }catch (Exception e){
                        LocalLog.getInstance().writeLog("exchange - error - " + e.toString());
                        System.out.println("exchange - error - " + e.toString());
                        onStop(index);
                        return;
                    }
                }
            }.start();
        }else if((inchingState || bidState) && exchangeState[index] == 1){
            LocalLog.getInstance().writeLog("exchange - ???????????? - inchingState - "+ inchingState + " - bidState - "+ bidState + " - ??????????????????????????????");
        }
    }

    private IFHttpOutLineCheckUserBalanceListener ifHttpOutLineCheckUserBalanceListener = new IFHttpOutLineCheckUserBalanceListener() {
        @Override
        public void onHttpOutLineCheckUserBalanceResult(String uid, String door, String code, String str, String data) {

            LocalLog.getInstance().writeLog(data);
            final int index = Integer.parseInt(door) - 1;

            if (code.equals("-1")) {  //??????????????????
                exchangeUid(Integer.parseInt(door), uid, "????????????");
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    if (code.equals("0")) {
                        String show = jsonObject.getString("show");
                        String errno = jsonObject.getString("errno");
                        //?????????
                        if (errno.equals("E2001")) {
                            //?????????????????? ?????????
                            if (show.equals("1")) {
                                changeBatteriesControllerListener.showDialog(str, 10, 1);
                            }
                            onStop(index);
                            return;
                        }
                        //?????????????????????
                        else if (errno.equals("E1001")) {
                            if (show.equals("1")) {
                                changeBatteriesControllerListener.showDialog(str, 10, 1);
                            }
                            //???????????????????????? ???????????? ???????????????
                            changeBatteriesControllerListener.showDialog(str + "???????????????????????????????????????????????????????????????<$time>????????????????????????????????????????????????????????????????????????????????????", 60, 1);
                            LogicOpenDoor.getInstance().putterPushAndPullContainBattery(Integer.parseInt(door), CabInfoSp.getInstance().getPutterActivityTime(), 60, "????????????????????????E1001 - " + str, new LogicOpenDoor.LogicOpenDoorDialogListener() {
                                @Override
                                public void showDialog(String msg, int time, int type) {
                                    changeBatteriesControllerListener.showDialog(msg,time,type);
                                }
                            });
                            onStop(index);
                            return;
                        }
                    } else if (code.equals("1")) {
                        JSONObject returnData = jsonObject.getJSONObject("data");
                        String uid32 = returnData.getString("uid32");
                        String utype = returnData.getString("utype");
                        exchangeUid(Integer.parseInt(door), uid32, utype);
                    }

                } catch (Exception e) {
                    LocalLog.getInstance().writeLog("exchange - error - " + e.toString());
                    System.out.println("exchange - error - " + e.toString());
                    onStop(index);
                    return;
                }
            }
        }
    };

    private void exchangeUid(int door, String uid32, final String type) {
        final int inIndex = door - 1;
        final int fInDoor = door;
        final String fUid32 = uid32;
        costType = type;
        if (fInDoor >= 0) {
            //???????????????????????????
            final String fInBID = daaDataFormat.getDcdcInfoByBaseFormat(door-1).getBID();
            final int fInElectric = daaDataFormat.getDcdcInfoByBaseFormat(door-1).getBatteryRelativeSurplus();
            String in_TopBidStr = fInBID.substring(0, 1);
            String in_volt = "";
            if (in_TopBidStr.equals("M")) {
                in_volt = "60";
            } else if (in_TopBidStr.equals("N")) {
                in_volt = "48";
            } else {
                changeBatteriesControllerListener.showDialog("??????ID???????????????????????????????????????????????????????????????????????????", 20, 1);
                onStop(inIndex);
                return;
            }
            //??????????????????????????? ?????????????????????????????? ?????? 48v???60v???
            int outDoorBarPerMax = -1;
            int outDoorBarIndex = -1;
            for (int i = 0; i < SystemConfig.getMaxBattery(); i++) {
                String temBid = daaDataFormat.getDcdcInfoByBaseFormat(i).getBID();
                String temUid = daaDataFormat.getDcdcInfoByBaseFormat(i).getUID();
                int temElectric = daaDataFormat.getDcdcInfoByBaseFormat(i).getBatteryRelativeSurplus();
                String TopBidStr = temBid.substring(0, 1);
                String volt = "";
                if (TopBidStr.equals("M")) {
                    volt = "60";
                } else if (TopBidStr.equals("N")) {
                    volt = "48";
                } else {
                    volt = "0";
                }
                int is_stop = ForbiddenSp.getInstance().getTargetForbidden(i);
                if (temElectric > outDoorBarPerMax && in_volt.equals(volt) && UtilBattery.is8A(temUid) && is_stop == 1 && i != fInDoor - 1) {
                    outDoorBarPerMax = temElectric;
                    outDoorBarIndex = i;
                }
            }

            String phone = UtilUidDictionart.getI10PhoneNumber(fUid32);
            int outElectric =  daaDataFormat.getDcdcInfoByBaseFormat(outDoorBarIndex).getBatteryRelativeSurplus();
            if (!phone.equals("90000002352")) {
                if (outDoorBarIndex != -1 && outElectric < fInElectric) {
                    outDoorBarIndex = -2;
                }
            }

            if (outDoorBarIndex == fInDoor - 1) {
                outDoorBarIndex = -1;
            }

            //???????????????????????????
            if (outDoorBarIndex == -1) {
                changeBatteriesControllerListener.showDialog("????????????????????????????????????????????????????????????????????????????????????????????????????????????<$time>????????????????????????????????????????????????????????????????????????????????????", 60, 1);
                //???????????????????????? ???????????? ???????????????
                LogicOpenDoor.getInstance().putterPushAndPullContainBattery(fInDoor, CabInfoSp.getInstance().getPutterActivityTime(), 60, "??????????????????????????????????????????", new LogicOpenDoor.LogicOpenDoorDialogListener() {
                    @Override
                    public void showDialog(String msg, int time, int type) {
                        changeBatteriesControllerListener.showDialog(msg,time,type);
                    }
                });
                onStop(inIndex);
                return;
            } else if (outDoorBarIndex == -2) {
                changeBatteriesControllerListener.showDialog("??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????<$time>????????????????????????????????????????????????????????????????????????????????????", 60, 1);
                LogicOpenDoor.getInstance().putterPushAndPullContainBattery(fInDoor, CabInfoSp.getInstance().getPutterActivityTime(), 60, "???????????????????????????????????????????????????????????????", new LogicOpenDoor.LogicOpenDoorDialogListener() {
                    @Override
                    public void showDialog(String msg, int time, int type) {
                        changeBatteriesControllerListener.showDialog(msg,time,type);
                    }
                });
                onStop(inIndex);
                return;
            } else if (outDoorBarIndex == -3) {
                changeBatteriesControllerListener.showDialog(fInDoor + "???????????????????????????????????????????????????????????????????????????", 10, 1);
                onStop(inIndex);
                return;
            } else {
                //????????????????????????
                final int fOutDoor = (outDoorBarIndex + 1);
                final int fOutElectric = daaDataFormat.getDcdcInfoByBaseFormat(outDoorBarIndex).getBatteryRelativeSurplus();
                final String fOutBid = daaDataFormat.getDcdcInfoByBaseFormat(outDoorBarIndex).getBID() + "";
                LocalLog.getInstance().writeLog("???????????? - " + fOutDoor);

                new LogicWriteUid(fOutDoor, fUid32, new LogicWriteUid.LogicWriteUidListener() {
                    @Override
                    public void returnStatus(boolean status) {
                        if(status){
                            new LogicWriteUid(fInDoor, "AAAAAAAA", new LogicWriteUid.LogicWriteUidListener() {
                                @Override
                                public void returnStatus(boolean status) {
                                    if(!status){
                                        try {
                                            JSONObject jsonObject = new JSONObject();
                                            jsonObject.put("battery",fInBID);
                                            jsonObject.put("uid32","AAAAAAAA");
                                            jsonObject.put("extime", System.currentTimeMillis() + "");
                                            jsonObject.put("door",fInDoor);
                                            List<BaseHttpParameterFormat> baseHttpParameterFormats =  new ArrayList<>();
                                            baseHttpParameterFormats.add(new BaseHttpParameterFormat("number",CabInfoSp.getInstance().getCabinetNumber_4600XXXX()));
                                            baseHttpParameterFormats.add(new BaseHttpParameterFormat("error",jsonObject.toString()));
                                            baseHttpParameterFormats.add(new BaseHttpParameterFormat("type","10"));
                                            BaseHttp baseHttp = new BaseHttp(HttpUrlMap.UploadErrorInfo,baseHttpParameterFormats);
                                            baseHttp.onStart();
                                        } catch (JSONException e) {
                                            LocalLog.getInstance().writeLog("exchange - error - " + e.toString());
                                            System.out.println("exchange - error - " + e.toString());
                                        }
                                        ForbiddenSp.getInstance().setTargetForbidden(fInDoor - 1, -3);
                                    }
                                    String tel = UtilUidDictionart.getI10EndPhoneNumber(fUid32);
                                    changeBatteriesControllerListener.showDialog("???????????????" + tel + "??????????????????" + (fOutDoor) + "??????????????????????????????<$time>???????????????????????????????????????????????????????????????????????????????????????", 60, 1);
                                    LogicOpenDoor.getInstance().putterPushAndPullContainBattery(fOutDoor, CabInfoSp.getInstance().getPutterActivityTime(), 60, "???????????????????????????", new LogicOpenDoor.LogicOpenDoorDialogListener() {
                                        @Override
                                        public void showDialog(String msg, int time, int type) {
                                            changeBatteriesControllerListener.showDialog(msg,time,type);
                                        }
                                    });
                                    //????????????
                                    changeBatteriesControllerListener.returnResult(new ChangeBatteriesChangeDataFormat(fInDoor , fInBID , fInElectric , fOutDoor , fOutBid , fOutElectric ,costType , costInfo));
                                    //???????????????
                                    OutLineExchangeSaveInfo outLineExchangeSaveInfo = new OutLineExchangeSaveInfo(CabInfoSp.getInstance().getCabinetNumber_4600XXXX(), fUid32, System.currentTimeMillis() + "", fInBID, fInDoor + "", fInElectric + "", fOutBid + "", fOutDoor + "", fOutElectric + "");
                                    ExchangeInfoDB.getInstance(context).insertData(outLineExchangeSaveInfo);
                                    onStop(inIndex);
                                    return;
                                }
                                @Override
                                public void showDialog(String msg, int time, int type) {
                                    changeBatteriesControllerListener.showDialog(msg,time,type);
                                }
                            });
                        }else{
                            changeBatteriesControllerListener.showDialog("?????????????????????????????????????????????????????????????????????????????????????????????????????????<$time>????????????????????????????????????????????????????????????????????????????????????", 60, 1);
                            LogicOpenDoor.getInstance().putterPushAndPullContainBattery(fInDoor, CabInfoSp.getInstance().getPutterActivityTime(), 60, "???????????????UID???????????????", new LogicOpenDoor.LogicOpenDoorDialogListener() {
                                @Override
                                public void showDialog(String msg, int time, int type) {
                                    changeBatteriesControllerListener.showDialog(msg,time,type);
                                }
                            });
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("battery",fOutBid);
                                jsonObject.put("uid32","fUid32");
                                jsonObject.put("extime", System.currentTimeMillis() + "");
                                jsonObject.put("door",fOutBid);
                                List<BaseHttpParameterFormat> baseHttpParameterFormats =  new ArrayList<>();
                                baseHttpParameterFormats.add(new BaseHttpParameterFormat("number",CabInfoSp.getInstance().getCabinetNumber_4600XXXX()));
                                baseHttpParameterFormats.add(new BaseHttpParameterFormat("error",jsonObject.toString()));
                                baseHttpParameterFormats.add(new BaseHttpParameterFormat("type","10"));
                                BaseHttp baseHttp = new BaseHttp(HttpUrlMap.UploadErrorInfo,baseHttpParameterFormats);
                                baseHttp.onStart();
                            } catch (JSONException e) {
                                LocalLog.getInstance().writeLog("exchange - error - " + e.toString());
                                System.out.println("exchange - error - " + e.toString());
                            }
                            onStop(inIndex);
                            return;
                        }
                    }

                    @Override
                    public void showDialog(String msg, int time, int type) {
                        changeBatteriesControllerListener.showDialog(msg,time,type);
                    }
                });
            }
        } else {
            changeBatteriesControllerListener.showDialog("??????????????????????????????", 10, 1);
            onStop(inIndex);
            return;
        }
    }

    private void onStop(int index){
        costInfo = "";
        costType = "";
        exchangeState[index] = 0;
        ChangeBatteriesController.getInstance().exchangeFinish();
        EnvironmentController.getInstance().hangUpButtonTriggerCancel();
    }

    public void onDestroy(){
        DaaController.getInstance().deleteListener(daaControllerListener);
    }
}
