package com.hellohuandian.longlink;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.URI;
import java.net.URISyntaxException;


/**
 * Created by apple on 2017/7/27.
 */

public class OpenLongLink {

    private WebSocketClient mWebSocketClient;

    private static String address = "http://long.halouhuandian.com:5858";
//    public static String address = "https://testlong.halouhuandian.com:5858";


    private String client_id;
    private Handler handler1, OrderHandler;

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
                        System.out.println("longlink :    调用connect()");
                    } catch (Exception e) {
                        System.out.println("longlink :    "+e.toString());
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


    public OpenLongLink(Handler handler1, Handler OrderHandler) {

        this.handler1 = handler1;
        this.OrderHandler = OrderHandler;

        if(!thread_longlink_save.isAlive()){
            thread_longlink_save.start();
        }

        try {
            closeConnect();
            initSocketClient();
            mWebSocketClient.connect();
            System.out.println("longlink :    调用connect()");
        } catch (Exception e) {
            System.out.println("longlink :    "+e.toString());
        }

    }


    private void initSocketClient() throws URISyntaxException {

        System.out.println("longlink :    长连接请求连接");

        if (mWebSocketClient == null) {

            mWebSocketClient = new WebSocketClient(new URI(address)) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    System.out.println("longlink :    onOpen first " + serverHandshake.toString());
                }

                @Override
                public void onMessage(final String s) {
                    //这个是收到服务器推送下来的消息
                    System.out.println("longlink :    " + s);
                    longlink_save_count = 20;
                    try {
                        JSONTokener jsonTokener = new JSONTokener(s);
                        JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                        String type = jsonObject.getString("type");

                        if (type.equals("init")) {

                            client_id = jsonObject.getString("client_id");
                            Message msg = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("longLinkId", client_id);
                            msg.setData(bundle);
                            handler1.sendMessage(msg);

                        } else if (type.equals("openCabBackDoor") ||
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
                                type.equals("updatePdu")||
                                type.equals("writeBtyUid")||
                                type.equals("activateBattery")||
                                type.equals("upgradeDcdc") ||
                                type.equals("upgradeAcdc") ||
                                type.equals("upgradeDcdcAll") ||
                                type.equals("upgradeAcdcAll") ||
                                type.equals("remoteCloseDoor") ||
                                type.equals("cabBottomHope")
                        ) {

                            Message message = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("msg", jsonObject.toString());
                            message.setData(bundle);
                            OrderHandler.sendMessage(message);

                        } else if (type.equals("ping")) {
                            mWebSocketClient.send("{\"type\":\"pong\"}");
                            System.out.println("longlink :    " + "{\"type\":\"pong\"}");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onClose(int i, String s, boolean remote) {
                    //连接断开，remote判定是客户端断开还是服务端断开
                    System.out.println("longlink :    Connection closed by " + (remote ? "remote peer" : "us") + ", info=" + s);
                }
                @Override
                public void onError(Exception e) {
                    System.out.println("longlink :    onMessage" + "error:" + e);

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
                System.out.println("longlink :    调用closeConnect() 重启长连接");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mWebSocketClient = null;
        }
    }

    public void onDestory(){

        try {
            if (mWebSocketClient == null) {
                return;
            } else {
                mWebSocketClient.closeConnection(0, "onDestory");
                mWebSocketClient.close();
                mWebSocketClient = null;

                longlink_save_code = 1;

                System.out.println("longlink :    调用closeConnect() 长连接停止");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mWebSocketClient = null;
        }

    }


}
