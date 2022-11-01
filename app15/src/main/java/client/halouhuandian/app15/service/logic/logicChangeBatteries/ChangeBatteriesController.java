package client.halouhuandian.app15.service.logic.logicChangeBatteries;


import android.content.Context;

import org.json.JSONObject;

import client.halouhuandian.app15.pub.BaseDataDistribution;

/**
 * 单例
 * 换电逻辑
 * 主要功能 初始化 按钮舱门打开逻辑 还有 舱门换电逻辑 以及 数据注册分发
 */
public class ChangeBatteriesController extends BaseDataDistribution {

    private static volatile ChangeBatteriesController logicChangeBatteries;
    private ChangeBatteriesController(){}
    public static ChangeBatteriesController getInstance(){
        if(logicChangeBatteries == null){
            synchronized (ChangeBatteriesController.class){
                if(logicChangeBatteries == null){
                    logicChangeBatteries = new ChangeBatteriesController();
                }
            }
        }
        return logicChangeBatteries;
    }

    private ChangeBatteriesOpenDoor changeBatteriesOpenDoor;
    private ChangeBatteriesProcess changeBatteriesProcess;

    //初始化
    public void init(Context context){
        if(changeBatteriesOpenDoor == null){
            changeBatteriesOpenDoor = new ChangeBatteriesOpenDoor(context, new ChangeBatteriesOpenDoor.ChangeBatteriesOpenDoorListener() {
                @Override
                public void showDialog(String info, int time, int type) {
                    sendData(new ChangeBatteriesReturnDataFormat(ChangeBatteriesReturnDataFormat.ChangeBatteriesReturnDataType.info , createDialogJson(info,time,type)));
                }
            });
        }
        if(changeBatteriesProcess == null){
            changeBatteriesProcess = new ChangeBatteriesProcess(context, new ChangeBatteriesProcess.ChangeBatteriesControllerListener() {
                @Override
                public void returnInchingTrigger(int door) {
                    sendData(new ChangeBatteriesReturnDataFormat(ChangeBatteriesReturnDataFormat.ChangeBatteriesReturnDataType.inchingTrigger , door));
                }

                @Override
                public void returnResult(ChangeBatteriesChangeDataFormat changeBatteriesDataFormat) {
                    sendData(new ChangeBatteriesReturnDataFormat(ChangeBatteriesReturnDataFormat.ChangeBatteriesReturnDataType.data , changeBatteriesDataFormat));
                }

                @Override
                public void showDialog(String msg, int time, int type) {
                    sendData(new ChangeBatteriesReturnDataFormat(ChangeBatteriesReturnDataFormat.ChangeBatteriesReturnDataType.info , createDialogJson(msg,time,type)));
                }
            });
        }
    }

    //开始触发 按钮打开舱门逻辑
    public void buttonTriggerOpenDoor(){
        if(changeBatteriesOpenDoor != null){
            changeBatteriesOpenDoor.onStart();
        }
    }
    //结束换电 - 主要就是结束掉按钮打开的计时 - 60秒内只开一个舱门 - 换电成功或者失败后这个功能就可以失效了
    public void exchangeFinish(){
        if(changeBatteriesOpenDoor != null){
            changeBatteriesOpenDoor.onFinish();
        }
    }
    //线程挂起 - 比如升级的时候不许打开舱门 等 ...
    //todo:: 漏洞 - 已经打开的舱门还是可以换电的（ 需要在挂起后延后 或者 升级时舱门全部关闭 ）
    public void hangUp(){
        if(changeBatteriesOpenDoor != null){
            changeBatteriesOpenDoor.hangUp();
        }
    }
    //取消挂起
    public void hangUpCancel(){
        if(changeBatteriesOpenDoor != null){
            changeBatteriesOpenDoor.hangUpCancel();
        }
    }

    public void onDestroy(){
        if(changeBatteriesOpenDoor != null){
            changeBatteriesOpenDoor.onDestroy();
        }
        if(changeBatteriesProcess != null){
            changeBatteriesProcess.onDestroy();
        }
    }

    //工具类
    private String createDialogJson(String msg, int time, int type){
        JSONObject dialogJson = new JSONObject();
        try {
            dialogJson.put("msg", msg);
            dialogJson.put("time", time);
            dialogJson.put("type", type);
        }catch (Exception e){
            System.out.println("ChangeBatteriesController - error - " + e.toString());
        }
        return dialogJson.toString();
    }
}
