package client.halouhuandian.app15.service.logic.logicHttpConnection.webSocket;

import android.content.Context;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import client.halouhuandian.app15.model.dao.fileSave.LocalLog;
import client.halouhuandian.app15.model.dao.sharedPreferences.CabInfoSp;
import client.halouhuandian.app15.service.logic.logicHttpConnection.http.BaseHttp;
import client.halouhuandian.app15.service.logic.logicHttpConnection.http.BaseHttpParameterFormat;
import client.halouhuandian.app15.service.logic.logicHttpConnection.http.HttpUrlMap;


/**
 * 长链接 建立维持
 * 字段注册以及接收 还有 这个线程的生命维持
 */

public class WebSocketLongLink {

    //上下文
    private Context context;
    //长链接下发这次连接的id 需要进行HTTP绑定
    private String clientID;
    //客户端
    private WebSocketClient mWebSocketClient;

    //长链接保护进程
    private Thread longLinkProtectThread = null;
    //长链接保护进程参数
    private int longLinkProtectThreadState = 0;
    //长链接保护进程计数参数
    private int longLinkProtectThreadCode = 60;

    //长链接心跳线程
    private Thread longLinkHeartThread = null;
    //长链接心跳线程参数
    private int longLinkHeartThreadCode = 0;
    //长链接心跳线程计数
    private int longLinkHeartThreadCount = 0;

    //挂起参数
    private int hangUpState = 0;

    //长链接下发命令参数
    private String[] issueOrders = new String[]{"bindSuccess","bindmxOK", "openCabBackDoor", "restartAndrBoard", "cmdRemoteOpenAdmin", "cmdAlertMsg", "remoteSendCabStat", "asyncAnalyzeOpenDoor", "updateCabinetApp",
            "remoteOpenDoor", "getBatteryInfo", "rentBtyList", "pushrodActSetTime", "setPushRodLitVal", "removeSetHeatMode", "updateHard", "rentBattery",
            "disableDoorOut", "updateAmmeter", "setThreadsProtectionStatus", "updateOneBattery", "updateOneHardDoor", "upVideoFileList", "upVideoFile", "updatePdu",
            "writeBtyUid", "activateBattery", "upgradeDcdc", "upgradeDcdcAll", "upgradeAcdcAll", "upgradeAcdc", "defsetDcdc", "setStopDcdc",
            "resetDcdc", "remoteCloseDoor", "envBoardUpgrade", "remoteSprayWater", "cabBottomHope" , "upLogFileList" , "upLogFileToServ" , "setGlobalDomain"};

    //返回接口
    public interface IFHttpOpenLongLinkLinstener {
        void onHttpReTurnIDResult(String code);

        void onHttpReturnDataResult(String data);

        void onHttpReturnErrorResult(int data);
    }

    //接口
    private IFHttpOpenLongLinkLinstener ifHttpOpenLongLinkLinstener;

    //初始化
    public void init(Context context, IFHttpOpenLongLinkLinstener ifHttpOpenLongLinkLinstener) {
        this.context = context;
        this.ifHttpOpenLongLinkLinstener = ifHttpOpenLongLinkLinstener;
        onStart();
    }

    //挂起命令
    public void hangUp() {
        hangUpState = 1;
    }

    //解除挂起命令
    public void hangUpCancel() {
        hangUpState = 0;
    }

    public void onStart() {
        //初始化保护进程
        if (longLinkProtectThread == null) {
            longLinkProtectThread = new Thread() {
                @Override
                public void run() {
                    super.run();
                    while (longLinkProtectThreadState == 0) {
                        try {
                            if (longLinkProtectThreadCode > 0) {
                                longLinkProtectThreadCode = longLinkProtectThreadCode - 1;
                            } else {
                                closeConnect();
                                initSocketClient();
                                mWebSocketClient.connect();
                                ifHttpOpenLongLinkLinstener.onHttpReturnErrorResult(0);
                                longLinkProtectThreadCode = 60;
                                LocalLog.getInstance().writeLog("longLink - step - connect", WebSocketLongLink.class);
                            }
                            sleep(1000);
                        } catch (Exception e) {
                            LocalLog.getInstance().writeLog("longLink - error - " + e.toString(), WebSocketLongLink.class);
                        }
                    }
                }
            };
            longLinkProtectThread.start();
        }

        //webSocket初始化
        closeConnect();
        initSocketClient();
        mWebSocketClient.connect();
        LocalLog.getInstance().writeLog("longLink - step - connect", WebSocketLongLink.class);
    }


