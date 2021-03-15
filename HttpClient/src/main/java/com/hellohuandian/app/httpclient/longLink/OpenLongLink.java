package com.hellohuandian.app.httpclient.longLink;

import com.hellohuandian.app.httpclient.MyApplication;
import com.hellohuandian.pubfunction.Unit.LogUtil;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;


/**
 * Created by apple on 2017/7/27.
 */

public class OpenLongLink {

    private WebSocketClient mWebSocketClient;

    private String client_id;
    private long keepTime;

    public interface IFHttpOpenLongLinkLinstener {
        void onHttpReTurnIDResult(String code);

        void onHttpReturnDataResult(String data);

        void onHttpReturnErrorResult(int data);
    }

    private IFHttpOpenLongLinkLinstener ifHttpOpenLongLinkLinstener;

    //长链接保护程序 有时候第一次长连接解析不了
    int longlink_save_code = 0;
    int longlink_save_count = 20;
    private Thread thread_longlink_save = new Thread() {
        @Override
        public void run() {
            super.run();

            while (longlink_save_code == 0) {
                if (longlink_save_count > 0) {
                    longlink_save_count = longlink_save_count - 1;
                } else {
                    try {
                        closeConnect();
                        initSocketClient();
                        mWebSocketClient.connect();
                        ifHttpOpenLongLinkLinstener.onHttpReturnErrorResult(0);
                        LogUtil.I("longlink :    调用connect()");
                    } catch (Exception e) {
                        LogUtil.I("longlink :    " + e.toString());
                    }
                    longlink_save_count = 20;
                }
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    public OpenLongLink(IFHttpOpenLongLinkLinstener ifHttpOpenLongLinkLinstener) {

        this.ifHttpOpenLongLinkLinstener = ifHttpOpenLongLinkLinstener;

        if (!thread_longlink_save.isAlive()) {
            thread_longlink_save.start();
        }

        try {
            closeConnect();
            initSocketClient();
            mWebSocketClient.connect();
            LogUtil.I("longlink :    调用connect()");
        } catch (Exception e) {
            LogUtil.I("longlink :    " + e.toString());
        }

    }


    private void initSocketClient() throws URISyntaxException {

        LogUtil.I("longlink :    长连接请求连接");

        if (mWebSocketClient == null) {

            mWebSocketClient = new WebSocketClient(new URI(MyApplication.longlink)) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    LogUtil.I("longlink :    onOpen first " + serverHandshake.toString());
                }

                @Override
                public void onMessage(final String s) {
                    //这个是收到服务器推送下来的消息
                    LogUtil.I("longlink :    " + s);
                    longlink_save_count = 20;
                    try {
                        JSONTokener jsonTokener = new JSONTokener(s);
                        JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                        String type = jsonObject.getString("type");

                        if (type.equals("init")) {
                            client_id = jsonObject.getString("client_id");
                            ifHttpOpenLongLinkLinstener.onHttpReTurnIDResult(client_id);
                        } else if (
                                type.equals("bindSuccess") ||
                                        type.equals("openCabBackDoor") ||
                                        type.equals("restartAndrBoard") ||
                                        type.equals("cmdRemoteOpenAdmin") ||
                                        type.equals("cmdAlertMsg") ||
                                        type.equals("remoteSendCabStat") ||
                                        type.equals("asyncAnalyzeOpenDoor") ||
                                        type.equals("updateCabinetApp") ||
                                        type.equals("remoteOpenDoor") ||
                                        type.equals("getBatteryInfo") ||
                                        type.equals("rentBtyList") ||
                                        type.equals("pushrodActSetTime") ||
                                        type.equals("setPushRodLitVal") ||
                                        type.equals("removeSetHeatMode") ||

                                        type.equals("updateHard") ||
                                        type.equals("rentBattery") ||
                                        type.equals("bindSuccess") ||
                                        type.equals("disableDoorOut") ||
                                        type.equals("updateAmmeter") ||
                                        type.equals("setThreadsProtectionStatus") ||
                                        type.equals("updateOneBattery") ||
                                        type.equals("updateOneHardDoor") ||
                                        type.equals("upVideoFileList") ||
                                        type.equals("upVideoFile") ||
                                        type.equals("updatePdu") ||
                                        type.equals("writeBtyUid") ||
                                        type.equals("activateBattery") ||
                                        type.equals("upgradeDcdc") ||
                                        type.equals("upgradeDcdcAll") ||
                                        type.equals("upgradeAcdcAll") ||
                                        type.equals("upgradeAcdc") ||
                                        type.equals("defsetDcdc") ||
                                        type.equals("setStopDcdc") ||
                                        type.equals("resetDcdc") ||
                                        type.equals("remoteCloseDoor") ||
                                        type.equals("envBoardUpgrade") ||
                                        type.equals("remoteSprayWater") ||
                                        type.equals("cabBottomHope")
                        ) {

                            ifHttpOpenLongLinkLinstener.onHttpReturnDataResult(jsonObject.toString());

                        } else if (type.equals("ping")) {
                            mWebSocketClient.send("{\"type\":\"pong\"}");
//                            ifHttpOpenLongLinkLinstener.onHttpReturnErrorResult(1);
                            keepTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.I("longlink :    " + e.toString());
                    }
                }

                @Override
                public void onClose(int i, String s, boolean remote) {
                    //连接断开，remote判定是客户端断开还是服务端断开
                    LogUtil.I("longlink :    Connection closed by " + (remote ? "remote peer" : "us") + ", info=" + s);
                }

                @Override
                public void onError(Exception e) {
                    LogUtil.I("longlink :    onMessage" + "error:" + e);

                }
            };
        }
    }

    private void closeConnect() {
        try {
            if (mWebSocketClient == null) {
                return;
            } else {
                mWebSocketClient.closeConnection(0, "onDestory");
                mWebSocketClient.close();
                mWebSocketClient = null;
                LogUtil.I("longlink :    调用closeConnect() 重启长连接");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mWebSocketClient = null;
        }
    }

    public void onDestory() {

        try {
            if (mWebSocketClient == null) {
                return;
            } else {
                mWebSocketClient.closeConnection(0, "onDestory");
                mWebSocketClient.close();
                mWebSocketClient = null;

                longlink_save_code = 1;

                LogUtil.I("longlink :    调用closeConnect() 长连接停止");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mWebSocketClient = null;
        }

    }

    public boolean isKeepConnected() {

        return System.currentTimeMillis() < keepTime;
    }
}
