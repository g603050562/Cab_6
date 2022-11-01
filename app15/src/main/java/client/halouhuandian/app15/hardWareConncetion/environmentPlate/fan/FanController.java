package client.halouhuandian.app15.hardWareConncetion.environmentPlate.fan;

import android.content.Context;

/**
 * 风扇控制器
 */


public class FanController {

    //单例
    private static volatile FanController fanController;
    private FanController(){}
    public static FanController getInstance(){
        if(fanController == null){
            synchronized (FanController.class){
                if(fanController == null) {
                    fanController = new FanController();
                }
            }
        }
        return fanController;
    }

    private Context context;
    private FanSwitcher fanSwitcher;
    private FanAuto fanAuto;

    public void init(Context context){
        this.context = context;
        if(fanSwitcher == null){
            fanSwitcher = new FanSwitcher();
        }
        if(fanAuto == null){
            fanAuto = new FanAuto();
            fanAuto.init(context);
        }
    }

    public void openFan_1(){
        if(fanSwitcher!=null){
            fanSwitcher.openFan_1();
        }
    }
    public void openFan_2(){
        if(fanSwitcher!=null){
            fanSwitcher.openFan_2();
        }
    }
    public void closeFan_1(){
        if(fanSwitcher!=null){
            fanSwitcher.closeFan_1();
        }
    }
    public void closeFan_2(){
        if(fanSwitcher!=null){
            fanSwitcher.closeFan_2();
        }
    }

    public int getActivityFanCount(){
        if(fanAuto != null){
            return fanAuto.getActivityFanCount();
        }else{
            return 0;
        }
    }


    public void onDestroy(){
        if(fanAuto != null){
            fanAuto.onDestroy();
        }
    }
}