    //客户端初始化
    private void initSocketClient(){

        try {

            if (mWebSocketClient == null) {

                mWebSocketClient = new WebSocketClient(new URI(HttpUrlMap.longlink)) {
                    @Override
                    public void onOpen(ServerHandshake serverHandshake) {
                        System.out.println("longLink - init - " + serverHandshake.getHttpStatusMessage());
                    }

                    @Override
                    public void onMessage(final String s) {
                        //这个是收到服务器推送下来的消息
                        System.out.println("longLink - receive - " + s);
                        longLinkProtectThreadCode = 60;

                        try {
                            JSONTokener jsonTokener = new JSONTokener(s);
                            JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                            String type = jsonObject.getString("type");

                            if (type.equals("init")) {
                                clientID = jsonObject.getString("fd");
                                ifHttpOpenLongLinkLinstener.onHttpReTurnIDResult(clientID);
                            }
                            else {
                                if (hangUpState == 0) {
                                    for (int i = 0; i < issueOrders.length; i++) {
                                        if (type.equals(issueOrders[i])) {
                                            ifHttpOpenLongLinkLinstener.onHttpReturnDataResult(jsonObject.toString());
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            LocalLog.getInstance().writeLog("longLink - error - " + e.toString() , WebSocketLongLink.class);
                        }
                    }

                    @Override
                    public void onClose(int i, String s, boolean remote) {
                        //连接断开，remote判定是客户端断开还是服务端断开
                        LocalLog.getInstance().writeLog("longLink - error - " + "longLink - close - Connection closed by " + (remote ? "remote peer" : "us") + ", info=" + s , WebSocketLongLink.class);
                    }

                    @Override
                    public void onError(Exception e) {
                        LocalLog.getInstance().writeLog("longLink - error - " + e.toString() , WebSocketLongLink.class);
                    }
                };
            }

            if(longLinkHeartThread == null){
                longLinkHeartThread = new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        longLinkHeartThreadCount = 0;
                        while (longLinkHeartThreadCode == 0){
                            try {
                                sleep(1000);
                                longLinkHeartThreadCount = longLinkHeartThreadCount + 1;
                                if(longLinkHeartThreadCount == 10 || longLinkHeartThreadCount == 30 || longLinkHeartThreadCount == 50 || longLinkHeartThreadCount == 70){
                                    mWebSocketClient.send("{\"type\":\"ping\",\"number\":\""+ CabInfoSp.getInstance().getCabinetNumber_4600XXXX() +"\"}");
                                    ifHttpOpenLongLinkLinstener.onHttpReturnErrorResult(1);
                                }

                                if(longLinkHeartThreadCount >= 80){
                                    longLinkHeartThreadCount = 0;
                                    List<BaseHttpParameterFormat> baseHttpParameterFormats =  new ArrayList<>();
                                    baseHttpParameterFormats.add(new BaseHttpParameterFormat("number",CabInfoSp.getInstance().getCabinetNumber_4600XXXX()));
                                    baseHttpParameterFormats.add(new BaseHttpParameterFormat("client_id",CabInfoSp.getInstance().getCabinetClientId()));
                                    BaseHttp baseHttp = new BaseHttp(HttpUrlMap.HttpBeat, baseHttpParameterFormats, 15, new BaseHttp.BaseHttpListener() {
                                        @Override
                                        public void dataReturn(int code, String message, String data) {
                                        }
                                    });
                                    baseHttp.onStart();
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                longLinkHeartThread.start();
            }

        } catch (Exception e) {
            LocalLog.getInstance().writeLog("longLink - error - " + e.toString(), WebSocketLongLink.class);
        }
    }

    private void closeConnect() {
        if (mWebSocketClient == null) {
            return;
        } else {
            mWebSocketClient.closeConnection(0, "onDestory");
            mWebSocketClient.close();
            mWebSocketClient = null;
            LocalLog.getInstance().writeLog("longLink - step - closeConnect", WebSocketLongLink.class);
        }
    }

    public void onDestroy() {
        longLinkProtectThreadState = 1;
        longLinkHeartThreadCode = 1;
        closeConnect();
    }
}
