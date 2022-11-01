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
 * 换电主逻辑
 * 类内部得到分发数据自己触发
 */

//todo::漏洞 - 如果同时打开两个舱门（一般不是出现 但是人为操作的话会） 会给两块儿电池同时换电 会引发冲突 应该一块儿电池进行的同时 其他换电线程挂起等待结束 （虽然是小概率事件）(任务队列吧)

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

    //最大电池数
    private int maxBattery = 12;
    //因为返回的是同一个daa内存地址对象 无法做比较 所以新建比较数据
    private int[] innerInching = new int[]{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
    private String[] bids = new String[]{"FFFFFFFF","FFFFFFFF","FFFFFFFF","FFFFFFFF","FFFFFFFF","FFFFFFFF","FFFFFFFF","FFFFFFFF","FFFFFFFF","FFFFFFFF","FFFFFFFF","FFFFFFFF"};
    //换电触发参数 因为UID和微动都可以触发换电 所以当一个触发换电的时候 另外一个再触达的话 就直接pass掉了
    private int[] exchangeState = new int[]{0,0,0,0,0,0,0,0,0,0,0,0};
    //内侧微动触发时间
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
        //0.微动触发计时
        if(inchingState == true){
            innerInchingTriggerTime[index] = System.currentTimeMillis();
        }

        //1.里侧微动从0到1 或者 电池UID从无到有的状态   有电池进入舱门  准备开始换电
        if((inchingState || bidState) && exchangeState[index] == 0){
            //1_1.如果是BID触发的 先看看之前有没有微动触发 如果没有认为是电池在舱门内部 电池信息一段时间没有上传 不给换电
            if(inchingState == false && bidState == true && System.currentTimeMillis() - innerInchingTriggerTime[index] > 120 * 1000 && newInchingByOuterClose == 1){
                LocalLog.getInstance().writeLog("exchange - 电池BID触发换电 但是120s内没有内部微动触发 不给换电 - 上次微动触发时间 - " + innerInchingTriggerTime[index] + " - 这次微动触发时间 - " + System.currentTimeMillis());
                return;
            }
            exchangeState[index] = 1;
            LocalLog.getInstance().writeLog("exchange - 开始换电 - inchingState - "+ inchingState + " - bidState - "+ bidState + " - 触发消费");
            EnvironmentController.getInstance().hangUpButtonTrigger();
            changeBatteriesControllerListener.showDialog("正在检测舱门，舱门将在<$time>秒后关闭，请注意安全",5 , 1);
            changeBatteriesControllerListener.returnInchingTrigger(index + 1);
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {
                        //2.收回舱门 检测微动是否压死
                        sleep(5000);
                        int isClose = daaDataFormat.getDcdcInfoByBaseFormat(inIndex).getInchingByOuterClose();
                        if(isClose == 0){
                            LogicOpenDoor.getInstance().putterPullAndReturnResult(inDoor, CabInfoSp.getInstance().getPutterActivityTime(), "换电检测到里侧微动变化，关闭舱门！", new LogicOpenDoor.LogicOpenDoorAsynchronousListener() {
                                @Override
                                public void returnData(LogicOpenDoorReturnDataFormat logicOpenDoorReturnDataFormat) throws InterruptedException {
//                                    if(!logicOpenDoorReturnDataFormat.getResult()){
//                                        LogicOpenDoor.getInstance().putterPull(inDoor, CabInfoSp.getInstance().getPutterActivityTime(), "换电检测到里侧微动变化，关闭舱门！");
//                                    }
                                }
                            });
                        }
                        //2_1.检测舱门是否彻底关闭
                        int innerState = -1;
                        for(int i = 0 ; i < 10 ; i++){
                            int newInchingByOuterClose = daaDataFormat.getDcdcInfoByBaseFormat(inIndex).getInchingByOuterClose();
                            if(newInchingByOuterClose == 1){
                                innerState = 1;
                                break;
                            }
                            sleep(1000);
                        }
                        //2_2.舱门关闭失败 开启舱门然后再次让用户换电
                        if(innerState == -1){
                            changeBatteriesControllerListener.showDialog("舱门关闭失败，请在<$time>请取出您的电池再次尝试换电!",60 , 1);
                            LogicOpenDoor.getInstance().putterPushAndPullContainBattery(inDoor, CabInfoSp.getInstance().getPutterActivityTime(), 60, "舱门未关闭，打开舱门取走电池", new LogicOpenDoor.LogicOpenDoorDialogListener() {
                                @Override
                                public void showDialog(String msg, int time, int type) {
                                    changeBatteriesControllerListener.showDialog(msg,time,type);
                                }
                            });
                            onStop(index);
                            return;
                        }
                        //3.舱门关闭成功 开始检测舱门信息
                        changeBatteriesControllerListener.showDialog("正在检测"+inDoor+"号舱门信息，请稍候！",10 , 1);
                        int newInchingByInner = daaDataFormat.getDcdcInfoByBaseFormat(inIndex).getInchingByInner();
                        String bid = daaDataFormat.getDcdcInfoByBaseFormat(inIndex).getBID();
                        //3_1.里侧微动为空 并且电池BID为空 没有电池插入 换电结束
                        if(newInchingByInner == 0 && UtilBattery.is160(bid)){
                            changeBatteriesControllerListener.showDialog("未检测到电池信息，换电结束！",10 , 1);
                            onStop(index);
                            return;
                        }
                        //3_2.里侧微动不为空 但是检测不到电池信息 电池需要激活
                        else if(newInchingByInner == 1 && UtilBattery.is160(bid)){
                            exchangeState[index] = 0;
                            changeBatteriesControllerListener.showDialog("正在尝试激活电池，请稍候！",40 , 1);
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
                                changeBatteriesControllerListener.showDialog("电池激活失败，将被回收，如有问题请拨打电话客服咨询！",30 , 1);
                                onStop(index);
                            }
                            return;
                        }
                        //4.开始换电
                        changeBatteriesControllerListener.showDialog("正在检测"+inDoor+"号电池信息，请稍候！",10 , 1);
                        String inUid = daaDataFormat.getDcdcInfoByBaseFormat(inIndex).getUID();
                        final String inBid = daaDataFormat.getDcdcInfoByBaseFormat(inIndex).getBID();
                        final int inElectric = daaDataFormat.getDcdcInfoByBaseFormat(inIndex).getBatteryRelativeSurplus();
                        //4_1.8个A的电池为没有绑定的电池 请求绑定接口
                        if(UtilBattery.is8A(inUid)){
                            HttpOutLineCheckOldBind httpOutLineCheckOldBind = new HttpOutLineCheckOldBind(CabInfoSp.getInstance().getCabinetNumber_4600XXXX(),inDoor+"", inBid, dbm, new IFHttpOutLineCheckOldBindListener() {
                                @Override
                                public void onHttpOutLineCheckOldBindResult(final String in_door, String code, String str, String data) {
                                    //4_1_1.没uid 也没网 放弃换电 回收电池
                                    if (code.equals("-1")) {
                                        changeBatteriesControllerListener.showDialog(inDoor+"号舱门电池未绑定，将被回收，如有问题请拨打电话客服！", 10, 1);
                                        onStop(index);
                                        return;
                                    } else {
                                        //4_1_2.根据服务器返回的信息 做 后续处理
                                        try {
                                            final JSONObject jsonObject = new JSONObject(data);
                                            //4_1_2_1.吞电池
                                            if (code.equals("0")) {
                                                changeBatteriesControllerListener.showDialog(inDoor+"号电池未绑定，将被回收，如有问题请拨打电话客服！", 10, 1);
                                                onStop(index);
                                                return;
                                            }
                                            //4_1_2_2.写入电池Uid
                                            else if (code.equals("1") || code.equals("2")) {
                                                JSONObject returnData = jsonObject.getJSONObject("data");
                                                final String uid32 = returnData.getString("uid32");
                                                new LogicWriteUid(inDoor, uid32, new LogicWriteUid.LogicWriteUidListener() {
                                                    @Override
                                                    public void returnStatus(boolean status) {
                                                        //4_1_2_2_1.电池写入成功 请求换电检查接口
                                                        if(status == true){
                                                            HttpOutLineCheckUserBalance httpOutLineCheckUserBalance = new HttpOutLineCheckUserBalance(CabInfoSp.getInstance().getCabinetNumber_4600XXXX(),uid32, inBid, inElectric+"", inDoor+"", dbm, ifHttpOutLineCheckUserBalanceListener);
                                                            httpOutLineCheckUserBalance.start();
                                                        }
                                                        //4_1_2_2_2.电池写入不成功 原电池弹出
                                                        else{
                                                            changeBatteriesControllerListener.showDialog("电池写入失败,请尝试重新换电，正在打开舱门，请注意安全，舱门将在<$time>秒后关闭，请尽快取出您的电池，如有问题请拨打电话客服！", 10, 1);
                                                            LogicOpenDoor.getInstance().putterPushAndPullContainBattery(inDoor, CabInfoSp.getInstance().getPutterActivityTime(), 60, "写入UID失败，弹出电池", new LogicOpenDoor.LogicOpenDoorDialogListener() {
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
                                            //4_1_2_3.其他数值错误
                                            else {
                                                changeBatteriesControllerListener.showDialog("网络请求失败，如有问题请拨打电话客服！", 10, 1);
                                                onStop(index);
                                                return;
                                            }
                                        } catch (Exception e) {
                                            LocalLog.getInstance().writeLog("exchange - error - " + e.toString());
                                            System.out.println("exchange - error - " + e.toString());
                                            changeBatteriesControllerListener.showDialog("网络请求失败，如有问题请拨打电话客服！", 10, 1);
                                            onStop(index);
                                            return;
                                        }
                                    }
                                }
                            });
                            httpOutLineCheckOldBind.start();
                        }
                        //4_2.携带UID的电池请求接口 查看是否符合换电要求
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
            LocalLog.getInstance().writeLog("exchange - 开始换电 - inchingState - "+ inchingState + " - bidState - "+ bidState + " - 此次换电触发已被消费");
        }
    }

    private IFHttpOutLineCheckUserBalanceListener ifHttpOutLineCheckUserBalanceListener = new IFHttpOutLineCheckUserBalanceListener() {
        @Override
        public void onHttpOutLineCheckUserBalanceResult(String uid, String door, String code, String str, String data) {

            LocalLog.getInstance().writeLog(data);
            final int index = Integer.parseInt(door) - 1;

            if (code.equals("-1")) {  //开启离线换电
                exchangeUid(Integer.parseInt(door), uid, "离线换电");
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    if (code.equals("0")) {
                        String show = jsonObject.getString("show");
                        String errno = jsonObject.getString("errno");
                        //吞电池
                        if (errno.equals("E2001")) {
                            //未绑定的电池 吞电池
                            if (show.equals("1")) {
                                changeBatteriesControllerListener.showDialog(str, 10, 1);
                            }
                            onStop(index);
                            return;
                        }
                        //弹出插入的电池
                        else if (errno.equals("E1001")) {
                            if (show.equals("1")) {
                                changeBatteriesControllerListener.showDialog(str, 10, 1);
                            }
                            //根据返回的错误码 未知原因 吐出原电池
                            changeBatteriesControllerListener.showDialog(str + "正在弹出插入的电池，请您注意安全，舱门将在<$time>秒后关闭，请尽快取出您的电池，如有问题请拨打电话客服咨询", 60, 1);
                            LogicOpenDoor.getInstance().putterPushAndPullContainBattery(Integer.parseInt(door), CabInfoSp.getInstance().getPutterActivityTime(), 60, "错误码弹出电池：E1001 - " + str, new LogicOpenDoor.LogicOpenDoorDialogListener() {
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
            //判断插入电池的类型
            final String fInBID = daaDataFormat.getDcdcInfoByBaseFormat(door-1).getBID();
            final int fInElectric = daaDataFormat.getDcdcInfoByBaseFormat(door-1).getBatteryRelativeSurplus();
            String in_TopBidStr = fInBID.substring(0, 1);
            String in_volt = "";
            if (in_TopBidStr.equals("M")) {
                in_volt = "60";
            } else if (in_TopBidStr.equals("N")) {
                in_volt = "48";
            } else {
                changeBatteriesControllerListener.showDialog("电池ID不符合标准，将被回收，如有问题请联系电话客服咨询！", 20, 1);
                onStop(inIndex);
                return;
            }
            //找出电量最高的电池 并且还得是符合标准的 比如 48v和60v的
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

            //没有找到合适的电池
            if (outDoorBarIndex == -1) {
                changeBatteriesControllerListener.showDialog("没有符合标准的电池，换电结束！正在弹出插入的电池，请您注意安全，舱门将在<$time>秒后关闭，请尽快取出您的电池，如有问题请拨打电话客服咨询", 60, 1);
                //根据返回的错误码 未知原因 吐出原电池
                LogicOpenDoor.getInstance().putterPushAndPullContainBattery(fInDoor, CabInfoSp.getInstance().getPutterActivityTime(), 60, "没有可以选择的电池，换电结束", new LogicOpenDoor.LogicOpenDoorDialogListener() {
                    @Override
                    public void showDialog(String msg, int time, int type) {
                        changeBatteriesControllerListener.showDialog(msg,time,type);
                    }
                });
                onStop(inIndex);
                return;
            } else if (outDoorBarIndex == -2) {
                changeBatteriesControllerListener.showDialog("您的电池电量高于当前电柜最大值，无需换电！正在弹出插入的电池，请您注意安全，舱门将在<$time>秒后关闭，请尽快取出您的电池，如有问题请拨打电话客服咨询", 60, 1);
                LogicOpenDoor.getInstance().putterPushAndPullContainBattery(fInDoor, CabInfoSp.getInstance().getPutterActivityTime(), 60, "您的电池电量高于当前电柜最大值，无需换电！", new LogicOpenDoor.LogicOpenDoorDialogListener() {
                    @Override
                    public void showDialog(String msg, int time, int type) {
                        changeBatteriesControllerListener.showDialog(msg,time,type);
                    }
                });
                onStop(inIndex);
                return;
            } else if (outDoorBarIndex == -3) {
                changeBatteriesControllerListener.showDialog(fInDoor + "号仓电池未绑定，将被回收，如有问题请拨打电话咨询！", 10, 1);
                onStop(inIndex);
                return;
            } else {
                //给数据库记录数据
                final int fOutDoor = (outDoorBarIndex + 1);
                final int fOutElectric = daaDataFormat.getDcdcInfoByBaseFormat(outDoorBarIndex).getBatteryRelativeSurplus();
                final String fOutBid = daaDataFormat.getDcdcInfoByBaseFormat(outDoorBarIndex).getBID() + "";
                LocalLog.getInstance().writeLog("目标电池 - " + fOutDoor);

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
                                    changeBatteriesControllerListener.showDialog("请手机尾号" + tel + "的用户拿走第" + (fOutDoor) + "号舱门电池，舱门将在<$time>秒后关闭，请尽快取出您的电池，如有问题请拨打电话客服咨询！", 60, 1);
                                    LogicOpenDoor.getInstance().putterPushAndPullContainBattery(fOutDoor, CabInfoSp.getInstance().getPutterActivityTime(), 60, "换电成功，换电结束", new LogicOpenDoor.LogicOpenDoorDialogListener() {
                                        @Override
                                        public void showDialog(String msg, int time, int type) {
                                            changeBatteriesControllerListener.showDialog(msg,time,type);
                                        }
                                    });
                                    //开启动画
                                    changeBatteriesControllerListener.returnResult(new ChangeBatteriesChangeDataFormat(fInDoor , fInBID , fInElectric , fOutDoor , fOutBid , fOutElectric ,costType , costInfo));
                                    //插入数据库
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
                            changeBatteriesControllerListener.showDialog("电池写入信息失败，换电结束，正在弹出插入的电池，请您注意安全，舱门将在<$time>秒后关闭，请尽快取出您的电池，如有问题请拨打电话客服咨询", 60, 1);
                            LogicOpenDoor.getInstance().putterPushAndPullContainBattery(fInDoor, CabInfoSp.getInstance().getPutterActivityTime(), 60, "电柜内电池UID写入失败！", new LogicOpenDoor.LogicOpenDoorDialogListener() {
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
            changeBatteriesControllerListener.showDialog("非法操作，换电结束！", 10, 1);
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
