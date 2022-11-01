package client.halouhuandian.app15.test.testBattery;

import android.app.Activity;
import android.content.Intent;

import client.halouhuandian.app15.pub.util.UtilFilesDirectory;
import client.halouhuandian.app15.view.activity.update.A_UpdateBattery;

public class TestBattery {

    public void DengBoBatteryTest(Activity mActivity){
        final Activity activity = mActivity;
        new Thread(){
            @Override
            public void run() {
                super.run();

                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(activity, A_UpdateBattery.class);
                        intent.putExtra("door", 5);
                        intent.putExtra("path", UtilFilesDirectory.SD_CARD + "main_2022-0624.hex");
                        intent.putExtra("type", "DengBo");
                        activity.startActivity(intent);
                    }
                });

            }
        }.start();
    }

}
