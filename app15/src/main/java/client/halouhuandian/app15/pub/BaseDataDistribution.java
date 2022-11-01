package client.halouhuandian.app15.pub;

import java.util.ArrayList;

/**
 * 自定义 数据分发 基础类
 * 功能主要就是 注册 分发 和注销
 */
public class BaseDataDistribution {

    public interface LogicListener{
        void returnData(Object object);
    }

    private ArrayList<LogicListener> logicListeners = new ArrayList<>();

    public void addListener(LogicListener listener){
        if(logicListeners.size() > 0){
            if(!logicListeners.contains(listener)){
                logicListeners.add(listener);
            }
        }else{
            logicListeners.add(listener);
        }
    }

    public void deleteListener(LogicListener listener){
        if(logicListeners.size() > 0){
            if(logicListeners.contains(listener)){
                logicListeners.remove(listener);
            }
        }
    }

    protected void sendData(Object object){
        for(int i = 0 ; i < logicListeners.size() ; i++){
            logicListeners.get(i).returnData(object);
        }
    }
}
