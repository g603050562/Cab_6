package client.halouhuandian.app15.hardWareConncetion.environmentPlate;

import client.halouhuandian.app15.hardWareConncetion.androidHard.SerialAndCanPortUtilsGeRui;

/**
 * 环境板生命线程
 */
public class EnvironmentLifeThread {

    private Thread lifeThread;
    //线程状态
    private int lifeState = 0;
    //发送自增的生命帧
    private int lifeCount = 0;

    public EnvironmentLifeThread(){
        lifeState = 0;
    }

    public void onStart(){
        if(lifeThread == null){
            lifeThread = new Thread(){
                @Override
                public void run() {
                    super.run();

                    while (lifeState == 0){
                        //每秒钟下发生命帧
                        byte alive[] = new byte[]{(byte) lifeCount};
                        String alive_b = "9807ff65";
                        SerialAndCanPortUtilsGeRui.getInstance().canSendOrder(alive_b + "", alive);
                        lifeCount = lifeCount > 254 ? 0 : lifeCount + 1;
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            };
            lifeThread.start();
        }
    }

    public void onDestroy(){
        lifeState = 1;
    }

}
