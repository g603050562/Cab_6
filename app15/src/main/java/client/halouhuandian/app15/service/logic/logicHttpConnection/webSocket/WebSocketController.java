package client.halouhuandian.app15.service.logic.logicHttpConnection.webSocket;

import android.content.Context;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

import client.halouhuandian.app15.model.dao.fileSave.LocalLog;
import client.halouhuandian.app15.model.dao.sharedPreferences.CabInfoSp;
import client.halouhuandian.app15.pub.BaseDataDistribution;
import client.halouhuandian.app15.service.logic.logicHttpConnection.http.BaseHttp;
import client.halouhuandian.app15.service.logic.logicHttpConnection.http.BaseHttpParameterFormat;
import client.halouhuandian.app15.service.logic.logicHttpConnection.http.HttpUrlMap;
import client.halouhuandian.app15.service.logic.logicHttpConnection.webSocket.mode.WebSocketReturnDataFormat;
import client.halouhuandian.app15.service.logic.logicHttpConnection.webSocket.mode.WebSocketReturnType;

/**
 * 单例
 * 长链接 控制类
 * 主要做注册和分发 和 简单控制
 */
public class WebSocketController extends BaseDataDistribution {

    private static volatile WebSocketController webSocketController = null;
    private WebSocketController(){};
    public static WebSocketController getInstance(){
        if(webSocketController == null){
            synchronized (WebSocketController.class){
                if(webSocketController == null){
                    webSocketController = new WebSocketController();
                }
            }
        }
        return webSocketController;
    }

    private Context context;
    private WebSocketLongLink webSocketLongLink;
    private WebSocketReturnDataAnalysis webSocketReturnDataAnalysis;

    private String hasReceiverCabId = null;

    public void init(Context context){
        this.context = context;
        onStart();
        LocalLog.getInstance().writeLog("长链接模块儿初始化", WebSocketController.class);
    }

    public void onStart(){
        if(webSocketLongLink == null){
            webSocketLongLink = new WebSocketLongLink();
            webSocketLongLink.init(context, new WebSocketLongLink.IFHttpOpenLongLinkLinstener() {
                @Override
                public void onHttpReTurnIDResult(String code) {
                    //绑定长链接
                    final String fCode = code;
                    List<BaseHttpParameterFormat> baseHttpParameterFormats =  new ArrayList<>();
                    CabInfoSp.getInstance().setCabinetClientId(fCode);
                    baseHttpParameterFormats.add(new BaseHttpParameterFormat("number",CabInfoSp.getInstance().getCabinetNumber_4600XXXX()));
                    baseHttpParameterFormats.add(new BaseHttpParameterFormat("client_id",code));
                    BaseHttp baseHttp = new BaseHttp(HttpUrlMap.BindLongLink, baseHttpParameterFormats, 15, new BaseHttp.BaseHttpListener() {
                        @Override
                        public void dataReturn(int code, String message, String data) {
                            System.out.println("longLink - code - " + code + " - message - " + message + " - data - " + data);
                            if(code != 1){
                                System.out.println("longLink - 重新绑定");
                                new Thread(){
                                    @Override
                                    public void run() {
                                        super.run();
                                        try {
                                            sleep(30 * 1000);
                                            onHttpReTurnIDResult(fCode);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();
                            }
                        }
                    });
                    baseHttp.onStart();
                }

                @Override
                public void onHttpReturnDataResult(String data) {
                    //命令返回
                    if(webSocketReturnDataAnalysis == null){
                        webSocketReturnDataAnalysis = new WebSocketReturnDataAnalysis(context, new WebSocketReturnDataAnalysis.WebSocketReturnDataAnalysisListener() {
                            @Override
                            public void dataReturn(WebSocketReturnDataFormat webSocketReturnDataFormat) {
                                if(webSocketReturnDataFormat.getWebSocketReturnType() == WebSocketReturnType.bindSuccess){
                                    hasReceiverCabId = webSocketReturnDataFormat.getObject().toString();
                                }
                                sendData(webSocketReturnDataFormat);
                            }
                        });
                    }
                    webSocketReturnDataAnalysis.dataAnalysis(data);
                }

                @Override
                public void onHttpReturnErrorResult(int data) {
                    //长连接状态返回
                    sendData(new WebSocketReturnDataFormat(WebSocketReturnType.onlineType , data));
                }
            });
        }
    }

    public String getHasReceiverCabId() {
        return hasReceiverCabId;
    }

    public void hangUp(){
        if(webSocketLongLink !=null){
            webSocketLongLink.hangUp();
        }
    }

    public void hangUpCancel(){
        webSocketLongLink.hangUpCancel();
    }

    public void onDestroy(){
        webSocketLongLink.onDestroy();
        webSocketLongLink = null;
        webSocketReturnDataAnalysis.onDestroy();
        webSocketReturnDataAnalysis = null;
    }

}
