package client.halouhuandian.app15.hardWareConncetion.environmentPlate.fireControl;

/**
 * 消防控制类
 */

public class FireControlController{
    //单例
    private volatile static FireControlController fireControlController = null;
    private FireControlController(){}
    public static FireControlController getInstance(){
        if(fireControlController == null){
            synchronized (FireControlController.class){
                if(fireControlController == null){
                    fireControlController = new FireControlController();
                }
            }
        }
        return fireControlController;
    }

    private FireControlSwitcher fireControlSwitcher;
    private FireControlAuto fireControlAuto;

    //初始化
    public void init(){
        if(fireControlAuto == null){
            fireControlAuto = new FireControlAuto();
        }
    }

    //打开目标消防
    public void openFireControl(int door){
        if(fireControlSwitcher == null){
            fireControlSwitcher = new FireControlSwitcher();
        }
        fireControlSwitcher.sendOpenCmd(door);
    }

    //关闭所有消防
    public void closeAllFireControl(){
        if(fireControlSwitcher == null){
            fireControlSwitcher = new FireControlSwitcher();
        }
        fireControlSwitcher.sendCloseCmd();
    }

    public void onDestroy(){
        closeAllFireControl();
        if(fireControlAuto == null){
            fireControlAuto.onDestroy();
        }
    }
}
