package client.halouhuandian.app15.hardWareConncetion.daa;

import java.util.ArrayList;

import client.halouhuandian.app15.hardWareConncetion.daa.mode.DaaDataFormat;
import client.halouhuandian.app15.model.dao.fileSave.LocalLog;
import client.halouhuandian.app15.service.logic.logicHttpConnection.webSocket.WebSocketController;

/**
 *（daa - dcdc_and_acdc 缩写）
 * dcdc和acdc控制器
 */
public class DaaController {

    public interface DaaControllerListener{
        void returnData(DaaDataFormat daaDataFormat , DaaIntegration.ReturnDataType returnDataType , int index); // type - 因为什么类型返回的信息    index - 下标
    }

    //下发类集合
    private DaaSend daaSend;
    //ac和dc解析
    private DaaIntegration daaIntegration = null;
    //数据分发队列
    private ArrayList<DaaControllerListener> daaControllerListeners = new ArrayList<>();
    //数据缓存
    private DaaDataFormat daaDataFormat;

    //单例
    private volatile static DaaController daaController;
    private DaaController(){
        daaSend = new DaaSend();
    }
    public static DaaController getInstance(){
        if(daaController == null){
            synchronized (DaaController.class){
                if(daaController == null){
                    daaController = new DaaController();
                }
            }
        }
        return daaController;
    }

    //初始化
    public void init(){
        LocalLog.getInstance().writeLog("DCDC和ACDC解析模块儿初始化", WebSocketController.class);
        if(daaIntegration == null){
            daaIntegration = new DaaIntegration(new DaaIntegration.DaaIntegrationListener() {
                @Override
                public void returnData(DaaDataFormat mDaaDataFormat, DaaIntegration.ReturnDataType returnDataType, int index) {
                    daaDataFormat = mDaaDataFormat;
                    for(int i = 0 ; i < daaControllerListeners.size() ; i ++){
                        daaControllerListeners.get(i).returnData(mDaaDataFormat,returnDataType, index);
                    }
                }
            });
        }
    }

    //仅获得当前数据 不做数据监听
    public DaaDataFormat getDaaDataFormat() {
        return daaDataFormat;
    }

    //数据分发注册
    public void addListener(DaaControllerListener daaControllerListener){
        try {
            if(daaControllerListeners.size() > 0){
                if(!daaControllerListeners.contains(daaControllerListener)){
                    daaControllerListeners.add(daaControllerListener);
                }
            }else{
                daaControllerListeners.add(daaControllerListener);
            }
        } catch (Exception e){
            System.out.println(e.toString());
        }
    }

    //数据分发注销
    public void deleteListener(DaaControllerListener daaControllerListener){
        try {
            if(daaControllerListeners.size() > 0){
                if(daaControllerListeners.contains(daaControllerListener)){
                    daaControllerListeners.remove(daaControllerListener);
                }
            }
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }

}
